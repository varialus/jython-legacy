package org.python.compiler.sea;

import org.thobe.compiler.sea.InvocationType;
import org.thobe.compiler.sea.Value;

public abstract class GeneratorCallback {
    public abstract Value invoke(InvocationType type, Value... arguments);

    public abstract Value string(String string);

    public abstract Value array(Value... content);

    public final Value stringArray(String... strings) {
        Value[] array = new Value[strings.length];
        for (int i = 0; i < strings.length; i++) {
            array[i] = string(strings[i]);
        }
        return array(array);
    }
}
