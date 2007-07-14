package org.python.core.testcache;

import java.util.HashMap;
import java.util.Map;

public class JavaPackageImpl implements JavaPackage {

    private String name;

    /*
    public JavaPackageImpl(String name) {
    }
    public JavaPackageImpl(String name, String jarfile) {
    }
    */

    public JavaPackageImpl(String name, PackageManager packageManager) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Map getMembers() {
        return new HashMap();
    }

    //XXX: is this getSupers?
    public Map getClasses() {
        return new HashMap();
    }

    public void addLazyClass(String string) {
    }

    public JavaPackage addPackage(String name) {
        return this;
    }

    public JavaPackage addPackage(String name, String packageName) {
        return this;
    }

    public void addPlaceholders(String classes) {
    }

}
