package org.python.util.install;

import java.awt.GraphicsEnvironment; // should be allowed on headless systems
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class Installation {
    protected static final String OPTION_START = "-";
    protected static final String CONSOLE_OPTION = "-console";
    protected static final String ALL = "1";
    protected static final String STANDARD = "2";
    protected static final String MINIMUM = "3";
    protected static final String OS_NAME = "os.name";
    protected static final String OS_VERSION = "os.version";
    protected static final String JAVA_VERSION = "java.version";
    protected static final String JAVA_VENDOR = "java.vendor";

    private static final String RESOURCE_CLASS = "org.python.util.install.TextConstants";

    private static ResourceBundle _textConstants = ResourceBundle.getBundle(RESOURCE_CLASS, Locale.getDefault());

    public static void main(String args[]) {
        Map arguments = new HashMap();
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith(OPTION_START)) {
                arguments.put(args[i], Boolean.TRUE);
                if (i < args.length - 1) {
                    if (!args[i + 1].startsWith(OPTION_START)) {
                        arguments.put(args[i], args[i + 1]);
                        i++;
                    }
                }
            } else {
                i++;
            }
        }
        try {
            JarInfo jarInfo = new JarInfo();
            if (!useGUI(arguments)) {
                ConsoleInstaller consoleInstaller = new ConsoleInstaller(jarInfo);
                consoleInstaller.install();
                System.exit(0);
            } else {
                new FrameInstaller(jarInfo);
            }
        } catch (InstallationCancelledException ice) {
            // this is intended:
            System.out.println(Installation.getText(TextKeys.INSTALLATION_CANCELLED));
            System.exit(1);
        } catch (InstallerException ie) {
            ie.printStackTrace();
            System.exit(1);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    protected static String getText(String key) {
        return _textConstants.getString(key);
    }

    protected static String getText(String key, String parameter0) {
        String parameters[] = new String[1];
        parameters[0] = parameter0;
        return MessageFormat.format(_textConstants.getString(key), parameters);
    }

    protected static String getText(String key, String parameter0, String parameter1) {
        String parameters[] = new String[2];
        parameters[0] = parameter0;
        parameters[1] = parameter1;
        return MessageFormat.format(_textConstants.getString(key), parameters);
    }

    protected static void setLanguage(Locale locale) {
        _textConstants = ResourceBundle.getBundle(RESOURCE_CLASS, locale);
    }

    protected static boolean isValidOs() {
        String osName = System.getProperty(OS_NAME, "");
        String lowerOs = osName.toLowerCase();
        if (isWindows())
            return true;
        if (lowerOs.indexOf("linux") >= 0)
            return true;
        if (lowerOs.indexOf("mac") >= 0)
            return true;
        if (lowerOs.indexOf("unix") >= 0)
            return true;
        return false;
    }

    protected static boolean isValidJava() {
        String javaVersion = System.getProperty(JAVA_VERSION, "");
        String lowerJava = javaVersion.toLowerCase();
        if (lowerJava.startsWith("1.2"))
            return true;
        if (lowerJava.startsWith("1.3"))
            return true;
        if (lowerJava.startsWith("1.4"))
            return true;
        if (lowerJava.startsWith("1.5"))
            return true;
        return false;
    }

    protected static boolean isWindows() {
        boolean isWindows = false;
        String osName = System.getProperty(OS_NAME, "");
        if (osName.toLowerCase().indexOf("windows") >= 0) {
            isWindows = true;
        }
        return isWindows;
    }

    protected static boolean isMacintosh() {
        boolean isMacintosh = false;
        String osName = System.getProperty(OS_NAME, "");
        if (osName.toLowerCase().indexOf("mac") >= 0) {
            isMacintosh = true;
        }
        return isMacintosh;
    }

    protected static boolean isJDK141() {
        boolean isJDK141 = false;
        String javaVersion = System.getProperty(JAVA_VERSION, "");
        if (javaVersion.toLowerCase().startsWith("1.4.1")) {
            isJDK141 = true;
        }
        return isJDK141;
    }
    
    protected static String getReadmeText(String targetDirectory) {
        File readmeFile = new File(targetDirectory, "README.txt");
        BufferedReader reader = null;
        StringBuffer buffer = new StringBuffer();
        try {
            reader = new BufferedReader(new FileReader(readmeFile));
            for (String s; (s = reader.readLine()) != null;) {
                buffer.append(s);
                buffer.append("\n");
            }
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return buffer.toString();
    }
    
    private static boolean useGUI(Map arguments) {
        if (arguments.containsKey(CONSOLE_OPTION)) {
            return false;
        } else {
            try {
                GraphicsEnvironment.getLocalGraphicsEnvironment();
                return true;
            } catch (Throwable t) {
                return false;
            }
        }
    }

}