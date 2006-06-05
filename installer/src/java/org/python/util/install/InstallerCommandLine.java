package org.python.util.install;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Parser;
import org.apache.commons.cli.PosixParser;

public class InstallerCommandLine {
    protected static final String INEXCLUDE_LIBRARY_MODULES = "mod";
    protected static final String INEXCLUDE_DEMOS_AND_EXAMPLES = "demo";
    protected static final String INEXCLUDE_DOCUMENTATION = "doc";
    protected static final String INEXCLUDE_SOURCES = "src";

    private static final String CONSOLE_SHORT = "c";
    private static final String CONSOLE_LONG = "console";
    private static final String CONSOLE_DESC = "console based installation (user interaction)\n"
            + "any other options will be ignored (except 'verbose')";

    private static final String SILENT_SHORT = "s";
    private static final String SILENT_LONG = "silent";
    private static final String SILENT_DESC = "silent installation (without user interaction)";

    private static final String VERBOSE_SHORT = "v";
    private static final String VERBOSE_LONG = "verbose";
    private static final String VERBOSE_DESC = "print more output during the installation\n"
            + "(also valid in GUI and autotest mode)";

    private static final String JRE_SHORT = "j";
    private static final String JRE_LONG = "jre";
    private static final String JRE_DESC = "home directory of the runtime jre or jdk\n"
            + "(executables are assumed in the /bin subdirectory)\n" + "select this if you want to run Jython with a\n"
            + "different java version than the installation";

    private static final String AUTOTEST_SHORT = "A";
    private static final String AUTOTEST_LONG = "autotest";
    private static final String AUTOTEST_DESC = "automatic stress tests for the installer\n"
            + "most of the other options are ignored\n" + "allowed additional options: '" + VERBOSE_LONG + "', '"
            + JRE_LONG + "'";

    private static final String DIRECTORY_SHORT = "d";
    private static final String DIRECTORY_LONG = "directory";
    private static final String DIRECTORY_DESC = "target directory to install to\n"
            + "(required in silent mode,\nused as default in GUI mode)";

    private static final String DIRECTORY_ARG = "dir";

    private static final String TYPE_STANDARD = "standard";
    private static final String TYPE_ALL = "all";
    private static final String TYPE_MINIMUM = "minimum";
    protected static final String TYPE_STANDALONE = "standalone";
    private static final String STANDALONE_DOCUMENTATION = "install a single, executable .jar,\n(containing all the modules)";

    private static final String INEXCLUDE_ARG = "part(s)";
    private static final String INEXCLUDE_PARTS = "more than one of the following is possible:\n" + "- "
            + INEXCLUDE_LIBRARY_MODULES + ": library modules\n" + "- " + INEXCLUDE_DEMOS_AND_EXAMPLES
            + ": demos and examples\n" + "- " + INEXCLUDE_DOCUMENTATION + ": documentation\n" + "- "
            + INEXCLUDE_SOURCES + ": java source code";

    private static final String TYPE_SHORT = "t";
    private static final String TYPE_LONG = "type";
    private static final String TYPE_ARG = TYPE_LONG;
    private static final String TYPE_DESC = "installation type\n" + "one of the following types is possible\n"
            + "(see also include/exclude parts):\n" + "- " + TYPE_ALL + ": everything (including " + INEXCLUDE_SOURCES
            + ")\n" + "- " + TYPE_STANDARD + ": core, " + INEXCLUDE_LIBRARY_MODULES + ", "
            + INEXCLUDE_DEMOS_AND_EXAMPLES + ", " + INEXCLUDE_DOCUMENTATION + "\n" + "- " + TYPE_MINIMUM + ": core\n"
            + "- " + TYPE_STANDALONE + ": " + STANDALONE_DOCUMENTATION;

    private static final String INCLUDE_SHORT = "i";
    private static final String INCLUDE_LONG = "include";
    private static final String INCLUDE_DESC = "finer control over parts to install\n" + INEXCLUDE_PARTS;

    private static final String EXCLUDE_SHORT = "e";
    private static final String EXCLUDE_LONG = "exclude";
    private static final String EXCLUDE_DESC = "finer control over parts not to install\n" + INEXCLUDE_PARTS
            + "\n(excludes override includes)";

    private static final String HELP_SHORT = "h";
    private static final String HELP2_SHORT = "?";
    private static final String HELP_LONG = "help";
    private static final String HELP_DESC = "print this help (overrides any other options)";

