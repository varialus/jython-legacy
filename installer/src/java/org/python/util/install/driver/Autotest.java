package org.python.util.install.driver;

import java.io.File;
import java.io.IOException;

import org.python.util.install.FileHelper;
import org.python.util.install.InstallationListener;
import org.python.util.install.InstallerCommandLine;

public abstract class Autotest implements InstallationListener {

    private static final String _DIR_SUFFIX = "_dir";

    private static int _count = 0; // unique test number
    private static File _rootDirectory = null;

    private String _name;
    private File _targetDir;
    private File _javaHome;
    private boolean _verbose;
    private String[] _commandLineArgs;
    private Verifier _verifier;

    /**
     * constructor
     * 
     * @throws IOException
     * @throws DriverException
     */
    protected Autotest(InstallerCommandLine commandLine) throws IOException, DriverException {
        _count++;
        setName();
        if (_rootDirectory == null) {
            createRootDirectory();
        }
        createTargetDirectory();
        setCommandLineArgs(new String[0]); // a priori value
        _verbose = commandLine.hasVerboseOption();
        _javaHome = commandLine.getJavaHome(); // null if not present
    }

    /**
     * @return the root directory for all test installations
     */
    protected static File getRootDir() {
        return _rootDirectory;
    }

    /**
     * @return the target directory of this test
     */
    protected File getTargetDir() {
        return _targetDir;
    }

    /**
     * @return the name of this test
     */
    protected String getName() {
        return _name;
    }

    /**
     * @return the array of command line arguments
     */
    protected String[] getCommandLineArgs() {
        return _commandLineArgs;
    }

    /**
     * set the array of command line arguments
     * 
     * @param commandLineArgs
     */
    protected void setCommandLineArgs(String[] commandLineArgs) {
        _commandLineArgs = commandLineArgs;
    }

    /**
     * @return <code>true</code> if this test has a java home deviation
     */
    protected boolean hasJavaHomeDeviation() {
        return getJavaHome() != null;
    }

    /**
     * @return the deviation for java home, <code>null</code> if there is none
     */
    protected File getJavaHome() {
        return _javaHome;
    }

    /**
     * @return <code>true</code> if this test should be verbose
     */
    protected boolean isVerbose() {
        return _verbose;
    }

    /**
     * @return the name suffix for this test
     */
    protected abstract String getNameSuffix();

    /**
     * @throws DriverException if the target directory does not exist or is empty (installation failed)
     */
    protected void assertTargetDirNotEmpty() throws DriverException {
        File targetDir = getTargetDir();
        if (targetDir != null) {
            if (targetDir.exists() && targetDir.isDirectory()) {
                if (targetDir.listFiles().length > 0) {
                    return;
                }
            }
        }
        throw new DriverException("installation failed for " + targetDir.getAbsolutePath());
    }

    /**
     * Convenience method to add the additional arguments, if specified behind the -A option
     * <p>
     * This adds (if present):
     * <ul>
     * <li> target directory (should always be present)
     * <li> verbose
     * <li> jre
     * </ul>
     */
    protected void addAdditionalArguments() {
        if (getTargetDir() != null) {
            addArgument("-d");
            addArgument(getTargetDir().getAbsolutePath());
        }
        if (isVerbose()) {
            addArgument("-v");
        }
        if (hasJavaHomeDeviation()) {
            addArgument("-j");
            addArgument(getJavaHome().getAbsolutePath());
        }
    }

    /**
     * Add an additional String argument to the command line arguments
     * 
     * @param newArgument
     */
    protected void addArgument(String newArgument) {
        setCommandLineArgs(addArgument(newArgument, getCommandLineArgs()));
    }

    /**
     * set the verifier
     */
    protected void setVerifier(Verifier verifier) {
        _verifier = verifier;
        _verifier.setTargetDir(getTargetDir());
    }

    protected Verifier getVerifier() {
        return _verifier;
    }

    //
    // private stuff
    //

    private void setName() {
        String countAsString = "";
        if (_count <= 99) {
            countAsString += "0";
        }
        if (_count <= 9) {
            countAsString += "0";
        }
        countAsString += _count;
        // explicitly use a blank in the directory name, to nail down some platform specific problems
        _name = countAsString + " " + getNameSuffix() + "_";
    }

    /**
     * Add the new argument to the args array
     * 
     * @param newArgument
     * @param args
     * 
     * @return the new String array, with size increased by 1
     */
    private String[] addArgument(String newArgument, String[] args) {
        String[] newArgs = new String[args.length + 1];
        for (int i = 0; i < args.length; i++) {
            newArgs[i] = args[i];
        }
        newArgs[args.length] = newArgument;
        return newArgs;
    }

    /**
     * create the root directory for all automatic installations
     * <p>
     * assumed to be a subdirectory of java.io.tmpdir
     * 
     * @throws IOException
     * @throws DriverException
     */
    private void createRootDirectory() throws IOException, DriverException {
        File tmpFile = File.createTempFile("jython.autoinstall.root_", _DIR_SUFFIX);
        if (FileHelper.createTempDirectory(tmpFile)) {
            _rootDirectory = tmpFile;
        } else {
            throw new DriverException("unable to create root temporary directory");
        }
    }

    /**
     * create a target directory for a test installation
     * 
     * @throws IOException
     * @throws DriverException
     */
    private void createTargetDirectory() throws IOException, DriverException {
        File tmpFile = File.createTempFile(getName(), _DIR_SUFFIX, _rootDirectory);
        if (FileHelper.createTempDirectory(tmpFile)) {
            _targetDir = tmpFile;
        } else {
            throw new DriverException("unable to create temporary target directory");
        }
    }

}
