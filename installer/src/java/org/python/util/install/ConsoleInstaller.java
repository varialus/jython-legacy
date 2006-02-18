package org.python.util.install;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class ConsoleInstaller implements ProgressListener {
    private static final String CANCEL = "c";
    private static final String PROMPT = ">>>";
    private static final String BEGIN_ANSWERS = "[";
    private static final String END_ANSWERS = "]";

    private InstallerCommandLine _commandLine;
    private JarInstaller _jarInstaller;
    private JarInfo _jarInfo;

    public ConsoleInstaller(InstallerCommandLine commandLine, JarInfo jarInfo) {
        _commandLine = commandLine;
        _jarInfo = jarInfo;
        _jarInstaller = new JarInstaller(this, jarInfo);
    }

    public void install() {
        File targetDirectory;
        if (!_commandLine.hasSilentOption()) {
            welcome();
            selectLanguage();
            String installationType = selectInstallationType();
            File javaHome=null;//TODO:oti java home
            checkVersion();
            acceptLicense();
            targetDirectory = determineTargetDirectory();
            promptForCopying(targetDirectory);
            _jarInstaller.inflate(targetDirectory, installationType, javaHome);
            showReadme(targetDirectory);
            success(targetDirectory);
        } else {
            message(Installation.getText(TextKeys.C_SILENT_INSTALLATION));
            targetDirectory = _commandLine.getTargetDirectory();
            checkTargetDirectorySilent(targetDirectory);
            checkVersionSilent();//TODO:oti java home, same as checkVersion above
            _jarInstaller.inflate(targetDirectory, _commandLine.getInstallationType(), _commandLine.getJavaHome());
            success(targetDirectory);
        }
    }

    protected final static void message(String message) {
        System.out.println(message); // this System.out.println is intended
    }

    private void welcome() {
        message(Installation.getText(TextKeys.C_WELCOME_TO_JYTHON));
        message(Installation.getText(TextKeys.C_VERSION_INFO, _jarInfo.getVersion()));
        message(Installation.getText(TextKeys.C_AT_ANY_TIME_CANCEL, CANCEL));
    }

    /**
     * question and answer
     * 
     * @param answers Possible answers (may be null)
     * @return (chosen) answer
     */
    private String question(String question, List answers) {
        if (answers != null && answers.size() > 0) {
            question = question + " " + BEGIN_ANSWERS;
            Iterator answersAsIterator = answers.iterator();
            while (answersAsIterator.hasNext()) {
                if (!question.endsWith(BEGIN_ANSWERS))
                    question = question + "/";
                question = question + (String) answersAsIterator.next();
            }
            question = question + END_ANSWERS;
        }
        question = question + " " + PROMPT + " ";
        boolean match = false;
        String answer = "";
        while (!match && !answer.equalsIgnoreCase(CANCEL)) {
            System.out.print(question); // this is print, not println (!)
            answer = readline();
            if (answers != null && answers.size() > 0) {
                Iterator answersAsIterator = answers.iterator();
                while (answersAsIterator.hasNext()) {
                    if (answer.equalsIgnoreCase((String) answersAsIterator.next())) {
                        match = true;
                    }
                }
            } else {
                match = true;
            }
        }
        if (answer.equalsIgnoreCase(CANCEL)) {
            throw new InstallationCancelledException();
        }
        return answer;
    }

    private String readline() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line = "";
        try {
            line = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }

    private void selectLanguage() {
        List availableLanguages = new ArrayList(2);
        availableLanguages.add(Installation.getText(TextKeys.C_ENGLISH));
        availableLanguages.add(Installation.getText(TextKeys.C_GERMAN)); // 1 == German
        List answers = new ArrayList(availableLanguages.size());
        String languages = "";
        for (Iterator iterator = availableLanguages.iterator(); iterator.hasNext();) {
            String language = (String) iterator.next();
            languages = languages + language + ", ";
            answers.add(language.substring(0, 1).toLowerCase());
        }
        languages = languages.substring(0, languages.length() - 2);
        message(Installation.getText(TextKeys.C_AVAILABLE_LANGUAGES, languages));
        String answer = question(Installation.getText(TextKeys.C_SELECT_LANGUAGE), answers);
        if (answer.equalsIgnoreCase((String) answers.get(1))) {
            Installation.setLanguage(Locale.GERMAN);
        } else {
            Installation.setLanguage(Locale.ENGLISH);
        }
    }

    private String selectInstallationType() {
        message(Installation.getText(TextKeys.C_INSTALL_TYPES));
        message("  " + Installation.ALL + ". " + Installation.getText(TextKeys.C_ALL));
        message("  " + Installation.STANDARD + ". " + Installation.getText(TextKeys.C_STANDARD));
        message("  " + Installation.MINIMUM + ". " + Installation.getText(TextKeys.C_MINIMUM));
        List answers = new ArrayList(3);
        answers.add(Installation.ALL);
        answers.add(Installation.STANDARD);
        answers.add(Installation.MINIMUM);
        return question(Installation.getText(TextKeys.C_SELECT_INSTALL_TYPE), answers);
    }

    private void checkVersion() {
        String osName = System.getProperty(Installation.OS_NAME);
        String osVersion = System.getProperty(Installation.OS_VERSION);
        String javaVendor = System.getProperty(Installation.JAVA_VENDOR);
        String javaVersion = System.getProperty(Installation.JAVA_VERSION);
        message(Installation.getText(TextKeys.C_OS_VERSION, osName, osVersion));
        if (Installation.isValidOs()) {
            question(Installation.getText(TextKeys.C_PROCEED), null);
        } else {
            message(Installation.getText(TextKeys.C_UNSUPPORTED_OS));
            question(Installation.getText(TextKeys.C_PROCEED_ANYWAY), null);
        }
        message(Installation.getText(TextKeys.C_JAVA_VERSION, javaVendor, javaVersion));
        if (Installation.isValidJava()) {
            question(Installation.getText(TextKeys.C_PROCEED), null);
        } else {
            message(Installation.getText(TextKeys.C_UNSUPPORTED_JAVA));
            question(Installation.getText(TextKeys.C_PROCEED_ANYWAY), null);
        }
    }

    private void checkVersionSilent() {
        if (!Installation.isValidOs()) {
            message(Installation.getText(TextKeys.C_UNSUPPORTED_OS));
        }
        if (!Installation.isValidJava()) {
            message(Installation.getText(TextKeys.C_UNSUPPORTED_JAVA));
        }
    }

    private void acceptLicense() {
        String read = question(Installation.getText(TextKeys.C_READ_LICENSE), getYNAnswers());
        if (read.equalsIgnoreCase(Installation.getText(TextKeys.C_YES))) {
            String licenseText = "n/a";
            try {
                licenseText = _jarInfo.getLicenseText();
                message(licenseText);
            } catch (IOException ioe) {
                throw new InstallerException(ioe);
            }
        }
        String accept = question(Installation.getText(TextKeys.C_ACCEPT), getYNAnswers());
        if (!accept.equalsIgnoreCase(Installation.getText(TextKeys.C_YES))) {
            throw new InstallationCancelledException();
        }
    }

    private File determineTargetDirectory() {
        File targetDirectory = null;
        try {
            do {
                targetDirectory = new File(question(Installation.getText(TextKeys.C_ENTER_TARGET_DIRECTORY), null));
                if (targetDirectory.exists() && !targetDirectory.isDirectory()) {
                    message(Installation.getText(TextKeys.C_NOT_A_DIRECTORY, targetDirectory.getCanonicalPath()));
                }
            } while (targetDirectory.exists() && !targetDirectory.isDirectory());
            if (!targetDirectory.exists()) {
                String create = question(Installation.getText(TextKeys.C_CREATE_DIRECTORY, targetDirectory
                        .getCanonicalPath()), getYNAnswers());
                if (create.equalsIgnoreCase(Installation.getText(TextKeys.C_YES))) {
                    if (!targetDirectory.mkdirs()) {
                        throw new InstallerException(Installation.getText(TextKeys.C_UNABLE_CREATE_DIRECTORY,
                                targetDirectory.getCanonicalPath()));
                    }
                } else {
                    throw new InstallationCancelledException();
                }
            }
            if (targetDirectory.list().length > 0) {
                String overwrite = question(Installation.getText(TextKeys.C_OVERWRITE_DIRECTORY, targetDirectory
                        .getCanonicalPath()), getYNAnswers());
                if (!overwrite.equalsIgnoreCase(Installation.getText(TextKeys.C_YES))) {
                    throw new InstallationCancelledException();
                } else {
                    String clear = question(Installation.getText(TextKeys.C_CLEAR_DIRECTORY, targetDirectory
                            .getCanonicalPath()), getYNAnswers());
                    if (clear.equalsIgnoreCase(Installation.getText(TextKeys.C_YES))) {
                        clearDirectory(targetDirectory);
                    } else {
                        throw new InstallationCancelledException();
                    }
                }
            }
        } catch (IOException ioe) {
            throw new InstallerException(ioe);
        }
        return targetDirectory;
    }

    private void checkTargetDirectorySilent(File targetDirectory) {
        try {
            if (!targetDirectory.exists()) {
                // create directory
                if (!targetDirectory.mkdirs()) {
                    throw new InstallerException(Installation.getText(TextKeys.C_UNABLE_CREATE_DIRECTORY,
                            targetDirectory.getCanonicalPath()));
                }
            } else {
                // assert it is an empty directory
                if (!targetDirectory.isDirectory()) {
                    throw new InstallerException(Installation.getText(TextKeys.C_NOT_A_DIRECTORY, targetDirectory
                            .getCanonicalPath()));
                } else {
                    if (targetDirectory.list().length > 0) {
                        throw new InstallerException(Installation.getText(TextKeys.C_NON_EMPTY_TARGET_DIRECTORY,
                                targetDirectory.getCanonicalPath()));
                    }
                }
            }
        } catch (IOException ioe) {
            throw new InstallerException(ioe);
        }
    }

    private void showReadme(final File targetDirectory) {
        String read = question(Installation.getText(TextKeys.C_READ_README), getYNAnswers());
        if (read.equalsIgnoreCase(Installation.getText(TextKeys.C_YES))) {
            try {
                message(Installation.getReadmeText(targetDirectory.getCanonicalPath()));
                question(Installation.getText(TextKeys.C_PROCEED), null);
            } catch (IOException ioe) {
                throw new InstallerException(ioe);
            }
        }
    }

    private void clearDirectory(File targetDirectory) {
        File files[] = targetDirectory.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                clearDirectory(files[i]);
            }
            if (!files[i].delete()) {
                throw new InstallerException(Installation.getText(TextKeys.C_UNABLE_TO_DELETE, files[i]
                        .getAbsolutePath()));
            }
        }
    }

    private void promptForCopying(final File targetDirectory) {
        try {
            String proceed = question(Installation.getText(TextKeys.C_CONFIRM_TARGET, targetDirectory
                    .getCanonicalPath()), getYNAnswers());
            if (!proceed.equalsIgnoreCase(Installation.getText(TextKeys.C_YES))) {
                throw new InstallationCancelledException();
            }
        } catch (IOException ioe) {
            throw new InstallerException(ioe); // catch for the compiler
        }
    }

    private void success(final File targetDirectory) {
        try {
            message(Installation.getText(TextKeys.C_CONGRATULATIONS)
                    + " "
                    + Installation.getText(TextKeys.C_SUCCESS, _jarInfo.getVersion(), targetDirectory
                            .getCanonicalPath()));
        } catch (IOException ioe) {
            throw new InstallerException(ioe); // catch for the compiler
        }
    }

    private List getYNAnswers() {
        List answers = new ArrayList(2);
        answers.add(Installation.getText(TextKeys.C_YES));
        answers.add(Installation.getText(TextKeys.C_NO));
        return answers;
    }

    private void progressMessage(int percentage) {
        message(" " + percentage + " %");
    }

    //
    // interface ProgressListener
    //

    public void progressChanged(int newPercentage) {
        progressMessage(newPercentage);
    }

    public int getInterval() {
        return 10; // fixed interval for console installer
    }

    public void progressFinished() {
        progressMessage(100);
    }

    public void progressEntry(String entry) {
        // ignore the single entries - only used in gui mode
    }

    public void progressStartScripts() {
        message(Installation.getText(TextKeys.GENERATING_START_SCRIPTS));
    }

}