package org.python.util.install.driver;

import java.io.File;

public class FileHelper {

    /**
     * create a temporary directory with the same name as the passed in File (which may exist as file, not directory)
     * 
     * @param tempDirectory
     * @return <code>true</code> only if the the directory was successfully created (or already existed)
     */
    public static boolean createTempDirectory(File tempDirectory) {
        if (!tempDirectory.isDirectory()) {
            if (tempDirectory.exists()) {
                tempDirectory.delete();
            }
            return tempDirectory.mkdir();
        } else {
            return true;
        }
    }

}
