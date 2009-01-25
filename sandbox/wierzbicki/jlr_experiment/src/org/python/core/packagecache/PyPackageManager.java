// Copyright (c) Corporation for National Research Initiatives
// Copyright 2000 Samuele Pedroni

package org.python.core.packagecache;

import org.python.core.PyJavaPackage;
import org.python.core.PyList;
import org.python.core.PyObject;

public interface PyPackageManager extends PackageManager {

    /**
     * Reports the specified package content names. Should be overriden. Used by
     * {@link PyJavaPackage#__dir__} and {@link PyJavaPackage#fillDir}.
     *
     * @return resulting list of names (PyList of PyString)
     * @param jpkg queried package
     * @param instantiate if true then instatiate reported names in package dict
     * @param exclpkgs exclude packages (just when instantiate is false)
     */
    PyList doDir(PyJavaPackage jpkg, boolean instantiate, boolean exclpkgs);

    //XXX
    PyObject lookupName(String name);

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
    PyJavaPackage makeJavaPackage(String name, String classes, String jarfile);
}
