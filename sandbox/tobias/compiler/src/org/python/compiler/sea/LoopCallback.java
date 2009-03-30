package org.python.compiler.sea;

import org.thobe.compiler.sea.Value;

public abstract class LoopCallback {
    protected abstract StateCarrier head() throws Exception;
    protected abstract void body(Value payload, LoopHandle loop) throws Exception;
    protected abstract void orelse() throws Exception;
}
