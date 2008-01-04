tree grammar PythonWalker;

options {
    tokenVocab=Python;
    ASTLabelType=PythonTree;
}

@header { 
package org.python.antlr;

import org.python.core.Py;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.antlr.ast.aliasType;
import org.python.antlr.ast.argumentsType;
import org.python.antlr.ast.boolopType;
import org.python.antlr.ast.comprehensionType;
import org.python.antlr.ast.cmpopType;
import org.python.antlr.ast.excepthandlerType;
import org.python.antlr.ast.exprType;
import org.python.antlr.ast.expr_contextType;
import org.python.antlr.ast.keywordType;
import org.python.antlr.ast.modType;
import org.python.antlr.ast.operatorType;
import org.python.antlr.ast.sliceType;
import org.python.antlr.ast.stmtType;
import org.python.antlr.ast.unaryopType;
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
import org.python.antlr.ast.Exec;
import org.python.antlr.ast.Expr;
import org.python.antlr.ast.For;
import org.python.antlr.ast.FunctionDef;
import org.python.antlr.ast.Global;
import org.python.antlr.ast.If;
import org.python.antlr.ast.Index;
import org.python.antlr.ast.Import;
import org.python.antlr.ast.ImportFrom;
import org.python.antlr.ast.Lambda;
import org.python.antlr.ast.ListComp;
import org.python.antlr.ast.Module;
import org.python.antlr.ast.Name;
import org.python.antlr.ast.Num;
import org.python.antlr.ast.Slice;
import org.python.antlr.ast.Subscript;
import org.python.antlr.ast.TryExcept;
import org.python.antlr.ast.TryFinally;
import org.python.antlr.ast.Tuple;
import org.python.antlr.ast.Pass;
import org.python.antlr.ast.Print;
import org.python.antlr.ast.Raise;
import org.python.antlr.ast.Return;
import org.python.antlr.ast.Str;
import org.python.antlr.ast.UnaryOp;
import org.python.antlr.ast.While;
import org.python.antlr.ast.Yield;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
} 
@members {
    boolean debugOn = false;

    public void debug(String message) {
        if (debugOn) {
            System.out.println(message);
        }
    }

    String name = "Test";

    //XXX: Not sure I need any below...
    String filename = "test.py";
    boolean linenumbers = true;
    boolean setFile = true;
    boolean printResults = false;
    //CompilerFlags cflags = Py.getCompilerFlags();

    private modType makeMod(PythonTree t, List stmts) {
        stmtType[] s;
        if (stmts != null) {
            s = (stmtType[])stmts.toArray(new stmtType[stmts.size()]);
        } else {
            s = new stmtType[0];
        }
        return new Module(t, s);
    }

    private ClassDef makeClassDef(PythonTree t, PythonTree nameToken, List bases, List body) {
        exprType[] b = (exprType[])bases.toArray(new exprType[bases.size()]);
        stmtType[] s = (stmtType[])body.toArray(new stmtType[body.size()]);
        return new ClassDef(t, nameToken.getText(), b, s);
    }

    private FunctionDef makeFunctionDef(PythonTree t, PythonTree nameToken, argumentsType args, List funcStatements, List decorators) {
        argumentsType a;
        debug("Matched FunctionDef");
        if (args != null) {
            a = args;
        } else {
            a = new argumentsType(t, new exprType[0], null, null, new exprType[0]); 
        }
        stmtType[] s = (stmtType[])funcStatements.toArray(new stmtType[funcStatements.size()]);
        exprType[] d;
        if (decorators != null) {
            d = (exprType[])decorators.toArray(new exprType[decorators.size()]);
        } else {
            d = new exprType[0];
        }
        return new FunctionDef(t, nameToken.getText(), a, s, d);
    }

    private argumentsType makeArgumentsType(PythonTree t, List params, PythonTree snameToken,
        PythonTree knameToken, List defaults) {
        debug("Matched Arguments");

        exprType[] p = (exprType[])params.toArray(new exprType[params.size()]);
        exprType[] d = (exprType[])defaults.toArray(new exprType[defaults.size()]);
        String s;
        String k;
        if (snameToken == null) {
            s = null;
        } else {
            s = snameToken.getText();
        }
        if (knameToken == null) {
            k = null;
        } else {
            k = knameToken.getText();
        }
        return new argumentsType(t, p, s, k, d);
    }

    String extractStrings(List s) {
        StringBuffer sb = new StringBuffer();
        Iterator iter = s.iterator();
        while (iter.hasNext()) {
            sb.append(extractString((String)iter.next()));
        }
        return sb.toString();
    }

    String extractString(String s) {
        char quoteChar = s.charAt(0);
        int start=0;
        boolean ustring = false;
        if (quoteChar == 'u' || quoteChar == 'U') {
            ustring = true;
            start++;
        }
        quoteChar = s.charAt(start);
        boolean raw = false;
        if (quoteChar == 'r' || quoteChar == 'R') {
            raw = true;
            start++;
        }
        int quotes = 3;
        if (s.length() - start == 2) {
            quotes = 1;
        }
        if (s.charAt(start) != s.charAt(start+1)) {
            quotes = 1;
        }

        if (raw) {
            return s.substring(quotes+start, s.length()-quotes);
        } else {
            StringBuffer sb = new StringBuffer(s.length());
            char[] ca = s.toCharArray();
            int n = ca.length-quotes;
            int i=quotes+start;
            int last_i=i;
            return PyString.decode_UnicodeEscape(s, i, n, "strict", ustring);
            //return decode_UnicodeEscape(s, i, n, "strict", ustring);
        }
    }

    Num makeFloat(PythonTree t) {
        debug("makeFloat matched " + t.getText());
        return new Num(t, Py.newFloat(Double.valueOf(t.getText())));
    }

    Num makeComplex(PythonTree t) {
        String s = t.getText();
        s = s.substring(0, s.length() - 1);
        return new Num(t, Py.newImaginary(Double.valueOf(s)));
    }

    Num makeInt(PythonTree t) {
        debug("Num matched " + t.getText());
        String s = t.getText();
        int radix = 10;
        if (s.startsWith("0x") || s.startsWith("0X")) {
            radix = 16;
            s = s.substring(2, s.length());
        } else if (s.startsWith("0")) {
            radix = 8;
        }
        if (s.endsWith("L") || s.endsWith("l")) {
            s = s.substring(0, s.length()-1);
            return new Num(t, Py.newLong(new BigInteger(s, radix)));
        }
        int ndigits = s.length();
        int i=0;
        while (i < ndigits && s.charAt(i) == '0')
            i++;
        if ((ndigits - i) > 11) {
            return new Num(t, Py.newLong(new BigInteger(s, radix)));
        }

        long l = Long.valueOf(s, radix).longValue();
        if (l > 0xffffffffl || (radix == 10 && l > Integer.MAX_VALUE)) {
            return new Num(t, Py.newLong(new BigInteger(s, radix)));
        }
        return new Num(t, Py.newInteger((int) l));
    }

    private stmtType makeTryExcept(PythonTree t, List body, List handlers, List orelse, List finBody) {
        stmtType[] b = (stmtType[])body.toArray(new stmtType[body.size()]);
        excepthandlerType[] e = (excepthandlerType[])handlers.toArray(new excepthandlerType[handlers.size()]);
        stmtType[] o;
        if (orelse != null) {
            o = (stmtType[])orelse.toArray(new stmtType[orelse.size()]);
        } else {
            o = new stmtType[0];
        }
 
        stmtType te = new TryExcept(t, b, e, o);
        if (finBody == null) {
            return te;
        }
        stmtType[] f = (stmtType[])finBody.toArray(new stmtType[finBody.size()]);
        stmtType[] mainBody = new stmtType[]{te};
        return new TryFinally(t, mainBody, f);
    }

    private TryFinally makeTryFinally(PythonTree t,  List body, List finBody) {
        stmtType[] b = (stmtType[])body.toArray(new stmtType[body.size()]);
        stmtType[] f = (stmtType[])finBody.toArray(new stmtType[finBody.size()]);
        return new TryFinally(t, b, f);
    }

    private If makeIf(PythonTree t, exprType test, List body, List orelse) {
        stmtType[] o;
        if (orelse != null) {
            o = (stmtType[])orelse.toArray(new stmtType[orelse.size()]);
        } else {
            o = new stmtType[0];
        }
        stmtType[] b;
        if (body != null) {
            b = (stmtType[])body.toArray(new stmtType[body.size()]);
        } else {
            b = new stmtType[0];
        }
        return new If(t, test, b, o);
    }


    private While makeWhile(PythonTree t, exprType test, List body, List orelse) {
        stmtType[] o;
        if (orelse != null) {
            o = (stmtType[])orelse.toArray(new stmtType[orelse.size()]);
        } else {
            o = new stmtType[0];
        }
        stmtType[] b = (stmtType[])body.toArray(new stmtType[body.size()]);
        return new While(t, test, b, o);
    }

    private For makeFor(PythonTree t, exprType target, exprType iter, List body, List orelse) {
        stmtType[] o;
        if (orelse != null) {
            o = (stmtType[])orelse.toArray(new stmtType[orelse.size()]);
        } else {
            o = new stmtType[0];
        }
        stmtType[] b = (stmtType[])body.toArray(new stmtType[body.size()]);
        return new For(t, target, iter, b, o);
    }
    
    //FIXME: just calling __neg__ for now - can be more efficient.
    private exprType negate(PythonTree t, exprType o) {
        if (o instanceof Num) {
            Num num = (Num)o;
            if (num.n instanceof PyObject) {
                num.n = ((PyObject)num.n).__neg__();
            }
            return num;
        }
        return new UnaryOp(t, unaryopType.USub, o);
    }
}

