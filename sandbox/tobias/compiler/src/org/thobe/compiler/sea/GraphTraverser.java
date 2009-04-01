package org.thobe.compiler.sea;

abstract class GraphTraverser {
    abstract class SelectTraverser {
        abstract void onCase(Integer key, Node node);

        abstract void otherwise(Node node);

        abstract SwitchNode done();
    }

    abstract SelectTraverser select(SwitchNode original, Value select);

    abstract IfNode selection(IfNode original, Value predicate,
            Node trueSuccessor, Node falseSuccessor);

    abstract ReturnNode returnValue(ReturnNode original, Value value);

    abstract StoreNode store(StoreNode original, Variable variable, Value value);

    abstract ThrowNode raiseException(ThrowNode original,
            ExceptionValue exception);

    abstract InvocationNode invoke(InvocationNode original,
            InvocationType invocation, Value[] arguments);

    abstract LoadNode load(LoadNode original, Variable variable);

    abstract PhiNode phi(PhiNode original, Node[] array);

    abstract ArrayNode array(ArrayNode original, Value[] content);
}
