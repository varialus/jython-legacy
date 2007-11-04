tree grammar MiniPyWalker;

options {
    tokenVocab=MiniPy;
    ASTLabelType=CommonTree;
//    output=AST;
}

@header {
package org.python.antlr;
import java.util.HashMap;
}

@members {
HashMap locals;
}

file_input
scope {
    List symbols;
}
@init {
    $file_input::symbols = new ArrayList();
}
    : stmt+ {System.out.println("symbols="+$file_input::symbols);}
    ;

stmt : simple_stmt
     | compound_stmt
     ;

simple_stmt : small_stmt
            ;

small_stmt : expr {$file_input::symbols.add($expr.text);System.out.println("EXPR:" + $expr.text);}
           | ^(Print expr) {$file_input::symbols.add($expr.text);System.out.println("PRINT:" + $expr.text);}
           ;

compound_stmt : funcdef
              | classdef
              ;

funcdef : ^(FunctionDef NAME ^(Body suite)) //{System.out.println("FUNC " + $NAME);}
        ;

classdef: ^(ClassDef NAME ^(Body suite)) //{System.out.println("CLASS " + $NAME);}
	;

suite : (stmt)+
      ;

expr : ^(PLUS a=expr b=expr) //{System.out.println("PLUS" + $a.text + " " + $b.text);}
                         | ^(ASSIGN NAME expr) //{System.out.println("NAME" + $expr.text);}
                         | atom
                         ;
atom: NAME
    | INT
    ; 