module returns [modType mod]
    : ^(Module
        ( stmts {$mod = makeMod($Module, $stmts.stypes); }
        | {$mod = makeMod($Module, null);}
        )
    )
    ;

funcdef
    : ^(FunctionDef ^(Name NAME) ^(Arguments varargslist?) ^(Body stmts) ^(Decorators decorators?)) {
        $stmts::statements.add(makeFunctionDef($FunctionDef, $NAME, $varargslist.args, $stmts.stypes, $decorators.etypes));
    }
    ;

varargslist returns [argumentsType args]
@init {
    List params = new ArrayList();
    List defaults = new ArrayList();
}
    : ^(Args defparameter[params, defaults]+) (^(StarArgs sname=NAME))? (^(KWArgs kname=NAME))? {
        $args = makeArgumentsType($Args, params, $sname, $kname, defaults);
    }
    | ^(StarArgs sname=NAME) (^(KWArgs kname=NAME))? {
        $args = makeArgumentsType($StarArgs,params, $sname, $kname, defaults);
    }
    | ^(KWArgs NAME) {
        $args = makeArgumentsType($KWArgs, params, null, $NAME, defaults);
    }
    ;

defparameter[List params, List defaults]
@init {
    List nms = new ArrayList();
}
    : NAME (ASSIGN test[expr_contextType.Load] )? {
        params.add(new Name($NAME, $NAME.text, expr_contextType.Param));
        if ($ASSIGN != null) {
            defaults.add($test.etype);
        }
    }
    | ^(FpList fplist_name[nms]+) {
        Name[] n = (Name[])nms.toArray(new Name[nms.size()]);
        params.add(new Tuple($fplist_name.start, n, expr_contextType.Store));
    }
    ;

