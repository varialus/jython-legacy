package org.python.compiler.flowgraph;

import java.util.HashMap;
import java.util.Map;

public final class CodeGraphFactory {
    private final Map<Object, Constant> constantPool = new HashMap<Object, Constant>();

    public CodeGraph createGraph(GraphInfo info) {
        return new CodeGraph(info, constantPool);
    }
}
