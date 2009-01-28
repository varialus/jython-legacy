// Copyright (c) Corporation for National Research Initiatives
// Copyright 2000 Samuele Pedroni

package org.python.core.packagecache;

import java.util.List;

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


    /**
     * Reports the specified package content names. Should be overriden. Used by
     * {@link PyJavaPackage#__dir__} and {@link PyJavaPackage#fillDir}.
     *
     * @return resulting list of names (List of PyString)
     * @param jpkg queried package
     * @param instantiate if true then instatiate reported names in package dict
     * @param exclpkgs exclude packages (just when instantiate is false)
     */
    List doDir(JavaPackage jpkg, boolean instantiate, boolean exclpkgs);

    Object lookupName(String name);

    /**
     * Creates package/updates statically known classes info. Uses
     * {@link PyJavaPackage#addPackage(java.lang.String, java.lang.String) },
     * {@link PyJavaPackage#addPlaceholders}.
     *
     * @param name package name
     * @param classes comma-separated string
     * @param jarfile involved jarfile; can be null
     * @return created/updated package
     */
    Object makeJavaPackage(String name, String classes, String jarfile);
}