fplist_name[List nms]
    : NAME {
        nms.add(new Name($NAME, $NAME.text, expr_contextType.Store));
    }
    ;


decorators returns [List etypes]
@init {
    List decs = new ArrayList();
}
    : decorator[decs]+ {
        $etypes = decs;
    }
    ;

decorator [List decs]
    : ^(Decorator dotted_attr (^(Call arglist))?) {
        if ($Call == null) {
            decs.add($dotted_attr.etype);
        } else {
            exprType[] args = (exprType[])$arglist.args.toArray(new exprType[$arglist.args.size()]);
            keywordType[] keywords = (keywordType[])$arglist.keywords.toArray(new keywordType[$arglist.keywords.size()]);
            Call c = new Call($Call, $dotted_attr.etype, args, keywords, $arglist.starargs, $arglist.kwargs);
            decs.add(c);
        }
    }
    ;

dotted_attr returns [exprType etype]
    : NAME {$etype = new Name($NAME, $NAME.text, expr_contextType.Load); debug("matched NAME in dotted_attr");}
    | ^(DOT n1=dotted_attr n2=dotted_attr) {
        $etype = new Attribute($DOT, $n1.etype, $n2.text, expr_contextType.Load);
    }
    ;

stmts returns [List stypes]
scope {
    List statements;
}
@init {
    $stmts::statements = new ArrayList();
}
    : stmt+ {
        debug("Matched stmts");
        $stypes = $stmts::statements;
    }
    | INDENT stmt+ DEDENT {
        debug("Matched stmts");
        $stypes = $stmts::statements;
    }
    ;

stmt //combines simple_stmt and compound_stmt from Python.g
    : expr_stmt
    | print_stmt
    | del_stmt
    | pass_stmt
    | flow_stmt
    | import_stmt
    | global_stmt
    | exec_stmt
    | assert_stmt
    | if_stmt
    | while_stmt
    | for_stmt
    | try_stmt
    | with_stmt
    | funcdef
    | classdef
    ;

expr_stmt
    : test[expr_contextType.Load] {
        debug("matched expr_stmt:test " + $test.etype);
        $stmts::statements.add(new Expr($test.start, $test.etype));
    }
    | ^(augassign targ=test[expr_contextType.Store] value=test[expr_contextType.Load]) {
        AugAssign a = new AugAssign($augassign.start, $targ.etype, $augassign.op, $value.etype);
        $stmts::statements.add(a);
    }
    | ^(Assign targets ^(Value value=test[expr_contextType.Load])) {
        debug("Matched Assign");
        exprType[] e = new exprType[$targets.etypes.size()];
        for(int i=0;i<$targets.etypes.size();i++) {
            e[i] = (exprType)$targets.etypes.get(i);
        }
        debug("exprs: " + e.length);
        Assign a = new Assign($Assign, e, $value.etype);
        $stmts::statements.add(a);
    }
    ;

call_expr returns [exprType etype]
    : ^(Call (^(Args arglist))? test[expr_contextType.Load]) {
        Call c;
        if ($Args == null) {
            c = new Call($Call, $test.etype, new exprType[0], new keywordType[0], null, null);
            debug("Matched Call site no args");
        } else {
            debug($arglist.text + "!!!!" + $test.text);
            debug("Matched Call w/ args");
            exprType[] args = (exprType[])$arglist.args.toArray(new exprType[$arglist.args.size()]);
            keywordType[] keywords = (keywordType[])$arglist.keywords.toArray(new keywordType[$arglist.keywords.size()]);
            c = new Call($Call, $test.etype, args, keywords, $arglist.starargs, $arglist.kwargs);
        }
        $etype = c;
    }
    ;

targets returns [List etypes]
@init {
    List targs = new ArrayList();
}
    : target[targs]+ {
        $etypes = targs;
    }
    ;

target[List etypes]
    : ^(Target test[expr_contextType.Store]) {
        etypes.add($test.etype);
    }
    ;

augassign returns [operatorType op]
    : PLUSEQUAL {$op = operatorType.Add;}
    | MINUSEQUAL {$op = operatorType.Sub;}
    | STAREQUAL {$op = operatorType.Mult;}
    | SLASHEQUAL {$op = operatorType.Div;}
    | PERCENTEQUAL {$op = operatorType.Mod;}
    | AMPEREQUAL {$op = operatorType.BitAnd;}
    | VBAREQUAL {$op = operatorType.BitOr;}
    | CIRCUMFLEXEQUAL {$op = operatorType.BitXor;}
    | LEFTSHIFTEQUAL {$op = operatorType.LShift;}
    | RIGHTSHIFTEQUAL {$op = operatorType.RShift;}
    | DOUBLESTAREQUAL {$op = operatorType.Pow;}
    | DOUBLESLASHEQUAL {$op = operatorType.FloorDiv;}
    ;

