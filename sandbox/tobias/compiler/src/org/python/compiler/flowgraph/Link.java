package org.python.compiler.flowgraph;

public class Link {
    public static class Branch {

        public final Link onTrue;
        public final Link onFalse;

        Branch(Link onTrue, Link onFalse) {
            this.onTrue = onTrue;
            this.onFalse = onFalse;
        }

    }

    private Variable[] incoming;
    private Block target;

    Link(Block target, Variable[] incoming) {
        this.target = target;
        this.incoming = incoming;
    }

}
