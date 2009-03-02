package org.python.compiler;

import org.python.compiler.flowgraph.CodeGraphVisitor;

public class CodeGeneratorGraphVisitor implements CodeGraphVisitor {
    private final BytecodeBundle bundle;

    CodeGeneratorGraphVisitor(BytecodeBundle bundle) {
        this.bundle = bundle;
    }
}
