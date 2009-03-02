package org.python.compiler.flowgraph;

/**
 * Only assigned once!
 * 
 * @author Tobias Ivarsson
 */
public class Variable {

    private final Block origin;

    Variable(Block origin) {
        this.origin = origin;
    }

    public boolean isConstant() {
        return false;
    }

    public boolean isValidIn(Block block) {
        return origin == block;
    }

}
