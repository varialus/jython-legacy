package org.python.compiler.advanced;

import org.python.antlr.PythonTree;
import org.python.antlr.ast.Expression;

public interface EnvironmentHolder {

    EnvironmentInfo getEnvironment(PythonTree node);

    YieldPoint getYieldPoint(Expression node);
}
