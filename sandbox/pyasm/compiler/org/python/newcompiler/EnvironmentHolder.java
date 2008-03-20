package org.python.newcompiler;

import org.python.antlr.PythonTree;
import org.python.antlr.ast.exprType;

public interface EnvironmentHolder {

    EnvironmentInfo getEnvironment(PythonTree node);

    YieldPoint getYieldPoint(exprType node);
}
