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

/** Python 2.3.3 Grammar
 *
 *  Terence Parr and Loring Craymer
 *  February 2004
 *
 *  Converted to ANTLR v3 November 2005 by Terence Parr.
 *
 *  This grammar was derived automatically from the Python 2.3.3
 *  parser grammar to get a syntactically correct ANTLR grammar
 *  for Python.  Then Terence hand tweaked it to be semantically
 *  correct; i.e., removed lookahead issues etc...  It is LL(1)
 *  except for the (sometimes optional) trailing commas and semi-colons.
 *  It needs two symbols of lookahead in this case.
 *
 *  Starting with Loring's preliminary lexer for Python, I modified it
 *  to do my version of the whole nasty INDENT/DEDENT issue just so I
 *  could understand the problem better.  This grammar requires
 *  PythonTokenStream.java to work.  Also I used some rules from the
 *  semi-formal grammar on the web for Python (automatically
 *  translated to ANTLR format by an ANTLR grammar, naturally <grin>).
 *  The lexical rules for python are particularly nasty and it took me
 *  a long time to get it 'right'; i.e., think about it in the proper
 *  way.  Resist changing the lexer unless you've used ANTLR a lot. ;)
 *
 *  I (Terence) tested this by running it on the jython-2.1/Lib
 *  directory of 40k lines of Python.
 *
 *  REQUIRES ANTLR v3
 *
 *
 *  Updated the original parser for Python 2.5 features. The parser has been
 *  altered to produce an AST - the AST work started from tne newcompiler
 *  grammar from Jim Baker.  The current parsing and compiling strategy looks
 *  like this:
 *
 *  Python source->Python.g->AST (org/python/parser/ast/*)->CodeCompiler(ASM)->.class
 */

grammar Python;

