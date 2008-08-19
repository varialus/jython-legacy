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
 *  Python source->Python.g->simple antlr AST->PythonWalker.g->
 *  decorated AST (org/python/parser/ast/*)->CodeCompiler(ASM)->.class
 */

grammar Python;
options {
    ASTLabelType=PythonTree;
    output=AST;
}

tokens {
    INDENT;
    DEDENT;

    PYNODE;
    Interactive;
    Expression;
    Test;
    Body;
    Arg;
    AugAssign;
    Dict;
    IfExp;
    Ellipsis;
    ListComp;
    Repr;
    Index;
    Target;
    Step;
    GeneratorExp;
    Ifs;
    Elts;

    StepOp;

    GenFor;
    GenIf;
    ListFor;
    ListIf;

}

@header {
package org.python.antlr;

import org.antlr.runtime.CommonToken;

import org.python.antlr.ParseException;
import org.python.antlr.PythonTree;
import org.python.antlr.ast.aliasType;
import org.python.antlr.ast.argumentsType;
import org.python.antlr.ast.Assert;
import org.python.antlr.ast.Assign;
import org.python.antlr.ast.Attribute;
import org.python.antlr.ast.AugAssign;
import org.python.antlr.ast.BinOp;
import org.python.antlr.ast.BoolOp;
import org.python.antlr.ast.boolopType;
import org.python.antlr.ast.Break;
import org.python.antlr.ast.Call;
import org.python.antlr.ast.ClassDef;
import org.python.antlr.ast.cmpopType;
import org.python.antlr.ast.Compare;
import org.python.antlr.ast.comprehensionType;
import org.python.antlr.ast.Context;
import org.python.antlr.ast.Continue;
import org.python.antlr.ast.Delete;
import org.python.antlr.ast.excepthandlerType;
import org.python.antlr.ast.Exec;
import org.python.antlr.ast.Expr;
import org.python.antlr.ast.exprType;
import org.python.antlr.ast.expr_contextType;
import org.python.antlr.ast.ExtSlice;
import org.python.antlr.ast.For;
import org.python.antlr.ast.FunctionDef;
import org.python.antlr.ast.Global;
import org.python.antlr.ast.If;
import org.python.antlr.ast.Import;
import org.python.antlr.ast.ImportFrom;
import org.python.antlr.ast.Index;
import org.python.antlr.ast.keywordType;
import org.python.antlr.ast.Lambda;
import org.python.antlr.ast.modType;
import org.python.antlr.ast.Module;
import org.python.antlr.ast.Name;
import org.python.antlr.ast.Num;
import org.python.antlr.ast.Pass;
import org.python.antlr.ast.Print;
import org.python.antlr.ast.Raise;
import org.python.antlr.ast.operatorType;
import org.python.antlr.ast.Return;
import org.python.antlr.ast.Slice;
import org.python.antlr.ast.sliceType;
import org.python.antlr.ast.stmtType;
import org.python.antlr.ast.Str;
import org.python.antlr.ast.Subscript;
import org.python.antlr.ast.TryExcept;
import org.python.antlr.ast.TryFinally;
import org.python.antlr.ast.Tuple;
import org.python.antlr.ast.unaryopType;
import org.python.antlr.ast.UnaryOp;
import org.python.antlr.ast.While;
import org.python.antlr.ast.With;
import org.python.antlr.ast.Yield;
import org.python.core.Py;
import org.python.core.PyString;
import org.python.core.PyUnicode;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.ListIterator;
} 

@members {
    boolean debugOn = false;

    private ErrorHandler errorHandler;

    private boolean seenSingleOuterSuite = false;

    private GrammarActions actions = new GrammarActions();

    public void setErrorHandler(ErrorHandler eh) {
        this.errorHandler = eh;
        actions.setErrorHandler(eh);
    }

    private void debug(String message) {
        if (debugOn) {
            System.out.println(message);
        }
    }

    protected void mismatch(IntStream input, int ttype, BitSet follow) throws RecognitionException {
        if (errorHandler.isRecoverable()) {
            super.mismatch(input, ttype, follow);
        } else {
            throw new MismatchedTokenException(ttype, input);
        }
    }

    protected Object recoverFromMismatchedToken(IntStream input, int ttype, BitSet follow)
        throws RecognitionException
    {
        if (errorHandler.isRecoverable()) {
            return super.recoverFromMismatchedToken(input, ttype, follow);
        }
        mismatch(input, ttype, follow);
        return null;
    }

}

@rulecatch {
catch (RecognitionException re) {
    errorHandler.reportError(this, re);
    errorHandler.recover(this, input,re);
    retval.tree = (PythonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
}
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

//For use in partial parsing.
public boolean eofWhileNested = false;
public boolean partial = false;

int implicitLineJoiningLevel = 0;
int startPos=-1;

//If you want to use another error recovery mechanisms change this
//and the same one in the parser.
private ErrorHandler errorHandler;

    public void setErrorHandler(ErrorHandler eh) {
        this.errorHandler = eh;
    }

    /** 
     *  Taken directly from antlr's Lexer.java -- needs to be re-integrated every time
     *  we upgrade from Antlr (need to consider a Lexer subclass, though the issue would
     *  remain).
     */
    public Token nextToken() {
        while (true) {
            state.token = null;
            state.channel = Token.DEFAULT_CHANNEL;
            state.tokenStartCharIndex = input.index();
            state.tokenStartCharPositionInLine = input.getCharPositionInLine();
            state.tokenStartLine = input.getLine();
            state.text = null;
            if ( input.LA(1)==CharStream.EOF ) {
                if (implicitLineJoiningLevel > 0) {
                    eofWhileNested = true;
                }
                return Token.EOF_TOKEN;
            }
            try {
                mTokens();
                if ( state.token==null ) {
                    emit();
                }
                else if ( state.token==Token.SKIP_TOKEN ) {
                    continue;
                }
                return state.token;
            } catch (NoViableAltException nva) {
                errorHandler.reportError(this, nva);
                errorHandler.recover(this, nva); // throw out current char and try again
            } catch (RecognitionException re) {
                errorHandler.reportError(this, re);
                // match() routine has already called recover()
            }
        }
    }
}

//single_input: NEWLINE | simple_stmt | compound_stmt NEWLINE
single_input : NEWLINE* EOF -> ^(Interactive)
             | simple_stmt NEWLINE* EOF -> ^(Interactive simple_stmt)
             | compound_stmt NEWLINE+ EOF -> ^(Interactive compound_stmt)
             ;

//file_input: (NEWLINE | stmt)* ENDMARKER
file_input
    : (NEWLINE | s+=stmt)+ -> ^(PYNODE<Module>[$file_input.start, actions.makeStmts($s)])
    | -> ^(PYNODE<Module>[$file_input.start, new stmtType[0\]])
    ;

//eval_input: testlist NEWLINE* ENDMARKER
eval_input : LEADING_WS? (NEWLINE)* testlist[expr_contextType.Load] (NEWLINE)* EOF -> ^(Expression testlist)
           ;

//not in CPython's Grammar file
dotted_attr returns [exprType etype]
    : n1=NAME
      ( (DOT n2+=dotted_attr)+ { $etype = actions.makeDottedAttr($n1, $n2); }
      | { $etype = new Name($NAME, $NAME.text, expr_contextType.Load); }
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

//decorator: '@' dotted_name [ '(' [arglist] ')' ] NEWLINE
decorator returns [exprType etype]
@after {
   $decorator.tree = $etype;
}
    : AT dotted_attr 
    //XXX: ignoring the arglist and Call generation right now.
    ( LPAREN (arglist
        {$etype = new Call($LPAREN, $dotted_attr.etype, actions.makeExprs($arglist.args),
                  actions.makeKeywords($arglist.keywords), $arglist.starargs, $arglist.kwargs);}
             | {$etype = $dotted_attr.etype;}
             )
      RPAREN
    // ^(Decorator dotted_attr ^(CallTok ^(Args arglist)?))
    | { $etype = $dotted_attr.etype; }
    ) NEWLINE
    ;

//decorators: decorator+
decorators returns [List etypes]
    : d+=decorator+ {$etypes = $d;}
    ;

//funcdef: [decorators] 'def' NAME parameters ':' suite
funcdef : decorators? DEF NAME parameters COLON suite
       -> ^(DEF<FunctionDef>[$DEF, $NAME.text, $parameters.args, actions.makeStmts($suite.stmts),
            actions.makeExprs($decorators.etypes)])
        ;

//parameters: '(' [varargslist] ')'
parameters returns [argumentsType args]
    : LPAREN 
      (varargslist {$args = $varargslist.args;}
      | {$args = new argumentsType($parameters.start, new exprType[0], null, null, new exprType[0]);}
      )
      RPAREN
    ;

//not in CPython's Grammar file
defparameter[List defaults] returns [exprType etype]
@after {
   $defparameter.tree = $etype;
}
    : fpdef[expr_contextType.Param] (ASSIGN test[expr_contextType.Load])? {
        $etype = (exprType)$fpdef.tree;
        if ($ASSIGN != null) {
            defaults.add($test.tree);
        } else if (!defaults.isEmpty()) {
            throw new ParseException(
                "non-default argument follows default argument",
                $fpdef.tree);
        }
    }
;

//varargslist: ((fpdef ['=' test] ',')*
//              ('*' NAME [',' '**' NAME] | '**' NAME) |
//              fpdef ['=' test] (',' fpdef ['=' test])* [','])
varargslist returns [argumentsType args]
@init {
    List defaults = new ArrayList();
}
    : d+=defparameter[defaults] (options {greedy=true;}:COMMA d+=defparameter[defaults])*
        (COMMA
            ( STAR starargs=NAME (COMMA DOUBLESTAR kwargs=NAME)?
            | DOUBLESTAR kwargs=NAME
            )?
        )?
        {$args = actions.makeArgumentsType($varargslist.start, $d, $starargs, $kwargs, defaults);}
        | STAR starargs=NAME (COMMA DOUBLESTAR kwargs=NAME)?{debug("parsed varargslist STARARGS");}
        {$args = actions.makeArgumentsType($varargslist.start, $d, $starargs, $kwargs, defaults);}
        | DOUBLESTAR kwargs=NAME {debug("parsed varargslist KWS");}
        {$args = actions.makeArgumentsType($varargslist.start, $d, null, $kwargs, defaults);}
        ;

//fpdef: NAME | '(' fplist ')'
fpdef[expr_contextType ctype] : NAME 
     -> ^(PYNODE<Name>[$NAME, $NAME.text, ctype])
      | (LPAREN fpdef[expr_contextType.Load] COMMA) => LPAREN fplist RPAREN
     -> fplist
      | LPAREN fplist RPAREN
     -> ^(LPAREN<Tuple>[$fplist.start, actions.makeExprs($fplist.etypes), expr_contextType.Store])
      ;

//fplist: fpdef (',' fpdef)* [',']
fplist returns [List etypes]
    : f+=fpdef[expr_contextType.Store] (options {greedy=true;}:COMMA f+=fpdef[expr_contextType.Store])* (COMMA)?
      {$etypes = $f;}
    ;

//stmt: simple_stmt | compound_stmt
stmt : simple_stmt
     | compound_stmt
     ;

//simple_stmt: small_stmt (';' small_stmt)* [';'] NEWLINE
simple_stmt : small_stmt (options {greedy=true;}:SEMI small_stmt)* (SEMI)? NEWLINE
           -> small_stmt+
            ;
//small_stmt: (expr_stmt | print_stmt  | del_stmt | pass_stmt | flow_stmt |
//             import_stmt | global_stmt | exec_stmt | assert_stmt)
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

//expr_stmt: testlist (augassign (yield_expr|testlist) |
//                     ('=' (yield_expr|testlist))*)
expr_stmt 
@init {
    stmtType stype = null;
}
@after {
    if (stype != null) {
        $expr_stmt.tree = stype;
    }
}

    :
    ((testlist[expr_contextType.Load] augassign) => lhs=testlist[expr_contextType.AugStore]
        ( (aay=augassign y1=yield_expr {stype = new AugAssign($lhs.tree, (exprType)$lhs.tree, $aay.op, (exprType)$y1.tree);})
        | (aat=augassign rhs=testlist[expr_contextType.Load] {stype = new AugAssign($lhs.tree, (exprType)$lhs.tree, $aat.op, (exprType)$rhs.tree);})
        )
    |(testlist[expr_contextType.Load] ASSIGN) => lhs=testlist[expr_contextType.Store]
        (
        | ((at=ASSIGN t+=testlist[expr_contextType.Store])+ -> ^(PYNODE<Assign>[$at, actions.makeAssignTargets((exprType)$lhs.tree, $t), actions.makeAssignValue($t)]))
        | ((ay=ASSIGN y2+=yield_expr)+ -> ^(PYNODE<Assign>[$ay, actions.makeAssignTargets((exprType)$lhs.tree, $y2), actions.makeAssignValue($y2)]))
        )
    | lhs=testlist[expr_contextType.Load] -> PYNODE<Expr>[$lhs.start, (exprType)$lhs.tree]
    )
    ;

//augassign: ('+=' | '-=' | '*=' | '/=' | '%=' | '&=' | '|=' | '^=' |
//            '<<=' | '>>=' | '**=' | '//=')
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

//print_stmt: 'print' ( [ test (',' test)* [','] ] |
//                      '>>' test [ (',' test)+ [','] ] )
print_stmt : PRINT 
             ( t1=printlist
            -> ^(PRINT<Print>[$PRINT, null, actions.makeExprs($t1.elts), $t1.newline])
             | RIGHTSHIFT t2=printlist2
            -> ^(PRINT<Print>[$PRINT, (exprType)$t2.elts.get(0), actions.makeExprs($t2.elts, 1), $t2.newline])
             |
            -> ^(PRINT<Print>[$PRINT, null, new exprType[0\], false])
             )
           ;

//not in CPython's Grammar file
printlist returns [boolean newline, List elts]
    : (test[expr_contextType.Load] COMMA) =>
    t+=test[expr_contextType.Load] (options {k=2;}: COMMA t+=test[expr_contextType.Load])* (trailcomma=COMMA)?
    { $elts=$t;
         if ($trailcomma == null) {
            $newline = true;
      } else {
           $newline = false;
      }
    }
    | t+=test[expr_contextType.Load] {
        $elts=$t;
        $newline = true;
    }
    ;

//XXX: would be nice if printlist and printlist2 could be merged.
//not in CPython's Grammar file
printlist2 returns [boolean newline, List elts]
    : (test[expr_contextType.Load] COMMA test[expr_contextType.Load]) =>
    t+=test[expr_contextType.Load] (options {k=2;}: COMMA t+=test[expr_contextType.Load])* (trailcomma=COMMA)?
    { $elts=$t;
         if ($trailcomma == null) {
            $newline = true;
      } else {
           $newline = false;
      }
    }
    | t+=test[expr_contextType.Load] {
        $elts=$t;
        $newline = true;
    }
    ;


//del_stmt: 'del' exprlist
del_stmt : DELETE exprlist2
        -> ^(DELETE<Delete>[$DELETE, actions.makeExprs($exprlist2.etypes)])
         ;

//pass_stmt: 'pass'
pass_stmt : PASS
         -> ^(PASS<Pass>[$PASS])
          ;

//flow_stmt: break_stmt | continue_stmt | return_stmt | raise_stmt | yield_stmt
flow_stmt : break_stmt
          | continue_stmt
          | return_stmt
          | raise_stmt
          | yield_stmt
          ;

//break_stmt: 'break'
break_stmt : BREAK
          -> ^(BREAK<Break>[$BREAK])
           ;

//continue_stmt: 'continue'
continue_stmt : CONTINUE
             -> ^(CONTINUE<Continue>[$CONTINUE])
              ;

//return_stmt: 'return' [testlist]
return_stmt : RETURN
              (testlist[expr_contextType.Load] -> ^(RETURN<Return>[$RETURN, (exprType)$testlist.tree])
              | -> ^(RETURN<Return>[$RETURN, null])
              )
            ;

//yield_stmt: yield_expr
yield_stmt : yield_expr -> ^(PYNODE<Expr>[$yield_expr.start, (exprType)$yield_expr.tree])
           ;

//raise_stmt: 'raise' [test [',' test [',' test]]]
raise_stmt: RAISE (t1=test[expr_contextType.Load] (COMMA t2=test[expr_contextType.Load] (COMMA t3=test[expr_contextType.Load])?)?)?
          -> ^(RAISE<Raise>[$RAISE, (exprType)$t1.tree, (exprType)$t2.tree, (exprType)$t3.tree])
          ;

//import_stmt: import_name | import_from
import_stmt : import_name
            | import_from
            ;

//import_name: 'import' dotted_as_names
import_name : IMPORT dotted_as_names
           -> ^(IMPORT<Import>[$IMPORT, $dotted_as_names.atypes])
            ;

//import_from: ('from' ('.'* dotted_name | '.'+)
//              'import' ('*' | '(' import_as_names ')' | import_as_names))
import_from: FROM (d+=DOT* dotted_name | d+=DOT+) IMPORT 
              (STAR
             -> ^(FROM<ImportFrom>[$FROM, actions.makeFromText($d, $dotted_name.text), actions.makeStarAlias($STAR), actions.makeLevel($d)])
              | i1=import_as_names
             -> ^(FROM<ImportFrom>[$FROM, actions.makeFromText($d, $dotted_name.text), actions.makeAliases($i1.atypes), actions.makeLevel($d)])
              | LPAREN i2=import_as_names COMMA? RPAREN
             -> ^(FROM<ImportFrom>[$FROM, actions.makeFromText($d, $dotted_name.text), actions.makeAliases($i2.atypes), actions.makeLevel($d)])
              )
           ;

//import_as_names: import_as_name (',' import_as_name)* [',']
import_as_names returns [aliasType[\] atypes]
    : n+=import_as_name (COMMA! n+=import_as_name)* {
        $atypes = (aliasType[])$n.toArray(new aliasType[$n.size()]);
    }
    ;

//import_as_name: NAME [('as' | NAME) NAME]
import_as_name returns [aliasType atype]
@after {
    $import_as_name.tree = $atype;
}
    : name=NAME (AS asname=NAME)? {
        $atype = new aliasType($name, $name.text, $asname.text);
    }
    ;

//XXX: when does CPython Grammar match "dotted_name NAME NAME"?
//dotted_as_name: dotted_name [('as' | NAME) NAME]
dotted_as_name returns [aliasType atype]
@after {
    $dotted_as_name.tree = $atype;
}

    : dotted_name (AS NAME)? {
        $atype = new aliasType($NAME, $dotted_name.text, $NAME.text);
    }
    ;

//dotted_as_names: dotted_as_name (',' dotted_as_name)*
dotted_as_names returns [aliasType[\] atypes]
    : d+=dotted_as_name (COMMA! d+=dotted_as_name)* {
        $atypes = (aliasType[])$d.toArray(new aliasType[$d.size()]);
    }
    ;

//dotted_name: NAME ('.' NAME)*
dotted_name : NAME (DOT attr)*
            ;

//global_stmt: 'global' NAME (',' NAME)*
global_stmt : GLOBAL n+=NAME (COMMA n+=NAME)*
           -> ^(GLOBAL<Global>[$GLOBAL, actions.makeNames($n)])
            ;

//exec_stmt: 'exec' expr ['in' test [',' test]]
exec_stmt
@init {
    stmtType stype = null;
}
@after {
   $exec_stmt.tree = stype;
}
    : EXEC expr[expr_contextType.Load] (IN t1=test[expr_contextType.Load] (COMMA t2=test[expr_contextType.Load])?)? {
         stype = new Exec($expr.start, (exprType)$expr.tree, (exprType)$t1.tree, (exprType)$t2.tree);
    }
    ;

//assert_stmt: 'assert' test [',' test]
assert_stmt : ASSERT t1=test[expr_contextType.Load] (COMMA t2=test[expr_contextType.Load])?
           -> ^(ASSERT<Assert>[$ASSERT, (exprType)$t1.tree, (exprType)$t2.tree])
            ;

//compound_stmt: if_stmt | while_stmt | for_stmt | try_stmt | funcdef | classdef
compound_stmt : if_stmt
              | while_stmt
              | for_stmt
              | try_stmt
              | with_stmt
              | funcdef
              | classdef
              ;

//if_stmt: 'if' test ':' suite ('elif' test ':' suite)* ['else' ':' suite]
if_stmt: IF test[expr_contextType.Load] COLON ifsuite=suite elifs+=elif_clause*  (ORELSE COLON elsesuite=suite)?
      -> ^(IF<If>[$IF, (exprType)$test.tree, actions.makeStmts($ifsuite.stmts), actions.makeElses($elsesuite.stmts, $elifs)])
       ;

//not in CPython's Grammar file
elif_clause : ELIF test[expr_contextType.Load] COLON suite
           -> ^(ELIF<If>[$ELIF, (exprType)$test.tree, actions.makeStmts($suite.stmts), new stmtType[0\]])
            ;

//while_stmt: 'while' test ':' suite ['else' ':' suite]
while_stmt
@init {
    stmtType stype = null;
}
@after {
   $while_stmt.tree = stype;
}
    : WHILE test[expr_contextType.Load] COLON s1=suite (ORELSE COLON s2=suite)? {
        stype = actions.makeWhile($WHILE, (exprType)$test.tree, $s1.stmts, $s2.stmts);
    }
    ;

//for_stmt: 'for' exprlist 'in' testlist ':' suite ['else' ':' suite]
for_stmt
@init {
    stmtType stype = null;
}
@after {
   $for_stmt.tree = stype;
}
    : FOR exprlist[expr_contextType.Store] IN testlist[expr_contextType.Load] COLON s1=suite (ORELSE COLON s2=suite)? {
        stype = actions.makeFor($FOR, $exprlist.etype, (exprType)$testlist.tree, $s1.stmts, $s2.stmts);
    }
    ;

//try_stmt: ('try' ':' suite
//           ((except_clause ':' suite)+
//           ['else' ':' suite]
//           ['finally' ':' suite] |
//           'finally' ':' suite))
try_stmt
@init {
    stmtType stype = null;
}
@after {
   $try_stmt.tree = stype;
}
    : TRY COLON trysuite=suite
        ( e+=except_clause+ (ORELSE COLON elsesuite=suite)? (FINALLY COLON finalsuite=suite)? {
            stype = actions.makeTryExcept($TRY, $trysuite.stmts, $e, $elsesuite.stmts, $finalsuite.stmts);
        }
        | FINALLY COLON finalsuite=suite {
            stype = actions.makeTryFinally($TRY, $trysuite.stmts, $finalsuite.stmts);
        }
        )
        ;

//with_stmt: 'with' test [ with_var ] ':' suite
with_stmt
@init {
    stmtType stype = null;
}
@after {
   $with_stmt.tree = stype;
}
    :WITH test[expr_contextType.Load] (with_var)? COLON suite {
        stype = new With($WITH, (exprType)$test.tree, $with_var.etype, actions.makeStmts($suite.stmts));
    }
    ;

//with_var: ('as' | NAME) expr
with_var returns [exprType etype]
    : (AS | NAME) expr[expr_contextType.Load] {
        $etype = (exprType)$expr.tree;
    }
    ;

//except_clause: 'except' [test [',' test]]
except_clause : EXCEPT (t1=test[expr_contextType.Load] (COMMA t2=test[expr_contextType.Load])?)? COLON suite
             -> ^(EXCEPT<excepthandlerType>[$EXCEPT, (exprType)$t1.tree, (exprType)$t2.tree, actions.makeStmts($suite.stmts), $EXCEPT.getLine(), $EXCEPT.getCharPositionInLine()])
              ;

//suite: simple_stmt | NEWLINE INDENT stmt+ DEDENT
suite returns [List stmts]
    : ss+=simple_stmt {$stmts = $ss;}
    | NEWLINE! INDENT (s+=stmt)+ DEDENT {$stmts = $s;}
    ;

//test: or_test ['if' or_test 'else' test] | lambdef
test[expr_contextType ctype]
@after {
    if ($test.tree instanceof BoolOp) {
        BoolOp b = (BoolOp)$test.tree;
        List values = new ArrayList();

        exprType left = (exprType)b.getChild(0);
        exprType right = (exprType)b.getChild(1);

        exprType[] e;
        if (left.getType() == b.getType() && right.getType() == b.getType()) {
            BoolOp leftB = (BoolOp)left;
            BoolOp rightB = (BoolOp)right;
            int lenL = leftB.values.length;
            int lenR = rightB.values.length;
            e = new exprType[lenL + lenR];
            System.arraycopy(leftB.values, 0, e, 0, lenL - 1);
            System.arraycopy(rightB.values, 0, e, lenL - 1, lenL + lenR);
        } else if (left.getType() == b.getType()) {
            BoolOp leftB = (BoolOp)left;
            e = new exprType[leftB.values.length + 1];
            System.arraycopy(leftB.values, 0, e, 0, leftB.values.length);
            e[e.length - 1] = right;
        } else if (right.getType() == b.getType()) {
            BoolOp rightB = (BoolOp)right;
            e = new exprType[rightB.values.length + 1];
            System.arraycopy(rightB.values, 0, e, 0, rightB.values.length);
            e[e.length - 1] = left;
        } else {
            e = new exprType[2];
            e[0] = left;
            e[1] = right;
        }
        b.values = e;
        switch (b.getType()) {
        case AND:
            b.op = boolopType.And;
            break;
        case OR:
            b.op = boolopType.Or;
            break;
        default:
            b.op = boolopType.UNDEFINED;
        }
    }
}

    :o1=or_test[ctype]
    ( (IF or_test[expr_contextType.Load] ORELSE) => IF o2=or_test[ctype] ORELSE test[expr_contextType.Load]
      -> ^(IfExp ^(Test $o2) ^(Body $o1) ^(ORELSE test))
    | -> or_test
    )
    | lambdef {debug("parsed lambdef");}
    ;

//or_test: and_test ('or' and_test)*
or_test[expr_contextType ctype] : and_test[ctype] (OR<BoolOp>^ and_test[ctype])*
        ;

//and_test: not_test ('and' not_test)*
and_test[expr_contextType ctype] : not_test[ctype] (AND<BoolOp>^ not_test[ctype])*
         ;

//not_test: 'not' not_test | comparison
not_test[expr_contextType ctype] returns [exprType etype]
@after {
    if ($etype != null) {
        $not_test.tree = $etype;
    }
}
    : NOT nt=not_test[ctype] {$etype = new UnaryOp($NOT, unaryopType.Not, (exprType)$nt.tree);}
    | comparison[ctype]
    ;

//comparison: expr (comp_op expr)*
//comp_op: '<'|'>'|'=='|'>='|'<='|'<>'|'!='|'in'|'not' 'in'|'is'|'is' 'not'
comparison[expr_contextType ctype]
@init {
    List cmps = new ArrayList();
}
@after {
    if (!cmps.isEmpty()) {
        $comparison.tree = new Compare($left.tree, (exprType)$left.tree, actions.makeCmpOps(cmps), actions.makeExprs($right));
    }
}
    : left=expr[ctype]
       ( ( comp_op right+=expr[ctype] {cmps.add($comp_op.op);}
         )+
       |  -> $left
       )
    ;

//comp_op: '<'|'>'|'=='|'>='|'<='|'<>'|'!='|'in'|'not' 'in'|'is'|'is' 'not'
comp_op returns [cmpopType op]
    : LESS {$op = cmpopType.Lt;}
    | GREATER {$op = cmpopType.Gt;}
    | EQUAL {$op = cmpopType.Eq;}
    | GREATEREQUAL {$op = cmpopType.GtE;}
    | LESSEQUAL {$op = cmpopType.LtE;}
    | ALT_NOTEQUAL {$op = cmpopType.NotEq;}
    | NOTEQUAL {$op = cmpopType.NotEq;}
    | IN {$op = cmpopType.In;}
    | NOT IN {$op = cmpopType.NotIn;}
    | IS {$op = cmpopType.Is;}
    | IS NOT {$op = cmpopType.IsNot;}
    ;


//expr: xor_expr ('|' xor_expr)*
expr[expr_contextType ect]
scope {
    expr_contextType ctype;
}
@init {
    $expr::ctype = ect;
}
@after {
    if ($expr.tree instanceof BinOp) {
        BinOp b = (BinOp)$expr.tree;
        b.left = (exprType)b.getChild(0);
        b.right = (exprType)b.getChild(1);
        switch (b.getType()) {
        case PLUS:
            b.op = operatorType.Add;
            break;
        case MINUS:
            b.op = operatorType.Sub;
            break;
        case STAR:
            b.op = operatorType.Mult;
            break;
        case SLASH:
            b.op = operatorType.Div;
            break;
        case PERCENT:
            b.op = operatorType.Mod;
            break;
        case DOUBLESLASH:
            b.op = operatorType.FloorDiv;
            break;
        case AMPER:
            b.op = operatorType.BitAnd;
            break;
        case VBAR:
            b.op = operatorType.BitOr;
            break;
        case CIRCUMFLEX:
            b.op = operatorType.BitXor;
            break;
        case LEFTSHIFT:
            b.op = operatorType.LShift;
            break;
        case RIGHTSHIFT:
            b.op = operatorType.RShift;
            break;
        case DOUBLESTAR:
            b.op = operatorType.Pow;
            break;
        }
    }
}
    : xor_expr (VBAR<BinOp>^ xor_expr)*
    ;

//xor_expr: and_expr ('^' and_expr)*
xor_expr : and_expr (CIRCUMFLEX<BinOp>^ and_expr)*
         ;

//and_expr: shift_expr ('&' shift_expr)*
and_expr : shift_expr (AMPER<BinOp>^ shift_expr)*
         ;

//shift_expr: arith_expr (('<<'|'>>') arith_expr)*
shift_expr : arith_expr ((LEFTSHIFT<BinOp>^|RIGHTSHIFT<BinOp>^) arith_expr)*
           ;

//arith_expr: term (('+'|'-') term)*

arith_expr
    :term ((PLUS<BinOp>^|MINUS<BinOp>^) term)*
    ;

//term: factor (('*'|'/'|'%'|'//') factor)*
term : factor ((STAR<BinOp>^ | SLASH<BinOp>^ | PERCENT<BinOp>^ | DOUBLESLASH<BinOp>^ ) factor)*
     ;

//factor: ('+'|'-'|'~') factor | power
factor returns [exprType etype]
@after {
    $factor.tree = $etype;
}
    : PLUS p=factor {$etype = new UnaryOp($PLUS, unaryopType.UAdd, $p.etype);}
    | MINUS m=factor {$etype = actions.negate($MINUS, $m.etype);}
    | TILDE t=factor {$etype = new UnaryOp($TILDE, unaryopType.Invert, $t.etype);}
    | power {$etype = (exprType)$power.tree;}
    ;

//power: atom trailer* ['**' factor]
power returns [exprType etype]
@after {
    if ($etype != null) {
        $power.tree = $etype;
    }
}
    : atom (t+=trailer)* (options {greedy=true;}:DOUBLESTAR factor)? {
        if ($t != null) {
            exprType current = (exprType)$atom.tree;
            //for(int i = $t.size() - 1; i > -1; i--) {
            for(int i = 0; i < $t.size(); i++) {
                Object o = $t.get(i);
                if (current instanceof Context) {
                    ((Context)current).setContext(expr_contextType.Load);
                }
                //XXX: good place for an interface to avoid all of this instanceof
                if (o instanceof Call) {
                    Call c = (Call)o;
                    c.func = current;
                    current = c;
                } else if (o instanceof Subscript) {
                    Subscript c = (Subscript)o;
                    c.value = current;
                    current = c;
                } else if (o instanceof Attribute) {
                    Attribute c = (Attribute)o;
                    c.value = current;
                    current = c;
                }
            }
            $etype = (exprType)current;
        }
    }
    ;

//atom: ('(' [yield_expr|testlist_gexp] ')' |
//       '[' [listmaker] ']' |
//       '{' [dictmaker] '}' |
//       '`' testlist1 '`' |
//       NAME | NUMBER | STRING+)
atom : LPAREN 
       ( yield_expr    -> yield_expr
       | testlist_gexp -> testlist_gexp
       | -> ^(PYNODE<Tuple>[$LPAREN, new exprType[0\], $expr::ctype])
       )
       RPAREN
     | LBRACK
       (listmaker -> listmaker
       | -> ^(LBRACK<org.python.antlr.ast.List>[$LBRACK, new exprType[0\], $expr::ctype])
       )
       RBRACK
     | LCURLY (dictmaker)? RCURLY -> ^(Dict LCURLY ^(Elts dictmaker)?)
     | BACKQUOTE testlist[expr_contextType.Load] BACKQUOTE -> ^(Repr BACKQUOTE testlist)
     | NAME -> ^(PYNODE<Name>[$NAME, $NAME.text, $expr::ctype])
     | INT -> ^(PYNODE<Num>[$INT, actions.makeInt($INT)])
     | LONGINT -> ^(PYNODE<Num>[$LONGINT, actions.makeInt($LONGINT)])
     | FLOAT -> ^(PYNODE<Num>[$FLOAT, actions.makeFloat($FLOAT)])
     | COMPLEX -> ^(PYNODE<Num>[$COMPLEX, actions.makeComplex($COMPLEX)])
     | (S+=STRING)+ 
    -> ^(PYNODE<Str>[actions.extractStringToken($S), actions.extractStrings($S)])
     ;

//listmaker: test ( list_for | (',' test)* [','] )
listmaker returns [exprType etype]
@after {
   $listmaker.tree = $etype;
}
    : t+=test[expr_contextType.Load] 
            ( list_for -> ^(ListComp test list_for)
            | (options {greedy=true;}:COMMA t+=test[expr_contextType.Load])* {
                $etype = new org.python.antlr.ast.List($listmaker.start, actions.makeExprs($t), $expr::ctype);
            }
            ) (COMMA)?
          ;

//testlist_gexp: test ( gen_for | (',' test)* [','] )
testlist_gexp
    : t+=test[expr_contextType.Load]
        ( ((options {k=2;}: c1=COMMA t+=test[expr_contextType.Load])* (c2=COMMA)?
         -> { $c1 != null || $c2 != null }? ^(PYNODE<Tuple>[$testlist_gexp.start, actions.makeExprs($t), $expr::ctype])
         -> test
          )
        | ( gen_for -> ^(GeneratorExp test gen_for)
          )
        )
    ;

//lambdef: 'lambda' [varargslist] ':' test
lambdef: LAMBDA (varargslist)? COLON test[expr_contextType.Load] {debug("parsed lambda");}
      -> ^(LAMBDA<Lambda>[$LAMBDA, $varargslist.args, (exprType)$test.tree])
       ;

//trailer: '(' [arglist] ')' | '[' subscriptlist ']' | '.' NAME
trailer : LPAREN (arglist ->  ^(LPAREN<Call>[$LPAREN, null, actions.makeExprs($arglist.args), actions.makeKeywords($arglist.keywords), $arglist.starargs, $arglist.kwargs])
                 | -> ^(LPAREN<Call>[$LPAREN, null, new exprType[0\], new keywordType[0\], null, null])
                 )
          RPAREN
        | LBRACK s=subscriptlist RBRACK -> $s
        | DOT attr -> ^(DOT<Attribute>[$DOT, null, $attr.text, $expr::ctype])
        ;

//subscriptlist: subscript (',' subscript)* [',']
//FIXME: tuples not always created when commas are present.
subscriptlist returns [exprType etype]
@after {
   $subscriptlist.tree = $etype;
}
    : sub+=subscript (options {greedy=true;}:COMMA sub+=subscript)* (COMMA)? {
        sliceType s;
        List sltypes = $sub;
        if (sltypes.size() == 0) {
            s = null;
        } else if (sltypes.size() == 1){
            s = (sliceType)sltypes.get(0);
        } else {
            sliceType[] st;
            //FIXME: here I am using ClassCastException to decide if sltypes is populated with Index
            //       only.  Clearly this is not the best way to do this but it's late. Somebody do
            //       something better please :) -- (hopefully a note to self)
            try {
                Iterator iter = sltypes.iterator();
                List etypes = new ArrayList();
                while (iter.hasNext()) {
                    Index i = (Index)iter.next();
                    etypes.add(i.value);
                }
                exprType[] es = (exprType[])etypes.toArray(new exprType[etypes.size()]);
                exprType t = new Tuple($subscriptlist.start, es, expr_contextType.Load);
                s = new Index($subscriptlist.start, t);
            } catch (ClassCastException cc) {
                st = (sliceType[])sltypes.toArray(new sliceType[sltypes.size()]);
                s = new ExtSlice($subscriptlist.start, st);
            }
        }
        $etype = new Subscript($subscriptlist.start, null, s, $expr::ctype);
    }
    ;

//subscript: '.' '.' '.' | test | [test] ':' [test] [sliceop]
subscript returns [sliceType sltype]
@after {
    if ($sltype != null) {
        $subscript.tree = $sltype;
    }
}
    : DOT DOT DOT -> Ellipsis
    | (test[expr_contextType.Load] COLON) => lower=test[expr_contextType.Load] (c1=COLON (upper1=test[expr_contextType.Load])? (sliceop)?)? {
        $sltype = actions.makeSubscript($lower.tree, $c1, $upper1.tree, $sliceop.tree);
    }
    | (COLON) => c2=COLON (upper2=test[expr_contextType.Load])? (sliceop)? {
        $sltype = actions.makeSubscript(null, $c2, $upper2.tree, $sliceop.tree);
    }
    | test[expr_contextType.Load] -> ^(PYNODE<Index>[$test.start, (exprType)$test.tree])
    ;

//sliceop: ':' [test]
sliceop : COLON (test[expr_contextType.Load])? -> ^(Step COLON ^(StepOp test)?)
        ;

//exprlist: expr (',' expr)* [',']
exprlist[expr_contextType ctype] returns [exprType etype]
    : (expr[expr_contextType.Load] COMMA) => e+=expr[ctype] (options {k=2;}: COMMA e+=expr[ctype])* (COMMA)? {
     $etype = new Tuple($exprlist.start, actions.makeExprs($e), ctype);
    }
    | expr[ctype] {
        $etype = (exprType)$expr.tree;
    }
    ;

//XXX: I'm hoping I can get rid of this -- but for now I need an exprlist that does not produce tuples
//     at least for del_stmt
exprlist2 returns [List etypes]
    : e+=expr[expr_contextType.Load] (options {k=2;}: COMMA e+=expr[expr_contextType.Load])* (COMMA)?
    {$etypes = $e;}
    ;

//testlist: test (',' test)* [',']
testlist[expr_contextType ctype] returns [exprType etype]
@after {
   $testlist.tree = $etype;
}
    : (test[expr_contextType.Load] COMMA) => t+=test[ctype] (options {k=2;}: c1=COMMA t+=test[ctype])* (c2=COMMA)? {
          $etype = new Tuple($testlist.start, actions.makeExprs($t), ctype);
    }
    | test[ctype] {
          $etype = (exprType)$test.tree;
    }
    ;

//XXX:
//testlist_safe: test [(',' test)+ [',']]

//dictmaker: test ':' test (',' test ':' test)* [',']
dictmaker : test[expr_contextType.Load] COLON test[expr_contextType.Load]
            (options {k=2;}:COMMA test[expr_contextType.Load] COLON test[expr_contextType.Load])* (COMMA)?
         -> test+
          ;

//classdef: 'class' NAME ['(' [testlist] ')'] ':' suite
classdef
@init {
    stmtType stype = null;
}
@after {
   $classdef.tree = stype;
}
    :CLASS NAME (LPAREN testlist[expr_contextType.Load]? RPAREN)? COLON suite {
        stype = new ClassDef($CLASS, $NAME.getText(), actions.makeBases($testlist.etype), actions.makeStmts($suite.stmts));
    }
    ;

//arglist: (argument ',')* (argument [',']| '*' test [',' '**' test] | '**' test)
arglist returns [List args, List keywords, exprType starargs, exprType kwargs]
@init {
    List arguments = new ArrayList();
    List kws = new ArrayList();
}
    : argument[arguments, kws] (COMMA argument[arguments, kws])*
            ( COMMA
                ( STAR s=test[expr_contextType.Load] (COMMA DOUBLESTAR k=test[expr_contextType.Load])?
                | DOUBLESTAR k=test[expr_contextType.Load]
                )?
            )? {
            $args=arguments;
            $keywords=kws;
            $starargs=(exprType)$s.tree;
            $kwargs=(exprType)$k.tree;
        }
        |   STAR s=test[expr_contextType.Load] (COMMA DOUBLESTAR k=test[expr_contextType.Load])? {
            $starargs=(exprType)$s.tree;
            $kwargs=(exprType)$k.tree;
        }
        |   DOUBLESTAR k=test[expr_contextType.Load] {
            $kwargs=(exprType)$k.tree;
        }
        ;

//argument: test [gen_for] | test '=' test  # Really [keyword '='] test
argument[List arguments, List kws]
    : t1=test[expr_contextType.Load]
        ( (ASSIGN t2=test[expr_contextType.Load]) {
            $kws.add(new exprType[]{(exprType)$t1.tree, (exprType)$t2.tree});
        }
        | gen_for //FIXME
        | {$arguments.add($t1.tree);}
        )
    ;

//list_iter: list_for | list_if
list_iter : list_for
          | list_if
          ;

//list_for: 'for' exprlist 'in' testlist_safe [list_iter]
list_for : FOR exprlist[expr_contextType.Load] IN testlist[expr_contextType.Load] (list_iter)?
        -> ^(FOR<comprehensionType>[$FOR, $exprlist.etype, $testlist.etype, null])
         ;

//list_if: 'if' test [list_iter]
list_if : IF test[expr_contextType.Load] (list_iter)?
       -> ^(ListIf ^(Target test) (Ifs list_iter)?)
        ;

//gen_iter: gen_for | gen_if
gen_iter: gen_for
        | gen_if
        ;

//gen_for: 'for' exprlist 'in' or_test [gen_iter]
gen_for: FOR exprlist[expr_contextType.Load] IN or_test[expr_contextType.Load] gen_iter?
      -> ^(GenFor ^(Target exprlist) ^(IN or_test) ^(Ifs gen_iter)?)
       ;

//gen_if: 'if' old_test [gen_iter]
gen_if: IF test[expr_contextType.Load] gen_iter?
     -> ^(GenIf ^(Target test) ^(Ifs gen_iter)?)
      ;

//yield_expr: 'yield' [testlist]
yield_expr : YIELD testlist[expr_contextType.Load]?
          -> ^(YIELD<Yield>[$YIELD, $testlist.etype])
           ;

//XXX:
//testlist1: test (',' test)*

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

STRINGPART
    : {partial}?=> ('r'|'u'|'ur'|'R'|'U'|'UR'|'uR'|'Ur')?
        (   '\'\'\'' ~('\'\'\'')*
        |   '"""' ~('"""')*
        )
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

