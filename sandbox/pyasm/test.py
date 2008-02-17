import sys, os, marshal

if os.name == 'java': # Jython side
    def cpythonCompile(string, filename='<stdin>'):
        # invoke script with CPython to compile and return marshalled code
        source, drain, error = os.popen3('python2.5 "%s" "%s"' %
                                         (__file__, filename))
        source.write(string) # send source
        source.close() # mark end of source (eof)
        binary = drain.read()
        if len(binary) == 0:
            print >> sys.stderr, error.read()
            sys.exit(1)
        try:
            data = "".join([chr(int(x)) for x in binary.split(',')]) #decode
        except:
            print >> sys.stderr, "Read from CPython:"
            print >> sys.stderr, data
            raise
        code = marshal.loads(data) # read back marshalled code and unmarshal
        return code # return unmarshalled, compiled code

    if __name__ == '__main__': # invoked as a script
        import sys
        action = 'print'
        if len(sys.argv) == 1:# If test.py is run with no arguments, check all the files
            action = 'check'
            files = ['tests/%s.py' % f for f in os.listdir('expected_output') if not f == '.svn']
        elif sys.argv[1] == '--store':
            action = 'store'
            del sys.argv[1]
        elif sys.argv[1] == '--check':
            action = 'check'
            del sys.argv[1]
        if not files:
            files = sys.argv[1:]
        for filename in files:
            file = open(filename) # open file
            code = cpythonCompile(file.read(), filename) # compile file
            vars = dict(__name__='__main__', __file__=filename, __doc__=None)
            import StringIO
            out = StringIO.StringIO()
            regularout = sys.stdout
            regularerr = sys.stderr
            sys.stdout = out
            sys.stderr = out
            try:
                exec code in vars # execute compiled file
            except Exception, e:
                print e.__class__
                print e
            sys.stdout = regularout
            sys.stderr = regularerr
            outfile = 'expected_output/%s' % filename.replace('.py', '').replace('tests/', '')
            cur = out.getvalue()
            
            if action == 'print':
                print cur
            elif action == 'store':
                store = open(outfile, 'w')
                store.write(cur)
                store.close()
            else:
                prior = open(outfile).read()
                print 'Checking', filename
                if not prior == cur:
                    print 'Expected'
                    print '=========='
                    print prior
                    print 'Current'
                    print '=========='
                    print cur

elif __name__ == '__main__': # CPython side
    filename = sys.argv[1]
    source = sys.stdin.read() # read source
    code = compile(source, filename, 'exec') # compile code
    data = marshal.dumps(code) # marshal code
    binary = ','.join(["%d"%ord(x) for x in data]) # avoid encoding errors
    sys.stdout.write(binary) # "return" marshalled code

else:
    raise EnvironmentError("Unknown runtime environment")
