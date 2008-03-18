package org.python.newcompiler;

import org.python.bytecode.Label;

public class ClassEnvironment extends AbstractEnvironment {

    public ClassEnvironment(Environment parent) {
        super(parent);
    }

    @Override
    public void addParameter(String name) {
        error(EnvironmentError.CANNOT_HAVE_PARAMETERS);
    }
}
