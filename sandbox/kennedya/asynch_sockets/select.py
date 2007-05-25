"""
AMAK: 20070515: New select implementation that uses java.nio
"""

import java.nio.channels.SelectableChannel
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
from java.nio.channels.SelectionKey import OP_ACCEPT, OP_CONNECT, OP_WRITE, OP_READ

class error(Exception): pass

POLLIN   = 1
POLLOUT  = 2
POLLPRI  = 4

class poll:

    def __init__(self):
        self.selector = java.nio.channels.Selector.open()
        self.chanmap = {}

    def _getselectable(self, userobject):
        if isinstance(userobject, java.nio.channels.SelectableChannel):
            return userobject
        else:
            if hasattr(userobject, 'fileno') and callable(getattr(userobject, 'fileno')):
                result = getattr(userobject, 'fileno')()
                if isinstance(result, java.nio.channels.SelectableChannel):
                    return result
            raise error("Object '%s' is not a watchable channel" % userobject, 10038)

    def register(self, userobject, mask):
        channel = self._getselectable(userobject)
        jmask = 0
        if mask & POLLIN:
            # Note that OP_READ is NOT a valid event on server socket channels.
            if channel.validOps() & OP_ACCEPT:
                jmask = OP_ACCEPT
            else:
                jmask = OP_READ
        if mask & POLLOUT:
            jmask |= OP_WRITE
            if channel.validOps() & OP_CONNECT:
                jmask |= OP_CONNECT
        selectionkey = channel.register(self.selector, jmask)
        self.chanmap[channel] = (userobject, selectionkey)

    def unregister(self, userobject):
        channel = self._getselectable(userobject)
        self.chanmap[channel][1].cancel()
        del self.chanmap[channel]

    def _dopoll(self, timeout=None):
        if timeout is None or timeout < 0:
            self.selector.select()
        elif timeout == 0:
            self.selector.selectNow()
        else:
            # No multiplication required: both cpython and java use millisecond timeouts
            self.selector.select(timeout)
        # The returned selectedKeys cannot be used from multiple threads!
        return self.selector.selectedKeys()

    def poll(self, timeout=None):
        selectedkeys = self._dopoll(timeout)
        results = []
        for k in selectedkeys.iterator():
            jmask = k.readyOps()
            pymask = 0
            if jmask & OP_READ: pymask |= POLLIN
            if jmask & OP_WRITE: pymask |= POLLOUT
            if jmask & OP_ACCEPT: pymask |= POLLIN
            if jmask & OP_CONNECT: pymask |= POLLOUT
            # Now return the original userobject, and the return event mask
            # A python 2.2 generator would be sweet here
            results.append( (self.chanmap[k.channel()][0], pymask) )
        return results

    def close(self):
        for k in self.selector.keys():
            k.cancel()
        self.selector.close()

def _calcselecttimeoutvalue(value):
    if value is None:
        return None
    try:
        floatvalue = float(value)
    except Exception, x:
        raise TypeError("Select timeout value must be a number or None")
    if value < 0:
        raise error("Select timeout value cannot be negative", 10022)
    if floatvalue < 0.000001:
        return 0
    return int(floatvalue * 1000) # Convert to milliseconds

def select ( read_fd_list, write_fd_list, outofband_fd_list, timeout=None):
    timeout = _calcselecttimeoutvalue(timeout)
    # First create a poll object to do the actual watching.
    pobj = poll()
    # Check the read list
    try:
        # AMAK: Need to remove all this list searching, change to a dictionary?
        for fd in read_fd_list:
            mask = POLLIN
            if fd in write_fd_list:
                mask |= POLLOUT
            pobj.register(fd, mask)
        # And now the write list
        for fd in write_fd_list:
            if not fd in read_fd_list: # fds in both have already been registered.
                pobj.register(fd, POLLOUT)
        results = pobj.poll(timeout)
    except AttributeError, ax:
        if str(ax) == "__getitem__":
            raise TypeError(ax)
        raise ax
    # Now start preparing the results
    read_ready_list, write_ready_list, oob_ready_list = [], [], []
    for fd, mask in results:
        if mask & POLLIN:
            read_ready_list.append(fd)
        if mask & POLLOUT:
            write_ready_list.append(fd)
    pobj.close()
    return read_ready_list, write_ready_list, oob_ready_list

