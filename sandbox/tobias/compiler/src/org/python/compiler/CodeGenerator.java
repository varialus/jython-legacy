package org.python.compiler;

/**
 * Generates executable code from the Intermediate Representation.
 * 
 * @author Tobias Ivarsson
 */
interface CodeGenerator {
    BytecodeBundle createBytecodeBundle();
}
