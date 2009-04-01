package org.python.compiler.advanced.sea;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
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
import org.python.antlr.ast.If;
import org.python.antlr.ast.IfExp;
import org.python.antlr.ast.Import;
import org.python.antlr.ast.ImportFrom;
import org.python.antlr.ast.Index;
import org.python.antlr.ast.Lambda;
import org.python.antlr.ast.List;
import org.python.antlr.ast.ListComp;
import org.python.antlr.ast.Name;
import org.python.antlr.ast.Print;
import org.python.antlr.ast.Raise;
import org.python.antlr.ast.Repr;
import org.python.antlr.ast.Return;
import org.python.antlr.ast.Slice;
import org.python.antlr.ast.Subscript;
import org.python.antlr.ast.TryExcept;
import org.python.antlr.ast.TryFinally;
import org.python.antlr.ast.Tuple;
import org.python.antlr.ast.UnaryOp;
import org.python.antlr.ast.VisitorIF;
import org.python.antlr.ast.While;
import org.python.antlr.ast.With;
import org.python.antlr.ast.Yield;
import org.python.antlr.ast.alias;
import org.python.antlr.ast.boolopType;
import org.python.antlr.ast.cmpopType;
import org.python.antlr.ast.comprehension;
import org.python.antlr.ast.keyword;
import org.python.antlr.base.excepthandler;
import org.python.antlr.base.expr;
import org.python.antlr.base.slice;
import org.python.compiler.advanced.AugAssignCallback;
import org.python.compiler.advanced.Constant;
import org.python.compiler.advanced.ConstantPool;
import org.python.compiler.advanced.IntermediateCodeGenerator;
import org.python.compiler.sea.BlockCallback;
import org.python.compiler.sea.GraphBuilder;
import org.python.compiler.sea.LoopCallback;
import org.python.compiler.sea.LoopHandle;
import org.python.compiler.sea.SelectionCallback;
import org.python.compiler.sea.StateCarrier;
import org.python.compiler.sea.UnrollerCallback;
import org.thobe.compiler.sea.ExceptionValue;
import org.thobe.compiler.sea.Value;
import org.thobe.compiler.sea.Variable;

/**
 * Intermediate code generator that generates a Sea of Nodes type of SSA IR.
 * 
 * @author Tobias Ivarsson
 */
