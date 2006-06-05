package org.python.util.install.driver;

import java.io.File;

public abstract class AbstractVerifier implements Verifier {

    private File _targetDir;

    public void setTargetDir(File targetDir) {
        _targetDir = targetDir;
    }

    public File getTargetDir() {
        return _targetDir;
    }

}
