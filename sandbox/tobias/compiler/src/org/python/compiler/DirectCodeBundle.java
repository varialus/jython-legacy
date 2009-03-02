package org.python.compiler;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.python.antlr.ast.VisitorIF;
import org.python.core.CompilerFlags;
import org.python.core.PyCode;
import org.python.core.PythonCodeBundle;

// TODO: Implement this if it proves to be too slow to use the flow graph
final class DirectCodeBundle implements
        IntermediateCodeGeneratorFactory<Object, ScopeInformation, Void>,
        PythonCodeBundle {

    public DirectCodeBundle(String name, String filename) {
        // TODO Auto-generated constructor stub
    }

    // IntermediateCodeGeneratorFactory implementation

    public IntermediateCodeGenerator<Object, ScopeInformation, Void> createCodeGenerator(
            boolean printExpr, Map<String, ConstantChecker<Object>> constants,
            VisitorIF<Object> driver, ScopeInformation scope,
            CompilerFlags flags) {
        return new DirectGenerator(); // TODO
    }

    // ScopeFactory implementation

    public ScopeInformation createClass(String name, String[] locals,
            String[] explicitlyGlobal, String[] explicitlyClosure,
            String[] free, String[] scopeRequired, boolean hasStarImport,
            List<ScopeInformation> children) {
        // TODO Auto-generated method stub
        return null;
    }

    public ScopeInformation createFunction(String name, String[] parameters,
            String[] locals, String[] explicitlyGlobal,
            String[] explicitlyClosure, String[] free, String[] scopeRequired,
            boolean isGenerator, boolean hasStarImport,
            List<ScopeInformation> children) {
        // TODO Auto-generated method stub
        return null;
    }

    public ScopeInformation createGlobal(String[] locals, String[] cell,
            boolean hasStarImport, List<ScopeInformation> children) {
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
