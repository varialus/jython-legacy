package org.thobe.compiler.sea;

public final class Graph {
    private Node entry;

    public void compile(GraphVisitor visitor) {
        entry.accept(new CodeGenerationTraverser(visitor));
    }

    public void transform(GraphVisitor visitor) {
        entry.accept(new TransformationTraverser(visitor));
    }

    public byte[] serialize() {
        SerializationTraverser traverser = new SerializationTraverser();
        entry.accept(traverser);
        return traverser.serialization();
    }
}
