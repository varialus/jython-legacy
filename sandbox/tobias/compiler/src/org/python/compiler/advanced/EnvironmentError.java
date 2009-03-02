package org.python.compiler.advanced;

/**
 * TODO: Add code for failing and producing error messages, warnings and so on. 
 * 
 * @author Tobias Ivarsson
 */
public enum EnvironmentError {
    PARAMETER_AS_GLOBAL, BOTH_NONLOCAL_AND_GLOBAL, PARAMETER_AS_NONLOCAL, ILLEGAL_GENERATOR, CANNOT_HAVE_PARAMETERS, DUPLICATE_PARAMETER_NAMES, FUTURE_OUT_OF_CONTEXT;

    public EnvironmentProblem visit(int lineNumber) {
        return new EnvironmentProblem(this, lineNumber);
    }

    public static class EnvironmentProblem {

        private EnvironmentError error;

        private int lineNumber;

        public EnvironmentProblem(EnvironmentError error, int lineNumber) {
            this.error = error;
            this.lineNumber = lineNumber;
        }
    }
}
