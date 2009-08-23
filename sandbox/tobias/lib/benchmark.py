#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""Python benchmarking module

Command line usage:
    %(prog)s <module> [<module> ...]
"""

from __future__ import with_statement

import sys, traceback

if __name__ == '__main__':
    from benchmark import __main__
    try:
        __main__(*sys.argv)
    except:
        traceback.print_exc()
        sys.exit(1)
    else:
        sys.exit(0)

__all__ = ('benchmark', 'run')

def benchmark(*args, **params):
    if len(args) > 1:
        raise ValueError("Illegal use of benchmark decorator.")
    def decorator(func):
        func.__benchmark__ = params
        return func
    if args:
        return decorator(*args)
    else:
        return decorator

def run(*cases, **params):
    if not cases:
        if hasattr(sys, '_getframe'):
            frame = sys._getframe(1)
        while frame and frame.f_globals['__name__'] == __name__:
            frame = frame.f_back
        if frame:
            cases = (frame.f_globals['__name__'],)
    benchmarks = []
    for case in cases:
        if isstring(case):
            if case.endswith('.py'):
                env = {'__name__':'__benchmark__', '__file__':case}
                try:
                    with open(case) as file:
                        exec(file.read(), env)
                except:
                    print("Error in benchmark file '%s'." % (case,))
                    traceback.print_exc()
                    continue
                else:
                    module = _Dummy()
                    module.__benchmarks__ = env.get('__benchmarks__', ())
            elif case in sys.modules:
                module = sys.modules[case]
            else:
                env = {}
                try:
                    exec("import %s as module" % (case,), env)
                except:
                    print("No such benchmark module '%s'." % (case,))
                    continue
                else:
                    module = env['module']
            name = case
            for case in getattr(module, '__benchmarks__', ()):
                if isstring(case):
                    try:
                        benchmarks.append(getattr(module, case))
                    except:
                        print("No such benchmark case '%s' in module '%s'." %
                              (case, name))
                else:
                    benchmarks.append((name, case))
        else:
            benchmarks.append((None, case))
    _run(benchmarks, params)

benchmark.run = run

try:
    basestring
except:
    def isstring(string):
        return isinstance(string, str)
else:
    def isstring(string):
        return isinstance(string, basestring)

__DEFAULT__ = object()
class _Dummy(object): pass

def _run(cases, params):
    warmup = params.get('warmup', __DEFAULT__)
    if warmup is __DEFAULT__:
        pass
    elif warmup is None:
        pass
    else:
        pass
    for module, case in cases:
        name = ('%s.%s' % (module, case.__name__)) if module else case.__name__
        print name

if sys.platform.lower().startswith('java'):
    def _warmup(params):
        warmup = params.pop('warmup', None)
        if warmup is INTERACTIVE_WARMUP:
            pass
        elif warmup is not None:
            return dict(
                iterations=int(warmup),
                )
else:
    def _warmup(params):
        params.pop('warmup',None)


def __main__(script, *args):
    from optparse import OptionParser
    parser = OptionParser()
    parser.add_option('--verbose', action='store_true', default=False)
    parser.add_option('--scripted', action='store_true', default=False)
    parser.add_option('--runs', type='int', default=1)
    parser.add_option('--loops', type='int', default=10)
    parser.add_option('--warmup-runs', type='int', default=1)
    parser.add_option('--warmup-loops', type='int', default=1)
    parser.add_option('--repetitions', '--reps', type='int', default=None)
    options, args = parser.parse_args(list(args))
                      
    params = {'verbose': options.verbose
              }
    if options.repetitions is not None:
        params['repetitions'] = options.repetitions
    if not args:
        print(__doc__ % {'prog': script})
    else:
        run(*args,**params)
