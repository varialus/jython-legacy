// Copyright (c) Corporation for National Research Initiatives
// Copyright 2000 Samuele Pedroni

package org.python.core.testcache;

import java.io.EOFException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Abstract package manager.
 */
public abstract class PackageManager extends Object {

    private JavaPackage topLevelPackage;
    protected boolean respectJavaAccessibility;

    public PackageManager() {
        this(true);
    }

    public PackageManager(boolean respectJavaAccessibility) {
        this.respectJavaAccessibility = respectJavaAccessibility;
        this.topLevelPackage = new JavaPackageImpl("", this);
    }

    abstract public Class findClass(String pkg, String name, String reason);

    public Class findClass(String pkg, String name) {
        return findClass(pkg, name, "java class");
    }

    public void notifyPackageImport(String pkg, String name) {
    }

    /**
     * Dynamically check if pkg.name exists as java pkg in the controlled
     * hierarchy. Should be overriden.
     * 
     * @param pkg parent pkg name
     * @param name candidate name
     * @return true if pkg exists
     */
    public abstract boolean packageExists(String pkg, String name);

    /**
     * Reports the specified package content names. Should be overriden. Used by
     * {@link JavaPackage#__dir__} and {@link JavaPackage#fillDir}.
     * 
     * @return resulting list of names (List)
     * @param jpkg queried package
     * @param instantiate if true then instatiate reported names in package dict
     * @param exclpkgs exclude packages (just when instantiate is false)
     */
    public abstract List doDir(JavaPackage jpkg, boolean instantiate,
            boolean exclpkgs);

    /**
     * Append a directory to the list of directories searched for java packages
     * and java classes.
     * 
     * @param dir A directory.
     */
    public abstract void addDirectory(java.io.File dir);

    /**
     * Append a directory to the list of directories searched for java packages
     * and java classes.
     * 
     * @param dir A directory name.
     */
    public abstract void addJarDir(String dir, boolean cache);

    /**
     * Append a jar file to the list of locations searched for java packages and
     * java classes.
     * 
     * @param jarfile A directory name.
     */
    public abstract void addJar(String jarfile, boolean cache);

    /**
     * Basic helper implementation of {@link #doDir}. It merges information
     * from jpkg {@link JavaPackage#clsSet} and {@link JavaPackage#members}.
     */
    protected Set basicDoDir(JavaPackage jpkg, boolean instantiate,
            boolean exclpkgs) {
        Map dict = jpkg.getMembers();
        Map cls = jpkg.getClasses();

        if (!instantiate) {
            Set ret = cls.keySet();

            Set dictKeys = dict.keySet();

            //for (String name : dictKeys) {
            Iterator keyIter = dictKeys.iterator();
            for (String name = null; keyIter.hasNext(); name = (String)keyIter.next()) {

                if (!cls.containsKey(name)) {
                    if (exclpkgs && dict.get(name) instanceof JavaPackage) {
                        continue;
                    }
                    ret.add(name);
                }
            }
            return ret;
        }

        Set clsNames = cls.keySet();

        //for (String name : clsNames) {
        Iterator nameIter = clsNames.iterator();
        for (String name = null; nameIter.hasNext(); name = (String)nameIter.next()) {
            if (!dict.containsKey(name))
                jpkg.addLazyClass(name.toString());
        }

        return dict.keySet();
    }

    public Object lookupName(String name) {
        Object top = this.topLevelPackage;
        do {
            int dot = name.indexOf('.');
            String firstName = name;
            String lastName = null;
            if (dot != -1) {
                firstName = name.substring(0, dot);
                lastName = name.substring(dot + 1, name.length());
            }
            firstName = firstName.intern();
            /*
            //XXX: I think this part is getting the next package... not sure yet.
            top = top.__findattr__(firstName);
            */
            if (top == null)
                return null;
            // ??pending: test for jpkg/jclass?
            name = lastName;
        } while (name != null);
        return top;
    }

    /**
     * Creates package/updates statically known classes info. Uses
     * {@link JavaPackage#addPackage(java.lang.String, java.lang.String) },
     * {@link JavaPackage#addPlaceholders}.
     * 
     * @param name package name
     * @param classes comma-separated string
     * @param jarfile involved jarfile; can be null
     * @return created/updated package
     */
    public JavaPackage makeJavaPackage(String name, String classes,
            String jarfile) {
        JavaPackage p = this.topLevelPackage;
        if (name.length() != 0)
            p = p.addPackage(name, jarfile);

        if (classes != null)
            p.addPlaceholders(classes);

        return p;
    }

    /**
     * Check that a given stream is a valid Java .class file. And return its
     * access permissions as an int.
     */
    static protected int checkAccess(java.io.InputStream cstream)
            throws java.io.IOException {
        java.io.DataInputStream istream = new java.io.DataInputStream(cstream);

        try {
            int magic = istream.readInt();
            if (magic != 0xcafebabe)
                return -1;
        } catch (EOFException e) {
            return -1;
        }
        
        //int minor = 
        istream.readShort();
        //int major =
        istream.readShort();
        
        // Check versions???
        // System.out.println("magic: "+magic+", "+major+", "+minor);
        int nconstants = istream.readShort();
        for (int i = 1; i < nconstants; i++) {
            int cid = istream.readByte();
            // System.out.println(""+i+" : "+cid);
            switch (cid) {
            case 7:
                istream.skipBytes(2);
                break;
            case 9:
            case 10:
            case 11:
                istream.skipBytes(4);
                break;
            case 8:
                istream.skipBytes(2);
                break;
            case 3:
            case 4:
                istream.skipBytes(4);
                break;
            case 5:
            case 6:
                istream.skipBytes(8);
                i++;
                break;
            case 12:
                istream.skipBytes(4);
                break;
            case 1:
                // System.out.println("utf: "+istream.readUTF()+";");
                int slength = istream.readUnsignedShort();
                istream.skipBytes(slength);
                break;
            default:
                // System.err.println("unexpected cid: "+cid+", "+i+", "+
                // nconstants);
                // for (int j=0; j<10; j++)
                // System.err.print(", "+istream.readByte());
                // System.err.println();
                return -1;
            }
        }
        return istream.readShort();
    }

}
