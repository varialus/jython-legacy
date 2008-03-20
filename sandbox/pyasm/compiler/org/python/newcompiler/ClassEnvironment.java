package org.python.newcompiler;

public class ClassEnvironment extends AbstractEnvironment {

    public ClassEnvironment(Environment parent) {
        super(parent);
    }

    public void addParameter(String name) {
        error(EnvironmentError.CANNOT_HAVE_PARAMETERS);
    }
}
