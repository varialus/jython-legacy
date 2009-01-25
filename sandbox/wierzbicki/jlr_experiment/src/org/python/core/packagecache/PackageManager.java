package org.python.core.packagecache;

public interface PackageManager {

    Class findClass(String pkg, String name, String reason);

    Class findClass(String pkg, String name);

    void notifyPackageImport(String pkg, String name);

    boolean packageExists(String pkg, String name);

    /**
     * Append a directory to the list of directories searched for java packages
     * and java classes.
     *
     * @param dir A directory.
     */
    void addDirectory(java.io.File dir);

    /**
     * Append a directory to the list of directories searched for java packages
     * and java classes.
     *
     * @param dir A directory name.
     */
    void addJarDir(String dir, boolean cache);

    /**
     * Append a jar file to the list of locations searched for java packages and
     * java classes.
     *
     * @param jarfile A directory name.
     */
    void addJar(String jarfile, boolean cache);

}
