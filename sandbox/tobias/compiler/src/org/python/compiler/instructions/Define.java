package org.python.compiler.instructions;

import org.thobe.compiler.sea.ValueOperation;

public enum Define implements ValueOperation {
    DEFINE_CLASS, MAKE_FUNCTION,
    //
    FINALIZE_CLASS, FINALIZE_FUNCTION,
}
