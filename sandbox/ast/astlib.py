"""lispify_ast - returns a tuple representation of the AST

Uses 2.5's _ast, not other AST implementations in CPython, since these
are not used by the compilation phase. And that's what we're
interested in.

Since this is a tuple, we can directly compare, and this is going to
be handy when comparing Jython's implementation vs CPython.

"""

import _ast

def lispify_ast(node):
    return tuple(lispify_ast2(node))

def lispify_ast2(node):
    yield node.__class__.__name__
    try:
        for field in node._fields:
            yield tuple(lispify_field(field, getattr(node, field)))
    except:
        pass

def lispify_field(field, child):
    yield field
    if not isinstance(child, list):
        children = [child]
    else:
        children = child

    for node in children:
        if isinstance(node, _ast.AST):
            yield lispify_ast(node)
        else:
            yield node


if __name__ == '__main__':
    import sys
    from pprint import pprint
    from popen2 import popen2
    from StringIO import StringIO
    from difflib import Differ

    code_path = sys.argv[1]
    jy_exe = "jython"
    if len(sys.argv) > 2:
        jy_exe = sys.argv[2]
    ast = compile(open(code_path).read(), code_path, "exec", _ast.PyCF_ONLY_AST)
    lispified = lispify_ast(ast)
    sio = StringIO()
    pprint(lispified, stream=sio)

    fin, fout = popen2("%s jastlib.py %s" % (jy_exe, code_path))

    sio.seek(0)
    pstr = sio.readlines()
    jstr = fin.readlines()

    differs = False
    diffstr = []
    diff = Differ()
    results = diff.compare(pstr, jstr)
    for d in results:
        diffstr.append(d)
        if d[0] in ['+', '-']:
            differs = True

    if differs:
        print "---------- ouput -------------"
        print "py: %s" % sio.getvalue()
        print "jy: %s" % "".join(jstr)
        print "---------- DIFF -------------"
        print "".join(diffstr)

    #ast2 = compile(open(code_path).read(), code_path, "exec", _ast.PyCF_ONLY_AST)
    #assert(lispified == lispify_ast(ast2))
