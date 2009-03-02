package org.python.compiler.flowgraph;

class BlockBuilderImpl implements BlockBuilder {
    private final CodeGraph graph;
    private Block start;
    private Block block;
    private boolean initialized = false;
    private final BlockState blockState;

    BlockBuilderImpl(CodeGraph graph, BlockState loopState) {
        this.graph = graph;
        this.blockState = loopState;
    }

    private Block block() {
        if (!initialized) {
            start = block = graph.createBlock();
            initialized = true;
        }
        return block;
    }

    public boolean isOpen() {
        return block() != null;
    }

    private interface LoopGenerator<E extends Exception> {
        Variable generate(BlockBuilderImpl setupBlock,
                BlockBuilderImpl loopBlock, Variable variable) throws E;
    }

    private <E extends Exception> void performLoop(
            ProducerCallback<E> setupBuilder, SimpleCallback<E> elseBuilder,
            LoopGenerator<E> generator) throws E {
        Block after = virtual();
        BlockBuilderImpl setupBlock = new BlockBuilderImpl(graph, blockState);
        BlockState state = new BlockState(after, setupBlock.virtual(),
                blockState != null ? blockState.finallyBuilder : null);
        BlockBuilderImpl loopBlock = new BlockBuilderImpl(graph, state);
        BlockBuilderImpl elseBlock = new BlockBuilderImpl(graph, blockState);
        Variable variable = setupBuilder.generateProducer(setupBlock);
        Variable predicate = generator.generate(setupBlock, loopBlock, variable);
        loopBlock.block(); // make sure that the body is initialized
        elseBuilder.generateBlock(elseBlock);
        Block onFalse;
        if (elseBlock.initialized) {
            onFalse = elseBlock.start;
            elseBlock.block().endWith(new BlockTermination.Goto(after));
        } else {
            onFalse = after;
        }
        if (setupBlock.initialized) {
            block().endWith(new BlockTermination.Goto(setupBlock.start));
            setupBlock.block().endWith(
                    new BlockTermination.Condition(predicate, loopBlock.start,
                            onFalse));
        } else {
            block().endWith(
                    new BlockTermination.Condition(predicate, loopBlock.start,
                            onFalse));
            if (loopBlock.isOpen()) {
                loopBlock.block().endWith(
                        new BlockTermination.Condition(predicate,
                                loopBlock.start, onFalse));
            }
        }
        block = after;
    }

    public <E extends Exception> void performBooleanLoop(
            ProducerCallback<E> setupBuilder,
            final SimpleCallback<E> loopBuilder, SimpleCallback<E> elseBuilder)
            throws E {
        performLoop(setupBuilder, elseBuilder, new LoopGenerator<E>() {
            public Variable generate(BlockBuilderImpl setupBlock,
                    BlockBuilderImpl loopBlock, Variable predicate) throws E {
                if (predicate == null) {
                    throw new IllegalArgumentException(
                            "Boolean loop setup must produce a variable.");
                }
                loopBuilder.generateBlock(loopBlock);
                return predicate;
            }
        });
    }

    public <E extends Exception> void performIteratorLoop(
            ProducerCallback<E> iterableBuilder,
            final ConsumerCallback<E> loopBuilder, SimpleCallback<E> elseBuilder)
            throws E {
        performLoop(iterableBuilder, elseBuilder, new LoopGenerator<E>() {
            public Variable generate(BlockBuilderImpl setupBlock,
                    BlockBuilderImpl loopBlock, Variable iterator) throws E {
                if (iterator == null) {
                    throw new IllegalArgumentException(
                            "Iterator loop setup must produce a variable.");
                }
                Variable next = setupBlock.block().addInstruction(
                        new Instruction.IteratorNext(iterator));
                Variable predicate = setupBlock.block().addInstruction(
                        new Instruction.NullCheck(next));
                loopBuilder.generateConsumer(loopBlock, next);
                return predicate;
            }
        });
    }

    public <E extends Exception> Variable performSelection(Variable onVariable,
            ProducerCallback<E> onTrue, ProducerCallback<E> onFalse) throws E {
        // TODO This needs blocks for before, on true, on false, and after
        // and in the case of onTrue and onFalse yielding variables, need to
        // return a variable that represents the selection of the two variables
        block().endWith(new BlockTermination.Condition(onVariable, null, null));
        Block next = graph.createBlock();
        Variable trueVar = null, falseVar = null;
        // do more stuff here
        block = next;
        if (trueVar != null && falseVar != null) {
            return block().addInstruction(
                    new Instruction.OneOf(trueVar, falseVar));
        } else if (trueVar != null || falseVar != null) {
            throw new IllegalArgumentException("");
        } else {
            return null;
        }
    }

    public <E extends Exception> void performTryCatch(
            SimpleCallback<E> tryBuilder, ConsumerCallback<E> catchBuilder,
            SimpleCallback<E> elseBuilder) throws E {
        // TODO This needs to set the exception handler for all the sub blocks of the tryBlock
    }

    public <E extends Exception> void performTryFinally(
            SimpleCallback<E> tryBuilder, SimpleCallback<E> finallyBuilder)
            throws E {
        BlockState state;
        if (blockState != null) {
            state = new BlockState(blockState.onBreak, blockState.onContinue,
                    finallyBuilder);
        } else {
            state = new BlockState(null, null, finallyBuilder);
        }
        BlockBuilderImpl tryBlock = new BlockBuilderImpl(graph, state);
        tryBuilder.generateBlock(tryBlock);
        // TODO: the end of the try-block through the finally block
    }

    public Variable performYield(Variable yield) {
        Block next = graph.createBlock();
        block().endWith(new BlockTermination.Yield(yield, next));
        block = next;
        return block().addInstruction(new Instruction.CoroutineLoad());
    }

    public void terminate(BlockTermination termination) {
        termination.of(block(), blockState);
    }

    public Variable valueOperation(ValueOperation op, Variable... variables) {
        return block.addInstruction(new Instruction.ValueInstruction(op,
                variables));
    }

    public void voidOperation(VoidOperation op, Variable... variables) {
        block.addInstruction(new Instruction.VoidInstruction(op, variables));
    }

    private Block virtual() {
        // TODO this should create a vitual next block, that is this block, or a set of blocks until some instruction is generated
        return null;
    }

    private abstract class VirtualBlock implements Block {

    }
}