    private static final String SYNTAX = "\n\tjava -jar jython_version.jar";
    private static final String HEADER = "\nno option at all will start the interactive GUI installer, except:\n"
            + "options respected in GUI mode are '" + DIRECTORY_LONG + "' and '" + JRE_LONG
            + "': the values provided for these serve as defaults;\n"
            + "in non GUI mode the following options are available:\n.";
    private static final String SYNTAX_WITHOUT_JAR = "\n\tjava -jar ";
    private static final String FOOTER = "";
    private static final String EXAMPLES = "\nexample of a GUI installation:{0}"
            + "\n\nexample of a console installation:{0} -" + CONSOLE_SHORT
            + "\n\nexample of a silent installation:{0} -" + SILENT_SHORT + " -" + DIRECTORY_SHORT + " targetDirectory"
            + "\n\nexamples of a silent installation with more options:{0} -" + SILENT_SHORT + " -" + DIRECTORY_SHORT
            + " targetDirectory -" + TYPE_SHORT + " " + TYPE_MINIMUM + " -" + INCLUDE_SHORT + " " + INEXCLUDE_SOURCES
            + " -" + JRE_SHORT + " javaHome" + "{0} -" + SILENT_SHORT + " -" + DIRECTORY_SHORT + " targetDirectory -"
            + TYPE_SHORT + " " + TYPE_STANDARD + " -" + EXCLUDE_SHORT + " " + INEXCLUDE_DEMOS_AND_EXAMPLES + " "
            + INEXCLUDE_DOCUMENTATION + "\n\t\t -" + INCLUDE_SHORT + " " + INEXCLUDE_SOURCES + " -" + JRE_SHORT
            + " javaHome -" + VERBOSE_SHORT
            + "\n\nexample of an autotest installation into temporary directories:{0} -" + AUTOTEST_SHORT
            + "\n\t(make sure you do NOT touch mouse NOR keyboard after hitting enter/return!)"
            + "\n\nexample of an autotest installation, using a different jre for the start scripts:{0} -"
            + AUTOTEST_SHORT + " -" + JRE_SHORT + " javaHome" + " -" + VERBOSE_SHORT
            + "\n\t(make sure you do NOT touch mouse NOR keyboard after hitting enter/return!)";

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
    public InstallerCommandLine() {
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
        if (args.length == 0) {
            // switch to console mode if gui is not allowed
            if (!Installation.isGuiAllowed()) {
                _args = new String[] { "-" + CONSOLE_SHORT };
            }
        }
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
        if (hasTypeOption()) {
            String type = _commandLine.getOptionValue(TYPE_SHORT);
            if (TYPE_ALL.equals(type) || TYPE_STANDARD.equals(type) || TYPE_MINIMUM.equals(type)
                    || TYPE_STANDALONE.equals(type)) {
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
        if (hasIncludeOption()) {
            String[] includeParts = _commandLine.getOptionValues(INCLUDE_SHORT);
            for (int i = 0; i < includeParts.length; i++) {
                if (!isValidInExcludePart(includeParts[i])) {
                    System.err.println("unrecognized include part '" + includeParts[i] + "'");
                    return false;
                }
            }
        }
        if (hasExcludeOption()) {
            String[] excludeParts = _commandLine.getOptionValues(EXCLUDE_SHORT);
            for (int i = 0; i < excludeParts.length; i++) {
                if (!isValidInExcludePart(excludeParts[i])) {
                    System.err.println("unrecognized exclude part '" + excludeParts[i] + "'");
                    return false;
                }
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

    public boolean hasAutotestOption() {
        return _commandLine.hasOption(AUTOTEST_SHORT) || _commandLine.hasOption(AUTOTEST_LONG);
    }

    public boolean hasDirectoryOption() {
        return _commandLine.hasOption(DIRECTORY_SHORT) || _commandLine.hasOption(DIRECTORY_LONG);
    }

    public boolean hasTypeOption() {
        return _commandLine.hasOption(TYPE_SHORT) || _commandLine.hasOption(TYPE_LONG);
    }

    public boolean hasIncludeOption() {
        return _commandLine.hasOption(INCLUDE_SHORT) || _commandLine.hasOption(INCLUDE_LONG);
    }

    public boolean hasExcludeOption() {
        return _commandLine.hasOption(EXCLUDE_SHORT) || _commandLine.hasOption(EXCLUDE_LONG);
    }

    public boolean hasJavaHomeOption() {
        return _commandLine.hasOption(JRE_SHORT) || _commandLine.hasOption(JRE_LONG);
    }

    public boolean hasVerboseOption() {
        return _commandLine.hasOption(VERBOSE_SHORT) || _commandLine.hasOption(VERBOSE_LONG);
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

    /**
     * @return the requested target directory, <code>null</code> if no directory specified
     */
    public File getTargetDirectory() {
        if (hasDirectoryOption()) {
            return new File(_commandLine.getOptionValue(DIRECTORY_SHORT));
        } else {
            return null;
        }
    }

    /**
     * @return the requested java home directory, <code>null</code> if no java home specified
     */
    public File getJavaHome() {
        if (hasJavaHomeOption()) {
            return new File(_commandLine.getOptionValue(JRE_SHORT));
        } else {
            return null;
        }
    }

    /**
     * The Installation type is built out of the type, include and exclude option
     * 
     * @return the installation type usable for the jar installer
     */
    public InstallationType getInstallationType() {
        InstallationType installationType = new InstallationType(); // defaults to standard
        // build a priori values out of the type option
        if (hasTypeOption()) {
            String typeName = _commandLine.getOptionValue(TYPE_SHORT);
            if (TYPE_ALL.equals(typeName)) {
                installationType.setAll();
            } else if (TYPE_MINIMUM.equals(typeName)) {
                installationType.setMinimum();
            } else if (TYPE_STANDALONE.equals(typeName)) {
                installationType.setStandalone();
            }
        }
        // add parts to include
        if (hasIncludeOption()) {
            String[] includeParts = _commandLine.getOptionValues(INCLUDE_SHORT);
            for (int i = 0; i < includeParts.length; i++) {
                if (INEXCLUDE_DEMOS_AND_EXAMPLES.equals(includeParts[i])) {
                    installationType.addDemosAndExamples();
                }
                if (INEXCLUDE_DOCUMENTATION.equals(includeParts[i])) {
                    installationType.addDocumentation();
                }
                if (INEXCLUDE_LIBRARY_MODULES.equals(includeParts[i])) {
                    installationType.addLibraryModules();
                }
                if (INEXCLUDE_SOURCES.equals(includeParts[i])) {
                    installationType.addSources();
                }
            }
        }
        // remove parts to exclude
        if (hasExcludeOption()) {
            String[] excludeParts = _commandLine.getOptionValues(EXCLUDE_SHORT);
            for (int i = 0; i < excludeParts.length; i++) {
                if (INEXCLUDE_DEMOS_AND_EXAMPLES.equals(excludeParts[i])) {
                    installationType.removeDemosAndExamples();
                }
                if (INEXCLUDE_DOCUMENTATION.equals(excludeParts[i])) {
                    installationType.removeDocumentation();
                }
                if (INEXCLUDE_LIBRARY_MODULES.equals(excludeParts[i])) {
                    installationType.removeLibraryModules();
                }
                if (INEXCLUDE_SOURCES.equals(excludeParts[i])) {
                    installationType.removeSources();
                }
            }
        }
        return installationType;
    }

    //
    // private methods
    //

    private void createOptions() {
        _options = new Options();
        _options.setSortAsAdded(true);

        // console or silent mode
        Option consoleOption = new Option(CONSOLE_SHORT, CONSOLE_LONG, false, CONSOLE_DESC);
        Option silentOption = new Option(SILENT_SHORT, SILENT_LONG, false, SILENT_DESC);
        Option autotestOption = new Option(AUTOTEST_SHORT, AUTOTEST_LONG, false, AUTOTEST_DESC);
        OptionGroup group1 = new OptionGroup();
        group1.addOption(consoleOption);
        group1.addOption(silentOption);
        group1.addOption(autotestOption);
        _options.addOptionGroup(group1);

        // target directory
        Option directoryOption = new Option(DIRECTORY_SHORT, DIRECTORY_LONG, true, DIRECTORY_DESC);
        directoryOption.setArgName(DIRECTORY_ARG);
        _options.addOption(directoryOption);

        // installation type
        Option typeOption = new Option(TYPE_SHORT, TYPE_LONG, true, TYPE_DESC);
        typeOption.setArgName(TYPE_ARG);
        _options.addOption(typeOption);

        // additional parts to include
        Option includeOption = new Option(INCLUDE_SHORT, INCLUDE_DESC);
        includeOption.setArgs(4);
        includeOption.setArgName(INEXCLUDE_ARG);
        includeOption.setLongOpt(INCLUDE_LONG);
        _options.addOption(includeOption);

        // parts to exclude
        Option excludeOption = new Option(EXCLUDE_SHORT, EXCLUDE_DESC);
        excludeOption.setArgs(4);
        excludeOption.setArgName(INEXCLUDE_ARG);
        excludeOption.setLongOpt(EXCLUDE_LONG);
        _options.addOption(excludeOption);

        // runtime jre
        Option jreOption = new Option(JRE_SHORT, JRE_LONG, true, JRE_DESC);
        jreOption.setArgName(DIRECTORY_ARG);
        _options.addOption(jreOption);

        // verbose
        Option verboseOption = new Option(VERBOSE_SHORT, VERBOSE_LONG, false, VERBOSE_DESC);
        _options.addOption(verboseOption);

        // different help options
        Option helpHOption = new Option(HELP_SHORT, HELP_LONG, false, HELP_DESC);
        Option helpQOption = new Option(HELP2_SHORT, HELP_DESC);
        OptionGroup group2 = new OptionGroup();
        group2.addOption(helpHOption);
        group2.addOption(helpQOption);
        _options.addOptionGroup(group2);
    }

    private boolean isValidInExcludePart(String part) {
        return INEXCLUDE_DEMOS_AND_EXAMPLES.equals(part) || INEXCLUDE_DOCUMENTATION.equals(part)
                || INEXCLUDE_LIBRARY_MODULES.equals(part) || INEXCLUDE_SOURCES.equals(part);
    }

}
