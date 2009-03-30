package org.python.compiler.sea;

import org.thobe.compiler.sea.Value;

interface InvocationStrategy {
    Value invoke(GeneratorCallback invoke, Value target, String method,
            Value[] arguments);

    Value invoke(GeneratorCallback invoke, Value target, String method,
            String[] keywords, Value[] arguments, Value[] keyword_arguments);

    Value invokeVar(GeneratorCallback invoke, Value target, String method,
            Value[] arguments, Value varargs);

    Value invokeKey(GeneratorCallback invoke, Value target, String method,
            Value[] arguments, Value kwarg);

    Value invokeVar(GeneratorCallback invoke, Value target, String method,
            String[] keywords, Value[] arguments, Value[] keyword_arguments,
            Value varargs);

    Value invokeKey(GeneratorCallback invoke, Value target, String method,
            String[] keywords, Value[] arguments, Value[] keyword_arguments,
            Value kwargs);

    Value invoke(GeneratorCallback invoke, Value target, String method,
            Value[] arguments, Value varargs, Value kwargs);

    Value invoke(GeneratorCallback invoke, Value target, String method,
            String[] keywords, Value[] arguments, Value[] keyword_arguments,
            Value varargs, Value kwargs);

    Value getAttribute(GeneratorCallback invoke, Value owner, String attribute);

    void setAttribute(GeneratorCallback invoke, Value owner, String attribute,
            Value value);

    void delAttribute(GeneratorCallback invoke, Value owner, String attribute);
}
