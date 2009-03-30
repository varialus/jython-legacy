package org.python.compiler.sea;

public abstract class LoopHandle {
    LoopHandle() {

    }

    public abstract void breakLoop();

    public abstract void continueLoop();
}
