package org.python.compiler.advanced.sea;

import org.thobe.compiler.sea.Value;
import org.thobe.compiler.sea.Variable;

abstract class ValueCarrier {
    private final Variable[] closure;

    private ValueCarrier(Variable[] closure) {
        this.closure = closure;
    }

    static ValueCarrier forClass(Variable[] closure, final Value[] bases,
            final Value[] decorators) {
        return new ValueCarrier(closure) {
            @Override
            Value[] decorators() {
                return decorators;
            }

            @Override
            Value[] bases() {
                return bases;
            }
        };
    }

    static ValueCarrier forFunction(Variable[] closure, final Value[] defaults,
            final Value[] decorators) {
        return new ValueCarrier(closure) {
            @Override
            Value[] decorators() {
                return decorators;
            }

            @Override
            Value[] defaults() {
                return defaults;
            }
        };
    }

    static ValueCarrier forLambda(Variable[] closure, final Value[] defaults) {
        return new ValueCarrier(closure) {
            @Override
            Value[] defaults() {
                return defaults;
            }
        };
    }

    static ValueCarrier forGenerator(final Value firstIter, Variable[] closure) {
        return new ValueCarrier(closure) {
            @Override
            Value firstIterator() {
                return firstIter;
            }
        };
    }

    final Variable[] closure() {
        return closure;
    }

    Value[] decorators() {
        throw unsupported("decorators");
    }

    Value[] bases() {
        throw unsupported("base classes");
    }

    Value[] defaults() {
        throw unsupported("default argument values");
    }

    Value firstIterator() {
        throw unsupported("a first iterator");
    }

    private RuntimeException unsupported(String that) {
        return new UnsupportedOperationException(getClass().getName()
                + " does not have " + that + ".");
    }
}
