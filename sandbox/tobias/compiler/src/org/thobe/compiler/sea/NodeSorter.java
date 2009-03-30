package org.thobe.compiler.sea;

public interface NodeSorter {
    enum Order {
        CORRECT, REVERSED, DO_NOT_CARE
    }

    Order order(Node first, Node second);
}
