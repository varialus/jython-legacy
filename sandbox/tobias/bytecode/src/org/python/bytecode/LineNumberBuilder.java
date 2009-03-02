package org.python.bytecode;

import java.util.HashMap;
import java.util.Map;

public class LineNumberBuilder implements LineNumberTable {

    private Map<Integer, Integer> table;

    public LineNumberBuilder(int lineNumber, char[] lineNumberTable) {
        int bytecodeOffset = 0;
        this.table = new HashMap<Integer, Integer>();
        table.put(bytecodeOffset, lineNumber);
        for (int i = 0; i < lineNumberTable.length;) {
            bytecodeOffset += lineNumberTable[i++];
            char lineInc = lineNumberTable[i++];
            lineNumber += lineInc;
            if (lineInc != 0) {
                table.put(bytecodeOffset, lineNumber);
            }
        }
    }

    public void visitInstruction(BytecodeVisitor visitor, int offset) {
        Integer lineNumber = table.get(offset);
        if (lineNumber != null) {
            visitor.visitLineNumber(lineNumber);
        }
    }
}
