def f():
    for e in __name__:
        try:
            print e
            return
        except:
            pass

if __name__ == '__main__':
    f()
