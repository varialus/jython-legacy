package org.python.hash;

import java.util.Arrays;
import java.util.Random;

/**
 * An implementation of the minimal perfect hash generation algorithm described
 * in "An optimal algorithm for generating minimal perfect hash functions",
 * Zbigniew J. Czech, George Havas, Bohdan S. Majewski, Information Processing
 * Letters, 43(5):257Ð264, October 1992.
 * 
 * @author Tobias Ivarsson
 */
public class CzechHavasMajewski extends PerfectHash {
    private final int[] table;
    private final String prefix1;
    private final String prefix2;

    /**
     * Instantiate a new CzechHavasMajewski minimal perfect hash. Verifies the
     * hash to assert the it really is perfect.
     * 
     * @param keys the set of keys used in this hash.
     * @param table the lookup table for making the hash perfect.
     * @param prefix1 the prefix for the first hash sub function.
     * @param prefix2 the prefix for the second hash sub function.
     */
    public CzechHavasMajewski(String[] keys, int[] table, String prefix1,
            String prefix2) {
        super(keys);
        if (table == null) {
            throw new IllegalArgumentException("");
        }
        this.table = table;
        this.prefix1 = prefix1;
        this.prefix2 = prefix2;
        verify();
    }

    @Override
    int compute(String key) {
        return (table[compute(prefix1, key, table.length)] + table[compute(
                prefix2, key, table.length)])
                % table.length;
    }

    private static int compute(String prefix, String key, int N) {
        return mod((prefix + key).hashCode(), N);
    }

    @Override
    public String getFunctionRepresentation() {
        String tableDef = "table=" + Arrays.toString(table);
        String function = "(table[hash(\"" + prefix1
                + "\" + key)] + table[hash(\"" + prefix2 + "\" + key)]) % "
                + table.length;
        return tableDef + "; " + function;
    }

    static class RandomHash {
        private static final Random random = new Random();
        private static final char[] chars = ("ABCDEFGHIJKLMNOPQRSTWXYZ"
                + "abcdefghijklmnopqrstuvwxyz" + "1234567890").toCharArray();
        private final int N;
        private final String prefix1;
        private final String prefix2;

        RandomHash(int N) {
            this.N = N;
            this.prefix1 = randomString(10);
            this.prefix2 = randomString(10);
        }

        boolean computeConnection(String key, Graph graph, int value) {
            int hash1 = CzechHavasMajewski.compute(prefix1, key, N);
            int hash2 = CzechHavasMajewski.compute(prefix2, key, N);
            return graph.connect(hash1, hash2, value);
        }

        PerfectHash makePerfectHash(String[] keys, Graph graph) {
            return new CzechHavasMajewski(keys, graph.values(), prefix1,
                    prefix2);
        }

        private static String randomString(int size) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < size; i++) {
                builder.append(chars[random.nextInt(chars.length)]);
            }
            return builder.toString();
        }
    }

}
