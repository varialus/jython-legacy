package org.python.compiler;

import java.util.Map;

import org.python.core.CompilerFlags;

interface ConstraintsDefinition<E> {

    void update(CompilerFlags flags, Map<String, ConstantChecker<E>> constNames);

}
