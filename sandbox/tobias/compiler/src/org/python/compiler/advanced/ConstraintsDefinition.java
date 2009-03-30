package org.python.compiler.advanced;

import java.util.Collection;

import org.python.core.CompilerFlags;

public interface ConstraintsDefinition {
    Collection<ConstantChecker> apply(CompilerFlags flags);
}
