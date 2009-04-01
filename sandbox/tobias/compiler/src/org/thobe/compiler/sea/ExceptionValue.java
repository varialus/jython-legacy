package org.thobe.compiler.sea;

public class ExceptionValue extends Value {
    ExceptionValue() {
        throw new UnsupportedOperationException(
                "How should exceptions be created?");
    }

    @Override
    public String toString() {
        return "ExceptionValue[how did you get this?]";
    }
}
