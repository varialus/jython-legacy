package org.python.compiler.instructions;

import org.thobe.compiler.sea.ValueOperation;

public enum Construct implements ValueOperation {
    MAKE_TUPLE, MAKE_LIST, MAKE_DICTIONARY,
    // Expand upon these?
    MAKE_SLICE, MAKE_EXTENDED_SLICE,
}
