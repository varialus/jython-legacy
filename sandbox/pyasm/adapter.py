# -*- coding: utf-8 -*-
from pyasm import ASMVisitor, Operator
from org.python.newcompiler.bytecode import PythonBytecodeCompilingBundle,\
    BytecodeCompiler
from org.python.bytecode import BinaryOperator, UnaryOperator,\
    ComparisonOperator, VariableContext, SliceMode, ConstantStore
from org.python.newcompiler import CompilerFlag, CodeInfo

__debugging__ = False

class Constants(ConstantStore):
    def __init__(self):
        global lastConstants
        lastConstants = self
        self._names = []
        self._varnames = []
        self._cellvars = []
        self._freevars = []
        self._constants = []
    def names(self,value):
        self._names.extend(value)
    def varnames(self,value):
        self._varnames.extend(value)
    def cellvars(self,value):
        self._cellvars.extend(value)
    def freevars(self,value):
        self._freevars.extend(value)
    def constants(self,value):
        self._constants.extend(value)

    def getName(self, index):
        return self._names[index]

    def getVariableName(self, index):
        return self._varnames[index]

    def getClosureName(self, index):
        if index < len(self._cellvars):
            return self._cellvars[index]
        else:
            return self._freevars[index - len(self._cellvars)]

    def getConstant(self, index):
        return self._constants[index]

class BytecodeInfo(CodeInfo):
    def getArgumentCount(self):
        return self._argcount
    def getLocalsCount(self):
        return self._nlocals
    def getMaxStackSize(self):
        return self._stacksize
    def getFilename(self):
        return self._filename
    def getName(self):
        return self._name

class Bundle(PythonBytecodeCompilingBundle):
    def __init__(self):
        self._visitor = None
        self.visitorStack = []
        self.codeStack = []
    def _init(self):
        visitor, info, flags, const = self.visitorStack[-1]
        visitor.visitCode(info.getArgumentCount(),
                          info.getLocalsCount(),
                          info.getMaxStackSize(),
                          CompilerFlag.toBitFlags(flags),
                          [], # constants
                          const._names,
                          const._varnames,
                          const._freevars,
                          const._cellvars,
                          info.getFilename(), info.getName(),
                          0) # first line number
    def compile(self, signature, info, flags, storeable):
        if self.visitorStack:
            visitor, i,f,c = self.visitorStack[-1]
        else:
            visitor = None
        visitor = ASMVisitor(0, visitor)
        self.visitorStack.append((visitor,info,flags, lastConstants))
        adapter = PyAsmAdapter(self, visitor)
        if __debugging__:
            return Tracer(adapter)
        else:
            return adapter
    def loadHandle(self, loader):
        return self.codeStack.pop()
    def saveFilesAndLoadHandle(self, loader, dir):
        return self.loadHandle()

class ContextManager(object):
    def __init__(self, kind):
        self.__load = "visitLoad%s" % kind
        self.__store = "visitStore%s" % kind
        self.__delete = "visitDelete%s" % kind
    def load(self, pyasm, name):
        getattr(pyasm, self.__load)( name )
    def store(self, pyasm, name):
        getattr(pyasm, self.__store)( name )
    def delete(self, pyasm, name):
        getattr(pyasm, self.__delete)( name )

class Tracer(BytecodeCompiler):
    def __init__(self, next, level=0):
        self.__pre = "pyasm."
        self.__level = level
        self.__ind = " "*(4*level)
        self.__next = next
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
        next = self.__next.constructClass(name, closureNames, flags)
        return Tracer(next, self.__level+1)
    def constructFunction(self, closureNames, numDefault, flags, name=None):
        print "%s%sconstructFunction([%s], %s, [%s], %s):" % (
            self.__ind, self.__pre, self.__join(closureNames),
            numDefault, self.__join(flags), name)
        next = self.__next.constructFunction(closureNames, numDefault,
                                             flags, name)
        return Tracer(next, self.__level+1)    
    def __getattr__(self,attr):
        def visit(*args):
            print "%s%s%s%s" % (self.__ind, self.__pre, attr,args)
            return getattr(self.__next, attr)(*args)
        return visit
    def visitStop(self):
        print "%s%svisitStop()" % (self.__ind, self.__pre)
        print
        return self.__next.visitStop()
    def visitResumeTable(self, start, entries):
        print "%s%svisitResumeTable(" % (self.__ind, self.__pre)
        print "%s    start: %s," % (self.__ind, start),
        for entry in entries:
            print "\n%s    entry: %s," % (self.__ind, entry),
        print ")"
        return self.__next.visitResumeTable(start, entries)

