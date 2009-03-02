package org.python.hash;

import java.util.Arrays;

/**
 * A simple implementation of a minimal perfect hash, usable in the cases where
 * the standard {@link String#hashCode()} yields a perfect hash for the set of
 * keys.
 * 
 * @author Tobias Ivarsson
 */
public class Simple extends PerfectHash {

    private final int[] table;

    /**
     * Instantiate a new minimal perfect hash that uses
     * {@link String#hashCode()}. Verifies the hash to assert the it really is
     * perfect.
     * 
     * @param keys the set of keys used in this hash.
     * @param table the reordering table for the hash.
     */
    public Simple(String[] keys, int[] table) {
        super(keys);
        this.table = table;
        verify();
    }

    @Override
    int compute(String key) {
        return table[mod(key.hashCode(), table.length)];
    }

    @Override
    String getFunctionRepresentation() {
        String tableDef = "table=" + Arrays.toString(table);
        String body = "table[hash(key) % table.length]";
        return tableDef + "; " + body;
    }

}
