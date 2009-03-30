package org.python.compiler.advanced;

public abstract class ConstantCodeGenerator<RESULT> implements
        ConstantOwner<RESULT> {
    public abstract RESULT[] resultArray(int size);

    public abstract void tuple(Constant token, RESULT... children);

    public abstract void string(Constant token, String string);

    public abstract void integer(Constant token, int number);

    public abstract void integer(Constant token, long number);

    public abstract void integer(Constant token, String number);

    public abstract void floatingpoint(Constant token, float number);

    public abstract void floatingpoint(Constant token, double number);

    public abstract void floatingpoint(Constant token, String number);
}
