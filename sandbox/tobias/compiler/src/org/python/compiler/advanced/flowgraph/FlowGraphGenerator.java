package org.python.compiler.advanced.flowgraph;

import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.Map;

import org.python.antlr.ParseException;
import org.python.antlr.PythonTree;
import org.python.antlr.ast.Assert;
import org.python.antlr.ast.Assign;
import org.python.antlr.ast.Attribute;
import org.python.antlr.ast.AugAssign;
import org.python.antlr.ast.BinOp;
import org.python.antlr.ast.BoolOp;
import org.python.antlr.ast.Break;
import org.python.antlr.ast.Call;
import org.python.antlr.ast.ClassDef;
import org.python.antlr.ast.Compare;
import org.python.antlr.ast.Continue;
import org.python.antlr.ast.Delete;
import org.python.antlr.ast.Dict;
import org.python.antlr.ast.Ellipsis;
import org.python.antlr.ast.ExceptHandler;
import org.python.antlr.ast.Exec;
import org.python.antlr.ast.Expr;
import org.python.antlr.ast.ExtSlice;
import org.python.antlr.ast.For;
import org.python.antlr.ast.FunctionDef;
import org.python.antlr.ast.GeneratorExp;
import org.python.antlr.ast.Global;
import org.python.antlr.ast.If;
import org.python.antlr.ast.IfExp;
import org.python.antlr.ast.Import;
import org.python.antlr.ast.ImportFrom;
import org.python.antlr.ast.Index;
import org.python.antlr.ast.Lambda;
import org.python.antlr.ast.List;
import org.python.antlr.ast.ListComp;
import org.python.antlr.ast.Name;
import org.python.antlr.ast.Num;
import org.python.antlr.ast.Pass;
import org.python.antlr.ast.Print;
import org.python.antlr.ast.Raise;
import org.python.antlr.ast.Repr;
import org.python.antlr.ast.Return;
import org.python.antlr.ast.Slice;
import org.python.antlr.ast.Str;
import org.python.antlr.ast.Subscript;
import org.python.antlr.ast.Suite;
import org.python.antlr.ast.TryExcept;
import org.python.antlr.ast.TryFinally;
import org.python.antlr.ast.Tuple;
import org.python.antlr.ast.UnaryOp;
import org.python.antlr.ast.VisitorBase;
import org.python.antlr.ast.VisitorIF;
import org.python.antlr.ast.While;
import org.python.antlr.ast.With;
import org.python.antlr.ast.Yield;
import org.python.antlr.ast.alias;
import org.python.antlr.ast.cmpopType;
import org.python.antlr.ast.comprehension;
import org.python.antlr.base.excepthandler;
import org.python.antlr.base.expr;
import org.python.antlr.ast.operatorType;
import org.python.antlr.base.slice;
import org.python.antlr.base.stmt;
import org.python.antlr.ast.unaryopType;
import org.python.compiler.advanced.Constant;
import org.python.compiler.advanced.ConstantPool;
import org.python.compiler.advanced.IntermediateCodeGenerator;
import org.python.compiler.advanced.ScopeInformation;
import org.python.compiler.flowgraph.BlockBuilder;
import org.python.compiler.flowgraph.BlockTermination;
import org.python.compiler.flowgraph.CodeGraph;
import org.python.compiler.flowgraph.ConsumerCallback;
import org.python.compiler.flowgraph.ProducerCallback;
import org.python.compiler.flowgraph.SimpleCallback;
import org.python.compiler.flowgraph.ValueOperation;
import org.python.compiler.flowgraph.Variable;
import org.python.compiler.flowgraph.VoidOperation;
import org.python.core.CodeFlag;
import org.python.core.CompilerFlags;

/**
 * @author Tobias Ivarsson
 */