binop returns [operatorType op]
    : PLUS {$op = operatorType.Add;}
    | MINUS {$op = operatorType.Sub;}
    | STAR {$op = operatorType.Mult;}
    | SLASH {$op = operatorType.Div;}
    | PERCENT {$op = operatorType.Mod;}
    | AMPER {$op = operatorType.BitAnd;}
    | VBAR {$op = operatorType.BitOr;}
    | CIRCUMFLEX {$op = operatorType.BitXor;}
    | LEFTSHIFT {$op = operatorType.LShift;}
    | RIGHTSHIFT {$op = operatorType.RShift;}
    | DOUBLESTAR {$op = operatorType.Pow;}
    | DOUBLESLASH {$op = operatorType.FloorDiv;}
    ;


print_stmt
    : ^(Print (^(Dest RIGHTSHIFT))? (^(Values test[expr_contextType.Load]))? (Newline)?) {
        Print p;
        exprType[] values;

        exprType dest = null;
        boolean hasdest = false;

        boolean newline = false;
        if ($Newline != null) {
            newline = true;
        }

        if ($Dest != null) {
            hasdest = true;
        }
        if ($Values != null) {
            if ($test.etype instanceof Tuple) {
                Tuple t = (Tuple)$test.etype;
                if (hasdest) {
                    dest = t.elts[0];
                    values = new exprType[t.elts.length - 1];
                    System.arraycopy(t.elts, 1, values, 0, values.length);
                } else {
                    values = t.elts;
                }
            } else {
                values  = new exprType[]{$test.etype};
            }
        } else {
            values = new exprType[0];
        }
        p = new Print($Print, dest, values, newline);
        $stmts::statements.add(p);
    }
    ;

del_stmt
    : ^(Delete elts[expr_contextType.Del]) {
        exprType[] t = (exprType[])$elts.etypes.toArray(new exprType[$elts.etypes.size()]);
        $stmts::statements.add(new Delete($Delete, t));
    }
    ;

pass_stmt
    : Pass {
        debug("Matched Pass");
        $stmts::statements.add(new Pass($Pass));
    }
    ;

flow_stmt
    : break_stmt
    | continue_stmt
    | return_stmt
    | raise_stmt
    | yield_stmt
    ;

break_stmt
    : Break {
        $stmts::statements.add(new Break($Break));
    }
    ;

continue_stmt
    : Continue {
        $stmts::statements.add(new Continue($Continue));
    }
    ;

return_stmt
    : ^(Return (^(Value test[expr_contextType.Load]))?) {
        exprType v = null;
        if ($Value != null) {
            v = $test.etype;
        }
        $stmts::statements.add(new Return($Return, v));
    }
    ;

yield_stmt
    : ^(Yield (^(Value test[expr_contextType.Load]))?) {
        exprType v = null;
        if ($Value != null) {
            v = $test.etype; 
        }
        Yield y = new Yield($Yield, v);
        $stmts::statements.add(new Expr($Yield, y));
    }
    ;

raise_stmt
    : ^(Raise (^(Type type=test[expr_contextType.Load]))? (^(Inst inst=test[expr_contextType.Load]))? (^(Tback tback=test[expr_contextType.Load]))?) {
        exprType t = null;
        if ($Type != null) {
            t = $type.etype;
        }
        exprType i = null;
        if ($Inst != null) {
            i = $inst.etype;
        }
        exprType b = null;
        if ($Tback != null) {
            b = $tback.etype;
        }

        $stmts::statements.add(new Raise($Raise, t, i, b));
    }
    ;

import_stmt
@init {
    List nms = new ArrayList();
}
    : ^(Import dotted_as_name[nms]+) {
        aliasType[] n = (aliasType[])nms.toArray(new aliasType[nms.size()]);
        $stmts::statements.add(new Import($Import, n));
    }
    | ^(ImportFrom dotted_name ^(Import STAR)) {
        //XXX here
        aliasType[] n = (aliasType[])nms.toArray(new aliasType[nms.size()]);
        $stmts::statements.add(new ImportFrom($Import, $dotted_name.result, new aliasType[]{new aliasType($STAR, "*", null)}, 0));
    }
    | ^(ImportFrom dotted_name ^(Import import_as_name[nms]+)) {
        //XXX here
        aliasType[] n = (aliasType[])nms.toArray(new aliasType[nms.size()]);
        $stmts::statements.add(new ImportFrom($Import, $dotted_name.result, n, 0));
    }
    ;

import_as_name[List nms]
    : ^(Alias name=NAME (^(Asname asname=NAME))?) {
        String as = null;
        if ($Asname != null) {
            as = $asname.text;
        }
        aliasType a = new aliasType($Alias, $name.text, as);
        nms.add(a);
    }
    ;

dotted_as_name [List nms]
    : ^(Alias dotted_name (^(Asname NAME))?) {
        String as = null;
        if ($Asname != null) {
            as = $NAME.text;
        }
        aliasType a = new aliasType($Alias, $dotted_name.result, as);
        nms.add(a);
    }
    ;

