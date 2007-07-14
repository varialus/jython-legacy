// Copyright (c) Corporation for National Research Initiatives
// Copyright 2000 Samuele Pedroni

package org.python.core.testcache;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.io.*;

/**
 * System package manager. Used by org.python.core.PySystemState.
 */
public class SysPackageManager extends PathPackageManager {

    protected void message(String msg) {
        System.err.println("*sys-package-mgr* mes:" + msg);
        //Py.writeMessage("*sys-package-mgr*", msg);
    }

    protected void warning(String warn) {
        System.err.println("*sys-package-mgr* warn:" + warn);
        //Py.writeWarning("*sys-package-mgr*", warn);
    }

    protected void comment(String msg) {
        System.err.println("*sys-package-mgr* comment:" + msg);
        //Py.writeComment("*sys-package-mgr*", msg);
    }

    protected void debug(String msg) {
        System.err.println("*sys-package-mgr* debug:" + msg);
        //Py.writeDebug("*sys-package-mgr*", msg);
    }

    public SysPackageManager(File cachedir, List classpaths, List jarpaths) {
        if (useCacheDir(cachedir)) {
            initCache();
            //for (String classpath : classpaths) {
            for (Iterator i = classpaths.iterator(); i.hasNext();) {
                String classpath = (String)i.next();
                addClassPath(classpath);
            }
            //for (String jarpath : jarpaths) {
            for (Iterator i = jarpaths.iterator(); i.hasNext();) {
                String jarpath = (String)i.next();
                addJarPath(jarpath);
            }
            saveCache();
        }
    }

    public void addJar(String jarfile, boolean cache) {
        addJarToPackages(new File(jarfile), cache);
        if (cache) {
            saveCache();
        }
    }

    public void addJarDir(String jdir, boolean cache) {
        addJarDir(jdir, cache, cache);
    }

    private void addJarDir(String jdir, boolean cache, boolean saveCache) {
        File file = new File(jdir);
        if (!file.isDirectory()) {
            return;
        }
        String[] files = file.list();
        for (int i = 0; i < files.length; i++) {
            String entry = files[i];
            if (entry.endsWith(".jar") || entry.endsWith(".zip")) {
                addJarToPackages(new File(jdir, entry), cache);
            }
        }
        if (saveCache) {
            saveCache();
        }
    }

    private void addJarPath(String path) {
        StringTokenizer tok = new StringTokenizer(path,
                java.io.File.pathSeparator);
        while (tok.hasMoreTokens()) {
            // ??pending: do jvms trim? how is interpreted entry=""?
            String entry = tok.nextToken();
            addJarDir(entry, true, false);
        }
    }

    public void notifyPackageImport(String pkg, String name) {
        if (pkg != null && pkg.length() > 0) {
            name = pkg + '.' + name;
        }
        //Py.writeComment("import", "'" + name + "' as java package");
    }

    //XXX: this is a kludge just to get something going.
    public Class findClass(String pkg, String name, String reason) {
        return findClass(pkg, name);
    }

    public Class findClass(String pkg, String name) {
        Class c = super.findClass(pkg, name);
        if (c != null) {
            //Py.writeComment("import", "'" + name + "' as java class");
        }
        return c;
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
        if (packageExists(this.searchPath, pkg, name)) {
            return true;
        }
        return false;
    }

}
