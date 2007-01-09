package org.python.util.install.driver;

import java.io.File;

import junit.framework.TestCase;

public class StandaloneVerifierTest extends TestCase {

    private StandaloneVerifier _verifier;

    protected void setUp() throws Exception {
        super.setUp();
        _verifier = new StandaloneVerifier();
        File targetDir = null;
        // have to install jython first in order to activate this test
        // targetDir = new File("C:/Temp/jython.autoinstall.root_54159_dir/006 consoleTest_54165_dir");
        _verifier.setTargetDir(targetDir);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        if (_verifier.getTargetDir() != null) {
            File autotestFile = new File(_verifier.getTargetDir().getCanonicalPath(), StandaloneVerifier.AUTOTEST_PY);
            if (autotestFile.exists()) {
                assertTrue(autotestFile.delete());
            }
        }
    }

    public void testVerify() throws Exception {
        if (_verifier.getTargetDir() != null) {
            _verifier.verify();
        }
    }

}