dotted_name returns [String result]
@init {
    StringBuffer buf = new StringBuffer();
}
    : NAME dot_name[buf]* {
        $result = $NAME.text + buf.toString();
        debug("matched dotted_name " + $result);
    }
    ;

dot_name [StringBuffer buf]
    : DOT NAME {
        buf.append(".");
        buf.append($NAME.text);
        debug("matched dot_name " + buf);
    }
    ;

global_stmt
@init {
    List nms = new ArrayList();
}
    : ^(Global name_expr[nms]+) {
        String[] n = (String[])nms.toArray(new String[nms.size()]);
        $stmts::statements.add(new Global($Global, n));
    }
    ;

name_expr[List nms]
    : NAME {
        nms.add($NAME.text);
    }
    ;

exec_stmt
    : ^(Exec exec=test[expr_contextType.Load] (^(Globals globals=test[expr_contextType.Load]))? (^(Locals locals=test[expr_contextType.Load]))?) {
        exprType g = null;
        if ($Globals != null) {
            g = $globals.etype;
        }
        exprType loc = null;
        if ($Locals != null) {
            loc = $locals.etype;
        }
        $stmts::statements.add(new Exec($Exec, $exec.etype, g, loc));
    }
    ;

assert_stmt
    : ^(Assert ^(Test tst=test[expr_contextType.Load]) (^(Msg msg=test[expr_contextType.Load]))?) {
        exprType m = null;
        if ($Msg != null) {
            m = $msg.etype;
        }
        $stmts::statements.add(new Assert($Assert, $tst.etype, m));
    }
    ;

if_stmt
@init {
    List elifs = new ArrayList();
}

    : ^(If test[expr_contextType.Load] body=stmts elif_clause[elifs]* (^(OrElse orelse=stmts))?) {
        stmtType[] o;
        if ($OrElse != null) {
            o = (stmtType[])$orelse.stypes.toArray(new stmtType[$orelse.stypes.size()]);
        } else {
            o = new stmtType[0];
        }
        stmtType[] b = (stmtType[])$body.stypes.toArray(new stmtType[$body.stypes.size()]);
        ListIterator iter = elifs.listIterator(elifs.size());
        while (iter.hasPrevious()) {
            If elif = (If)iter.previous();
            elif.orelse = o;
            o = new stmtType[]{elif};
        }
        If i = new If($If, $test.etype, b, o);
        $stmts::statements.add(i);
    }
    ;

elif_clause[List elifs]
    : ^(Elif test[expr_contextType.Load] stmts) {
        debug("matched elif");
        stmtType[] b = (stmtType[])$stmts.stypes.toArray(new stmtType[$stmts.stypes.size()]);
        //the stmtType[0] is intended to be replaced in the iterator of the if_stmt rule.
        //there is probably a better way to do this.
        elifs.add(new If($Elif, $test.etype, b, new stmtType[0]));
    }
    ;

while_stmt
    : ^(While test[expr_contextType.Load] ^(Body body=stmts) (^(OrElse orelse=stmts))?) {
        List o = null;
        if ($OrElse != null) {
            o = $orelse.stypes;
        }
        While w = makeWhile($While, $test.etype, $body.stypes, o);
        $stmts::statements.add(w);
    }
    ;

for_stmt
    : ^(For ^(Target targ=test[expr_contextType.Store]) ^(Iter iter=test[expr_contextType.Load]) ^(Body body=stmts) (^(OrElse orelse=stmts))?) {
        List o = null;
        if ($OrElse != null) {
            o = $orelse.stypes;
        }
        For f = makeFor($For, $targ.etype, $iter.etype, $body.stypes, o);
        $stmts::statements.add(f);
    }
    ;

try_stmt
@init {
    List handlers = new ArrayList();
}
    : ^(TryExcept ^(Body body=stmts) except_clause[handlers]+ (^(OrElse orelse=stmts))? (^(FinalBody 'finally' fin=stmts))?) {
        List o = null;
        List f = null;
        if ($OrElse != null) {
            o = $orelse.stypes;
        }
        if ($FinalBody != null) {
            f = $fin.stypes;
        }
        stmtType te = makeTryExcept($TryExcept, $body.stypes, handlers, o, f);
        $stmts::statements.add(te);
    }
    | ^(TryFinally ^(Body body=stmts) ^(FinalBody fin=stmts)) {
        TryFinally tf = makeTryFinally($TryFinally, $body.stypes, $fin.stypes);
        $stmts::statements.add(tf);
    }
    ;

except_clause[List handlers]
    : ^(ExceptHandler 'except' (^(Type type=test[expr_contextType.Load]))? (^(Name name=test[expr_contextType.Store]))? ^(Body stmts)) {
        stmtType[] b;
        if ($stmts.start != null) {
            b = (stmtType[])$stmts.stypes.toArray(new stmtType[$stmts.stypes.size()]);
        } else b = new stmtType[0];
        exprType t = null;
        if ($Type != null) {
            t = $type.etype;
        }
        exprType n = null;
        if ($Name != null) {
            n = $name.etype;
        }
        //XXX: getCharPositionInLine() -7 is only accurate in the simplist cases -- need to
        //     look harder at CPython to figure out what is really needed here.
        handlers.add(new excepthandlerType($ExceptHandler, t, n, b, $ExceptHandler.getLine(), $ExceptHandler.getCharPositionInLine()));
    }
    ;

