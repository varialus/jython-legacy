package org.python.util.install;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.python.util.install.Installation.JavaFilenameFilter;
import org.python.util.install.Installation.JavaVersionInfo;

public class ConsoleInstaller implements ProgressListener, TextKeys {
    private static final String _CANCEL = "c";
    private static final String _PROMPT = ">>>";
    private static final String _BEGIN_ANSWERS = "[";
    private static final String _END_ANSWERS = "]";
    private static final String _CURRENT = "==";

    private InstallerCommandLine _commandLine;
    private JarInstaller _jarInstaller;
    private JarInfo _jarInfo;

    public ConsoleInstaller(InstallerCommandLine commandLine, JarInfo jarInfo) {
        _commandLine = commandLine;
        _jarInfo = jarInfo;
        _jarInstaller = new JarInstaller(this, jarInfo);
    }

    public void install() {
        File targetDirectory = null;
        File javaHome = null;
        if (!_commandLine.hasSilentOption()) {
            welcome();
            selectLanguage();
            acceptLicense();
            InstallationType installationType = selectInstallationType();
            targetDirectory = determineTargetDirectory();
            javaHome = checkVersion(determineJavaHome());
            promptForCopying(targetDirectory, installationType, javaHome);
            _jarInstaller.inflate(targetDirectory, installationType, javaHome);
            showReadme(targetDirectory);
            success(targetDirectory);
        } else {
            message(getText(C_SILENT_INSTALLATION));
            targetDirectory = _commandLine.getTargetDirectory();
            checkTargetDirectorySilent(targetDirectory);
            javaHome = checkVersionSilent(_commandLine.getJavaHome());
            _jarInstaller.inflate(targetDirectory, _commandLine.getInstallationType(), javaHome);
            success(targetDirectory);
        }
    }

    protected final static void message(String message) {
        System.out.println(message); // this System.out.println is intended
    }

    private void welcome() {
        message(getText(C_WELCOME_TO_JYTHON));
        message(getText(C_VERSION_INFO, _jarInfo.getVersion()));
        message(getText(C_AT_ANY_TIME_CANCEL, _CANCEL));
    }

    private String question(String question) {
        return question(question, null, false);
    }

    private String question(String question, boolean answerRequired) {
        return question(question, null, answerRequired);
    }

    private String question(String question, List answers) {
        return question(question, answers, true);
    }

    /**
     * question and answer
     * 
     * @param answers Possible answers (may be null)
     * @return (chosen) answer
     */
    private String question(String question, List answers, boolean answerRequired) {
        if (answers != null && answers.size() > 0) {
            question = question + " " + _BEGIN_ANSWERS;
            Iterator answersAsIterator = answers.iterator();
            while (answersAsIterator.hasNext()) {
                if (!question.endsWith(_BEGIN_ANSWERS))
                    question = question + "/";
                question = question + (String) answersAsIterator.next();
            }
            question = question + _END_ANSWERS;
        }
        question = question + " " + _PROMPT + " ";
        boolean match = false;
        String answer = "";
        while (!match && !_CANCEL.equalsIgnoreCase(answer)) {
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
                if (answerRequired && "".equals(answer)) {
                    match = false;
                }
            }
            if (!match && !_CANCEL.equalsIgnoreCase(answer)) {
                message(getText(C_INVALID_ANSWER, answer));
            }
        }
        if (_CANCEL.equalsIgnoreCase(answer)) {
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
        availableLanguages.add(getText(C_ENGLISH));
        availableLanguages.add(getText(C_GERMAN)); // 1 == German
        List answers = new ArrayList(availableLanguages.size());
        String languages = "";
        for (Iterator iterator = availableLanguages.iterator(); iterator.hasNext();) {
            String language = (String) iterator.next();
            languages = languages + language + ", ";
            answers.add(language.substring(0, 1).toLowerCase());
        }
        languages = languages.substring(0, languages.length() - 2);
        message(getText(C_AVAILABLE_LANGUAGES, languages));
        String answer = question(getText(C_SELECT_LANGUAGE), answers);
        if (answer.equalsIgnoreCase((String) answers.get(1))) {
            Installation.setLanguage(Locale.GERMAN);
        } else {
            Installation.setLanguage(Locale.ENGLISH);
        }
    }

