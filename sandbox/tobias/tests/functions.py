def func(x):
    z = x

def bench(times):
    for x in xrange(times):
        func(x)

def main(script, times='10000', warmups='20000', reps='3'):
    from time import time
    warmups = int(warmups); reps = int(reps); times = int(times)
    for i in xrange(warmups):
        bench(times)
    res = []
    for i in xrange(reps):
        t = time()
        bench(times)
        t = time() - t
        res.append(t)
    print min(res), max(res)

if __name__ == '__main__':
    import sys
    main(*sys.argv)
