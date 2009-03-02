package org.python.compiler;

import org.python.antlr.base.expr;

public interface ConstantChecker<T> {

    boolean acceptValue(expr value);

    T getConstantValue();

}
