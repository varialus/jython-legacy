package org.python.util.install.driver;

import java.io.File;

public class SilentAutotest {

    private File _targetDir;
    private String[] _commandLineArgs;

    protected SilentAutotest(File targetDir) {
        _targetDir = targetDir;
    }

    protected File getTargetDir() {
        return _targetDir;
    }

    protected String[] getCommandLineArgs() {
        return _commandLineArgs;
    }

    protected void setCommandLineArgs(String[] commandLineArgs) {
        _commandLineArgs = commandLineArgs;
    }

    protected void assertTargetDirNotEmpty() throws DriverException {
        if (_targetDir != null) {
            if (_targetDir.exists() && _targetDir.isDirectory()) {
                if (_targetDir.listFiles().length > 0) {
                    return;
                }
            }
        }
        throw new DriverException("installation failed for " + getTargetDir().getAbsolutePath());
    }
}
