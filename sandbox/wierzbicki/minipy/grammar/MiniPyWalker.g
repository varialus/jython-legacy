tree grammar MiniPyWalker;

options {
    tokenVocab=MiniPy;
    ASTLabelType=CommonTree;
}

@header {
package org.python.antlr;
import org.objectweb.asm.*;

import java.io.*;
}

@members {
    ClassWriter cw = new ClassWriter(0);
    String name;
}

file_input[String name]
@init {
    System.out.println("begin");
    cw.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER,
                name, null, "java/lang/Object", null);
}

@after {
    try {
        byte[] ba = cw.toByteArray();
        FileOutputStream fos = new FileOutputStream(name + ".class");
        ByteArrayOutputStream baos = new ByteArrayOutputStream(ba.length);
        baos.write(ba, 0, ba.length);
        baos.writeTo(fos);
        baos.close();
        fos.close();
    } catch (IOException e) {
        System.err.println("Error writing " + name + ".class: " + e);
    }
    System.out.println("finished");
}
    : stmt+
    ;

stmt : simple_stmt
     | compound_stmt
     ;

simple_stmt : small_stmt
            ;

small_stmt : expr
           | ^(Print expr)
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
