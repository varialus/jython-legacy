package org.python.newcompiler.bytecode;

import java.util.Set;

import org.python.newcompiler.BytecodeBundle;
import org.python.newcompiler.CodeInfo;
import org.python.newcompiler.CompilerFlag;

public interface PythonBytecodeCompilingBundle extends BytecodeBundle {

    BytecodeCompiler compile(String signature,
                             CodeInfo info,
                             Set<CompilerFlag> flags,
                             boolean storeable);
}
