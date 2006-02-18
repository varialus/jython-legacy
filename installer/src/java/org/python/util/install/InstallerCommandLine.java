package org.python.util.install;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Parser;
import org.apache.commons.cli.PosixParser;

public class InstallerCommandLine {

    private static final String IGNORED_IF_NOT_SILENT = "ignored if not in silent mode";
    private static final String REQUIRED_IF_SILENT = "required if in silent mode";
    private static final String NEW_LINE = ";\n";

    private static final String HELP_SHORT = "h";
    private static final String HELP2_SHORT = "?";
    private static final String HELP_LONG = "help";
    private static final String HELP_DESC = "print this help/usage.";

    private static final String CONSOLE_SHORT = "c";
    private static final String CONSOLE_LONG = "console";
    private static final String CONSOLE_DESC = "do a console based installation (with user interaction).";

    private static final String SILENT_SHORT = "s";
    private static final String SILENT_LONG = "silent";
    private static final String SILENT_DESC = "do a silent installation (without user interaction).";

    private static final String DIRECTORY_SHORT = "d";
    private static final String DIRECTORY_LONG = "directory";
    private static final String DIRECTORY_DESC = "target directory to install to" + NEW_LINE + REQUIRED_IF_SILENT
            + NEW_LINE + IGNORED_IF_NOT_SILENT + ".";

    private static final String JRE_SHORT = "j";
    private static final String JRE_LONG = "jre";
    private static final String JRE_DESC = "home directory of the runtime jre, or jdk" + NEW_LINE
            + "executables are assumed in the /bin subdirectory" + NEW_LINE + IGNORED_IF_NOT_SILENT + ".";

    private static final String DIRECTORY_ARG = "dir";

    private static final String TYPE_STANDARD = "standard";
    private static final String TYPE_ALL = "all";
    private static final String TYPE_MINIMUM = "minimum";

    private static final String TYPE_SHORT = "t";
    private static final String TYPE_LONG = "type";
    private static final String TYPE_ARG = TYPE_LONG;
    private static final String TYPE_DESC = "installation type: " + TYPE_ALL + ", " + TYPE_STANDARD + " or "
            + TYPE_MINIMUM + NEW_LINE + IGNORED_IF_NOT_SILENT + ".";

    private static final String HEADER = "\nthe following options are available:";
    private static final String SYNTAX = "\n\tjava -jar jython_version.jar";
    private static final String SYNTAX_WITHOUT_JAR = "\n\tjava -jar ";
    private static final String FOOTER = "\nno option at all will start the interactive GUI installer.";
    private static final String EXAMPLES = "\n\nexample of a GUI installation:{0}"
            + "\n\nexample of a console installation:{0} -" + CONSOLE_SHORT
            + "\n\nexample of a silent installation:{0} -" + SILENT_SHORT + " -" + DIRECTORY_SHORT + " targetDirectory"
            + "\n\nexample of a silent installation with more options:{0} -" + SILENT_SHORT + " -" + DIRECTORY_SHORT
            + " targetDirectory -" + TYPE_SHORT + " " + TYPE_MINIMUM + " -" + JRE_SHORT + " javaHome";

    private String[] _args;
    private Options _options;
    private CommandLine _commandLine;
    private JarInfo _jarInfo;
    private final Parser _parser = new PosixParser();

    public InstallerCommandLine(JarInfo jarInfo) {
        createOptions();
        _jarInfo = jarInfo;
    }

    /**
     * constructor intended for JUnit tests only.
     */
    InstallerCommandLine() {
        this(null);
    }

    /**
     * Set the arguments from the command line.
     * 
     * @param args the arguments of the command line
     * @return <code>true</code> if all arguments are valid, <code>false</code> otherwise. No help is printed if
     * <code>false</code> is returned
     */
    public boolean setArgs(String args[]) {
        _args = args;
        try {
            _commandLine = _parser.parse(_options, _args, false); // throw for missing or unknown options / arguments
        } catch (MissingArgumentException mae) {
            System.err.println(mae.getMessage());
            return false;
        } catch (ParseException pe) {
            System.err.println(pe.getMessage());
            return false;
        }
        List unrecognized = _commandLine.getArgList();
        if (unrecognized.size() > 0) {
            System.err.println("unrecognized argument(s): " + unrecognized);
            return false;
        }
        if (_commandLine.hasOption(TYPE_SHORT)) { // sufficient even if user chose long opt
            String type = _commandLine.getOptionValue(TYPE_SHORT);
            if (TYPE_ALL.equals(type) || TYPE_STANDARD.equals(type) || TYPE_MINIMUM.equals(type)) {
            } else {
                System.err.println("unrecognized argument '" + type + "' to option: " + TYPE_SHORT + " / " + TYPE_LONG);
                return false;
            }
        }
        if (hasSilentOption()) {
            if (!hasDirectoryOption()) {
                System.err.println("option " + DIRECTORY_SHORT + " / " + DIRECTORY_LONG + " is required in "
                        + SILENT_LONG + " mode");
                return false;
            }
        }
        return true;
    }

