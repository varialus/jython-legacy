package org.thobe.compiler.sea;

public class ArrayValue extends Value {
    private final ArrayNode from;

    ArrayValue(ArrayNode from) {
        this.from = from;
    }

    public Value[] array(int size) {
        // TODO Auto-generated method stub
        return null;
    }

    public Value[] array() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String toString() {
        return "value_of(" + from + ")";
    }
}
