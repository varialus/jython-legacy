package org.thobe.compiler.sea;

import org.python.compiler.sea.Variable;

public abstract class GraphBuilder {
    protected GraphBuilder(NamespacePopulator populator) {
        populator.populate(new VariableFactory(){});
    }

    protected InvocationNode invoke(InvocationType invocation,
            Value... arguments) {
        return new InvocationNode(invocation, arguments);
    }

    protected ArrayNode array(Value[] content) {
        return new ArrayNode(content);
    }

    protected <N extends Node> N schedule(N node) {
        // TODO Auto-generated method stub
        return node;
    }

    protected <V extends Value> V schedule(V value) {
        // TODO Auto-generated method stub
        return value;
    }

    protected Continuation replaceContinuation(Continuation progression) {
        // TODO Auto-generated method stub
        return null;
    }

    protected Continuation currentContinuation() {
        // TODO Auto-generated method stub
        return null;
    }

    protected void nextContinuation(Continuation start) {
        // TODO Auto-generated method stub

    }

    protected Continuation newContinuation() {
        // TODO Auto-generated method stub
        return null;
    }

    protected Continuation merge(Continuation... progressions) {
        // TODO Auto-generated method stub
        return null;
    }

    protected Value phi(Value... values) {
        return new PhiNode(values).result();
    }

    protected Value phiOrNull(Value... values) {
        boolean hasNull = false;
        for (int i = 0; i < values.length; i++) {
            if (values[i] == null) {
                if (hasNull) {
                    continue;
                } else if (i == 0) {
                    hasNull = true;
                } else {
                    throw new IllegalArgumentException(
                            "Either all or no arguments to phiOrNull needs to be null.");
                }
            } else if (hasNull) {
                throw new IllegalArgumentException(
                        "Either all or no arguments to phiOrNull needs to be null.");
            }
        }
        if (hasNull) {
            return null;
        } else {
            return phi(values);
        }
    }

    protected final ValueNode loadVariable(Variable variable) {
        return new LoadNode(variable);
    }

    protected final Node storeVariable(Variable variable, Value value) {
        return new StoreNode(variable, value);
    }

    protected final Value NULL() {
        // TODO Auto-generated method stub
        return null;
    }

    public Node throwException(ExceptionValue exception) {
        return new ThrowNode(exception);
    }

    public Variable variable(String name) {
        return null; // TODO: something with the frame
    }

    public Variable argument(int index) {
        return null; // TODO: something with the frame
    }
}
