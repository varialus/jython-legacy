package org.python.util.install;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class TypePage extends AbstractWizardPage {

    private JLabel _label;
    private JRadioButton _allButton;
    private JRadioButton _standardButton;
    private JRadioButton _minimumButton;

    public TypePage() {
        super();
        initComponents();
    }

    private void initComponents() {
        _label = new JLabel();
        add(_label);
        // radio buttons
        RadioButtonListener radioButtonListener = new RadioButtonListener();
        _allButton = new JRadioButton();
        _allButton.setActionCommand(Installation.ALL);
        _allButton.addActionListener(radioButtonListener);
        _standardButton = new JRadioButton();
        _standardButton.setActionCommand(Installation.STANDARD);
        _standardButton.addActionListener(radioButtonListener);
        _minimumButton = new JRadioButton();
        _minimumButton.setActionCommand(Installation.MINIMUM);
        _minimumButton.addActionListener(radioButtonListener);
        ButtonGroup radioButtonGroup = new ButtonGroup();
        radioButtonGroup.add(_allButton);
        radioButtonGroup.add(_standardButton);
        radioButtonGroup.add(_minimumButton);
        JPanel radioPanel = new JPanel(new GridLayout(0, 1));
        radioPanel.add(_allButton);
        radioPanel.add(_standardButton);
        radioPanel.add(_minimumButton);
        add(radioPanel);
    }

    protected String getTitle() {
        return Installation.getText(TextKeys.INSTALLATION_TYPE_PROPERTY);
    }

    protected String getDescription() {
        return Installation.getText(TextKeys.INSTALLATION_TYPE_DESCRIPTION);
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
        _label.setText(Installation.getText(TextKeys.SELECT_INSTALLATION_TYPE) + ": ");
        _allButton.setText(Installation.getText(TextKeys.ALL));
        _standardButton.setText(Installation.getText(TextKeys.STANDARD));
        _minimumButton.setText(Installation.getText(TextKeys.MINIMUM));
        String installationType = FrameInstaller.getInstallationType();
        if (installationType.equals(Installation.ALL)) {
            _allButton.setSelected(true);
        }
        if (installationType.equals(Installation.STANDARD)) {
            _standardButton.setSelected(true);
        }
        if (installationType.equals(Installation.MINIMUM)) {
            _minimumButton.setSelected(true);
        }
    }

    protected void passivate() {
    }

    protected void beforeValidate() {
    }

    private final static class RadioButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            FrameInstaller.setInstallationType(e.getActionCommand());
        }
    }
}