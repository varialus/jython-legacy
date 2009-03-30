package org.python.compiler.advanced;

/**
 * Generates executable code from the Intermediate Representation.
 * 
 * @author Tobias Ivarsson
 */
public interface CodeGenerator {
    BytecodeBundle createBytecodeBundle();
}
