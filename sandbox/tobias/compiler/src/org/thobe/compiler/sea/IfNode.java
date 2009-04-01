package org.thobe.compiler.sea;

class IfNode extends Node {
    private Node trueSuccessor;
    private Node falseSuccessor;
    private final Value predicate;

    IfNode(Value predicate) {
        this.predicate = predicate;
    }

    void setOnTrue(Node next) {
        trueSuccessor = next;
    }

    void setOnFalse(Node next) {
        falseSuccessor = next;
    }

    @Override
    IfNode accept(GraphTraverser traverser) {
        return traverser.selection(this, predicate, trueSuccessor,
                falseSuccessor);
    }

    @Override
    NodeSuccession succession() {
        return NodeSuccession.DUMMY;
    }

    @Override
    public String toString() {
        return "IfNode[" + predicate + " ? " + trueSuccessor + " : "
                + falseSuccessor + "]";
    }
}
