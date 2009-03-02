package org.python.compiler;

import org.python.antlr.ast.ClassDef;
import org.python.antlr.ast.FunctionDef;
import org.python.antlr.ast.GeneratorExp;
import org.python.antlr.ast.Lambda;
import org.python.antlr.ast.VisitorIF;

public interface IntermediateCodeGenerator<RESULT, SCOPE, CARRIER> extends
        VisitorIF<RESULT> {

    public CARRIER beforeClassDef(ClassDef node, SCOPE scope) throws Exception;

    public void afterClassDef(ClassDef node, CARRIER data, RESULT result)
            throws Exception;

    public CARRIER beforeFunctionDef(FunctionDef node, SCOPE scope)
            throws Exception;

    public void afterFunctionDef(FunctionDef node, CARRIER data, RESULT result)
            throws Exception;

    public CARRIER beforeLambda(Lambda node, SCOPE scope) throws Exception;

    public RESULT afterLambda(Lambda node, CARRIER data, RESULT result)
            throws Exception;

    public CARRIER beforeGeneratorExp(GeneratorExp node, SCOPE scope)
            throws Exception;

    public RESULT afterGeneratorExp(GeneratorExp node, CARRIER data,
            RESULT result) throws Exception;

}
