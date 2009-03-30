package org.python.compiler.advanced;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.python.antlr.ParseException;
import org.python.antlr.PythonTree;

final class ScopeBuilder<T> {

    static <T> ScopeBuilder<T> create(ScopeFactory<T> factory,
            Map<PythonTree, T> scopes) {
        return new ScopeBuilder<T>(factory, null, null, null,
                ScopeType.GLOBAL_SCOPE, scopes);
    }

    private static String[] merge(String[]... arrays) {
        int size = 0;
        for (String[] array : arrays) {
            size += array.length;
        }
        String[] result = new String[size];
        int i = 0;
        for (String[] array : arrays) {
            for (String string : array) {
                result[i++] = string;
            }
        }
        return null;
    }

    private enum ScopeType {
        GLOBAL_SCOPE {
            @Override
            <T> T produce(ScopeFactory<T> factory, String name,
                    String[] parameters, String[] locals,
                    String[] explicitlyGlobal, String[] explicitlyClosure,
                    String[] free, String[] scopeRequired, boolean isGenerator,
                    boolean hasStarImport, List<T> children) {
                if (parameters.length != 0) {
                    throw new IllegalStateException(
                            "A module cannot have parameters.");
                }
                String[] global = merge(locals, explicitlyGlobal,
                        explicitlyClosure, free);
                /*
                return factory.createGlobal(global, scopeRequired,
                        hasStarImport, children);
                        */
                return null;
            }
        },
        CLASS_SCOPE(false) {
            @Override
            <T> T produce(ScopeFactory<T> factory, String name,
                    String[] parameters, String[] locals,
                    String[] explicitlyGlobal, String[] explicitlyClosure,
                    String[] free, String[] scopeRequired, boolean isGenerator,
                    boolean hasStarImport, List<T> children) {
                if (parameters.length != 0) {
                    throw new IllegalStateException(
                            "A class cannot have parameters.");
                }
                /*
                return factory.createClass(name, locals, explicitlyGlobal,
                        explicitlyClosure, free, scopeRequired, hasStarImport,
                        children);
                        */
                return null;
            }

            @Override
            void require(String id, ScopeBuilder<?> scope) {
                scope.parent.require(id);
                scope.required.add(id);
            }
        },
        FUNCTION_SCOPE {
            @Override
            <T> T produce(ScopeFactory<T> factory, String name,
                    String[] parameters, String[] locals,
                    String[] explicitlyGlobal, String[] explicitlyClosure,
                    String[] free, String[] scopeRequired, boolean isGenerator,
                    boolean hasStarImport, List<T> children) {
                /*
                return factory.createFunction(name, parameters, locals,
                        explicitlyGlobal, explicitlyClosure, free,
                        scopeRequired, isGenerator, hasStarImport, children);
                        */
                return null;
            }
        },
        GENERATOR_SCOPE {
            @Override
            <T> T produce(ScopeFactory<T> factory, String name,
                    String[] parameters, String[] locals,
                    String[] explicitlyGlobal, String[] explicitlyClosure,
                    String[] free, String[] scopeRequired, boolean isGenerator,
                    boolean hasStarImport, List<T> children) {
                if (parameters.length != 0) {
                    throw new IllegalStateException(
                            "A nameless generator cannot have named parameters.");
                }
                if (!isGenerator) {
                    throw new IllegalStateException(
                            "A nameless generator needs to be defined as a generator.");
                }
                /*
                return factory.createFunction(name, new String[] { null },
                        locals, explicitlyGlobal, explicitlyClosure, free,
                        scopeRequired, isGenerator, hasStarImport, children);
                        */
                return null;
            }
        };

        final boolean inheritFromThis;

        private ScopeType() {
            this(true);
        }

        private ScopeType(boolean inheritFromThis) {
            this.inheritFromThis = inheritFromThis;
        }

        void require(String id, ScopeBuilder<?> scope) {
            scope.required.add(id);
        };

        <T> T produce(ScopeFactory<T> factory, String name, // 
                String[] parameters, //
                String[] locals, //
                String[] explicitlyGlobal, //
                String[] explicitlyClosure, //
                String[] free, //
                String[] scopeRequired, //
                boolean isGenerator, boolean hasStarImport, List<T> children) {
            return null;
        }

    }

