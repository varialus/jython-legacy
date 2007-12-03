package org.python.antlr;

import java.io.*;
import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import org.antlr.stringtemplate.*;
import org.antlr.stringtemplate.language.*;
import org.python.antlr.PythonLexer;
import org.python.antlr.PythonParser;
import org.python.antlr.PythonTokenSource;

import org.python.antlr.ast.modType;

public class AstTest {
    // override nextToken to set startPos (this seems too hard)
    public static class MyLexer extends PythonLexer {
        public MyLexer(CharStream lexer) {
            super(lexer);
        }
        public Token nextToken() {
            startPos = getCharPositionInLine();
            return super.nextToken();
        }
    }

    public modType parse(String[] args) throws Exception {
        modType result = null;
        CharStream input = new ANTLRFileStream(args[0]);
        PythonLexer lexer = new MyLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        tokens.discardOffChannelTokens(true);
        PythonTokenSource indentedSource = new PythonTokenSource(tokens);
        tokens = new CommonTokenStream(indentedSource);
        //System.out.println("tokens="+tokens.getTokens());
        PythonParser parser = new PythonParser(tokens);

        try {
            PythonParser.file_input_return r = parser.file_input();
            if (args.length > 1) {
                System.out.println(((Tree)r.tree).toStringTree());
            }
            CommonTreeNodeStream nodes = new CommonTreeNodeStream((Tree)r.tree);
            nodes.setTokenStream(tokens);
            PythonWalker walker = new PythonWalker(nodes);
            result = walker.module();
        } catch (RecognitionException e) {
            System.err.println("Error: " + e);
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        new Main().parse(args);
    }
}
