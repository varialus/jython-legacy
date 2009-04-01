package org.thobe.compiler.sea;

public interface GraphVisitor {
    void node(Node node);
    /**
     * Allocate a location to store a local variable.
     * 
     * @param name The name of the local variable.
     * @return a new location for storing the local variable.
     */
    //VariableLocation allocateLocal(String name, ValueType type);

    /**
     * Allocate a location to store a temporary variable.
     * 
     * @return a new location for storing the temporary variable.
     */
    //VariableLocation allocateTemporary();

    //void deallocate(VariableLocation location);
}
