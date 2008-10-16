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

        args = new String[] { "-type", "standard" };
        commandLine = new InstallerCommandLine();
        assertFalse(commandLine.setArgs(args));
        assertTrue(commandLine.hasArguments());

        args = new String[] { "-v" };
        commandLine = new InstallerCommandLine();
        assertTrue(commandLine.setArgs(args));
        assertTrue(commandLine.hasArguments());

        args = new String[] { "--verbose" };
        commandLine = new InstallerCommandLine();
        assertTrue(commandLine.setArgs(args));
        assertTrue(commandLine.hasArguments());

        args = new String[] { "-A" };
        commandLine = new InstallerCommandLine();
        assertTrue(commandLine.setArgs(args));
        assertTrue(commandLine.hasArguments());

        args = new String[] { "--autotest" };
        commandLine = new InstallerCommandLine();
        assertTrue(commandLine.setArgs(args));
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

        args = new String[] { "-type" };
        commandLine = new InstallerCommandLine();
        assertFalse(commandLine.setArgs(args));
        assertTrue(commandLine.hasArguments());

        args = new String[] { "-type", "weird" };
        commandLine = new InstallerCommandLine();
        assertFalse(commandLine.setArgs(args));
        assertTrue(commandLine.hasArguments());

        args = new String[] { "-" };
        commandLine = new InstallerCommandLine();
        assertFalse(commandLine.setArgs(args));
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

        args = new String[] { "-s", "-A" };
        commandLine = new InstallerCommandLine();
        assertFalse(commandLine.setArgs(args));

        args = new String[] { "-c", "-A" };
        commandLine = new InstallerCommandLine();
        assertFalse(commandLine.setArgs(args));

        args = new String[] { "--silent", "--console" };
        commandLine = new InstallerCommandLine();
        assertFalse(commandLine.setArgs(args));

        args = new String[] { "--silent", "--autotest" };
        commandLine = new InstallerCommandLine();
        assertFalse(commandLine.setArgs(args));

        args = new String[] { "--console", "--autotest" };
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
    
    public void testHeadless() {
        String[] args;
        InstallerCommandLine commandLine;
        
        // normal gui startup without any arguments
        assertTrue(Installation.isGuiAllowed());
        args = new String[0];
        commandLine = new InstallerCommandLine();
        assertTrue(commandLine.setArgs(args));
        assertFalse(commandLine.hasConsoleOption());
        assertFalse(commandLine.hasSilentOption());
        
        // simulate startup without any arguments on a headless system
        boolean originalHeadless = Boolean.getBoolean(Installation.HEADLESS_PROPERTY_NAME);
        try {
            if(!originalHeadless) {
                System.setProperty(Installation.HEADLESS_PROPERTY_NAME, "true");
                assertFalse(Installation.isGuiAllowed());
                args = new String[0];
                commandLine = new InstallerCommandLine();
                assertTrue(commandLine.setArgs(args));
                assertTrue(commandLine.hasConsoleOption());
                assertFalse(commandLine.hasSilentOption());
            }
        } finally {
            if(!originalHeadless) {
                System.setProperty(Installation.HEADLESS_PROPERTY_NAME, "false");
            }
            assertTrue(Installation.isGuiAllowed());
        }
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
        InstallationType type;

        args = new String[] { "-t", "all" };
        commandLine = new InstallerCommandLine();
        assertTrue(commandLine.setArgs(args));
        assertTrue(commandLine.hasTypeOption());
        type = commandLine.getInstallationType();
        assertNotNull(type);
        assertTrue(type.isAll());

        args = new String[] { "--type", "standard" };
        commandLine = new InstallerCommandLine();
        assertTrue(commandLine.setArgs(args));
        assertTrue(commandLine.hasTypeOption());
        type = commandLine.getInstallationType();
        assertNotNull(type);
        assertTrue(type.isStandard());

        args = new String[] { "--type", "minimum" };
        commandLine = new InstallerCommandLine();
        assertTrue(commandLine.setArgs(args));
        assertTrue(commandLine.hasTypeOption());
        type = commandLine.getInstallationType();
        assertNotNull(type);
        assertTrue(type.isMinimum());

        args = new String[] { "--type", "standalone" };
        commandLine = new InstallerCommandLine();
        assertTrue(commandLine.setArgs(args));
        assertTrue(commandLine.hasTypeOption());
        type = commandLine.getInstallationType();
        assertNotNull(type);
        assertTrue(type.isStandalone());

        assertNull(commandLine.getJavaHome());
    }

    public void testInclude() {
        String[] args;
        InstallerCommandLine commandLine;
        InstallationType type;

        args = new String[] { "-t", "minimum", "-i", "mod" };
        commandLine = new InstallerCommandLine();
        assertTrue(commandLine.setArgs(args));
        assertTrue(commandLine.hasIncludeOption());
        type = commandLine.getInstallationType();
        assertNotNull(type);
        assertFalse(type.isStandalone());
        assertTrue(type.installLibraryModules());
        assertFalse(type.installDemosAndExamples());
        assertFalse(type.installDocumentation());
        assertFalse(type.installSources());

        args = new String[] { "-t", "minimum", "-i", "mod", "demo" };
        commandLine = new InstallerCommandLine();
        assertTrue(commandLine.setArgs(args));
        assertTrue(commandLine.hasIncludeOption());
        type = commandLine.getInstallationType();
        assertNotNull(type);
        assertFalse(type.isStandalone());
        assertTrue(type.installLibraryModules());
        assertTrue(type.installDemosAndExamples());
        assertFalse(type.installDocumentation());
        assertFalse(type.installSources());

        args = new String[] { "-t", "minimum", "-i", "mod", "demo", "doc" };
        commandLine = new InstallerCommandLine();
        assertTrue(commandLine.setArgs(args));
        assertTrue(commandLine.hasIncludeOption());
        type = commandLine.getInstallationType();
        assertNotNull(type);
        assertFalse(type.isStandalone());
        assertTrue(type.installLibraryModules());
        assertTrue(type.installDemosAndExamples());
        assertTrue(type.installDocumentation());
        assertFalse(type.installSources());

        args = new String[] { "-t", "minimum", "--include", "mod", "demo", "doc", "src" };
        commandLine = new InstallerCommandLine();
        assertTrue(commandLine.setArgs(args));
        assertTrue(commandLine.hasIncludeOption());
        type = commandLine.getInstallationType();
        assertNotNull(type);
        assertFalse(type.isStandalone());
        assertTrue(type.installLibraryModules());
        assertTrue(type.installDemosAndExamples());
        assertTrue(type.installDocumentation());
        assertTrue(type.installSources());

        args = new String[] { "-i", "modulo" };
        commandLine = new InstallerCommandLine();
        assertFalse(commandLine.setArgs(args));
    }

    public void testExclude() {
        String[] args;
        InstallerCommandLine commandLine;
        InstallationType type;

        args = new String[] { "-t", "all", "-e", "mod" };
        commandLine = new InstallerCommandLine();
        assertTrue(commandLine.setArgs(args));
        assertTrue(commandLine.hasExcludeOption());
        type = commandLine.getInstallationType();
        assertNotNull(type);
        assertFalse(type.isStandalone());
        assertFalse(type.installLibraryModules());
        assertTrue(type.installDemosAndExamples());
        assertTrue(type.installDocumentation());
        assertTrue(type.installSources());

        args = new String[] { "-t", "all", "-e", "mod", "demo" };
        commandLine = new InstallerCommandLine();
        assertTrue(commandLine.setArgs(args));
        assertTrue(commandLine.hasExcludeOption());
        type = commandLine.getInstallationType();
        assertNotNull(type);
        assertFalse(type.isStandalone());
        assertFalse(type.installLibraryModules());
        assertFalse(type.installDemosAndExamples());
        assertTrue(type.installDocumentation());
        assertTrue(type.installSources());

        args = new String[] { "-t", "all", "-e", "mod", "demo", "doc" };
        commandLine = new InstallerCommandLine();
        assertTrue(commandLine.setArgs(args));
        assertTrue(commandLine.hasExcludeOption());
        type = commandLine.getInstallationType();
        assertNotNull(type);
        assertFalse(type.isStandalone());
        assertFalse(type.installLibraryModules());
        assertFalse(type.installDemosAndExamples());
        assertFalse(type.installDocumentation());
        assertTrue(type.installSources());

        args = new String[] { "-t", "all", "--exclude", "mod", "demo", "doc", "src" };
        commandLine = new InstallerCommandLine();
        assertTrue(commandLine.setArgs(args));
        assertTrue(commandLine.hasExcludeOption());
        type = commandLine.getInstallationType();
        assertNotNull(type);
        assertFalse(type.isStandalone());
        assertFalse(type.installLibraryModules());
        assertFalse(type.installDemosAndExamples());
        assertFalse(type.installDocumentation());
        assertFalse(type.installSources());

        args = new String[] { "--exclude", "sources" };
        commandLine = new InstallerCommandLine();
        assertFalse(commandLine.setArgs(args));
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

        args = new String[] { "-s", "-d", "dir", "-t", "standard", "-j", "java", "-v" };
        commandLine = new InstallerCommandLine();
        assertTrue(commandLine.setArgs(args));
        assertTrue(commandLine.hasSilentOption());
        assertTrue(commandLine.hasDirectoryOption());
        assertNotNull(commandLine.getTargetDirectory());
        assertEquals("dir", commandLine.getTargetDirectory().getName());
        assertTrue(commandLine.hasTypeOption());
        assertNotNull(commandLine.getInstallationType());
        assertTrue(commandLine.getInstallationType().installDemosAndExamples());
        assertTrue(commandLine.getInstallationType().installDocumentation());
        assertTrue(commandLine.getInstallationType().installLibraryModules());
        assertFalse(commandLine.getInstallationType().installSources());
        assertTrue(commandLine.hasJavaHomeOption());
        assertEquals("java", commandLine.getJavaHome().getName());
        assertTrue(commandLine.hasVerboseOption());

        args = new String[] { "-s", "-d", "dir", "-t", "standard", "-e", "doc", "demo", "-i", "src", "-j", "java", "-v" };
        commandLine = new InstallerCommandLine();
        assertTrue(commandLine.setArgs(args));
        assertTrue(commandLine.hasSilentOption());
        assertTrue(commandLine.hasDirectoryOption());
        assertNotNull(commandLine.getTargetDirectory());
        assertEquals("dir", commandLine.getTargetDirectory().getName());
        assertTrue(commandLine.hasTypeOption());
        assertNotNull(commandLine.getInstallationType());
        assertFalse(commandLine.getInstallationType().installDemosAndExamples());
        assertFalse(commandLine.getInstallationType().installDocumentation());
        assertTrue(commandLine.getInstallationType().installLibraryModules());
        assertTrue(commandLine.getInstallationType().installSources());
        assertTrue(commandLine.hasJavaHomeOption());
        assertEquals("java", commandLine.getJavaHome().getName());
        assertTrue(commandLine.hasVerboseOption());
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

    public void testHasVerboseOptionInArgs() {
        String[] args = new String[0];
        assertFalse(InstallerCommandLine.hasVerboseOptionInArgs(args));

        args = new String[] {"a", "b", "c"};
        assertFalse(InstallerCommandLine.hasVerboseOptionInArgs(args));
 
        args = new String[] {"a", InstallerCommandLine.VERBOSE_SHORT, "c"};
        assertFalse(InstallerCommandLine.hasVerboseOptionInArgs(args));
        
        args = new String[] {"a", "-" + InstallerCommandLine.VERBOSE_SHORT, "c"};
        assertTrue(InstallerCommandLine.hasVerboseOptionInArgs(args));
        
        args = new String[] {"a", InstallerCommandLine.VERBOSE_LONG, "c"};
        assertFalse(InstallerCommandLine.hasVerboseOptionInArgs(args));
        
        args = new String[] {"a", "-" + InstallerCommandLine.VERBOSE_LONG, "c"};
        assertFalse(InstallerCommandLine.hasVerboseOptionInArgs(args));
        
        args = new String[] {"a", "--" + InstallerCommandLine.VERBOSE_LONG, "c"};
        assertTrue(InstallerCommandLine.hasVerboseOptionInArgs(args));
    }
    
}