with_stmt
    : ^(With test[expr_contextType.Load] with_var? ^(Body stmts))
    ;

with_var
    : ('as' | NAME) test[expr_contextType.Load]
    ;

//FIXME: lots of placeholders
test[expr_contextType ctype] returns [exprType etype]
    : ^(AND left=test[ctype] right=test[ctype]) {
        List values = new ArrayList();
        boolean leftIsAnd = false;
        boolean rightIsAnd = false;
        BoolOp leftB = null;
        BoolOp rightB = null;
        if ($left.start.getType() == AND) {
            leftIsAnd = true;
            leftB = (BoolOp)$left.etype;
        }
        if ($right.start.getType() == AND) {
            rightIsAnd = true;
            rightB = (BoolOp)$right.etype;
        }
        exprType[] e;
        if (leftIsAnd && rightIsAnd) {
            debug("matched And + L + R");
            int lenL = leftB.values.length;
            int lenR = rightB.values.length;
            e = new exprType[lenL + lenR];
            System.arraycopy(leftB.values, 0, e, 0, lenL - 1);
            System.arraycopy(rightB.values, 0, e, lenL - 1, lenL + lenR);
        } else if (leftIsAnd) {
            debug("matched And + L");
            e = new exprType[leftB.values.length + 1];
            System.arraycopy(leftB.values, 0, e, 0, leftB.values.length);
            e[e.length - 1] = $right.etype;
        } else if (rightIsAnd) {
            debug("matched And + R");
            e = new exprType[rightB.values.length + 1];
            System.arraycopy(rightB.values, 0, e, 0, rightB.values.length);
            e[e.length - 1] = $left.etype;
        } else {
            debug("matched And");
            e = new exprType[2];
            e[0] = $left.etype;
            e[1] = $right.etype;
        }
        //XXX: could re-use BoolOps discarded above in many cases.
        $etype = new BoolOp($AND, boolopType.And, e);
    }
    | ^(OR left=test[ctype] right=test[ctype]) {
        //XXX: AND and OR could be factored into one method.
        List values = new ArrayList();
        boolean leftIsOr = false;
        boolean rightIsOr = false;
        BoolOp leftB = null;
        BoolOp rightB = null;
        if ($left.start.getType() == OR) {
            leftIsOr = true;
            leftB = (BoolOp)$left.etype;
        }
        if ($right.start.getType() == OR) {
            rightIsOr = true;
            rightB = (BoolOp)$right.etype;
        }
        exprType[] e;
        if (leftIsOr && rightIsOr) {
            debug("matched Or + L + R");
            int lenL = leftB.values.length;
            int lenR = rightB.values.length;
            e = new exprType[lenL + lenR];
            System.arraycopy(leftB.values, 0, e, 0, lenL - 1);
            System.arraycopy(rightB.values, 0, e, lenL - 1, lenL + lenR);
        } else if (leftIsOr) {
            debug("matched Or + L");
            e = new exprType[leftB.values.length + 1];
            System.arraycopy(leftB.values, 0, e, 0, leftB.values.length);
            e[e.length - 1] = $right.etype;
        } else if (rightIsOr) {
            debug("matched Or + R");
            e = new exprType[rightB.values.length + 1];
            System.arraycopy(rightB.values, 0, e, 0, rightB.values.length);
            e[e.length - 1] = $left.etype;
        } else {
            debug("matched Or");
            e = new exprType[2];
            e[0] = $left.etype;
            e[1] = $right.etype;
        }
        //XXX: could re-use BoolOps discarded above in many cases.
        $etype = new BoolOp($OR, boolopType.Or, e);
    }
    | ^(comp_op left=test[ctype] targs=test[ctype]) {
        exprType[] comparators;
        cmpopType[] ops;
        exprType val;
        //XXX: does right need to be checked for Compare?
        if ($left.etype instanceof Compare) {
            Compare c = (Compare)$left.etype;
            comparators = new exprType[c.comparators.length + 1];
            ops = new cmpopType[c.ops.length + 1];
            System.arraycopy(c.ops, 0, ops, 0, c.ops.length);
            System.arraycopy(c.comparators, 0, comparators, 0, c.comparators.length);
            comparators[c.comparators.length] = $targs.etype;
            ops[c.ops.length] = $comp_op.op;
            val = c.left;
        } else {
            comparators = new exprType[1];
            ops = new cmpopType[1];
            ops[0] = $comp_op.op;
            comparators[0] = $targs.etype;
            val = $left.etype;
        }
        $etype = new Compare($comp_op.start, val, ops, comparators);
        debug("COMP_OP: " + $comp_op.start);
    }
    | atom[ctype] {
        debug("matched atom");
        debug("***" + $atom.etype);
        $etype = $atom.etype;
    }
    | ^(binop left=test[ctype] right=test[ctype]) {
        debug("BinOp matched");
        $etype = new BinOp($left.start, left.etype, $binop.op, right.etype);
    }
    | call_expr {
        $etype = $call_expr.etype;
    }
    | lambdef {
        $etype = $lambdef.etype;
    }
    ;

