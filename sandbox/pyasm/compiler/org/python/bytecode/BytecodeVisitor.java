package org.python.bytecode;

import org.python.core.PyObject;

public interface BytecodeVisitor {

    void visitLineNumber(int lineNumber);

    void visitStop();

    void visitPop();

    void visitRot(int numberOfElements);

    void visitDup(int numberOfElements);

    void visitNop();

    void visitUnaryOperator(UnaryOperator operator);

    void visitBinaryOperator(BinaryOperator operator);

    void visitInplaceOperator(BinaryOperator operator);

    void visitCompareOperator(ComparisonOperator compareOperator);

    void visitListAppend();

    void visitSetAdd();

    void visitLoadSubscript();

    void visitStoreSubscript();

    void visitDeleteSubscript();

    void visitLoadSlice(SliceMode mode);

    void visitStoreSlice(SliceMode mode);

    void visitDeleteSlice(SliceMode mode);

    void visitLoad(VariableContext context, String name);

    void visitStore(VariableContext context, String name);

    void visitDelete(VariableContext context, String name);

    void visitLoadAttribute(String attr);

    void visitStoreAttribute(String attr);

    void visitDeleteAttribute(String attr);

    void visitPrintExpression();

    void visitPrintItem();

    void visitPrintNewline();

    void visitPrintItemTo();

    void visitPrintNewlineTo();

    void visitBreak();

    void visitWithCleanup();

    void visitLoadLocals();

    void visitReturn();

    void visitYield(int index, Label label);

    void visitExec();

    void visitPopBlock();

    void visitEndFinally();

    void visitBuildClass();

    void visitUnpackSequence(int before, boolean hasStar, int after);

    void visitForIteration(Label end);

    void visitLoadConstant(PyObject constant);

    void visitBuildTuple(int size);

    void visitBuildList(int size);

    void visitBuildMap(int zero);

    void visitLabel(Label label);

    void visitJump(Label destination);

    void visitJumpIfTrue(Label destination);

    void visitJumpIfFalse(Label destination);

    void visitContinue(Label destination);

    void visitSetupLoop(Label end);

    void visitSetupExcept(Label end);

    void visitSetupFinally(Label end);

    void visitRaise(int numArgs);

    void visitCall(boolean varPos, boolean varKey, int numPos, int numKey);

    void visitBuildFunction(boolean closure, int numDefault);

    void visitBuildSlice(int numIndices);

    void visitLoadClosure(String variable);

    void visitImportAll();

    void visitImportName(String name);

    void visitImportFrom(String name);

    void visitBuildSet(int size);
}
