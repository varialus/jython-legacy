package org.python.antlr;

import java.io.*;
import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import org.antlr.stringtemplate.*;
import org.antlr.stringtemplate.language.*;
import org.python.antlr.PythonLexer;
import org.python.antlr.PythonParser;
import org.python.antlr.PythonTokenSource;

public class Main {
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

    public static void main(String[] args) throws Exception {
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
            //PythonParser.module_return r = parser.module();
            if (args.length > 1) {
                System.out.println("tree: "+((Tree)r.tree).toStringTree());
                System.out.println("-----------------------------------");
            }
            CommonTreeNodeStream nodes = new CommonTreeNodeStream((Tree)r.tree);
            nodes.setTokenStream(tokens);
            PythonWalker walker = new PythonWalker(nodes);
            walker.module();
        } catch (RecognitionException e) {
            // FIXME:
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.err.println("FJW: " + e);
        }

    }
}