tokens {
    INDENT;
    DEDENT;
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

single_input
    : NEWLINE* EOF
    | simple_stmt NEWLINE* EOF
    | compound_stmt NEWLINE+ EOF
    ;

file_input
    : (NEWLINE
      | stmt
      )*
    ;

eval_input
    : LEADING_WS? (NEWLINE)* testlist (NEWLINE)* EOF 
    ;

dotted_attr
    : NAME
      ( (DOT NAME)+
      |
      )
    ;

//attr is here for Java  compatibility.  A Java foo.getIf() can be called from Jython as foo.if
//     so we need to support any keyword as an attribute.
attr
    : NAME
    | AND
    | AS
    | ASSERT
    | BREAK
    | CLASS
    | CONTINUE
    | DEF
    | DELETE
    | ELIF
    | EXCEPT
    | EXEC
    | FINALLY
    | FROM
    | FOR
    | GLOBAL
    | IF
    | IMPORT
    | IN
    | IS
    | LAMBDA
    | NOT
    | OR
    | ORELSE
    | PASS
    | PRINT
    | RAISE
    | RETURN
    | TRY
    | WHILE
    | WITH
    | YIELD
    ;

decorator
    : AT dotted_attr 
    ( LPAREN
      ( arglist
      |
      )
      RPAREN
    |
    ) NEWLINE
    ;

decorators
    : decorator+
    ;

funcdef
    : decorators? DEF NAME parameters COLON suite
    ;

parameters
    : LPAREN 
      (varargslist
      |
      )
      RPAREN
    ;

defparameter
    : fpdef (ASSIGN test)?
    ;

varargslist
    : defparameter (options {greedy=true;}:COMMA defparameter)*
      (COMMA
          (STAR NAME (COMMA DOUBLESTAR NAME)?
          | DOUBLESTAR NAME
          )?
      )?
    | STAR NAME (COMMA DOUBLESTAR NAME)?
    | DOUBLESTAR NAME
    ;

fpdef
    : NAME 
    | LPAREN fplist RPAREN
    ;

fplist
    : fpdef
      (options {greedy=true;}:COMMA fpdef)* (COMMA)?
    ;

stmt
    : simple_stmt
    | compound_stmt
    ;

simple_stmt
    : small_stmt (options {greedy=true;}:SEMI small_stmt)* (SEMI)? NEWLINE
    ;

small_stmt : expr_stmt
           | print_stmt
           | del_stmt
           | pass_stmt
           | flow_stmt
           | import_stmt
           | global_stmt
           | exec_stmt
           | assert_stmt
           ;

expr_stmt 
    : ((testlist augassign) => lhs=testlist
        ( (augassign yield_expr 
          )
        | (augassign testlist
          )
        )
    | (testlist ASSIGN) => lhs=testlist
        (
        | ((ASSIGN testlist)+
          )
        | ((ASSIGN yield_expr)+
          )
        )
    | lhs=testlist
    )
    ;

augassign
    : PLUSEQUAL
    | MINUSEQUAL
    | STAREQUAL
    | SLASHEQUAL
    | PERCENTEQUAL
    | AMPEREQUAL
    | VBAREQUAL
    | CIRCUMFLEXEQUAL
    | LEFTSHIFTEQUAL
    | RIGHTSHIFTEQUAL
    | DOUBLESTAREQUAL
    | DOUBLESLASHEQUAL
    ;

print_stmt
    : PRINT 
      (printlist
      | RIGHTSHIFT printlist2
      |
      )
    ;

//not in CPython's Grammar file
printlist
    : (test COMMA) =>
       test (options {k=2;}: COMMA test)*
         (trailcomma=COMMA)?
    | test
    ;

//XXX: would be nice if printlist and printlist2 could be merged.
//not in CPython's Grammar file
printlist2
    : (test COMMA test) =>
       test (options {k=2;}: COMMA test)*
         (trailcomma=COMMA)?
    | test
    ;

del_stmt
    : DELETE del_list
    ;

pass_stmt
    : PASS
    ;

flow_stmt
    : break_stmt
    | continue_stmt
    | return_stmt
    | raise_stmt
    | yield_stmt
    ;

break_stmt
    : BREAK
    ;

continue_stmt
    : CONTINUE
    ;

return_stmt
    : RETURN 
      (testlist
      |
      )
      ;

yield_stmt
    : yield_expr
    ;

raise_stmt
    : RAISE (test (COMMA test
        (COMMA test)?)?)?
    ;

import_stmt
    : import_name
    | import_from
    ;

import_name
    : IMPORT dotted_as_names
    ;

import_from
    : FROM (DOT* dotted_name | DOT+) IMPORT 
        (STAR
        | import_as_names
        | LPAREN import_as_names COMMA? RPAREN
        )
    ;

import_as_names
    : import_as_name (COMMA import_as_name)*
    ;

import_as_name
    : name=NAME (AS asname=NAME)?
    ;

dotted_as_name
    : dotted_name (AS NAME)?
    ;

dotted_as_names
    : dotted_as_name (COMMA dotted_as_name)*
    ;

dotted_name
    : NAME (DOT attr)*
    ;

global_stmt
    : GLOBAL NAME (COMMA NAME)*
    ;

exec_stmt
    : EXEC expr (IN test
        (COMMA test)?)?
    ;

assert_stmt
    : ASSERT test (COMMA test)?
    ;

compound_stmt
    : if_stmt
    | while_stmt
    | for_stmt
    | try_stmt
    | with_stmt
    | funcdef
    | classdef
    ;

if_stmt
    : IF test COLON suite elif_clause*
        (ORELSE COLON suite)?
    ;

//not in CPython's Grammar file
elif_clause
    : ELIF test COLON suite
    ;

while_stmt
    : WHILE test COLON suite (ORELSE COLON suite)?
    ;

for_stmt
    : FOR exprlist IN testlist COLON suite
        (ORELSE COLON suite)?
    ;

try_stmt
    : TRY COLON suite
      ( except_clause+ (ORELSE COLON suite)? (FINALLY COLON suite)?
      | FINALLY COLON suite
      )
      ;

with_stmt
    : WITH test (with_var)? COLON suite
    ;

with_var
    : (AS | NAME) expr
    ;

except_clause
    : EXCEPT (test (COMMA test)?)? COLON suite
    ;

suite
    : simple_stmt
    | NEWLINE INDENT
      (stmt
      )+ DEDENT
    ;

test
    :or_test
      ( (IF or_test ORELSE) => IF o2=or_test ORELSE e=test
      |
      )
    | lambdef
    ;

or_test
    : left=and_test
        ( (OR and_test
          )+
        |
        )
    ;

and_test
    : not_test
        ( (AND not_test
          )+
        |
        )
    ;

not_test
    : NOT nt=not_test
    | comparison
    ;

comparison
    : left=expr
       ( ( comp_op expr
         )+
       |
       )
    ;

comp_op
    : LESS
    | GREATER
    | EQUAL
    | GREATEREQUAL
    | LESSEQUAL
    | ALT_NOTEQUAL
    | NOTEQUAL
    | IN
    | NOT IN
    | IS
    | IS NOT
    ;

expr
    : left=xor_expr
        ( (VBAR xor_expr
          )+
        |
        )
    ;

xor_expr
    : left=and_expr
        ( (CIRCUMFLEX and_expr
          )+
        |
        )
    ;

and_expr
    : shift_expr
        ( (AMPER shift_expr
          )+
        |
        )
    ;

shift_expr
    : left=arith_expr
        ( ( shift_op arith_expr
          )+
        |
        )
    ;

shift_op
    : LEFTSHIFT
    | RIGHTSHIFT
    ;

arith_expr
    : left=term
        ( (arith_op term
          )+
        |
        )
    ;

arith_op
    : PLUS
    | MINUS
    ;

term
    : factor
        ( (term_op factor
          )+
        |
        )
    ;

term_op
    :STAR
    |SLASH
    |PERCENT
    |DOUBLESLASH
    ;

factor
    : PLUS factor
    | MINUS factor
    | TILDE factor
    | power
    ;

power
    : atom (trailer)* (options {greedy=true;}:DOUBLESTAR factor)?
    ;

atom
    : LPAREN 
      ( yield_expr
      | testlist_gexp
      |
      )
      RPAREN
    | LBRACK
      (listmaker
      |
      )
      RBRACK
    | LCURLY 
       (dictmaker
       |
       )
       RCURLY
     | BACKQUOTE testlist BACKQUOTE
     | NAME
     | INT
     | LONGINT
     | FLOAT
     | COMPLEX
     | (STRING)+ 
     ;

listmaker
    : test 
        (list_for
        | (options {greedy=true;}:COMMA test)*
        ) (COMMA)?
          ;

testlist_gexp
    : test
        ( ((options {k=2;}: COMMA test)* (COMMA)?
          )
        | (gen_for
          )
        )
    ;

lambdef
    : LAMBDA (varargslist)? COLON test
    ;

trailer
    : LPAREN 
        (arglist
        |
        )
      RPAREN
    | LBRACK subscriptlist RBRACK
    | DOT attr
    ;

subscriptlist
    : subscript (options {greedy=true;}:COMMA subscript)* (COMMA)?
    ;

subscript
    : DOT DOT DOT
    | (test COLON)
   => test (COLON (test)? (sliceop)?)?
    | (COLON)
   => COLON (test)? (sliceop)?
    | test
    ;

sliceop
    : COLON
      (test
      )?
    ;

exprlist
    : (expr COMMA) => expr (options {k=2;}: COMMA expr)* (COMMA)?
    | expr
    ;

//not in CPython's Grammar file
del_list
    : expr (options {k=2;}: COMMA expr)* (COMMA)?
    ;

testlist
    : (test COMMA)
   => test (options {k=2;}: COMMA test)* (COMMA)?
    | test
    ;

dictmaker
    : test COLON test
        (options {k=2;}:COMMA test COLON test)*
        (COMMA)?
    ;

classdef
    : CLASS NAME (LPAREN testlist? RPAREN)? COLON suite
    ;

arglist
    : argument (COMMA argument)*
          (COMMA
              ( STAR test (COMMA DOUBLESTAR test)?
              | DOUBLESTAR test
              )?
          )?
    | STAR test (COMMA DOUBLESTAR test)?
    | DOUBLESTAR test
    ;

argument
    : t1=test
        ((ASSIGN t2=test)
        | gen_for
        |
        )
    ;

list_iter
    : list_for
    | list_if
    ;

list_for
    : FOR exprlist IN testlist (list_iter)?
    ;

list_if
    : IF test (list_iter)?
    ;

gen_iter
    : gen_for
    | gen_if
    ;

gen_for
    : FOR exprlist IN or_test gen_iter?
    ;

gen_if
    : IF test gen_iter?
    ;

yield_expr
    : YIELD testlist?
    ;

AS        : 'as' ;
ASSERT    : 'assert' ;
BREAK     : 'break' ;
CLASS     : 'class' ;
CONTINUE  : 'continue' ;
DEF       : 'def' ;
DELETE    : 'del' ;
ELIF      : 'elif' ;
EXCEPT    : 'except' ;
EXEC      : 'exec' ;
FINALLY   : 'finally' ;
FROM      : 'from' ;
FOR       : 'for' ;
GLOBAL    : 'global' ;
IF        : 'if' ;
IMPORT    : 'import' ;
IN        : 'in' ;
IS        : 'is' ;
LAMBDA    : 'lambda' ;
ORELSE    : 'else' ;
PASS      : 'pass'  ;
PRINT     : 'print' ;
RAISE     : 'raise' ;
RETURN    : 'return' ;
TRY       : 'try' ;
WHILE     : 'while' ;
WITH      : 'with' ;
YIELD     : 'yield' ;

LPAREN    : '(' {implicitLineJoiningLevel++;} ;

RPAREN    : ')' {implicitLineJoiningLevel--;} ;

LBRACK    : '[' {implicitLineJoiningLevel++;} ;

RBRACK    : ']' {implicitLineJoiningLevel--;} ;

COLON     : ':' ;

COMMA    : ',' ;

SEMI    : ';' ;

PLUS    : '+' ;

MINUS    : '-' ;

STAR    : '*' ;

SLASH    : '/' ;

VBAR    : '|' ;

AMPER    : '&' ;

LESS    : '<' ;

GREATER    : '>' ;

ASSIGN    : '=' ;

PERCENT    : '%' ;

BACKQUOTE    : '`' ;

LCURLY    : '{' {implicitLineJoiningLevel++;} ;

RCURLY    : '}' {implicitLineJoiningLevel--;} ;

CIRCUMFLEX    : '^' ;

TILDE    : '~' ;

EQUAL    : '==' ;

NOTEQUAL    : '!=' ;

ALT_NOTEQUAL: '<>' ;

LESSEQUAL    : '<=' ;

LEFTSHIFT    : '<<' ;

GREATEREQUAL    : '>=' ;

RIGHTSHIFT    : '>>' ;

PLUSEQUAL    : '+=' ;

MINUSEQUAL    : '-=' ;

DOUBLESTAR    : '**' ;

STAREQUAL    : '*=' ;

DOUBLESLASH    : '//' ;

SLASHEQUAL    : '/=' ;

VBAREQUAL    : '|=' ;

PERCENTEQUAL    : '%=' ;

AMPEREQUAL    : '&=' ;

CIRCUMFLEXEQUAL    : '^=' ;

LEFTSHIFTEQUAL    : '<<=' ;

RIGHTSHIFTEQUAL    : '>>=' ;

DOUBLESTAREQUAL    : '**=' ;

DOUBLESLASHEQUAL    : '//=' ;

DOT : '.' ;

AT : '@' ;

AND : 'and' ;

OR : 'or' ;

NOT : 'not' ;

FLOAT
    :   '.' DIGITS (Exponent)?
    |   DIGITS '.' Exponent
    |   DIGITS ('.' (DIGITS (Exponent)?)? | Exponent)
    ;

LONGINT
    :   INT ('l'|'L')
    ;

fragment
Exponent
    :    ('e' | 'E') ( '+' | '-' )? DIGITS
    ;

INT :   // Hex
        '0' ('x' | 'X') ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )+
    |   // Octal
        '0'  ( '0' .. '7' )*
    |   '1'..'9' DIGITS*
    ;

