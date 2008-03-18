package org.python.newcompiler;

import java.util.Set;

import org.python.parser.ParseException;

/**
 * An enumeration of the future features.
 * 
 * For use by <code>from __future__ import [feature]</code>.
 * 
 * @author Tobias Ivarsson
 */
public enum Future {
    /**
     * Enables nested scopes.
     */
    nested_scopes(CompilerFlag.CO_NESTED),
    /**
     * Makes integer / integer division return float.
     */
    division(CompilerFlag.CO_NESTED),
    /**
     * Enables generators.
     */
    generators(CompilerFlag.CO_NESTED),
    /**
     * Enables absolute imports.
     */
    absolute_import(CompilerFlag.CO_NESTED),
    /**
     * Enables the with statement.
     */
    with_statement(CompilerFlag.CO_NESTED),
    /**
     * Use braces for block delimiters instead of indentation.
     */
    braces {

        @Override
        protected void addFeature(Set<CompilerFlag> flags) throws ParseException {
            throw new ParseException("not a chance");
        }
    },
    /**
     * Enable the Global Interpreter Lock in Jython.
     */
    GIL {

        @Override
        protected void addFeature(Set<CompilerFlag> flags) throws ParseException {
            throw new ParseException("Never going to happen!");
        }
    },
    /**
     * Enable the Global Interpreter Lock in Jython.
     */
    global_interpreter_lock {

        @Override
        protected void addFeature(Set<CompilerFlag> flags) throws ParseException {
            GIL.addFeature(flags);
        }
    };

    public static final String signature = "__future__";

    private final CompilerFlag flag;

    private Future() {
        this(null);
    }

    private Future(CompilerFlag flag) {
        this.flag = flag;
    }

    /**
     * Add the compiler flag associated with this future feature to the supplied set of flags.
     * 
     * @param flags
     *            the set to add the corresponding compiler flag to.
     * @throws ParseException
     *             if the feature was invalid.
     */
    protected void addFeature(Set<CompilerFlag> flags) throws ParseException {
        flags.add(this.flag);
    }

    static void addFeature(String feature, Set<CompilerFlag> flags) throws ParseException {
        Future future;
        try {
            future = valueOf(feature);
        } catch (IllegalArgumentException e) {
            throw new ParseException("future feature '" + feature + "' is not defined");
        }
        future.addFeature(flags);
    }
}
