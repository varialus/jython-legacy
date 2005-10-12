package org.python.util.install;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Locale;
import java.util.Properties;

import javax.swing.UIManager;

public class FrameInstaller {
    private static final String TRUE = "1";
    private static final String FALSE = "0";

    private static Properties _properties = new Properties();

    protected FrameInstaller(JarInfo jarInfo) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
        Wizard wizard = new Wizard(jarInfo);
        wizard.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                System.exit(0);
            }
        });
        wizard.addWizardListener(new SimpleWizardListener());
        wizard.show();
    }

    protected static void setProperty(String key, String value) {
        _properties.setProperty(key, value);
    }

    protected static String getProperty(String key) {
        return _properties.getProperty(key);
    }

    protected static String getProperty(String key, String defaultValue) {
        return _properties.getProperty(key, defaultValue);
    }

    protected static void setTargetDirectory(String targetDirectory) {
        setProperty(TextKeys.TARGET_DIRECTORY_PROPERTY, targetDirectory.trim());
    }

    protected static String getTargetDirectory() {
        return getProperty(TextKeys.TARGET_DIRECTORY_PROPERTY);
    }

    protected static void setLanguage(Locale locale) {
        setProperty(TextKeys.LANGUAGE_PROPERTY, locale.toString());
        Installation.setLanguage(locale);
    }

    protected static Locale getLanguage() {
        return new Locale(getProperty(TextKeys.LANGUAGE_PROPERTY));
    }

    protected static String getInstallationType() {
        return getProperty(TextKeys.INSTALLATION_TYPE_PROPERTY, Installation.ALL);
    }

    protected static void setInstallationType(String installationType) {
        setProperty(TextKeys.INSTALLATION_TYPE_PROPERTY, installationType);
    }

    protected static void setAccept(boolean accept) {
        if (accept) {
            setProperty(TextKeys.ACCEPT_PROPERTY, TRUE);
        } else {
            setProperty(TextKeys.ACCEPT_PROPERTY, FALSE);
        }
    }

    protected static boolean isAccept() {
        return TRUE.equals(getProperty(TextKeys.ACCEPT_PROPERTY, FALSE));
    }

    private class SimpleWizardListener implements WizardListener {
        public void wizardStarted(WizardEvent event) {
        }

        public void wizardFinished(WizardEvent event) {
            System.exit(0);
        }

        public void wizardCancelled(WizardEvent event) {
            System.exit(1);
        }

        public void wizardNext(WizardEvent event) {
        }

        public void wizardPrevious(WizardEvent event) {
        }
    }
}