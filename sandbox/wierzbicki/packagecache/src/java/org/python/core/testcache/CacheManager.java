package org.python.core.testcache;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CacheManager
{
    public File cachedir;
    public PackageManager packageManager;

    public CacheManager(File cachedir, List classpaths, List jarpaths) {
        this.cachedir = cachedir;
        File pkgdir;
        if (cachedir != null) {
            pkgdir = new File(cachedir, "packages");
        } else {
            pkgdir = null;
        }
        packageManager = new SysPackageManager(pkgdir, classpaths, jarpaths);
    }

    public static void main(String[] args) {
        List jarpaths = new ArrayList();
        List classpaths = new ArrayList();
        CacheManager c = new CacheManager(new File("cachedir"), classpaths, jarpaths);
    }
}
