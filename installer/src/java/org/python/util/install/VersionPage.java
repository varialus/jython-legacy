package org.python.util.install;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.python.util.install.Installation.JavaVersionInfo;

public class VersionPage extends AbstractWizardPage {
    private JLabel _osLabel;
    private JCheckBox _osBox = new JCheckBox();
    private JLabel _javaLabel;
    private JCheckBox _javaBox = new JCheckBox();

    public VersionPage() {
        super();
        initComponents();
    }

    private void initComponents() {
        _osLabel = new JLabel();
        JTextField osName = new JTextField(System.getProperty(Installation.OS_NAME));
        osName.setEditable(false);
        JTextField osVersion = new JTextField(System.getProperty(Installation.OS_VERSION));
        osVersion.setEditable(false);
        _osBox.setEnabled(false);
        _osBox.setSelected(Installation.isValidOs());

        _javaLabel = new JLabel();
        JTextField javaVendor = new JTextField(System.getProperty(JavaVersionTester.JAVA_VENDOR));
        javaVendor.setEditable(false);
        JTextField javaVersion = new JTextField(System.getProperty(JavaVersionTester.JAVA_VERSION));
        javaVersion.setEditable(false);
        _javaBox.setEnabled(false);

        // TODO:oti correct java version info
        JavaVersionInfo javaVersionInfo = new JavaVersionInfo();
        Installation.fillJavaVersionInfo(javaVersionInfo, System.getProperties());

        _javaBox.setSelected(Installation.isValidJava(javaVersionInfo));

        JPanel panel = new JPanel();
        GridBagLayout gridBagLayout = new GridBagLayout();
        panel.setLayout(gridBagLayout);
        GridBagConstraints gridBagConstraints = newGridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        panel.add(_osLabel, gridBagConstraints);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        panel.add(osName, gridBagConstraints);
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        panel.add(osVersion, gridBagConstraints);
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        panel.add(_osBox, gridBagConstraints);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        panel.add(_javaLabel, gridBagConstraints);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        panel.add(javaVendor, gridBagConstraints);
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        panel.add(javaVersion, gridBagConstraints);
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        panel.add(_javaBox, gridBagConstraints);

        add(panel);
    }

    private GridBagConstraints newGridBagConstraints() {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        return gridBagConstraints;
    }

    protected String getTitle() {
        return Installation.getText(TextKeys.VERSION_CHECK);
    }

    protected String getDescription() {
        return Installation.getText(TextKeys.VERSION_CHECK_DESCRIPTION);
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
        _osLabel.setText(Installation.getText(TextKeys.OS_INFO) + ": ");
        String osText;
        if (_osBox.isSelected()) {
            osText = Installation.getText(TextKeys.OK);
        } else {
            osText = Installation.getText(TextKeys.MAYBE_NOT_SUPPORTED);
        }
        _osBox.setText(osText);
        _javaLabel.setText(Installation.getText(TextKeys.JAVA_INFO) + ": ");
        String javaText;
        if (_javaBox.isSelected()) {
            javaText = Installation.getText(TextKeys.OK);
        } else {
            javaText = Installation.getText(TextKeys.NOT_OK);
        }
        _javaBox.setText(javaText);
    }

    protected void passivate() {
    }

    protected void beforeValidate() {
    }

}