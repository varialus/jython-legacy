package org.python.compiler.advanced.sea;

import java.util.Arrays;

import org.python.compiler.sea.CallStrategy;
import org.python.compiler.sea.FrameStrategy;
import org.python.compiler.sea.GraphBuilder;
import org.python.compiler.sea.SuperGraph;
import org.python.core.CompilerFlags;
import org.thobe.compiler.sea.NamespacePopulator;
import org.thobe.compiler.sea.Value;
import org.thobe.compiler.sea.VariableFactory;

abstract class SeaScope implements NamespacePopulator {

    abstract Iterable<String> free();

    abstract GraphBuilder builder(SuperGraph ocean, CompilerFlags flags);

    abstract Value graph(SeaBuilder builder);

    abstract Value yieldValue(SeaBuilder builder, Value value);

    abstract void returnValue(SeaBuilder builder, Value value);

    abstract void mirandaReturn(SeaBuilder builder);

    public static SeaScope forModule(final String[] locals,
            final String[] cell, boolean hasStarImport) {
        return new SeaScope() {
            @Override
            GraphBuilder builder(SuperGraph ocean, CompilerFlags flags) {
                return new SeaBuilder(this, ocean, flags);
            }

            @Override
            Iterable<String> free() {
                return Arrays.asList();
            }

            public void populate(VariableFactory factory) {
                for (String var : locals) {
                    factory.createLocalVariable(var);
                }
                for (String var : cell) {
                    factory.createCellVariable(var);
                }
            }

            @Override
            Value graph(SeaBuilder builder) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            void mirandaReturn(SeaBuilder builder) {
                // TODO Auto-generated method stub

            }

            @Override
            void returnValue(SeaBuilder builder, Value value) {
                // TODO Auto-generated method stub

            }

            @Override
            Value yieldValue(SeaBuilder builder, Value value) {
                // TODO Auto-generated method stub
                return null;
            }
        };
    }

    public static SeaScope forClass(String name, String[] locals,
            String[] globals, String[] free, String[] cell,
            boolean hasStarImport) {
        return new SeaScope() {
            @Override
            public GraphBuilder builder(SuperGraph ocean, CompilerFlags flags) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            Iterable<String> free() {
                // TODO Auto-generated method stub
                return null;
            }

            public void populate(VariableFactory factory) {
                // TODO Auto-generated method stub

            }

            @Override
            Value graph(SeaBuilder builder) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            void mirandaReturn(SeaBuilder builder) {
                // TODO Auto-generated method stub

            }

            @Override
            void returnValue(SeaBuilder builder, Value value) {
                // TODO Auto-generated method stub

            }

            @Override
            Value yieldValue(SeaBuilder builder, Value value) {
                // TODO Auto-generated method stub
                return null;
            }
        };
    }

    public static SeaScope forFunction(String name, final String[] parameters,
            final String[] locals, final String[] globals, final String[] free,
            final String[] cell, boolean hasStarImport) {
        return new SeaScope() {
            @Override
            public GraphBuilder builder(SuperGraph ocean, CompilerFlags flags) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            Iterable<String> free() {
                // TODO: implement this
                return null;
            }

            public void populate(VariableFactory factory) {
                for (String var : parameters) {
                    factory.createParameter(var);
                }
                for (String var : locals) {
                    factory.createLocalVariable(var);
                }
                // TODO: more stuff
            }

            @Override
            Value graph(SeaBuilder builder) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            void mirandaReturn(SeaBuilder builder) {
                // TODO Auto-generated method stub

            }

            @Override
            void returnValue(SeaBuilder builder, Value value) {
                // TODO Auto-generated method stub

            }

            @Override
            Value yieldValue(SeaBuilder builder, Value value) {
                // TODO Auto-generated method stub
                return null;
            }
        };
    }

    public static SeaScope forGenerator(String name, String[] parameters,
            String[] locals, String[] globals, String[] free, String[] cell,
            boolean hasStarImport) {
        return new SeaScope() {
            @Override
            public GraphBuilder builder(SuperGraph ocean, CompilerFlags flags) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            Iterable<String> free() {
                // TODO Auto-generated method stub
                return null;
            }

            public void populate(VariableFactory factory) {
                // TODO Auto-generated method stub

            }

            @Override
            Value graph(SeaBuilder builder) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            void mirandaReturn(SeaBuilder builder) {
                // TODO Auto-generated method stub

            }

            @Override
            void returnValue(SeaBuilder builder, Value value) {
                // TODO Auto-generated method stub

            }

            @Override
            Value yieldValue(SeaBuilder builder, Value value) {
                // TODO Auto-generated method stub
                return null;
            }
        };
    }

    private static class SeaBuilder extends GraphBuilder {
        final SeaScope scope;

        SeaBuilder(SeaScope scope, SuperGraph ocean, CompilerFlags flags) {
            super(scope, new CallStrategy(){}, new FrameStrategy(), flags);
            this.scope = scope;
        }

        @Override
        public Value graph() {
            return scope.graph(this);
        }

        @Override
        public void mirandaReturn() {
            scope.mirandaReturn(this);
        }

        @Override
        public void returnPythonValue(Value value) {
            scope.returnValue(this, value);
        }

        @Override
        public Value yeildValue(Value value) {
            return scope.yieldValue(this, value);
        }
    }
}