final class NodeSeaGenerator extends
        IntermediateCodeGenerator<Value, SeaScope, ValueCarrier> {
    private final GraphBuilder generate;
    private final boolean linenumbers;
    private final boolean print_expressions;
    ExceptionValue currentException;
    LoopHandle currentLoop;
    private final Map<Constant, Value> constants;

    NodeSeaGenerator(VisitorIF<Value> director, ConstantPool constants,
            GraphBuilder builder, boolean linenumbers,
            boolean print_expressions, Map<Constant, Value> constant) {
        super(director, constants);
        this.generate = builder;
        this.linenumbers = linenumbers;
        this.print_expressions = print_expressions;
        this.constants = constant;
    }

    @Override
    protected Value[] resultArray(int size) {
        return new Value[size];
    }

    void linenumber(PythonTree node) {
        if (linenumbers) generate.linenumber(node.getLine());
    }

    // Constant management

    public Value loadConstant(Constant token) {
        final Value constant = constants.get(token);
        if (constant == null) {
            throw new IllegalArgumentException("No such constant: " + token + " in " + constants);
        }
        return constant;
    }

    public Value loadConstant(String name) {
        return generate.loadGlobalConstant(name);
    }

    public Value True() {
        return generate.True();
    }

    public Value False() {
        return generate.False();
    }

    public Value None() {
        return generate.None();
    }

    public Value Ellipsis() {
        return generate.Ellipsis();
    }

    // Variable management

    Variable[] closureFor(SeaScope scope) {
        Collection<Variable> result = new ArrayList<Variable>();
        for (String name : scope.free()) {
            result.add(variable(name));
        }
        return result.toArray(new Variable[result.size()]);
    }

    Variable variable(String name) {
        return generate.variable(name);
    }

    Variable generatorArgument() {
        return generate.argument(0);
    }

    // Helper methods

    private void unrollComprehension(Iterable<comprehension> iterators,
            final ComprehensionCallback callback) throws Exception {
        generate.loop(new ComprehensionLoop(callback, iterators.iterator()) {
            @Override
            protected StateCarrier head() throws Exception {
                return generate.next(callback.initialIterator(current));
            }
        });
    }

    private abstract class ComprehensionLoop extends LoopCallback {
        private final ComprehensionCallback callback;
        private final Iterator<comprehension> iterators;
        final comprehension current;

        public ComprehensionLoop(ComprehensionCallback callback,
                Iterator<comprehension> iterators) {
            this(callback, iterators, iterators.next());
        }

        private ComprehensionLoop(ComprehensionCallback callback,
                Iterator<comprehension> iterators, comprehension current) {
            this.callback = callback;
            this.iterators = iterators;
            this.current = current;
        }

        @Override
        protected final void body(Value item, LoopHandle loop) throws Exception {
            LoopHandle prevLoop = currentLoop;
            currentLoop = loop;
            try {
                assign(current.getInternalTarget(), item);
                generate.selection(generate.unroll(null,
                        current.getInternalIfs(), new UnrollerCallback<expr>() {
                            @Override
                            protected StateCarrier generate(Value previous,
                                    expr node) throws Exception {
                                return generate.onTrue(evaluate(node));
                            }
                        }), new SelectionCallback() {
                    @Override
                    protected Value onTrue() throws Exception {
                        if (iterators.hasNext()) {
                            comprehension next = iterators.next();
                            final Value iter = evaluate(next.getInternalIter());
                            generate.loop(new ComprehensionLoop(callback,
                                    iterators, next) {
                                @Override
                                protected StateCarrier head() throws Exception {
                                    return generate.next(iter);
                                }
                            });
                        } else {
                            callback.body();
                        }
                        return null;
                    }

                    @Override
                    protected Value onFalse() throws Exception {
                        return null;
                    }
                });
            } finally {
                currentLoop = prevLoop;
            }
        }

        @Override
        protected final void orelse() {
        }
    }

    private static abstract class ComprehensionCallback {
        abstract Value initialIterator(comprehension first) throws Exception;

        abstract void body() throws Exception;
    }

    private Value applyDecorators(Value decorated, Value[] decorators) {
        for (Value decorator : decorators) {
            decorated = generate.call(decorator, decorated);
        }
        return decorated;
    }

    // AssignmentGenerator

    public Value buildExtendedSlice(java.util.List<slice> dims)
            throws Exception {
        return generate.tuple(evaluate(dims));
    }

    public Value dictGet(Value dict, Value key) throws Exception {
        return generate.getSubscript(dict, key);
    }

    public Value[] unpack(Value value, int count) throws Exception {
        return generate.unpack(value, count);
    }

    public Value[] unpackStar(Value value, int before, int after)
            throws Exception {
        return generate.unpackStar(value, before, after);
    }

    public Value loadName(String name) throws Exception {
        return generate.load(variable(name));
    }

    public Value loadAttribute(Value target, String attribute) throws Exception {
        return generate.getAttribute(target, attribute);
    }

    public Value loadSubscript(Value target, Value subscript) throws Exception {
        return generate.getSubscript(target, subscript);
    }

    public Value loadSlice(Value target, Value lower, Value step, Value upper)
            throws Exception {
        return generate.getSlice(target, lower, step, upper);
    }

    public Value loadEllipsis(Value target) throws Exception {
        return loadSubscript(target, generate.Ellipsis());
    }

    public void storeName(String name, Value value) throws Exception {
        generate.store(variable(name), value);
    }

    public void storeAttribute(Value target, String attribute, Value value)
            throws Exception {
        generate.setAttribute(target, attribute, value);
    }

    public void storeSubscript(Value target, Value subscript, Value value)
            throws Exception {
        generate.setSubscript(target, subscript, value);
    }

    public void storeEllipsis(Value target, Value value) throws Exception {
        storeSubscript(target, generate.Ellipsis(), value);
    }

    public void storeSlice(Value target, Value lower, Value step, Value upper,
            Value value) throws Exception {
        generate.setSlice(target, lower, step, upper, value);
    }

    public void deleteName(String name) throws Exception {
        generate.delete(variable(name));
    }

    public void deleteAttribute(Value target, String attribute)
            throws Exception {
        generate.delAttribute(target, attribute);
    }

    public void deleteSubscript(Value target, Value subscript) throws Exception {
        generate.delSubscript(target, subscript);
    }

    public void deleteSlice(Value target, Value lower, Value step, Value upper)
            throws Exception {
        generate.delSlice(target, lower, step, upper);
    }

    public void deleteEllipsis(Value target) throws Exception {
        deleteSubscript(target, generate.Ellipsis());
    }

    // --- Definitions ---

    // ClassDef

    @Override
    public ValueCarrier beforeClassDef(ClassDef node, SeaScope scope)
            throws Exception {
        Value[] decorators = evaluate(node.getInternalDecorator_list());
        Value[] bases = evaluate(node.getInternalBases());
        return ValueCarrier.forClass(closureFor(scope), bases, decorators);
    }

    @Override
    public Value visitClassDef(ClassDef node) throws Exception {
        linenumber(node);
        compile(node.getInternalBody());
        generate.returnLocalDefinitions();
        return generate.graph();
    }

    @Override
    public void afterClassDef(ClassDef node, ValueCarrier data, Value result)
            throws Exception {
        linenumber(node);
        Value cls = generate.makeClass(node.getInternalName(), data.bases(),
                data.closure(), result);
        cls = applyDecorators(cls, data.decorators());
        generate.completeClass(node.getInternalName(), cls);
        generate.store(variable(node.getInternalName()), cls);
    }

    // FunctionDef

    @Override
    public ValueCarrier beforeFunctionDef(FunctionDef node, SeaScope scope)
            throws Exception {
        Value[] decorators = evaluate(node.getInternalDecorator_list());
        Value[] defaults = evaluate(node.getInternalArgs().getInternalDefaults());
        return ValueCarrier.forFunction(closureFor(scope), defaults, decorators);
    }

    @Override
    public Value visitFunctionDef(FunctionDef node) throws Exception {
        linenumber(node);
        Iterator<expr> args = node.getInternalArgs().getInternalArgs().iterator();
        for (int index = 0; args.hasNext(); index++) {
            expr arg = args.next();
            if (arg instanceof Tuple) {
                assign(arg, generate.load(generate.argument(index)));
            }
        }
        compile(node.getInternalBody());
        generate.mirandaReturn();
        return generate.graph();
    }

    @Override
    public void afterFunctionDef(FunctionDef node, ValueCarrier data,
            Value result) throws Exception {
        linenumber(node);
        Value function = generate.makeFunction(node.getInternalName(), result,
                data.closure(), data.defaults());
        function = applyDecorators(function, data.decorators());
        generate.store(variable(node.getInternalName()), function);
    }

    // Lambda

    @Override
    public ValueCarrier beforeLambda(Lambda node, SeaScope scope)
            throws Exception {
        return ValueCarrier.forLambda(closureFor(scope),
                evaluate(node.getInternalArgs().getInternalDefaults()));
    }

    @Override
    public Value visitLambda(Lambda node) throws Exception {
        linenumber(node);
        generate.returnPythonValue(evaluate(node.getInternalBody()));
        return generate.graph();
    }

    @Override
    public Value afterLambda(Lambda node, ValueCarrier data, Value result)
            throws Exception {
        linenumber(node);
        return generate.makeFunction("<lambda>", result, data.closure(),
                data.defaults());
    }

    // GeneratorExp

    @Override
    public ValueCarrier beforeGeneratorExp(GeneratorExp node, SeaScope scope)
            throws Exception {
        Value firstIter = evaluate(node.getInternalGenerators().get(0).getInternalIter());
        return ValueCarrier.forGenerator(firstIter, closureFor(scope));
    }

    @Override
    public Value visitGeneratorExp(final GeneratorExp node) throws Exception {
        linenumber(node);
        unrollComprehension(node.getInternalGenerators(),
                new ComprehensionCallback() {
                    @Override
                    Value initialIterator(comprehension first) {
                        return generate.load(generatorArgument());
                    }

                    @Override
                    void body() throws Exception {
                        generate.yeildValue(evaluate(node.getInternalElt()));
                    }
                });
        generate.mirandaReturn();
        return generate.graph();
    }

    @Override
    public Value afterGeneratorExp(GeneratorExp node, ValueCarrier data,
            Value result) throws Exception {
        linenumber(node);
        Value generator = generate.makeGenerator(result, data.closure(),
                data.defaults());
        return generate.call(generator, data.firstIterator());
    }

    // --- Compound Statements ---

    // Try/Except/Finally

    @Override
    public Value visitTryFinally(final TryFinally node) throws Exception {
        linenumber(node);
        generate.tryBlock(new BlockCallback() {
            public void body() throws Exception {
                compile(node.getInternalBody());
            }

            public void onExceptionalExit(ExceptionValue exception)
                    throws Exception {
                compile(node.getInternalFinalbody());
                generate.raise(exception);
            }

            public void onNormalExit() throws Exception {
                compile(node.getInternalFinalbody());
            }
        });
        return null;
    }

    @Override
    public Value visitTryExcept(final TryExcept node) throws Exception {
        linenumber(node);
        generate.tryBlock(new BlockCallback() {
            public void body() throws Exception {
                compile(node.getInternalBody());
            }

            public void onExceptionalExit(final ExceptionValue exception)
                    throws Exception {
                ExceptionValue lastException = currentException;
                currentException = exception;
                try {
                    generate.unroll(null, node.getInternalHandlers(),
                            new UnrollerCallback<excepthandler>() {
                                @Override
                                protected StateCarrier generate(Value previous,
                                        excepthandler _handler)
                                        throws Exception {
                                    ExceptHandler handler = (ExceptHandler) _handler;
                                    Value type = evaluateOrNull(handler.getInternalType());
                                    StateCarrier selection;
                                    if (type == null) {
                                        selection = generate.always();
                                    } else {
                                        selection = generate.match(exception,
                                                type);
                                    }
                                    expr target = handler.getInternalName();
                                    if (target != null) {
                                        assign(
                                                target,
                                                generate.exceptionValue(exception));
                                    }
                                    compile(handler.getInternalBody());
                                    return selection;
                                }
                            });
                } finally {
                    currentException = lastException;
                }
            }

            public void onNormalExit() throws Exception {
                compile(node.getInternalOrelse());
            }
        });
        return null;
    }

    @Override
    public Value visitWith(final With node) throws Exception {
        linenumber(node);
        Value context_manager = evaluate(node.getInternalContext_expr());
        final Value __exit__ = generate.getAttribute(context_manager,
                "__exit__");
        Value value = generate.invoke(context_manager, "__enter__");
        if (node.getInternalOptional_vars() != null) {
            assign(node.getInternalOptional_vars(), value);
        }
        generate.tryBlock(new BlockCallback() {
            public void body() throws Exception {
                compile(node.getInternalBody());
            }

            public void onExceptionalExit(final ExceptionValue exception)
                    throws Exception {
                Value type = generate.exceptionType(exception);
                Value value = generate.exceptionValue(exception);
                Value traceback = generate.exceptionTrace(exception);
                Value result = generate.call(__exit__, type, value, traceback);
                generate.selection(result, new SelectionCallback() {
                    @Override
                    public Value onFalse() {
                        generate.raise(exception);
                        return null;
                    }

                    @Override
                    public Value onTrue() {
                        return null;
                    }
                });
            }

            public void onNormalExit() throws Exception {
                Value none = generate.None();
                generate.call(__exit__, none, none, none);
            }
        });
        return null;
    }

    // Loops

    @Override
    public Value visitFor(final For node) throws Exception {
        linenumber(node);
        final Value iter = evaluate(node.getInternalIter());
        generate.loop(new LoopCallback() {
            @Override
            protected StateCarrier head() {
                return generate.next(iter);
            }

            @Override
            protected void body(Value payload, LoopHandle loop)
                    throws Exception {
                LoopHandle prevLoop = currentLoop;
                currentLoop = loop;
                try {
                    assign(node.getInternalTarget(), payload);
                    compile(node.getInternalBody());
                } finally {
                    currentLoop = prevLoop;
                }
            }

            @Override
            protected void orelse() throws Exception {
                compile(node.getInternalOrelse());
            }
        });
        return null;
    }

    @Override
    public Value visitWhile(final While node) throws Exception {
        linenumber(node);
        generate.loop(new LoopCallback() {
            @Override
            protected StateCarrier head() throws Exception {
                return generate.onTrue(evaluate(node.getInternalTest()));
            }

            @Override
            protected void body(Value payload, LoopHandle loop)
                    throws Exception {
                LoopHandle prevLoop = currentLoop;
                currentLoop = loop;
                try {
                    compile(node.getInternalBody());
                } finally {
                    currentLoop = prevLoop;
                }
            }

            @Override
            protected void orelse() throws Exception {
                compile(node.getInternalOrelse());
            }
        });
        return null;
    }

    // Selection

    @Override
    public Value visitIf(final If node) throws Exception {
        linenumber(node);
        generate.selection(evaluate(node.getInternalTest()),
                new SelectionCallback() {
                    @Override
                    protected Value onTrue() throws Exception {
                        compile(node.getInternalBody());
                        return null;
                    }

                    @Override
                    protected Value onFalse() throws Exception {
                        compile(node.getInternalOrelse());
                        return null;
                    }
                });
        return null;
    }

    // --- Simple Statements ---

    @Override
    public Value visitExpr(Expr node) throws Exception {
        linenumber(node);
        Value value = evaluate(node.getInternalValue());
        if (print_expressions) {
            generate.printExpression(value);
        }
        return null;
    }

    @Override
    public Value visitAssert(final Assert node) throws Exception {
        linenumber(node);
        Value test = evaluate(node.getInternalTest());
        generate.selection(test, new SelectionCallback() {
            @Override
            protected Value onFalse() throws Exception {
                Value message = evaluate(node.getInternalMsg());
                ExceptionValue exception = generate.makeException(
                        generate.AssertionError(), message);
                generate.raise(exception);
                return null;
            }

            @Override
            protected Value onTrue() throws Exception {
                return null; // All OK
            }
        });
        return null;
    }

    @Override
    public Value visitExec(Exec node) throws Exception {
        linenumber(node);
        Value body = evaluate(node.getInternalBody());
        Value globals = evaluateOrNull(node.getInternalGlobals());
        Value locals = evaluateOrNull(node.getInternalLocals());
        generate.exec(body, globals, locals);
        return null;
    }

    @Override
    public Value visitPrint(Print node) throws Exception {
        linenumber(node);
        Value dest = evaluateOrNull(node.getInternalDest());
        Value[] values = evaluate(node.getInternalValues());
        if (dest != null) {
            generate.printTo(dest, node.getInternalNl(), values);
        } else {
            generate.print(node.getInternalNl(), values);
        }
        return null;
    }

    // Exit points

    @Override
    public Value visitReturn(Return node) throws Exception {
        linenumber(node);
        generate.returnPythonValue(evaluate(node.getInternalValue()));
        return null;
    }

    @Override
    public Value visitYield(Yield node) throws Exception {
        linenumber(node);
        return generate.yeildValue(evaluate(node.getInternalValue()));
    }

    @Override
    public Value visitRaise(Raise node) throws Exception {
        linenumber(node);
        Value type = evaluateOrNull(node.getInternalType());
        Value inst = evaluateOrNull(node.getInternalInst());
        Value tback = evaluateOrNull(node.getInternalTback());
        ExceptionValue exception;
        if (type == null) {
            exception = currentException;
            if (exception == null) {
                throw new ParseException("No exception to re-raise", node);
            }
        } else if (inst == null) {
            exception = generate.makeException(type);
        } else if (tback == null) {
            exception = generate.makeException(type, inst);
        } else {
            exception = generate.makeException(type, inst, tback);
        }
        generate.raise(exception);
        return null;
    }

    // Import

    @Override
    public Value visitImport(Import node) throws Exception {
        linenumber(node);
        for (alias part : node.getInternalNames()) {
            String name = part.getInternalName();
            String asname = part.getInternalAsname();
            Value module = generate.importName(name);
            if (asname != null) {
                String[] names = name.split(".");
                name = asname;
                for (int i = 1; i < names.length; i++) {
                    module = generate.getAttribute(module, names[i]);
                }
            } else {
                name = name.split(".")[0];
            }
            generate.store(variable(name), module);
        }
        return null;
    }

    @Override
    public Value visitImportFrom(ImportFrom node) throws Exception {
        linenumber(node);
        String module_name = node.getInternalModule();
        java.util.List<alias> names = node.getInternalNames();
        if (names == null || names.isEmpty()) {
            throw new ParseException(
                    "Illegal AST structure. No names in import from.", node);
        } else if (names.size() == 1
                && "*".equals(names.get(0).getInternalName())) {
            // Star import
            generate.importAll(module_name);
        } else {
            Value module = generate.importModule(module_name,
                    node.getInternalLevel());
            for (alias part : node.getInternalNames()) {
                String name = part.getInternalName();
                String asname = part.getInternalAsname();
                Value imported = generate.importFrom(module, name);
                if (asname != null) {
                    name = asname;
                }
                generate.store(variable(name), imported);
            }
        }
        return null;
    }

    // Assignment

    @Override
    public Value visitAssign(Assign node) throws Exception {
        linenumber(node);
        Value value = evaluate(node.getInternalValue());
        for (expr target : node.getInternalTargets()) {
            assign(target, value);
        }
        return null;
    }

    @Override
    public Value visitAugAssign(final AugAssign node) throws Exception {
        linenumber(node);
        augAssign(node.getInternalTarget(), new AugAssignCallback<Value>() {
            @Override
            protected Value compute(Value lhs) throws Exception {
                Value rhs = evaluate(node.getInternalValue());
                return generate.inplaceOperator(node.getInternalOp(), lhs, rhs);
            }
        });
        return null;
    }

    @Override
    public Value visitDelete(Delete node) throws Exception {
        linenumber(node);
        delete(node.getInternalTargets());
        return null;
    }

    // Loop Control

    @Override
    public Value visitBreak(Break node) throws Exception {
        linenumber(node);
        currentLoop.breakLoop();
        return null;
    }

    @Override
    public Value visitContinue(Continue node) throws Exception {
        linenumber(node);
        currentLoop.continueLoop();
        return null;
    }

    // --- Expressions ---

    @Override
    public Value visitIfExp(final IfExp node) throws Exception {
        linenumber(node);
        return generate.selection(evaluate(node.getInternalTest()),
                new SelectionCallback() {
                    @Override
                    protected Value onTrue() throws Exception {
                        return evaluate(node.getInternalBody());
                    }

                    @Override
                    protected Value onFalse() throws Exception {
                        return evaluate(node.getInternalOrelse());
                    }
                });
    }

    @Override
    public Value visitCall(Call node) throws Exception {
        linenumber(node);
        expr function = node.getInternalFunc();
        // Separate invocation from call to utilize invoke dynamic optimization
        if (generate.optimizedInvocation() && function instanceof Attribute) {
            Attribute attr = (Attribute) function;
            Value target = evaluate(attr.getInternalValue());
            CallTypeSelector dispatch = generateCall(node);
            return dispatch.invoke(target, attr.getInternalAttr());
        } else {
            Value callable = evaluate(function);
            CallTypeSelector dispatch = generateCall(node);
            return dispatch.call(callable);
        }
    }

    private static abstract class CallTypeSelector {
        abstract Value invoke(Value target, String method);

        abstract Value call(Value callable);
    }

    private CallTypeSelector generateCall(Call node) throws Exception {
        // Positional arguments
        final Value[] args = evaluate(node.getInternalArgs());
        // Star arguments
        final Value starargs = evaluateOrNull(node.getInternalStarargs());
        // Keyword arguments
        Collection<keyword> keys = node.getInternalKeywords();
        final String[] kw_keys;
        final Value[] kw_args;
        if (keys == null || keys.isEmpty()) {
            kw_keys = null;
            kw_args = null;
        } else {
            kw_keys = new String[keys.size()];
            kw_args = new Value[keys.size()];
            int i = 0;
            for (keyword kw : keys) {
                kw_keys[i] = kw.getInternalArg();
                kw_args[i] = evaluate(kw.getInternalValue());
                i++;
            }
        }
        // Double star arguments
        final Value kwargs = evaluateOrNull(node.getInternalKwargs());
        // Finally create an appropriate dispatcher
        return new CallTypeSelector() {
            @Override
            Value call(Value callable) {
                if (kw_keys != null) {
                    if (starargs != null) {
                        if (kwargs != null) {
                            return generate.callKeywordsVarargsVarkeys(
                                    callable, kw_keys, args, kw_args, starargs,
                                    kwargs);
                        } else {
                            return generate.callKeywordsVarargs(callable,
                                    kw_keys, args, kw_args, starargs);
                        }
                    } else {
                        if (kwargs != null) {
                            return generate.callKeywordsVarkeys(callable,
                                    kw_keys, args, kw_args, kwargs);
                        } else {
                            return generate.callKeywords(callable, kw_keys,
                                    args, kw_args);
                        }
                    }
                } else {
                    if (starargs != null) {
                        if (kwargs != null) {
                            return generate.callVarargsVarkeys(callable, args,
                                    starargs, kwargs);
                        } else {
                            return generate.callVarargs(callable, args,
                                    starargs);
                        }
                    } else {
                        if (kwargs != null) {
                            return generate.callVarkeys(callable, args, kwargs);
                        } else {
                            return generate.call(callable, args);
                        }
                    }
                }
            }

            @Override
            Value invoke(Value target, String method) {
                if (kw_keys != null) {
                    if (starargs != null) {
                        if (kwargs != null) {
                            return generate.invokeKeywordsVarargsVarkeys(
                                    target, method, kw_keys, args, kw_args,
                                    starargs, kwargs);
                        } else {
                            return generate.invokeKeywordsVarargs(target,
                                    method, kw_keys, args, kw_args, starargs);
                        }
                    } else {
                        if (kwargs != null) {
                            return generate.invokeKeywordsVarkeys(target,
                                    method, kw_keys, args, kw_args, kwargs);
                        } else {
                            return generate.invokeKeywords(target, method,
                                    kw_keys, args, kw_args);
                        }
                    }
                } else {
                    if (starargs != null) {
                        if (kwargs != null) {
                            return generate.invokeVarargsVarkeys(target,
                                    method, args, starargs, kwargs);
                        } else {
                            return generate.invokeVarargs(target, method, args,
                                    starargs);
                        }
                    } else {
                        if (kwargs != null) {
                            return generate.invokeVarkeys(target, method, args,
                                    kwargs);
                        } else {
                            return generate.invoke(target, method, args);
                        }
                    }
                }
            }
        };
    }

    // Load

    @Override
    public Value visitAttribute(Attribute node) throws Exception {
        linenumber(node);
        Value value = evaluate(node.getInternalValue());
        return generate.getAttribute(value, node.getInternalAttr());
    }

    @Override
    public Value visitEllipsis(Ellipsis node) throws Exception {
        linenumber(node);
        return generate.Ellipsis();
    }

    @Override
    public Value visitName(Name node) throws Exception {
        linenumber(node);
        return generate.load(variable(node.getInternalId()));
    }

    @Override
    public Value visitSubscript(Subscript node) throws Exception {
        linenumber(node);
        return subscript(evaluate(node.getInternalValue()),
                node.getInternalSlice());
    }

    // Operations

    private static class Comparison {
        final cmpopType op;
        final expr comparator;

        Comparison(cmpopType op, expr comparator) {
            this.op = op;
            this.comparator = comparator;
        }

        static Iterable<Comparison> iterator(final Compare node) {
            return new Iterable<Comparison>() {
                public Iterator<Comparison> iterator() {
                    return new Iterator<Comparison>() {
                        private final Iterator<cmpopType> ops = node.getInternalOps().iterator();
                        private final Iterator<expr> comparators = node.getInternalComparators().iterator();

                        public boolean hasNext() {
                            return ops.hasNext() && comparators.hasNext();
                        }

                        public Comparison next() {
                            return new Comparison(ops.next(),
                                    comparators.next());
                        }

                        public void remove() {
                            throw new UnsupportedOperationException();
                        }
                    };
                }
            };
        }
    }

    @Override
    public Value visitCompare(Compare node) throws Exception {
        linenumber(node);
        return generate.unroll(evaluate(node.getInternalLeft()),
                Comparison.iterator(node), new UnrollerCallback<Comparison>() {
                    @Override
                    protected StateCarrier generate(Value previous,
                            Comparison node) throws Exception {
                        return generate.compare(node.op, previous,
                                evaluate(node.comparator));
                    }
                });
    }

    @Override
    public Value visitBoolOp(final BoolOp node) throws Exception {
        linenumber(node);
        UnrollerCallback<expr> callback;
        if (node.getInternalOp() == boolopType.And) {
            callback = new UnrollerCallback<expr>() {
                @Override
                protected StateCarrier generate(Value previous, expr value)
                        throws Exception {
                    return generate.and(evaluate(value), previous);
                }
            };
        } else if (node.getInternalOp() == boolopType.Or) {
            callback = new UnrollerCallback<expr>() {
                @Override
                protected StateCarrier generate(Value previous, expr value)
                        throws Exception {
                    return generate.or(evaluate(value), previous);
                }
            };
        } else {
            throw new ParseException("Unknown boolean operator.", node);
        }
        return generate.unroll(null, node.getInternalValues(), callback);
    }

    @Override
    public Value visitBinOp(BinOp node) throws Exception {
        linenumber(node);
        Value left = evaluate(node.getInternalLeft());
        Value right = evaluate(node.getInternalRight());
        return generate.binaryOperator(node.getInternalOp(), left, right);
    }

    @Override
    public Value visitUnaryOp(UnaryOp node) throws Exception {
        linenumber(node);
        Value operand = evaluate(node.getInternalOperand());
        return generate.unaryOperator(node.getInternalOp(), operand);
    }

    // Creation

    @Override
    public Value visitTuple(Tuple node) throws Exception {
        linenumber(node);
        return generate.tuple(evaluate(node.getInternalElts()));
    }

    @Override
    public Value visitDict(Dict node) throws Exception {
        linenumber(node);
        Iterable<expr> keys = node.getInternalKeys();
        Iterable<expr> values = node.getInternalValues();
        Value dict = generate.makeMap();
        for (Iterator<expr> _key = keys.iterator(), _value = values.iterator(); _key.hasNext()
                && _value.hasNext();) {
            Value key = evaluate(_key.next());
            Value value = evaluate(_value.next());
            generate.mapStore(dict, key, value);
        }
        return dict;
    }

    @Override
    public Value visitList(List node) throws Exception {
        linenumber(node);
        return generate.list(evaluate(node.getInternalElts()));
    }

    @Override
    public Value visitListComp(final ListComp node) throws Exception {
        linenumber(node);
        final Value list = generate.list();
        unrollComprehension(node.getInternalGenerators(),
                new ComprehensionCallback() {
                    @Override
                    Value initialIterator(comprehension first) throws Exception {
                        return evaluate(first.getInternalIter());
                    }

                    @Override
                    void body() throws Exception {
                        generate.listAppend(list,
                                evaluate(node.getInternalElt()));
                    }
                });
        return list;
    }

    // Conversion

    @Override
    public Value visitRepr(Repr node) throws Exception {
        linenumber(node);
        return generate.repr(evaluate(node.getInternalValue()));
    }

    // --- Slices ---

    @Override
    public Value visitIndex(Index node) throws Exception {
        linenumber(node);
        return evaluate(node.getInternalValue());
    }

    @Override
    public Value visitSlice(Slice node) throws Exception {
        linenumber(node);
        Value lower = evaluateOrNull(node.getInternalLower());
        Value step = evaluateOrNull(node.getInternalStep());
        Value upper = evaluateOrNull(node.getInternalUpper());
        return generate.slice(lower, step, upper);
    }

    @Override
    public Value visitExtSlice(ExtSlice node) throws Exception {
        linenumber(node);
        return generate.tuple(evaluate(node.getInternalDims()));
    }
}
