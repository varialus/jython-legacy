package org.python.util.pydoclet;

import org.python.core.Py;
import org.python.core.PyObject;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;
import com.sun.javadoc.RootDoc;
import java.util.Properties;

/**
 *  A Doclet for creating pydoc source from documented Java code.
 */
public class PyDoclet {

    public static boolean start(RootDoc root) {
        Properties props = new Properties();
        Properties baseProps = PySystemState.getBaseProperties();
    
        if(props.getProperty("python.home") == null
                && baseProps.getProperty("python.home") == null) {
            //FIXME: hard coded local path.
            props.put("python.home", "/Users/frank/src/jython/trunk/jython/dist");
        }

        PySystemState.initialize(baseProps, props, new String[0]);
        PythonInterpreter interp = new PythonInterpreter(null, new PySystemState());
        interp.execfile("doclet.py");
        PyObject cls = interp.get("doclet");
        if (cls == null) {
            throw new RuntimeException("No callable (class or function) named doclet");
        }
        PyObject doclet = cls.__call__();
        Object o = doclet.__tojava__(Processor.class);
        if (o == Py.NoConversion) {
            throw new RuntimeException("doclet.py must implement Processor");
        }
        Processor p = (Processor)o;
        p.process(root);
        return true;
    }
}
