package org.python.util.install.driver;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.StringTokenizer;

import org.python.util.install.ChildProcess;
import org.python.util.install.Installation;

public class NormalVerifier implements Verifier {

    protected static final String AUTOTEST_PY = "autotest.py";
    private static final String JYTHON_UP = "jython up and running";
    private static final String JYTHON = "jython";

    private File _targetDir;

    public void setTargetDir(File targetDir) {
        _targetDir = targetDir;
    }

    public File getTargetDir() {
        return _targetDir;
    }

    public void verify() throws DriverException {
        // create the test .py script
        createTestScriptFile();

        // start jython and capture the output
        ChildProcess childProcess = new ChildProcess(getCommand());
        childProcess.setDebug(true);
        ByteArrayOutputStream redirectedErr = new ByteArrayOutputStream();
        ByteArrayOutputStream redirectedOut = new ByteArrayOutputStream();
        int exitValue = 0;
        PrintStream oldErr = System.err;
        PrintStream oldOut = System.out;
        try {
            System.setErr(new PrintStream(redirectedErr));
            System.setOut(new PrintStream(redirectedOut));
            exitValue = childProcess.run();
        } finally {
            System.setErr(oldErr);
            System.setOut(oldOut);
        }

        // verify the output
        String output = null;
        String error = null;
        try {
            redirectedErr.flush();
            redirectedOut.flush();
            String encoding = "US-ASCII";
            output = redirectedOut.toString(encoding);
            error = redirectedErr.toString(encoding);
        } catch (IOException ioe) {
            throw new DriverException(ioe);
        }
        if (exitValue != 0) {
            throw new DriverException("start of jython failed, output:\n" + output + "\nerror:\n" + error);
        }
        verifyError(error);
        verifyOutput(output);
    }

    /**
     * will be overridden in subclass StandaloneVerifier
     * 
     * @return the command array to start jython with
     * @throws DriverException if there was a problem getting the target directory path
     */
    protected String[] getCommand() throws DriverException {
        String parentDirName = null;
        try {
            parentDirName = getTargetDir().getCanonicalPath() + File.separator;
        } catch (IOException ioe) {
            throw new DriverException(ioe);
        }
        String[] command = new String[2];
        if (Installation.isWindows()) {
            command[0] = parentDirName + JYTHON + ".bat";
        } else {
            command[0] = parentDirName + JYTHON;
        }
        command[1] = parentDirName + AUTOTEST_PY;
        return command;
    }

    private void verifyError(String error) throws DriverException {
        StringTokenizer tokenizer = new StringTokenizer(error, "\n");
        while (tokenizer.hasMoreTokens()) {
            String line = tokenizer.nextToken();
            if (!line.startsWith("*sys-package-mgr*")) {
                throw new DriverException(error);
            }
        }
    }

    private void verifyOutput(String output) throws DriverException {
        boolean started = false;
        StringTokenizer tokenizer = new StringTokenizer(output, "\n");
        while (tokenizer.hasMoreTokens()) {
            String line = tokenizer.nextToken();
            if (!line.startsWith("[ChildProcess]")) {
                if (line.startsWith(JYTHON_UP)) {
                    started = true;
                } else {
                    throw new DriverException(output);
                }
            }
        }
        if (!started) {
            throw new DriverException("start of jython failed:\n" + output);
        }
    }

    private String getTestScript() {
        StringBuffer buf = new StringBuffer();
        buf.append("import sys\n");
        buf.append("print '" + JYTHON_UP + "'\n");
        return buf.toString();
    }

    private void createTestScriptFile() throws DriverException {
        File file = new File(getTargetDir(), AUTOTEST_PY);
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(getTestScript());
            writer.flush();
            writer.close();
        } catch (IOException ioe) {
            throw new DriverException(ioe);
        }
    }

}
