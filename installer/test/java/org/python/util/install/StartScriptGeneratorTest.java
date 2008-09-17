package org.python.util.install;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;
import junit.framework.TestCase;

public class StartScriptGeneratorTest extends TestCase {

    private static final String AT_DATE = "@DATE";

    private static final String WIN_CR_LF = StartScriptGenerator.WIN_CR_LF;

    private StartScriptGenerator _generator;

    private File _targetDir;

    protected void setUp() throws Exception {
        String userDirName = System.getProperty("user.dir"); // only true in eclipse ?
        File userDir = new File(userDirName);
        File parentDir = userDir.getParentFile();
        assertTrue(parentDir.exists());
        _targetDir = new File(parentDir, "jython");
        if (!_targetDir.exists()) {
            _targetDir = new File(parentDir, "jython-trunk");
        }
        assertTrue(_targetDir.exists());
        assertTrue(_targetDir.isDirectory());
        _targetDir = new File(_targetDir, "src");
        _targetDir = new File(_targetDir, "shell");
        assertTrue(_targetDir.exists());
        assertTrue(_targetDir.isDirectory());
        File javaHome = new File(System.getProperty("java.home"));
        _generator = new StartScriptGenerator(_targetDir, javaHome);
    }

    // TODO: test on Solaris
    public void testUnix() throws IOException {
        _generator.setFlavour(StartScriptGenerator.UNIX_FLAVOUR);
        StringBuffer buf = new StringBuffer(100);
        buf.append("#!/usr/bin/env bash\n");
        buf.append("\n");
        buf.append("# This file was generated by the Jython installer\n");
        buf.append("# Created on " + AT_DATE + " by " + System.getProperty("user.name") + "\n");
        buf.append("\n");
        buf.append("JAVA_HOME=\"");
        buf.append(System.getProperty("java.home"));
        buf.append("\"\n");
        buf.append("JYTHON_HOME=\"");
        buf.append(_targetDir.getAbsolutePath());
        buf.append("\"\n");
        // some rudimentary tests - feel free to do more
        String start = buf.toString().replaceAll(AT_DATE, new Date().toString());
        String unixScript = _generator.getJythonScript(StartScriptGenerator.UNIX_FLAVOUR);
        assertTrue(unixScript.startsWith(start));
        assertTrue(unixScript.length() > 3500);
        assertTrue(unixScript.indexOf("-Dpython.home=") > start.length());
        assertTrue(unixScript.indexOf("-Dpython.executable=") > start.length());
    }

    public void testWindows() throws IOException {
        StringBuffer winBuf = new StringBuffer(100);
        winBuf.append("@echo off" + WIN_CR_LF);
        winBuf.append("rem This file was generated by the Jython installer" + WIN_CR_LF);
        winBuf.append("rem Created on " + AT_DATE + " by " + System.getProperty("user.name") + ""
                + WIN_CR_LF);
        winBuf.append(WIN_CR_LF);
        winBuf.append("set JAVA_HOME=\"");
        winBuf.append(System.getProperty("java.home"));
        winBuf.append("\"");
        winBuf.append(WIN_CR_LF);
        winBuf.append("set JYTHON_HOME=\"");
        winBuf.append(_targetDir.getAbsolutePath());
        winBuf.append("\"");
        winBuf.append(WIN_CR_LF);
        // some rudimentary tests - feel free to do more
        String start = winBuf.toString().replaceAll(AT_DATE, new Date().toString());
        String winScript = _generator.getJythonScript(StartScriptGenerator.WINDOWS_FLAVOUR);
        assertTrue(winScript.startsWith(start));
        assertTrue(winScript.length() > 3500);
        assertTrue(winScript.indexOf("if not [%JAVA_HOME%] == []") > start.length());
        assertTrue(winScript.indexOf("-Dpython.home=") > start.length());
        assertTrue(winScript.indexOf("-Dpython.executable=") > start.length());
    }

    public void testFlavour() {
        int expectedFlavour;
        expectedFlavour = StartScriptGenerator.UNIX_FLAVOUR;
        _generator.setFlavour(expectedFlavour);
        assertEquals(expectedFlavour, _generator.getFlavour());
        expectedFlavour = StartScriptGenerator.BOTH_FLAVOUR;
        _generator.setFlavour(expectedFlavour);
        assertEquals(expectedFlavour, _generator.getFlavour());
        TestStartScriptGenerator testGenerator = new TestStartScriptGenerator(new File("dummy"),
                                                                              new File("dummy"),
                                                                              false);
        expectedFlavour = StartScriptGenerator.WINDOWS_FLAVOUR;
        testGenerator.setFlavour(expectedFlavour);
        assertEquals(expectedFlavour, testGenerator.getFlavour());
        expectedFlavour = StartScriptGenerator.UNIX_FLAVOUR;
        testGenerator.setFlavour(expectedFlavour);
        assertEquals(expectedFlavour, testGenerator.getFlavour());
        testGenerator = new TestStartScriptGenerator(new File("dummy"), new File("dummy"), true);
        testGenerator.setFlavour(StartScriptGenerator.WINDOWS_FLAVOUR);
        assertEquals(StartScriptGenerator.BOTH_FLAVOUR, testGenerator.getFlavour());
    }

    public void testGenerateBothFlavours() throws IOException {
        File dir = new File(System.getProperty("java.io.tmpdir"), "StartScriptGeneratorTest");
        try {
            if (!dir.exists()) {
                assertTrue(dir.mkdirs());
            }
            File bin = new File(dir, "bin");
            if (!bin.exists()) {
                assertTrue(bin.mkdirs());
            }
            File jython = new File(bin, "jython");
            if (!jython.exists()) {
                assertTrue(jython.createNewFile());
            }
            File jython_bat = new File(bin, "jython.bat");
            if (!jython_bat.exists()) {
                assertTrue(jython_bat.createNewFile());
            }
            File javaHome = new File(System.getProperty("java.home"));
            TestStartScriptGenerator testGenerator = new TestStartScriptGenerator(dir,
                                                                                  javaHome,
                                                                                  true);
            // test generator constructor timing problem: do set the flavour once again
            testGenerator.setFlavour(StartScriptGenerator.WINDOWS_FLAVOUR);
            testGenerator.generateStartScripts();
            String[] fileNames = dir.list();
            assertEquals(3, fileNames.length); // 2 files plus the /bin subdirectory
            Set<String> fileNamesSet = new HashSet<String>(4);
            for (int i = 0; i < fileNames.length; i++) {
                fileNamesSet.add(fileNames[i]);
            }
            assertTrue(fileNamesSet.contains("bin"));
            assertTrue(fileNamesSet.contains("jython"));
            assertTrue(fileNamesSet.contains("jython.bat"));
        } finally {
            if (dir.exists()) {
                rmdir(dir);
            }
        }
    }

    private void rmdir(File dir) {
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                Assert.assertTrue(files[i].delete());
            } else {
                if (files[i].isDirectory()) {
                    rmdir(files[i]);
                }
            }
        }
        assertTrue(dir.delete());
    }

    class TestStartScriptGenerator extends StartScriptGenerator {

        private boolean _hasBothFlavours;

        public TestStartScriptGenerator(File targetDirectory, File javaHome, boolean hasBothFlavours) {
            super(targetDirectory, javaHome);
            _hasBothFlavours = hasBothFlavours;
        }

        protected boolean hasUnixlikeShell() {
            return _hasBothFlavours;
        }
    }
}
