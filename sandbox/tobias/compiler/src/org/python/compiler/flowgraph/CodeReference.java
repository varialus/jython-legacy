package org.python.compiler.flowgraph;

class CodeReference extends Variable {

    private final CodeGraph graph;

    CodeReference(CodeGraph graph) {
        super(null);
        this.graph = graph;
    }

}
