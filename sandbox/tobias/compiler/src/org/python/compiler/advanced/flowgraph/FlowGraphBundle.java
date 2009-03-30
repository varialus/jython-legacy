package org.python.compiler.advanced.flowgraph;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.python.antlr.ast.VisitorIF;
import org.python.compiler.advanced.BytecodeBundle;
import org.python.compiler.advanced.BytecodeCodeBundle;
import org.python.compiler.advanced.CodeGenerator;
import org.python.compiler.advanced.ConstantCodeGenerator;
import org.python.compiler.advanced.ConstantPool;
import org.python.compiler.advanced.IntermediateCodeGenerator;
import org.python.compiler.advanced.IntermediateCodeGeneratorFactory;
import org.python.compiler.advanced.ScopeInformation;
import org.python.compiler.flowgraph.CodeGraph;
import org.python.compiler.flowgraph.CodeGraphFactory;
import org.python.compiler.flowgraph.CodeGraphVisitor;
import org.python.compiler.flowgraph.GraphInfo;
import org.python.compiler.flowgraph.Transformer;
import org.python.compiler.flowgraph.Variable;
import org.python.core.CompilerFlags;
import org.python.core.PythonCodeBundle;

/**
 * Represents the super graph of a compilation unit.
 * 
 * @author Tobias Ivarsson
 */
public final class FlowGraphBundle
        implements
        IntermediateCodeGeneratorFactory<Variable, ScopeInformation, GeneratedCodeState<Variable>> {
    private final String name;
    private final String filename;
    private final CodeGraphFactory factory = new CodeGraphFactory();
    private CodeGraph module = null;

    public FlowGraphBundle(String name, String filename) {
        this.name = name;
        this.filename = filename;
    }

    public void acceptTransformations(Transformer transformer) {
        // TODO Auto-generated method stub

    }

    public PythonCodeBundle generateCode(CodeGenerator codegen) {
        if (module == null) {
            throw new IllegalStateException(
                    "The flow graph bundle has no module code.");
        }
        Queue<CodeGraph> workQueue = new LinkedList<CodeGraph>(
                Arrays.asList(module));
        BytecodeBundle bundle = codegen.createBytecodeBundle();
        CodeGraphVisitor visitor = new CodeGeneratorGraphVisitor(bundle);
        while (!workQueue.isEmpty()) {
            CodeGraph graph = workQueue.poll();
            graph.accept(visitor);
        }
        return new BytecodeCodeBundle(bundle, name, filename);
    }

    public IntermediateCodeGenerator<Variable, ScopeInformation, GeneratedCodeState<Variable>> createModuleCodeGenerator(
            boolean linenumbers, boolean printExpr, VisitorIF<Variable> driver,
            ConstantPool constants, ScopeInformation scope, CompilerFlags flags) {
        CodeGraph graph = makeGraphFor(scope);
        if (scope.isModule()) {
            if (module != null) {
                throw new IllegalArgumentException(
                        "Cannot have multiple modules in one flow graph bundle.");
            }
            module = graph;
        }
        return new FlowGraphGenerator(printExpr, driver,constants, flags, graph);
    }

    public IntermediateCodeGenerator<Variable, ScopeInformation, GeneratedCodeState<Variable>> createClassCodeGenerator(
            boolean linenumbers, VisitorIF<Variable> driver,
            ConstantPool constants, ScopeInformation scope, CompilerFlags flags) {
        return createModuleCodeGenerator(linenumbers, false, driver,constants, scope,
                flags);
    }

    public IntermediateCodeGenerator<Variable, ScopeInformation, GeneratedCodeState<Variable>> createFunctionCodeGenerator(
            boolean linenumbers, VisitorIF<Variable> driver,
            ConstantPool constants, ScopeInformation scope, CompilerFlags flags) {
        return createModuleCodeGenerator(linenumbers, false, driver,constants, scope,
                flags);
    }
    
    public ConstantCodeGenerator<Variable> createConstantCodeGenerator() {
        // TODO Auto-generated method stub
        return null;
    }

    private CodeGraph makeGraphFor(ScopeInformation scope) {
        return factory.createGraph(new GraphInfo() {
            // TODO: implement this by means of the scope info
        });
    }

    public ScopeInformation createGlobal(String[] locals, String[] cell,
            boolean hasStarImport) {
        /*
        return ScopeInformation.makeGlobalScope(locals, cell, hasStarImport,
                children);
                */
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
}
