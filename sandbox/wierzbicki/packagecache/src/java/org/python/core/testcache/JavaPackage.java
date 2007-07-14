package org.python.core.testcache;

import java.util.HashMap;
import java.util.Map;

public interface JavaPackage {
    public String getName();
    public Map getMembers();
    public Map getClasses();
    public void addLazyClass(String string);
    public JavaPackage addPackage(String name);
    public JavaPackage addPackage(String name, String packageName);
    public void addPlaceholders(String classes);
}
