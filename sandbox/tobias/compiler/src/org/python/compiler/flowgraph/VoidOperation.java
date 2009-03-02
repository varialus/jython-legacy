package org.python.compiler.flowgraph;

public enum VoidOperation {
    STORE, STORE_ATTRIBUTE, STORE_ITEM,
    DELETE, DELETE_ATTRIBUTE, DELETE_ITEM,
    // Type specific operations
    LIST_APPEND,
    // System calls
    ASSERT, PRINT, PRINT_WITH_NEWLINE, PRINT_TO, PRINT_TO_WITH_NEWLINE
}
