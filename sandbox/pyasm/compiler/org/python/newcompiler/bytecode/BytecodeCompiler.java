package org.python.newcompiler.bytecode;

import java.util.Set;

import org.python.bytecode.BytecodeVisitor;
import org.python.bytecode.Label;
import org.python.newcompiler.CompilerFlag;
import org.python.newcompiler.CompilerVariable;

public interface BytecodeCompiler extends BytecodeVisitor {

    void visitResumeTable(Label start, Iterable<Label> entryPoints);

    BytecodeCompiler constructFunction(Iterable<String> closureNames,
                                       int numDefault,
                                       Set<CompilerFlag> flags);

    BytecodeCompiler constructFunction(Iterable<String> closureNames,
                                       int numDefault,
                                       Set<CompilerFlag> flags,
                                       String name);

    BytecodeCompiler constructClass(String name,
                                    Iterable<String> closureNames,
                                    Set<CompilerFlag> flags);

    CompilerVariable storeContextManagerExit();

    CompilerVariable enterContextManager();

    void loadVariable(CompilerVariable contextVariable);
}
