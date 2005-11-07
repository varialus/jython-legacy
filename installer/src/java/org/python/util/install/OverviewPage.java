package org.python.util.install;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class OverviewPage extends AbstractWizardPage {

    private JLabel _directoryLabel;
    private JLabel _typeLabel;
    private JTextField _directory;
    private JTextField _type;
    private JLabel _message;

    public OverviewPage() {
        super();
        initComponents();
    }

    private void initComponents() {
        _directoryLabel = new JLabel();
        _directory = new JTextField();
        _directory.setEditable(false);
        _typeLabel = new JLabel();
        _type = new JTextField();
        _type.setEditable(false);
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
        // attn special constraints for message (should always be on last row)
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3; // or 2 ?
        gridBagConstraints.gridwidth = 2;
        panel.add(_message, gridBagConstraints);

        add(panel);
    }

    private GridBagConstraints newGridBagConstraints() {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        return gridBagConstraints;
    }

    protected String getTitle() {
        return Installation.getText(TextKeys.OVERVIEW_TITLE);
    }

    protected String getDescription() {
        return Installation.getText(TextKeys.OVERVIEW_DESCRIPTION);
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
        _directoryLabel.setText(Installation.getText(TextKeys.TARGET_DIRECTORY_PROPERTY) + ": ");
        _directory.setText(FrameInstaller.getTargetDirectory());
        _typeLabel.setText(Installation.getText(TextKeys.INSTALLATION_TYPE_PROPERTY) + ": ");
        String installationType = FrameInstaller.getInstallationType();
        if (installationType.equals(Installation.ALL)) {
            _type.setText(Installation.getText(TextKeys.ALL));
        }
        if (installationType.equals(Installation.STANDARD)) {
            _type.setText(Installation.getText(TextKeys.STANDARD));
        }
        if (installationType.equals(Installation.MINIMUM)) {
            _type.setText(Installation.getText(TextKeys.MINIMUM));
        }
        _message.setText(Installation.getText(TextKeys.CONFIRM_START, Installation.getText(TextKeys.NEXT)));
    }

    protected void passivate() {
    }

    protected void beforeValidate() {
    }

}