    private InstallationType selectInstallationType() {
        InstallationType installationType = new InstallationType();
        String no = getText(C_NO);
        String yes = getText(C_YES);
        message(getText(C_INSTALL_TYPES));
        message("  " + Installation.ALL + ". " + getText(C_ALL));
        message("  " + Installation.STANDARD + ". " + getText(C_STANDARD));
        message("  " + Installation.MINIMUM + ". " + getText(C_MINIMUM));
        List answers = new ArrayList(3);
        answers.add(Installation.ALL);
        answers.add(Installation.STANDARD);
        answers.add(Installation.MINIMUM); // TODO:oti add standalone
        String answer = question(getText(C_SELECT_INSTALL_TYPE), answers);
        if (Installation.ALL.equals(answer)) {
            installationType.setAll();
        } else if (Installation.STANDARD.equals(answer)) {
            installationType.setStandard();
        } else if (Installation.MINIMUM.equals(answer)) {
            installationType.setMinimum();
        } else if (Installation.STANDALONE.equals(answer)) {
            installationType.setStandalone();
        }
        if (!installationType.isStandalone()) {
            // include parts ?
            if (!installationType.isAll()) {
                answer = question(getText(C_INCLUDE), getYNAnswers());
                if (yes.equals(answer)) {
                    answers = new ArrayList(5);
                    answers.add(InstallerCommandLine.INEXCLUDE_LIBRARY_MODULES);
                    answers.add(InstallerCommandLine.INEXCLUDE_DEMOS_AND_EXAMPLES);
                    answers.add(InstallerCommandLine.INEXCLUDE_DOCUMENTATION);
                    answers.add(InstallerCommandLine.INEXCLUDE_SOURCES);
                    answers.add(no);
                    do {
                        answer = question(getText(C_INEXCLUDE_PARTS, no), answers);
                        if (InstallerCommandLine.INEXCLUDE_LIBRARY_MODULES.equals(answer)) {
                            installationType.addLibraryModules();
                        } else if (InstallerCommandLine.INEXCLUDE_DEMOS_AND_EXAMPLES.equals(answer)) {
                            installationType.addDemosAndExamples();
                        } else if (InstallerCommandLine.INEXCLUDE_DOCUMENTATION.equals(answer)) {
                            installationType.addDocumentation();
                        } else if (InstallerCommandLine.INEXCLUDE_SOURCES.equals(answer)) {
                            installationType.addSources();
                        }
                        if (!no.equals(answer)) {
                            message(getText(C_SCHEDULED, answer));
                        }
                    } while (!no.equals(answer));
                }
            }
            // exclude parts ?
            if (!installationType.isMinimum()) {
                answer = question(getText(C_EXCLUDE), getYNAnswers());
                if (yes.equals(answer)) {
                    do {
                        answer = question(getText(C_INEXCLUDE_PARTS, no), answers);
                        if (InstallerCommandLine.INEXCLUDE_LIBRARY_MODULES.equals(answer)) {
                            installationType.removeLibraryModules();
                        } else if (InstallerCommandLine.INEXCLUDE_DEMOS_AND_EXAMPLES.equals(answer)) {
                            installationType.removeDemosAndExamples();
                        } else if (InstallerCommandLine.INEXCLUDE_DOCUMENTATION.equals(answer)) {
                            installationType.removeDocumentation();
                        } else if (InstallerCommandLine.INEXCLUDE_SOURCES.equals(answer)) {
                            installationType.removeSources();
                        }
                        if (!no.equals(answer)) {
                            message(getText(C_UNSCHEDULED, answer));
                        }
                    } while (!no.equals(answer));
                }
            }
        }
        return installationType;
    }

    private File checkVersion(File javaHome) {
        // handle target java version
        JavaInfo javaInfo = verifyTargetJava(javaHome);
        message(getText(C_JAVA_VERSION, javaInfo.getJavaVersionInfo().getVendor(), javaInfo
                .getJavaVersionInfo().getVersion()));
        if (Installation.isValidJava(javaInfo.getJavaVersionInfo())) {
            question(getText(C_PROCEED));
        } else {
            message(getText(C_UNSUPPORTED_JAVA));
            question(getText(C_PROCEED_ANYWAY));
        }
        // handle OS
        String osName = System.getProperty(Installation.OS_NAME);
        String osVersion = System.getProperty(Installation.OS_VERSION);
        message(getText(C_OS_VERSION, osName, osVersion));
        if (Installation.isValidOs()) {
            question(getText(C_PROCEED));
        } else {
            message(getText(C_UNSUPPORTED_OS));
            question(getText(C_PROCEED_ANYWAY));
        }
        return javaInfo.getJavaHome();
    }

