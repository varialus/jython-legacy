package org.python.compiler.flowgraph;


public interface ProducerCallback<E extends Exception> {
    Variable generateProducer(BlockBuilder builder) throws E;
}
