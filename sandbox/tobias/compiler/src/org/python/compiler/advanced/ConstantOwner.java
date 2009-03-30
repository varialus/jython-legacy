package org.python.compiler.advanced;

public interface ConstantOwner<RESULT> {
    RESULT True();

    RESULT False();

    RESULT None();

    RESULT Ellipsis();

    RESULT loadConstant(Constant token);
    
    RESULT loadConstant(String name);
}
