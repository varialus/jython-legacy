package org.python.compiler;

import org.python.antlr.ast.VisitorIF;
import org.python.antlr.base.mod;
import org.python.compiler.flowgraph.Transformer;
import org.python.core.CompilerFlags;
import org.python.core.PythonCodeBundle;
import org.python.core.PythonCompiler;

public class AdvancedCompiler implements PythonCompiler {
    private VisitorIF<?> astTransformer;
    private final Transformer flowgraphTransformations = null; // TODO
    private final AdvancedPreferences preferences = null;
    private final CodeGenerator codegen = null;// Create this depending on the preferences
    private boolean generateFlowGraph = true;

    public PythonCodeBundle compile(mod node, String name, String filename,
            boolean linenumbers, boolean printResults, CompilerFlags cflags)
            throws Exception {
        if (astTransformer != null) {
            node.accept(astTransformer);
        }
        if (generateFlowGraph) {
            FlowGraphBundle bundle = new FlowGraphBundle(name, filename);
            CompilerDirector.compile(bundle, preferences.forFlowGraph(),
                    cflags, linenumbers, printResults, node);
            bundle.acceptTransformations(flowgraphTransformations);
            return bundle.generateCode(codegen);
        } else {
            DirectCodeBundle bundle = new DirectCodeBundle(name, filename);
            CompilerDirector.compile(bundle, preferences.forDirect(), cflags,
                    linenumbers, printResults, node);
            return bundle;
        }
    }

}
