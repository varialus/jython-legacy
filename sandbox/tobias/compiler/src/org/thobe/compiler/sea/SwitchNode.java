package org.thobe.compiler.sea;

import java.util.Map;

class SwitchNode extends Node {
    private Node defaultSuccessor;
    private Map<Integer, Node> successors;
    SwitchNode(Value select) {
    }
    void addSuccessor(int on, Node next) {
        successors.put(on, next);
    }
}
