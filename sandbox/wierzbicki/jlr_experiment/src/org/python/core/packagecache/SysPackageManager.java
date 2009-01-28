// Copyright (c) Corporation for National Research Initiatives
// Copyright 2000 Samuele Pedroni

package org.python.core.packagecache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.python.core.Options;
import org.python.core.Py;
import org.python.core.PyJavaPackage;
import org.python.core.PyList;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.core.PyStringMap;
import org.python.core.PySystemState;
import org.python.core.imp;
import org.python.core.util.RelativeFile;
import org.python.util.Generic;

/**
 * System package manager. Used by org.python.core.PySystemState.
 */
public class SysPackageManager extends BasePackageManager {

    public SysPackageManager(File cachedir, Properties registry) {
        super(new PyJavaPackage(""),
                cachedir,
                Options.respectJavaAccessibility,
                findClassPaths(registry),
                findJarPaths(registry));
    }

    private static List<String> findClassPaths(Properties registry) {
        List<String> classpaths = new ArrayList<String>();
        String paths = registry.getProperty("python.packages.paths",
                "java.class.path,sun.boot.class.path");
        String fakepath = registry
                .getProperty("python.packages.fakepath", null);
        StringTokenizer tok = new StringTokenizer(paths, ",");
        while (tok.hasMoreTokens()) {
            String entry = tok.nextToken().trim();
            String tmp = registry.getProperty(entry);
            if (tmp == null) {
                continue;
            }
            classpaths.add(tmp);
        }

        if (fakepath != null) {
            classpaths.add(fakepath);
        }
        return classpaths;
    }

    private static List<String> findJarPaths(Properties registry) {
        List<String> paths = new ArrayList<String>();
        String directories = registry.getProperty(
                "python.packages.directories", "java.ext.dirs");
        StringTokenizer tok = new StringTokenizer(directories, ",");
        while (tok.hasMoreTokens()) {
            String entry = tok.nextToken().trim();
            String tmp = registry.getProperty(entry);
            if (tmp == null) {
                continue;
            }
            paths.add(tmp);
        }
        return paths;
    }

    public Class findClass(String pkg, String name, String reason) {
        if (pkg != null && pkg.length() > 0) {
            name = pkg + '.' + name;
        }
        return Py.findClassEx(name, reason);
    }

    /**
     * Dynamically check if pkg.name exists as java pkg in the controlled
     * hierarchy. Should be overriden.
     *
     * @param pkg parent pkg name
     * @param name candidate name
     * @return true if pkg exists
     */
    public boolean packageExists(String pkg, String name) {
        if (packageExists(this.getSearchPath(), pkg, name)) {
            return true;
        }

        PySystemState system = Py.getSystemState();

        if (system.getClassLoader() == null
                && packageExists(Py.getSystemState().path, pkg, name)) {
            return true;
        }

        return false;
    }

    /**
     * Helper for {@link #packageExists(java.lang.String,java.lang.String)}.
     * Scans for package pkg.name the directories in path.
     */
    protected boolean packageExists(List path, String pkg, String name) {
        String child = pkg.replace('.', File.separatorChar) + File.separator
                + name;

        for (int i = 0; i < path.size(); i++) {
            String dir = path.get(i).toString();

            File f = new RelativeFile(dir, child);
            if (f.isDirectory() && imp.caseok(f, name)) {
                /*
                 * Figure out if we have a directory a mixture of python and
                 * java or just an empty directory (which means Java) or a
                 * directory with only Python source (which means Python).
                 */
                PackageExistsFileFilter m = new PackageExistsFileFilter();
                f.listFiles(m);
                boolean exists = m.packageExists();
                if (exists) {
                    Py.writeComment("import", "java package as '"
                            + f.getAbsolutePath() + "'");
                }
                return exists;
            }
        }
        return false;
    }

