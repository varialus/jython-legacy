package org.python.util.install;

import java.util.ListResourceBundle;

public class TextConstants_de extends ListResourceBundle implements TextKeys {

    static final Object[][] contents = {
        // Die folgenden Texte dürfen Umlaute und Sonderzeichen enthalten:
        { ACCEPT, "Ja, ich akzeptiere" }, // license
        { ALL, "Alles (volle Installation, inklusive Sourcen)" }, // installation type
        { BROWSE, "Suchen..." }, // button (open the JFileChooser)
        { CANCEL, "Abbrechen" }, // button text
        { CHOOSE_LOCATION, "Wählen Sie das Verzeichnis, in das Jython installiert werden soll" }, // selection
        { CONFIRM_START, "Bitte drücken Sie {0}, um die Installation zu starten" }, // overview
        { CONGRATULATIONS, "Gratulation!" }, // congratulations
        { CREATED_DIRECTORY, "Verzeichnis {0} wurde erstellt" }, // directory
        { DIRECTORIES_ONLY, "Nur Verzeichnisse" }, // file chooser
        { DO_NOT_ACCEPT, "Nein, ich akzeptiere nicht" }, // license
        { EMPTY_TARGET_DIRECTORY, "Das Zielverzeichnis darf nicht leer sein" }, // error
        { ENGLISH, "Englisch" }, // language
        { ERROR, "Fehler" }, // error
        { ERROR_ACCESS_JARFILE, "Problem beim Zugriff auf das jar File" }, // error
        { FINISH, "Beenden" }, // button
        { GENERATING_START_SCRIPTS, "Start Scripts werden generiert ..." }, // progress
        { GERMAN, "Deutsch" }, // language
        { INFLATING, "Entpacke {0}" }, // progress
        { INFORMATION, "Information" }, // information
        { INSTALLATION_CANCELLED, "Sie haben die Installation abgebrochen." }, // final
        { INSTALLATION_IN_PROGRESS, "Die Installation läuft" }, // progress
        { INSTALLATION_TYPE_DESCRIPTION, "Die folgenden Installationstypen sind verfügbar" }, // installation type
        { INSTALLATION_TYPE_PROPERTY, "Installationstyp" }, // installation type
        { JAVA_INFO, "Java Hersteller / Version" }, // version
        { JAR_NOT_FOUND, "Jar File {0} nicht gefunden." }, // error
        { JYTHON_INSTALL, "Jython Installation" }, // title
        { LANGUAGE_PROPERTY, "Sprache" }, // language
        { LICENSE, "Lizenzvereinbarung" }, // license
        { MAYBE_NOT_SUPPORTED, "Eventuell nicht unterstützt" }, // version
        { MINIMUM, "Minimum (Kern)" }, // installation type
        { NEXT, "Weiter" }, // button
        { NON_EMPTY_TARGET_DIRECTORY, "Das Zielverzeichnis enthält bereits Daten." }, // error
        { NO_MANIFEST, "Jar File {0} enthält kein Manifest." }, // error
        { NOT_OK, "Nicht ok !" }, // version
        { OK, "Ok" }, // version
        { OS_INFO, "Betriebssystem Name / Version" }, // version
        { OVERVIEW_DESCRIPTION, "Sie haben folgende Einstellungen für die Installation ausgewählt" }, // overview
        { OVERVIEW_TITLE, "Übersicht über die gewählten Einstellungen" }, // overview
        { PLEASE_ACCEPT_LICENSE, "Bitte lesen und akzeptieren Sie die Lizenzvereinbarung" }, // license
        { PLEASE_README, "Bitte lesen Sie die folgenden Informationen" }, // readme
        { PLEASE_READ_LICENSE, "Bitte lesen Sie die Lizenzvereinbarung sorfältig durch" }, // license
        { PLEASE_WAIT, "Bitte um etwas Geduld, die Installation kann einige Sekunden dauern ..." }, // progress
        { PRESS_FINISH, "Bitte drücken Sie {0}, um die Installation abzuschliessen." }, // finish
        { PREVIOUS, "Zurück" }, // button
        { PROGRESS, "Fortschritt" }, // progress
        { README, "README" }, // readme
        { SELECT, "Auswählen" }, // button (approval in JFileChooser)
        { SELECT_INSTALLATION_TYPE, "Bitte wählen Sie den Installationstyp" }, // installation type
        { SELECT_LANGUAGE, "Bitte wählen Sie Ihre Sprache" }, // language
        { SELECT_TARGET_DIRECTORY, "Bitte wählen Sie das Zielverzeichnis" }, // directory
        { STANDARD, "Standard (Kern, Bibliotheksmodule, Demos und Beispiele)" }, // installation type
        { SUCCESS, "Sie haben Jython {0} erfolgreich im Verzeichnis {1} installiert." }, // final
        { TARGET_DIRECTORY_PROPERTY, "Zielverzeichnis" }, // property
        { UNABLE_CREATE_DIRECTORY, "Fehler beim Erstellen von Verzeichnis {0}." }, // error
        { UNABLE_CREATE_FILE, "Fehler beim Erstellen von File {0}." }, // error
        { UNABLE_TO_DELETE, "Fehler beim Löschen von {0}" }, // console
        { UNEXPECTED_URL, "Das Jar File für die Installation weist eine unerwartete URL {0} auf." }, // error
        { VERSION_CHECK, "Versionsprüfung" }, // version
        { VERSION_CHECK_DESCRIPTION, "Die Installationsvoraussetzungen werden überprüft" }, // version
        { VERSION_INFO, "Sie sind im Begriff, Jython Version {0} zu installieren." }, // version
        { WELCOME_TO_JYTHON, "Willkommen bei Jython !" }, // welcome
        { ZIP_ENTRY_SIZE, "Der Zip Eintrag {0} hat eine unbekannte Grösse." }, // error
        { ZIP_ENTRY_TOO_BIG, "Der Zip Eintrag {0} ist zu gross." }, // error

        // Konsole Texte (beginnend mit C_) sollten keine Umlaute und andere Sonderzeichen enthalten:
        { C_ACCEPT, "Akzeptieren Sie die Lizenzvereinbarung ?" }, // license
        { C_ALL, "Alles (volle Installation, inklusive Sourcen)" }, // installation type
        { C_AT_ANY_TIME_CANCEL, "(Sie koennen die Installation jederzeit durch Eingabe von {0} abbrechen)" }, // console
        { C_AVAILABLE_LANGUAGES, "Die folgenden Sprachen sind fuer den Installationsvorgang verfuegbar: {0}" }, // languages
        { C_CLEAR_DIRECTORY, "Der Inhalt von Verzeichnis {0} wird anschliessend geloescht! Moechten Sie wirklich weiterfahren ?" }, //console
        { C_CONFIRM_TARGET, "Bitte bestaetigen Sie den Start des Kopiervorgangs ins Verzeichnis {0}" }, // console
        { C_CONGRATULATIONS, "Gratulation!" }, // congratulations
        { C_CREATE_DIRECTORY, "Das Verzeichnis {0} gibt es nicht - soll es erstellt werden ?" }, // console
        { C_ENTER_TARGET_DIRECTORY, "Bitte geben Sie das Zielverzeichnis ein" }, // console
        { C_ENTER_JAVA_HOME, "Bitte geben Sie das gewuenschte java home Verzeichnis ein" }, // console
        { C_ENGLISH, "Englisch" }, // language
        { C_GERMAN, "Deutsch" }, // language
        { C_INSTALL_TYPES, "Die folgenden Installationstypen sind verfuegbar:" }, // installation type
        { C_JAVA_VERSION, "Ihre Java Version fuer den Start von Jython ist: {0} / {1}" }, // version
        { C_MINIMUM, "Minimum (Kern)" }, // installation type
        { C_NO, "n" }, // answer
        { C_NO_BIN_DIRECTORY, "Es gibt kein /bin Verzeichnis unterhalb {0}." }, //error
        { C_NON_EMPTY_TARGET_DIRECTORY, "Das Zielverzeichnis {0} enthaelt bereits Daten" }, // error
        { C_NOT_A_DIRECTORY, "{0} ist kein Verzeichnis. " }, // error
        { C_NOT_FOUND, "{0} nicht gefunden." }, // error
        { C_OS_VERSION, "Ihre Betriebssystem Version ist: {0} / {1}" }, // version
        { C_OVERWRITE_DIRECTORY, "Das Verzeichnis {0} enthaelt bereits Daten, und die Installation wuerde diese ueberschreiben - ok ?" }, // console
        { C_PROCEED, "Bitte druecken Sie Enter um weiterzufahren" }, // console
        { C_PROCEED_ANYWAY, "Bitte druecken Sie Enter um trotzdem weiterzufahren" }, // console
        { C_READ_LICENSE, "Moechten Sie die Lizenzvereinbarung jetzt lesen ?" }, // license
        { C_READ_README, "Moechten Sie den Inhalt von README jetzt lesen ?" }, // readme
        { C_SELECT_INSTALL_TYPE, "Bitte waehlen Sie den Installationstyp" }, // installation type
        { C_SELECT_LANGUAGE, "Bitte waehlen Sie Ihre Sprache" }, // language
        { C_SILENT_INSTALLATION, "Die Installation wird ohne Benutzerinteraktion ausgefuehrt" }, // installation mode
        { C_STANDARD, "Standard (Kern, Bibliotheksmodule, Demos und Beispiele)" }, // installation type
        { C_SUCCESS, "Sie haben Jython {0} erfolgreich im Verzeichnis {1} installiert." }, // final
        { C_TO_CURRENT_JAVA, "Warnung: Wechsel zum aktuellen JDK wegen Fehler: {0}." }, // warning
        { C_UNABLE_CREATE_DIRECTORY, "Fehler beim Erstellen von Verzeichnis {0}." }, // error
        { C_UNABLE_TO_DELETE, "Fehler beim Loeschen von {0}" }, // console
        { C_UNSUPPORTED_JAVA, "Diese Java Version ist nicht unterstuetzt." }, // version
        { C_UNSUPPORTED_OS, "Dieses Betriebssystem ist eventuell nicht vollstaendig unterstuetzt." }, // version
        { C_USING_TYPE, "Installationstyp ist {0}" }, // installation type
        { C_VERSION_INFO, "Sie sind im Begriff, Jython Version {0} zu installieren." }, // version
        { C_WELCOME_TO_JYTHON, "Willkommen bei Jython !" }, // welcome
        { C_YES, "j" }, // answer

    };

    public Object[][] getContents() {
        return contents;
    }

}