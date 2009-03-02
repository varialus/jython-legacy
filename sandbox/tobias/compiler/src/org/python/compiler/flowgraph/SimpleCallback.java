package org.python.compiler.flowgraph;

public interface SimpleCallback<E extends Exception> {
    void generateBlock(BlockBuilder builder) throws E;
}
