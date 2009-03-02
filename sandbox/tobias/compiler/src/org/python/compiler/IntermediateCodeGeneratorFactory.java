package org.python.compiler;

import java.util.Map;

import org.python.antlr.ast.VisitorIF;
import org.python.core.CompilerFlags;

public interface IntermediateCodeGeneratorFactory<E, S, T> extends
        ScopeFactory<S> {
    IntermediateCodeGenerator<E, S, T> createCodeGenerator(boolean printExpr,
            Map<String, ConstantChecker<E>> constants, VisitorIF<E> driver,
            S scope, CompilerFlags flags);
}
