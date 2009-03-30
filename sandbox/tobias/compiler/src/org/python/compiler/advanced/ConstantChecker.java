package org.python.compiler.advanced;

import org.python.antlr.base.expr;

public interface ConstantChecker {
    boolean acceptValue(expr value);

    String name();

}