COMPLEX
    :   DIGITS+ ('j'|'J')
    |   FLOAT ('j'|'J')
    ;

fragment
DIGITS : ( '0' .. '9' )+ ;

NAME:    ( 'a' .. 'z' | 'A' .. 'Z' | '_')
        ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )*
    ;

/** Match various string types.  Note that greedy=false implies '''
 *  should make us exit loop not continue.
 */
STRING
    :   ('r'|'u'|'ur'|'R'|'U'|'UR'|'uR'|'Ur')?
        (   '\'\'\'' (options {greedy=false;}:TRIAPOS)* '\'\'\''
        |   '"""' (options {greedy=false;}:TRIQUOTE)* '"""'
        |   '"' (ESC|~('\\'|'\n'|'"'))* '"'
        |   '\'' (ESC|~('\\'|'\n'|'\''))* '\''
        ) {
           if (state.tokenStartLine != input.getLine()) {
               state.tokenStartLine = input.getLine();
               state.tokenStartCharPositionInLine = -2;
           }
        }
    ;

/** the two '"'? cause a warning -- is there a way to avoid that? */
fragment
TRIQUOTE
    : '"'? '"'? (ESC|~('\\'|'"'))+
    ;

/** the two '\''? cause a warning -- is there a way to avoid that? */
fragment
TRIAPOS
    : '\''? '\''? (ESC|~('\\'|'\''))+
    ;

