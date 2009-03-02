package org.python.compiler.flowgraph;

import java.util.Map;

public class CodeGraph {
    private final Map<Object, Constant> constantPool;
    private final CodeReference reference = new CodeReference(this);
    private final Variable implicitArgument;
    private BlockBuilder builder = new BlockBuilderImpl(this, null);

    CodeGraph(GraphInfo info, Map<Object, Constant> constantPool) {
        this.constantPool = constantPool;
        implicitArgument = (Variable) from(info);
    }

    private <T> T from(T t) {
        return t; // Temporary method, until interfaces are defined.
    }

    public Variable handle() {
        return reference;
    }

    public <E extends Exception> void generateCode(SimpleCallback<E> generator)
            throws E {
        generator.generateBlock(body());
    }

    public <E extends Exception> void generateCode(ConsumerCallback<E> generator)
            throws E {
        generator.generateConsumer(body(), implicitArgument);
    }

    private BlockBuilder body() {
        try {
            return builder;
        } finally {
            builder = null;
        }
    }

    public Variable getConstant(Object value) {
        if (value instanceof Variable) {
            Variable variable = (Variable) value;
            if (!variable.isConstant()) {
                throw new IllegalArgumentException();
            }
            return variable;
        }
        Constant result = constantPool.get(value);
        if (result == null) {
            constantPool.put(value, result = new Constant(value));
        }
        return result;
    }

    public static Variable Void() {
        return Constant.VOID;
    }

    public static Variable None() {
        return Constant.NONE;
    }

    public static Variable Ellipsis() {
        return Constant.ELLIPSIS;
    }

    public static Variable True() {
        return Constant.TRUE;
    }

    public static Variable False() {
        return Constant.FALSE;
    }

    public static Variable EnterMethod() {
        return Constant.ENTER;
    }

    public static Variable ExitMethod() {
        return Constant.EXIT;
    }

    /**
     * @param id
     * @return a handle that can be used to load and store to the local
     *         variable.
     */
    public Variable getVariable(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    public Variable globals() {
        // TODO Auto-generated method stub
        return null;
    }

    public Variable locals() {
        // TODO Auto-generated method stub
        return null;
    }

    public void accept(CodeGraphVisitor visitor) {
        // TODO Auto-generated method stub

    }

    Block createBlock() {
        // TODO Auto-generated method stub
        return null;
    }
}
