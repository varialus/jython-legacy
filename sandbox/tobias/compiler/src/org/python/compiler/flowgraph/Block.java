package org.python.compiler.flowgraph;

import org.python.compiler.flowgraph.Instruction.IteratorNext;

interface Block {

    public void endWith(BlockTermination transition);

    public Variable addInstruction(Instruction instr);

}
