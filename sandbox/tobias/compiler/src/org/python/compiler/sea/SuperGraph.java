package org.python.compiler.sea;

import org.thobe.compiler.sea.Graph;
import org.thobe.compiler.sea.NamespacePopulator;
import org.thobe.compiler.sea.NodeSorter;
import org.thobe.compiler.sea.Value;
import org.thobe.compiler.sea.VariableFactory;

public class SuperGraph {
    private Graph constants;
    private Graph module;

    public GraphBuilder constants() {
        // TODO: capture this graph...
        return new GraphBuilder(new NamespacePopulator() {
            public void populate(VariableFactory factory) {
            }
        }, new CallStrategy() {
        }, null, null) {
            @Override
            public Value graph() {
                throw new UnsupportedOperationException();
            }

            @Override
            public void mirandaReturn() {
                throw new UnsupportedOperationException();
            }

            @Override
            public void returnPythonValue(Value evaluate) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Value yeildValue(Value value) {
                throw new UnsupportedOperationException();
            }
        };
    }

    public void accept(NodeSorter sort, SupergraphVisitor visitor) {
        // TODO: replace this method
        constants.serialize(visitor.visitGraph());
        module.serialize(visitor.visitGraph());
    }
}
