package org.python.newcompiler;

import java.util.Set;

public interface CodeInfo {

    int getArgumentCount();

    int getLocalsCount();

    int getMaxStackSize();

    String getFilename();

    String getName();
}
