package org.python.compiler.advanced;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.python.antlr.PythonTree;
import org.python.antlr.ast.ClassDef;
import org.python.antlr.ast.Expression;
import org.python.antlr.ast.FunctionDef;
import org.python.antlr.ast.GeneratorExp;
import org.python.antlr.ast.Interactive;
import org.python.antlr.ast.Lambda;
import org.python.antlr.ast.Return;
import org.python.antlr.ast.VisitorBase;
import org.python.antlr.base.mod;
import org.python.antlr.base.stmt;
import org.python.core.CompilerFlags;
import org.python.core.FutureFeature;

public final class CompilerDirector<RESULT, SCOPE, CARRIER> extends
        VisitorBase<RESULT> {
    private static final PragmaParser interactivePragmas = new PragmaParser(
            FutureFeature.PRAGMA_MODULE);
    private static final PragmaParser modulePragmas = new PragmaParser(
            FutureFeature.PRAGMA_MODULE);
    private final CompilerFlags flags;
    private final SyntaxErrorPolicy errorPolicy = null;
    private final Map<PythonTree, SCOPE> scopes = new HashMap<PythonTree, SCOPE>();
    private IntermediateCodeGenerator<RESULT, SCOPE, CARRIER> codegen;
    private ConstantPool constants;
    private final boolean printResults;
    private final boolean linenumbers;
    private final IntermediateCodeGeneratorFactory<RESULT, SCOPE, CARRIER> factory;
    private final ConstraintsDefinition constraints;

    public static <R, S, C> void compile(
            IntermediateCodeGeneratorFactory<R, S, C> factory,
            ConstraintsDefinition constraints, CompilerFlags cflags,
            boolean linenumbers, boolean printResults, mod base)
            throws Exception {
        base.accept(new CompilerDirector<R, S, C>(factory, constraints, cflags,
                linenumbers, printResults));
    }

    private CompilerDirector(
            IntermediateCodeGeneratorFactory<RESULT, SCOPE, CARRIER> factory,
            ConstraintsDefinition constraints, CompilerFlags cflags,
            boolean linenumbers, boolean printResults) {
        this.factory = factory;
        this.constraints = constraints;
        this.printResults = printResults;
        this.linenumbers = linenumbers;
        flags = new CompilerFlags();
    }

    @Override
    public void traverse(PythonTree node) throws Exception {
        // Don't do anything, the traversal is handled by the code generator
    }

    @Override
    protected RESULT unhandled_node(PythonTree node) throws Exception {
        // Not explicitly handled by this? then it has to be handled by the code generator
        return node.accept(codegen);
    }

    // Top level elements - initialize the compiler

    @Override
    public RESULT visitModule(org.python.antlr.ast.Module node)
            throws Exception {
        compile(modulePragmas, false, node.getInternalBody());
        return null;
    }

    @Override
    public RESULT visitInteractive(Interactive node) throws Exception {
        compile(interactivePragmas, printResults, node.getInternalBody());
        return null;
    }

    @Override
    public RESULT visitExpression(Expression node) throws Exception {
        compile(null, false, Arrays.asList(new Return(node,
                node.getInternalBody())));
        return null;
    }

    private void compile(PragmaParser pragmas, boolean printResults,
            List<? extends stmt> body) throws Exception {
        if (pragmas != null) {
            pragmas.parse(body, flags);
        }
        ConstantPool constants = new ConstantPool(constraints.apply(flags));
        SCOPE scope = ScopeAnalyzer.scan(errorPolicy, pragmas, constants,
                scopes, factory, body, false);
        constants.accept(factory.createConstantCodeGenerator());
        IntermediateCodeGenerator<RESULT, SCOPE, CARRIER> prevCode = codegen;
        ConstantPool prevConst = this.constants;
        this.constants = constants;
        try {
            codegen = factory.createModuleCodeGenerator(linenumbers,
                    printResults, this, constants, scope, flags);
            for (stmt statement : body) {
                statement.accept(this);
            }
        } finally {
            this.constants = prevConst;
            codegen = prevCode;
        }
    }

    // Scope defining elements

    @Override
    public RESULT visitClassDef(ClassDef node) throws Exception {
        SCOPE scope = scopes.get(node);
        CARRIER state = codegen.beforeClassDef(node, scope);
        IntermediateCodeGenerator<RESULT, SCOPE, CARRIER> old = codegen;
        RESULT result;
        try {
            codegen = factory.createClassCodeGenerator(linenumbers, this,
                    constants, scope, flags);
            result = codegen.visitClassDef(node);
        } finally {
            codegen = old;
        }
        codegen.afterClassDef(node, state, result);
        return result;
    }

    @Override
    public RESULT visitFunctionDef(FunctionDef node) throws Exception {
        SCOPE scope = scopes.get(node);
        CARRIER state = codegen.beforeFunctionDef(node, scope);
        IntermediateCodeGenerator<RESULT, SCOPE, CARRIER> old = codegen;
        RESULT result;
        try {
            codegen = factory.createFunctionCodeGenerator(linenumbers, this,
                    constants, scope, flags);
            result = codegen.visitFunctionDef(node);
        } finally {
            codegen = old;
        }
        codegen.afterFunctionDef(node, state, result);
        return result;
    }

    @Override
    public RESULT visitLambda(Lambda node) throws Exception {
        SCOPE scope = scopes.get(node);
        CARRIER state = codegen.beforeLambda(node, scope);
        IntermediateCodeGenerator<RESULT, SCOPE, CARRIER> old = codegen;
        RESULT result;
        try {
            codegen = factory.createFunctionCodeGenerator(linenumbers, this,
                    constants, scope, flags);
            result = codegen.visitLambda(node);
        } finally {
            codegen = old;
        }
        return codegen.afterLambda(node, state, result);
    }

    @Override
    public RESULT visitGeneratorExp(GeneratorExp node) throws Exception {
        SCOPE scope = scopes.get(node);
        CARRIER state = codegen.beforeGeneratorExp(node, scope);
        IntermediateCodeGenerator<RESULT, SCOPE, CARRIER> old = codegen;
        RESULT result;
        try {
            codegen = factory.createModuleCodeGenerator(linenumbers, false,
                    this, constants, scope, flags);
            result = codegen.visitGeneratorExp(node);
        } finally {
            codegen = old;
        }
        return codegen.afterGeneratorExp(node, state, result);
    }
}