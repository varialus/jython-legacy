package org.python.antlr;

import java.io.File;

import junit.framework.TestCase;

/**
 * Decorates the PythonTreeWalker class as a JUnit test case for a single .py
 * file
 */
public class PythonTreeWalkerTestCase extends TestCase {

	private String _path;

	/**
	 * Constructor called from rerun menu point in eclipse
	 * 
	 * @param name The name of the test.
	 */
	public PythonTreeWalkerTestCase(String name) {
		super(name);
		setPath(name);
	}

	/**
	 * Create a test case which walks <code>pyFile</code>.
	 * 
	 * @param pyFile The *.py file
	 */
	public PythonTreeWalkerTestCase(File pyFile) {
		this(pyFile.getAbsolutePath());
	}

	@Override
	protected void runTest() throws Throwable {
		String path = getPath();
		File file = new File(path);
		assertTrue("file " + path + " not found", file.exists());
		PythonTreeWalker treeWalker = new PythonTreeWalker();
		treeWalker.setTolerant(false);
		treeWalker.setParseOnly(false);
		PythonTree tree = treeWalker.parse(new String[] { path });
		assertNotNull("no tree generated for file ".concat(path), tree);
	}

	@Override
	public int countTestCases() {
		return 1;
	}

	private void setPath(String path) {
		_path = path;
	}

	private String getPath() {
		return _path;
	}

}
