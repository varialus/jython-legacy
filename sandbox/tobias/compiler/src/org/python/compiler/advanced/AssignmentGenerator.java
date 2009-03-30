package org.python.compiler.advanced;

import org.python.antlr.PythonTree;
import org.python.antlr.base.expr;
import org.python.antlr.base.slice;

public interface AssignmentGenerator<T> {

    T evaluate(PythonTree expression) throws Exception;

    T evaluateOrNull(PythonTree expression) throws Exception;

    T buildExtendedSlice(java.util.List<slice> dims) throws Exception;

    T[] unpackStar(T value, int before, int after) throws Exception;

    T[] unpack(T value, int count) throws Exception;

    T dictGet(T dict, T key) throws Exception;

    T loadName(String name) throws Exception;

    T loadAttribute(T target, String attribute) throws Exception;

    T loadSubscript(T target, T subscript) throws Exception;

    T loadSlice(T target, T lower, T step, T upper) throws Exception;

    T loadEllipsis(T target) throws Exception;

    void storeName(String name, T compute) throws Exception;

    void storeAttribute(T target, String attribute, T value) throws Exception;

    void storeSubscript(T target, T subscript, T value) throws Exception;

    void storeSlice(T target, T lower, T step, T upper, T value)
            throws Exception;

    void storeEllipsis(T target, T value) throws Exception;

    void deleteName(String name) throws Exception;

    void deleteAttribute(T target, String attribute) throws Exception;

    void deleteSubscript(T target, T subscript) throws Exception;

    void deleteSlice(T target, T lower, T step, T upper) throws Exception;

    void deleteEllipsis(T target) throws Exception;
}