comp_op returns [cmpopType op]
    : LESS {$op = cmpopType.Lt;}
    | GREATER {$op = cmpopType.Gt;}
    | EQUAL {$op = cmpopType.Eq;}
    | GREATEREQUAL {$op = cmpopType.GtE;}
    | LESSEQUAL {$op = cmpopType.LtE;}
    | ALT_NOTEQUAL {$op = cmpopType.NotEq;}
    | NOTEQUAL {$op = cmpopType.NotEq;}
    | 'in' {$op = cmpopType.In;}
    | NotIn {$op = cmpopType.NotIn;}
    | 'is' {$op = cmpopType.Is;}
    | IsNot {$op = cmpopType.IsNot;}
    ;

//I *think* only sequences need to collect test rules in the walker since
//testlist in the parser either results in one test or a tuple.
elts[expr_contextType ctype] returns [List etypes]
scope {
    List elements;
}
@init {
    $elts::elements = new ArrayList();
}

    : elt[ctype]+ {
        $etypes = $elts::elements;
    }
    ;

elt[expr_contextType ctype]
    : test[ctype] {
        $elts::elements.add($test.etype);
    }
    ;

//FIXME: lots of placeholders
atom[expr_contextType ctype] returns [exprType etype]
    : ^(Tuple (^(Elts elts[ctype]))?) {
        debug("matched Tuple");
        exprType[] e;
        if ($Elts != null) {
            e = (exprType[])$elts.etypes.toArray(new exprType[$elts.etypes.size()]);
        } else {
            e = new exprType[0];
        }
        $etype = new Tuple($Tuple, e, ctype);
    }
    | ^(List (^(Elts elts[ctype]))?) {
        debug("matched List");
        exprType[] e;
        if ($Elts != null) {
            e = (exprType[])$elts.etypes.toArray(new exprType[$elts.etypes.size()]);
        } else {
            e = new exprType[0];
        }
        $etype = new org.python.antlr.ast.List($List, e, ctype);
    }
    | ^(ListComp test[ctype] list_for) {
        debug("matched ListComp");
        $etype = new ListComp($ListComp, $test.etype, new comprehensionType[]{$list_for.gen});
    }
    | ^(GenExpFor gen_for) {}
    | ^(Dict (^(Elts elts[ctype]))?) {
        exprType[] keys;
        exprType[] values;
        if ($Elts != null) {
            int size = $elts.etypes.size() / 2;
            keys = new exprType[size];
            values = new exprType[size];
            for(int i=0;i<size;i++) {
                keys[i] = (exprType)$elts.etypes.get(i*2);
                values[i] = (exprType)$elts.etypes.get(i*2+1);
            }
        } else {
            keys = new exprType[0];
            values = new exprType[0];
        }
        $etype = new Dict($Dict, keys, values);
 
    }
    | ^(Repr test[ctype]*) {}
    | ^(Name NAME) {
        debug("matched Name " + $NAME.text);
        $etype = new Name($NAME, $NAME.text, ctype);
    }
    | ^(DOT NAME test[expr_contextType.Load]) {
        debug("matched DOT in atom: " + $test.etype + "###" + $NAME.text);
        $etype = new Attribute($DOT, $test.etype, $NAME.text, ctype);
    }
    | ^(SubscriptList subscriptlist test[expr_contextType.Load]) {
        //XXX: only handling one subscript for now.
        sliceType s;
        if ($subscriptlist.sltypes.size() == 0) {
            s = null;
        } else {
            s = (sliceType)$subscriptlist.sltypes.get(0);
        }
        $etype = new Subscript($SubscriptList, $test.etype, s, ctype);
    }
    | ^(Num INT) {
        $etype = makeInt($INT);
        debug("makeInt output: " + $etype);
    }
    | ^(Num LONGINT) {$etype = makeInt($LONGINT);}
    | ^(Num FLOAT) {
        $etype = makeFloat($FLOAT);
        debug("float matched" + $etype);
    }
    | ^(Num COMPLEX) {$etype = makeComplex($COMPLEX);}
    | stringlist {
        $etype = new Str($stringlist.start, extractStrings($stringlist.strings));
    }
    | ^(USub test[ctype]) {
        debug("USub matched " + $test.etype);
        //FIXME: need to actually negate this
        $etype = negate($USub, $test.etype);
    }
    | ^(UAdd test[ctype]) {
        $etype = $test.etype;
    }
    | ^(Invert test[ctype]) {
        //FIXME: need to actually invert this
        $etype = $test.etype;
    }
    | ^(NOT test[ctype]) {
        $etype = new UnaryOp($NOT, unaryopType.Not, $test.etype);
    }
    ;

stringlist returns [List strings]
@init {
    List strs = new ArrayList();
}
    : ^(Str string[strs]+) {$strings = strs;}
    ;

string[List strs]
    : STRING {strs.add($STRING.text);}
    ;

lambdef returns [exprType etype]
    : ^(Lambda varargslist? ^(Body test[expr_contextType.Load])) {
        argumentsType a = $varargslist.args;
        if (a == null) {
            a = new argumentsType($Lambda, new exprType[0], null, null, new exprType[0]);
        }
        $etype = new Lambda($Lambda, a, $test.etype);
    }
    ;

