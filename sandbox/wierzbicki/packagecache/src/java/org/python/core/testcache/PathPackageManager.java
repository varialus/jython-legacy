// Copyright (c) Corporation for National Research Initiatives
// Copyright 2000 Samuele Pedroni

package org.python.core.testcache;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Path package manager. Gathering classes info dynamically from a set of
 * directories in path {@link #searchPath}, and statically from a set of jars,
 * like {@link CachedJarsPackageManager}.
 */
public abstract class PathPackageManager extends CachedJarsPackageManager {

    public List searchPath;

    public PathPackageManager() {
        this.searchPath = new ArrayList();
    }

    /**
     * XXX: cut and pasted from imp.java for now. 
     * If <code>directoryName</code> is empty, return a correct directory name for a path.
     * If  <code>directoryName</code> is not an empty string, this method returns <code>directoryName</code> unchanged.
     */
    private static String defaultEmptyPathDirectory(String directoryName) {
        // The empty string translates into the current working
        // directory, which is usually provided on the system property
        // "user.dir". Don't rely on File's constructor to provide
        // this correctly.
        if (directoryName.length() == 0) {
            directoryName = System.getProperty("user.dir");
        }
        return directoryName;
    }
 
    /**
     * Helper for {@link #packageExists(java.lang.String,java.lang.String)}.
     * Scans for package pkg.name the directories in path.
     */
    protected boolean packageExists(List path, String pkg, String name) {
        String child = pkg.replace('.', File.separatorChar) + File.separator
                + name;

        //for (String dir : path) {
        Iterator pathIter = path.iterator();
        for (String dir = null; pathIter.hasNext(); dir = (String)pathIter.next()) {
            String directory = defaultEmptyPathDirectory(dir);

            File f = new File(directory, child);
            if (f.isDirectory()) { //XXX /*commented out for now*/ && imp.caseok(f, name, name.length())) {
                /*
                 * Figure out if we have a directory a mixture of python and
                 * java or just an empty directory (which means Java) or a
                 * directory with only Python source (which means Python).
                 */
                PackageExistsFileFilter m = new PackageExistsFileFilter();
                f.listFiles(m);
                boolean exists = m.packageExists();
                if (exists) {
                    //XXX
                    /*
                    Py.writeComment("import", "java package as '"
                            + f.getAbsolutePath() + "'");
                    */
                }
                return exists;
            }
        }
        return false;
    }

    class PackageExistsFileFilter implements FilenameFilter {
        private boolean java;

        private boolean python;

        public boolean accept(File dir, String name) {
            if (name.endsWith(".py") || name.endsWith("$py.class")) {
                this.python = true;
            } else if (name.endsWith(".class")) {
                this.java = true;
            }
            return false;
        }

        public boolean packageExists() {
            if (this.python && !this.java) {
                return false;
            }
            return true;
        }
    }

    /**
     * Helper for {@link #doDir(JavaPackage,boolean,boolean)}. Scans for
     * package jpkg content over the directories in path. Add to ret the founded
     * classes/pkgs. Filter out classes using {@link #filterByName},{@link #filterByAccess}.
     */
    protected void doDir(List path, List ret, JavaPackage jpkg,
            boolean instantiate, boolean exclpkgs) {
        String child = jpkg.getName().replace('.', File.separatorChar);

        //for (String dir : path) {
        Iterator pathIter = path.iterator();
        for (String dir = null; pathIter.hasNext(); dir = (String)pathIter.next()) {
            if (dir.length() == 0) {
                dir = null;
            }

            File childFile = new File(dir, child);

            String[] list = childFile.list();
            if (list == null) {
                continue;
            }

            doList: for (int j = 0; j < list.length; j++) {
                String jname = list[j];

                File cand = new File(childFile, jname);

                int jlen = jname.length();

                boolean pkgCand = false;

                if (cand.isDirectory()) {
                    if (!instantiate && exclpkgs) {
                        continue;
                    }
                    pkgCand = true;
                } else {
                    if (!jname.endsWith(".class")) {
                        continue;
                    }
                    jlen -= 6;
                }

                jname = jname.substring(0, jlen);

                /*
                PyString name = new PyString(jname);
                */

                if (filterByName(jname, pkgCand)) {
                    continue;
                }

                // for opt maybe we should some hash-set for ret
                if (jpkg.getMembers().containsKey(jname) || jpkg.getClasses().containsKey(jname)
                        || ret.contains(jname)) {
                    continue;
                }

                if (!Character.isJavaIdentifierStart(jname.charAt(0))) {
                    continue;
                }

                for (int k = 1; k < jlen; k++) {
                    if (!Character.isJavaIdentifierPart(jname.charAt(k))) {
                        continue doList;
                    }
                }

                if (!pkgCand) {
                    try {
                        System.err.println("XXX: " + jpkg.getName());
                        int acc = checkAccess(new BufferedInputStream(
                                new FileInputStream(cand)));
                        if ((acc == -1) || filterByAccess(jname, acc)) {
                            continue;
                        }
                    } catch (IOException e) {
                        continue;
                    }
                }

                if (instantiate) {
                    if (pkgCand) {
                        jpkg.addPackage(jname);
                    } else {
                        jpkg.addLazyClass(jname);
                    }
                }

                ret.add(jname);

            }
        }

    }

    /**
     * Add directory dir (if exists) to {@link #searchPath}.
     */
    public void addDirectory(File dir) {
        try {
            if (dir.getPath().length() == 0) {
                this.searchPath.add(/*XXX was: Py.EmptyString*/ "");
            } else {
                this.searchPath.add(dir.getCanonicalPath());
            }
        } catch (IOException e) {
            warning("skipping bad directory, '" + dir + "'");
        }
    }

    // ??pending:
    // Uses simply split and not a StringTokenizer+trim to adhere to
    // sun jvm parsing of classpath.
    // E.g. "a;" is parsed by sun jvm as a, ""; the latter is interpreted
    // as cwd. jview trims and cwd is per default in classpath.
    // The logic here should work for both(...). Need to distinguish?
    // This code does not avoid duplicates in searchPath.
    // Should cause no problem (?).

    /**
     * Adds "classpath" entry. Calls {@link #addDirectory} if path refers to a
     * dir, {@link #addJarToPackages(java.io.File, boolean)} with param cache
     * true if path refers to a jar.
     */
    public void addClassPath(String path) {
        String[] paths = path.split(java.io.File.pathSeparator);

        for (int i = 0; i < paths.length; i++) {
            String entry = paths[i];
            if (entry.endsWith(".jar") || entry.endsWith(".zip")) {
                addJarToPackages(new File(entry), true);
            } else {
                File dir = new File(entry);
                if (entry.length() == 0 || dir.isDirectory()) {
                    addDirectory(dir);
                }
            }
        }
    }

    public List doDir(JavaPackage jpkg, boolean instantiate,
            boolean exclpkgs) {
        Set basic = basicDoDir(jpkg, instantiate, exclpkgs);
        List ret = new ArrayList();

        doDir(this.searchPath, ret, jpkg, instantiate, exclpkgs);

        //XXX: probably don't need a new list here.
        List merge = new ArrayList();
        merge.addAll(basic);
        merge.addAll(ret);

        return merge;
    }

    public boolean packageExists(String pkg, String name) {
        return packageExists(this.searchPath, pkg, name);
    }

}
