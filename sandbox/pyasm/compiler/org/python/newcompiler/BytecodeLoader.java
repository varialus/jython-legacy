package org.python.newcompiler;

public interface BytecodeLoader {

    Class makeClass(String name, byte[] bytecode);
}
