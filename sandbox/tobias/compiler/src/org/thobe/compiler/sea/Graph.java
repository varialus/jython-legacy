package org.thobe.compiler.sea;

public final class Graph {
    private final Node entry;

    Graph(Node entry) {
        this.entry = entry;
    }

    public void serialize(final GraphVisitor visitor) {
        entry.accept(new GraphTraverser() {
            @Override
            ArrayNode array(ArrayNode original, Value[] content) {
                visitor.node(original);
                return original;
            }

            @Override
            InvocationNode invoke(InvocationNode original,
                    InvocationType invocation, Value[] arguments) {
                visitor.node(original);
                return original;
            }

            @Override
            LoadNode load(LoadNode original, Variable variable) {
                visitor.node(original);
                return original;
            }

            @Override
            PhiNode phi(PhiNode original, Node[] array) {
                visitor.node(original);
                return original;
            }

            @Override
            ThrowNode raiseException(ThrowNode original,
                    ExceptionValue exception) {
                visitor.node(original);
                return original;
            }

            @Override
            ReturnNode returnValue(ReturnNode original, Value value) {
                visitor.node(original);
                return original;
            }

            @Override
            SelectTraverser select(final SwitchNode original, Value select) {
                visitor.node(original);
                return new SelectTraverser() {
                    @Override
                    void onCase(Integer key, Node node) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    void otherwise(Node node) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    SwitchNode done() {
                        return original;
                    }
                };
            }

            @Override
            IfNode selection(IfNode original, Value predicate,
                    Node trueSuccessor, Node falseSuccessor) {
                visitor.node(original);
                return original;
            }

            @Override
            StoreNode store(StoreNode original, Variable variable, Value value) {
                visitor.node(original);
                return original;
            }
        });
    }

    public byte[] serialize() {
        SerializationTraverser traverser = null;//new SerializationTraverser();
        entry.accept(traverser);
        return traverser.serialization();
    }
}
