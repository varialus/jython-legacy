package org.python.compiler.sea;

import org.thobe.compiler.sea.Value;

public abstract class UnrollerCallback<T> {
    protected abstract StateCarrier generate(Value previous, T node)
            throws Exception;
}
