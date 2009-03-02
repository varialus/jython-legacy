package org.python.compiler.instructions;

import org.thobe.compiler.sea.VoidOperation;

public enum SystemCalls implements VoidOperation {
    ASSERT, PRINT, PRINT_WITH_NEWLINE, PRINT_TO, PRINT_TO_WITH_NEWLINE
}
