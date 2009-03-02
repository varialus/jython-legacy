package org.python.bytecode;

public final class Label {

    private static int count = 0;

    private final int id;

    private int index = -1;

    public Label() {
        this.id = count++;
    }

    public void put(int index) {
        if (this.index < 0) {
            this.index = index;
        } else {
            throw new RuntimeException("Cannot add a label to an instruction stream more than once.");
        }
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof Label) && ((Label)other).id == this.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "Label[" + id + "]";
    }
}
