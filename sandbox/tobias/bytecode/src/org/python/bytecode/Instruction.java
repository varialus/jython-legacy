package org.python.bytecode;

public interface Instruction {

    void accept(BytecodeVisitor visitor);
}
