import sys
sys.setrecursionlimit(1001)
import time
t0 = time.clock()
import b; b.main()
t1 = time.clock()
print >>sys.stderr, "%.3f seconds" % (t1-t0)
