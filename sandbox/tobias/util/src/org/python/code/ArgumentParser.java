package org.python.code;

import java.util.LinkedList;
import java.util.List;

import org.python.core.PyDictionary;
import org.python.core.PyObject;
import org.python.core.PyTuple;

/**
 * Work in progress: an outline for an argument parser based on a perfect hash.
 * 
 * @author Tobias Ivarsson
 */
public class ArgumentParser {

    public static PyObject[] parse(ArgumentParser spec, PyObject[] arguments,
            String[] keywords, PyObject star, PyDictionary starstar) {
        PyObject[] result = new PyObject[totalArgSize(spec)];
        List<PyObject> stararg;
        PyDictionary kwstararg;
        if (spec.has_kwstararg) {
            kwstararg = spec.has_kwstararg ? new PyDictionary() : null;
            result[result.length - 1] = kwstararg;
        }

        stararg = spec.has_stararg ? new LinkedList<PyObject>() : null;

        if (stararg != null) {
            PyObject[] elements = new PyObject[stararg.size()];
            elements = stararg.toArray(elements);
            result[result.length - (1 + (spec.has_kwstararg ? 1 : 0))] = new PyTuple(
                    elements);
        }
        return result;
    }

    private int argcount;
    private boolean has_stararg;
    private boolean has_kwstararg;
    private int positionalonlycount;

    private static int totalArgSize(ArgumentParser spec) {
        return spec.argcount + (spec.has_stararg ? 1 : 0)
                + spec.positionalonlycount + (spec.has_kwstararg ? 1 : 0);
    }

}
