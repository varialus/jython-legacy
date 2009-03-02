package org.python.compiler.advanced;

public interface ScopeInfo {

    Iterable<String> getVariableNames();

    Iterable<String> getFreeVariableNames();

    Iterable<String> getCellVariableNames();
}
