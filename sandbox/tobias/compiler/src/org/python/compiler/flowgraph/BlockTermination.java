package org.python.compiler.flowgraph;

public abstract class BlockTermination {
    public static final BlockTermination BREAK = new BlockTermination() {
    };
    public static final BlockTermination CONTINUE = new BlockTermination() {
    };

    public static class Return extends BlockTermination {
        public Return(Variable result) {
        }
    }

    public static class Raise extends BlockTermination {
        public Raise(Variable exception) {
        }
    }

    // Internal transitions

    static class Yield extends BlockTermination {
        Yield(Variable yield, Block next) {
            // TODO Auto-generated constructor stub
        }
    }

    static class Goto extends BlockTermination {
        Goto(Block next) {
            // TODO Auto-generated constructor stub
        }
    }

    static class Condition extends BlockTermination {
        Condition(Variable predicate, Block onTrue, Block onFalse) {
            // TODO Auto-generated constructor stub
        }
    }

    private BlockTermination() {
        // TODO Auto-generated constructor stub
    }

    void of(Block block, BlockState blockState) {
        block.endWith(this); // default behavior
    }
}
