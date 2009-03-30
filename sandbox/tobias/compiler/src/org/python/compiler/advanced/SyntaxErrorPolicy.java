package org.python.compiler.advanced;

import java.util.Collection;
import java.util.LinkedList;

import org.python.antlr.ParseException;
import org.python.antlr.PythonTree;
import org.python.antlr.ast.GeneratorExp;

public abstract class SyntaxErrorPolicy {

    public static final SyntaxErrorPolicy RAISE_EXCEPTION_ON_FIRST_ERROR = new SyntaxErrorPolicy() {

        @Override
        void error(ParseException parseException) {
            throw parseException;
        }

    };

    public static final class ErrorCollectingPolicy extends SyntaxErrorPolicy {
        private final Collection<ParseException> collection = new LinkedList<ParseException>();

        public void throwException() {
            if (hasErrorOccured()) {
                StringBuilder result = new StringBuilder();
                for (ParseException error : collection) {
                    result.append(error.toString());
                    result.append("\n");
                }
                throw new ParseException(result.toString());
            }
        }

        public boolean hasErrorOccured() {
            return !collection.isEmpty();
        }

        public void clear() {
            collection.clear();
        }

        @Override
        void error(ParseException parseException) {
            collection.add(parseException);
        }
    }

    private SyntaxErrorPolicy() {

    }

    public void syntaxError(String message, PythonTree node)
            throws ParseException {
        error(new ParseException(message, node));
    }

    public void syntaxError(ParseException except, PythonTree node)
            throws ParseException {
        ParseException exception = new ParseException(except.getMessage(), node);
        exception.setStackTrace(except.getStackTrace());
        error(exception);
    }

    public void astError(String message, GeneratorExp node) {
        error(new ParseException(message, node));
    }

    abstract void error(ParseException parseException);
}
