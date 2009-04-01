package org.thobe.compiler.sea;

public abstract class Node {
    abstract Node accept(GraphTraverser traverser);
    abstract NodeSuccession succession();
    @Override
    abstract public String toString();
}
