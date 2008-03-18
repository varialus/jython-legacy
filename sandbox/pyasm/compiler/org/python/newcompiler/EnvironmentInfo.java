package org.python.newcompiler;

import java.util.Set;

import org.python.bytecode.Label;
import org.python.bytecode.VariableContext;

public interface EnvironmentInfo {

    Iterable<String> closureVariables();

    VariableContext getVariableContextFor(String id);

    boolean isReenterable();

    Iterable<Label> getEntryPoints();

    Set<CompilerFlag> getCompilerFlags();
}
