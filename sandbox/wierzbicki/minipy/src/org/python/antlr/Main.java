package org.python.antlr;

import java.io.*;
import java.util.*;
import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import org.python.antlr.MiniPyLexer;
import org.python.antlr.MiniPyParser;
import org.python.antlr.MiniPyTokenSource;

public class Main {

    public static class MyLexer extends MiniPyLexer {
        public MyLexer(CharStream lexer) {
            super(lexer);
        }
        public Token nextToken() {
            startPos = getCharPositionInLine();
            return super.nextToken();
        }
    }

    public static void printTree(CommonTree t, int indent) {
        if ( t != null ) {
            System.out.println("XXX: " + t.toString());
            StringBuffer sb = new StringBuffer(indent);
            for ( int i = 0; i < indent; i++ ) {
                sb = sb.append("   ");
            }
            for ( int i = 0; i < t.getChildCount(); i++ ) {
                System.out.println(sb.toString() + t.getChild(i).toString());
                printTree((CommonTree)t.getChild(i), indent+1);
            }
        }
    }
    
    public static void main(String[] args) throws Exception {
        CharStream input = new ANTLRFileStream(args[0]);
        MiniPyLexer lexer = new MyLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        //tokens.discardOffChannelTokens(true);
        MiniPyTokenSource indentedSource = new MiniPyTokenSource(tokens);
        tokens = new CommonTokenStream(indentedSource);
        //System.out.println("tokens="+tokens.getTokens());
        MiniPyParser parser = new MiniPyParser(tokens);
        MiniPyParser.file_input_return r = parser.file_input();
        if (args.length > 1) {
            System.out.println("tree: "+((Tree)r.tree).toStringTree());
            System.out.println("-----------------------------------");
        }
        printTree((CommonTree)r.tree, 0);
        CommonTreeNodeStream nodes = new CommonTreeNodeStream(new PyTreeAdaptor(), (Tree)r.tree);
        nodes.setTokenStream(tokens);
        MiniPyWalker walker = new MiniPyWalker(nodes);
        walker.file_input("Test");
}
}
