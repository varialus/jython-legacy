package org.thobe.compiler.sea;

class GraphReferenceNode extends Node {
    Node start;

    @Override
    Node accept(GraphTraverser traverser) {
        return start.accept(traverser);
    }

    @Override
    NodeSuccession succession() {
        return new NodeSuccession() {
            @Override
            NodeSuccession setNext(Node node) {
                start = node;
                return node.succession();
            }
        };
    }

    @Override
    public String toString() {
        return start != null ? start.toString() : "NullGraph";
    }
}
