package org.python.util.install;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Working horse extracting the contents of the installation .jar to the file system. <br>
 * The directory stucture is preserved, but there is the possibility to exclude some entries (directories at the
 * moment).
 */
public class JarInstaller {
    private static final String PATH_SEPARATOR = "/";
    private static final int BUFFER_SIZE = 1024;

    private ProgressListener _progressListener;
    private JarInfo _jarInfo;

    public JarInstaller(ProgressListener progressListener, JarInfo jarInfo) {
        _progressListener = progressListener;
        _jarInfo = jarInfo;
    }

    /**
     * Do the pysical installation:
     * <ul>
     * <li>unzip the files
     * <li>generate the start scripts
     * </ul>
     * 
     * @param targetDirectory
     * @param installationType
     */
    public void inflate(final File targetDirectory, String installationType, File javaHome) {
        try {
            List excludeDirs = _jarInfo.getExcludeDirs();
            if (!Installation.ALL.equals(installationType)) {
                // has to correspond with build.xml
                excludeDirs.add("src");
            }
            if (Installation.MINIMUM.equals(installationType)) {
                // has to correspond with build.Lib.include.properties
                excludeDirs.add("Demo");
                excludeDirs.add("Lib" + PATH_SEPARATOR + "email");
                excludeDirs.add("Lib" + PATH_SEPARATOR + "encodings");
                excludeDirs.add("Lib" + PATH_SEPARATOR + "test");
            }

            int count = 0;
            int percent = 0;
            int numberOfIntervals = 100 / _progressListener.getInterval();
            int numberOfEntries = approximateNumberOfEntries(installationType);
            int threshold = numberOfEntries / numberOfIntervals + 1; // +1 = pessimistic

            // unzip
            ZipInputStream zipInput = new ZipInputStream(new BufferedInputStream(new FileInputStream(_jarInfo
                    .getJarFile()), BUFFER_SIZE));
            ZipEntry zipEntry = zipInput.getNextEntry();
            while (zipEntry != null) {
                String zipEntryName = zipEntry.getName();
                boolean exclude = false;
                Iterator excludeDirsAsIterator = excludeDirs.iterator();
                while (excludeDirsAsIterator.hasNext()) {
                    if (zipEntryName.startsWith((String) excludeDirsAsIterator.next() + PATH_SEPARATOR))
                        exclude = true;
                }
                if (!exclude) {
                    count++;
                    if (count % threshold == 0) {
                        percent = percent + _progressListener.getInterval();
                        _progressListener.progressChanged(percent);
                    }
                    createDirectories(targetDirectory, zipEntryName);
                    if (!zipEntry.isDirectory()) {
                        File file = createFile(targetDirectory, zipEntryName);
                        _progressListener.progressEntry(file.getAbsolutePath());
                        FileOutputStream output = new FileOutputStream(file);
                        int uncompressedSize = getUncompressedSize(zipEntry);
                        if (uncompressedSize > 0) {
                            int totalRead = 0;
                            while (totalRead < uncompressedSize) {
                                int toRead = uncompressedSize - totalRead;
                                if (toRead > BUFFER_SIZE)
                                    toRead = BUFFER_SIZE;
                                byte[] bytes = new byte[toRead];
                                int read = zipInput.read(bytes, 0, toRead);
                                output.write(bytes, 0, read);
                                totalRead = totalRead + read;
                            }
                        }
                        output.close();
                        file.setLastModified(zipEntry.getTime());
                    }
                }
                zipInput.closeEntry();
                zipEntry = zipInput.getNextEntry();
            }
            // generate start scripts
            _progressListener.progressStartScripts();
            StartScriptGenerator generator = new StartScriptGenerator(targetDirectory, javaHome);
            if (Installation.isWindows()) {
                generator.generateJythonForWindows();
                generator.generateJythoncForWindows();
            } else {
                // everything else defaults to unix at the moment
                generator.generateJythonForUnix();
                generator.generateJythoncForUnix();
            }
            // end
            _progressListener.progressFinished();
        } catch (IOException ioe) {
            throw new InstallerException(Installation.getText(TextKeys.ERROR_ACCESS_JARFILE), ioe);
        }
    }

    private int approximateNumberOfEntries(String installationType) throws IOException {
        int numberOfEntries = _jarInfo.getNumberOfEntries();
        if (Installation.MINIMUM.equals(installationType)) {
            numberOfEntries = numberOfEntries / 3;
        } else if (Installation.STANDARD.equals(installationType)) {
            numberOfEntries = (numberOfEntries * 3) / 4;
        }
        return numberOfEntries;
    }

    private void createDirectories(final File targetDirectory, final String zipEntryName) {
        int lastSepIndex = zipEntryName.lastIndexOf(PATH_SEPARATOR);
        if (lastSepIndex > 0) {
            File directory = new File(targetDirectory, zipEntryName.substring(0, lastSepIndex));
            if (directory.exists() && directory.isDirectory()) {
            } else {
                if (!directory.mkdirs()) {
                    throw new InstallerException(Installation.getText(TextKeys.UNABLE_CREATE_DIRECTORY, directory
                            .getAbsolutePath()));
                }
            }
        }
    }

    private File createFile(final File targetDirectory, final String zipEntryName) throws IOException {
        File file = new File(targetDirectory, zipEntryName);
        if (file.exists() && file.isFile()) {
        } else {
            if (!file.createNewFile()) {
                throw new InstallerException(Installation.getText(TextKeys.UNABLE_CREATE_FILE, file.getCanonicalPath()));
            }
        }
        return file;
    }

    private int getUncompressedSize(final ZipEntry zipEntry) {
        long size = zipEntry.getSize();
        if (size > Integer.MAX_VALUE) {
            throw new InstallerException(Installation.getText(TextKeys.ZIP_ENTRY_TOO_BIG, zipEntry.toString()));
        }
        if (size < 0) {
            throw new InstallerException(Installation.getText(TextKeys.ZIP_ENTRY_SIZE, zipEntry.toString()));
        }
        return (int) size;
    }

}