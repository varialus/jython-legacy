package org.python.compiler.sea;

import org.thobe.compiler.sea.Value;

public abstract class CallStrategy {
    Value call(GeneratorCallback invoke, Value callable, Value[] arguments) {
        // TODO Auto-generated method stub
        return null;
    }

    Value call(GeneratorCallback invoke, Value callable, String[] keywords,
            Value[] arguments, Value[] keyword_arguments) {
        // TODO Auto-generated method stub
        return null;
    }

    Value callVar(GeneratorCallback invoke, Value callable, Value[] arguments,
            Value varargs) {
        // TODO Auto-generated method stub
        return null;
    }

    Value callKey(GeneratorCallback invoke, Value callable, Value[] arguments,
            Value kwarg) {
        // TODO Auto-generated method stub
        return null;
    }

    Value callVar(GeneratorCallback invoke, Value callable, String[] keywords,
            Value[] arguments, Value[] keyword_arguments, Value varargs) {
        // TODO Auto-generated method stub
        return null;
    }

    Value callKey(GeneratorCallback invoke, Value callable, String[] keywords,
            Value[] arguments, Value[] keyword_arguments, Value kwargs) {
        // TODO Auto-generated method stub
        return null;
    }

    Value call(GeneratorCallback invoke, Value callable, Value[] arguments,
            Value varargs, Value kwargs) {
        // TODO Auto-generated method stub
        return null;
    }

    Value call(GeneratorCallback invoke, Value callable, String[] keywords,
            Value[] arguments, Value[] keyword_arguments, Value varargs,
            Value kwargs) {
        // TODO Auto-generated method stub
        return null;
    }
}
