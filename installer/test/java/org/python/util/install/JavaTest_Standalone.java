package org.python.util.install;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Helper class to test an external java
 * 
 * <p>
 * <b>Assumption</b>: the current directory is the one of "this" classpath
 */
public class JavaTest_Standalone {

    private static final long TIMEOUT = 10000; // 10 seconds

    private static String _binDirectory; // binary directory for the external vm

    public static void main(String[] args) {
        if (args.length > 0) {
            _binDirectory = args[0];
        } else {
            System.err.println("missing argument: please specify the /bin directory of the java version to test");
            System.exit(1);
        }

        // launch the java command
        File binDirectory = new File(_binDirectory);
        if (binDirectory.exists() && binDirectory.isDirectory()) {
            try {
                // temporary file will be written by the child process
                File tempFile = File.createTempFile("jython_installation", ".properties");
                if (tempFile.exists() && tempFile.canWrite()) {
                    String command[] = new String[5];
                    command[0] = binDirectory.getAbsolutePath() + File.separator + "java";
                    command[1] = "-cp";
                    command[2] = ".";
                    command[3] = "org.python.util.install.JavaVersionTester";
                    command[4] = tempFile.getAbsolutePath();

                    ChildProcess childProcess = new ChildProcess(command, TIMEOUT);
                    childProcess.setDebug(true);
                    int exitValue = childProcess.run();
                    if (exitValue != 0) {
                        System.err.println(getPrefix() + "got error code " + exitValue);
                    } else {
                        System.out.println(getPrefix() + "got a normal return");
                    }
                    readTempFile(tempFile);
                    System.exit(exitValue);
                } else {
                    System.err.println("problems creating temp file");
                    System.exit(1);
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        } else {
            System.err.println(_binDirectory + " is not an existing directory");
            System.exit(1);
        }
    }

    private static String getPrefix() {
        return "[JavaTest_Standalone] ";
    }

    private static void readTempFile(File tempFile) throws IOException {
        Properties tempProperties = new Properties();
        tempProperties.load(new FileInputStream(tempFile));
        tempProperties.list(System.out);
    }

}
