package org.python.compiler;

import java.util.List;

/**
 * @author Tobias Ivarsson
 * @param <T> The base type of the scope representation created by this factory.
 */
public interface ScopeFactory<T> {

    /**
     * @param locals
     * @param cell
     * @param hasStarImport
     * @param children
     * @return an instance of a global scope representation, as created by the
     *         specific factory implementation.
     */
    T createGlobal(String[] locals, String[] cell, boolean hasStarImport,
            List<T> children);

    /**
     * @param name
     * @param locals
     * @param explicitlyGlobal
     * @param explicitlyClosure
     * @param free
     * @param scopeRequired
     * @param hasStarImport
     * @param children
     * @return an instance of a class scope representation, as created by the
     *         specific factory implementation.
     */
    T createClass(String name, String[] locals, String[] explicitlyGlobal,
            String[] explicitlyClosure, String[] free, String[] scopeRequired,
            boolean hasStarImport, List<T> children);

    /**
     * @param name
     * @param parameters
     * @param locals
     * @param explicitlyGlobal
     * @param explicitlyClosure
     * @param free
     * @param scopeRequired
     * @param isGenerator
     * @param hasStarImport
     * @param children
     * @return an instance of a function scope representation, as created by the
     *         specific factory implementation.
     */
    T createFunction(String name, String[] parameters, String[] locals,
            String[] explicitlyGlobal, String[] explicitlyClosure,
            String[] free, String[] scopeRequired, boolean isGenerator,
            boolean hasStarImport, List<T> children);

}
