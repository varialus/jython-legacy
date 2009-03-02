package org.python.compiler.instructions;

import org.thobe.compiler.sea.ValueOperation;

public enum Exception implements ValueOperation {
    MAKE_EXCEPTION, MATCH_EXCEPTION, EXCEPTION_TYPE, EXCEPTION_VALUE, EXCEPTION_TRACEBACK,
}
