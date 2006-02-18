package org;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests extends TestSuite {

    public static Test suite() throws Exception {
        String testPackageName = AllTests.class.getPackage().getName();

        TestSuite suite = new TestSuite("Test " + testPackageName + " recursive.");

        String testSuiteClassName = AllTests.class.getName();

        File testDir = new File(AllTests.class.getClassLoader().getResource(
                testSuiteClassName.replace('.', '/') + ".class").getFile()).getParentFile();
        Iterator testFiles = findTests(testDir).iterator();
        while (testFiles.hasNext()) {
            File testFile = (File) testFiles.next();

            String testClassName = testFile.getPath().substring(testDir.getPath().length());
            testClassName = testPackageName.replace('.', '/') + testClassName;
            testClassName = testClassName.substring(0, testClassName.length() - ".class".length());
            testClassName = testClassName.replace('\\', '.');
            testClassName = testClassName.replace('/', '.');

            Class testClass = Class.forName(testClassName);
            suite.addTestSuite(testClass);
        }

        return suite;
    }

    //
    // private methods
    //

    private static List findTests(File findDir) {
        ArrayList result = new ArrayList(100);

        File[] files = findDir.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isDirectory()) {
                result.addAll(findTests(file));
            } else if (file.getName().endsWith("Test.class")) {
                result.add(file);
            }
        }

        return result;
    }

}
