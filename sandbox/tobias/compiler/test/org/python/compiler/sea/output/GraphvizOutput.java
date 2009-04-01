package org.python.compiler.sea.output;

import org.python.antlr.base.mod;
import org.python.compiler.advanced.CompilerDirector;
import org.python.compiler.advanced.Preferences;
import org.python.compiler.advanced.sea.NodeSeaBundle;
import org.python.compiler.sea.SupergraphVisitor;
import org.python.core.CompileMode;
import org.python.core.CompilerFlags;
import org.python.core.ParserFacade;
import org.thobe.compiler.sea.GraphVisitor;
import org.thobe.compiler.sea.Node;

public class GraphvizOutput {
    public static void print(CompileMode mode, String source) throws Exception {
        CompilerFlags flags = new CompilerFlags();
        mod ast = ParserFacade.parse(source, mode,
                GraphvizOutput.class.getName(), flags);
        print(ast, flags);
    }

    private static void print(mod ast, CompilerFlags flags) throws Exception {
        NodeSeaBundle bundle = new NodeSeaBundle();
        CompilerDirector.compile(bundle, new Preferences(), flags, true, false,
                ast);
        bundle.accept(new SupergraphVisitor() {
            public GraphVisitor visitGraph() {
                System.out.println("graph:");
                return new GraphVisitor() {
                    public void node(Node node) {
                        System.out.println("  " + node);
                    }
                };
            }
        });
    }

    public static void main(String[] args) throws Exception {
        print(CompileMode.exec, "print 'hello world'");
    }
}
