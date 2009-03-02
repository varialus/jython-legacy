package org.python.compiler.flowgraph;

class Constant extends Variable {

    public static final Constant NONE = new Constant(null);
    public static final Constant ELLIPSIS = new Constant(null);
    public static final Constant VOID = new Constant(null);
    public static final Constant TRUE = new Constant(null);
    public static final Constant FALSE = new Constant(null);
    public static final Constant ENTER = new Constant(null);
    public static final Constant EXIT = new Constant(null);

    Constant(Object value) {
        super(null);
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public boolean isValidIn(Block block) {
        // Constants are always valid.
        return true;
    }

}
