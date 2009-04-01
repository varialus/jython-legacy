package org.thobe.compiler.sea;

import java.util.Arrays;

class ArrayNode extends ValueNode {
    private final Value[] content;

    ArrayNode(Value[] content) {
        this.content = content;
    }

    @Override
    public Value result() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    ArrayNode accept(GraphTraverser traverser) {
        return traverser.array(this, content);
    }

    @Override
    public String toString() {
        return "Array:[" + Arrays.toString(content) + "]";
    }
}
