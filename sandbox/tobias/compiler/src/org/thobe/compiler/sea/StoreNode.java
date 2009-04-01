package org.thobe.compiler.sea;

class StoreNode extends Node {
    private Node next;
    private final Variable variable;
    private final Value value;

    StoreNode(Variable variable, Value value) {
        this.variable = variable;
        this.value = value;
    }

    @Override
    StoreNode accept(GraphTraverser traverser) {
        return traverser.store(this, variable, value);
    }

    @Override
    NodeSuccession succession() {
        return new NodeSuccession() {
            @Override
            NodeSuccession setNext(Node node) {
                next = node;
                return node.succession();
            }
        };
    }

    @Override
    public String toString() {
        return "StoreNode[" + variable + " <- " + value + "]";
    }
}
