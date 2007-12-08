package org.python.antlr;

import java.io.*;
import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import org.antlr.stringtemplate.*;
import org.antlr.stringtemplate.language.*;
import org.python.antlr.PythonLexer;
import org.python.antlr.PythonParser;
import org.python.antlr.PythonTree;
import org.python.antlr.PythonTokenSource;

//import org.python.antlr.ast.modType;

public class GrammarOnly {
    // override nextToken to set startPos (this seems too hard)
    public static class PyLexer extends PythonLexer {
        public PyLexer(CharStream lexer) {
            super(lexer);
        }
        public Token nextToken() {
            startPos = getCharPositionInLine();
            return super.nextToken();
        }
    }

	public static TreeAdaptor pyadaptor = new CommonTreeAdaptor() {
		public Object create(Token token) {
            /*
            if (token != null && token.getType() == PythonParser.Target) {
                System.out.println("Target found");
            }
            */
			return new PythonTree(token);
		}
		public Object dupNode(Object t) {
			if ( t==null ) {
				return null;
			}
			return create(((PythonTree)t).token);
		}
	};

    public PythonTree parse(String[] args) throws Exception {
        PythonTree result = null;
        CharStream input = new ANTLRFileStream(args[0]);
        PythonLexer lexer = new PyLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        tokens.discardOffChannelTokens(true);
        PythonTokenSource indentedSource = new PythonTokenSource(tokens);
        tokens = new CommonTokenStream(indentedSource);
        //System.out.println("tokens="+tokens.getTokens());
        PythonParser parser = new PythonParser(tokens);
        parser.setTreeAdaptor(pyadaptor);
        try {
            PythonParser.file_input_return r = parser.file_input();
            if (args.length > 1) {
                System.out.println(((Tree)r.tree).toStringTree());
            }
            CommonTreeNodeStream nodes = new CommonTreeNodeStream((Tree)r.tree);
            nodes.setTokenStream(tokens);
            //PythonWalker walker = new PythonWalker(nodes);
            //result = walker.module();
            //System.out.println(result.toStringTree());
        } catch (RecognitionException e) {
            System.err.println("Error: " + e);
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        new GrammarOnly().parse(args);
    }
}
