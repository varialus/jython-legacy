/*
 [The 'BSD licence']
 Copyright (c) 2004 Terence Parr and Loring Craymer
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

grammar MiniPy;

options {
    output=AST;
}

tokens {
    INDENT;
    DEDENT;

    Name; Body;
    ClassDef;
    FunctionDef;
    Body;
    Print;
}

@header { 
package org.python.antlr;
} 

@lexer::header { 
package org.python.antlr;
} 

@lexer::members {
/** Handles context-sensitive lexing of implicit line joining such as
 *  the case where newline is ignored in cases like this:
 *  a = [3,
 *       4]
 */
int implicitLineJoiningLevel = 0;
int startPos=-1;
}

file_input : (NEWLINE! | stmt)*
           ;

stmt : simple_stmt
     | compound_stmt
     ;

simple_stmt : small_stmt NEWLINE!
            ;

small_stmt : expr
           | assignStmt
           | printStmt
           ;

compound_stmt : funcdef
              | classdef
              ;

funcdef :  'def' NAME COLON suite
        -> ^(FunctionDef NAME ^(Body suite))
        ;

classdef :  'class' NAME COLON suite
         -> ^(ClassDef NAME ^(Body suite))
         ;

suite : simple_stmt
      | NEWLINE! INDENT (stmt)+ DEDENT
      ;

assignStmt
    :   NAME ASSIGN expr
        -> ^(ASSIGN NAME expr)
    ;

printStmt
    :   'print' expr -> ^(Print expr)
    ;

expr
    :   atom ( '+'^ atom )*
    ;

atom
    : NAME      
    | INT      
    | '(' expr ')' -> expr
    ; 

NAME: ( 'a' .. 'z' | 'A' .. 'Z' | '_')
      ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )*
    ;

INT : ('0'..'9')+
    ;

ASSIGN : '=' ;

COLON : ':' ;

/** Treat a sequence of blank lines as a single blank line.  If
 *  nested within a (..), {..}, or [..], then ignore newlines.
 *  If the first newline starts in column one, they are to be ignored.
 */
NEWLINE : (('\r')? '\n' )+
          {if ( startPos==0 || implicitLineJoiningLevel>0 )
              $channel=HIDDEN;
          }
        ;

WS : {startPos>0}?=> (' '|'\t')+ {$channel=HIDDEN;}
   ;
 
/** Grab everything before a real symbol.  Then if newline, kill it
 *  as this is a blank line.  If whitespace followed by comment, kill it
 *  as it's a comment on a line by itself.
 *
 *  Ignore leading whitespace when nested in [..], (..), {..}.
 */
LEADING_WS
@init {
    int spaces = 0;
}
    :   {startPos==0}?=>
        (   {implicitLineJoiningLevel>0}? ( ' ' | '\t' )+ {$channel=HIDDEN;}
            |   ( ' '  { spaces++; }
            | '\t' { spaces += 8; spaces -= (spaces \% 8); }
            )+
            {
            // make a string of n spaces where n is column number - 1
            char[] indentation = new char[spaces];
            for (int i=0; i<spaces; i++) {
                indentation[i] = ' ';
            }
            String s = new String(indentation);
            emit(new ClassicToken(LEADING_WS,new String(indentation)));
            }
            // kill trailing newline if present and then ignore
            ( ('\r')? '\n' {if (state.token!=null) state.token.setChannel(HIDDEN); else $channel=HIDDEN;})*
           // {token.setChannel(99); }
        )
    ;
