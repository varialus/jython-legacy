tree grammar PythonWalker;

options {
    tokenVocab=Python;
    ASTLabelType=CommonTree;
}

@header { 
package org.python.antlr;
} 

module
    : stmt* {
        //System.out.println("<STMT>");
    }
    ;


funcdef
    : ^(FunctionDef ^(Name NAME) ^(Args varargslist?) ^(Body suite)) {
    }
    ;

varargslist
    : ^(Arguments defparameter*) (^(StarArgs sname=NAME))? (^(KWArgs kname=NAME))? {
    }
    | ^(StarArgs sname=NAME) (^(KWArgs kname=NAME))? {
    }
    | ^(KWArgs NAME) {
    }
    ;

defparameter
    : NAME (ASSIGN test )? {
    }
    ;

stmt:
    ^(Stmt any_stmt+)
    ;

any_stmt //combines simple_stmt and compound_stmt from Python.g
    : expr_stmt
    | print_stmt
    | del_stmt
    | pass_stmt
    | flow_stmt
    | import_stmt
    | global_stmt
    | exec_stmt
    | assert_stmt
    | funcdef
    | classdef
    | if_stmt
    | while_stmt
    | for_stmt
    | try_stmt
    ;

expr_stmt
    : ^(Expr testlist) {
    }
    | ^(Expr ^(augassign targ=test value=test)) {
    }
    | ^(Expr ^(Assign (^(Target testlist))+ ^(Value testlist))) {
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
    : ^(Print RIGHTSHIFT? testlist?)
    {
    }
    ;

del_stmt : ^(Delete exprlist)
         ;

pass_stmt : Pass {
    }
    ;

flow_stmt : break_stmt
          | continue_stmt
          | return_stmt
          | raise_stmt
          | yield_stmt
          ;

break_stmt : 'break'
           ;

continue_stmt : 'continue'
              ;

return_stmt : ^(Return (testlist)?)
            ;

yield_stmt : ^(Yield testlist)
           ;

raise_stmt: ^(Raise (^(Type test))? (^(Inst test))? (^(Tback test))?)
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

exec_stmt : ^(Exec test (^(Globals test))? (^(Locals test))?)
          ;

assert_stmt : ^(Assert ^(Test test) (^(Msg test))?)
            ;


if_stmt: ^(If test suite elif_clause* (^(Else suite))?)
       ;

elif_clause : ^(Elif test suite)
            ;

while_stmt : ^(While test ^(Body suite) (^(Else suite))?)
           ;

for_stmt : ^(For exprlist ^(In testlist) ^(Body suite) (^(Else suite))?)
         ;

try_stmt : ^(TryExcept ^(Body suite) except_clause+ (^(Else suite))?)
         | ^(TryFinally suite)
         ;

except_clause : ^(Except (^(Type test))? (^(Name test))? ^(Body suite))
              ;
 
suite
    : INDENT stmt+ DEDENT
    | stmt+
    ;

//FIXME: lots of placeholders
test
    : ^('and' test test) {}
    | ^('or' test test) {}
    | ^('not' test) {}
    | ^(comp_op left=test targs=test)
    | ^(PLUS test test)
    | ^(MINUS left=test right=test) {}
    | ^(AMPER test test)
    | ^(VBAR test test)
    | ^(CIRCUMFLEX test test)
    | ^(LEFTSHIFT test test)
    | ^(RIGHTSHIFT test test)
    | ^(STAR test test)
    | ^(SLASH test test)
    | ^(PERCENT test test)
    | ^(DOUBLESLASH test test)
    | ^(DOUBLESTAR test test)
    | ^(UnaryPlus test)
    | ^(UnaryMinus test)
    | ^(UnaryTilde test)
    | lambdef
    | atom (trailer)* {}
    ;

comp_op
    : LESS {}
    | GREATER {}
    | EQUAL {}
    | GREATEREQUAL {}
    | LESSEQUAL {}
    | ALT_NOTEQUAL {}
    | NOTEQUAL {}
    | 'in' {}
    | NotIn {}
    | 'is' {}
    | IsNot {}
    ;

//FIXME: lots of placeholders
atom
    : ^(List testlist?) {}
    | ^(ListComp list_for) {}
    | ^(Parens testlist?) {}
    | ^(Dict testlist?) {}
    | ^(Repr testlist?) {}
    | NAME {}
    | INT {}
    | LONGINT {}
    | FLOAT {}
    | COMPLEX {}
    | stringlist {
    }
    ;

stringlist
    : ^(String string+) {}
    ;

string
    : STRING {}
    ;

lambdef: ^(Lambda varargslist? ^(Body test))
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
          | ^(Subscript (^(Start test))? (^(End test))? (^(SliceOp test?))?)
          ;

exprlist : ^(ExprList test+)
         ;

testlist
    : test+ {}
    ;

classdef
    : ^(ClassDef ^(Name NAME) (^(Bases testlist))? ^(Body suite)) {
        //System.out.println("class ");
    }
    ;

arglist
    : ^(Arguments argument+) (^(StarArgs test))? (^(KWArgs test))?
    | ^(StarArgs test) (^(KWArgs test))?
    | ^(KWArgs test)
    ;

argument : ^(Arg test (^(Default test))?)
         ;

list_iter: list_for
    | list_if
    ;

list_for: 'for' exprlist 'in' testlist (list_iter)?
    ;

list_if: 'if' test (list_iter)?
    ;

