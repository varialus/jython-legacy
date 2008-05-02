package org.python.util.pydoclet;

import org.python.core.Py;
import org.python.core.PyObject;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;
import com.sun.javadoc.*;
import java.io.*;
import java.text.*;
import java.util.*;

/**
 *  A Doclet for creating pydoc source from documented Java code.
 */
public class PyDoclet {

public static boolean start(RootDoc root) {
    Properties props = new Properties();
    Properties baseProps = PySystemState.getBaseProperties();

    if(props.getProperty("python.home") == null
            && baseProps.getProperty("python.home") == null) {
        props.put("python.home", "/Users/frank/src/jython/trunk/jython/dist");
    }

    PySystemState.initialize(baseProps, props, new String[0]);
    PythonInterpreter interp = new PythonInterpreter(null, new PySystemState());
    interp.execfile("doclet.py");
    PyObject cls = interp.get("doclet");
    if (cls == null) {
        throw new RuntimeException("No callable (class or function) named doclet.py");
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

/*
public static boolean startx(RootDoc root) {
  //iterate over all classes.
  ClassDoc[] classes = root.classes();
  for (int i=0; i< classes.length; i++) {
    //iterate over all methods and print their names.
    MethodDoc[] methods = classes[i].methods();
    out("Methods");
    out("-------");
    for (int j=0; j<methods.length; j++) {
      out("Method: name = " + methods[j].name());
    }
    out("Fields");
    out("------");
    //iterate over all fields, printing name, comment text, and type.
    FieldDoc[] fields = classes[i].fields();
    for (int j=0; j<fields.length; j++) {
      Object[] field_info = {fields[j].name(), fields[j].commentText(),
                    fields[j].type()};
      out(FIELDINFO.format(field_info));
      //iterate over all field tags and print their values.
      Tag[] tags = fields[j].tags();
      for (int k=0; k<tags.length; k++) {
     out("\tField Tag Name= " + tags[k].name());
     out("\tField Tag Value = " + tags[k].text());
      }
    }
  }
  //No error processing done, simply return true.
  return true;
}

private static void out(String msg) {
  System.out.println(msg);
}

private static MessageFormat METHODINFO =
  new MessageFormat("Method: return type {0}, name = {1};");

private static MessageFormat FIELDINFO =
  new MessageFormat("Field: name = {0}, comment = {1}, type = {2};");
*/
}