    private final ScopeFactory<T> factory;
    private final ScopeBuilder<T> parent;
    private final ScopeType type;
    private final Map<PythonTree, T> scopes;
    private final List<T> children = new LinkedList<T>();
    private final PythonTree forNode;
    private final Set<String> parameters = new LinkedHashSet<String>();
    private final Set<String> defined = new HashSet<String>();
    private final Set<String> used = new HashSet<String>();
    private final Set<String> deletedOnlyLocal = new HashSet<String>();
    private final Set<String> globals = new HashSet<String>();
    private final Set<String> nonlocals = new HashSet<String>();
    private final Set<String> required = new HashSet<String>();
    private boolean isGenerator = false;
    private boolean hasStarImport = false;
    private final String name;

    private ScopeBuilder(ScopeFactory<T> factory, ScopeBuilder<T> parent,
            String name, PythonTree forNode, ScopeType type,
            Map<PythonTree, T> scopes) {
        this.factory = factory;
        this.parent = parent;
        this.name = name;
        this.forNode = forNode;
        this.type = type;
        this.scopes = scopes;
        this.isGenerator = type == ScopeType.GENERATOR_SCOPE;
    }

    final void def(String id) {
        defined.add(id);
        deletedOnlyLocal.remove(id);
    }

    final void use(String id) {
        used.add(id);
    }

    final void del(String id) {
        if (!defined.contains(id)) {
            if (!(globals.contains(id) || nonlocals.contains(id))) {
                deletedOnlyLocal.add(id);
            }
        }
    }

    private void require(String id) {
        type.require(id, this);
    }

    final void addGlobal(String id) {
        if (parameters.contains(id)) {
            throw new ParseException("name '" + id
                    + "' is parameter and global");
        } else if (nonlocals.contains(id)) {
            throw new ParseException("name '" + id + "' is nonlocal and global");
        }
        globals.add(id);
        deletedOnlyLocal.remove(id);
    }

    final void addNonlocal(String id) {
        if (parameters.contains(id)) {
            throw new ParseException("name '" + id
                    + "' is parameter and nonlocal");
        } else if (globals.contains(id)) {
            throw new ParseException("name '" + id + "' is nonlocal and global");
        }
        nonlocals.add(id);
        deletedOnlyLocal.remove(id);
    }

    final void addParam(String id) {
        if (!parameters.add(id)) {
            throw new ParseException("duplicate argument '" + id
                    + "' in function definition");
        }
    }

    final ScopeBuilder<?> createClassScope(String name, PythonTree forNode) {
        return new ScopeBuilder<T>(factory, this, name, forNode,
                ScopeType.CLASS_SCOPE, this.scopes);
    }

    final ScopeBuilder<?> createFunctionScope(String name, PythonTree forNode) {
        return new ScopeBuilder<T>(factory, this, name, forNode,
                ScopeType.FUNCTION_SCOPE, this.scopes);
    }

    final ScopeBuilder<?> createGeneratorExpressionScope(PythonTree forNode) {
        return new ScopeBuilder<T>(factory, this, null, forNode,
                ScopeType.GENERATOR_SCOPE, this.scopes);
    }

    final void setHasStarImport() {
        hasStarImport = true;
    }

    final void setIsGenerator() {
        isGenerator = true;
    }

    private void addChild(T child) {
        children.add(child);
    }

    private void requireAll(Set<String>... required) {
        if (parent != null) {
            for (Set<String> set : required) {
                for (String req : set) {
                    parent.require(req);
                }
            }
        }
    }

    final T complete() {
        if (!deletedOnlyLocal.isEmpty()) {
            throw new ParseException("local variables " + deletedOnlyLocal
                    + " are only ever deleted, never assigned.");
        }
        // parameters, globals and nonlocals are already sane, sanitize the rest
        // defined becomes the locals, defined in this scope
        defined.removeAll(parameters);
        defined.removeAll(nonlocals);
        defined.removeAll(globals);
        // used becomes the unqualified free variables
        used.removeAll(parameters);
        used.removeAll(defined);
        used.removeAll(nonlocals);
        used.removeAll(globals);
        String[] empty = {};
        String[] scope = required.toArray(empty);
        if (type.inheritFromThis) {
            System.out.println("remove " + defined + " from " + required);
            required.removeAll(defined);
        }
        requireAll(used, nonlocals, required);
        T product = type.produce(factory, name, // The name of the entity
                parameters.toArray(empty), // parameters
                defined.toArray(empty), // locals, defined in this scope
                globals.toArray(empty), // explicitly global
                nonlocals.toArray(empty), // explicitly closure
                used.toArray(empty), // free, non explicit
                scope, // required by child scopes. global or closure
                isGenerator, hasStarImport, children);
        if (forNode != null && scopes != null) {
            scopes.put(forNode, product);
        }
        if (parent != null) {
            parent.addChild(product);
        }
        return product;
    }
}
