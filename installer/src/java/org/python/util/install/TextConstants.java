package org.python.util.install;

import java.util.ListResourceBundle;

public class TextConstants extends ListResourceBundle implements TextKeys {

    static final Object[][] contents = {
        // LOCALIZE THIS

        { ACCEPT, "I accept" }, // license
        { ALL, "All (everything, including sources)" }, // installation type
        { BROWSE, "Browse..." }, // button (open the JFileChooser)
        { CANCEL, "Cancel" }, // button
        { CHOOSE_LOCATION, "Choose the location where you want Jython to be installed to" }, // selection
        { CONFIRM_START, "Please press {0} to start the installation" }, // overview
        { CONGRATULATIONS, "Congratulations!" }, // congratulations
        { CREATED_DIRECTORY, "Created directory {0}" }, // directory
        { DO_NOT_ACCEPT, "I do not accept" }, // license
        { DIRECTORIES_ONLY, "Directories only" }, // file chooser
        { EMPTY_TARGET_DIRECTORY, "Target directory must not be empty" }, // error
        { ENGLISH, "English" }, // language
        { ERROR, "Error" }, // error
        { ERROR_ACCESS_JARFILE, "Error accessing jar file" }, // error
        { FINISH, "Finish" }, // button
        { GENERATING_START_SCRIPTS, "Generating start scripts ..." }, // progress
        { GERMAN, "German" }, // language
        { INFLATING, "Inflating {0}" }, // progress
        { INFORMATION, "Information" }, // information
        { INSTALLATION_CANCELLED, "Installation cancelled." }, // final
        { INSTALLATION_IN_PROGRESS, "The installation is now in progress" }, // progress
        { INSTALLATION_TYPE_DESCRIPTION, "The following installation types are available" }, // installation type
        { INSTALLATION_TYPE_PROPERTY, "Installation type" }, // installation type
        { JAR_NOT_FOUND, "Unable to find jar file {0}." }, // error
        { JAVA_INFO, "Java vendor / version" }, // version
        { JYTHON_INSTALL, "Jython Installation" }, // title
        { LANGUAGE_PROPERTY, "Language" }, // language
        { LICENSE, "License agreement" }, // license
        { MAYBE_NOT_SUPPORTED, "Maybe not supported" }, // version
        { MINIMUM, "Minimum (core)" }, // installation type
        { NEXT, "Next" }, // button
        { NON_EMPTY_TARGET_DIRECTORY, "Target directory is not empty" }, // error
        { NO_MANIFEST, "No manifest found in jar file {0}." }, // error
        { NOT_OK, "Not ok !" }, // version
        { OK, "Ok" }, // version
        { OS_INFO, "Operation system name / version" }, // version
        { OVERVIEW_DESCRIPTION, "The installation will be done using the following options" }, // overview
        { OVERVIEW_TITLE, "Overview (summary of options)" }, // overview
        { PLEASE_ACCEPT_LICENSE, "Please read and accept the license agreement" }, // license
        { PLEASE_README, "Please read the following information" }, // readme
        { PLEASE_READ_LICENSE, "Please read the license agreement carefully" }, // license
        { PLEASE_WAIT, "Please stand by, this may take a few seconds ..." }, // progress
        { PRESS_FINISH, "Please press {0} to exit the installation." }, // finish
        { PREVIOUS, "Previous" }, // button
        { PROGRESS, "Progress" }, // progress
        { README, "README" }, // readme
        { SELECT, "Select" }, // button (approval in JFileChooser)
        { SELECT_INSTALLATION_TYPE, "Please select the installation type" }, // installation type
        { SELECT_LANGUAGE, "Please select your language" }, // language
        { SELECT_TARGET_DIRECTORY, "Please select the target directory" }, // directory
        { STANDARD, "Standard (core, library modules, demos and examples)" }, // installation type
        { SUCCESS, "You successfully installed Jython {0} to directory {1}." }, // success
        { TARGET_DIRECTORY_PROPERTY, "Target directory" }, // property
        { UNABLE_CREATE_DIRECTORY, "Unable to create directory {0}." }, // error
        { UNABLE_CREATE_FILE, "Unable to create file {0}." }, // error
        { UNABLE_TO_DELETE, "Unable to delete {0}" }, // error
        { UNEXPECTED_URL, "Unexpected URL {0} found for installation jar file." }, // error
        { VERSION_CHECK, "Version check" }, // version
        { VERSION_CHECK_DESCRIPTION, "This is a check of the installation requirements" }, // version
        { VERSION_INFO, "You are about to install Jython version {0}" }, // version
        { WELCOME_TO_JYTHON, "Welcome to Jython !" }, // welcome
        { ZIP_ENTRY_SIZE, "Size of zip entry {0} unknown." }, // error
        { ZIP_ENTRY_TOO_BIG, "Zip entry {0} too big." }, // error

        // console texts (C_*) should not contain special characters (like e.g. &uuml;)
        { C_ACCEPT, "Do you accept the license agreement ?" }, // license
        { C_ALL, "All (everything, including sources)" }, // installation type
        { C_AT_ANY_TIME_CANCEL, "(at any time, answer {0} to cancel the installation)" }, // console
        { C_AVAILABLE_LANGUAGES, "For the installation process, the following languages are available: {0}" }, // console
        { C_CLEAR_DIRECTORY, "Contents of directory {0} will be deleted now! Are you sure to proceed ?" }, //console
        { C_CONFIRM_TARGET, "Please confirm copying of files to directory {0}" }, // console
        { C_CONGRATULATIONS, "Congratulations!" }, // congratulations
        { C_CREATE_DIRECTORY, "Unable to find directory {0}, create it ?" }, // console
        { C_ENTER_TARGET_DIRECTORY, "Please enter the target directory" }, // console
        { C_ENGLISH, "English" }, // language
        { C_GERMAN, "German" }, // language
        { C_INSTALL_TYPES, "The following installation types are available:" }, // installation type
        { C_JAVA_VERSION, "Your java version is: {0} / {1}" }, // version
        { C_MINIMUM, "Minimum (core)" }, // installation type
        { C_NO, "n" }, // answer
        { C_NON_EMPTY_TARGET_DIRECTORY, "Target directory {0} is not empty" }, // error
        { C_NOT_A_DIRECTORY, "{0} is not a directory. " }, // error
        { C_OS_VERSION, "Your operation system version is: {0} / {1}" }, // version
        { C_OVERWRITE_DIRECTORY, "Directory {0} is not empty - ok to overwrite contents ?" }, // console
        { C_PROCEED, "Please press Enter to proceed" }, // console
        { C_PROCEED_ANYWAY, "Please press Enter to proceed anyway" }, // console
        { C_READ_LICENSE, "Do you want to read the license agreement now ?" }, // license
        { C_READ_README, "Do you want to show the contents of README ?" }, // readme
        { C_SELECT_INSTALL_TYPE, "Please select the installation type" }, // installation type
        { C_SELECT_LANGUAGE, "Please select your language" }, // language
        { C_SILENT_INSTALLATION, "Performing silent installation" }, // installation mode
        { C_STANDARD, "Standard (core, library modules, demos and examples)" }, // installation type
        { C_SUCCESS, "You successfully installed Jython {0} to directory {1}." }, // success
        { C_UNABLE_CREATE_DIRECTORY, "Unable to create directory {0}." }, // error
        { C_UNABLE_TO_DELETE, "Unable to delete {0}" }, // error
        { C_UNSUPPORTED_JAVA, "This java version is not supported." }, // version
        { C_UNSUPPORTED_OS, "This operating system might not be fully supported." }, // version
        { C_USING_TYPE, "Using installation type {0}" }, // installation type
        { C_VERSION_INFO, "You are about to install Jython version {0}" }, // version
        { C_WELCOME_TO_JYTHON, "Welcome to Jython !" }, // welcome
        { C_YES, "y" }, // answer
        
        // END OF MATERIAL TO LOCALIZE
    };

    public Object[][] getContents() {
        return contents;
    }

}