    private File checkVersionSilent(File javaHome) {
        // check target java version
        JavaInfo javaInfo = verifyTargetJava(javaHome);
        if (!Installation.isValidJava(javaInfo.getJavaVersionInfo())) {
            message(getText(C_UNSUPPORTED_JAVA));
        }
        // check OS
        if (!Installation.isValidOs()) {
            message(getText(C_UNSUPPORTED_OS));
        }
        return javaInfo.getJavaHome();
    }

    private JavaInfo verifyTargetJava(File javaHome) {
        JavaVersionInfo javaVersionInfo = new JavaVersionInfo();
        String currentJavaHomeName = System.getProperty(JavaVersionTester.JAVA_HOME);
        if (javaHome != null) {
            if (currentJavaHomeName.equals(javaHome.getAbsolutePath())) {
                // no experiments if current java is selected
                javaHome = null;
            } else {
                javaVersionInfo = Installation.getExternalJavaVersion(javaHome);
                if (javaVersionInfo.getErrorCode() != Installation.NORMAL_RETURN) {
                    // switch back to current if an error occurred
                    message(getText(C_TO_CURRENT_JAVA, javaVersionInfo.getReason()));
                    javaHome = null;
                }
            }
        }
        if (javaHome == null) {
            Installation.fillJavaVersionInfo(javaVersionInfo, System.getProperties());
            javaHome = new File(currentJavaHomeName);
        }
        JavaInfo javaInfo = new JavaInfo();
        javaInfo.setJavaHome(javaHome);
        javaInfo.setJavaVersionInfo(javaVersionInfo);
        return javaInfo;
    }

    private void acceptLicense() {
        String read = question(getText(C_READ_LICENSE), getYNAnswers());
        if (read.equalsIgnoreCase(getText(C_YES))) {
            String licenseText = "n/a";
            try {
                licenseText = _jarInfo.getLicenseText();
                message(licenseText);
            } catch (IOException ioe) {
                throw new InstallerException(ioe);
            }
        }
        String accept = question(getText(C_ACCEPT), getYNAnswers());
        if (!accept.equalsIgnoreCase(getText(C_YES))) {
            throw new InstallationCancelledException();
        }
    }

    private File determineTargetDirectory() {
        File targetDirectory = null;
        try {
            do {
                targetDirectory = new File(question(getText(C_ENTER_TARGET_DIRECTORY), true));
                if (targetDirectory.exists()) {
                    if (!targetDirectory.isDirectory()) {
                        message(getText(C_NOT_A_DIRECTORY, targetDirectory.getCanonicalPath()));
                    } else {
                        if (targetDirectory.list().length > 0) {
                            String overwrite = question(getText(C_OVERWRITE_DIRECTORY,
                                    targetDirectory.getCanonicalPath()), getYNAnswers());
                            if (overwrite.equalsIgnoreCase(getText(C_YES))) {
                                String clear = question(getText(C_CLEAR_DIRECTORY,
                                        targetDirectory.getCanonicalPath()), getYNAnswers());
                                if (clear.equalsIgnoreCase(getText(C_YES))) {
                                    clearDirectory(targetDirectory);
                                }
                            }
                        }
                    }
                } else {
                    String create = question(getText(C_CREATE_DIRECTORY, targetDirectory
                            .getCanonicalPath()), getYNAnswers());
                    if (create.equalsIgnoreCase(getText(C_YES))) {
                        if (!targetDirectory.mkdirs()) {
                            throw new InstallerException(getText(C_UNABLE_CREATE_DIRECTORY,
                                    targetDirectory.getCanonicalPath()));
                        }
                    }
                }
            } while (!targetDirectory.exists() || !targetDirectory.isDirectory() || targetDirectory.list().length > 0);
        } catch (IOException ioe) {
            throw new InstallerException(ioe);
        }
        return targetDirectory;
    }

    private File determineJavaHome() {
        File javaHome = null;
        File binDir = null;
        try {
            boolean javaFound = false;
            do {
                String javaHomeName = question(getText(C_ENTER_JAVA_HOME, _CURRENT), true);
                if (_CURRENT.equals(javaHomeName)) {
                    javaHome = new File(System.getProperty(JavaVersionTester.JAVA_HOME));
                } else {
                    javaHome = new File(javaHomeName);
                }
                if (!javaHome.exists()) {
                    message(getText(C_NOT_FOUND, javaHome.getCanonicalPath()));
                } else {
                    if (!javaHome.isDirectory()) {
                        message(getText(C_NOT_A_DIRECTORY, javaHome.getCanonicalPath()));
                    } else {
                        binDir = new File(javaHome, "bin");
                        if (!binDir.exists()) {
                            message(getText(C_NO_BIN_DIRECTORY, javaHome.getCanonicalPath()));
                        } else {
                            if (binDir.list(new JavaFilenameFilter()).length <= 0) {
                                message(getText(C_NO_JAVA_EXECUTABLE, binDir.getCanonicalPath()));
                            } else {
                                javaFound = true;
                            }
                        }
                    }
                }
            } while (!javaHome.exists() || !javaHome.isDirectory() || !binDir.exists() || !javaFound);
        } catch (IOException ioe) {
            throw new InstallerException(ioe);
        }
        return javaHome;
    }

