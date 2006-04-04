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
        JavaVersionInfo javaVersionInfo = new JavaVersionInfo();
        String currentJavaHomeName = System.getProperty(JavaVersionTester.JAVA_HOME);
        if (currentJavaHomeName.equals(javaHome.getAbsolutePath())) {
            // no experiments if current java is selected
            Installation.fillJavaVersionInfo(javaVersionInfo, System.getProperties());
            javaHome = new File(currentJavaHomeName);
        } else {
            javaVersionInfo = Installation.getExternalJavaVersion(javaHome);
            if (javaVersionInfo.getErrorCode() != Installation.NORMAL_RETURN) {
                throw new ValidationException(javaVersionInfo.getReason());
            }
        }
        FrameInstaller.setTargetJavaHome(javaHome.getAbsolutePath());
        FrameInstaller.setJavaVersionInfo(javaVersionInfo);
    }

}
