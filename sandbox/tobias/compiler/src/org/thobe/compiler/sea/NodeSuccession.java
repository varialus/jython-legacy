package org.thobe.compiler.sea;

public abstract class NodeSuccession {
    public static final NodeSuccession DUMMY = new NodeSuccession() {
        @Override
        NodeSuccession setNext(Node node) {
            throw new IllegalStateException(
                    "Trying to set next node for node with no single succession.");
        }
    };
    public static final NodeSuccession NOTHING = new NodeSuccession() {
        @Override
        NodeSuccession setNext(Node node) {
            return this;
        }
    };

    abstract NodeSuccession setNext(Node node);
}
