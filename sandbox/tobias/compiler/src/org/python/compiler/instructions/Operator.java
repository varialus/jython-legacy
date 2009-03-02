package org.python.compiler.instructions;

import org.thobe.compiler.sea.ValueOperation;

public enum Operator implements ValueOperation {
    // Binary operators
    ADD, SUBTRACT, MULTIPLY, FLOOR_DIVIDE, TRUE_DIVIDE, MODULO, POWER, SHIFT_LEFT, SHIFT_RIGHT, AND, OR, XOR,
    // Binary internal operators
    AUG_ADD, AUG_SUBTRACT, AUG_MULTIPLY, AUG_FLOOR_DIVIDE, AUG_TRUE_DIVIDE, AUG_MODULO, AUG_POWER, AUG_SHIFT_LEFT, AUG_SHIFT_RIGHT, AUG_AND, AUG_OR, AUG_XOR,
    // Unary operators
    POSITIVE, NEGATIVE, NOT, INVERT, GET_ITERATOR,
    // Comparison operators
    EQUAL, NOT_EQUAL, LESS_THAN, LESS_THAN_OR_EQUAL_TO, GREATER_THAN, GREATER_THAN_OR_EQUAL_TO, IS, IS_NOT, CONTAINED_IN, NOT_CONTAINED_IN,
}