    public boolean hasArguments() {
        return _args.length > 0;
    }

    public boolean hasHelpOption() {
        return _commandLine.hasOption(HELP_SHORT) || _commandLine.hasOption(HELP2_SHORT)
                || _commandLine.hasOption(HELP_LONG);
    }

    public boolean hasSilentOption() {
        return _commandLine.hasOption(SILENT_SHORT) || _commandLine.hasOption(SILENT_LONG);
    }

    public boolean hasConsoleOption() {
        return _commandLine.hasOption(CONSOLE_SHORT) || _commandLine.hasOption(CONSOLE_LONG);
    }

    public boolean hasDirectoryOption() {
        return _commandLine.hasOption(DIRECTORY_SHORT) || _commandLine.hasOption(DIRECTORY_LONG);
    }

    public boolean hasTypeOption() {
        return _commandLine.hasOption(TYPE_SHORT) || _commandLine.hasOption(TYPE_LONG);
    }

    public boolean hasJavaHomeOption() {
        return _commandLine.hasOption(JRE_SHORT) || _commandLine.hasOption(JRE_LONG);
    }

    public void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.defaultWidth = 76;
        String syntax = SYNTAX;
        if (_jarInfo != null) {
            try {
                syntax = SYNTAX_WITHOUT_JAR + _jarInfo.getJarFile().getName();
            } catch (IOException ioe) {
            }
        }
        formatter.printHelp(syntax, HEADER, _options, FOOTER, true);
        String exampleSyntax[] = new String[1];
        exampleSyntax[0] = syntax;
        String examples = MessageFormat.format(EXAMPLES, exampleSyntax);
        System.out.println(examples);
    }

    public File getTargetDirectory() {
        if (hasDirectoryOption()) {
            return new File(_commandLine.getOptionValue(DIRECTORY_SHORT));
        } else {
            throw new InstallerException("no target directory specified"); // should not happen
        }
    }

    public File getJavaHome() {
        if (hasJavaHomeOption()) {
            return new File(_commandLine.getOptionValue(JRE_SHORT));
        } else {
            throw new InstallerException("no java home specified"); // should not happen
        }
    }

    public String getInstallationType() {
        String type = Installation.STANDARD;
        String typeName = _commandLine.getOptionValue(TYPE_SHORT);
        if (TYPE_STANDARD.equals(typeName)) {
            type = Installation.STANDARD;
        } else if (TYPE_ALL.equals(typeName)) {
            type = Installation.ALL;
        } else if (TYPE_MINIMUM.equals(typeName)) {
            type = Installation.MINIMUM;
        }
        message(Installation.getText(TextKeys.C_USING_TYPE, typeName));
        return type;
    }

    //
    // private methods
    //

    private void createOptions() {
        _options = new Options();

        // console or silent mode
        Option consoleOption = new Option(CONSOLE_SHORT, CONSOLE_LONG, false, CONSOLE_DESC);
        Option silentOption = new Option(SILENT_SHORT, SILENT_LONG, false, SILENT_DESC);
        OptionGroup group1 = new OptionGroup();
        group1.addOption(consoleOption);
        group1.addOption(silentOption);
        _options.addOptionGroup(group1);

        // target directory
        _options.addOption(OptionBuilder.withArgName(DIRECTORY_ARG).hasArg().withDescription(DIRECTORY_DESC)
                .withLongOpt(DIRECTORY_LONG).create(DIRECTORY_SHORT));

        // runtime jre
        _options.addOption(OptionBuilder.withArgName(DIRECTORY_ARG).hasArg().withDescription(JRE_DESC).withLongOpt(
                JRE_LONG).create(JRE_SHORT));

        // installation type
        _options.addOption(OptionBuilder.withArgName(TYPE_ARG).hasArg().withDescription(TYPE_DESC).withLongOpt(
                TYPE_LONG).create(TYPE_SHORT));

        // different help options
        Option helpHOption = new Option(HELP_SHORT, HELP_LONG, false, HELP_DESC);
        Option helpQOption = new Option(HELP2_SHORT, HELP_DESC);
        OptionGroup group2 = new OptionGroup();
        group2.addOption(helpHOption);
        group2.addOption(helpQOption);
        _options.addOptionGroup(group2);
    }

    private void message(String message) {
        ConsoleInstaller.message(message);
    }

}
