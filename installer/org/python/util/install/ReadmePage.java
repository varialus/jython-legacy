package org.python.util.install;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ReadmePage extends AbstractWizardPage {

    private JTextArea _textArea;

    public ReadmePage() {
        super();
        initComponents();
    }

    private void initComponents() {
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(1, 1));
        _textArea = new JTextArea(13, 80);
        JScrollPane scrollPane = new JScrollPane(_textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        _textArea.setEditable(false);
        _textArea.setText("n/a");
        centerPanel.add(scrollPane);

        setLayout(new BorderLayout(0, 5));
        add(centerPanel, BorderLayout.CENTER);
    }

    protected String getTitle() {
        return Installation.getText(TextKeys.README);
    }

    protected String getDescription() {
        return Installation.getText(TextKeys.PLEASE_README);
    }

    protected boolean isCancelVisible() {
        return true;
    }

    protected boolean isPreviousVisible() {
        return false;
    }

    protected boolean isNextVisible() {
        return true;
    }

    protected JComponent getFocusField() {
        return null;
    }

    protected void activate() {
        _textArea.setText(Installation.getReadmeText(FrameInstaller.getTargetDirectory()));
    }

    protected void passivate() {
    }

    protected void beforeValidate() {
    }

}