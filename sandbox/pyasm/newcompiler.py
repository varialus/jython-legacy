#from org.python.core.BytecodeLoader import makeCode
from org.python.objectweb import asm
from org.python.objectweb.asm import Type, Opcodes as Op
from org.python.objectweb.asm.commons import GeneratorAdapter, Method,\
    TableSwitchGenerator
from org.python import core

from org.python.newcompiler.bytecode import PythonBytecodeCompilingBundle,\
    BytecodeCompiler
from org.python.bytecode import BinaryOperator,UnaryOperator,ComparisonOperator

from jarray import array
import java

__debugging__ = False

def reallyContains(dct, item):
    if item in dct:
        keys = list(dct.keys())
        return item is keys[keys.index(item)]
    return False

def getType(clazz):
    if isinstance(clazz,(str,unicode)):
        return Type.getObjectType(clazz)
    else:
        return Type.getType(clazz)

def getArrayType(typ):
    if not isinstance(typ,Type):
        typ = getType(typ)
    return Type.getType("[%s" % typ.getDescriptor())

def method(resType, name, *argTypes):
    return Method.getMethod("%s %s (%s)" % (
            resType.getDescriptor(), name, ",".join([
                    arg.getDescriptor() for arg in argTypes])))
def constructor(*argTypes):
    return method(voidType, "<init>", *argTypes)

def stringArray(*lst):
    return array(lst,java.lang.String)

pyObjectType = getType(object)
pyFrameType = getType(core.PyFrame)
pyCodeType = getType(core.PyCode)
pyType = getType(core.Py)
pySysType = getType(core.PySystemState)
pyExceptionType = getType(core.PyException)
pyBooleanType = getType(bool)
pyTupleType = getType(tuple)
pyListType = getType(list)
pyDictType = getType(dict)
pyStringType = getType(str)
pyBuiltin = getType(core.__builtin__)
imp = getType(core.imp)
compilerResources = getType(core.NewCompilerResources)
objectType = getType(java.lang.Object)
stringType = getType(java.lang.String)
throwableType = getType(java.lang.Throwable)

class Switch(TableSwitchGenerator):
    def __init__(self, generator):
        self.__generator = generator
    def generateCase(self, key, end):
        self.__generator(key, end)
    def generateDefault(self):
        self.__generator(None, None)

class Bundle(PythonBytecodeCompilingBundle):
    def __init__(self):
        pass
    def compile(self, signature, info, flags, storeable):
        return Tracer(info)
    def loadHandle(self, loader):
        pass
    def saveFilesAndLoadHandle(self, loader, dir):
        pass

class Tracer(BytecodeCompiler):
    def __init__(self, codeinfo, level=0):
        self.__codeinfo = codeinfo
        self.__pre = "pyasm."
        self.__level = level
        self.__ind = " "*(4*level)
        self.reference = CodeReference(codeinfo)
    def __join(self, it, sep=','):
        if it is not None:
            return sep.join(it)
        else:
            return ""
    def __str__(self):
        return self.__repr__()
    def __repr__(self):
        return "Tracer()"
    def constructClass(self, name, closureNames, flags):
        print "%s%sconstructClass(%s, [%s], [%s]):" % (
            self.__ind, self.__pre, name,
            self.__join(closureNames), self.__join(flags))
        return Tracer(self.__codeinfo, self.__level+1)    
    def constructFunction(self, closureNames, numDefault, flags, name=None):
        print "%s%sconstructFunction([%s], %s, [%s], %s):" % (
            self.__ind, self.__pre, self.__join(closureNames),
            numDefault, self.__join(flags), name)
        return Tracer(self.__codeinfo, self.__level+1)    
    def __getattr__(self,attr):
        def visit(*args):
            print "%s%s%s%s" % (self.__ind, self.__pre, attr,args)
        return visit
    def visitStop(self):
        print "%s%svisitStop()" % (self.__ind, self.__pre)
        print
    def visitResumeTable(self, start, entries):
        print "%s%svisitResumeTable(" % (self.__ind, self.__pre)
        print "%s    start: %s," % (self.__ind, start),
        for entry in entries:
            print "\n%s    entry: %s," % (self.__ind, entry),
        print ")"

class CodeReference(object):
    def __init__(self, info):
        self.__info = info
    def __str__(self):
        return self.__repr__()
    def __repr__(self):
        return "CodeReference(%s)" % (self.__info,)
    def __getattr__(self,attr):
        def meth(*args):
            print "%s.%s%s" % (self,attr,args)
        return meth

