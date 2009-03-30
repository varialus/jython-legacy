package org.python.compiler.advanced;

import java.util.Map;

import org.python.antlr.ast.VisitorIF;
import org.python.core.CompilerFlags;

public interface IntermediateCodeGeneratorFactory<RESULT, SCOPE, CARRIER>
        extends ScopeFactory<SCOPE> {
    IntermediateCodeGenerator<RESULT, SCOPE, CARRIER> createModuleCodeGenerator(
            boolean linenumbers, boolean printExpr, VisitorIF<RESULT> driver,
            ConstantPool constants, SCOPE scope, CompilerFlags flags);

    IntermediateCodeGenerator<RESULT, SCOPE, CARRIER> createClassCodeGenerator(
            boolean linenumbers, VisitorIF<RESULT> driver,
            ConstantPool constants, SCOPE scope, CompilerFlags flags);

    IntermediateCodeGenerator<RESULT, SCOPE, CARRIER> createFunctionCodeGenerator(
            boolean linenumbers, VisitorIF<RESULT> driver,
            ConstantPool constants, SCOPE scope, CompilerFlags flags);

    ConstantCodeGenerator<RESULT> createConstantCodeGenerator();
}
