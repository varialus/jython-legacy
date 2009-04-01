package org.thobe.compiler.sea;

class ThrowNode extends Node {
    private final ExceptionValue exception;

    ThrowNode(ExceptionValue exception) {
        this.exception = exception;
    }

    @Override
    ThrowNode accept(GraphTraverser traverser) {
        return traverser.raiseException(this, exception);
    }

    @Override
    NodeSuccession succession() {
        return NodeSuccession.DUMMY;
    }

    @Override
    public String toString() {
        return "ThrowNode[" + exception + "]";
    }
}
