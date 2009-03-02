package org.python.compiler.flowgraph;


public interface BlockBuilder {
    public void voidOperation(VoidOperation op, Variable... variables);

    public Variable valueOperation(ValueOperation op, Variable... variables);

    public Variable performYield(Variable yield);

    public void terminate(BlockTermination termination);

    public boolean isOpen();

    public <E extends Exception> Variable performSelection(Variable onVariable,
            ProducerCallback<E> onTrue, ProducerCallback<E> onFalse)
            throws E;

    public <E extends Exception> void performTryCatch(
            SimpleCallback<E> tryBuilder, ConsumerCallback<E> catchBuilder,
            SimpleCallback<E> elseBuilder) throws E;

    public <E extends Exception> void performTryFinally(
            SimpleCallback<E> tryBuilder, SimpleCallback<E> finallyBuilder)
            throws E;

    public <E extends Exception> void performIteratorLoop(
            ProducerCallback<E> iterableBuilder,
            ConsumerCallback<E> loopBuilder, SimpleCallback<E> elseBuilder)
            throws E;

    public <E extends Exception> void performBooleanLoop(
            ProducerCallback<E> setupBuilder,
            SimpleCallback<E> loopBuilder, SimpleCallback<E> elseBuilder)
            throws E;
}
