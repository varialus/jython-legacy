package org.python.util.install;

import org.python.util.install.Installation.JavaVersionInfo;

import junit.framework.TestCase;

public class FrameInstallerTest extends TestCase {

    public void testJavaVersionInfo() {
        String version = "1;2;3";
        String vendor = "jython [macrosystems]";
        String specificationVersion = "@spec 1,4";

        JavaVersionInfo vInfo = new JavaVersionInfo();
        vInfo.setVersion(version);
        vInfo.setVendor(vendor);
        vInfo.setSpecificationVersion(specificationVersion);

        FrameInstaller.setJavaVersionInfo(vInfo);
        JavaVersionInfo returnedInfo = FrameInstaller.getJavaVersionInfo();

        assertNotNull(returnedInfo);
        assertEquals(version, returnedInfo.getVersion());
        assertEquals(vendor, returnedInfo.getVendor());
        assertEquals(specificationVersion, returnedInfo.getSpecificationVersion());
    }

    public void testInstallationType() {
        InstallationType installationType = new InstallationType();
        installationType.addLibraryModules();
        installationType.removeDemosAndExamples();
        installationType.removeDocumentation();
        installationType.addSources();

        FrameInstaller.setInstallationType(installationType);
        InstallationType returnedType = FrameInstaller.getInstallationType();

        assertNotNull(returnedType);
        assertTrue(returnedType.installLibraryModules());
        assertFalse(returnedType.installDemosAndExamples());
        assertFalse(returnedType.installDocumentation());
        assertTrue(returnedType.installSources());
    }

    public void testStandalone() {
        InstallationType installationType = new InstallationType();
        installationType.setStandalone();
        assertTrue(installationType.installLibraryModules());
        assertFalse(installationType.installDemosAndExamples());
        assertFalse(installationType.installDocumentation());
        assertFalse(installationType.installSources());

        FrameInstaller.setInstallationType(installationType);
        InstallationType returnedType = FrameInstaller.getInstallationType();

        assertNotNull(returnedType);
        assertTrue(returnedType.isStandalone());
        assertTrue(returnedType.installLibraryModules());
        assertFalse(returnedType.installDemosAndExamples());
        assertFalse(returnedType.installDocumentation());
        assertFalse(returnedType.installSources());
    }
}
