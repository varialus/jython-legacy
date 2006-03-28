package org.python.util.install;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
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
    private static final String LIB_NAME_SEP = "Lib" + PATH_SEPARATOR;
    private static final String LIB_PAWT_SEP = LIB_NAME_SEP + "pawt" + PATH_SEPARATOR;
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
    public void inflate(final File targetDirectory, InstallationType installationType, File javaHome) {
        try {
            // has to correspond with build.xml
            // has to correspond with build.Lib.include.properties
            List excludeDirs = _jarInfo.getExcludeDirs();
            List coreLibFiles = new ArrayList();
            if (!installationType.installSources()) {
                excludeDirs.add("src");
            }
            if (!installationType.installDocumentation()) {
                excludeDirs.add("Doc");
            }
            if (!installationType.installDemosAndExamples()) {
                excludeDirs.add("Demo");
            }
            if (!installationType.installLibraryModules()) {
                excludeDirs.add(LIB_NAME_SEP + "email");
                excludeDirs.add(LIB_NAME_SEP + "encodings");
                excludeDirs.add(LIB_NAME_SEP + "test");
                excludeDirs.add(LIB_NAME_SEP + "jxxload_help");
                coreLibFiles = getCoreLibFiles();
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
                // handle exclusion of directories
                Iterator excludeDirsAsIterator = excludeDirs.iterator();
                while (excludeDirsAsIterator.hasNext()) {
                    if (zipEntryName.startsWith((String) excludeDirsAsIterator.next() + PATH_SEPARATOR)) {
                        exclude = true;
                    }
                }
                // handle exclusion of core Lib files
                if (!exclude) {
                    exclude = shouldExcludeFile(installationType, coreLibFiles, zipEntry, zipEntryName);
                }
                if (exclude) {
                    if (Installation.isVerbose()) {
                        ConsoleInstaller.message("excluding " + zipEntryName);
                    }
                } else {
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

    private int approximateNumberOfEntries(InstallationType installationType) {
        int numberOfEntries = 65; // core (minimum)
        if (installationType.installLibraryModules()) {
            numberOfEntries += 664;
        }
        if (installationType.installDemosAndExamples()) {
            numberOfEntries += 44;
        }
        if (installationType.installDocumentation()) {
            numberOfEntries += 209;
        }
        if (installationType.installSources()) {
            numberOfEntries += 401;
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

    private List getCoreLibFiles() {
        List coreLibFiles = new ArrayList();
        coreLibFiles.add("__future__.py");
        coreLibFiles.add("copy.py");
        coreLibFiles.add("dbexts.py");
        coreLibFiles.add("imaplib.py");
        coreLibFiles.add("isql.py");
        coreLibFiles.add("javaos.py");
        coreLibFiles.add("javapath.py");
        coreLibFiles.add("jreload.py");
        coreLibFiles.add("marshal.py");
        coreLibFiles.add("random.py");
        coreLibFiles.add("re.py");
        coreLibFiles.add("site.py");
        coreLibFiles.add("socket.py");
        coreLibFiles.add("sre.py");
        coreLibFiles.add("sre_compile.py");
        coreLibFiles.add("sre_constants.py");
        coreLibFiles.add("sre_parse.py");
        coreLibFiles.add("string.py");
        coreLibFiles.add("zipfile.py");
        coreLibFiles.add("zlib.py");
        return coreLibFiles;
    }

    private boolean shouldExcludeFile(InstallationType installationType, List coreLibFiles, ZipEntry zipEntry,
            String zipEntryName) {
        boolean exclude = false;
        if (!installationType.installLibraryModules()) {
            // handle files in Lib
            if (!zipEntry.isDirectory() && zipEntryName.startsWith(LIB_NAME_SEP)) {
                // include all files in /pawt subdirectory
                if (!zipEntryName.startsWith(LIB_PAWT_SEP)) {
                    if (zipEntryName.endsWith(".py")) { // only compare *.py files
                        exclude = true;
                        Iterator coreLibFilesAsIterator = coreLibFiles.iterator();
                        while (coreLibFilesAsIterator.hasNext()) {
                            String coreFileName = (String) coreLibFilesAsIterator.next();
                            if (zipEntryName.endsWith(PATH_SEPARATOR + coreFileName)) {
                                exclude = false;
                            }
                        }
                    }
                }
            }
        }
        return exclude;
    }

}