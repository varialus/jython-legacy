package org.python.compiler.advanced;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.python.antlr.ParseException;
import org.python.antlr.PythonTree;
import org.python.antlr.ast.Assign;
import org.python.antlr.ast.ClassDef;
import org.python.antlr.ast.Dict;
import org.python.antlr.ast.FunctionDef;
import org.python.antlr.ast.GeneratorExp;
import org.python.antlr.ast.Global;
import org.python.antlr.ast.Import;
import org.python.antlr.ast.ImportFrom;
import org.python.antlr.ast.Lambda;
import org.python.antlr.ast.List;
import org.python.antlr.ast.Name;
import org.python.antlr.ast.Num;
import org.python.antlr.ast.Str;
import org.python.antlr.ast.Tuple;
import org.python.antlr.ast.VisitorBase;
import org.python.antlr.ast.Yield;
import org.python.antlr.ast.alias;
import org.python.antlr.ast.arguments;
import org.python.antlr.ast.expr_contextType;
import org.python.antlr.base.expr;
import org.python.antlr.base.stmt;

/**
 * Builds a tree of scope representations and validates the structure of the
 * syntax tree.
 * 
 * @author Tobias Ivarsson
 * @param <E>
 */
final class ScopesBuilder extends VisitorBase<Void> {
    static <T> T scan(SyntaxErrorPolicy errorPolicy, PragmaParser pragmas,
            ConstantPool constants, Map<PythonTree, T> scopes,
            ScopeFactory<T> factory, java.util.List<? extends stmt> body)
            throws Exception {
        ScopeBuilder<T> base = ScopeBuilder.create(factory, scopes);
        ScopesBuilder builder = new ScopesBuilder(errorPolicy, constants,
                pragmas);
        builder.push(base);
        try {
            for (PythonTree node : body) {
                node.accept(builder);
            }
        } finally {
            builder.pop();
        }
        return base.complete();
    }

    private final Stack<ScopeBuilder<?>> stack = new Stack<ScopeBuilder<?>>();
    private final SyntaxErrorPolicy errorPolicy;
    private final ConstantPool constants;
    private final PragmaParser pragmas;

    private ScopesBuilder(SyntaxErrorPolicy errorPolicy,
            ConstantPool constants, PragmaParser pragmas) {
        this.errorPolicy = (errorPolicy != null) ? errorPolicy
                : SyntaxErrorPolicy.RAISE_EXCEPTION_ON_FIRST_ERROR;
        this.constants = constants;
        this.pragmas = pragmas;
    }

    private void push(ScopeBuilder<?> scope) {
        stack.push(scope);
    }

    private void pop() {
        stack.pop().complete();
    }

    private ScopeBuilder<?> current() {
        return stack.peek();
    }

    private void defParamIfNotNull(PythonTree args, String id) {
        if (id != null) {
            try {
                current().addParam(id);
            } catch (ParseException pe) {
                errorPolicy.syntaxError(pe, args);
            }
        }
    }

    @Override
    public void traverse(PythonTree node) throws Exception {
        node.traverse(this);
    }

    @Override
    protected Void unhandled_node(PythonTree node) throws Exception {
        return null;
    }

    @Override
    public Void visitClassDef(ClassDef node) throws Exception {
        if (constants.isConstant(node.getInternalName())) {
            errorPolicy.syntaxError("Cannot define class "
                    + node.getInternalName() + ", " + node.getInternalName()
                    + " is a constant.", node);
        }
        try {
            current().def(node.getInternalName());
        } catch (ParseException pe) {
            errorPolicy.syntaxError(pe, node);
        }
        for (expr expression : node.getInternalBases()) {
            expression.accept(this);
        }
        push(current().createClassScope(node.getInternalName(), node));
        try {
            for (stmt statement : node.getInternalBody()) {
                statement.accept(this);
            }
        } finally {
            pop();
        }
        return null;
    }

