package org.python.compiler.bytecode;

import java.util.Set;

import org.python.compiler.advanced.BytecodeBundle;
import org.python.compiler.advanced.CodeInfo;
import org.python.compiler.advanced.CompilerFlag;

public interface PythonBytecodeCompilingBundle extends BytecodeBundle {

    BytecodeCompiler compile(String signature,
                             CodeInfo info,
                             Set<CompilerFlag> flags,
                             boolean storeable);
}
