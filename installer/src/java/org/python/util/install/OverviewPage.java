package org.python.util.install;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.python.util.install.Installation.JavaVersionInfo;

public class OverviewPage extends AbstractWizardPage {

    private final static int _LONGER_LENGTH = 25;
    private final static int _SHORTER_LENGTH = 10;

    private JLabel _directoryLabel;
    private JLabel _typeLabel;
    private JTextField _directory;
    private JTextField _type;
    private JLabel _message;

    private JLabel _osLabel;
    private JCheckBox _osBox;
    private JLabel _javaLabel;
    private JTextField _javaVendor;
    private JTextField _javaVersion;
    private JCheckBox _javaBox;

    public OverviewPage() {
        super();
        initComponents();
    }

    private void initComponents() {
        _directoryLabel = new JLabel();
        _directory = new JTextField(_LONGER_LENGTH);
        _directory.setEditable(false);
        _typeLabel = new JLabel();
        _type = new JTextField(_LONGER_LENGTH);
        _type.setEditable(false);

        _osLabel = new JLabel();
        JTextField osName = new JTextField(_LONGER_LENGTH);
        osName.setText(System.getProperty(Installation.OS_NAME));
        osName.setEditable(false);
        JTextField osVersion = new JTextField(_SHORTER_LENGTH);
        osVersion.setText(System.getProperty(Installation.OS_VERSION));
        osVersion.setEditable(false);
        _osBox = new JCheckBox();
        _osBox.setEnabled(false);
        _osBox.setSelected(Installation.isValidOs());

        _javaLabel = new JLabel();
        _javaVendor = new JTextField(_LONGER_LENGTH);
        _javaVendor.setEditable(false);
        _javaVersion = new JTextField(_SHORTER_LENGTH);
        _javaVersion.setEditable(false);
        _javaBox = new JCheckBox();
        _javaBox.setEnabled(false);

        _message = new JLabel();

        JPanel panel = new JPanel();
        GridBagLayout gridBagLayout = new GridBagLayout();
        panel.setLayout(gridBagLayout);
        GridBagConstraints gridBagConstraints = newGridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        panel.add(_directoryLabel, gridBagConstraints);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        panel.add(_directory, gridBagConstraints);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        panel.add(_typeLabel, gridBagConstraints);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        panel.add(_type, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        panel.add(_osLabel, gridBagConstraints);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        panel.add(osName, gridBagConstraints);
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        panel.add(osVersion, gridBagConstraints);
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        panel.add(_osBox, gridBagConstraints);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        panel.add(_javaLabel, gridBagConstraints);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        panel.add(_javaVendor, gridBagConstraints);
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        panel.add(_javaVersion, gridBagConstraints);
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        panel.add(_javaBox, gridBagConstraints);

        // attn special constraints for message (should always be on last row)
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        panel.add(_message, gridBagConstraints);

        add(panel);
    }

    protected String getTitle() {
        return getText(OVERVIEW_TITLE);
    }

    protected String getDescription() {
        return getText(OVERVIEW_DESCRIPTION);
    }

    protected boolean isCancelVisible() {
        return true;
    }

    protected boolean isPreviousVisible() {
        return true;
    }

    protected boolean isNextVisible() {
        return true;
    }

    protected JComponent getFocusField() {
        return null;
    }

    protected void activate() {
        // directory
        _directoryLabel.setText(getText(TARGET_DIRECTORY_PROPERTY) + ": ");
        _directory.setText(FrameInstaller.getTargetDirectory());

        // type
        _typeLabel.setText(getText(INSTALLATION_TYPE) + ": ");
        InstallationType installationType = FrameInstaller.getInstallationType();
        if (installationType.isAll()) {
            _type.setText(getText(ALL));
        }
        if (installationType.isStandard()) {
            _type.setText(getText(STANDARD));
        }
        if (installationType.isMinimum()) {
            _type.setText(getText(MINIMUM));
        }

        // os
        _osLabel.setText(getText(OS_INFO) + ": ");
        String osText;
        if (_osBox.isSelected()) {
            osText = getText(OK);
        } else {
            osText = getText(MAYBE_NOT_SUPPORTED);
        }
        _osBox.setText(osText);

        // java
        _javaLabel.setText(getText(JAVA_INFO) + ": ");
        JavaVersionInfo javaVersionInfo = FrameInstaller.getJavaVersionInfo();
        _javaVendor.setText(javaVersionInfo.getVendor());
        _javaVersion.setText(javaVersionInfo.getVersion());
        _javaBox.setSelected(Installation.isValidJava(javaVersionInfo));
        String javaText;
        if (_javaBox.isSelected()) {
            javaText = getText(OK);
        } else {
            javaText = getText(NOT_OK);
        }
        _javaBox.setText(javaText);

        // message
        _message.setText(getText(CONFIRM_START, getText(NEXT)));
    }

    protected void passivate() {
    }

    protected void beforeValidate() {
    }

}