package org.python.util.install;

import java.io.File;

import org.python.util.install.Installation.JavaVersionInfo;

public class JavaSelectionPageValidator extends AbstractWizardValidator {

    JavaSelectionPage _page;

    JavaSelectionPageValidator(JavaSelectionPage page) {
        super();
        _page = page;
    }

    protected void validate() throws ValidationException {
        String directory = _page.getJavaHome().getText().trim(); // trim to be sure
        File javaHome = new File(directory);
        JavaVersionInfo javaVersionInfo = Installation.getExternalJavaVersion(javaHome);
        if (javaVersionInfo.getErrorCode() != Installation.NORMAL_RETURN) {
            throw new ValidationException(javaVersionInfo.getReason());
        }
        FrameInstaller.setTargetJavaHome(directory);
        FrameInstaller.setJavaVersionInfo(javaVersionInfo);
    }

}
