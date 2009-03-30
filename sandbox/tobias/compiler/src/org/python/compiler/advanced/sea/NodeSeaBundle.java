package org.python.compiler.advanced.sea;

import java.util.HashMap;
import java.util.Map;

import org.python.antlr.ast.VisitorIF;
import org.python.compiler.advanced.Constant;
import org.python.compiler.advanced.ConstantCodeGenerator;
import org.python.compiler.advanced.ConstantPool;
import org.python.compiler.advanced.IntermediateCodeGenerator;
import org.python.compiler.advanced.IntermediateCodeGeneratorFactory;
import org.python.compiler.sea.GraphBuilder;
import org.python.compiler.sea.SuperGraph;
import org.python.compiler.sea.SupergraphVisitor;
import org.python.core.CompilerFlags;
import org.thobe.compiler.sea.Value;

public class NodeSeaBundle implements
        IntermediateCodeGeneratorFactory<Value, SeaScope, ValueCarrier> {
    private final Map<Constant, Value> constants = new HashMap<Constant, Value>();
    private final SuperGraph ocean = new SuperGraph();

    public IntermediateCodeGenerator<Value, SeaScope, ValueCarrier> createModuleCodeGenerator(
            boolean linenumbers, boolean printExpr, VisitorIF<Value> driver,
            ConstantPool constants, SeaScope scope, CompilerFlags flags) {
        return new NodeSeaGenerator(driver, constants, scope.builder(ocean,
                flags), linenumbers, printExpr, this.constants);
    }

    public IntermediateCodeGenerator<Value, SeaScope, ValueCarrier> createClassCodeGenerator(
            boolean linenumbers, VisitorIF<Value> driver,
            ConstantPool constants, SeaScope scope, CompilerFlags flags) {
        return new NodeSeaGenerator(driver, constants, scope.builder(ocean,
                flags), linenumbers, false, this.constants);
    }

    public IntermediateCodeGenerator<Value, SeaScope, ValueCarrier> createFunctionCodeGenerator(
            boolean linenumbers, VisitorIF<Value> driver,
            ConstantPool constants, SeaScope scope, CompilerFlags flags) {
        return new NodeSeaGenerator(driver, constants, scope.builder(ocean,
                flags), linenumbers, false, this.constants);
    }

    public ConstantCodeGenerator<Value> createConstantCodeGenerator() {
        return new NodeSeaConstantGenerator(constantBuilder(), constants);
    }

    private final GraphBuilder constantBuilder() {
        return ocean.constants();
    }

    public SeaScope createGlobal(String[] locals, String[] cell,
            boolean hasStarImport) {
        return SeaScope.forModule(locals, cell, hasStarImport);
    }

    public SeaScope createClass(String name, String[] locals, String[] globals,
            String[] free, String[] cell, boolean hasStarImport) {
        return SeaScope.forClass(name, locals, globals, free, cell,
                hasStarImport);
    }

    public SeaScope createFunction(String name, String[] parameters,
            String[] locals, String[] globals, String[] free, String[] cell,
            boolean isGenerator, boolean hasStarImport) {
        if (isGenerator) {
            return SeaScope.forGenerator(name, parameters, locals, globals,
                    free, cell, hasStarImport);
        } else {
            return SeaScope.forFunction(name, parameters, locals, globals,
                    free, cell, hasStarImport);
        }
    }

    public void accept(SupergraphVisitor visitor) {
        ocean.accept(null, visitor);
    }
}
