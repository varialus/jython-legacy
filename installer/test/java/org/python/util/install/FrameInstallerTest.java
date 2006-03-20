package org.python.util.install;

import org.python.util.install.Installation.JavaVersionInfo;

import junit.framework.TestCase;

public class FrameInstallerTest extends TestCase {

    public void testSetJavaVersionInfo() {
        String version = "1;2;3";
        String vendor = "jython [macrosystems]";
        String specificationVersion = "@spec 1,4";

        JavaVersionInfo vInfo = new JavaVersionInfo();
        vInfo.setVersion(version);
        vInfo.setVendor(vendor);
        vInfo.setSpecificationVersion(specificationVersion);

        FrameInstaller.setJavaVersionInfo(vInfo);
        JavaVersionInfo returnedInfo = FrameInstaller.getJavaVersionInfo();

        assertEquals(version, returnedInfo.getVersion());
        assertEquals(vendor, returnedInfo.getVendor());
        assertEquals(specificationVersion, returnedInfo.getSpecificationVersion());
    }

}