    private void function(PythonTree node, String name, arguments args,
            java.util.List<? extends PythonTree> body) throws Exception {
        for (expr expression : args.getInternalDefaults()) {
            expression.accept(this);
        }
        push(current().createFunctionScope(name, node));
        try {
            defParamIfNotNull(args, args.getInternalVararg());
            defParamIfNotNull(args, args.getInternalKwarg());
            for (expr arg : args.getInternalArgs()) {
                try {
                    arg.accept(this);
                } catch (ParseException pe) {
                    errorPolicy.syntaxError(pe, arg);
                }
            }
            for (PythonTree stmt : body) {
                stmt.accept(this);
            }
        } finally {
            pop();
        }
    }

    @Override
    public Void visitFunctionDef(FunctionDef node) throws Exception {
        if (constants.isConstant(node.getInternalName())) {
            errorPolicy.syntaxError("Cannot define function "
                    + node.getInternalName() + ", " + node.getInternalName()
                    + " is a constant.", node);
        }
        try {
            current().def(node.getInternalName());
        } catch (ParseException pe) {
            errorPolicy.syntaxError(pe, node);
        }
        for (expr expression : node.getInternalDecorator_list()) {
            expression.accept(this);
        }
        function(node, node.getInternalName(), node.getInternalArgs(),
                node.getInternalBody());
        return null;
    }

    @Override
    public Void visitLambda(Lambda node) throws Exception {
        function(node, "<lambda>", node.getInternalArgs(),
                Arrays.asList(node.getInternalBody()));
        return null;
    }

    @Override
    public Void visitGeneratorExp(GeneratorExp node) throws Exception {
        node.getInternalGenerators().get(0).getInternalIter().accept(this);
        push(current().createGeneratorExpressionScope(node));
        try {
            node.getInternalGenerators().get(0).getInternalTarget().accept(this);
            for (expr expression : node.getInternalGenerators().get(0).getInternalIfs()) {
                expression.accept(this);
            }
            for (int i = 1; i < node.getInternalGenerators().size(); i++) {
                // TODO?
            }
        } finally {
            pop();
        }
        return null;
    }

    @Override
    public Void visitGlobal(Global node) throws Exception {
        for (String id : node.getInternalNames()) {
            try {
                current().addGlobal(id);
            } catch (ParseException pe) {
                errorPolicy.syntaxError(pe, node);
            }
        }
        return null;
    }

    @Override
    public Void visitName(Name node) throws Exception {
        boolean is_constant = constants.isConstant(node.getInternalId());
        try {
            switch (node.getInternalCtx()) {
            case AugLoad:
            case AugStore:
                throw new ParseException("The name context "
                        + node.getInternalCtx() + " is not supported!");
            case Del:
                if (is_constant) {
                    errorPolicy.syntaxError("Cannot delete "
                            + node.getInternalId() + ", it is a constant.",
                            node);
                } else {
                    current().del(node.getInternalId());
                }
                break;
            case Load:
                current().use(node.getInternalId());
                break;
            case Param:
                current().addParam(node.getInternalId());
                break;
            case Store:
                current().def(node.getInternalId());
                break;
            default:
                throw new ParseException("Unknown name context: "
                        + node.getInternalCtx());
            }
        } catch (ParseException pe) {
            errorPolicy.syntaxError(pe, node);
        }
        return null;
    }

    @Override
    public Void visitImport(Import node) throws Exception {
        for (alias al : node.getInternalNames()) {
            String name;
            if (al.getInternalAsname() != null) {
                name = al.getInternalAsname();
            } else if (al.getInternalName().contains(".")) {
                name = al.getInternalName().substring(
                        al.getInternalName().indexOf("."));
            } else {
                name = al.getInternalName();
            }
            if (constants.isConstant(name)) {
                errorPolicy.syntaxError("Cannot import " + al.getInternalName()
                        + " as " + name + ", " + name + " is a constant.", al);
            } else {
                try {
                    current().def(name);
                } catch (ParseException pe) {
                    errorPolicy.syntaxError(pe, al);
                }
            }
        }
        return null;
    }

