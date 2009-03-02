package org.python.compiler;

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

class CompilerDirector<E, S, H> extends VisitorBase<E> {
    private static final PragmaParser interactivePragmas = new PragmaParser(
            FutureFeature.PRAGMA_MODULE);
    private static final PragmaParser modulePragmas = new PragmaParser(
            FutureFeature.PRAGMA_MODULE);
    private final CompilerFlags flags;
    private final SyntaxErrorPolicy errorPolicy = null;
    private final Map<PythonTree, S> scopes = new HashMap<PythonTree, S>();
    private IntermediateCodeGenerator<E, S, H> codegen;
    private final boolean printResults;
    private final boolean linenumbers; // TODO
    private final IntermediateCodeGeneratorFactory<E, S, H> factory;
    private final ConstraintsDefinition<E> constraints;
    private final Map<String, ConstantChecker<E>> constNames = new HashMap<String, ConstantChecker<E>>();

    public static <E, S, H> void compile(
            IntermediateCodeGeneratorFactory<E, S, H> factory,
            ConstraintsDefinition<E> constraints, CompilerFlags cflags,
            boolean linenumbers, boolean printResults,
            mod base) throws Exception {
        base.accept(new CompilerDirector<E, S, H>(factory, constraints, cflags,
                linenumbers, printResults));
    }

    private CompilerDirector(IntermediateCodeGeneratorFactory<E, S, H> factory,
            ConstraintsDefinition<E> constraints, CompilerFlags cflags,
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
    protected E unhandled_node(PythonTree node) throws Exception {
        // Not explicitly handled by this? then it has to be handled by the code generator
        return node.accept(codegen);
    }

    // Top level elements - initialize the compiler

    @Override
    public E visitModule(org.python.antlr.ast.Module node) throws Exception {
        compile(modulePragmas, false, node.getInternalBody());
        return null;
    }

    @Override
    public E visitInteractive(Interactive node) throws Exception {
        compile(interactivePragmas, printResults, node.getInternalBody());
        return null;
    }

    @Override
    public E visitExpression(Expression node) throws Exception {
        compile(null, false, Arrays.asList(new Return(node, node.getInternalBody())));
        return null;
    }

    private void compile(PragmaParser pragmas, boolean printResults,
            List<? extends stmt> body) throws Exception {
        if (pragmas != null) {
            pragmas.parse(body, flags);
        }
        constraints.update(flags, constNames);
        S scope = ScopesBuilder.scan(errorPolicy, pragmas, constNames, scopes,
                factory, body);
        IntermediateCodeGenerator<E, S, H> old = codegen;
        try {
            codegen = factory.createCodeGenerator(printResults, constNames,
                    this, scope, flags);
            for (stmt statement : body) {
                statement.accept(this);
            }
        } finally {
            codegen = old;
        }
    }

    // Scope defining elements

    @Override
    public E visitClassDef(ClassDef node) throws Exception {
        S scope = scopes.get(node);
        H state = codegen.beforeClassDef(node, scope);
        IntermediateCodeGenerator<E, S, H> old = codegen;
        E result;
        try {
            codegen = factory.createCodeGenerator(false, constNames, this,
                    scope, flags);
            result = codegen.visitClassDef(node);
        } finally {
            codegen = old;
        }
        codegen.afterClassDef(node, state, result);
        return result;
    }

    @Override
    public E visitFunctionDef(FunctionDef node) throws Exception {
        S scope = scopes.get(node);
        H state = codegen.beforeFunctionDef(node, scope);
        IntermediateCodeGenerator<E, S, H> old = codegen;
        E result;
        try {
            codegen = factory.createCodeGenerator(false, constNames, this,
                    scope, flags);
            result = codegen.visitFunctionDef(node);
        } finally {
            codegen = old;
        }
        codegen.afterFunctionDef(node, state, result);
        return result;
    }

    @Override
    public E visitLambda(Lambda node) throws Exception {
        S scope = scopes.get(node);
        H state = codegen.beforeLambda(node, scope);
        IntermediateCodeGenerator<E, S, H> old = codegen;
        E result;
        try {
            codegen = factory.createCodeGenerator(false, constNames, this,
                    scope, flags);
            result = codegen.visitLambda(node);
        } finally {
            codegen = old;
        }
        return codegen.afterLambda(node, state, result);
    }

    @Override
    public E visitGeneratorExp(GeneratorExp node) throws Exception {
        S scope = scopes.get(node);
        H state = codegen.beforeGeneratorExp(node, scope);
        IntermediateCodeGenerator<E, S, H> old = codegen;
        E result;
        try {
            codegen = factory.createCodeGenerator(false, constNames, this,
                    scope, flags);
            result = codegen.visitGeneratorExp(node);
        } finally {
            codegen = old;
        }
        return codegen.afterGeneratorExp(node, state, result);
    }
}