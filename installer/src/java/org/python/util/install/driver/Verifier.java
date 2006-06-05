package org.python.util.install.driver;

import java.io.File;

public interface Verifier {

    public void setTargetDir(File targetDir);

    public File getTargetDir();

    public void verify() throws DriverException;

}
