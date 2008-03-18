package org.python.bytecode;

public interface LineNumberTable {

    void visitInstruction(BytecodeVisitor visitor, int offset);
}
