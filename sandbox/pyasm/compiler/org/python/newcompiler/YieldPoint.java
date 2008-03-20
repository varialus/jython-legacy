package org.python.newcompiler;

import org.python.bytecode.BytecodeVisitor;
import org.python.bytecode.Instruction;
import org.python.bytecode.Label;

public class YieldPoint implements Instruction {

    public final Label label;

    public final int index;

    public YieldPoint(Label label, int index) {
        this.label = label;
        this.index = index;
    }

    public void accept(BytecodeVisitor visitor) {
        visitor.visitYield(index, label);
    }
}