public class FlowGraphGenerator
        extends
        IntermediateCodeGenerator<Variable, ScopeInformation, GeneratedCodeState<Variable>> {
    private static final Map<operatorType, ValueOperation> binaryOperators;
    static {
        Map<operatorType, ValueOperation> ops = new EnumMap<operatorType, ValueOperation>(
                operatorType.class);
        ops.put(operatorType.Add, ValueOperation.ADD);
        ops.put(operatorType.Sub, ValueOperation.SUBTRACT);
        ops.put(operatorType.Mult, ValueOperation.MULTIPLY);
        ops.put(operatorType.Div, ValueOperation.TRUE_DIVIDE);
        ops.put(operatorType.Mod, ValueOperation.MODULO);
        ops.put(operatorType.Pow, ValueOperation.POWER);
        ops.put(operatorType.LShift, ValueOperation.SHIFT_LEFT);
        ops.put(operatorType.RShift, ValueOperation.SHIFT_RIGHT);
        ops.put(operatorType.BitOr, ValueOperation.OR);
        ops.put(operatorType.BitXor, ValueOperation.XOR);
        ops.put(operatorType.BitAnd, ValueOperation.AND);
        ops.put(operatorType.FloorDiv, ValueOperation.FLOOR_DIVIDE);
        binaryOperators = Collections.unmodifiableMap(ops);
    }
    private static final Map<operatorType, ValueOperation> augOperators;
    static {
        Map<operatorType, ValueOperation> ops = new EnumMap<operatorType, ValueOperation>(
                operatorType.class);
        ops.put(operatorType.Add, ValueOperation.AUG_ADD);
        ops.put(operatorType.Sub, ValueOperation.AUG_SUBTRACT);
        ops.put(operatorType.Mult, ValueOperation.AUG_MULTIPLY);
        ops.put(operatorType.Div, ValueOperation.AUG_TRUE_DIVIDE);
        ops.put(operatorType.Mod, ValueOperation.AUG_MODULO);
        ops.put(operatorType.Pow, ValueOperation.AUG_POWER);
        ops.put(operatorType.LShift, ValueOperation.AUG_SHIFT_LEFT);
        ops.put(operatorType.RShift, ValueOperation.AUG_SHIFT_RIGHT);
        ops.put(operatorType.BitOr, ValueOperation.AUG_OR);
        ops.put(operatorType.BitXor, ValueOperation.AUG_XOR);
        ops.put(operatorType.BitAnd, ValueOperation.AUG_AND);
        ops.put(operatorType.FloorDiv, ValueOperation.AUG_FLOOR_DIVIDE);
        augOperators = Collections.unmodifiableMap(ops);
    }
    private static final Map<unaryopType, ValueOperation> unaryOperators;
    static {
        Map<unaryopType, ValueOperation> ops = new EnumMap<unaryopType, ValueOperation>(
                unaryopType.class);
        ops.put(unaryopType.Invert, ValueOperation.INVERT);
        ops.put(unaryopType.Not, ValueOperation.NOT);
        ops.put(unaryopType.UAdd, ValueOperation.POSITIVE);
        ops.put(unaryopType.USub, ValueOperation.NEGATIVE);
        unaryOperators = ops;
    }
    private static final Map<cmpopType, ValueOperation> comparators;
    static {
        Map<cmpopType, ValueOperation> ops = new EnumMap<cmpopType, ValueOperation>(
                cmpopType.class);
        ops.put(cmpopType.Eq, ValueOperation.EQUAL);
        ops.put(cmpopType.NotEq, ValueOperation.NOT_EQUAL);
        ops.put(cmpopType.Lt, ValueOperation.LESS_THAN);
        ops.put(cmpopType.LtE, ValueOperation.LESS_THAN_OR_EQUAL_TO);
        ops.put(cmpopType.Gt, ValueOperation.GREATER_THAN);
        ops.put(cmpopType.GtE, ValueOperation.GREATER_THAN_OR_EQUAL_TO);
        ops.put(cmpopType.Is, ValueOperation.IS);
        ops.put(cmpopType.IsNot, ValueOperation.IS_NOT);
        ops.put(cmpopType.In, ValueOperation.CONTAINED_IN);
        ops.put(cmpopType.NotIn, ValueOperation.NOT_CONTAINED_IN);
        comparators = Collections.unmodifiableMap(ops);
    }
    private final CodeGraph graph;
    private final boolean printResult;
    private BlockBuilder current;
    private Variable exception;
    //private final Map<String, ConstantChecker<Variable>> constants;
    private final CompilerFlags flags;

    /**
     * @param printResult
     * @param compiler
     * @param constants
     * @param flags
     * @param graph
     */
    public FlowGraphGenerator(boolean printResult,
            VisitorIF<Variable> compiler,
            ConstantPool constants,
            CompilerFlags flags, CodeGraph graph) {
        super(compiler, constants);
        this.printResult = printResult;
        //this.constants = constants;
        this.graph = graph;
        this.flags = flags;
    }

    @Override
    protected Variable[] resultArray(int size) {
        return new Variable[size];
    }
    
    public Variable loadConstant(Constant constant) {
        // TODO Auto-generated method stub
        return null;
    }

    // Helper method

    private enum Generate implements SimpleCallback<Exception>,
            ConsumerCallback<Exception>, ProducerCallback<Exception> {
        NOTHING;
        public void generateBlock(BlockBuilder builder) {
        }

        public void generateConsumer(BlockBuilder builder, Variable variable) {
        }

        public Variable generateProducer(BlockBuilder builder) throws Exception {
            return null;
        }
    }

    private abstract class ManagedSimpleCallback implements
            SimpleCallback<Exception>, ProducerCallback<Exception> {
        public final Variable generateProducer(BlockBuilder builder)
                throws Exception {
            // Makes the ManagedSimpleCallback usable for if-statements
            generateBlock(builder);
            return null;
        }

        public final void generateBlock(BlockBuilder builder) throws Exception {
            BlockBuilder old = current;
            current = builder;
            try {
                generate();
            } finally {
                current = old;
            }
        }

        abstract void generate() throws Exception;
    }

    private abstract class ManagedConsumerCallback implements
            ConsumerCallback<Exception> {
        public final void generateConsumer(BlockBuilder builder,
                Variable variable) throws Exception {
            BlockBuilder old = current;
            current = builder;
            try {
                generate(variable);
            } finally {
                current = old;
            }
        }

        abstract void generate(Variable variable) throws Exception;
    }

    private abstract class ManagedProducerCallback implements
            ProducerCallback<Exception> {
        public final Variable generateProducer(BlockBuilder builder)
                throws Exception {
            BlockBuilder old = current;
            current = builder;
            try {
                return generate();
            } finally {
                current = old;
            }
        }

        abstract Variable generate() throws Exception;
    }

    private abstract class ExceptionHandler implements
            ConsumerCallback<Exception> {
        public void generateConsumer(BlockBuilder builder, Variable variable)
                throws Exception {
            BlockBuilder oldBuilder = current;
            Variable oldException = exception;
            current = builder;
            exception = variable;
            try {
                generate();
            } finally {
                current = oldBuilder;
                exception = oldException;
            }
        }

        abstract void generate() throws Exception;
    }

    private interface TraverseBody {
        Variable generate(IterativeTraverser traverser, Variable variable)
                throws Exception;
    }

    private Variable iterativeTraversal(TraverseBody body) throws Exception {
        return iterativeTraversal(body, null);
    }

    private Variable iterativeTraversal(TraverseBody body, Variable state)
            throws Exception {
        return new IterativeTraverser(body, 0, state).perform(null);
    }

    private final class IterativeTraverser implements
            SimpleCallback<Exception>, ConsumerCallback<Exception>,
            ProducerCallback<Exception> {
        private final TraverseBody generator;
        final int index;
        final Variable state;

        IterativeTraverser(TraverseBody generator, int index, Variable state) {
            this.generator = generator;
            this.index = index;
            this.state = state;
        }

        public void generateBlock(BlockBuilder builder) throws Exception {
            doGenerate(builder, null);
        }

        public void generateConsumer(BlockBuilder builder, Variable variable)
                throws Exception {
            doGenerate(builder, variable);
        }

        public Variable generateProducer(BlockBuilder builder) throws Exception {
            return doGenerate(builder, null);
        }

        private Variable doGenerate(BlockBuilder builder, Variable variable)
                throws Exception {
            BlockBuilder old = current;
            current = builder;
            try {
                return perform(variable);
            } finally {
                current = old;
            }
        }

        Variable perform(Variable variable) throws Exception {
            return generator.generate(this, variable);
        }

        IterativeTraverser next(Variable state) {
            return new IterativeTraverser(generator, index + 1, state);
        }
    }

    private Variable loadClosure(ScopeInformation scope) {
        java.util.List<Variable> freeVars = new LinkedList<Variable>();
        for (String free : scope.freeVariables()) {
            freeVars.add(graph.getVariable(free));
        }
        Variable[] closure = freeVars.toArray(new Variable[] {});
        return current.valueOperation(ValueOperation.GROUP_VARIABLES, closure);
    }

    private Variable loadDefaults(java.util.List<expr> defaults)
            throws Exception {
        Variable[] vars = evaluate(defaults);
        return current.valueOperation(ValueOperation.GROUP_VARIABLES, vars);
    }

    private Variable[] loadDecorators(java.util.List<expr> decorators)
            throws Exception {
        Variable[] loaded = evaluate(decorators);
        // reverse the result, decorators are declared and loaded top-down,
        // but applied bottom-up
        Variable[] result = new Variable[loaded.length];
        for (int i = 0; i < loaded.length; i++) {
            result[result.length - i - 1] = loaded[i];
        }
        return result;
    }

    private Variable applyDecorators(Variable target, Variable[] decorators) {
        if (decorators == null || decorators.length == 0) {
            return target;
        }
        for (Variable decorator : decorators) {
            target = current.valueOperation(ValueOperation.CALL, decorator,
                    target);
        }
        return target;
    }

    /*
    private void compile(java.util.List<stmt> body) throws Exception {
        if (body == null || body.isEmpty()) {
            return;
        }
        for (stmt stmt : body) {
            stmt.accept(compiler);
            if (!current.isOpen()) {
                return;
            }
        }
    }
    */

    private class Assignment extends VisitorBase<Void> {
        private final Variable value;

        Assignment(Variable value) {
            this.value = value;
        }

        @Override
        public void traverse(PythonTree node) throws Exception {
        }

        @Override
        protected Void unhandled_node(PythonTree node) throws Exception {
            throw new IllegalArgumentException("Cannot assign to "
                    + node.getClass().getName());
        }

        @Override
        public Void visitName(Name node) throws Exception {
            current.voidOperation(VoidOperation.STORE,
                    graph.getVariable(node.getInternalId()), value);
            return null;
        }

        private void tupleAssign(java.util.List<expr> elts) throws Exception {
            int before = -1, after = 0;
            for (expr elt : elts) {
                if (false /*elt instanceof Starred*/) {
                    // For 3.0 compatibility, where one can have starred targets
                    if (before == -1) {
                        before = after;
                        after = 0;
                    } else {
                        throw new ParseException(
                                "more than one starred expressions in assignment",
                                elt);
                    }
                } else {
                    after++;
                }
            }
            Variable unpacked;
            if (before != -1) {
                unpacked = current.valueOperation(ValueOperation.UNPACK, value,
                        graph.getConstant(before), graph.getConstant(after));
            } else {
                unpacked = current.valueOperation(ValueOperation.UNPACK, value,
                        graph.getConstant(after));
            }
            int i = 0;
            if (before != -1) {
                for (; i < before; i++) {
                    Variable value = current.valueOperation(
                            ValueOperation.GROUP_GET, unpacked,
                            graph.getConstant(i));
                    elts.get(i).accept(new Assignment(value));
                }
                Variable value = current.valueOperation(
                        ValueOperation.GROUP_GET, unpacked,
                        graph.getConstant(i));
                elts.get(i++)/* TODO: cast elts[i++] to Starred */
                /* and pick out the value element */.accept(
                        new Assignment(value));
            }
            before += 1; // either this was -1, or we consumed the extra starred
            for (; i < before + after; i++) {
                Variable value = current.valueOperation(
                        ValueOperation.GROUP_GET, unpacked,
                        graph.getConstant(i));
                elts.get(i).accept(new Assignment(value));
            }
        }

        @Override
        public Void visitTuple(Tuple node) throws Exception {
            tupleAssign(node.getInternalElts());
            return null;
        }

        @Override
        public Void visitList(List node) throws Exception {
            tupleAssign(node.getInternalElts());
            return null;
        }

        @Override
        public Void visitDict(Dict node) throws Exception {
            // Dictionary assignment is easy enough, once the syntax supports it
            for (int i = 0; i < node.getInternalKeys().size(); i++) {
                Variable key = evaluate(node.getInternalKeys().get(i));
                Variable value = current.valueOperation(
                        ValueOperation.LOAD_ATTRIBUTE, this.value, key);
                node.getInternalValues().get(i).accept(new Assignment(value));
            }
            return null;
        }

        @Override
        public Void visitAttribute(Attribute node) throws Exception {
            Variable target = evaluate(node.getInternalValue());
            current.voidOperation(VoidOperation.STORE_ATTRIBUTE, target,
                    graph.getConstant(node.getInternalAttr()), value);
            return null;
        }

        @Override
        public Void visitSubscript(Subscript node) throws Exception {
            Variable target = evaluate(node.getInternalValue());
            Variable slice = evaluate(node.getInternalSlice());
            current.voidOperation(VoidOperation.STORE_ITEM, target, slice,
                    value);
            return null;
        }
    }

    private final VisitorIF<Void> delete = new VisitorBase<Void>() {
        @Override
        public void traverse(PythonTree node) throws Exception {
        }

        @Override
        protected Void unhandled_node(PythonTree node) throws Exception {
            throw new IllegalArgumentException("Cannot delete "
                    + node.getClass().getName());
        }

        @Override
        public Void visitName(Name node) throws Exception {
            current.voidOperation(VoidOperation.DELETE,
                    graph.getConstant(node.getInternalId()));
            return null;
        }

        @Override
        public Void visitAttribute(Attribute node) throws Exception {
            Variable target = evaluate(node.getInternalValue());
            current.voidOperation(VoidOperation.DELETE_ATTRIBUTE, target,
                    graph.getConstant(node.getInternalAttr()));
            return null;
        }

        @Override
        public Void visitSubscript(Subscript node) throws Exception {
            Variable target = evaluate(node.getInternalValue());
            Variable slice = evaluate(node.getInternalSlice());
            current.voidOperation(VoidOperation.DELETE_ITEM, target, slice);
            return null;
        }
    };

    // -- Entry points --

    // Class

    public GeneratedCodeState<Variable> beforeClassDef(ClassDef node,
            ScopeInformation scope) throws Exception {
        Variable name = graph.getConstant(node.getInternalName());
        Variable[] baseVars;
        if (node.getInternalName() != null) {
            baseVars = new Variable[node.getInternalBases().size()];
            int i = 0;
            for (expr base : node.getInternalBases()) {
                baseVars[i] = base.accept(this);
            }
        } else {
            baseVars = new Variable[0];
        }
        Variable bases = current.valueOperation(ValueOperation.GROUP_VARIABLES,
                baseVars);
        return new GeneratedCodeState<Variable>(name, bases,
                loadClosure(scope), new Variable[] {});
    }

    @Override
    public Variable visitClassDef(final ClassDef node) throws Exception {
        graph.generateCode(new ManagedSimpleCallback() {
            @Override
            public void generate() throws Exception {
                compile(node.getInternalBody());
            }
        });
        return graph.handle();
    }

    public void afterClassDef(ClassDef node,
            GeneratedCodeState<Variable> state, Variable result)
            throws Exception {
        Variable dict = current.valueOperation(ValueOperation.CALL_CODE,
                result, state.classClosure());
        Variable klass = current.valueOperation(ValueOperation.DEFINE_CLASS,
                state.name(), state.classBases(), dict);
        klass = applyDecorators(klass, state.decorators());
        current.voidOperation(VoidOperation.STORE, state.name(), klass);
    }

    // Function

    public GeneratedCodeState<Variable> beforeFunctionDef(FunctionDef node,
            ScopeInformation scope) throws Exception {
        Variable name = graph.getConstant(node.getInternalName());
        Variable[] decorators = loadDecorators(node.getInternalDecorator_list());
        return new GeneratedCodeState<Variable>(name, loadClosure(scope),
                loadDefaults(node.getInternalArgs().getInternalDefaults()),
                decorators);
    }

    @Override
    public Variable visitFunctionDef(final FunctionDef node) throws Exception {
        graph.generateCode(new ManagedSimpleCallback() {
            @Override
            public void generate() throws Exception {
                compile(node.getInternalBody());
            }
        });
        return graph.handle();
    }

    public void afterFunctionDef(FunctionDef node,
            GeneratedCodeState<Variable> state, Variable result)
            throws Exception {
        Variable function = current.valueOperation(
                ValueOperation.MAKE_FUNCTION, result, state.funcClosure(),
                state.funcDefaults());
        function = applyDecorators(function, state.decorators());
        current.voidOperation(VoidOperation.STORE, state.name(), function);
    }

    // Lambda

    public GeneratedCodeState<Variable> beforeLambda(Lambda node,
            ScopeInformation scope) throws Exception {
        return new GeneratedCodeState<Variable>(null, loadClosure(scope),
                loadDefaults(node.getInternalArgs().getInternalDefaults()));
    }

    @Override
    public Variable visitLambda(final Lambda node) throws Exception {
        graph.generateCode(new ManagedSimpleCallback() {
            @Override
            public void generate() throws Exception {
                current.terminate(new BlockTermination.Return(
                        compile(node.getInternalBody())));
            }
        });
        return graph.handle();
    }

    public Variable afterLambda(Lambda node,
            GeneratedCodeState<Variable> state, Variable result)
            throws Exception {
        return current.valueOperation(ValueOperation.MAKE_FUNCTION, result,
                state.funcClosure(), state.funcDefaults());
    }

    // Generator

    public GeneratedCodeState<Variable> beforeGeneratorExp(GeneratorExp node,
            ScopeInformation scope) throws Exception {
        Variable startArg = evaluate(node.getInternalGenerators().get(0).getInternalIter());
        startArg = current.valueOperation(ValueOperation.GET_ITERATOR, startArg);
        return new GeneratedCodeState<Variable>(null, loadClosure(scope),
                startArg);
    }

    @Override
    public Variable visitGeneratorExp(final GeneratorExp node) throws Exception {
        graph.generateCode(new ManagedConsumerCallback() {
            @Override
            public void generate(Variable argument) throws Exception {
                Variable iter = current.valueOperation(ValueOperation.LOAD,
                        argument);
                traverseComprehension(node.getInternalGenerators(), iter,
                        new ManagedSimpleCallback() {
                            @Override
                            public void generate() throws Exception {
                                current.performYield(evaluate(node.getInternalElt()));
                            }
                        });
            }
        });
        return graph.handle();
    }

    public Variable afterGeneratorExp(GeneratorExp node,
            GeneratedCodeState<Variable> state, Variable result)
            throws Exception {
        return current.valueOperation(ValueOperation.CALL_CODE, result,
                state.funcClosure(), state.generatorStartArg());
    }

    private static final class ReturnVariable implements
            ProducerCallback<Exception> {
        private final Variable result;

        ReturnVariable(Variable result) {
            this.result = result;
        }

        public Variable generateProducer(BlockBuilder builder) {
            return result;
        }
    }

    private void traverseComprehension(
            final java.util.List<comprehension> generators,
            final Variable iter, final ManagedSimpleCallback callback)
            throws Exception {
        IterativeTraverser it = new IterativeTraverser(new TraverseBody() {
            public Variable generate(final IterativeTraverser traverser,
                    Variable variable) throws Exception {
                comprehension generator = generators.get(traverser.index);
                generator.getInternalTarget().accept(new Assignment(variable));
                final ProducerCallback<Exception> next;
                if (traverser.index + 1 == generators.size()) {
                    next = callback;
                } else {
                    next = null;
                }
                if (generator.getInternalIfs() != null
                        && !generator.getInternalIfs().isEmpty()) {
                    final java.util.List<expr> ifs = generator.getInternalIfs();
                    iterativeTraversal(new TraverseBody() {
                        public Variable generate(IterativeTraverser traverser,
                                Variable variable) throws Exception {
                            Variable result = evaluate(ifs.get(traverser.index));
                            if (traverser.index + 1 == ifs.size()) {
                                return current.performSelection(result, next,
                                        Generate.NOTHING);
                            } else {
                                return current.performSelection(result,
                                        traverser.next(null), Generate.NOTHING);
                            }
                        }
                    });
                } else if (next == callback) {
                    callback.generate();
                } else {
                    current.performIteratorLoop(new ManagedProducerCallback() {
                        @Override
                        Variable generate() throws Exception {
                            return evaluate(generators.get(traverser.index + 1).getInternalIter());
                        }
                    }, traverser.next(null), Generate.NOTHING);
                }
                return null;
            }
        }, 0, null);
        current.performIteratorLoop(new ReturnVariable(iter), it,
                Generate.NOTHING);
    }

    @Override
    public Variable visitFor(final For node) throws Exception {
        current.performIteratorLoop(new ManagedProducerCallback() {
            @Override
            Variable generate() throws Exception {
                return evaluate(node.getInternalIter());
            }
        }, new ManagedConsumerCallback() {
            @Override
            void generate(Variable variable) throws Exception {
                node.getInternalTarget().accept(new Assignment(variable));
                compile(node.getInternalBody());
            }
        }, new ManagedSimpleCallback() {
            @Override
            void generate() throws Exception {
                compile(node.getInternalOrelse());
            }
        });
        return null;
    }

    @Override
    public Variable visitWhile(final While node) throws Exception {
        current.performBooleanLoop(new ManagedProducerCallback() {
            @Override
            Variable generate() throws Exception {
                return evaluate(node.getInternalTest());
            }
        }, new ManagedSimpleCallback() {
            @Override
            void generate() throws Exception {
                compile(node.getInternalBody());
            }
        }, new ManagedSimpleCallback() {
            @Override
            void generate() throws Exception {
                compile(node.getInternalOrelse());
            }
        });
        return null;
    }

    @Override
    public Variable visitBreak(Break node) throws Exception {
        current.terminate(BlockTermination.BREAK);
        return null;
    }

    @Override
    public Variable visitContinue(Continue node) throws Exception {
        current.terminate(BlockTermination.CONTINUE);
        return null;
    }

    @Override
    public Variable visitListComp(final ListComp node) throws Exception {
        final Variable list = current.valueOperation(ValueOperation.MAKE_LIST);
        Variable iterVal = evaluate(node.getInternalGenerators().get(0).getInternalIter());
        Variable iter = current.valueOperation(ValueOperation.GET_ITERATOR,
                iterVal);
        traverseComprehension(node.getInternalGenerators(), iter,
                new ManagedSimpleCallback() {
                    @Override
                    void generate() throws Exception {
                        current.voidOperation(VoidOperation.LIST_APPEND, list,
                                evaluate(node.getInternalElt()));
                    }
                });
        return list;
    }

    // Selection

    @Override
    public Variable visitIf(final If node) throws Exception {
        current.performSelection(evaluate(node.getInternalTest()),
                new ManagedSimpleCallback() {
                    @Override
                    void generate() throws Exception {
                        compile(node.getInternalBody());
                    }
                }, new ManagedSimpleCallback() {
                    @Override
                    void generate() throws Exception {
                        compile(node.getInternalOrelse());
                    }
                });
        return null;
    }

    @Override
    public Variable visitIfExp(final IfExp node) throws Exception {
        return current.performSelection(evaluate(node.getInternalTest()),
                new ManagedProducerCallback() {
                    @Override
                    Variable generate() throws Exception {
                        return evaluate(node.getInternalBody());
                    }
                }, new ManagedProducerCallback() {
                    @Override
                    Variable generate() throws Exception {
                        return evaluate(node.getInternalOrelse());
                    }
                });
    }

    @Override
    public Variable visitBoolOp(final BoolOp node) throws Exception {
        final boolean expandTrue;
        switch (node.getInternalOp()) {
        case And:
            expandTrue = true;
            break;
        case Or:
            expandTrue = false;
            break;
        default:
            throw new ParseException("Unknown boolean operation "
                    + node.getInternalOp().name(), node);
        }
        return iterativeTraversal(new TraverseBody() {
            public Variable generate(IterativeTraverser traverser,
                    Variable variable) throws Exception {
                Variable result = evaluate(node.getInternalValues().get(
                        traverser.index));
                if (traverser.index + 1 == node.getInternalValues().size()) {
                    return result;
                } else if (expandTrue) {
                    return current.performSelection(result,
                            traverser.next(null), new ReturnVariable(result));
                } else {
                    return current.performSelection(result, new ReturnVariable(
                            result), traverser.next(null));
                }
            }
        });
    }

    @Override
    public Variable visitCompare(final Compare node) throws Exception {
        Variable left = evaluate(node.getInternalLeft());
        if (node.getInternalComparators().size() == 1) {
            Variable right = evaluate(node.getInternalComparators().get(0));
            return current.valueOperation(
                    comparators.get(node.getInternalOps().get(0)), left, right);
        } else {
            return iterativeTraversal(new TraverseBody() {
                public Variable generate(IterativeTraverser traverser,
                        Variable variable) throws Exception {
                    Variable next = evaluate(node.getInternalComparators().get(
                            traverser.index));
                    Variable result = current.valueOperation(
                            comparators.get(node.getInternalOps().get(0)),
                            traverser.state, next);
                    if (traverser.index + 1 == node.getInternalComparators().size()) {
                        return result;
                    } else {
                        return current.performSelection(result,
                                traverser.next(next),
                                new ReturnVariable(result));
                    }
                }
            }, left);
        }
    }

    // Assignment

    @Override
    public Variable visitAssign(Assign node) throws Exception {
        Variable value = evaluate(node.getInternalValue());
        for (expr target : node.getInternalTargets()) {
            target.accept(new Assignment(value));
        }
        return null;
    }

    @Override
    public Variable visitAugAssign(AugAssign node) throws Exception {
        ValueOperation op;
        if (node.getInternalOp() == operatorType.Div) {
            op = flags.isFlagSet(CodeFlag.CO_FUTURE_DIVISION) ? ValueOperation.AUG_TRUE_DIVIDE
                    : ValueOperation.AUG_FLOOR_DIVIDE;
        } else {
            op = augOperators.get(node.getInternalOp());
        }
        if (node.getInternalTarget() instanceof Name) {
            Name name = (Name) node.getInternalTarget();
            Variable lhs = evaluate(name);
            Variable rhs = evaluate(node.getInternalValue());
            Variable value = current.valueOperation(op, lhs, rhs);
            current.voidOperation(VoidOperation.STORE,
                    graph.getVariable(name.getInternalId()), value);
        } else if (node.getInternalTarget() instanceof Attribute) {
            Attribute attribute = (Attribute) node.getInternalTarget();
            Variable owner = evaluate(attribute.getInternalValue());
            Variable attr = graph.getConstant(attribute.getInternalAttr());
            Variable lhs = current.valueOperation(
                    ValueOperation.LOAD_ATTRIBUTE, owner, attr);
            Variable rhs = evaluate(node.getInternalValue());
            Variable value = current.valueOperation(op, lhs, rhs);
            current.voidOperation(VoidOperation.STORE_ATTRIBUTE, owner, attr,
                    value);
        } else if (node.getInternalTarget() instanceof Subscript) {
            Subscript sub = (Subscript) node.getInternalTarget();
            Variable owner = evaluate(sub.getInternalValue());
            Variable slice = evaluate(sub.getInternalSlice());
            Variable lhs = current.valueOperation(ValueOperation.LOAD_ITEM,
                    owner, slice);
            Variable rhs = evaluate(node.getInternalValue());
            Variable value = current.valueOperation(op, lhs, rhs);
            current.voidOperation(VoidOperation.STORE_ITEM, owner, slice, value);
        } else {
            throw new ParseException(
                    "illegal expression for augmented assignment");
        }
        return null;
    }

    // Deletion

    @Override
    public Variable visitDelete(Delete node) throws Exception {
        node.traverse(delete);
        return null;
    }

    // Call

    @Override
    public Variable visitCall(Call node) throws Exception {
        Variable func = evaluate(node.getInternalFunc());
        Variable[] args = new Variable[node.getInternalArgs().size()];
        for (int i = 0; i < node.getInternalArgs().size(); i++) {
            args[i] = evaluate(node.getInternalArgs().get(i));
        }
        Variable arguments = current.valueOperation(
                ValueOperation.GROUP_VARIABLES, args);
        Variable[] keywords = new Variable[node.getInternalKeywords().size()];
        for (int i = 0; i < node.getInternalKeywords().size(); i++) {
            Variable arg = graph.getConstant(node.getInternalKeywords().get(i).getInternalArg());
            Variable value = evaluate(node.getInternalKeywords().get(i).getInternalValue());
            keywords[i] = current.valueOperation(
                    ValueOperation.MAKE_KEYWORD_TUPLE, arg, value);
        }
        Variable keywordArguments = current.valueOperation(
                ValueOperation.GROUP_VARIABLES, keywords);
        Variable starargs = node.getInternalStarargs() != null ? evaluate(node.getInternalStarargs())
                : CodeGraph.Void();
        Variable kwargs = node.getInternalKwargs() != null ? evaluate(node.getInternalKwargs())
                : CodeGraph.Void();
        return current.valueOperation(ValueOperation.CALL_FUNCTION, func,
                arguments, keywordArguments, starargs, kwargs);
    }

    // Exec

    @Override
    public Variable visitExec(Exec node) throws Exception {
        Variable body = evaluate(node.getInternalBody());
        Variable globals;
        Variable locals;
        if (node.getInternalGlobals() != null) {
            globals = evaluate(node.getInternalGlobals());
            if (node.getInternalLocals() != null) {
                locals = evaluate(node.getInternalLocals());
            } else {
                locals = globals;
            }
        } else {
            globals = graph.globals();
            locals = graph.locals();
        }
        return current.valueOperation(ValueOperation.EXEC, body, globals,
                locals);
    }

    // Builtin types

    @Override
    public Variable visitDict(Dict node) throws Exception {
        Variable[] content = new Variable[node.getInternalKeys().size()];
        for (int i = 0; i < node.getInternalKeys().size(); i++) {
            Variable value = evaluate(node.getInternalValues().get(i));
            content[i] = current.valueOperation(
                    ValueOperation.MAKE_KEYWORD_TUPLE,
                    graph.getConstant(node.getInternalKeys().get(i)), value);
        }
        return current.valueOperation(ValueOperation.MAKE_DICTIONARY, content);
    }

    @Override
    public Variable visitEllipsis(Ellipsis node) throws Exception {
        return CodeGraph.Ellipsis();
    }

    @Override
    public Variable visitTuple(Tuple node) throws Exception {
        Variable[] elts = new Variable[node.getInternalElts().size()];
        for (int i = 0; i < node.getInternalElts().size(); i++) {
            elts[i] = evaluate(node.getInternalElts().get(i));
        }
        return current.valueOperation(ValueOperation.MAKE_TUPLE, elts);
    }

    @Override
    public Variable visitList(List node) throws Exception {
        Variable[] elts = new Variable[node.getInternalElts().size()];
        for (int i = 0; i < node.getInternalElts().size(); i++) {
            elts[i] = evaluate(node.getInternalElts().get(i));
        }
        return current.valueOperation(ValueOperation.MAKE_LIST, elts);
    }

    // Imports

    @Override
    public Variable visitImport(Import node) throws Exception {
        for (alias al : node.getInternalNames()) {
            Variable name = graph.getConstant(al.getInternalName());
            Variable module = current.valueOperation(ValueOperation.IMPORT,
                    name);
            Variable asname = al.getInternalAsname() != null ? graph.getConstant(al.getInternalAsname())
                    : name;
            current.voidOperation(VoidOperation.STORE, asname, module);
        }
        return null;
    }

    @Override
    public Variable visitImportFrom(ImportFrom node) throws Exception {
        Variable moduleName = graph.getConstant(node.getInternalModule());
        Variable[] nameVars = new Variable[node.getInternalNames().size()];
        for (int i = 0; i < node.getInternalNames().size(); i++) {
            nameVars[i] = graph.getConstant(node.getInternalNames().get(i).getInternalName());
        }
        Variable names = current.valueOperation(ValueOperation.GROUP_VARIABLES,
                nameVars);
        Variable module;
        if (flags.isFlagSet(CodeFlag.CO_FUTURE_ABSOLUTE_IMPORT)) {
            module = current.valueOperation(ValueOperation.IMPORT_ABSOLUTE,
                    moduleName, graph.getConstant(node.getInternalLevel()),
                    names);
        } else {
            module = current.valueOperation(ValueOperation.IMPORT, moduleName,
                    names);
        }
        for (int i = 0; i < node.getInternalNames().size(); i++) {
            Variable child = current.valueOperation(ValueOperation.IMPORT_FROM,
                    module, nameVars[i]);
            String name = node.getInternalNames().get(i).getInternalAsname() != null ? node.getInternalNames().get(
                    i).getInternalAsname()
                    : node.getInternalNames().get(i).getInternalName();
            current.voidOperation(VoidOperation.STORE, graph.getVariable(name),
                    child);
        }
        return null;
    }

    // Print

    @Override
    public Variable visitPrint(Print node) throws Exception {
        Variable[] variables;
        VoidOperation print;
        int i = 0;
        if (node.getInternalDest() != null) {
            variables = new Variable[node.getInternalValues().size() + 1];
            variables[i++] = evaluate(node.getInternalDest());
            print = node.getInternalNl() ? VoidOperation.PRINT_TO_WITH_NEWLINE
                    : VoidOperation.PRINT_TO;
        } else {
            variables = new Variable[node.getInternalValues().size()];
            print = node.getInternalNl() ? VoidOperation.PRINT_WITH_NEWLINE
                    : VoidOperation.PRINT;
        }
        for (int j = 0; j < node.getInternalValues().size(); j++) {
            variables[i++] = evaluate(node.getInternalValues().get(j));
        }
        current.voidOperation(print, variables);
        return null;
    }

    // Simple statements

    @Override
    public Variable visitAssert(Assert node) throws Exception {
        Variable test = evaluate(node.getInternalTest());
        Variable msg = evaluate(node.getInternalMsg());
        current.voidOperation(VoidOperation.ASSERT, test, msg);
        return null;
    }

    @Override
    public Variable visitExpr(Expr node) throws Exception {
        Variable result = evaluate(node.getInternalValue());
        if (printResult) {
            current.voidOperation(VoidOperation.PRINT_WITH_NEWLINE, result);
        }
        return null;
    }

    @Override
    public Variable visitPass(Pass node) throws Exception {
        return null;
    }

    // Simple expressions 

    @Override
    public Variable visitBinOp(BinOp node) throws Exception {
        Variable left = evaluate(node.getInternalLeft());
        Variable right = evaluate(node.getInternalRight());
        ValueOperation op;
        if (node.getInternalOp() == operatorType.Div) {
            op = flags.isFlagSet(CodeFlag.CO_FUTURE_DIVISION) ? ValueOperation.TRUE_DIVIDE
                    : ValueOperation.FLOOR_DIVIDE;
        } else {
            op = binaryOperators.get(node.getInternalOp());
        }
        return current.valueOperation(op, left, right);
    }

    @Override
    public Variable visitAttribute(Attribute node) throws Exception {
        Variable target = evaluate(node.getInternalValue());
        return current.valueOperation(ValueOperation.LOAD_ATTRIBUTE, target,
                graph.getConstant(node.getInternalAttr()));
    }

    @Override
    public Variable visitName(Name node) throws Exception {
        //ConstantChecker<Variable> constant = constants.get(node.getInternalId());
        //if (constant != null) {
        //    return constant.getConstantValue();
        //} else {
        return current.valueOperation(ValueOperation.LOAD,
                graph.getVariable(node.getInternalId()));
        //}
    }

    @Override
    public Variable visitNum(Num node) throws Exception {
        return graph.getConstant(node.getInternalN());
    }

    // Slices

    @Override
    public Variable visitExtSlice(ExtSlice node) throws Exception {
        Variable dims[] = new Variable[node.getInternalDims().size()];
        for (int i = 0; i < node.getInternalDims().size(); i++) {
            dims[i] = evaluate(node.getInternalDims().get(i));
        }
        return current.valueOperation(ValueOperation.MAKE_EXTENDED_SLICE, dims);
    }

    @Override
    public Variable visitIndex(Index node) throws Exception {
        return evaluate(node.getInternalValue());
    }

    @Override
    public Variable visitSlice(Slice node) throws Exception {
        Variable lower = node.getInternalLower() != null ? evaluate(node.getInternalLower())
                : CodeGraph.None();
        Variable upper = node.getInternalUpper() != null ? evaluate(node.getInternalUpper())
                : CodeGraph.None();
        Variable step = node.getInternalStep() != null ? evaluate(node.getInternalStep())
                : CodeGraph.None();
        return current.valueOperation(ValueOperation.MAKE_SLICE, lower, upper,
                step);
    }

    @Override
    public Variable visitRaise(Raise node) throws Exception {
        if (node.getInternalType() == null) {
            current.terminate(new BlockTermination.Raise(exception));
        } else {
            Variable type = evaluate(node.getInternalType());
            Variable instance = node.getInternalInst() != null ? evaluate(node.getInternalInst())
                    : CodeGraph.Void();
            Variable traceback = node.getInternalTback() != null ? evaluate(node.getInternalTback())
                    : CodeGraph.Void();
            Variable exception = current.valueOperation(
                    ValueOperation.MAKE_EXCEPTION, type, instance, traceback);
            current.terminate(new BlockTermination.Raise(exception));
        }
        return null;
    }

    @Override
    public Variable visitReturn(Return node) throws Exception {
        current.terminate(new BlockTermination.Return(
                evaluate(node.getInternalValue())));
        return null;
    }

    @Override
    public Variable visitRepr(Repr node) throws Exception {
        Variable value = evaluate(node.getInternalValue());
        return current.valueOperation(ValueOperation.REPR, value);
    }

    @Override
    public Variable visitStr(Str node) throws Exception {
        // TODO: enforce string type
        return graph.getConstant(node.getInternalS());
    }

    @Override
    public Variable visitSubscript(Subscript node) throws Exception {
        Variable value = evaluate(node.getInternalValue());
        Variable slice = evaluate(node.getInternalSlice());
        return current.valueOperation(ValueOperation.LOAD_ITEM, value, slice);
    }

    @Override
    public Variable visitTryExcept(final TryExcept node) throws Exception {
        final TraverseBody handlers = new TraverseBody() {
            public Variable generate(IterativeTraverser traverser,
                    Variable variable) throws Exception {
                if (traverser.index == node.getInternalHandlers().size()) {
                    // No more handlers, re-raise the exception
                    current.terminate(new BlockTermination.Raise(exception));
                    return null;
                } else if (((ExceptHandler) node.getInternalHandlers().get(
                        traverser.index)).getInternalType() == null) {
                    // Matches anything
                    ExceptHandler handler = (ExceptHandler) node.getInternalHandlers().get(
                            traverser.index);
                    if (handler.getInternalName() != null) {
                        Variable value = current.valueOperation(
                                ValueOperation.EXCEPTION_VALUE, exception);
                        handler.getInternalName().accept(new Assignment(value));
                    }
                    compile(handler.getInternalBody());
                    return null;
                } else {
                    final ExceptHandler handler = (ExceptHandler) node.getInternalHandlers().get(
                            traverser.index);
                    Variable match;
                    if (handler.getInternalType() instanceof Tuple) {
                        final Tuple tuple = (Tuple) handler.getInternalType();
                        match = iterativeTraversal(new TraverseBody() {
                            public Variable generate(
                                    IterativeTraverser traverser,
                                    Variable variable) throws Exception {
                                Variable type = evaluate(tuple.getInternalElts().get(
                                        traverser.index));
                                Variable match = current.valueOperation(
                                        ValueOperation.MATCH_EXCEPTION, type,
                                        exception);
                                return current.performSelection(match,
                                        new ReturnVariable(match),
                                        traverser.next(null));
                            }
                        });
                    } else {
                        Variable type = evaluate(handler.getInternalType());
                        match = current.valueOperation(
                                ValueOperation.MATCH_EXCEPTION, type, exception);
                    }
                    return current.performSelection(match,
                            new ManagedProducerCallback() {
                                @Override
                                Variable generate() throws Exception {
                                    if (handler.getInternalName() != null) {
                                        Variable value = current.valueOperation(
                                                ValueOperation.EXCEPTION_VALUE,
                                                exception);
                                        handler.getInternalName().accept(
                                                new Assignment(value));
                                    }
                                    compile(handler.getInternalBody());
                                    return null;
                                }
                            }, traverser.next(null));
                }
            }
        };
        current.performTryCatch(new ManagedSimpleCallback() {
            @Override
            void generate() throws Exception {
                compile(node.getInternalBody());
            }
        }, new ExceptionHandler() {
            @Override
            void generate() throws Exception {
                iterativeTraversal(handlers);
            }
        }, new ManagedSimpleCallback() {
            @Override
            void generate() throws Exception {
                compile(node.getInternalOrelse());
            }
        });
        return null;
    }

    @Override
    public Variable visitTryFinally(final TryFinally node) throws Exception {
        current.performTryFinally(new ManagedSimpleCallback() {
            @Override
            void generate() throws Exception {
                compile(node.getInternalBody());
            }
        }, new ManagedSimpleCallback() {
            @Override
            void generate() throws Exception {
                compile(node.getInternalFinalbody());
            }
        });
        return null;
    }

    @Override
    public Variable visitUnaryOp(UnaryOp node) throws Exception {
        Variable operand = evaluate(node.getInternalOperand());
        return current.valueOperation(unaryOperators.get(node.getInternalOp()),
                operand);
    }

    @Override
    public Variable visitWith(final With node) throws Exception {
        Variable context = evaluate(node.getInternalContext_expr());
        final Variable exit = current.valueOperation(
                ValueOperation.LOAD_ATTRIBUTE, context, CodeGraph.ExitMethod());

        Variable enter = current.valueOperation(ValueOperation.LOAD_ATTRIBUTE,
                context, CodeGraph.EnterMethod());
        Variable result = current.valueOperation(ValueOperation.CALL, enter);
        if (node.getInternalOptional_vars() != null) {
            node.getInternalOptional_vars().accept(new Assignment(result));
        }
        current.performTryCatch(new ManagedSimpleCallback() {
            @Override
            void generate() throws Exception {
                compile(node.getInternalBody());
            }
        }, new ManagedConsumerCallback() {
            @Override
            void generate(final Variable exception) throws Exception {
                Variable type = current.valueOperation(
                        ValueOperation.EXCEPTION_TYPE, exception);
                Variable value = current.valueOperation(
                        ValueOperation.EXCEPTION_VALUE, exception);
                Variable traceback = current.valueOperation(
                        ValueOperation.EXCEPTION_TRACEBACK, exception);
                current.performSelection(current.valueOperation(
                        ValueOperation.CALL, exit, type, value, traceback),
                        Generate.NOTHING, new ManagedSimpleCallback() {
                            @Override
                            void generate() throws Exception {
                                current.terminate(new BlockTermination.Raise(
                                        exception));
                            }
                        });
            }
        }, new ManagedSimpleCallback() {
            @Override
            void generate() throws Exception {
                current.valueOperation(ValueOperation.CALL, exit,
                        CodeGraph.None(), CodeGraph.None(), CodeGraph.None());
            }
        });
        return null;
    }

    @Override
    public Variable visitYield(Yield node) throws Exception {
        return current.performYield(evaluate(node.getInternalValue()));
    }

    // -- Ignored --

    @Override
    public Variable visitGlobal(Global node) throws Exception {
        // Explicitly ignored
        return null;
    }

    public Variable buildExtendedSlice(java.util.List<slice> dims)
            throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public void deleteAttribute(Variable target, String attribute)
            throws Exception {
        // TODO Auto-generated method stub

    }

    public void deleteEllipsis(Variable target) throws Exception {
        // TODO Auto-generated method stub

    }

    public void deleteName(String name) throws Exception {
        // TODO Auto-generated method stub

    }

    public void deleteSlice(Variable target, Variable lower, Variable step,
            Variable upper) throws Exception {
        // TODO Auto-generated method stub

    }

    public void deleteSubscript(Variable target, Variable subscript)
            throws Exception {
        // TODO Auto-generated method stub

    }

    public Variable dictGet(Variable dict, Variable key) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Variable loadAttribute(Variable target, String attribute)
            throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Variable loadEllipsis(Variable target) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Variable loadName(String name) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Variable loadSlice(Variable target, Variable lower, Variable step,
            Variable upper) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Variable loadSubscript(Variable target, Variable subscript)
            throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public void storeAttribute(Variable target, String attribute, Variable value)
            throws Exception {
        // TODO Auto-generated method stub

    }

    public void storeEllipsis(Variable target, Variable value) throws Exception {
        // TODO Auto-generated method stub

    }

    public void storeName(String name, Variable compute) throws Exception {
        // TODO Auto-generated method stub

    }

    public void storeSlice(Variable target, Variable lower, Variable step,
            Variable upper, Variable value) throws Exception {
        // TODO Auto-generated method stub

    }

    public void storeSubscript(Variable target, Variable subscript,
            Variable value) throws Exception {
        // TODO Auto-generated method stub

    }

    public Variable[] unpack(Variable value, int count) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Variable[] unpackStar(Variable value, int before, int after)
            throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Variable Ellipsis() {
        // TODO Auto-generated method stub
        return null;
    }

    public Variable False() {
        // TODO Auto-generated method stub
        return null;
    }

    public Variable None() {
        // TODO Auto-generated method stub
        return null;
    }

    public Variable True() {
        // TODO Auto-generated method stub
        return null;
    }

    public Variable loadConstant(String name) {
        // TODO Auto-generated method stub
        return null;
    }

}
