package org.thobe.compiler.sea;

class ReturnNode extends Node {
    private final Value value;

    ReturnNode(Value value) {
        this.value = value;
    }

    @Override
    ReturnNode accept(GraphTraverser traverser) {
        return traverser.returnValue(this, value);
    }

    @Override
    NodeSuccession succession() {
        return NodeSuccession.NOTHING;
    }

    @Override
    public String toString() {
        return "ReturnNode[" + value + "]";
    }
}
