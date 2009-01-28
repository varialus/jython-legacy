// Released to the public domain by Frank Wierzbicki Jan 3, 2009
package org.python.core.packagecache;

import java.util.Map;

public interface JavaPackage {
    public String getName();

    /* XXX: should return List */
    public Object dir();

    /* XXX: should return Set or Map */
    public Object getClasses();

    /* XXX: should return Map */
    public Object getMembers();

    public Object addClass(String string, Class<?> clazz);
    public JavaPackage addPackage(String name);
    public JavaPackage addPackage(String name, String jarfile);
    public void addPlaceholders(String classes);
    void setPackageManager(PackageManager mgr);
}
