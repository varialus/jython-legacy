package org.python.util.install;

import junit.framework.TestCase;

public class InstallerCommandLineTest extends TestCase {

    public void testValidArguments() {
        String[] args;
        InstallerCommandLine commandLine;

        args = new String[0];
        commandLine = new InstallerCommandLine();
        assertTrue(commandLine.setArgs(args));
        assertFalse(commandLine.hasArguments());

        args = new String[0];
        commandLine = new InstallerCommandLine();
        assertTrue(commandLine.setArgs(args));
        assertFalse(commandLine.hasArguments());

        args = new String[] { "-c" };
        commandLine = new InstallerCommandLine();
        assertTrue(commandLine.setArgs(args));
        assertTrue(commandLine.hasArguments());

        args = new String[] { "--directory", "c:/temp" };
        commandLine = new InstallerCommandLine();
        assertTrue(commandLine.setArgs(args));
        assertTrue(commandLine.hasArguments());

        args = new String[] { "--type", "all" };
        commandLine = new InstallerCommandLine();
        assertTrue(commandLine.setArgs(args));
        assertTrue(commandLine.hasArguments());

        args = new String[] { "-t", "minimum" };
        commandLine = new InstallerCommandLine();
        assertTrue(commandLine.setArgs(args));
        assertTrue(commandLine.hasArguments());

        args = new String[] { "-type", "standard" }; // PosixParser bursts this into -t ype
        commandLine = new InstallerCommandLine();
        assertFalse(commandLine.setArgs(args));
        assertTrue(commandLine.hasArguments());

        args = new String[0];
        commandLine = new InstallerCommandLine();
        assertTrue(commandLine.setArgs(args));
        assertFalse(commandLine.hasArguments());
    }

    public void testInvalidArguments() {
        String[] args;
        InstallerCommandLine commandLine;

        args = new String[] { "--one" };
        commandLine = new InstallerCommandLine();
        assertFalse(commandLine.setArgs(args));
        assertTrue(commandLine.hasArguments());

        args = new String[] { "--one argOne" };
        commandLine = new InstallerCommandLine();
        assertFalse(commandLine.setArgs(args));
        assertTrue(commandLine.hasArguments());

        args = new String[] { "--one", "--two", "--three" };
        commandLine = new InstallerCommandLine();
        assertFalse(commandLine.setArgs(args));
        assertTrue(commandLine.hasArguments());

        args = new String[] { "-o" };
        commandLine = new InstallerCommandLine();
        assertFalse(commandLine.setArgs(args));
        assertTrue(commandLine.hasArguments());

        args = new String[] { "-type" }; // PosixParser bursts -type into -t ype
        commandLine = new InstallerCommandLine();
        assertFalse(commandLine.setArgs(args));
        assertTrue(commandLine.hasArguments());

        args = new String[] { "-type", "weird" };
        commandLine = new InstallerCommandLine();
        assertFalse(commandLine.setArgs(args));
        assertTrue(commandLine.hasArguments());
    }

    public void testMissingArgument() {
        String[] args;
        InstallerCommandLine commandLine;

        args = new String[] { "--directory" };
        commandLine = new InstallerCommandLine();
        assertFalse(commandLine.setArgs(args));

        args = new String[] { "--type" };
        commandLine = new InstallerCommandLine();
        assertFalse(commandLine.setArgs(args));
    }

    public void testUnknownArgument() {
        String[] args;
        InstallerCommandLine commandLine;

        args = new String[] { "yeah" };
        commandLine = new InstallerCommandLine();
        assertFalse(commandLine.setArgs(args));

        args = new String[] { "--silent", "yeah" };
        commandLine = new InstallerCommandLine();
        assertFalse(commandLine.setArgs(args));

        args = new String[] { "--silent", "yeah", "yoyo" };
        commandLine = new InstallerCommandLine();
        assertFalse(commandLine.setArgs(args));

        args = new String[] { "--type", "takatuka" };
        commandLine = new InstallerCommandLine();
        assertFalse(commandLine.setArgs(args));
    }

    public void testOptionGroups() {
        String[] args;
        InstallerCommandLine commandLine;

        args = new String[] { "-s", "-c" };
        commandLine = new InstallerCommandLine();
        assertFalse(commandLine.setArgs(args));

        args = new String[] { "--silent", "--console" };
        commandLine = new InstallerCommandLine();
        assertFalse(commandLine.setArgs(args));

        args = new String[] { "-?", "-h" };
        commandLine = new InstallerCommandLine();
        assertFalse(commandLine.setArgs(args));

        args = new String[] { "-?", "--help" };
        commandLine = new InstallerCommandLine();
        assertFalse(commandLine.setArgs(args));
    }

    public void testSilent() {
        String[] args;
        InstallerCommandLine commandLine;

        args = new String[] { "-s" };
        commandLine = new InstallerCommandLine();
        assertFalse(commandLine.setArgs(args)); // expect required directory in silent mode

        args = new String[] { "-s", "-d", "/tmp" };
        commandLine = new InstallerCommandLine();
        assertTrue(commandLine.setArgs(args));
        assertTrue(commandLine.hasSilentOption());
    }

