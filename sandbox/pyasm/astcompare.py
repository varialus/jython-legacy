import os, re, sys

from org.python.newcompiler.bytecode import PythonBytecodeCompilingBundle
import org.python.antlr.Main as Parser
from org.python.newcompiler.ast import AstToBytecode
from newcompiler import BytecodeCompiler, CodeReference

py_line_no = 0
py_line_no_printed = False
cpy_dis = []
cpy_dis_line_no = 0
dis_offset = 0

class Disassembler(BytecodeCompiler):
    def __init__(self, codeinfo, level=0, post=None):
        self.__codeinfo = codeinfo
        self.__pre = "pyasm."
        self.__level = level
        self.__ind = ">"*(4*level)
        self.post = post
        self.reference = CodeReference(codeinfo)
    def __join(self, it, sep=','):
        if it is not None:
            return sep.join(it)
        else:
            return ""
    def __str__(self):
        return self.__repr__()
    def __repr__(self):
        return "Disassembler()"
    def constructClass(self, name, closureNames, flags):
        print "%s%sconstructClass(%s, [%s], [%s]):" % (
            self.__ind, self.__pre, name,
            self.__join(closureNames), self.__join(flags))
        return Disassembler(self.__codeinfo, self.__level+1)
    def constructFunction(self, closureNames, numDefault, flags, name=None):
        # print "%s%sconstructFunction([%s], %s, [%s], %s):" % (
        #     self.__ind, self.__pre, self.__join(closureNames),
        #     numDefault, self.__join(flags), name)
        def post(dis_offset=dis_offset):
            self.dump(None, dis_offset, 'LOAD_CONST', '<code object>')
            self.dump(None, None, 'MAKE_FUNCTION')
            self.dump(None, None, 'STORE_NAME', name)
        return Disassembler(self.__codeinfo, self.__level+1, post)
    def dump(self, line_no, offset, opname, *args):
        global py_line_no, py_line_no_printed, cpy_dis_line_no, dis_offset
        print self.__ind,
        if self.__level == 0:
            print '%3d:' % cpy_dis_line_no,
        else:
            print '    ',
        if py_line_no_printed or self.__level > 0:
            print '   ',
        else:
            if line_no is None: line_no = py_line_no
            print '%3d' % (line_no + 1),
            py_line_no_printed = True
        if self.__level == 0:
            print '|     ',
        else:
            print '      ',
        if offset is None: offset = dis_offset
        print '%4d' % offset,
        print OPNAMES.get(opname, opname).ljust(20),
        if args:
            print '(%s)' % ','.join(args)
            if self.__level == 0:
                dis_offset += 2 + len(args)
        else:
            print
            if self.__level == 0:
                dis_offset += 1
        if self.__level == 0:
            if cpy_dis_line_no < len(cpy_dis):
                print '%4d:' % cpy_dis_line_no, cpy_dis[cpy_dis_line_no]
            cpy_dis_line_no += 1

    def __getattr__(self,attr):
        def visit(*args):
            opname = '_'.join([c.upper()
                               for c in re.findall(r'[A-Z](?:[^A-Z]*)',
                                                   attr.replace('visit', ''))])
            self.dump(None, None, opname, *[repr(a) for a in args])
        return visit
    def visitLineNumber(self, lineNumber):
        global py_line_no, py_line_no_printed
        py_line_no = lineNumber
        py_line_no_printed = False
    def visitStop(self):
        if self.post is None:
            print '-- stop --'
        else:
            self.post()
    def visitResumeTable(self, start, entries):
        print "%s%svisitResumeTable(" % (self.__ind, self.__pre)
        print "%s    start: %s," % (self.__ind, start),
        for entry in entries:
            print "\n%s    entry: %s," % (self.__ind, entry),
        print ")"

class DisassemblerBundle(PythonBytecodeCompilingBundle):
    def __init__(self): pass
    def compile(self, signature, info, flags, storeable):
        return Disassembler(info)
    def loadHandler(self, loader): pass
    def saveFilesAndLoadHandle(self, loader, dir): pass

def _parse(*files):
    return Parser().parse(files)
def _pbc(ast, bundle, name):
    return ast.accept(AstToBytecode(bundle, name))
def _compile(filepath, name, *args,**kwargs):
    module = _pbc(_parse(filepath), DisassemblerBundle(*args, **kwargs), name)

OPNAMES = dict(LOAD_CONSTANT='LOAD_CONST', RETURN='RETURN_VALUE',
               LOAD='LOAD_ATTRIBUTE')

if __name__ == '__main__':
    from subprocess import Popen, PIPE
    f = sys.argv[1]
    p = Popen(['python', '-m', 'dis', f], stdout=PIPE)
    for line in p.stdout:
        cpy_dis.append('%s%s' % (line[:36], line[42:-2]))
    _compile(f, os.path.splitext(f)[0])
