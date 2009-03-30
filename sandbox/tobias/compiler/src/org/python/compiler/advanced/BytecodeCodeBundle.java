package org.python.compiler.advanced;

import java.io.File;
import java.io.OutputStream;

import org.python.core.PyCode;
import org.python.core.PythonCodeBundle;

public class BytecodeCodeBundle implements PythonCodeBundle {
    private final BytecodeBundle bundle;

    public BytecodeCodeBundle(BytecodeBundle bundle, String name, String filename) {
        this.bundle = bundle;
    }

    public PyCode loadCode() throws Exception {
        return null; // TODO: implement this
    }

    public void saveCode(String directory) throws Exception {
        if (!directory.endsWith(File.separator)) {
            directory += File.separator;
        }
        for (String filename : bundle.filenames()) {
            File file = new File(directory + filename);
        }
    }

    public void writeTo(OutputStream stream) throws Exception {
        // NOTE: This can only be supported if we only have one unit in the bundle
    }
}
