import java.io.*;
import org.antlr.runtime.*;
import org.antlr.stringtemplate.*;
import org.antlr.stringtemplate.language.*;

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
        walk(new File(args[0]));
    }

    public static void walk(File dir) throws Exception {
        String pattern = ".py";
 
        File pyfiles[] = dir.listFiles();
        if(pyfiles != null) {
            for(int i=0; i<pyfiles.length; i++) {
                if(pyfiles[i].isDirectory()) {
                    walk(pyfiles[i]);
                } else {
                    if(pyfiles[i].getName().endsWith(pattern)) {
                        run(pyfiles[i].getPath());
                    }
                }
            }
        }
    }


    public static void run(String path) throws Exception {
        System.out.println("parsing " + path);
        CharStream input = new ANTLRFileStream(path);
        PythonLexer lexer = new MyLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        //tokens.discardOffChannelTokens(true);
        PythonTokenSource indentedSource = new PythonTokenSource(tokens, "<test>");
        tokens = new CommonTokenStream(indentedSource);
        PythonParser parser = new PythonParser(tokens);
        parser.file_input();
    }
}
