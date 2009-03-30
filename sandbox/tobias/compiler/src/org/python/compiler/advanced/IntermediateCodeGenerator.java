package org.python.compiler.advanced;

import java.util.Collection;
import java.util.List;

import org.python.antlr.ParseException;
import org.python.antlr.PythonTree;
import org.python.antlr.ast.ClassDef;
import org.python.antlr.ast.Ellipsis;
import org.python.antlr.ast.Expression;
import org.python.antlr.ast.ExtSlice;
import org.python.antlr.ast.FunctionDef;
import org.python.antlr.ast.GeneratorExp;
import org.python.antlr.ast.Index;
import org.python.antlr.ast.Interactive;
import org.python.antlr.ast.Lambda;
import org.python.antlr.ast.Module;
import org.python.antlr.ast.Num;
import org.python.antlr.ast.Pass;
import org.python.antlr.ast.Slice;
import org.python.antlr.ast.Str;
import org.python.antlr.ast.Suite;
import org.python.antlr.ast.VisitorBase;
import org.python.antlr.ast.VisitorIF;
import org.python.antlr.base.expr;
import org.python.antlr.base.slice;
import org.python.antlr.base.stmt;

public abstract class IntermediateCodeGenerator<RESULT, SCOPE, CARRIER> extends
        VisitorBase<RESULT> implements AssignmentGenerator<RESULT>, ConstantOwner<RESULT> {

    private final VisitorIF<RESULT> compiler;
    private final ConstantPool constants;

    protected IntermediateCodeGenerator(VisitorIF<RESULT> compiler,
            ConstantPool constants) {
        this.compiler = compiler;
        this.constants = constants;
    }

    public abstract CARRIER beforeClassDef(ClassDef node, SCOPE scope)
            throws Exception;

    public abstract void afterClassDef(ClassDef node, CARRIER data,
            RESULT result) throws Exception;

    public abstract CARRIER beforeFunctionDef(FunctionDef node, SCOPE scope)
            throws Exception;

    public abstract void afterFunctionDef(FunctionDef node, CARRIER data,
            RESULT result) throws Exception;

    public abstract CARRIER beforeLambda(Lambda node, SCOPE scope)
            throws Exception;

    public abstract RESULT afterLambda(Lambda node, CARRIER data, RESULT result)
            throws Exception;

    public abstract CARRIER beforeGeneratorExp(GeneratorExp node, SCOPE scope)
            throws Exception;

    public abstract RESULT afterGeneratorExp(GeneratorExp node, CARRIER data,
            RESULT result) throws Exception;

    // Implementation

    protected abstract RESULT[] resultArray(int size);

    protected final void compile(List<stmt> body) throws Exception {
        for (stmt statement : body) {
            statement.accept(compiler);
        }
    }

    protected final RESULT compile(expr body) throws Exception {
        return body.accept(compiler);
    }

    public final RESULT evaluateOrNull(PythonTree expression) throws Exception {
        return expression == null ? null : evaluate(expression);
    }

    public final RESULT evaluate(PythonTree expression) throws Exception {
        RESULT result = constant(expression);
        if (result == null) {
            result = expression.accept(this);
        }
        return result;
    }

    private RESULT constant(PythonTree expression) throws Exception {
        Constant constant = constants.getConstant(expression);
        if (constant == null) return null;
        return constant.loadFrom(this);
    }

    protected RESULT[] evaluate(Collection<? extends PythonTree> expressions)
            throws Exception {
        if (expressions == null) {
            return resultArray(0);
        }
        RESULT[] result = resultArray(expressions.size());
        int i = 0;
        for (PythonTree expression : expressions) {
            result[i++] = evaluate(expression);
        }
        return result;
    }

    protected final void assign(expr target, RESULT value) throws Exception {
        target.accept(Assignment.standard(this, value));
    }

    protected final void augAssign(expr target,
            AugAssignCallback<RESULT> callback) throws Exception {
        target.accept(Assignment.augmented(this, callback));
    }

    protected final void delete(Iterable<expr> targets) throws Exception {
        VisitorIF<Void> delete = Assignment.deletion(this);
        for (expr target : targets) {
            target.accept(delete);
        }
    }

    protected final RESULT subscript(RESULT target, slice subscript)
            throws Exception {
        return subscript.accept(new SubscriptVisitor(target));
    }

    @Override
    public final void traverse(PythonTree node) throws Exception {
        // Delegate to the director
        node.traverse(compiler);
    }

    @Override
    protected final RESULT unhandled_node(PythonTree node) throws Exception {
        throw new ParseException("Cannot generate code for "
                + node.getClass().getName(), node);
    }

    @Override
    public final RESULT visitModule(Module node) throws Exception {
        return super.visitModule(node);
    }

    @Override
    public final RESULT visitInteractive(Interactive node) throws Exception {
        return super.visitInteractive(node);
    }

    @Override
    public final RESULT visitSuite(Suite node) throws Exception {
        throw new ParseException("Cannot compile Suite without context.", node);
    }

    @Override
    public final RESULT visitExpression(Expression node) throws Exception {
        return super.visitExpression(node);
    }

    @Override
    public RESULT visitPass(Pass node) throws Exception {
        return null; // Ignore by default
    }
    
    @Override
    public RESULT visitStr(Str node) throws Exception {
        return loadConstant(constants.getConstant(node));
    }
    
    @Override
    public RESULT visitNum(Num node) throws Exception {
        return loadConstant(constants.getConstant(node));
    }

    private class SubscriptVisitor extends VisitorBase<RESULT> {
        private final RESULT target;

        public SubscriptVisitor(RESULT target) {
            this.target = target;
        }

        @Override
        public RESULT visitEllipsis(Ellipsis node) throws Exception {
            return loadEllipsis(target);
        }

        @Override
        public RESULT visitSlice(Slice node) throws Exception {
            RESULT lower = evaluate(node.getInternalLower());
            RESULT step = evaluate(node.getInternalStep());
            RESULT upper = evaluate(node.getInternalUpper());
            return loadSlice(target, lower, step, upper);
        }

        @Override
        public RESULT visitIndex(Index node) throws Exception {
            return loadSubscript(target, evaluate(node.getInternalValue()));
        }

        @Override
        public RESULT visitExtSlice(ExtSlice node) throws Exception {
            return loadSubscript(target,
                    buildExtendedSlice(node.getInternalDims()));
        }

        @Override
        public final void traverse(PythonTree node) throws Exception {
        }

        @Override
        protected final RESULT unhandled_node(PythonTree node) throws Exception {
            throw new ParseException("Illegal subscript node: "
                    + node.getClass(), node);
        }
    }
}
