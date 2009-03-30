package org.python.compiler.advanced;

import org.python.antlr.ParseException;
import org.python.antlr.PythonTree;
import org.python.antlr.ast.Attribute;
import org.python.antlr.ast.Dict;
import org.python.antlr.ast.Ellipsis;
import org.python.antlr.ast.ExtSlice;
import org.python.antlr.ast.Index;
import org.python.antlr.ast.List;
import org.python.antlr.ast.Name;
import org.python.antlr.ast.Slice;
import org.python.antlr.ast.Subscript;
import org.python.antlr.ast.Tuple;
import org.python.antlr.ast.VisitorBase;
import org.python.antlr.ast.VisitorIF;
import org.python.antlr.base.expr;

abstract class Assignment<T> extends VisitorBase<Void> {
    static <T> VisitorIF<Void> standard(AssignmentGenerator<T> generator,
            T value) {
        return new StandardAssignment<T>(generator, value);
    }

    static <T> VisitorIF<Void> augmented(AssignmentGenerator<T> generator,
            AugAssignCallback<T> producer) {
        return new AugmentedAssignment<T>(generator, producer);
    }

    static <T> VisitorIF<Void> deletion(AssignmentGenerator<T> generator) {
        return new Deletor<T>(generator);
    }

    final AssignmentGenerator<T> generator;
    private final String illegalNodeMessage;

    private Assignment(AssignmentGenerator<T> generator,
            String illegalNodeMessage) {
        this.generator = generator;
        this.illegalNodeMessage = illegalNodeMessage;
    }

    @Override
    public final Void visitName(Name node) throws Exception {
        handleName(node.getInternalId());
        return null;
    }

    @Override
    public final Void visitAttribute(Attribute node) throws Exception {
        T target = generator.evaluate(node.getInternalValue());
        handleAttribute(target, node.getInternalAttr());
        return null;
    }

    @Override
    public final Void visitSubscript(Subscript node) throws Exception {
        T target = generator.evaluate(node.getInternalValue());
        node.getInternalSlice().accept(new SubscriptAssignment(target));
        return null;
    }

    abstract void handleName(String name) throws Exception;

    abstract void handleAttribute(T target, String attribute) throws Exception;

    abstract void handleSubscript(T target, T subscript) throws Exception;

    abstract void handleSlice(T target, T lower, T step, T upper)
            throws Exception;

    abstract void handleEllipsis(T target) throws Exception;

    @Override
    public final void traverse(PythonTree node) throws Exception {
    }

    @Override
    protected final Void unhandled_node(PythonTree node) throws Exception {
        throw new ParseException(illegalNodeMessage, node);
    }

    private static final class StandardAssignment<T> extends Assignment<T> {
        private static final String EXCEPTION_MESSAGE = "";
        private final T value;

        public StandardAssignment(AssignmentGenerator<T> generator, T value) {
            super(generator, EXCEPTION_MESSAGE);
            this.value = value;
        }

        @Override
        final void handleName(String name) throws Exception {
            generator.storeName(name, value);
        }

        @Override
        final void handleAttribute(T target, String attribute) throws Exception {
            generator.storeAttribute(target, attribute, value);
        };

        @Override
        final void handleSubscript(T target, T subscript) throws Exception {
            generator.storeSubscript(target, subscript, value);
        };

        @Override
        final void handleSlice(T target, T lower, T step, T upper)
                throws Exception {
            generator.storeSlice(target, lower, step, upper, value);
        };

        @Override
        final void handleEllipsis(T target) throws Exception {
            generator.storeEllipsis(target, value);
        };

        @Override
        public final Void visitTuple(Tuple node) throws Exception {
            tupleAssign(node.getInternalElts());
            return null;
        }

        @Override
        public final Void visitList(List node) throws Exception {
            tupleAssign(node.getInternalElts());
            return null;
        }

        @Override
        public final Void visitDict(Dict node) throws Exception {
            // Dictionary assignment is easy enough, once the syntax supports it
            java.util.List<expr> keys = node.getInternalKeys();
            java.util.List<expr> values = node.getInternalValues();
            for (int i = 0; i < node.getInternalKeys().size(); i++) {
                T key = generator.evaluate(keys.get(i));
                assign(values.get(i), generator.dictGet(this.value, key));
            }
            return null;
        }

