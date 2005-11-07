package org.python.util.install;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class Arguments extends Properties {

    private static final String OPTION_START = "-";

    private static final String CONSOLE_OPTION = OPTION_START + "console";
    private static final String SILENT_OPTION = OPTION_START + "silent";
    private static final String DIRECTORY_OPTION = OPTION_START + "directory";
    private static final String TYPE_OPTION = OPTION_START + "type";

    private static final String HELP_OPTION = OPTION_START + "help";
    private static final String USAGE_OPTION = OPTION_START + "usage";

    private static final String STANDARD_TYPE = "standard";
    private static final String ALL_TYPE = "all";
    private static final String MINIMUM_TYPE = "minimum";

    private static final String EMPTY = "";

    private JarInfo _jarInfo;

    public Arguments(JarInfo jarInfo) {
        _jarInfo = jarInfo;
    }

    public boolean parse(String args[]) {
        boolean isValid = true;
        // property setting and formal validations
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith(OPTION_START)) {
                // allow only known options
                if (!CONSOLE_OPTION.equals(args[i]) && !SILENT_OPTION.equals(args[i])
                        && !DIRECTORY_OPTION.equals(args[i]) && !TYPE_OPTION.equals(args[i])) {
                    if (!HELP_OPTION.equals(args[i]) && !USAGE_OPTION.equals(args[i])) {
                        message(Installation.getText(TextKeys.C_INVALID_OPTION, args[i]));
                    }
                    isValid = false;
                }
                boolean hasValue = false;
                int oi = i;
                setProperty(args[i], EMPTY);
                if (i < args.length - 1) {
                    if (!args[i + 1].startsWith(OPTION_START)) {
                        setProperty(args[i], args[i + 1]);
                        hasValue = true;
                        i++;
                    }
                }
                if (!hasValue) {
                    // check options which need a value
                    if (DIRECTORY_OPTION.equals(args[oi]) || TYPE_OPTION.equals(args[oi])) {
                        message(Installation.getText(TextKeys.C_NEED_VALUE_FOR_OPTION, args[oi]));
                        isValid = false;
                    }
                }
            } else {
                i++;
            }
        }
        // logical validations
        if (isSilentMode()) {
            // at least directory is mandatory in silent mode
            if (!containsKey(DIRECTORY_OPTION)) {
                message(Installation.getText(TextKeys.C_TARGET_DIRECTORY_NOT_SPECIFIED, DIRECTORY_OPTION));
                isValid = false;
            }
        } else {
            // some options require silent mode
            if (containsKey(DIRECTORY_OPTION) || containsKey(TYPE_OPTION)) {
                message(Installation.getText(TextKeys.C_NEED_SILENT_MODE, getAllOptions(args), SILENT_OPTION));
                isValid = false;
            }
        }
        if (containsKey(TYPE_OPTION)) {
            // validate the possible installation types
            String typeName = getProperty(TYPE_OPTION, EMPTY);
            if (!EMPTY.equals(typeName)) {
                if (!STANDARD_TYPE.equals(typeName) && !ALL_TYPE.equals(typeName) && !MINIMUM_TYPE.equals(typeName)) {
                    message(Installation.getText(TextKeys.C_INVALID_TYPE, typeName));
                    isValid = false;
                }
            }
        }
        return isValid;
    }

    public void showUsage() throws IOException {
        String jarFileName = _jarInfo.getJarFile().getName();
        message(EMPTY);
        message(Installation.getText(TextKeys.C_USAGES));
        message(EMPTY);
        message("java -jar " + jarFileName);
        message("  " + Installation.getText(TextKeys.C_USAGE_FRAME));
        message(EMPTY);
        message("java -jar " + jarFileName + " " + CONSOLE_OPTION);
        message("  " + Installation.getText(TextKeys.C_USAGE_CONSOLE));
        message(EMPTY);
        message("java -jar " + jarFileName + " " + SILENT_OPTION + " " + DIRECTORY_OPTION + " targetDirectory");
        message("  " + Installation.getText(TextKeys.C_SILENT_INSTALLATION));
        message(EMPTY);
        message("java -jar " + jarFileName + " " + SILENT_OPTION + " " + DIRECTORY_OPTION + " targetDirectory "
                + TYPE_OPTION + " all");
        String types = STANDARD_TYPE + ", " + MINIMUM_TYPE + ", " + ALL_TYPE;
        message("  " + Installation.getText(TextKeys.C_USAGE_SILENT_TYPE, types));
        message(EMPTY);
    }

    public boolean isConsoleMode() {
        return containsKey(CONSOLE_OPTION);
    }

    public File getTargetDirectory() {
        return new File(getTargetDirectoryName());
    }

    public boolean isSilentMode() {
        if (containsKey(SILENT_OPTION)) {
            return true;
        } else {
            return isConsoleMode() && !EMPTY.equals(getTargetDirectoryName());
        }
    }

    public String getInstallationType() {
        String type = Installation.STANDARD;
        String typeName = getProperty(TYPE_OPTION, STANDARD_TYPE);
        if (STANDARD_TYPE.equals(typeName)) {
            type = Installation.STANDARD;
        } else if (ALL_TYPE.equals(typeName)) {
            type = Installation.ALL;
        } else if (MINIMUM_TYPE.equals(typeName)) {
            type = Installation.MINIMUM;
        }
        message(Installation.getText(TextKeys.C_USING_TYPE, typeName));
        return type;
    }

    private String getTargetDirectoryName() {
        return getProperty(DIRECTORY_OPTION, EMPTY);
    }

    private String getAllOptions(String[] args) {
        String options = EMPTY;
        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                options = options + " ";
            }
            options = options + args[i];
        }
        return options;
    }

    private void message(String message) {
        ConsoleInstaller.message(message);
    }

}