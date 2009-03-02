package org.python.compiler.instructions;

import org.thobe.compiler.sea.ValueOperation;

public enum Import implements ValueOperation {
    IMPORT_NAME, IMPORT_ABSOLUTE, IMPORT_FROM,
}
