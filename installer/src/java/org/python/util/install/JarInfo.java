package org.python.util.install;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class JarInfo {
    private static final String JAR_URL_PREFIX = "jar:file:";
    private static final String JAR_SEPARATOR = "!";
    private static final String JYTHON = "Jython";
    private static final String VERSION_ATTRIBUTE = "version";
    private static final String EXCLUDE_DIRS_ATTRIBUTE = "exclude-dirs";
    private static final String EXCLUDE_DIRS_DELIM = ";";
    private static final String URL_BLANK_REPLACEMENT = "%20";

    private File _jarFile;
    private int _numberOfEntries;
    private Manifest _manifest;
    private String _licenseText;

    public JarInfo() {
        _jarFile = null;
        _numberOfEntries = 0;
        _manifest = null;

        try {
            readJarInfo();
        } catch (IOException ioe) {
            throw new InstallerException(Installation.getText(TextKeys.ERROR_ACCESS_JARFILE), ioe);
        }
    }

    public String getVersion() {
        String version = "<unknown>";
        try {
            Attributes jythonAttributes = getManifest().getAttributes(JYTHON);
            if (jythonAttributes != null) {
                version = jythonAttributes.getValue(VERSION_ATTRIBUTE); // do
                // not
                // use
                // containsKey
            }
        } catch (IOException ioe) {
        }
        return version;
    }

    public File getJarFile() throws IOException {
        if (_jarFile == null)
            readJarInfo();
        return _jarFile;
    }

    public Manifest getManifest() throws IOException {
        if (_manifest == null)
            readJarInfo();
        return _manifest;
    }

    public int getNumberOfEntries() throws IOException {
        if (_numberOfEntries == 0)
            readJarInfo();
        return _numberOfEntries;
    }

    public List getExcludeDirs() throws IOException {
        List excludeDirs = new ArrayList();
        Attributes jythonAttributes = getManifest().getAttributes(JYTHON);
        if (jythonAttributes != null) {
            // do not use containsKey
            String excludeDirsString = jythonAttributes.getValue(EXCLUDE_DIRS_ATTRIBUTE);
            if (excludeDirsString != null && excludeDirsString.length() > 0) {
                StringTokenizer tokenizer = new StringTokenizer(excludeDirsString, EXCLUDE_DIRS_DELIM);
                while (tokenizer.hasMoreTokens()) {
                    excludeDirs.add(tokenizer.nextToken());
                }
            }
        }
        return excludeDirs;
    }

    public String getLicenseText() throws IOException {
        if (_licenseText == null) {
            readJarInfo();
        }
        return _licenseText;
    }

    private void readJarInfo() throws IOException {
        String fullClassName = getClass().getName();
        String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
        URL url = getClass().getResource(className + ".class");
        // we expect an URL like:
        //   jar:file:/C:/stuff/jython21i.jar!/org/python/util/install/JarInfo.class
        String urlString = url.toString();
        int jarSeparatorIndex = urlString.indexOf(JAR_SEPARATOR);
        if (!urlString.startsWith(JAR_URL_PREFIX) || jarSeparatorIndex <= 0) {
            throw new InstallerException(Installation.getText(TextKeys.UNEXPECTED_URL, urlString));
        }
        String jarFileName = urlString.substring(JAR_URL_PREFIX.length(), jarSeparatorIndex);
        // handle directories containing blanks
        if (jarFileName.indexOf(URL_BLANK_REPLACEMENT) >= 0) {
            jarFileName = jarFileName.replaceAll(URL_BLANK_REPLACEMENT, " ");
        }
        _jarFile = new File(jarFileName);
        if (!_jarFile.exists()) {
            throw new InstallerException(Installation.getText(TextKeys.JAR_NOT_FOUND, _jarFile.getAbsolutePath()));
        }
        JarFile jarFile = new JarFile(_jarFile);
        Enumeration entries = jarFile.entries();
        _numberOfEntries = 0;
        while (entries.hasMoreElements()) {
            JarEntry entry = (JarEntry) entries.nextElement();
            if ("LICENSE.txt".equals(entry.getName())) {
                _licenseText = readLicense(entry, jarFile);
            }
            _numberOfEntries++;
        }
        _manifest = jarFile.getManifest();
        if (_manifest == null) {
            throw new InstallerException(Installation.getText(TextKeys.NO_MANIFEST, _jarFile.getAbsolutePath()));
        }
        jarFile.close();
    }

    private String readLicense(JarEntry entry, JarFile jarFile) throws IOException {
        StringBuffer buffer = new StringBuffer(10000);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(jarFile.getInputStream(entry)));
            for (String s; (s = reader.readLine()) != null;) {
                buffer.append(s);
                buffer.append("\n");
            }
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return buffer.toString();
    }
}