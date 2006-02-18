package org.python.util.install;

import java.io.File;
import java.io.FileWriter;

/**
 * Helper class to test a java version
 */
public class JavaVersionTester {

    private static final String JAVA_VERSION = "java.version";
    private static final String JAVA_SPECIFICATION_VERSION = "java.specification.version";
    private static final String JAVA_HOME = "java.home";
    private static final String NEWLINE = "\n";

    private static final String UNKNOWN = "<unknown>";

    public static void main(String[] args) {
        if (args.length > 0) {
            String tempFilePath = args[0];
            File tempFile = new File(tempFilePath);
            if (tempFile.exists() && tempFile.canWrite()) {
                try {
                    writeTempFile(tempFile);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            } else {
                System.err.println("problems with temp file " + tempFilePath);
                System.exit(1);
            }
        } else {
            System.err.println("no temp file given");
            System.out.println("exiting with 1");
            System.exit(1);
        }
    }

    private static void writeTempFile(File file) throws Exception {
        FileWriter writer = new FileWriter(file);
        writer.write(createFileContent());
        writer.flush();
        writer.close();
    }

    private static String createFileContent() {
        StringBuffer sb = new StringBuffer(500);
        String java_home = System.getProperty(JAVA_HOME, UNKNOWN);
        if (File.separatorChar != '/') {
            java_home = java_home.replace(File.separatorChar, '/'); // backslash would be interpreted as escape char
        }
        sb.append(JAVA_HOME + "=" + java_home + NEWLINE);
        sb.append(JAVA_VERSION + "=" + System.getProperty(JAVA_VERSION, UNKNOWN) + NEWLINE);
        sb.append(JAVA_SPECIFICATION_VERSION + "=" + System.getProperty(JAVA_SPECIFICATION_VERSION, UNKNOWN) + NEWLINE);
        return sb.toString();
    }

}
