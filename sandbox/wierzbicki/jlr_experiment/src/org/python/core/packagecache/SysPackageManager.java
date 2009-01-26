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
public class SysPackageManager extends BasePackageManager implements PyPackageManager{

    public SysPackageManager(File cachedir, Properties registry) {
        /* from PathPackageManager constructor */
        this.searchPath = new PyList();

        /* from old PackageManager constructor */
        this.topLevelPackage = new PyJavaPackage("", this);

        if (useCacheDir(cachedir)) {
            initCache();
            findAllPackages(registry);
            saveCache();
        }
    }

    public PyList doDir(PyJavaPackage jpkg, boolean instantiate,
            boolean exclpkgs) {
        PyList basic = basicDoDir(jpkg, instantiate, exclpkgs);
        PyList ret = new PyList();

        doDir(this.getSearchPath(), ret, jpkg, instantiate, exclpkgs);

        PySystemState system = Py.getSystemState();

        if (system.getClassLoader() == null) {
            doDir(system.path, ret, jpkg, instantiate, exclpkgs);
        }

        return merge(basic, ret);
    }

    /* From old PackageManager */

    /**
     * Basic helper implementation of {@link #doDir}. It merges information
     * from jpkg {@link PyJavaPackage#clsSet} and {@link PyJavaPackage#__dict__}.
     */
    protected PyList basicDoDir(PyJavaPackage jpkg, boolean instantiate,
            boolean exclpkgs) {
        PyStringMap dict = jpkg.__dict__;
        PyStringMap cls = jpkg.clsSet;

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
                jpkg.addClass(name, Py.findClass(jpkg.__name__ + "." + name));
            }
        }

        return dict.keys();
    }

    /**
     * Helper merging list2 into list1. Returns list1.
     */
    protected PyList merge(PyList list1, PyList list2) {
        for (int i = 0; i < list2.__len__(); i++) {
            PyObject name = list2.pyget(i);
            list1.append(name);
        }

        return list1;
    }


    public PyObject lookupName(String name) {
        PyObject top = this.getTopLevelPackage();
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
    public Object makeJavaPackage(String name, String classes,
            String jarfile) {
        PyJavaPackage p = this.getTopLevelPackage();
        if (name.length() != 0)
            p = p.addPackage(name, jarfile);

        if (classes != null)
            p.addPlaceholders(classes);

        return p;
    }

}