        private void tupleAssign(java.util.List<expr> elts) throws Exception {
            int before = -1, after = 0;
            for (expr elt : elts) {
                if (false /*elt instanceof Starred*/) {
                    // For 3.0 compatibility, where one can have starred targets
                    if (before == -1) {
                        before = after;
                        after = 0;
                    } else {
                        throw new ParseException(
                                "more than one starred expressions in assignment",
                                elt);
                    }
                } else {
                    after++;
                }
            }
            T[] unpacked;
            if (before != -1) {
                unpacked = generator.unpackStar(value, before, after);
            } else {
                unpacked = generator.unpack(value, after);
            }
            int i = 0;
            if (before != -1) {
                for (; i < before; i++) {
                    assign(elts.get(i), unpacked[i]);
                }
                assign(elts.get(i)/* TODO: cast elts[i++] to Starred */
                /* and pick out the value element */, unpacked[i]);
                i++;
            }
            before += 1; // either this was -1, or we consumed the extra starred
            for (; i < before + after; i++) {
                assign(elts.get(i), unpacked[i]);
            }
        }

        private void assign(expr target, T value) throws Exception {
            target.accept(new StandardAssignment<T>(generator, value));
        }
    }

    private static final class AugmentedAssignment<T> extends Assignment<T> {
        private static final String EXCEPTION_MESSAGE = "";
        private final AugAssignCallback<T> producer;

        public AugmentedAssignment(AssignmentGenerator<T> generator,
                AugAssignCallback<T> producer) {
            super(generator, EXCEPTION_MESSAGE);
            this.producer = producer;
        }

        @Override
        final void handleName(String name) throws Exception {
            generator.storeName(name,
                    producer.compute(generator.loadName(name)));
        }

        @Override
        final void handleAttribute(T target, String attribute) throws Exception {
            generator.storeAttribute(
                    target,
                    attribute,
                    producer.compute(generator.loadAttribute(target, attribute)));
        };

        @Override
        final void handleSubscript(T target, T subscript) throws Exception {
            generator.storeSubscript(
                    target,
                    subscript,
                    producer.compute(generator.loadSubscript(target, subscript)));
        };

        @Override
        final void handleSlice(T target, T lower, T step, T upper)
                throws Exception {
            generator.storeSlice(target, lower, step, upper,
                    producer.compute(generator.loadSlice(target, lower, step,
                            upper)));
        };

        @Override
        final void handleEllipsis(T target) throws Exception {
            generator.storeEllipsis(target,
                    producer.compute(generator.loadEllipsis(target)));
        }
    }

    private final class SubscriptAssignment extends VisitorBase<Void> {
        private final T target;

        public SubscriptAssignment(T target) {
            this.target = target;
        }

        @Override
        public final void traverse(PythonTree node) throws Exception {
            Assignment.this.traverse(node);
        }

        @Override
        protected final Void unhandled_node(PythonTree node) throws Exception {
            return Assignment.this.unhandled_node(node);
        }

        @Override
        public Void visitEllipsis(Ellipsis node) throws Exception {
            handleEllipsis(target);
            return null;
        }

        @Override
        public Void visitIndex(Index node) throws Exception {
            T subscript = generator.evaluate(node.getInternalValue());
            handleSubscript(target, subscript);
            return null;
        }

        @Override
        public Void visitExtSlice(ExtSlice node) throws Exception {
            T subscript = generator.buildExtendedSlice(node.getInternalDims());
            handleSubscript(target, subscript);
            return null;
        }

        @Override
        public Void visitSlice(Slice node) throws Exception {
            T lower = generator.evaluateOrNull(node.getInternalLower());
            T step = generator.evaluateOrNull(node.getInternalLower());
            T upper = generator.evaluateOrNull(node.getInternalLower());
            handleSlice(target, lower, step, upper);
            return null;
        }
    }

    private static final class Deletor<T> extends Assignment<T> {
        private static final String EXCEPTION_MESSAGE = "";

        Deletor(AssignmentGenerator<T> generator) {
            super(generator, EXCEPTION_MESSAGE);
        }

        @Override
        void handleName(String name) throws Exception {
            generator.deleteName(name);
        }

        @Override
        void handleAttribute(T target, String attribute) throws Exception {
            generator.deleteAttribute(target, attribute);
        }

        @Override
        void handleSubscript(T target, T subscript) throws Exception {
            generator.deleteSubscript(target, subscript);
        }

        @Override
        void handleSlice(T target, T lower, T step, T upper) throws Exception {
            generator.deleteSlice(target, lower, step, upper);
        }

        @Override
        void handleEllipsis(T target) throws Exception {
            generator.deleteEllipsis(target);
        }
    }
}
