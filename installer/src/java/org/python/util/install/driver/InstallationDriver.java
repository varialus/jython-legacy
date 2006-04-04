package org.python.util.install.driver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.python.util.install.Installation;

public class InstallationDriver {

    private static final String _DIR_SUFFIX = "_dir";

    private File _tempRootDirectory;
    private SilentAutotest[] _silentTests;
    private ConsoleAutotest[] _consoleTests;

    /**
     * construct the driver
     * 
     * @param args console arguments
     * 
     * @throws IOException
     * @throws DriverException
     */
    public InstallationDriver(String[] args) throws DriverException {
        try {
            createTempRootDirectory();
            buildSilentTests();
            buildConsoleTests();
        } catch (IOException ioe) {
            throw new DriverException(ioe);
        }
    }

    /**
     * execute all the automatic tests
     * 
     * @throws DriverException
     */
    public void drive() throws DriverException {
        // silent tests
        for (int i = 0; i < _silentTests.length; i++) {
            driveSilentTest(_silentTests[i]);
        }
        // console tests
        for (int i = 0; i < _consoleTests.length; i++) {
            driveConsoleTest(_consoleTests[i]);
        }

        // TODO:oti gui tests
    }

    /**
     * create the root directory for all automatic installations
     * <p>
     * assumed to be a subdirectory of java.io.tmpdir
     * 
     * @throws IOException
     * @throws DriverException
     */
    private void createTempRootDirectory() throws IOException, DriverException {
        File tmpFile = File.createTempFile("jython.autoinstall.root_", _DIR_SUFFIX);
        if (createTempDirectory(tmpFile)) {
            _tempRootDirectory = tmpFile;
        } else {
            throw new DriverException("unable to create root temporary directory");
        }
    }

    /**
     * create an empty temporary target directory
     * 
     * @param parentDirectory
     * @param prefix
     * 
     * @return the target directory
     * 
     * @throws IOException
     * @throws DriverException
     */
    private File createTempTargetDirectory(String prefix) throws IOException, DriverException {
        File tmpFile = File.createTempFile(prefix, _DIR_SUFFIX, _tempRootDirectory);
        if (createTempDirectory(tmpFile)) {
            return tmpFile;
        } else {
            throw new DriverException("unable to create temporary target directory");
        }
    }

    /**
     * create a temporary directory with the same name as the passed in File (which may exist as file, not directory)
     * 
     * @param tempDirectory
     * @return <code>true</code> only if the the directory was successfully created (or already existed)
     */
    private boolean createTempDirectory(File tempDirectory) {
        if (!tempDirectory.isDirectory()) {
            if (tempDirectory.exists()) {
                tempDirectory.delete();
            }
            return tempDirectory.mkdir();
        } else {
            return true;
        }
    }

    /**
     * execute a single console test
     * 
     * @param consoleTest
     * @throws DriverException
     * @throws IOException
     */
    private void driveConsoleTest(ConsoleAutotest consoleTest) throws DriverException {
        Tunnel _tunnel;
        try {
            _tunnel = new Tunnel();
            // have to fork off the driver thread first
            ConsoleDriver driver = new ConsoleDriver(_tunnel, consoleTest.getAnswers());
            driver.start();
            // now do the installation
            Installation.driverMain(consoleTest.getCommandLineArgs(), _tunnel);
            consoleTest.assertTargetDirNotEmpty();
            _tunnel.close();
        } catch (IOException ioe) {
            throw new DriverException(ioe);
        }
    }

    /**
     * execute a single silent test
     * 
     * @param silentTest
     * @throws DriverException
     */
    private void driveSilentTest(SilentAutotest silentTest) throws DriverException {
        Installation.driverMain(silentTest.getCommandLineArgs(), null); // only a thin wrapper
        silentTest.assertTargetDirNotEmpty();
    }

    /**
     * build all the silent tests // TODO:oti more tests
     * 
     * @throws IOException
     * @throws DriverException
     */
    private void buildSilentTests() throws IOException, DriverException {
        List silentTests = new ArrayList(50);
        final String prefix = "silentTest_";

        File targetDir = createTempTargetDirectory(prefix + "01_");
        SilentAutotest test1 = new SilentAutotest(targetDir);
        String[] arguments = new String[] { "-s", "-d", targetDir.getAbsolutePath() };
        test1.setCommandLineArgs(arguments);
        silentTests.add(test1);

        targetDir = createTempTargetDirectory(prefix + "02_");
        SilentAutotest test2 = new SilentAutotest(targetDir);
        arguments = new String[] { "-s", "-d", targetDir.getAbsolutePath(), "-t", "minimum" };
        test2.setCommandLineArgs(arguments);
        silentTests.add(test2);

        // build array
        int size = silentTests.size();
        _silentTests = new SilentAutotest[size];
        Iterator silentIterator = silentTests.iterator();
        for (int i = 0; i < size; i++) {
            _silentTests[i] = (SilentAutotest) silentIterator.next();
        }
    }

    /**
     * build all the console tests // TODO:oti more tests
     * 
     * @throws IOException
     * @throws DriverException
     */
    private void buildConsoleTests() throws IOException, DriverException {
        List consoleTests = new ArrayList(50);
        final String prefix = "consoleTest_";
        final String[] arguments = new String[] { "-c" };

        File targetDir = createTempTargetDirectory(prefix + "01_");
        ConsoleAutotest test1 = new ConsoleAutotest(targetDir);
        test1.setCommandLineArgs(arguments);
        test1.addAnswer("e"); // language
        test1.addAnswer("n"); // no read of license
        test1.addAnswer("y"); // accept license
        test1.addAnswer("3"); // type: minimum
        test1.addAnswer("n"); // include: nothing
        test1.addAnswer(test1.getTargetDir().getAbsolutePath()); // target directory
        test1.addAnswer("=="); // current jre
        test1.addAnswer(""); // simple enter for java version
        test1.addAnswer(""); // simple enter for os version
        test1.addAnswer("y"); // confirm copying
        test1.addAnswer("n"); // no readme
        consoleTests.add(test1);

        targetDir = createTempTargetDirectory(prefix + "02_");
        ConsoleAutotest test2 = new ConsoleAutotest(targetDir);
        test2.setCommandLineArgs(arguments);
        test2.addAnswer("e"); // language
        test2.addAnswer("n"); // no read of license
        test2.addAnswer("y"); // accept license
        test2.addAnswer("3"); // type: minimum
        test2.addAnswer("y"); // include
        test2.addAnswer("mod"); // include
        test2.addAnswer("demo"); // include
        test2.addAnswer("src"); // include
        test2.addAnswer("n"); // no further includes
        test2.addAnswer("y"); // exclude
        test2.addAnswer("demo"); // exclude
        test2.addAnswer("wrongAnswer"); // wrong answer
        test2.addAnswer("n"); // no further excludes
        test2.addAnswer(test2.getTargetDir().getAbsolutePath()); // target directory
        test2.addAnswer("=="); // current jre
        test2.addAnswer(""); // simple enter for java version
        test2.addAnswer(""); // simple enter for os version
        test2.addAnswer("y"); // confirm copying
        test2.addAnswer("n"); // no readme
        consoleTests.add(test2);

        // build array
        int size = consoleTests.size();
        _consoleTests = new ConsoleAutotest[size];
        Iterator consoleIterator = consoleTests.iterator();
        for (int i = 0; i < size; i++) {
            _consoleTests[i] = (ConsoleAutotest) consoleIterator.next();
        }
    }

}
