package org.thobe.compiler.sea;

class LoadNode extends ValueNode {
    private final Variable variable;

    LoadNode(Variable variable) {
        this.variable = variable;
    }

    private StoreNode defined;
    private final Value result = new Value() {
        @Override
        public String toString() {
            return "value_of(" + LoadNode.this + ")";
        }
    };

    @Override
    public Value result() {
        return result;
    }

    @Override
    LoadNode accept(GraphTraverser traverser) {
        return traverser.load(this, variable);
    }

    @Override
    public String toString() {
        return "LoadNode[" + variable + "]";
    }
}
