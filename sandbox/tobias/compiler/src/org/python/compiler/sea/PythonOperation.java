package org.python.compiler.sea;

import java.util.EnumMap;
import java.util.Map;

import org.python.antlr.ast.operatorType;
import org.python.antlr.ast.unaryopType;
import org.thobe.compiler.sea.InvocationType;

public enum PythonOperation implements InvocationType {
    // Constants
    LOAD_TRUE, LOAD_FALSE, LOAD_NONE, LOAD_ELLIPSIS, LOAD_ASSERTION_ERROR,
    // Unpack
    UNPACK, UNPACK_STAR,
    // Construction
    MAKE_TUPLE, MAKE_LIST, MAKE_EMPTY_DICT, APPEND_LIST,
    // Print / Exec
    EXEC, PRINT, PRINT_NEWLINE, PRINT_TO, PRINT_NEWLINE_TO, PRINT_EXPRESSION,
    // Attributes
    GET_ATTRIBUTE, SET_ATTRIBUTE, DELETE_ATTRIBUTE,
    // Subscript
    GET_SLICE, SET_SLICE, DELETE_SLICE, GET_ITEM, SET_ITEM, DELETE_ITEM,
    // Binary operators
    BINARY_ADD, BINARY_SUB, BINARY_MULT, BINARY_DIV, BINARY_MOD, BINARY_POW, BINARY_LSHIFT, BINARY_RSHIFT, BINARY_BIT_OR, BINARY_BIT_XOR, BINARY_BIT_AND, BINARY_FLOOR_DIV,
    // In place operators
    INPLACE_ADD, INPLACE_SUB, INPLACE_MULT, INPLACE_DIV, INPLACE_MOD, INPLACE_POW, INPLACE_LSHIFT, INPLACE_RSHIFT, INPLACE_BIT_OR, INPLACE_BIT_XOR, INPLACE_BIT_AND, INPLACE_FLOOR_DIV,
    // Unary operators
    UNARY_INVERT, UNARY_NOT, UNARY_ADD, UNARY_SUB, STRING_REPRESENTATION,
    // --- end ---
    ;
    private static final Map<operatorType, PythonOperation> BINARY_OPERATORS = new EnumMap<operatorType, PythonOperation>(
            operatorType.class);
    private static final Map<operatorType, PythonOperation> INPLACE_OPERATORS = new EnumMap<operatorType, PythonOperation>(
            operatorType.class);
    private static final Map<unaryopType, PythonOperation> UNARY_OPERATORS = new EnumMap<unaryopType, PythonOperation>(
            unaryopType.class);
    static {
        // Binary operators
        BINARY_OPERATORS.put(operatorType.Add, BINARY_ADD);
        BINARY_OPERATORS.put(operatorType.Sub, BINARY_SUB);
        BINARY_OPERATORS.put(operatorType.Mult, BINARY_MULT);
        BINARY_OPERATORS.put(operatorType.Div, BINARY_DIV);
        BINARY_OPERATORS.put(operatorType.Mod, BINARY_MOD);
        BINARY_OPERATORS.put(operatorType.Pow, BINARY_POW);
        BINARY_OPERATORS.put(operatorType.LShift, BINARY_LSHIFT);
        BINARY_OPERATORS.put(operatorType.RShift, BINARY_RSHIFT);
        BINARY_OPERATORS.put(operatorType.BitOr, BINARY_BIT_OR);
        BINARY_OPERATORS.put(operatorType.BitXor, BINARY_BIT_XOR);
        BINARY_OPERATORS.put(operatorType.BitAnd, BINARY_BIT_AND);
        BINARY_OPERATORS.put(operatorType.FloorDiv, BINARY_FLOOR_DIV);
        // In place operators
        INPLACE_OPERATORS.put(operatorType.Add, INPLACE_ADD);
        INPLACE_OPERATORS.put(operatorType.Sub, INPLACE_SUB);
        INPLACE_OPERATORS.put(operatorType.Mult, INPLACE_MULT);
        INPLACE_OPERATORS.put(operatorType.Div, INPLACE_DIV);
        INPLACE_OPERATORS.put(operatorType.Mod, INPLACE_MOD);
        INPLACE_OPERATORS.put(operatorType.Pow, INPLACE_POW);
        INPLACE_OPERATORS.put(operatorType.LShift, INPLACE_LSHIFT);
        INPLACE_OPERATORS.put(operatorType.RShift, INPLACE_RSHIFT);
        INPLACE_OPERATORS.put(operatorType.BitOr, INPLACE_BIT_OR);
        INPLACE_OPERATORS.put(operatorType.BitXor, INPLACE_BIT_XOR);
        INPLACE_OPERATORS.put(operatorType.BitAnd, INPLACE_BIT_AND);
        INPLACE_OPERATORS.put(operatorType.FloorDiv, INPLACE_FLOOR_DIV);
        // Unary operators
        UNARY_OPERATORS.put(unaryopType.Invert, UNARY_INVERT);
        UNARY_OPERATORS.put(unaryopType.Not, UNARY_NOT);
        UNARY_OPERATORS.put(unaryopType.UAdd, UNARY_ADD);
        UNARY_OPERATORS.put(unaryopType.USub, UNARY_SUB);
    }

    public static PythonOperation binaryOperator(operatorType op) {
        return BINARY_OPERATORS.get(op);
    }

    public static PythonOperation inplaceOperator(operatorType op) {
        return INPLACE_OPERATORS.get(op);
    }

    public static PythonOperation unaryOperator(unaryopType op) {
        return UNARY_OPERATORS.get(op);
    }
}
