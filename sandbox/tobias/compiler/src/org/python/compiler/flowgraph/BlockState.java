package org.python.compiler.flowgraph;

class BlockState {
    final Block onBreak;
    final Block onContinue;
    final SimpleCallback<? extends Exception> finallyBuilder;

    BlockState(Block onBreak, Block onContinue,
            SimpleCallback<? extends Exception> finallyBuilder) {
        this.onBreak = onBreak;
        this.onContinue = onContinue;
        this.finallyBuilder = finallyBuilder;
    }
}
