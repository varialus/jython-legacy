package org.python.compiler.advanced;

public interface CodeInfo {

    int getArgumentCount();
    
    Iterable<String> getArgumentNames();

    int getLocalsCount();

    int getMaxStackSize();

    String getFilename();

    String getName();
    
    ScopeInfo getScopeInfo();
}
