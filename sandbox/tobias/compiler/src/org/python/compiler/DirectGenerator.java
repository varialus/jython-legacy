package org.python.compiler;

import org.python.antlr.ast.Assert;
import org.python.antlr.ast.Assign;
import org.python.antlr.ast.Attribute;
import org.python.antlr.ast.AugAssign;
import org.python.antlr.ast.BinOp;
import org.python.antlr.ast.BoolOp;
import org.python.antlr.ast.Break;
import org.python.antlr.ast.Call;
import org.python.antlr.ast.ClassDef;
import org.python.antlr.ast.Compare;
import org.python.antlr.ast.Continue;
import org.python.antlr.ast.Delete;
import org.python.antlr.ast.Dict;
import org.python.antlr.ast.Ellipsis;
import org.python.antlr.ast.ExceptHandler;
import org.python.antlr.ast.Exec;
import org.python.antlr.ast.Expr;
import org.python.antlr.ast.Expression;
import org.python.antlr.ast.ExtSlice;
import org.python.antlr.ast.For;
import org.python.antlr.ast.FunctionDef;
import org.python.antlr.ast.GeneratorExp;
import org.python.antlr.ast.Global;
import org.python.antlr.ast.If;
import org.python.antlr.ast.IfExp;
import org.python.antlr.ast.Import;
import org.python.antlr.ast.ImportFrom;
import org.python.antlr.ast.Index;
import org.python.antlr.ast.Interactive;
import org.python.antlr.ast.Lambda;
import org.python.antlr.ast.List;
import org.python.antlr.ast.ListComp;
import org.python.antlr.ast.Module;
import org.python.antlr.ast.Name;
import org.python.antlr.ast.Num;
import org.python.antlr.ast.Pass;
import org.python.antlr.ast.Print;
import org.python.antlr.ast.Raise;
import org.python.antlr.ast.Repr;
import org.python.antlr.ast.Return;
import org.python.antlr.ast.Slice;
import org.python.antlr.ast.Str;
import org.python.antlr.ast.Subscript;
import org.python.antlr.ast.Suite;
import org.python.antlr.ast.TryExcept;
import org.python.antlr.ast.TryFinally;
import org.python.antlr.ast.Tuple;
import org.python.antlr.ast.UnaryOp;
import org.python.antlr.ast.While;
import org.python.antlr.ast.With;
import org.python.antlr.ast.Yield;

//TODO: Implement this if it proves to be too slow to use the flow graph
public class DirectGenerator implements
		IntermediateCodeGenerator<Object, ScopeInformation, Void> {

    public void afterClassDef(ClassDef node, Void data, Object result)
            throws Exception {
        // TODO Auto-generated method stub
        
    }

    public void afterFunctionDef(FunctionDef node, Void data, Object result)
            throws Exception {
        // TODO Auto-generated method stub
        
    }

    public Object afterGeneratorExp(GeneratorExp node, Void data, Object result)
            throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Object afterLambda(Lambda node, Void data, Object result)
            throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Void beforeClassDef(ClassDef node, ScopeInformation scope)
            throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Void beforeFunctionDef(FunctionDef node, ScopeInformation scope)
            throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Void beforeGeneratorExp(GeneratorExp node, ScopeInformation scope)
            throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Void beforeLambda(Lambda node, ScopeInformation scope)
            throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Object visitAssert(Assert node) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Object visitAssign(Assign node) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Object visitAttribute(Attribute node) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Object visitAugAssign(AugAssign node) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Object visitBinOp(BinOp node) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Object visitBoolOp(BoolOp node) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Object visitBreak(Break node) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Object visitCall(Call node) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Object visitClassDef(ClassDef node) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Object visitCompare(Compare node) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Object visitContinue(Continue node) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Object visitDelete(Delete node) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Object visitDict(Dict node) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Object visitEllipsis(Ellipsis node) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Object visitExceptHandler(ExceptHandler node) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Object visitExec(Exec node) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Object visitExpr(Expr node) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Object visitExpression(Expression node) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Object visitExtSlice(ExtSlice node) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Object visitFor(For node) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Object visitFunctionDef(FunctionDef node) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Object visitGeneratorExp(GeneratorExp node) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Object visitGlobal(Global node) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Object visitIf(If node) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Object visitIfExp(IfExp node) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Object visitImport(Import node) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Object visitImportFrom(ImportFrom node) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Object visitIndex(Index node) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Object visitInteractive(Interactive node) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Object visitLambda(Lambda node) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Object visitList(List node) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Object visitListComp(ListComp node) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Object visitModule(Module node) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Object visitName(Name node) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Object visitNum(Num node) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Object visitPass(Pass node) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Object visitPrint(Print node) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Object visitRaise(Raise node) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Object visitRepr(Repr node) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Object visitReturn(Return node) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Object visitSlice(Slice node) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Object visitStr(Str node) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Object visitSubscript(Subscript node) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Object visitSuite(Suite node) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Object visitTryExcept(TryExcept node) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Object visitTryFinally(TryFinally node) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Object visitTuple(Tuple node) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Object visitUnaryOp(UnaryOp node) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Object visitWhile(While node) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Object visitWith(With node) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Object visitYield(Yield node) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

}
