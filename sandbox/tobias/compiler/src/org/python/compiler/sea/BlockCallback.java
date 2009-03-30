package org.python.compiler.sea;

import org.thobe.compiler.sea.ExceptionValue;

public interface BlockCallback {
    void body() throws Exception;

    void onExceptionalExit(ExceptionValue exception) throws Exception;

    void onNormalExit() throws Exception;
}