    private void checkTargetDirectorySilent(File targetDirectory) {
        try {
            if (!targetDirectory.exists()) {
                // create directory
                if (!targetDirectory.mkdirs()) {
                    throw new InstallerException(getText(C_UNABLE_CREATE_DIRECTORY,
                            targetDirectory.getCanonicalPath()));
                }
            } else {
                // assert it is an empty directory
                if (!targetDirectory.isDirectory()) {
                    throw new InstallerException(getText(C_NOT_A_DIRECTORY, targetDirectory
                            .getCanonicalPath()));
                } else {
                    if (targetDirectory.list().length > 0) {
                        throw new InstallerException(getText(C_NON_EMPTY_TARGET_DIRECTORY,
                                targetDirectory.getCanonicalPath()));
                    }
                }
            }
        } catch (IOException ioe) {
            throw new InstallerException(ioe);
        }
    }

    private void showReadme(final File targetDirectory) {
        String read = question(getText(C_READ_README), getYNAnswers());
        if (read.equalsIgnoreCase(getText(C_YES))) {
            try {
                message(Installation.getReadmeText(targetDirectory.getCanonicalPath()));
                question(getText(C_PROCEED));
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
                throw new InstallerException(getText(C_UNABLE_TO_DELETE, files[i]
                        .getAbsolutePath()));
            }
        }
    }

    private void promptForCopying(final File targetDirectory, final InstallationType installationType,
            final File javaHome) {
        try {
            message(getText(C_SUMMARY));
            message("  - " + InstallerCommandLine.INEXCLUDE_LIBRARY_MODULES + ": "
                    + installationType.installLibraryModules());
            message("  - " + InstallerCommandLine.INEXCLUDE_DEMOS_AND_EXAMPLES + ": "
                    + installationType.installDemosAndExamples());
            message("  - " + InstallerCommandLine.INEXCLUDE_DOCUMENTATION + ": "
                    + installationType.installDocumentation());
            message("  - " + InstallerCommandLine.INEXCLUDE_SOURCES + ": " + installationType.installSources());
            message("  - JRE: " + javaHome.getAbsolutePath());
            String proceed = question(getText(C_CONFIRM_TARGET, targetDirectory
                    .getCanonicalPath()), getYNAnswers());
            if (!proceed.equalsIgnoreCase(getText(C_YES))) {
                throw new InstallationCancelledException();
            }
        } catch (IOException ioe) {
            throw new InstallerException(ioe); // catch for the compiler
        }
    }

    private void success(final File targetDirectory) {
        try {
            message(getText(C_CONGRATULATIONS)
                    + " "
                    + getText(C_SUCCESS, _jarInfo.getVersion(), targetDirectory
                            .getCanonicalPath()));
        } catch (IOException ioe) {
            throw new InstallerException(ioe); // catch for the compiler
        }
    }

    private List getYNAnswers() {
        List answers = new ArrayList(2);
        answers.add(getText(C_YES));
        answers.add(getText(C_NO));
        return answers;
    }

    private void progressMessage(int percentage) {
        message(" " + percentage + " %");
    }

    private String getText(String textKey) {
        return Installation.getText(textKey);
    }
    
    private String getText(String textKey, String parameter0) {
        return Installation.getText(textKey, parameter0);
    }
    
    private String getText(String textKey, String parameter0, String parameter1) {
        return Installation.getText(textKey, parameter0, parameter1);
    }
    
    private static class JavaInfo {
        private JavaVersionInfo _javaVersionInfo;
        private File _javaHome;

        void setJavaHome(File javaHome) {
            _javaHome = javaHome;
        }

        File getJavaHome() {
            return _javaHome;
        }

        void setJavaVersionInfo(JavaVersionInfo javaVersionInfo) {
            _javaVersionInfo = javaVersionInfo;
        }

        JavaVersionInfo getJavaVersionInfo() {
            return _javaVersionInfo;
        }
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
        message(getText(GENERATING_START_SCRIPTS));
    }

}