package org.python.compiler.advanced;

public class ClassEnvironment extends AbstractEnvironment {

    public ClassEnvironment(Environment parent, CompilerPolicy policy) {
        super(parent, policy);
    }

    public void addParameter(String name) {
        error(EnvironmentError.CANNOT_HAVE_PARAMETERS);
    }
}
