package org.python.compiler.flowgraph;


public interface ConsumerCallback<E extends Exception> {
    void generateConsumer(BlockBuilder builder, Variable variable) throws E;
}
