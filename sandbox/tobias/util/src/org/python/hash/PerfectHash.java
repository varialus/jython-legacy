package org.python.hash;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.python.hash.CzechHavasMajewski.RandomHash;

/**
 * Base class for the perfect hash function implementations in this package.
 * 
 * Also contains a factory method for computing the the most appropriate
 * implementation of the hash functions in this package.
 * 
 * @author Tobias Ivarsson
 */
public abstract class PerfectHash {

    /**
     * Compute the hash code for a given key from the key set.
     * 
     * @param key the key to generate the hash code for.
     * @return the hash code for the given key in this perfect hash.
     * @throws IllegalArgumentException if the given key is not in the key set
     *             for the perfect hash.
     */
    public int hashCode(String key) {
        int hashCode = compute(key);
        if (keys[hashCode].equals(key)) {
            return hashCode;
        } else {
            throw new IllegalArgumentException("No such key!");
        }
    }

    /**
     * Compute the hash code for a given key. If the key is not in the key set,
     * return a fallback value
     * 
     * @param key the key to compute the hash code for.
     * @param fallback the fallback value.
     * @return the hash code for the given key in this perfect hash, or the
     *         fallback value if the given key is not in the key set of this
     *         perfect hash.
     */
    public int hashCodeWithFallback(String key, int fallback) {
        int hashCode = compute(key);
        if (keys[hashCode].equals(key)) {
            return hashCode;
        } else {
            return fallback;
        }
    }

    /**
     * Generate a perfect hash function for a given set of keys.
     * 
     * @param keys the keys to generate the perfect hash function for.
     * @return the perfect hash function for the given key set.
     */
    public static PerfectHash generatePerfectHash(String... keys) {
        if (keys == null || keys.length == 0) {
            throw new IllegalArgumentException("");
        }
        PerfectHash hash;
        if (keys.length == 1) {
            return new Single(keys[0]);
        }
        hash = generateSimple(keys);
        if (hash != null) {
            return hash;
        }
        hash = generateCzechHavasMajewski(keys);
        if (hash != null) {
            return hash;
        }
        throw new IllegalArgumentException(
                "failed to compute a perfect hash for the given keys.");
    }

    PerfectHash(String[] keys) {
        if (keys == null || keys.length == 0) {
            throw new IllegalArgumentException(
                    "No keys specified for the hash function.");
        }
        this.keys = keys;
    }

    private final String[] keys;

    abstract int compute(String key);

    void verify() {
        for (String key : keys) {
            if (hashCodeWithFallback(key, -1) == -1) {
                throw new IllegalArgumentException(
                        "The specified arguments does not define a perfect hash.");
            }
        }
    }

    private static PerfectHash generateSimple(String[] keys) {
        int[] table = new int[keys.length];
        Arrays.fill(table, -1);
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            int hash = mod(key.hashCode(), table.length);
            if (table[hash] != -1) {
                return null;
            }
            table[hash] = i;
        }
        return new Simple(keys, table);
    }

    private static PerfectHash generateCzechHavasMajewski(String[] keys) {
        double c = 1.1;
        int times = 0;
        while (true) {
            int N = (int) (keys.length * c);
            times += 1;
            if ((times % 5) == 0) {
                c += 0.1;
            }
            if (times == 10000) {
                return null;
            }
            RandomHash hash = new RandomHash(N);
            Graph G = new Graph(N);
            boolean ok = true;
            for (int i = 0; ok && (i < keys.length); i++) {
                ok &= hash.computeConnection(keys[i], G, i);
            }
            if (ok & G.isAcyclic()) {
                return hash.makePerfectHash(keys, G);
            }
        }
    }

    private static class Edge {
        final int start;
        final int end;
        private final int hash;

        Edge(int start, int end, int N) {
            if (end < start) {
                int temp = start;
                start = end;
                end = temp;
            }
            this.start = start;
            this.end = end;
            hash = end * N + start;
        }

        Edge(int start, int end) {
            this.start = start;
            this.end = end;
            this.hash = 0;
        }

        @Override
        public int hashCode() {
            return hash;
        }

        @Override
        public boolean equals(Object other) {
            if (other instanceof Edge) {
                Edge edge = (Edge) other;
                return edge.start == start && edge.end == end;
            } else {
                return false;
            }
        }
    }

    static class Graph {
        private enum VertexState {
            UNVISITED, VISITED
        }

        private final Map<Edge, Integer> edgeValues = new HashMap<Edge, Integer>();
        private final Map<Integer, Set<Integer>> reachable = new HashMap<Integer, Set<Integer>>();
        private final int num_vertices;

        private Graph(int N) {
            this.num_vertices = N;
        }

        private Set<Integer> getReachable(int vertex) {
            Set<Integer> result = reachable.get(vertex);
            if (result == null) {
                reachable.put(vertex, result = new HashSet<Integer>());
            }
            return result;
        }

        boolean connect(int first, int second, int value) {
            edgeValues.put(new Edge(first, second, num_vertices), value);
            return getReachable(first).add(second)
                    && getReachable(second).add(first);
        }

        int[] values() {
            return dfs(new ValueBuilder(num_vertices)).values;
        }

        private boolean isAcyclic() {
            return dfs(new CycleTest()).acyclic;
        }

        private <T extends Searcher> T dfs(T action) {
            VertexState[] state = new VertexState[num_vertices];
            Arrays.fill(state, VertexState.UNVISITED);
            for (int i = 0; i < num_vertices; i++) {
                if (state[i] == VertexState.UNVISITED) {
                    Stack<Edge> visitStack = new Stack<Edge>();
                    visitStack.push(new Edge(-1, i));
                    while (!visitStack.isEmpty()) {
                        Edge edge = visitStack.pop();
                        state[edge.end] = VertexState.VISITED; // OR rather
                        // "VISITING"
                        for (int neighbour : getReachable(edge.end)) {
                            if (neighbour != edge.start) {
                                if (state[neighbour] == VertexState.UNVISITED) {
                                    visitStack.push(new Edge(edge.end,
                                            neighbour));
                                    action.apply(edge.end, neighbour,
                                            edgeValues);
                                } else {
                                    action.fail();
                                    return action;
                                }
                            }
                        }
                        state[edge.end] = VertexState.VISITED;
                    }
                }
            }
            return action;
        }
    }

    private static abstract class Searcher {
        abstract void fail();

        void apply(int vertex, int neighbour, Map<Edge, Integer> edgeValues) {
        }
    }

    private static class CycleTest extends Searcher {
        boolean acyclic = true;

        @Override
        void fail() {
            acyclic = false;
        }
    }

    private static class ValueBuilder extends Searcher {
        int[] values;

        ValueBuilder(int N) {
            values = new int[N];
            Arrays.fill(values, 0);
        }

        @Override
        void fail() {
            values = null;
        }

        @Override
        void apply(int vertex, int neighbour, Map<Edge, Integer> edgeValues) {
            int newValue = edgeValues.get(new Edge(vertex, neighbour,
                    values.length))
                    - values[vertex];
            values[neighbour] = mod(newValue, values.length);
        }
    }

    @Override
    public final String toString() {
        return "PerfectHash{type=" + getClass().getSimpleName() + "; "
                + getFunctionRepresentation() + "}";
    }

    abstract String getFunctionRepresentation();

    static int mod(int value, int n) {
        value %= n;
        if (value < 0) {
            return n + value;
        } else {
            return value;
        }
    }

}