class PyAsmCompiler(BytecodeCompiler):
    def __init__(self, codeinfo):
        self.__codeinfo = codeinfo
        self.reference = CodeReference(codeinfo)
        self.transferManager = None # TODO
        self.asm = None


    def constructClass(self, name, closureNames, flags):
        pass
    
    def constructFunction(self, closureNames, numDefault, flags, name=None):
        pass

    def enterContextManager(self):
        pass
    
    def loadVariable(self, contextVariable):
        pass
    
    def storeContextManagerExit(self):
        pass

    
    def visitStop(self):
        """The final method to get invoked."""
        pass # TODO: implement some finalization here
    
    def visitResumeTable(self, start, entryPoints):
        self.transferManager.loadEntryVariable(self.asm)
        def labels():
            yield start
            for label in entryPoints:
                yield label
        error = self.label()
        labels = [self.label(l) for l in labels()]
        self.asm.visitTableSwitchInsn(0, len(labels), error,
                                      array(labels, asm.Label))
        self.asm.visitLabel(error)
        # TODO: throw some illegal entry exception here
        self.asm.visitLabel(start)

    binaryOperator = {BinaryOperator.ADD:          '_add',
                      BinaryOperator.SUBTRACT:     '_sub',
                      BinaryOperator.MULTIPLY:     '_mul',
                      BinaryOperator.DIVIDE:       '_div',
                      BinaryOperator.FLOOR_DIVIDE: '_floordiv',
                      BinaryOperator.TRUE_DIVIDE:  '_truediv',
                      BinaryOperator.MODULO:       '_mod',
                      BinaryOperator.POWER:        '_pow',
                      BinaryOperator.SHIFT_LEFT:   '_lshift',
                      BinaryOperator.SHIFT_RIGHT:  '_rshift',
                      BinaryOperator.AND:          '_and',
                      BinaryOperator.OR:           '_or',
                      BinaryOperator.XOR:          '_xor',}
    def visitBinaryOperator(self, operator):
        self.asm.invokeVirtual(pyObjectType,
                               method(pyObjectType,
                                      self.binaryOperator[operator],
                                      pyObjectType))

    inplaceOperator = {BinaryOperator.ADD:          '__iadd__',
                       BinaryOperator.SUBTRACT:     '__isub__',
                       BinaryOperator.MULTIPLY:     '__imul__',
                       BinaryOperator.DIVIDE:       '__idiv__',
                       BinaryOperator.FLOOR_DIVIDE: '__ifloordiv__',
                       BinaryOperator.TRUE_DIVIDE:  '__itruediv__',
                       BinaryOperator.MODULO:       '__imod__',
                       BinaryOperator.POWER:        '__ipow__',
                       BinaryOperator.SHIFT_LEFT:   '__ilshift__',
                       BinaryOperator.SHIFT_RIGHT:  '__irshift__',
                       BinaryOperator.AND:          '__iand__',
                       BinaryOperator.OR:           '__ior__',
                       BinaryOperator.XOR:          '__ixor__',}
    def visitInplaceOperator(self, operator):
        self.asm.invokeVirtual(pyObjectType,
                               method(pyObjectType,
                                      self.inplaceOperator[operator],
                                      pyObjectType))

    unaryOperator = {UnaryOperator.INVERT:   '__invert__',
                     UnaryOperator.POSITIVE: '__pos__',
                     UnaryOperator.NEGATIVE: '__neg__',
                     UnaryOperator.NOT:      '__not__',
                     UnaryOperator.CONVERT:  '__repr__',}
    def visitUnaryOperator(self, operator):
        if operator is UnaryOperator.CONVERT:
            rType = pyStringType
        else:
            rType = pyObjectType
        self.asm.invokeVirtual(pyObjectType,
                               method(rType, self.unaryOperator[operator]))
    
    compareOperator = {ComparisonOperator.LESS_THAN:             '_lt',
                       ComparisonOperator.LESS_THAN_OR_EQUAL:    '_le',
                       ComparisonOperator.EQUAL:                 '_eq',
                       ComparisonOperator.NOT_EQUAL:             '_ne',
                       ComparisonOperator.GREATER_THAN:          '_gt',
                       ComparisonOperator.GREATER_THAN_OR_EQUAL: '_ge',
                       ComparisonOperator.IN:                    '_in',
                       ComparisonOperator.NOT_IN:                '_notin',
                       ComparisonOperator.IS:                    '_is',
                       ComparisonOperator.IS_NOT:                '_isnot',}
    def visitCompareOperator(self, operator):
        if operator is ComparisonOperator.EXCEPTION_MATCH:
            self.asm.invokeStatic(pyType, method(Type.BOOLEAN_TYPE,
                                                 "matchException",
                                                 pyExceptionType, pyObjectType))
            self.asm.invokeStatic(pyType, method(pyBooleanType, "newBoolean",
                                                 Type.BOOLEAN_TYPE))
        else:
            self.asm.invokeVirtual(pyObjectType,
                                   method(pyObjectType,
                                          self.compareOperator[operator],
                                          pyObjectType))
    
    def visitBreak(self):
        pass # TODO: implement this with good block management
    
    def visitBuildClass(self):
        # TODO: something with newLocal, this needs to be prettier
        sequenceType = getType(core.PySequenceList)
        bases = self.newLocal(getArrayType(pyObjectType), "bases")
        dict = self.newLocal(pyObjectType, "dict")
        dict.store()
        self.asm.checkCast(sequenceType)
        self.asm.invokeVirtual(sequenceType, Method.getMethod(
                "org.python.core.PyObject[] getArray ()"))
        bases.store()
        self.asm.invokeVirtual(pyObjectType, Method.getMethod(
                "String toString ()"))
        bases.load(); bases.end()
        dict.load(); dict.end()
        self.asm.invokeStatic(compilerResources, Method.getMethod(
                "org.python.core.PyObject makeClass (%s)" % ", ".join(
                    ["String", "org.python.core.PyObject[]",
                     "org.python.core.PyObject"])))

    
    def visitBuildFunction(self, closure, numDefault):
        code = self.newTemp(pyCodeType)
        code.store()
        if closue:
            cell = self.newTemp(pySequenceType)
            cell.store()
            init = constructor(pyObjectType,  # globals
                               pyObjectArray, # defaults
                               pyCodeType,    # code
                               pyObjectArray) # closure_cells
        else:
            cell = None
            init = constructor(pyObjectType,  # globals
                               pyObjectArray, # defaults
                               pyCodeType)    # code
        self.newPopulatedArray(pyObjectType, numDefault)
        default = self.newTemp(pyObjectArray)
        default.store()
        self.asm.newInstance(pyFunctionType)
        self.asm.dup()
        self.loadFrame()
        self.asm.getField(pyFrameType, "f_globals", pyObjectType)
        default.load(); default.end()
        code.load(); code.end()
        if closure:
            cell.load(); cell.end()
            self.asm.invokeVirtual(pySequenceType,
                                   method(pyObjectType, "getArray"))
        self.asm.invokeConstructor(pyFunctionType, init)
    
    def visitBuildTuple(self, size):
        self.buildSequence(pyTupleType, size)

    def visitBuildList(self, size):
        self.buildSequence(pyListType, size)
    
    def visitBuildSet(self, size):
        self.buildSequence(pySetType, size)
    
    def visitBuildMap(self, zero):
        assert zero == 0
        self.asm.newInstance(pyDictType)
        self.asm.dup()
        self.asm.invokeConstructor(pyDictType, constructor())
    
    def visitBuildSlice(self, numIndices):
        if numargs == 3:
            pass
        if numargs == 2:
            self.push(None)
        else:
            raise TypeError("Can only build slices from 2 or 3 arguments.")
        step = self.newTemp(pyObjectType)
        step.store()
        self.asm.newInstance(pySliceType)
        self.asm.dup()
        self.asm.dup2X2()
        self.asm.pop()
        self.asm.pop()
        step.load(); step.end()
        self.asm.invokeConstructor(pySliceType, constructor(*[pyObjectType]*3))
    
    def visitCall(self, varPos, varKey, numPos, numKey):
        # FIXME: implement this
        pass
    
    def visitContinue(self, loopStart):
        # FIXME: implement this with nice new block subsystem
        pass
    
    def visitLoadConstant(self, constant):
        pass
    
    def visitLoadClosure(self, variable):
        self.loadFrame()
        # todo getIndex
    
    def visitLoadLocals(self):
        pass
    
    def visitLoad(self, context, name):
        pass
    
    def visitStore(self, context, name):
        pass
    
    def visitDelete(self, context, name):
        pass

    def visitLoadAttribute(self, attr):
        pass
    
    def visitStoreAttribute(self, attr):
        pass
    
    def visitDeleteAttribute(self, attr):
        pass
    
    def visitLoadSlice(self, mode):
        pass
    
    def visitStoreSlice(self, mode):
        pass
    
    def visitDeleteSlice(self, mode):
        pass
    
    def visitLoadSubscript(self):
        pass

    def visitStoreSubscript(self):
        pass
    
    def visitDeleteSubscript(self):
        pass
    
    def visitDup(self, numberOfElements):
        pass
    
    def visitEndFinally(self):
        pass
    
    def visitExec(self):
        pass
    
    def visitForIteration(self, end):
        pass
    
    def visitImportAll(self):
        pass
    
    def visitImportFrom(self, name):
        pass
    
    def visitImportName(self, name):
        pass
    
    def visitJump(self, destination):
        pass
    
    def visitJumpIfFalse(self, destination):
        pass
    
    def visitJumpIfTrue(self, destination):
        pass
    
    def visitLabel(self, label):
        pass
    
    def visitLineNumber(self, lineNumber):
        pass
    
    def visitListAppend(self):
        pass
        
    def visitNop(self):
        pass
    
    def visitPop(self):
        pass
    
    def visitPopBlock(self):
        pass
    
    def visitPrintExpression(self):
        pass
    
    def visitPrintItem(self):
        pass
    
    def visitPrintItemTo(self):
        pass
    
    def visitPrintNewline(self):
        pass
    
    def visitPrintNewlineTo(self):
        pass
    
    def visitRaise(self, numArgs):
        pass
    
    def visitReturn(self):
        pass
    
    def visitRot(self, numberOfElements):
        pass
    
    def visitSetAdd(self):
        pass
    
    def visitSetupExcept(self, end):
        pass
    
    def visitSetupFinally(self, end):
        pass
    
    def visitSetupLoop(self, end):
        pass
    
    def visitUnpackSequence(self, before, hasStar, after):
        pass
    
    def visitWithCleanup(self):
        pass
    
    def visitYield(self):
        pass

