package org.python.newcompiler;

public interface CodeInfo {

    int getArgumentCount();

    int getLocalsCount();

    int getMaxStackSize();

    String getFilename();

    String getName();
}
