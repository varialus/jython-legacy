package org.python.newcompiler.ast;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.python.antlr.PythonTree;
import org.python.antlr.ast.ClassDef;
import org.python.antlr.ast.Exec;
import org.python.antlr.ast.Expression;
import org.python.antlr.ast.FunctionDef;
import org.python.antlr.ast.GeneratorExp;
import org.python.antlr.ast.Global;
import org.python.antlr.ast.Import;
import org.python.antlr.ast.ImportFrom;
import org.python.antlr.ast.Interactive;
import org.python.antlr.ast.Lambda;
import org.python.antlr.ast.Module;
import org.python.antlr.ast.Name;
import org.python.antlr.ast.Suite;
import org.python.antlr.ast.VisitorBase;
import org.python.antlr.ast.Yield;
import org.python.antlr.ast.aliasType;
import org.python.antlr.ast.comprehensionType;
import org.python.antlr.ast.exprType;
import org.python.antlr.ast.stmtType;
import org.python.bytecode.Label;
import org.python.newcompiler.ClassEnvironment;
import org.python.newcompiler.Environment;
import org.python.newcompiler.EnvironmentHolder;
import org.python.newcompiler.EnvironmentInfo;
import org.python.newcompiler.FunctionEnvironment;
import org.python.newcompiler.Future;
import org.python.newcompiler.GlobalEnvironment;
import org.python.newcompiler.YieldPoint;
import org.python.parser.ParseException;

public class ContextBuilder extends VisitorBase<EnvironmentHolder> implements EnvironmentHolder {

    private final Environment globalEnvironment = new GlobalEnvironment();

    private Environment currentEnvironment = globalEnvironment;

    private Map<PythonTree, EnvironmentInfo> environmentMap = new HashMap<PythonTree, EnvironmentInfo>();

    private LinkedHashMap<exprType, YieldPoint> resumeTable = new LinkedHashMap<exprType, YieldPoint>();

    private int resumePoint = 0;

    public EnvironmentInfo getEnvironment(PythonTree tree) {
        return environmentMap.get(tree);
    }

    public YieldPoint getYieldPoint(exprType node) {
        return resumeTable.get(node);
    }

    private void addYieldPoint(exprType node) {
        Label label = new Label();
        currentEnvironment.addEntryPoint(label);
        resumeTable.put(node, new YieldPoint(label, resumePoint++));
    }

    public EnvironmentHolder visitClassDef(ClassDef node) throws Exception {
        // Class name
        currentEnvironment.addAssignment(node.name);
        // Class bases
        if (node.bases != null) {
            for (exprType expr : node.bases) {
                expr.accept(this);
            }
        }
        // Class body - in new environment
        Environment last = currentEnvironment;
        ClassEnvironment classEnvironment = new ClassEnvironment(currentEnvironment);
        environmentMap.put(node, classEnvironment);
        currentEnvironment = classEnvironment;
        try {
            for (stmtType stmt : node.body) {
                stmt.accept(this);
            }
        } finally {
            currentEnvironment = last;
        }
        return unhandled_node(node);
    }

    public EnvironmentHolder visitFunctionDef(FunctionDef node) throws Exception {
        // Function name
        currentEnvironment.addAssignment(node.name);
        // Function decorators
        if (node.decorators != null) {
            for (exprType decorator : node.decorators) {
                decorator.accept(this);
            }
        }
        // Argument defaults
        if (node.args != null && node.args.defaults != null) {
            for (exprType arg : node.args.defaults) {
                arg.accept(this);
            }
        }
        // In Function scope
        Environment last = currentEnvironment;
        FunctionEnvironment functionEnvironment = new FunctionEnvironment(currentEnvironment);
        environmentMap.put(node, functionEnvironment);
        currentEnvironment = functionEnvironment;
        try {
            // Function arguments
            if (node.args != null) {
                if (node.args.args != null) {
                    for (exprType arg : node.args.args) {
                        arg.accept(this);
                    }
                }
                if (node.args.vararg != null) {
                    functionEnvironment.setVarArg(node.args.vararg);
                }
                if (node.args.kwarg != null) {
                    functionEnvironment.setKeywordArg(node.args.kwarg);
                }
            }
            // Function body
            for (stmtType stmt : node.body) {
                stmt.accept(this);
            }
        } finally {
            currentEnvironment = last;
        }
        return unhandled_node(node);
    }

