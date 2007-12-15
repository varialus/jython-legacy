tree grammar PythonWalker;

options {
    tokenVocab=Python;
    ASTLabelType=PythonTree;
}

@header { 
package org.python.antlr;

//import org.python.core.Py;
//import org.python.core.PyString;
//import org.python.core.CompilerFlags;
import org.python.antlr.ast.aliasType;
import org.python.antlr.ast.argumentsType;
import org.python.antlr.ast.cmpopType;
import org.python.antlr.ast.excepthandlerType;
import org.python.antlr.ast.exprType;
import org.python.antlr.ast.expr_contextType;
import org.python.antlr.ast.keywordType;
import org.python.antlr.ast.modType;
import org.python.antlr.ast.operatorType;
import org.python.antlr.ast.stmtType;
import org.python.antlr.ast.Assign;
import org.python.antlr.ast.Attribute;
import org.python.antlr.ast.AugAssign;
import org.python.antlr.ast.Break;
import org.python.antlr.ast.Call;
import org.python.antlr.ast.ClassDef;
import org.python.antlr.ast.Compare;
import org.python.antlr.ast.Continue;
import org.python.antlr.ast.Delete;
import org.python.antlr.ast.Dict;
import org.python.antlr.ast.Expr;
import org.python.antlr.ast.FunctionDef;
import org.python.antlr.ast.Import;
import org.python.antlr.ast.Module;
import org.python.antlr.ast.Name;
import org.python.antlr.ast.Num;
import org.python.antlr.ast.TryExcept;
import org.python.antlr.ast.TryFinally;
import org.python.antlr.ast.Tuple;
import org.python.antlr.ast.Pass;
import org.python.antlr.ast.Print;
import org.python.antlr.ast.Raise;
import org.python.antlr.ast.Return;
import org.python.antlr.ast.Str;
import org.python.antlr.ast.Yield;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
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
            return s.substring(quotes+start+1, s.length()-quotes);
        } else {
            StringBuffer sb = new StringBuffer(s.length());
            char[] ca = s.toCharArray();
            int n = ca.length-quotes;
            int i=quotes+start;
            int last_i=i;
            //return PyString.decode_UnicodeEscape(s, i, n, "strict", ustring);
            return decode_UnicodeEscape(s, i, n, "strict", ustring);
        }
    }

    //FIXME: placeholder until I re-integrate with Jython.
    public static String decode_UnicodeEscape(String str, int start, int end, String errors, boolean unicode) {
        return str.substring(start, end);
    }


    Num makeNum(PythonTree t) {
        debug("Num matched");
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
            //return new Num(t, Py.newLong(new java.math.BigInteger(s, radix)));
            return new Num(t, new java.math.BigInteger(s, radix));
        }
        int ndigits = s.length();
        int i=0;
        while (i < ndigits && s.charAt(i) == '0')
            i++;
        if ((ndigits - i) > 11) {
            //return new Num(t, Py.newLong(new java.math.BigInteger(s, radix)));
            return new Num(t, new java.math.BigInteger(s, radix));
        }

        long l = Long.valueOf(s, radix).longValue();
        if (l > 0xffffffffl || (radix == 10 && l > Integer.MAX_VALUE)) {
            //return new Num(t, Py.newLong(new java.math.BigInteger(s, radix)));
            return new Num(t, new java.math.BigInteger(s, radix));
        }
        //return new Num(t, Py.newInteger((int) l));
        return new Num(t, new Long(l));
    }

    private stmtType makeTryExcept(PythonTree t, List body, List handlers, List orelses, List finBody) {
        stmtType[] b = (stmtType[])body.toArray(new stmtType[body.size()]);
        excepthandlerType[] e = (excepthandlerType[])handlers.toArray(new excepthandlerType[handlers.size()]);
        stmtType[] o;
        if (orelses != null) {
            o = (stmtType[])orelses.toArray(new stmtType[orelses.size()]);
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
    : NAME (ASSIGN test[expr_contextType.Load] )? {
        params.add(new Name($NAME, $NAME.text, org.python.antlr.ast.Name.Param));
        if ($ASSIGN != null) {
            defaults.add($test.etype);
        }
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
            debug("not call site!");
            decs.add($dotted_attr.etype);
        } else {
            exprType[] args = (exprType[])$arglist.args.toArray(new exprType[$arglist.args.size()]);
            keywordType[] keywords = (keywordType[])$arglist.keywords.toArray(new keywordType[$arglist.keywords.size()]);
            Call c = new Call($Call, $dotted_attr.etype, args, keywords, $arglist.starargs, $arglist.kwargs);
            debug("call site!");
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
//    | call_expr
    ;

call_expr
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
        $stmts::statements.add(new Expr($Call, c));
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

augassign returns [int op]
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

print_stmt
    : ^(Print RIGHTSHIFT? test[expr_contextType.Load]?) {
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
    : ^(Import dotted_as_name+) {
    }
    | ^(ImportFrom dotted_name ^(Import STAR))
    | ^(ImportFrom dotted_name ^(Import import_as_name+))
    ;

import_as_name
    : ^(Alias NAME (^(Asname NAME))?)
    ;

dotted_as_name
    : ^(Alias dotted_name (^(Asname NAME))?)
    ;

dotted_name
    : start=NAME (DOT NAME)*
    ;

global_stmt
    : ^(Global NAME+)
    ;

exec_stmt
    : ^(Exec test[expr_contextType.Load] (^(Globals test[expr_contextType.Load]))? (^(Locals test[expr_contextType.Load]))?)
    ;

assert_stmt
    : ^(Assert ^(Test test[expr_contextType.Load]) (^(Msg test[expr_contextType.Load]))?)
    ;


if_stmt
    : ^(If test[expr_contextType.Load] suite elif_clause* (^(OrElse suite))?)
    ;

elif_clause
    : ^(Elif test[expr_contextType.Load] suite)
    ;

while_stmt
    : ^(While test[expr_contextType.Load] ^(Body suite) (^(OrElse suite))?)
    ;

for_stmt
    : ^(For ^(Target test[expr_contextType.Load]+) ^(Iter test[expr_contextType.Load]) ^(Body suite) (^(OrElse suite))?)
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
    : ^(ExceptHandler (^(Type type=test[expr_contextType.Load]))? (^(Name name=test[expr_contextType.Load]))? ^(Body stmts)) {
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
    : ^(With test[expr_contextType.Load] with_var? ^(Body suite))
    ;

with_var
    : ('as' | NAME) test[expr_contextType.Load]
    ;

 
suite
    : INDENT stmt+ DEDENT
    | stmt+
    ;

//FIXME: lots of placeholders
test[int ctype] returns [exprType etype]
    : ^('and' test[ctype] test[ctype])
    | ^('or' test[ctype] test[ctype])
    | ^('not' test[ctype])
    | ^(comp_op left=test[ctype] targs=test[ctype]) {
        exprType[] targets = new exprType[1];
        int[] ops = new int[1];
        ops[0] = $comp_op.op;
        targets[0] = $targs.etype;
        $etype = new Compare($comp_op.start, $left.etype, ops, targets);
        debug("COMP_OP: " + $comp_op.start);
    }
    | atom[ctype] {
        debug("matched atom");
        debug("***" + $atom.etype);
        $etype = $atom.etype;
    }
    | ^(PLUS test[ctype] test[ctype])
    | ^(MINUS left=test[ctype] right=test[ctype]) {}
    | ^(AMPER test[ctype] test[ctype])
    | ^(VBAR test[ctype] test[ctype])
    | ^(CIRCUMFLEX test[ctype] test[ctype])
    | ^(LEFTSHIFT test[ctype] test[ctype])
    | ^(RIGHTSHIFT test[ctype] test[ctype])
    | ^(STAR test[ctype] test[ctype])
    | ^(SLASH test[ctype] test[ctype])
    | ^(PERCENT test[ctype] test[ctype])
    | ^(DOUBLESLASH test[ctype] test[ctype])
    | ^(DOUBLESTAR test[ctype] test[ctype])
    | ^(UAdd test[ctype])
    | ^(USub test[ctype])
    | ^(Invert test[ctype])
    | lambdef
    | call_expr
    ;

comp_op returns [int op]
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
elts[int ctype] returns [List etypes]
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

elt[int ctype]
    : test[ctype] {
        $elts::elements.add($test.etype);
    }
    ;

//FIXME: lots of placeholders
atom[int ctype] returns [exprType etype]
    : ^(Tuple elts[ctype]) {
        exprType[] e = new exprType[$elts.etypes.size()];
        for(int i=0;i<$elts.etypes.size();i++) {
            e[i] = (exprType)$elts.etypes.get(i);
        }
        $etype = new Tuple($Tuple, e, ctype);
    }
    | ^(List test[ctype]*) {}
    | ^(ListComp list_for) {}
    | ^(GenExpFor gen_for) {}
//    | ^(Tuple test[ctype]*) {}
    | ^(Parens test[ctype]*) {}
    | ^(Dict test[ctype]*) {}
    | ^(Repr test[ctype]*) {}
    | ^(Name NAME) {
        debug("matched Name " + $NAME.text);
        $etype = new Name($NAME, $NAME.text, ctype);
    }
    | ^(DOT NAME test[ctype]) {
        debug("matched DOT in atom: " + $test.etype + "###" + $NAME.text);
        $etype = new Attribute($DOT, $test.etype, $NAME.text, expr_contextType.Load);
    }
    | ^(SubscriptList subscriptlist test[ctype])
    | ^(Num INT) {$etype = makeNum($INT);}
    | ^(Num LONGINT) {$etype = makeNum($LONGINT);}
    | ^(Num FLOAT)
    | ^(Num COMPLEX)
    | stringlist {
        $etype = new Str($stringlist.start, extractStrings($stringlist.strings));
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

lambdef: ^(Lambda varargslist? ^(Body test[expr_contextType.Load]))
       ;

subscriptlist
    :   subscript+
    ;

subscript : Ellipsis
          | ^(Subscript (^(Start test[expr_contextType.Load]))? (^(End test[expr_contextType.Load]))? (^(SliceOp test[expr_contextType.Load]?))?)
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

list_iter: list_for
    | list_if
    ;

list_for: ^(ListFor ^(Target test[expr_contextType.Load]+) ^(Iter test[expr_contextType.Load]+) (^(Ifs list_iter))?)
    ;

list_if: ^(ListIf ^(Target test[expr_contextType.Load]) (Ifs list_iter)?)
    ;

gen_iter: gen_for
        | gen_if
        ;

gen_for: ^(GenFor ^(Target test[expr_contextType.Load]+) (^(Iter gen_iter))?)
       ;

gen_if: ^(GenIf ^(Target test[expr_contextType.Load]) (^(Iter gen_iter))?)
      ;

