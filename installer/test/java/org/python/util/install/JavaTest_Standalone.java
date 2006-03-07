package org.python.util.install;

import java.io.File;

import org.python.util.install.Installation.JavaVersionInfo;

/**
 * Helper class to test an external java version
 */
public class JavaTest_Standalone {

    public static void main(String[] args) {
        if (args.length > 0) {
            File javaHome = new File(args[0]);
            JavaVersionInfo versionInfo = Installation.getExternalJavaVersion(javaHome);
            if (versionInfo.getErrorCode() != Installation.NORMAL_RETURN) {
                System.err.println(versionInfo.getReason());
            } else {
                System.out.println(getPrefix() + "java version:" + versionInfo.getVersion());
                System.out.println(getPrefix() + "java spec version:" + versionInfo.getSpecificationVersion());
            }
            System.exit(versionInfo.getErrorCode());
        } else {
            System.err.println(getPrefix() + "missing argument: please specify the java home directory "
                    + "(/bin directory assumed below)");
            System.exit(1);
        }
    }

    private static String getPrefix() {
        return "[JavaTest_Standalone] ";
    }

}
