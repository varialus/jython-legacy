package org.python.compiler.instructions;

import org.thobe.compiler.sea.VoidOperation;

public enum MemoryOperation implements VoidOperation {
    STORE, STORE_ATTRIBUTE, STORE_ITEM, DELETE, DELETE_ATTRIBUTE, DELETE_ITEM,
}
