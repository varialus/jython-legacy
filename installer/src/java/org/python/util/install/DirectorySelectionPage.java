package org.python.util.install;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class DirectorySelectionPage extends AbstractWizardPage {
    private JLabel _label;
    private JTextField _directory;
    private JButton _browse;
    private JarInfo _jarInfo;

    public DirectorySelectionPage(JarInfo jarInfo) {
        super();
        _jarInfo = jarInfo;
        initComponents();
    }

    private void initComponents() {
        // label
        _label = new JLabel();
        add(_label);
        // directory
        _directory = new JTextField(30);
        _directory.addFocusListener(new DirectoryFocusListener());
        add(_directory);
        // browse button
        _browse = new JButton();
        _browse.addActionListener(new BrowseButtonListener());
        add(_browse);
    }

    JTextField getDirectory() {
        return _directory;
    }

    protected String getTitle() {
        return Installation.getText(TextKeys.TARGET_DIRECTORY_PROPERTY);
    }

    protected String getDescription() {
        return Installation.getText(TextKeys.CHOOSE_LOCATION);
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
        return _directory;
    }

    protected void activate() {
        _label.setText(Installation.getText(TextKeys.SELECT_TARGET_DIRECTORY) + ": ");
        _browse.setText(Installation.getText(TextKeys.BROWSE));
        String directory = FrameInstaller.getTargetDirectory();
        if (directory == null || directory.length() <= 0) {
            File defaultDirectory = getDefaultDirectory();
            try {
                directory = defaultDirectory.getCanonicalPath();
            } catch (IOException e) {
                directory = "?";
            }
            FrameInstaller.setTargetDirectory(directory);
        }
        _directory.setText(FrameInstaller.getTargetDirectory());
    }

    protected void passivate() {
    }

    protected void beforeValidate() {
    }

    private File getDefaultDirectory() {
        // 1st try: user.home
        String directory = "";
        File parentDirectory = null;
        directory = System.getProperty("user.home", "");
        if (directory.length() > 0) {
            parentDirectory = new File(directory);
            if (parentDirectory.exists() && parentDirectory.isDirectory()) {
                return makeJythonSubDirectory(parentDirectory);
            }
        }
        // 2nd try: user.dir
        directory = System.getProperty("user.dir", "");
        if (directory.length() > 0) {
            parentDirectory = new File(directory);
            if (parentDirectory.exists() && parentDirectory.isDirectory()) {
                return makeJythonSubDirectory(parentDirectory);
            }
        }
        // 3rd try: current directory
        return makeJythonSubDirectory(new File(new File(new File("dummy").getAbsolutePath()).getParent()));
    }

    private File makeJythonSubDirectory(File parentDirectory) {
        String jythonSubDirectoryName = "jython" + _jarInfo.getVersion();
        return new File(parentDirectory, jythonSubDirectoryName);
    }

    private class BrowseButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String directoryName = _directory.getText();
            File directory = new File(directoryName);
            if (directory.exists()) {
                if (!directory.isDirectory()) {
                    // switch to parent directory if user typed the name of a file
                    directory = directory.getParentFile();
                }
            } else {
                // try to create the typed directory, switch to default
                // directory if all else fails
                if (!directory.mkdirs()) {
                    directory = getDefaultDirectory();
                }
            }
            JFileChooser fileChooser = new JFileChooser(directory);
            fileChooser.setDialogTitle(Installation.getText(TextKeys.SELECT_TARGET_DIRECTORY));
            // the filter is at the moment only used for the title of the dialog:
            fileChooser.setFileFilter(new DirectoryFilter());
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (fileChooser.isAcceptAllFileFilterUsed()) {
                if (Installation.isMacintosh() && Installation.isJDK141()) {
                    // work around ArrayIndexOutOfBoundsExceptio on Mac OS X, java version 1.4.1
                } else {
                    fileChooser.setAcceptAllFileFilterUsed(false);
                }
            }
            int returnValue = fileChooser.showDialog(_browse, Installation.getText(TextKeys.SELECT));
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                _directory.setText(fileChooser.getSelectedFile().getAbsolutePath());
                FrameInstaller.setTargetDirectory(_directory.getText());
            }
        }
    }

    private class DirectoryFocusListener implements FocusListener {
        public void focusGained(FocusEvent e) {
        }

        public void focusLost(FocusEvent e) {
            FrameInstaller.setTargetDirectory(_directory.getText());
        }
    }

}