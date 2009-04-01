package org.python.compiler.sea.output;

import org.python.compiler.sea.PythonOperation;
import org.thobe.compiler.sea.GraphBuilder;
import org.thobe.compiler.sea.GraphVisitor;
import org.thobe.compiler.sea.NamespacePopulator;
import org.thobe.compiler.sea.Node;
import org.thobe.compiler.sea.ValueNode;
import org.thobe.compiler.sea.VariableFactory;

public final class BuildGraph extends GraphBuilder {
    protected BuildGraph(NamespacePopulator populator) {
        super(populator);
    }

    public static void main(String[] args) {
        BuildGraph builder = new BuildGraph(new NamespacePopulator() {
            public void populate(VariableFactory factory) {
                factory.createLocalVariable("x");
                factory.createLocalVariable("y");
            }
        });
        ValueNode load_x = builder.loadVariable(builder.variable("x"));
        ValueNode load_y = builder.loadVariable(builder.variable("y"));
        ValueNode add = builder.schedule(builder.invoke(
                PythonOperation.BINARY_ADD, load_x.result(), load_y.result()));
        builder.schedule(builder.returnValue(add.result()));
        builder.build().serialize(new GraphVisitor() {
            public void node(Node node) {
                System.out.println(node);
            }
        });
    }
}
