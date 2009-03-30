package org.python.compiler.advanced.direct;

import java.io.OutputStream;

import org.python.antlr.ast.VisitorIF;
import org.python.compiler.advanced.ConstantCodeGenerator;
import org.python.compiler.advanced.ConstantPool;
import org.python.compiler.advanced.IntermediateCodeGenerator;
import org.python.compiler.advanced.IntermediateCodeGeneratorFactory;
import org.python.compiler.advanced.ScopeInformation;
import org.python.core.CompilerFlags;
import org.python.core.PyCode;
import org.python.core.PythonCodeBundle;

// TODO: Implement this if it proves to be too slow to use the flow graph
public final class DirectCodeBundle implements
        IntermediateCodeGeneratorFactory<Object, ScopeInformation, Void>,
        PythonCodeBundle {

    public DirectCodeBundle(String name, String filename) {
        // TODO Auto-generated constructor stub
    }

    // IntermediateCodeGeneratorFactory implementation

    public IntermediateCodeGenerator<Object, ScopeInformation, Void> createModuleCodeGenerator(
            boolean linenumbers, boolean printExpr, VisitorIF<Object> driver, ConstantPool constants,
            ScopeInformation scope, CompilerFlags flags) {
        return new DirectGenerator(driver, constants); // TODO
    }

    public IntermediateCodeGenerator<Object, ScopeInformation, Void> createClassCodeGenerator(
            boolean linenumbers, VisitorIF<Object> driver, ConstantPool constants,
            ScopeInformation scope, CompilerFlags flags) {
        // TODO Auto-generated method stub
        return null;
    }

    public IntermediateCodeGenerator<Object, ScopeInformation, Void> createFunctionCodeGenerator(
            boolean linenumbers, VisitorIF<Object> driver, ConstantPool constants,
            ScopeInformation scope, CompilerFlags flags) {
        // TODO Auto-generated method stub
        return null;
    }
    
    public ConstantCodeGenerator<Object> createConstantCodeGenerator() {
        // TODO Auto-generated method stub
        return null;
    }

    // ScopeFactory implementation

    public ScopeInformation createGlobal(String[] locals, String[] cell,
            boolean hasStarImport) {
        // TODO Auto-generated method stub
        return null;
    }

    public ScopeInformation createClass(String name, String[] locals,
            String[] globals, String[] free, String[] cell,
            boolean hasStarImport) {
        // TODO Auto-generated method stub
        return null;
    }

    public ScopeInformation createFunction(String name, String[] parameters,
            String[] locals, String[] globals, String[] free, String[] cell,
            boolean isGenerator, boolean hasStarImport) {
        // TODO Auto-generated method stub
        return null;
    }

    // PythonCodeBundle implementation

    public PyCode loadCode() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public void saveCode(String directory) throws Exception {
        // TODO Auto-generated method stub

    }

    public void writeTo(OutputStream stream) throws Exception {
        // TODO Auto-generated method stub

    }
}
