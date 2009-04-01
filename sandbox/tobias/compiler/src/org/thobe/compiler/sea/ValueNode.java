package org.thobe.compiler.sea;

public abstract class ValueNode extends Node {
    static ValueNode repeat(final Value value) {
        return new ValueNode() {
            @Override
            public Value result() {
                return value;
            }

            @Override
            ValueNode accept(GraphTraverser traverser) {
                throw new UnsupportedOperationException("Needs fixing!");
            }

            @Override
            public String toString() {
                return "RepeatedValueNode[" + value + "]";
            }
        };
    }

    private Node next;

    ValueNode() {
        // TODO Auto-generated constructor stub
    }

    public abstract Value result();

    @Override
    abstract ValueNode accept(GraphTraverser traverser);

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
}
