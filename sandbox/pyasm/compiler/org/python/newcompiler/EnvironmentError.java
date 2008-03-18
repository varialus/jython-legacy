package org.python.newcompiler;

public enum EnvironmentError {
    PARAMETER_AS_GLOBAL, BOTH_NONLOCAL_AND_GLOBAL, PARAMETER_AS_NONLOCAL, ILLEGAL_GENERATOR, CANNOT_HAVE_PARAMETERS, DUPLICATE_PARAMETER_NAMES, FUTURE_OUT_OF_CONTEXT;

    public EnvironmentProblem visit(int lineNumber) {
        return new EnvironmentProblem(this, lineNumber);
    }

    public static class EnvironmentProblem {

        public EnvironmentProblem(EnvironmentError environmentError, int lineNumber) {
        // TODO Auto-generated constructor stub
        }
    }
}
