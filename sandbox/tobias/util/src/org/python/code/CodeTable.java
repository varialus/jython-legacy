package org.python.code;

import org.python.core.PyBaseFrame;
import org.python.core.PyCode;
import org.python.core.PyObject;

public abstract class CodeTable extends PyCode {
    
    private final int index;

    protected CodeTable(int tableIndex) {
        this.index = tableIndex;
    }

    @Override
    public PyObject call(PyBaseFrame frame, PyObject closure) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PyObject call(PyObject[] args, String[] keywords, PyObject globals,
            PyObject[] defaults, PyObject closure) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PyObject call(PyObject self, PyObject[] args, String[] keywords,
            PyObject globals, PyObject[] defaults, PyObject closure) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PyObject call(PyObject globals, PyObject[] defaults, PyObject closure) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PyObject call(PyObject arg1, PyObject globals, PyObject[] defaults,
            PyObject closure) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PyObject call(PyObject arg1, PyObject arg2, PyObject globals,
            PyObject[] defaults, PyObject closure) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PyObject call(PyObject arg1, PyObject arg2, PyObject arg3,
            PyObject globals, PyObject[] defaults, PyObject closure) {
        // TODO Auto-generated method stub
        return null;
    }

}
