package org.python.compiler;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.python.antlr.ast.VisitorIF;
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
final class FlowGraphBundle
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

    void acceptTransformations(Transformer transformer) {
        // TODO Auto-generated method stub

    }

    PythonCodeBundle generateCode(CodeGenerator codegen) {
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

    public IntermediateCodeGenerator<Variable, ScopeInformation, GeneratedCodeState<Variable>> createCodeGenerator(
            boolean printExpr,
            Map<String, ConstantChecker<Variable>> constants,
            VisitorIF<Variable> driver, ScopeInformation scope,
            CompilerFlags flags) {
        CodeGraph graph = makeGraphFor(scope);
        if (scope.isModule()) {
            if (module != null) {
                throw new IllegalArgumentException(
                        "Cannot have multiple modules in one flow graph bundle.");
            }
            module = graph;
        }
        return new FlowGraphGenerator(printExpr, driver, constants, flags,
                graph);
    }

    private CodeGraph makeGraphFor(ScopeInformation scope) {
        return factory.createGraph(new GraphInfo() {
            // TODO: implement this by means of the scope info
        });
    }

    public ScopeInformation createClass(String name, String[] locals,
            String[] explicitlyGlobal, String[] explicitlyClosure,
            String[] free, String[] scopeRequired, boolean hasStarImport,
            List<ScopeInformation> children) {
        return ScopeInformation.makeClassScope(name, locals, explicitlyGlobal,
                explicitlyClosure, free, scopeRequired, hasStarImport, children);
    }

    public ScopeInformation createFunction(String name, String[] parameters,
            String[] locals, String[] explicitlyGlobal,
            String[] explicitlyClosure, String[] free, String[] scopeRequired,
            boolean isGenerator, boolean hasStarImport,
            List<ScopeInformation> children) {
        return ScopeInformation.makeFunctionScope(name, parameters, locals,
                explicitlyGlobal, explicitlyClosure, free, scopeRequired,
                isGenerator, hasStarImport, children);
    }

    public ScopeInformation createGlobal(String[] locals, String[] cell,
            boolean hasStarImport, List<ScopeInformation> children) {
        return ScopeInformation.makeGlobalScope(locals, cell, hasStarImport,
                children);
    }
}
