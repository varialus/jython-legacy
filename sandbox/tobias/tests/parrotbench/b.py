import b0
import b1
import b2
import b3
import b4
import b5
import b6

import sys
from time import time

WARMUP = 250
#WARMUP = 0
TIMES = 3
NUMBER = 1
ITERATIONS = 1

def run(module, iteration):
    print >>sys.stderr, "-->", module.__name__
    res = []
    for i in range(TIMES):
        t = time()
        for i in range(NUMBER):
            module.main()
        t = time() - t
        res.append(t/NUMBER)
    return res

if 'java' in sys.platform:
    def run(module, iteration, run=run):
        if not iteration:
            for i in range(WARMUP):
                module.main()
        return run(module, iteration)

def main():
    t = time()
    for i in range(ITERATIONS):
        print >>sys.stderr, "--> iteration", i
        for module in (b0, b2, b3, b4, b5, b6): # b1 ommitted
            mt = min(run(module, i))
            print >>sys.stderr, "best of %s:" % TIMES, mt
    t = time() - t
    print >>sys.stderr, "--> All done.", t

if __name__ == '__main__':
    main()