    /**
     * Helper for {@link #doDir(PyJavaPackage,boolean,boolean)}. Scans for
     * package jpkg content over the directories in path. Add to ret the founded
     * classes/pkgs. Filter out classes using {@link #filterByName},{@link #filterByAccess}.
     */
    protected void doDir(List path, List ret, JavaPackage jpkg,
            boolean instantiate, boolean exclpkgs) {
        String child = jpkg.getName().replace('.', File.separatorChar);

        for (int i = 0; i < path.size(); i++) {
            String dir = path.get(i).toString();
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
                PyString name = new PyString(jname);

                if (filterByName(jname, pkgCand)) {
                    continue;
                }

                // for opt maybe we should some hash-set for ret
                if (((PyStringMap)jpkg.getMembers()).has_key(name) || ((PyStringMap)jpkg.getClasses()).has_key(name)
                        || ret.contains(name)) {
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
                        jpkg.addClass(jname, Py.findClass(jpkg.getName() + "." + jname));
                    }
                }

                ret.add(name);

            }
        }
    }

    public List doDir(JavaPackage jpkg, boolean instantiate,
            boolean exclpkgs) {
        List basic = basicDoDir(jpkg, instantiate, exclpkgs);
        List ret = new PyList();

        doDir(this.getSearchPath(), ret, jpkg, instantiate, exclpkgs);

        PySystemState system = Py.getSystemState();

        if (system.getClassLoader() == null) {
            doDir(system.path, ret, jpkg, instantiate, exclpkgs);
        }

        basic.addAll(ret);
        return basic;
    }

    /**
     * Basic helper implementation of {@link #doDir}. It merges information
     * from jpkg {@link JavaPackage#clsSet} and {@link JavaPackage#__dict__}.
     */
    protected List basicDoDir(JavaPackage jpkg, boolean instantiate,
            boolean exclpkgs) {
        PyStringMap dict = (PyStringMap)jpkg.getMembers();
        PyStringMap cls = (PyStringMap)jpkg.getClasses();

        if (!instantiate) {
            PyList ret = cls.keys();

            PyList dictKeys = dict.keys();

            for (int i = 0; i < dictKeys.__len__(); i++) {
                PyObject name = dictKeys.pyget(i);
                if (!cls.has_key(name)) {
                    if (exclpkgs && dict.get(name) instanceof PyJavaPackage)
                        continue;
                    ret.append(name);
                }
            }

            return ret;
        }

        for (PyObject pyname : cls.keys().asIterable()) {
            if (!dict.has_key(pyname)) {
                String name = pyname.toString();
                jpkg.addClass(name, Py.findClass(jpkg.getName() + "." + name));
            }
        }

        return dict.keys();
    }

    public PyObject lookupName(String name) {
        PyObject top = (PyObject)this.getTopLevelPackage();
        do {
            int dot = name.indexOf('.');
            String firstName = name;
            String lastName = null;
            if (dot != -1) {
                firstName = name.substring(0, dot);
                lastName = name.substring(dot + 1, name.length());
            }
            firstName = firstName.intern();
            top = top.__findattr__(firstName);
            if (top == null)
                return null;
            // ??pending: test for jpkg/jclass?
            name = lastName;
        } while (name != null);
        return top;
    }

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
    public JavaPackage makeJavaPackage(String name, String classes,
            String jarfile) {
        JavaPackage p = this.getTopLevelPackage();
        if (name.length() != 0)
            p = p.addPackage(name, jarfile);

        if (classes != null)
            p.addPlaceholders(classes);

        return p;
    }

    protected void message(String msg) {
        Py.writeMessage("*sys-package-mgr*", msg);
    }

    protected void warning(String warn) {
        Py.writeWarning("*sys-package-mgr*", warn);
    }

    protected void comment(String msg) {
        Py.writeComment("*sys-package-mgr*", msg);
    }

    protected void debug(String msg) {
        Py.writeDebug("*sys-package-mgr*", msg);
    }

    class PackageExistsFileFilter implements FilenameFilter {
        private boolean java;

        private boolean python;

        public boolean accept(File dir, String name) {
            if(name.endsWith(".py") || name.endsWith("$py.class") || name.endsWith("$_PyInner.class")) {
                python = true;
            }else if (name.endsWith(".class")) {
                java = true;
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

}