    @Override
    public Void visitImportFrom(ImportFrom node) throws Exception {
        try {
            pragmas.checkPragma(node);
        } catch (ParseException pe) {
            errorPolicy.error(pe);
        }
        if (node.getInternalNames() == null
                || node.getInternalNames().isEmpty()
                || (node.getInternalNames().size() == 1 && "*".equals(node.getInternalNames().get(
                        0).getInternalName()))) {
            // import *
            current().setHasStarImport();
        } else {
            for (alias al : node.getInternalNames()) {
                String name = al.getInternalAsname() != null ? al.getInternalAsname()
                        : al.getInternalName();
                if (constants.isConstant(name)) {
                    errorPolicy.syntaxError("Cannot import "
                            + al.getInternalName() + " as " + name + ", "
                            + name + " is a constant.", al);
                } else {
                    try {
                        current().def(name);
                    } catch (ParseException pe) {
                        errorPolicy.syntaxError(pe, al);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Void visitYield(Yield node) throws Exception {
        current().setIsGenerator();
        return null;
    }

    @Override
    public Void visitNum(Num node) throws Exception {
        constants.createConstant(node);
        return null;
    }

    @Override
    public Void visitStr(Str node) throws Exception {
        constants.createConstant(node);
        return null;
    }

    @Override
    public Void visitTuple(Tuple node) throws Exception {
        expr_contextType ctx = node.getInternalCtx();
        if (ctx == expr_contextType.Load || ctx == expr_contextType.AugLoad) {
            constants.createConstant(node);
        }
        return null;
    }

    @Override
    public Void visitAssign(Assign node) throws Exception {
        for (expr target : node.getInternalTargets()) {
            checkAssign(target, node.getInternalValue());
        }
        return super.visitAssign(node);
    }

    private void checkAssign(expr target, expr value) {
        if (target instanceof Name) {
            Name name = (Name) target;
            if (constants.isConstantWithUnacceptedAssignment(
                    name.getInternalId(), value)) {
                errorPolicy.syntaxError("Cannot assign to "
                        + name.getInternalId() + ", " + name.getInternalId()
                        + " is a constant.", target);
            }
        } else if (target instanceof Tuple) {
            checkAll(((Tuple) target).getInternalElts(), value);
        } else if (target instanceof List) {
            checkAll(((List) target).getInternalElts(), value);
        } else if (target instanceof Dict) {
            checkDict((Dict) target, value);
        }
    }

    private void checkAll(java.util.List<expr> targets, expr value) {
        java.util.List<expr> values = Collections.emptyList();
        if (value instanceof Tuple) {
            values = ((Tuple) value).getInternalElts();
        } else if (value instanceof List) {
            values = ((List) value).getInternalElts();
        }
        int i = 0;
        for (; i < targets.size() && i < values.size(); i++) {
            checkAssign(targets.get(i), values.get(i));
        }
        for (; i < targets.size(); i++) {
            checkAssign(targets.get(i), null);
        }
    }

    // For when/if dictionary assignment is made available
    private void checkDict(Dict dict, expr value) {
        if (value instanceof Dict) {
            Dict valDict = (Dict) value;
            Map<String, expr> knownValues = new HashMap<String, expr>();
            for (int i = 0; i < valDict.getInternalKeys().size(); i++) {
                if (valDict.getInternalKeys().get(i) instanceof Str) {
                    knownValues.put(
                            ((Str) valDict.getInternalKeys().get(i)).getInternalS().toString(),
                            valDict.getInternalValues().get(i));
                }
            }
            for (int i = 0; i < dict.getInternalKeys().size(); i++) {
                if (dict.getInternalKeys().get(i) instanceof Str) {
                    checkAssign(
                            dict.getInternalValues().get(i),
                            knownValues.get(((Str) dict.getInternalKeys().get(i)).toString()));
                } else {
                    checkAssign(dict.getInternalValues().get(i), null);
                }
            }
        } else {
            checkAll(dict.getInternalValues(), null);
        }
    }
}
