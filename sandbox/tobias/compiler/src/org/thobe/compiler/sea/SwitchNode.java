package org.thobe.compiler.sea;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

class SwitchNode extends Node {
    private Node defaultSuccessor;
    private final SortedMap<Integer, Node> successors = new TreeMap<Integer, Node>();
    private final Value select;

    SwitchNode(Value select) {
        this.select = select;
    }

    void addSuccessor(int on, Node next) {
        successors.put(on, next);
    }

    @Override
    SwitchNode accept(GraphTraverser traverser) {
        GraphTraverser.SelectTraverser selector = traverser.select(this, select);
        for (Map.Entry<Integer, Node> entry : successors.entrySet()) {
            selector.onCase(entry.getKey(), entry.getValue());
        }
        if (defaultSuccessor != null) {
            selector.otherwise(defaultSuccessor);
        }
        return selector.done();
    }

    @Override
    NodeSuccession succession() {
        return NodeSuccession.DUMMY;
    }

    @Override
    public String toString() {
        StringBuilder targets = new StringBuilder();
        String sep = ": ";
        for (Map.Entry<Integer, Node> entry : successors.entrySet()) {
            targets.append(sep);
            targets.append(entry.getKey().toString());
            targets.append(" -> ");
            targets.append(entry.getValue().toString());
            sep = ", ";
        }
        return "SwitchNode[" + targets + "]";
    }
}