fragment
ESC
    :    '\\' .
    ;

/** Consume a newline and any whitespace at start of next line
 *  unless the next line contains only white space, in that case
 *  emit a newline.
 */
CONTINUED_LINE
    :    '\\' ('\r')? '\n' (' '|'\t')*  { $channel=HIDDEN; }
         ( nl=NEWLINE {emit(new CommonToken(NEWLINE,nl.getText()));}
         |
         )
    ;

/** Treat a sequence of blank lines as a single blank line.  If
 *  nested within a (..), {..}, or [..], then ignore newlines.
 *  If the first newline starts in column one, they are to be ignored.
 *
 *  Frank Wierzbicki added: Also ignore FORMFEEDS (\u000C).
 */
NEWLINE
@init {
    int newlines = 0;
}
    :   (('\u000C')?('\r')? '\n' {newlines++; } )+ {
         if ( startPos==0 || implicitLineJoiningLevel>0 )
            $channel=HIDDEN;
        }
    ;

WS  :    {startPos>0}?=> (' '|'\t'|'\u000C')+ {$channel=HIDDEN;}
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
    int newlines = 0;
}
    :   {startPos==0}?=>
        (   {implicitLineJoiningLevel>0}? ( ' ' | '\t' )+ {$channel=HIDDEN;}
        |    (     ' '  { spaces++; }
             |    '\t' { spaces += 8; spaces -= (spaces \% 8); }
             )+
             ( ('\r')? '\n' {newlines++; }
             )* {
                   if (input.LA(1) != -1) {
                       // make a string of n spaces where n is column number - 1
                       char[] indentation = new char[spaces];
                       for (int i=0; i<spaces; i++) {
                           indentation[i] = ' ';
                       }
                       CommonToken c = new CommonToken(LEADING_WS,new String(indentation));
                       c.setLine(input.getLine());
                       c.setCharPositionInLine(input.getCharPositionInLine());
                       emit(c);
                       // kill trailing newline if present and then ignore
                       if (newlines != 0) {
                           if (state.token!=null) {
                               state.token.setChannel(HIDDEN);
                           } else {
                               $channel=HIDDEN;
                           }
                       }
                   } else {
                       // make a string of n newlines
                       char[] nls = new char[newlines];
                       for (int i=0; i<newlines; i++) {
                           nls[i] = '\n';
                       }
                       emit(new CommonToken(NEWLINE,new String(nls)));
                   }
                }
        )
    ;

/** Comments not on line by themselves are turned into newlines.

    b = a # end of line comment

    or

    a = [1, # weird
         2]

    This rule is invoked directly by nextToken when the comment is in
    first column or when comment is on end of nonwhitespace line.

    Only match \n here if we didn't start on left edge; let NEWLINE return that.
    Kill if newlines if we live on a line by ourselves
    
    Consume any leading whitespace if it starts on left edge.
 */
COMMENT
@init {
    $channel=HIDDEN;
}
    :    {startPos==0}?=> (' '|'\t')* '#' (~'\n')* '\n'+
    |    '#' (~'\n')* // let NEWLINE handle \n unless char pos==0 for '#'
    ;