varCtx = {VariableContext.UNQUALIFIED: ContextManager('Name'),
          VariableContext.GLOBAL:      ContextManager('Global'),
          VariableContext.LOCAL:       ContextManager('Fast'),
          VariableContext.CLOSURE:     ContextManager('Deref'), }

class PyAsmAdapter(BytecodeCompiler):
    def __init__(self, bundle, visitor):
        self.pyasm = visitor
        self.bundle = bundle

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
        self.pyasm.visitEnd()
        self.bundle.visitorStack.pop()
        self.bundle.codeStack.append(self.pyasm.getCode())
        self.pyasm = None
        self.bundle = None
        
    def visitResumeTable(self, start, entryPoints):
        self.pyasm.visitResumeTable(start, [entry for entry in entryPoints])
        self.pyasm.visitLabel(start)

    operators = {
        # Binary operators
        BinaryOperator.ADD:          Operator.ADD,
        BinaryOperator.SUBTRACT:     Operator.SUBTRACT,
        BinaryOperator.MULTIPLY:     Operator.MULTIPLY,
        BinaryOperator.DIVIDE:       Operator.DIVIDE,
        BinaryOperator.FLOOR_DIVIDE: Operator.FLOOR_DIVIDE,
        BinaryOperator.TRUE_DIVIDE:  Operator.TRUE_DIVIDE,
        BinaryOperator.MODULO:       Operator.MODULO,
        BinaryOperator.POWER:        Operator.POWER,
        BinaryOperator.SHIFT_LEFT:   Operator.LSHIFT,
        BinaryOperator.SHIFT_RIGHT:  Operator.RSHIFT,
        BinaryOperator.AND:          Operator.AND,
        BinaryOperator.OR:           Operator.OR,
        BinaryOperator.XOR:          Operator.XOR,
        # Unary operators
        UnaryOperator.INVERT:   Operator.INVERT,
        UnaryOperator.POSITIVE: Operator.POSITIVE,
        UnaryOperator.NEGATIVE: Operator.NEGATIVE,
        UnaryOperator.NOT:      Operator.NOT,
        UnaryOperator.CONVERT:  Operator.CONVERT,
        # Comparison operators
        ComparisonOperator.LESS_THAN:            Operator.LESS_THAN,
        ComparisonOperator.LESS_THAN_OR_EQUAL:   Operator.LESS_THAN_OR_EQUAL,
        ComparisonOperator.EQUAL:                Operator.EQUAL,
        ComparisonOperator.NOT_EQUAL:            Operator.NOT_EQUAL,
        ComparisonOperator.GREATER_THAN:         Operator.GREATER_THAN,
        ComparisonOperator.GREATER_THAN_OR_EQUAL:Operator.GREATER_THAN_OR_EQUAL,
        ComparisonOperator.IN:                   Operator.IN,
        ComparisonOperator.NOT_IN:               Operator.NOT_IN,
        ComparisonOperator.IS:                   Operator.IS,
        ComparisonOperator.IS_NOT:               Operator.IS_NOT,}
    def visitBinaryOperator(self, operator):
        self.pyasm.visitBinaryOperator(self.operators[operator])

    def visitInplaceOperator(self, operator):
        self.pyasm.visitInplaceOperator(self.operators[operator])

    def visitUnaryOperator(self, operator):
        if operator is UnaryOperator.ITERATOR:
            self.pyasm.visitGetIterator()
        else:
            self.pyasm.visitUnaryOperator(self.operators[operator])
    
    def visitCompareOperator(self, operator):
        self.pyasm.visitCompareOperator(self.operators[operator])
    
    def visitBreak(self):
        self.pyasm.visitBreakLoop()
    
    def visitBuildClass(self):
        self.pyasm.visit
    
    def visitBuildFunction(self, closure, numDefault):
        if closure:
            self.pyasm.visitMakeClosure(numDefault)
        else:
            self.pyasm.visitMakeFunction(numDefault)

    def visitBuildTuple(self, size):
        self.pyasm.visitBuildTuple(size)

    def visitBuildList(self, size):
        self.pyasm.visitBuildList(size)
    
    def visitBuildSet(self, size):
        self.pyasm.visitBuildSet(size)
    
    def visitBuildMap(self, zero):
        self.pyasm.visitBuildMap(zero)
    
    def visitBuildSlice(self, numIndices):
        self.pyasm.visitBuildSlice(numIndices)
    
    def visitCall(self, varPos, varKey, numPos, numKey):
        if varPos and varKey:
            self.pyasm.visitCallFunctionVarargKeyword(numPos, numKey)
        elif varPos:
            self.pyasm.visitCallFunctionVararg(numPos, numKey)
        elif varKey:
            self.pyasm.visitCallFunctionKeyword(numPos, numKey)
        else:
            self.pyasm.visitCallFunction(numPos, numKey)
    
    def visitContinue(self, loopStart):
        self.pyasm.visitContinueLoop(loopStart)
    
    def visitLoadConstant(self, constant):
        self.pyasm.visitLoadConstant(constant)
    
    def visitLoadClosure(self, variable):
        self.pyasm.visitLoadClosure(variable)
    
    def visitLoadLocals(self):
        self.pyasm.visitLoadLocals()
    
    def visitLoad(self, context, name):
        varCtx[context].load(self.pyasm, name)
    
    def visitStore(self, context, name):
        varCtx[context].store(self.pyasm, name)
    
    def visitDelete(self, context, name):
        varCtx[context].delete(self.pyasm, name)

    def visitLoadAttribute(self, attr):
        self.pyasm.visitLoadAttribute(attr)
    
    def visitStoreAttribute(self, attr):
        self.pyasm.visitStoreAttribute(attr)
    
    def visitDeleteAttribute(self, attr):
        self.pyasm.visitDeleteAttribute(attr)
    
    slicePlus = {SliceMode.PLUS_0: 0,
                 SliceMode.PLUS_1: 1,
                 SliceMode.PLUS_2: 2,
                 SliceMode.PLUS_3: 3}
    def visitLoadSlice(self, mode):
        self.pyasm.visitLoadSlice(self.slicePlus[mode])
    
    def visitStoreSlice(self, mode):
        self.pyasm.visitStoreSlice(self.slicePlus[mode])
    
    def visitDeleteSlice(self, mode):
        self.pyasm.visitDeleteSlice(self.slicePlus[mode])
    
    def visitLoadSubscript(self):
        self.pyasm.visitBinaryOperator(Operator.SUBSCRIPT)

    def visitStoreSubscript(self):
        self.pyasm.visitStoreSubscript()
    
    def visitDeleteSubscript(self):
        self.pyasm.visitDeleteSubscript()
    
    def visitDup(self, numberOfElements):
        self.pyasm.visitDup(numberOfElements)
    
    def visitEndFinally(self):
        self.pyasm.visitEndFinally()
    
    def visitExec(self):
        self.pyasm.visitExecStatement()
    
    def visitForIteration(self, end):
        self.pyasm.visitForIteration(end)
    
    def visitImportAll(self):
        self.pyasm.visitImportStar()
    
    def visitImportFrom(self, name):
        self.pyasm.visitImportFrom(name)
    
    def visitImportName(self, name):
        self.pyasm.visitImportName(name)
    
    def visitJump(self, destination):
        self.pyasm.visitJump(destination)
    
    def visitJumpIfFalse(self, destination):
        self.pyasm.visitJumpIfFalse(destination)
    
    def visitJumpIfTrue(self, destination):
        self.pyasm.visitJumpIfTrue(destination)
    
    def visitLabel(self, label):
        self.pyasm.visitLabel(label)
    
    def visitLineNumber(self, lineNumber):
        self.pyasm.visitLineNumber(lineNumber)
    
    def visitListAppend(self):
        self.pyasm.visitListAppend()
        
    def visitNop(self):
        self.pyasm.visitNop()
    
    def visitPop(self):
        self.pyasm.visitPop()
    
    def visitPopBlock(self):
        self.pyasm.visitPopBlock()
    
    def visitPrintExpression(self):
        self.pyasm.visitPrintExpressio()
    
    def visitPrintItem(self):
        self.pyasm.visitPrintItem()
    
    def visitPrintItemTo(self):
        self.pyasm.visitPrintItemTo()
    
    def visitPrintNewline(self):
        self.pyasm.visitPrintNewline()
    
    def visitPrintNewlineTo(self):
        self.pyasm.visitPrintNewlineTo()
    
    def visitRaise(self, numArgs):
        self.pyasm.visitRaiseVarargs(numArgs)
    
    def visitReturn(self):
        self.pyasm.visitReturnValue()
    
    def visitRot(self, numberOfElements):
        self.pyasm.visitRot(numberOfElements)
    
    def visitSetAdd(self):
        raise RuntimeError("SET_ADD is not supported by the old PyASM")
    
    def visitSetupExcept(self, end):
        self.pyasm.visitSetupExcept(end)
    
    def visitSetupFinally(self, end):
        self.pyasm.visitSetupFinally(end)
    
    def visitSetupLoop(self, end):
        self.pyasm.visitSetupLoop(end)
    
    def visitUnpackSequence(self, before, hasStar, after):
        message = "The old PyASM does not support star sequence unpacking"
        assert hasStar == False, message
        assert after == 0, message
        self.pyasm.visitUnpackSequence(before)
    
    def visitWithCleanup(self):
        self.pyasm.visitWithCleanup()
    
    def visitYield(self, index, label):
        self.pyasm.visitYieldValue(index, label)

