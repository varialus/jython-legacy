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
import org.python.antlr.ast.exprType;
import org.python.antlr.ast.expr_contextType;
import org.python.antlr.ast.modType;
import org.python.antlr.ast.operatorType;
import org.python.antlr.ast.stmtType;
import org.python.antlr.ast.Assign;
import org.python.antlr.ast.AugAssign;
import org.python.antlr.ast.ClassDef;
import org.python.antlr.ast.Compare;
import org.python.antlr.ast.Dict;
import org.python.antlr.ast.Expr;
import org.python.antlr.ast.FunctionDef;
import org.python.antlr.ast.Import;
import org.python.antlr.ast.Module;
import org.python.antlr.ast.Name;
import org.python.antlr.ast.Num;
import org.python.antlr.ast.Pass;
import org.python.antlr.ast.Tuple;
import org.python.antlr.ast.Pass;
import org.python.antlr.ast.Print;
import org.python.antlr.ast.Str;

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

    private FunctionDef makeFunctionDef(PythonTree t, PythonTree nameToken, argumentsType args, List funcStatements) {
        argumentsType a;
        if (args != null) {
            a = args;
        } else {
            a = new argumentsType(t, new exprType[0], null, null, new exprType[0]); 
        }
        stmtType[] s = (stmtType[])funcStatements.toArray(new stmtType[funcStatements.size()]);
        return new FunctionDef(t, nameToken.getText(), a, s, null);
    }

    private argumentsType makeArgumentsType(PythonTree t, List params, PythonTree snameToken,
        PythonTree knameToken, List defaults) {

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
        boolean raw = false;
        if (quoteChar == 'u' || quoteChar == 'U') {
            ustring = true;
            start++;
        }
        quoteChar = s.charAt(start);
        if (quoteChar == 'r' || quoteChar == 'R') {
            raw = true;
            start++;
        }
        quoteChar = s.charAt(start);
        int quotes = 1;
        if (quoteChar == '"' && s.charAt(start +1) == '"' ||
            quoteChar == '\'' && s.charAt(start+1) == '\'') {
            quotes = 3;
        }
        if (raw) {
            //XXX: What about ur?
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
        return str;
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

}

module returns [modType mod]
    : ^(Module stmts?) {
        debug("matched module");
        $mod = makeMod($Module, $stmts.stypes);
    }
    ;

decorator: ^(Decorator dotted_name ^(ArgList arglist?))
         ;

decorators: decorator+
          ;

funcdef : ^(FunctionDef  ^(Name NAME) ^(Arguments varargslist?) ^(Body suite) (^(Decorators decorators))? )
        ;

varargslist
    : ^(Args defparameter*) (^(StarArgs sname=NAME))? (^(KWArgs kname=NAME))? {
    }
    | ^(StarArgs sname=NAME) (^(KWArgs kname=NAME))? {
    }
    | ^(KWArgs NAME) {
    }
    ;

defparameter
    : NAME (ASSIGN test[expr_contextType.Load] )? {
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
    | ^(augassign targ=test[expr_contextType.Load] value=test[expr_contextType.Load]) {
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

targets returns [List etypes]
@init {
    List targs = new ArrayList();
}
    : target[targs]+ {
        $etypes = targs;
    }
    ;

target[List etypes]
    : ^(Target test[expr_contextType.Load]) {
        etypes.add($test.etype);
    }
    ;

augassign
    : PLUSEQUAL {}
    | MINUSEQUAL {}
    | STAREQUAL {}
    | SLASHEQUAL {}
    | PERCENTEQUAL {}
    | AMPEREQUAL {}
    | VBAREQUAL {}
    | CIRCUMFLEXEQUAL {}
    | LEFTSHIFTEQUAL {}
    | RIGHTSHIFTEQUAL {}
    | DOUBLESTAREQUAL {}
    | DOUBLESLASHEQUAL {}
    ;

print_stmt
    : ^(Print RIGHTSHIFT? test[expr_contextType.Load]?)
    {
    }
    ;

del_stmt
    : ^(Delete ^(Targets test[expr_contextType.Load]+))
    ;

pass_stmt
    : Pass {
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
    : 'break'
    ;

continue_stmt
    : 'continue'
    ;

return_stmt
    : ^(Return (test[expr_contextType.Load])?)
    ;

yield_stmt
    : ^(Yield test[expr_contextType.Load]?)
    ;

raise_stmt
    : ^(Raise (^(Type test[expr_contextType.Load]))? (^(Inst test[expr_contextType.Load]))? (^(Tback test[expr_contextType.Load]))?)
    ;

import_stmt
    : ^(Import dotted_as_name+) {
    }
    | ^(ImportFrom dotted_name ^(Import STAR))
    | ^(ImportFrom dotted_name ^(Import import_as_name+))
    ;

import_as_name : ^(Alias NAME (^(Asname NAME))?)
               ;

dotted_as_name : ^(Alias dotted_name (^(Asname NAME))?)
               ;

dotted_name
    : start=NAME (DOT NAME)* {
    }
    ;

global_stmt : ^(Global NAME+)
            ;

exec_stmt : ^(Exec test[expr_contextType.Load] (^(Globals test[expr_contextType.Load]))? (^(Locals test[expr_contextType.Load]))?)
          ;

assert_stmt : ^(Assert ^(Test test[expr_contextType.Load]) (^(Msg test[expr_contextType.Load]))?)
            ;


if_stmt: ^(If test[expr_contextType.Load] suite elif_clause* (^(OrElse suite))?)
       ;

elif_clause : ^(Elif test[expr_contextType.Load] suite)
            ;

while_stmt : ^(While test[expr_contextType.Load] ^(Body suite) (^(OrElse suite))?)
           ;

for_stmt : ^(For ^(Target test[expr_contextType.Load]+) ^(Iter test[expr_contextType.Load]) ^(Body suite) (^(OrElse suite))?)
         ;

try_stmt : ^(TryExcept ^(Body suite) except_clause+ (^(OrElse suite))?)
         | ^(TryFinally suite)
         ;

except_clause : ^(Except (^(Type test[expr_contextType.Load]))? (^(Name test[expr_contextType.Load]))? ^(Body suite))
              ;

with_stmt: ^(With test[expr_contextType.Load] with_var? ^(Body suite))
         ;

with_var: ('as' | NAME) test[expr_contextType.Load]
        ;

 
suite
    : INDENT stmt+ DEDENT
    | stmt+
    ;

//FIXME: lots of placeholders
test[int ctype] returns [exprType etype]
    : ^('and' test[expr_contextType.Load] test[expr_contextType.Load])
    | ^('or' test[expr_contextType.Load] test[expr_contextType.Load])
    | ^('not' test[expr_contextType.Load])
    | ^(comp_op left=test[expr_contextType.Load] targs=test[expr_contextType.Load]) {
        exprType[] targets = new exprType[1];
        int[] ops = new int[1];
        ops[0] = $comp_op.op;
        targets[0] = $targs.etype;
        $etype = new Compare($comp_op.start, $left.etype, ops, targets);
        debug("COMP_OP: " + $comp_op.start);
    }
    | atom[ctype] (trailer)* {$etype = $atom.etype;}
    | ^(PLUS test[expr_contextType.Load] test[expr_contextType.Load])
    | ^(MINUS left=test[expr_contextType.Load] right=test[expr_contextType.Load]) {}
    | ^(AMPER test[expr_contextType.Load] test[expr_contextType.Load])
    | ^(VBAR test[expr_contextType.Load] test[expr_contextType.Load])
    | ^(CIRCUMFLEX test[expr_contextType.Load] test[expr_contextType.Load])
    | ^(LEFTSHIFT test[expr_contextType.Load] test[expr_contextType.Load])
    | ^(RIGHTSHIFT test[expr_contextType.Load] test[expr_contextType.Load])
    | ^(STAR test[expr_contextType.Load] test[expr_contextType.Load])
    | ^(SLASH test[expr_contextType.Load] test[expr_contextType.Load])
    | ^(PERCENT test[expr_contextType.Load] test[expr_contextType.Load])
    | ^(DOUBLESLASH test[expr_contextType.Load] test[expr_contextType.Load])
    | ^(DOUBLESTAR test[expr_contextType.Load] test[expr_contextType.Load])
    | ^(UAdd test[expr_contextType.Load])
    | ^(USub test[expr_contextType.Load])
    | ^(Invert test[expr_contextType.Load])
    | lambdef
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

//FIXME: lots of placeholders
atom[int ctype] returns [exprType etype]
    : ^(List test[expr_contextType.Load]*) {}
    | ^(ListComp list_for) {}
    | ^(GenExpFor gen_for) {}
    | ^(Tuple test[expr_contextType.Load]*) {}
    | ^(Parens test[expr_contextType.Load]*) {}
    | ^(Dict test[expr_contextType.Load]*) {}
    | ^(Repr test[expr_contextType.Load]*) {}
    | ^(Name NAME) {$etype = new Name($NAME, $NAME.text, ctype);}
    | ^(Num INT) {$etype = makeNum($INT);}
    | ^(Num LONGINT) {$etype = makeNum($LONGINT);}
    | ^(Num FLOAT)
    | ^(Num COMPLEX)
    | stringlist {
    }
    ;

stringlist
    : ^(Str string+) {}
    ;

string
    : STRING {}
    ;

lambdef: ^(Lambda varargslist? ^(Body test[expr_contextType.Load]))
       ;

trailer
    : ^(ArgList arglist?)
    | ^(SubscriptList subscriptlist)
    | DOT NAME
    ;

subscriptlist
    :   subscript+
    ;

subscript : Ellipsis
          | ^(Subscript (^(Start test[expr_contextType.Load]))? (^(End test[expr_contextType.Load]))? (^(SliceOp test[expr_contextType.Load]?))?)
          ;

classdef
    : ^(ClassDef ^(Name NAME) (^(Bases test[expr_contextType.Load]))? ^(Body suite)) {
        debug("class matched");
    }
    ;

arglist
    : ^(Arguments argument+) (^(StarArgs test[expr_contextType.Load]))? (^(KWArgs test[expr_contextType.Load]))?
    | ^(StarArgs test[expr_contextType.Load]) (^(KWArgs test[expr_contextType.Load]))?
    | ^(KWArgs test[expr_contextType.Load])
    ;

argument : ^(Arg test[expr_contextType.Load] (^(Default test[expr_contextType.Load]))?)
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

