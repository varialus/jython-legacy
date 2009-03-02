package org.python.compiler.advanced;

public interface BytecodeLoader {

    Class makeClass(String name, byte[] bytecode);
}