    public EnvironmentHolder visitLambda(Lambda node) throws Exception {
        // Argument defaults
        if (node.args != null && node.args.defaults != null) {
            for (exprType arg : node.args.defaults) {
                arg.accept(this);
            }
        }
        // In Lambda scope
        Environment last = currentEnvironment;
        FunctionEnvironment functionEnvironment = new FunctionEnvironment(currentEnvironment);
        environmentMap.put(node, functionEnvironment);
        currentEnvironment = functionEnvironment;
        try {
            // Lambda arguments
            if (node.args != null) {
                if (node.args.args != null) {
                    for (exprType arg : node.args.args) {
                        arg.accept(this);
                    }
                }
                if (node.args.vararg != null) {
                    functionEnvironment.setVarArg(node.args.vararg);
                }
                if (node.args.kwarg != null) {
                    functionEnvironment.setKeywordArg(node.args.kwarg);
                }
            }
            // Lambda body
            node.body.accept(this);
        } finally {
            currentEnvironment = last;
        }
        return unhandled_node(node);
    }

    public EnvironmentHolder visitGeneratorExp(GeneratorExp node) throws Exception {
        for (comprehensionType comp : node.generators) {
            comp.iter.accept(this);
        }
        Environment last = currentEnvironment;
        FunctionEnvironment functionEnvironment = new FunctionEnvironment(currentEnvironment);
        environmentMap.put(node, functionEnvironment);
        currentEnvironment = functionEnvironment;
        addYieldPoint(node);
        try {
            for (comprehensionType comp : node.generators) {
                comp.iter.accept(this);
                if (comp.ifs != null) {
                    for (exprType cond : comp.ifs) {
                        cond.accept(this);
                    }
                }
                comp.target.accept(this);
            }
            node.elt.accept(this);
        } finally {
            currentEnvironment = last;
        }
        return unhandled_node(node);
    }

    public EnvironmentHolder visitExec(Exec node) throws Exception {
        currentEnvironment.markHasExec();
        return super.visitExec(node);
    }

    public EnvironmentHolder visitGlobal(Global node) throws Exception {
        for (String name : node.names) {
            currentEnvironment.markAsGlobal(name);
        }
        return unhandled_node(node);
    }

    public EnvironmentHolder visitName(Name node) throws Exception {
        switch(node.ctx){
            case AugLoad:
                currentEnvironment.addReference(node.id);
                break;
            case AugStore:
                currentEnvironment.addAssignment(node.id);
                break;
            case Del:
                currentEnvironment.addReference(node.id);
                break;
            case Load:
                currentEnvironment.addReference(node.id);
                break;
            case Param:
                currentEnvironment.addParameter(node.id);
                break;
            case Store:
                currentEnvironment.addAssignment(node.id);
                break;
            default:
                throw new RuntimeException("Unknown name context " + node.ctx + " of '" + node.id
                        + "'.");
        }
        return unhandled_node(node);
    }

    public EnvironmentHolder visitImport(Import node) throws Exception {
        for (aliasType alias : node.names) {
            String[] names = alias.name.split(".");
            String name = (alias.asname != null) ? alias.asname : names[0];
            currentEnvironment.addAssignment(name);
        }
        return unhandled_node(node);
    }

    public EnvironmentHolder visitImportFrom(ImportFrom node) throws Exception {
        if (node.module.equals(Future.signature)) {
            if (node.names != null && node.names.length != 0) {
                for (aliasType alias : node.names) {
                    currentEnvironment.addFuture(alias.name);
                }
            } else {
                throw new ParseException("future statement does not support import *");
            }
        }
        if (node.names != null && node.names.length != 0) {
            for (aliasType alias : node.names) {
                String name = (alias.asname != null) ? alias.asname : alias.name;
                currentEnvironment.addAssignment(name);
            }
        } else {
            // Import star :(
        }
        return unhandled_node(node);
    }

    public EnvironmentHolder visitYield(Yield node) throws Exception {
        addYieldPoint(node);
        return super.visitYield(node);
    }

    public EnvironmentHolder visitInteractive(Interactive node) throws Exception {
        assertTopLevel("Interactive");
        environmentMap.put(node, (EnvironmentInfo)currentEnvironment);
        return super.visitInteractive(node);
    }

    public EnvironmentHolder visitModule(Module node) throws Exception {
        assertTopLevel("Module");
        environmentMap.put(node, (EnvironmentInfo)currentEnvironment);
        return super.visitModule(node);
    }

    public EnvironmentHolder visitExpression(Expression node) throws Exception {
        assertTopLevel("Expression");
        environmentMap.put(node, (EnvironmentInfo)currentEnvironment);
        return super.visitExpression(node);
    }

    public EnvironmentHolder visitSuite(Suite node) throws Exception {
        assertTopLevel("Suite");
        environmentMap.put(node, (EnvironmentInfo)currentEnvironment);
        return super.visitSuite(node);
    }

    private void assertTopLevel(String name) {
        if (currentEnvironment != globalEnvironment) {
            throw new RuntimeException(name + " encountered out of root level!");
        }
    }

    // Generic ones
    public void traverse(PythonTree node) throws Exception {
        node.traverse(this);
    }

    protected EnvironmentHolder unhandled_node(PythonTree node) throws Exception {
        return this;
    }
}
