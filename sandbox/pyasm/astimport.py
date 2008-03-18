import sys
import os

__debugging__ = False

import org.python.antlr.Main as Parser
from org.python.newcompiler.ast import AstToBytecode
from newcompiler import Bundle
def _makeModule(name, code, path):
    module = _imp.addModule(name)
    builtins = _Py.getSystemState().builtins
    frame = _Frame(code, module.__dict__, module.__dict__, builtins)
    module.__file__ = path
    code.call(frame) # execute module code
    return module
def _parse(*files):
    return Parser().parse(files)
def _pbc(ast, bundle, name):
    return ast.accept(AstToBytecode(bundle, name))
def _compile(filepath, name, *args,**kwargs):
    return _pbc(_parse(filepath), Bundle(*args,**kwargs), name)

class _Importer(object):
    def __init__(self, path):
        if __debugging__: print "Importer invoked"
        self.__path = path
    def find_module(self, fullname, path=None):
        if __debugging__:
            print "Importer.find_module(fullname=%s, path=%s)" % (
                repr(fullname), repr(path))
        path = fullname.split('.')
        filename = path[-1]
        path = path[:-1]
        pyfile = os.path.join(self.__path, *(path + [filename + '.py']))
        if os.path.exists(pyfile):
            return self
        else:
            return None
    def load_module(self, fullname):
        path = fullname.split('.')
        path[-1] += '.py'
        filename = os.path.join(self.__path, *path)
        code = _compile(filename, fullname)
        return __makeModule(fullname, code, filename)

class _MetaImporter(object):
    def __init__(self):
        self.__importers = {}
    def find_module(self, fullname, path):
        if __debugging__: print "MetaImporter.find_module(%s, %s)" % (
            repr(fullname), repr(path))
        for _path in sys.path:
            if _path not in self.__importers:
                try:
                    self.__importers[_path] = _Importer(_path)
                except:
                    self.__importers[_path] = None
            importer = self.__importers[_path]
            if importer is not None:
                loader = importer.find_module(fullname, path)
                if loader is not None:
                    return loader
        else:
            return None

sys.meta_path.insert(0, _MetaImporter())
