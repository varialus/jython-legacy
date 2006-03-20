package org.python.util.install;

import javax.swing.JOptionPane;

public class Wizard extends AbstractWizard {

    public Wizard(JarInfo jarInfo) {
        super();

        setTitle(Installation.getText(TextKeys.JYTHON_INSTALL));

        LanguagePage languagePage = new LanguagePage(jarInfo);
        LicensePage licensePage = new LicensePage(jarInfo);
        licensePage.setValidator(new LicensePageValidator(licensePage));
        TypePage typePage = new TypePage();
        DirectorySelectionPage directoryPage = new DirectorySelectionPage(jarInfo);
        directoryPage.setValidator(new DirectorySelectionPageValidator(directoryPage));
        JavaSelectionPage javaPage = new JavaSelectionPage();
        javaPage.setValidator(new JavaSelectionPageValidator(javaPage));
        OverviewPage overviewPage = new OverviewPage();
        ProgressPage progressPage = new ProgressPage(jarInfo);
        ReadmePage readmePage = new ReadmePage();
        SuccessPage successPage = new SuccessPage(jarInfo);

        this.addPage(languagePage);
        this.addPage(licensePage);
        this.addPage(typePage);
        this.addPage(directoryPage);
        this.addPage(javaPage);
        this.addPage(overviewPage);
        this.addPage(progressPage);
        this.addPage(readmePage);
        this.addPage(successPage);

        setSize(600, 300);
        validate();
    }

    protected boolean finish() {
        return true;
    }

    protected String getCancelString() {
        return Installation.getText(TextKeys.CANCEL);
    }

    protected String getFinishString() {
        return Installation.getText(TextKeys.FINISH);
    }

    protected String getNextString() {
        return Installation.getText(TextKeys.NEXT);
    }

    protected String getPreviousString() {
        return Installation.getText(TextKeys.PREVIOUS);
    }

    public void validationStarted(ValidationEvent event) {
    }

    public void validationFailed(ValidationEvent event, ValidationException exception) {
        JOptionPane.showMessageDialog(this, exception.getMessage(), Installation.getText(TextKeys.ERROR),
                JOptionPane.ERROR_MESSAGE);
    }

    public void validationInformationRequired(ValidationEvent event, ValidationInformationException exception) {
        JOptionPane.showMessageDialog(this, exception.getMessage(), Installation.getText(TextKeys.INFORMATION),
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void validationSucceeded(ValidationEvent event) {
    }
}