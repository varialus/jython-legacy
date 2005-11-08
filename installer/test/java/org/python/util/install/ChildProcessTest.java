
package org.python.util.install;

import junit.framework.TestCase;

public class ChildProcessTest extends TestCase {

  private final static String CLASS_NAME = "org.python.util.install.ChildProcessExample";

  /**
   * test a default child process
   */
  public void testChildProcess() {
    ChildProcess childProcess = new ChildProcess();
    String command = buildJavaCommand(CLASS_NAME);
    childProcess.setCommand(command);
    childProcess.setDebug(true);
    int exitValue = childProcess.run();
    assertEquals("Expected child process to end normally instead of " + exitValue, 0, exitValue);
  }

  /**
   * test the child process with a timeout
   */
  public void testChildProcessTimeout() {
    ChildProcess childProcess = new ChildProcess();
    String command = buildJavaCommand(CLASS_NAME);
    childProcess.setCommand(command);
    childProcess.setDebug(true);
    childProcess.setTimeout(2000); // timeout to 2 seconds
    int exitValue = childProcess.run();
    assertEquals("Expected child process to be destroyed instead of " + exitValue, ChildProcess.DESTROYED_AFTER_TIMEOUT, exitValue);
  }

  /**
   * test the child process with a command array
   */
  public void testChildProcessCommandArray() {
    String command = buildJavaCommand(CLASS_NAME);
    String commandArray[] = command.split(" ");
    ChildProcess childProcess = new ChildProcess(commandArray);
    childProcess.setDebug(true);
    int exitValue = childProcess.run();
    assertEquals("Expected child process to end normally instead of " + exitValue, 0, exitValue);
  }

  //
  // private methods
  //

  private String buildJavaCommand(String classAndArguments) {
    String classpath = System.getProperty("java.class.path");
    return "java -classpath \"" + classpath + "\" " + classAndArguments;
  }

}