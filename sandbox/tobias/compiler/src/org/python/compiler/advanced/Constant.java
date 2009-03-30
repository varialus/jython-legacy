package org.python.compiler.advanced;

public abstract class Constant {
    private final ConstantPool owner;
    private int index;

    Constant(ConstantPool owner) {
        this.owner = owner;
    }

    public final int index() {
        return index;
    }

    <RESULT> void build(ConstantCodeGenerator<RESULT> builder) {
    }

    abstract <RESULT> RESULT loadFrom(ConstantOwner<RESULT> owner);

    @Override
    public abstract String toString();

    boolean isNum() {
        // TODO Auto-generated method stub
        return false;
    }

    boolean isStr() {
        // TODO Auto-generated method stub
        return false;
    }

    boolean isInt() {
        // TODO Auto-generated method stub
        return false;
    }

    boolean isTrue() {
        // TODO Auto-generated method stub
        return false;
    }
}
