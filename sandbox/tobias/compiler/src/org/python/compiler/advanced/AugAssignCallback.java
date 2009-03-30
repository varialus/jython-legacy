package org.python.compiler.advanced;

public abstract class AugAssignCallback<T> {
    protected abstract T compute(T lhs) throws Exception;
}
