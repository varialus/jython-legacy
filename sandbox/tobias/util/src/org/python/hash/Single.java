package org.python.hash;

/**
 * A simple implementation of a minimal perfect hash for the cases where the key
 * set is minimal.
 * 
 * @author Tobias Ivarsson
 */
public class Single extends PerfectHash {

    /**
     * Instantiate a new single value minimal perfect hash. Verifies the hash to
     * assert the it really is perfect.
     * 
     * @param key the key used in this hash.
     */
    public Single(String key) {
        super(new String[] { key });
    }

    @Override
    int compute(String key) {
        return 0;
    }

    @Override
    String getFunctionRepresentation() {
        return "0";
    }

}