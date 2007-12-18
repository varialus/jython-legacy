"""lispify_ast - returns a tuple representation of the AST

Uses 2.5's _ast, not other AST implementations in CPython, since these
are not used by the compilation phase. And that's what we're
interested in.

Since this is a tuple, we can directly compare, and this is going to
be handy when comparing Jython's implementation vs CPython.

"""
import org.python.antlr.PythonTree as AST
import org.python.antlr.Main as parser

from types import ArrayType

contexts = {0:("0Store"),1:tuple(["Load"]),2:tuple(["Store"]),3:["3Store"],4:["4Store"],5:["5Store"],6:tuple(["Param"])}
#ops = {0:("Store"),1:tuple(["Eq"]),2:tuple(["Sub"]),3:["3Store"],4:["4Store"],5:["5Store"],6:tuple(["Param"])}

def lispify_ast(node):
    return tuple(lispify_ast2(node))

def lispify_ast2(node):
    s = node.__class__.__name__
    name = s.split(".")[-1]
    if name.endswith("Type"):
        name = name[:-4]
    yield name
    try:
        for field in node._fields:
            yield tuple(lispify_field(field, getattr(node, field)))
    except:
        pass

def lispify_field(field, child):
    fname = field
    yield field
    if not isinstance(child, ArrayType):
        children = [child]
    else:
        children = child

    for node in children:
        if isinstance(node, AST):
            yield lispify_ast(node)
        else:
            if fname == "ctx":
                yield contexts[node]
            #if fname == "ops":
            #    yield ops[node]
            elif fname == "n":
                yield int(node)
            else:
                yield node

if __name__ == '__main__':
    import sys
    from pprint import pprint

    code_path = sys.argv[1]
    ast = parser().parse([code_path])

    lispified = lispify_ast(ast)
    pprint(lispified)
    #assert(lispified == lispify_ast(ast2))