subscriptlist returns [List sltypes]
@init {
    List subs = new ArrayList();
}
    :   subscript[subs]+ {
        $sltypes = subs;
    }
    |   index[subs]+ {
        exprType e;
        if (subs.size() > 1) {
            exprType[] es = (exprType[])subs.toArray(new exprType[subs.size()]);
            e = new Tuple($index.start, es, expr_contextType.Load);
        } else {
            e = (exprType)subs.get(0);
        }
        $sltypes = new ArrayList();
        $sltypes.add(new Index($index.start, e));
    }
    ;

index [List subs]
    : ^(Index test[expr_contextType.Load]) {
        subs.add($test.etype);
    }
    ;

subscript [List subs]
    : Ellipsis
    | ^(Subscript (^(Lower start=test[expr_contextType.Load]))?
          (^(Upper COLON (^(UpperOp end=test[expr_contextType.Load]))?))? (^(Step (^(StepOp op=test[expr_contextType.Load]))?))?) {
              boolean isSlice = false;
              exprType s = null;
              exprType e = null;
              exprType o = null;
              if ($Lower != null) {
                  s = $start.etype;
              }
              if ($Upper != null) {
                  isSlice = true;
                  if ($UpperOp != null) {
                      e = $end.etype;
                  }
              }
              if ($Step != null) {
                  isSlice = true;
                  if ($StepOp != null) {
                      o = $op.etype;
                  }
              }

              if (isSlice) {
                 subs.add(new Slice($Subscript, s, e, o));
              }
              else {
                 subs.add(new Index($Subscript, s));
              }
          }
          ;

classdef
    : ^(ClassDef ^(Name classname=NAME) (^(Bases bases))? ^(Body stmts)) {
        List b;
        if ($Bases != null) {
            b = $bases.names;
        } else {
            b = new ArrayList();
        }
        $stmts::statements.add(makeClassDef($ClassDef, $classname, b, $stmts.stypes));
    }
    ;

bases returns [List names]
@init {
    List nms = new ArrayList();
}
    : base[nms] {
        //The instanceof and tuple unpack here is gross.  I *should* be able to detect
        //"Tuple or Tuple DOWN or some such in a syntactic predicate in the "base" rule
        //instead, but I haven't been able to get it to work.
        if (nms.get(0) instanceof Tuple) {
            debug("TUPLE");
            Tuple t = (Tuple)nms.get(0);
            $names = Arrays.asList(t.elts);
        } else {
            debug("NOT TUPLE");
            $names = nms;
        }
    }
    ;

//FIXME: right now test matches a Tuple from Python.g output -- I'd rather
//       unpack the tuple here instead of in bases, otherwise this rule
//       should just get absorbed back into bases, since it is never matched
//       more than once as it is now.
base[List names]
    : test[expr_contextType.Load] {
        names.add($test.etype);
    }
    ;

arglist returns [List args, List keywords, exprType starargs, exprType kwargs]
@init {
    List arguments = new ArrayList();
    List kws = new ArrayList();
}
    : ^(Args argument[arguments]* keyword[kws]*) (^(StarArgs stest=test[expr_contextType.Load]))? (^(KWArgs ktest=test[expr_contextType.Load]))? {
        $args=arguments;
        $keywords=kws;
        if ($StarArgs != null) {
            $starargs=$stest.etype;
        }
        if ($KWArgs != null) {
            $kwargs=$ktest.etype;
        }
    }
    | ^(StarArgs stest=test[expr_contextType.Load]) (^(KWArgs ktest=test[expr_contextType.Load]))? {
        $args=arguments;
        $keywords=kws;
        $starargs=$stest.etype;
        if ($KWArgs != null) {
            $kwargs=$ktest.etype;
        }
    }
    | ^(KWArgs test[expr_contextType.Load]) {
        $args=arguments;
        $keywords=kws;
        $kwargs=$test.etype;
    }
    ;

argument[List arguments]
    : ^(Arg test[expr_contextType.Load]) {
        arguments.add($test.etype);
    }
    | ^(GenFor test[expr_contextType.Load] gen_for)
        //arguments.add($test.etype));
    ;

keyword[List kws]
    : ^(Keyword ^(Arg arg=test[expr_contextType.Load]) ^(Value val=test[expr_contextType.Load])) {
        kws.add(new keywordType($Keyword, $arg.text, $val.etype));
    }
    ;

list_iter returns [exprType etype]
    : list_for
    | list_if {
        $etype = $list_if.etype;
    }
    ;

list_for returns [comprehensionType gen]
    :
    ^(ListFor ^(Target targ=test[expr_contextType.Store]) ^(Iter iter=test[expr_contextType.Load]) (^(Ifs list_iter))?) {
        debug("matched list_for");
        //XXX: Not collecting from Ifs yet.
        exprType[] e;
        if ($Ifs != null && $list_iter.etype != null) {
            e = new exprType[]{$list_iter.etype};
        } else {
            e = new exprType[0];
        }
        $gen = new comprehensionType($ListFor, $targ.etype, $iter.etype, e);
    }
    ;

list_if returns [exprType etype]
    : ^(ListIf ^(Target test[expr_contextType.Load]) (Ifs list_iter)?) {
        $etype = $test.etype;
    }
    ;

gen_iter: gen_for
        | gen_if
        ;

gen_for: ^(GenFor ^(Target test[expr_contextType.Load]+) (^(Iter gen_iter))?)
       ;

gen_if: ^(GenIf ^(Target test[expr_contextType.Load]) (^(Iter gen_iter))?)
      ;

