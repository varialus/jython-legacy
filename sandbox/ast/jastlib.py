"""lispify_ast - returns a tuple representation of the AST

Uses 2.5's _ast, not other AST implementations in CPython, since these
are not used by the compilation phase. And that's what we're
interested in.

Since this is a tuple, we can directly compare, and this is going to
be handy when comparing Jython's implementation vs CPython.

"""
import org.python.antlr.ast as _ast
import org.python.antlr.Main as parser

from types import ArrayType

def lispify_ast(node):
    return tuple(lispify_ast2(node))

def lispify_ast2(node):
    yield str(node)
    try:
        for field in node._fields:
            yield tuple(lispify_field(field, getattr(node, field)))
    except:
        pass

def lispify_field(field, child):
    yield field
    if not isinstance(child, ArrayType):
        children = [child]
    else:
        children = child

    for node in children:
        if isinstance(node, (str, int)):
            yield node
        else:
            yield lispify_ast(node)

if __name__ == '__main__':
    import sys
    from pprint import pprint

    code_path = sys.argv[1]
    ast = parser().parse([code_path])
    lispified = lispify_ast(ast)
    pprint(lispified)
