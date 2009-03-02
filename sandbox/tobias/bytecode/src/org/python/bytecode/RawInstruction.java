package org.python.bytecode;

public interface RawInstruction {

    void acceptRaw(RawBytecodeVisitor visitor);
}
