package org.python.util.install;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class ProgressPage extends AbstractWizardPage implements ProgressListener {

    private JarInfo _jarInfo;
    private JLabel _label;
    private JProgressBar _progressBar;
    private JLabel _progressEntry;

    public ProgressPage(JarInfo jarInfo) {
        super();
        _jarInfo = jarInfo;
        initComponents();
    }

    private void initComponents() {
        JPanel northPanel = new JPanel();
        _label = new JLabel();
        northPanel.add(_label);
        _progressBar = new JProgressBar();
        northPanel.add(_progressBar);
        JPanel centerPanel = new JPanel();
        _progressEntry = new JLabel();
        centerPanel.add(_progressEntry);
        setLayout(new BorderLayout(0, 5));
        add(northPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    protected String getTitle() {
        return Installation.getText(TextKeys.INSTALLATION_IN_PROGRESS);
    }

    protected String getDescription() {
        return Installation.getText(TextKeys.PLEASE_WAIT);
    }

    protected boolean isCancelVisible() {
        return true;
    }

    protected boolean isPreviousVisible() {
        return false;
    }

    protected boolean isNextVisible() {
        return false;
    }

    protected JComponent getFocusField() {
        return null;
    }

    protected void activate() {
        _label.setText(Installation.getText(TextKeys.PROGRESS) + ": ");
        _progressBar.setValue(0);
        _progressBar.setStringPainted(true);
        try {
            _progressEntry.setText(Installation.getText(TextKeys.INFLATING, _jarInfo.getJarFile().getName()));
        } catch (IOException e) {
            // should not happen
        }
        JarInstaller jarInstaller = new JarInstaller(this, _jarInfo);
        File targetDirectory = new File(FrameInstaller.getTargetDirectory());
        // TODO:oti correct java home
        File javaHome = new File(System.getProperty(JavaVersionTester.JAVA_HOME));
        jarInstaller.inflate(targetDirectory, FrameInstaller.getInstallationType(), javaHome); // TODO:oti
                                                                                                // FrameInstaller.getJavaHome()
    }

    protected void passivate() {
    }

    protected void beforeValidate() {
    }

    //
    // interface ProgressListener
    //

    public void progressChanged(int newPercentage) {
        _progressBar.setValue(newPercentage);
    }

    public int getInterval() {
        return 1;
    }

    public void progressFinished() {
        _progressBar.setValue(100);
        getWizard().gotoNextPage();
    }

    public void progressEntry(String entry) {
        _progressEntry.setText(Installation.getText(TextKeys.INFLATING, entry));
    }

    public void progressStartScripts() {
        _progressEntry.setText(Installation.getText(TextKeys.GENERATING_START_SCRIPTS));
    }
}