package org.python.compiler.advanced;

import java.util.Set;

public enum CompilerPolicy {
    DEFAULT {

        @Override
        public EnvironmentError verifyFlag(CompilerFlag flag) {
            return null;
        }
    };

    public Set<CompilerFlag> inheritFlags(Set<CompilerFlag> flags) {
        return null;
    }

    public abstract EnvironmentError verifyFlag(CompilerFlag flag);
}
