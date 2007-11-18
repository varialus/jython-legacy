tree grammar PythonWalker;

options {
    tokenVocab=Python;
    ASTLabelType=CommonTree;
}

@header { 
package org.python.antlr;
} 

module
    : (^(Stmt stmt))+ {
        //System.out.println("<STMT>");
    }
    ;


funcdef
    : ^(FunctionDef ^(Name NAME) ^(Args varargslist?) ^(Body suite)) {
    }
    ;

varargslist
    : ^(Arguments defparameter) {
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

stmt
    : expr_stmt
    | print_stmt
    | del_stmt
    | pass_stmt
    | flow_stmt
    | import_stmt
//    | global_stmt
//    | exec_stmt
//    | assert_stmt
    | funcdef
    | classdef
    ;

expr_stmt
    : ^(Expr testlist) {
        //System.out.println("<testlist>");
    }
    | ^(Expr ^(augassign targ=test value=test)) {
    }
    | ^(Expr ^(Assign targs=test value=test)) {
       //System.out.println("<Assign>");
       System.out.println($targs.text + " = " + $value.text);
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
    : ^(Print RIGHTSHIFT? testlist)
    {
    }
    ;

del_stmt : 'del' testlist
         ;

pass_stmt : Pass {
    }
    ;

flow_stmt : break_stmt
          | continue_stmt
          | return_stmt
//          | raise_stmt
          | yield_stmt
          ;

break_stmt : 'break'
           ;

continue_stmt : 'continue'
              ;

return_stmt : ^(Return (testlist)?)
            ;

yield_stmt : 'yield' testlist
           ;

import_stmt
    : ^(Import dotted_as_name+) {
    }
    | ^(ImportFrom dotted_name ^(Import STAR))
    | ^(ImportFrom dotted_name ^(Import import_as_name+))
    ;

import_as_name : name=NAME ('as' alias=NAME)?
               ;

dotted_as_name
    : dotted_name ('as' NAME)? {
    }
    ;

dotted_name
    : start=NAME (DOT NAME)* {
    }
    ;

suite
    : INDENT stmt+ DEDENT
    ;

//FIXME: lots of placeholders
test
: ^('and' test test) {}
    | ^('or' test test) {}
    | ^('not' test) {}
    | ^(comp_op left=test targs=test) {}
    | atom {}
    | NAME {}
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
    : ^(List testlist?) {
    }
    | ^(Tuple testlist?) {
    }
    | ^(Dict testlist?) {
    }
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

trailer: LPAREN (arglist)? RPAREN
    | LBRACK subscriptlist RBRACK
    | DOT NAME
    ;

subscriptlist
    :   subscript (options {greedy=true;}:COMMA subscript)* (COMMA)?
    ;

subscript
    : DOT DOT DOT
    | test (COLON (test)? (sliceop)?)?
    | COLON (test)? (sliceop)?
    ;

sliceop: COLON (test)?
    ;

testlist
    : test+ {}
    ;

classdef
    : ^(ClassDef ^(Name NAME) (^(Bases bases))? ^(Body suite)) {
    }
    ;

bases
    : base+ {}
    ;

base
    : NAME {} 
    ;

arglist: argument (COMMA argument)*
        ( COMMA
          ( STAR test (COMMA DOUBLESTAR test)?
          | DOUBLESTAR test
          )?
        )?
    |   STAR test (COMMA DOUBLESTAR test)?
    |   DOUBLESTAR test
    ;

argument : test (ASSIGN test)?
         ;

list_iter: list_for
    | list_if
    ;

list_for: 'for' testlist 'in' testlist (list_iter)?
    ;

list_if: 'if' test (list_iter)?
    ;