    public void testConsole() {
        String[] args;
        InstallerCommandLine commandLine;

        args = new String[] { "-c" };
        commandLine = new InstallerCommandLine();
        assertTrue(commandLine.setArgs(args));
        assertTrue(commandLine.hasConsoleOption());
    }

    public void testDirectory() {
        String[] args;
        InstallerCommandLine commandLine;

        args = new String[] { "-d", "dir" };
        commandLine = new InstallerCommandLine();
        assertTrue(commandLine.setArgs(args));
        assertTrue(commandLine.hasDirectoryOption());
        assertNotNull(commandLine.getTargetDirectory());
        assertEquals("dir", commandLine.getTargetDirectory().getName());

        args = new String[] { "-s", "--directory", "dir" };
        commandLine = new InstallerCommandLine();
        assertTrue(commandLine.setArgs(args));
        assertTrue(commandLine.hasDirectoryOption());
        assertNotNull(commandLine.getTargetDirectory());
        assertEquals("dir", commandLine.getTargetDirectory().getName());
    }

    public void testType() {
        String[] args;
        InstallerCommandLine commandLine;

        args = new String[] { "-t", "all" };
        commandLine = new InstallerCommandLine();
        assertTrue(commandLine.setArgs(args));
        assertTrue(commandLine.hasTypeOption());
        assertEquals(Installation.ALL, commandLine.getInstallationType());

        args = new String[] { "--type", "standard" };
        commandLine = new InstallerCommandLine();
        assertTrue(commandLine.setArgs(args));
        assertTrue(commandLine.hasTypeOption());
        assertEquals(Installation.STANDARD, commandLine.getInstallationType());

        args = new String[] { "--type", "minimum" };
        commandLine = new InstallerCommandLine();
        assertTrue(commandLine.setArgs(args));
        assertTrue(commandLine.hasTypeOption());
        assertEquals(Installation.MINIMUM, commandLine.getInstallationType());

        assertNull(commandLine.getJavaHome());
    }

    public void testJavaHome() {
        String[] args;
        InstallerCommandLine commandLine;

        args = new String[] { "-j", "java" };
        commandLine = new InstallerCommandLine();
        assertTrue(commandLine.setArgs(args));
        assertTrue(commandLine.hasJavaHomeOption());
        assertNotNull(commandLine.getJavaHome());
        assertEquals("java", commandLine.getJavaHome().getName());

        args = new String[] { "--jre", "java" };
        commandLine = new InstallerCommandLine();
        assertTrue(commandLine.setArgs(args));
        assertTrue(commandLine.hasJavaHomeOption());
        assertNotNull(commandLine.getJavaHome());
        assertEquals("java", commandLine.getJavaHome().getName());

        assertNull(commandLine.getTargetDirectory());
    }

    public void testExamples() {
        String[] args;
        InstallerCommandLine commandLine;

        args = new String[] { "-c" };
        commandLine = new InstallerCommandLine();
        assertTrue(commandLine.setArgs(args));
        assertTrue(commandLine.hasConsoleOption());

        args = new String[] { "-s", "-d", "dir" };
        commandLine = new InstallerCommandLine();
        assertTrue(commandLine.setArgs(args));
        assertTrue(commandLine.hasSilentOption());
        assertTrue(commandLine.hasDirectoryOption());
        assertNotNull(commandLine.getTargetDirectory());
        assertEquals("dir", commandLine.getTargetDirectory().getName());

        args = new String[] { "-s", "-d", "dir", "-t", "standard", "-j", "java" };
        commandLine = new InstallerCommandLine();
        assertTrue(commandLine.setArgs(args));
        assertTrue(commandLine.hasSilentOption());
        assertTrue(commandLine.hasDirectoryOption());
        assertNotNull(commandLine.getTargetDirectory());
        assertEquals("dir", commandLine.getTargetDirectory().getName());
        assertTrue(commandLine.hasTypeOption());
        assertEquals(Installation.STANDARD, commandLine.getInstallationType());
        assertTrue(commandLine.hasJavaHomeOption());
        assertEquals("java", commandLine.getJavaHome().getName());
    }

    public void testHelp() {
        String[] args;
        InstallerCommandLine commandLine;

        args = new String[0];
        commandLine = new InstallerCommandLine();
        assertTrue(commandLine.setArgs(args));
        assertFalse(commandLine.hasHelpOption());

        args = new String[] { "--help" };
        commandLine = new InstallerCommandLine();
        assertTrue(commandLine.setArgs(args));
        assertTrue(commandLine.hasHelpOption());

        args = new String[] { "-h" };
        commandLine = new InstallerCommandLine();
        assertTrue(commandLine.setArgs(args));
        assertTrue(commandLine.hasHelpOption());

        args = new String[] { "-?" };
        commandLine = new InstallerCommandLine();
        assertTrue(commandLine.setArgs(args));
        assertTrue(commandLine.hasHelpOption());

        // now print the help
        commandLine.printHelp();
    }

}
