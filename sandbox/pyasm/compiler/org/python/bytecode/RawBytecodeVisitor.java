package org.python.bytecode;

public interface RawBytecodeVisitor {

    void visitStop(Instruction stop);

    void visitLoad(VariableContext context, int nameIndex);

    void visitStore(VariableContext context, int nameIndex);

    void visitDelete(VariableContext context, int nameIndex);

    void visitLoadAttribute(int nameIndex);

    void visitStoreAttribute(int nameIndex);

    void visitDeleteAttribute(int nameIndex);

    void visitLoadClosure(int nameIndex);

    void visitForIteration(int delta);

    void visitLoadConstant(int constIndex);

    void visitRelativeJump(int delta);

    void visitAbsouteJump(int addr);

    void visitJumpIfTrue(int delta);

    void visitJumpIfFalse(int delta);

    void visitSetupLoop(int delta);

    void visitSetupExcept(int delta);

    void visitSetupFinally(int delta);

    void visitImportName(int nameIndex);

    void visitImportFrom(int nameIndex);

    void visitInstruction(Instruction instruction);

    void visitContinue(int addr);

    void visitYield();
}
