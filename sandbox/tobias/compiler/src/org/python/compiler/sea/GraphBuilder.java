package org.python.compiler.sea;

import org.python.antlr.ast.cmpopType;
import org.python.antlr.ast.operatorType;
import org.python.antlr.ast.unaryopType;
import org.python.core.CodeFlag;
import org.python.core.CompilerFlags;
import org.thobe.compiler.sea.ArrayValue;
import org.thobe.compiler.sea.NodeSuccession;
import org.thobe.compiler.sea.ExceptionValue;
import org.thobe.compiler.sea.NamespacePopulator;
import org.thobe.compiler.sea.InvocationType;
import org.thobe.compiler.sea.Selection;
import org.thobe.compiler.sea.Value;
import org.thobe.compiler.sea.Variable;

public abstract class GraphBuilder extends org.thobe.compiler.sea.GraphBuilder {
    private final CallStrategy call;
    private final InvocationStrategy invocation;
    private final FrameStrategy frame;
    private final CompilerFlags flags;
    private final GeneratorCallback access = new GeneratorCallback() {
        @Override
        public Value invoke(InvocationType type, Value... arguments) {
            return schedule(GraphBuilder.this.invoke(type, arguments)).result();
        }

        @Override
        public Value string(String string) {
            return Value.string(string);
        }

        @Override
        public Value array(Value... content) {
            return GraphBuilder.this.array(content).result();
        }
    };

    protected GraphBuilder(NamespacePopulator populator, CallStrategy call,
            FrameStrategy frame, CompilerFlags flags) {
        super(populator);
        if (call == null) {
            throw new NullPointerException("Call strategy may not be null.");
        }
        this.call = call;
        if (call instanceof InvocationStrategy) {
            this.invocation = (InvocationStrategy) call;
        } else {
            this.invocation = null;
        }
        this.frame = frame;
        this.flags = flags;
    }

    private operatorType compensateDivision(operatorType op) {
        if (op == operatorType.Div) {
            if (!flags.isFlagSet(CodeFlag.CO_FUTURE_DIVISION)) {
                return operatorType.FloorDiv;
            }
        }
        return op;
    }

    @Deprecated
    private <X> X something() {
        return null;
    }

    /**
     * @return <code>true</code> if invocation should be optimized
     *         <code>false</code> if they should be treated as calls.
     */
    public final boolean optimizedInvocation() {
        return invocation != null;
    }

    /**
     * Set the line number.
     * 
     * @param line the line number to set.
     */
    public final void linenumber(int line) {
        // FIXME: implement this somehow
    }

    // --- Handle ---

    /**
     * @return a reference to this graph.
     */
    public abstract Value graph();

    // --- Common Globals ---

    /**
     * @return the Python value True.
     */
    public Value True() {
        return invoke(PythonOperation.LOAD_TRUE).result();
    }

    /**
     * @return the Python value False.
     */
    public Value False() {
        return invoke(PythonOperation.LOAD_FALSE).result();
    }

    /**
     * @return the Python value None.
     */
    public Value None() {
        return invoke(PythonOperation.LOAD_NONE).result();
    }

    /**
     * @return the Python value Ellipsis.
     */
    public Value Ellipsis() {
        return invoke(PythonOperation.LOAD_ELLIPSIS).result();
    }

    /**
     * @param string
     * @return a Python string with the supplied content.
     */
    public Value string(String string) {
        // TODO: implement this
        return null;
    }

    /**
     * @return the Python exception type AssertionError.
     */
    public Value AssertionError() {
        return invoke(PythonOperation.LOAD_ASSERTION_ERROR).result();
    }

    // --- Callbacks ---

    public Value selection(Value result, SelectionCallback selection)
            throws Exception {
        // TODO: this isn't quite right.
        // What if one (or both) paths are made up of side effect free nodes?
        // How do we then determine which value to return, since the nodes are
        // not scheduled in a particular path?
        // --
        // What about a tentative scheduling, so that we have a concept of nodes
        // belonging to a path, but not in a particular order. This would mean
        // that Continuations are "major versions" of some other (internal)
        // concept. Or we could schedule some artificial node in the end of the
        // path. This node would do nothing except denote where the value came
        // from. The artificial node approach is probably better, it imposes
        // less overhead. Although it does make verification harder. How does
        // phi verify that each value comes from different paths?
        // Does it have to? 
        Selection select = something();// TODO: design API like: selection(types.isTrue(), result);
        replaceSuccession(select.onTrue());
        Value on_true = schedule(selection.onTrue()); // schedule artificial node
        NodeSuccession after_true = replaceSuccession(select.onFalse());
        Value on_false = schedule(selection.onFalse()); // schedule artificial node
        NodeSuccession after_false = currentSuccession();
        replaceSuccession(merge(after_true, after_false));
        return phiOrNull(on_true, on_false);
    }

    public void loop(LoopCallback loop) throws Exception {
        // TODO: this has issues for the same reasons as selection does.
        final NodeSuccession start = currentSuccession();
        final NodeSuccession after = newSuccession();
        StateCarrier state = loop.head();
        replaceSuccession(state.onTrue());
        loop.body(state.payload(), new LoopHandle() {
            @Override
            public void breakLoop() {
                nextSuccession(after);
            }

            @Override
            public void continueLoop() {
                nextSuccession(start);
            }
        });
        replaceSuccession(state.onFalse());
        loop.orelse();
        nextSuccession(after);
        replaceSuccession(after);
    }

    public void tryBlock(BlockCallback block) {
        // TODO Auto-generated method stub

    }

    public <T> Value unroll(Value first, Iterable<T> nodes,
            UnrollerCallback<T> callback) throws Exception {
        Value previous = first;
        for (T node : nodes) {
            StateCarrier carrier = callback.generate(previous, node);
            previous = carrier.payload();
        }
        // TODO Auto-generated method stub
        return null;
    }

    public final StateCarrier always() {
        // TODO Auto-generated method stub
        return null;
    }

    public final StateCarrier onTrue(Value condition) {
        // TODO Auto-generated method stub
        return null;
    }

    public final StateCarrier onFalse(Value condition) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @param condition
     * @param previous
     * @return
     */
    public final StateCarrier and(Value condition, Value previous) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @param condition
     * @param previous
     * @return
     */
    public final StateCarrier or(Value condition, Value previous) {
        // TODO Auto-generated method stub
        return null;
    }

    public final StateCarrier next(Value iter) {
        // TODO Auto-generated method stub
        return null;
    }

    public final StateCarrier compare(cmpopType op, Value last, Value current) {
        // TODO Auto-generated method stub
        return null;
    }

    // --- Exception handling ---

    public StateCarrier match(ExceptionValue exception, Value type) {
        // TODO Auto-generated method stub
        return null;
    }

    public final void raise(ExceptionValue exception) {
        schedule(throwException(exception));
    }

    public ExceptionValue makeException(Value type) {
        // TODO Auto-generated method stub
        return null;
    }

    public ExceptionValue makeException(Value type, Value message) {
        // TODO Auto-generated method stub
        return null;
    }

    public ExceptionValue makeException(Value type, Value inst, Value tback) {
        // TODO Auto-generated method stub
        return null;
    }

    public Value exceptionType(ExceptionValue exception) {
        // TODO Auto-generated method stub
        return null;
    }

    public Value exceptionValue(ExceptionValue exception) {
        // TODO Auto-generated method stub
        return null;
    }

    public Value exceptionTrace(ExceptionValue exception) {
        // TODO Auto-generated method stub
        return null;
    }

    // --- Calls and invocations ---

    /**
     * @param target
     * @param methodName
     * @param arguments
     * @return
     */
    public final Value invoke(Value target, String methodName,
            Value... arguments) {
        return invocation.invoke(access, target, methodName, arguments);
    }

    /**
     * @param callable
     * @param arguments
     * @return
     */
    public final Value call(Value callable, Value... arguments) {
        return call.call(access, callable, arguments);
    }

    /**
     * @param target
     * @param methodName
     * @param keywords
     * @param arguments
     * @param keyword_arguments
     * @return
     */
    public final Value invokeKeywords(Value target, String methodName,
            String[] keywords, Value[] arguments, Value[] keyword_arguments) {
        return invocation.invoke(access, target, methodName, keywords,
                arguments, keyword_arguments);
    }

    /**
     * @param callable
     * @param keywords
     * @param arguments
     * @param keyword_arguments
     * @return
     */
    public final Value callKeywords(Value callable, String[] keywords,
            Value[] arguments, Value[] keyword_arguments) {
        return call.call(access, callable, keywords, arguments,
                keyword_arguments);
    }

    /**
     * @param target
     * @param methodName
     * @param arguments
     * @param varargs
     * @return
     */
    public final Value invokeVarargs(Value target, String methodName,
            Value[] arguments, Value varargs) {
        return invocation.invokeVar(access, target, methodName, arguments,
                varargs);
    }

    /**
     * @param callable
     * @param arguments
     * @param varargs
     * @return
     */
    public final Value callVarargs(Value callable, Value[] arguments,
            Value varargs) {
        return call.callVar(access, callable, arguments, varargs);
    }

    /**
     * @param target
     * @param methodName
     * @param arguments
     * @param kwarg
     * @return
     */
    public final Value invokeVarkeys(Value target, String methodName,
            Value[] arguments, Value kwarg) {
        return invocation.invokeKey(access, target, methodName, arguments,
                kwarg);
    }

    /**
     * @param callable
     * @param arguments
     * @param kwarg
     * @return
     */
    public final Value callVarkeys(Value callable, Value[] arguments,
            Value kwarg) {
        return call.callKey(access, callable, arguments, kwarg);
    }

    /**
     * @param target
     * @param methodName
     * @param keywords
     * @param arguments
     * @param keyword_arguments
     * @param varargs
     * @return
     */
    public final Value invokeKeywordsVarargs(Value target, String methodName,
            String[] keywords, Value[] arguments, Value[] keyword_arguments,
            Value varargs) {
        return invocation.invokeVar(access, target, methodName, keywords,
                arguments, keyword_arguments, varargs);
    }

    /**
     * @param callable
     * @param keywords
     * @param arguments
     * @param keyword_arguments
     * @param varargs
     * @return
     */
    public final Value callKeywordsVarargs(Value callable, String[] keywords,
            Value[] arguments, Value[] keyword_arguments, Value varargs) {
        return call.callVar(access, callable, keywords, arguments,
                keyword_arguments, varargs);
    }

    /**
     * @param target
     * @param methodName
     * @param keywords
     * @param arguments
     * @param keyword_arguments
     * @param kwargs
     * @return
     */
    public final Value invokeKeywordsVarkeys(Value target, String methodName,
            String[] keywords, Value[] arguments, Value[] keyword_arguments,
            Value kwargs) {
        return invocation.invokeKey(access, target, methodName, keywords,
                arguments, keyword_arguments, kwargs);
    }

    /**
     * @param callable
     * @param keywords
     * @param arguments
     * @param keyword_arguments
     * @param kwargs
     * @return
     */
    public final Value callKeywordsVarkeys(Value callable, String[] keywords,
            Value[] arguments, Value[] keyword_arguments, Value kwargs) {
        return call.callKey(access, callable, keywords, arguments,
                keyword_arguments, kwargs);
    }

    /**
     * @param target
     * @param methodName
     * @param arguments
     * @param varargs
     * @param kwargs
     * @return
     */
    public final Value invokeVarargsVarkeys(Value target, String methodName,
            Value[] arguments, Value varargs, Value kwargs) {
        return invocation.invoke(access, target, methodName, arguments,
                varargs, kwargs);
    }

    /**
     * @param callable
     * @param arguments
     * @param varargs
     * @param kwargs
     * @return
     */
    public final Value callVarargsVarkeys(Value callable, Value[] arguments,
            Value varargs, Value kwargs) {
        return call.call(access, callable, arguments, varargs, kwargs);
    }

    /**
     * @param target
     * @param methodName
     * @param keywords
     * @param arguments
     * @param keyword_arguments
     * @param varargs
     * @param kwargs
     * @return
     */
    public final Value invokeKeywordsVarargsVarkeys(Value target,
            String methodName, String[] keywords, Value[] arguments,
            Value[] keyword_arguments, Value varargs, Value kwargs) {
        return invocation.invoke(access, target, methodName, keywords,
                arguments, keyword_arguments, varargs, kwargs);
    }

    /**
     * @param callable
     * @param keywords
     * @param arguments
     * @param keyword_arguments
     * @param varargs
     * @param kwargs
     * @return
     */
    public final Value callKeywordsVarargsVarkeys(Value callable,
            String[] keywords, Value[] arguments, Value[] keyword_arguments,
            Value varargs, Value kwargs) {
        return call.call(access, callable, keywords, arguments,
                keyword_arguments, varargs, kwargs);
    }

    // --- Memory references ---

    /**
     * Load the value from a variable.
     * 
     * @param variable The variable to load the value from.
     * @return The loaded value.
     */
    public Value load(Variable variable) {
        return loadVariable(variable).result();
    }

    /**
     * Store a value in a variable.
     * 
     * @param variable The variable to store the value in.
     * @param value The value to store in the variable.
     */
    public void store(Variable variable, Value value) {
        schedule(storeVariable(variable, value));
    }

    // --- Member access ---

    /**
     * @param owner
     * @param attribute
     * @return The value of the attribute.
     */
    public final Value getAttribute(Value owner, String attribute) {
        if (invocation != null) {
            return invocation.getAttribute(access, owner, attribute);
        } else {
            return schedule(
                    invoke(PythonOperation.GET_ATTRIBUTE, owner,
                            Value.string(attribute))).result();
        }
    }

    /**
     * @param owner
     * @param attribute
     * @param value
     */
    public void setAttribute(Value owner, String attribute, Value value) {
        if (invocation != null) {
            invocation.setAttribute(access, owner, attribute, value);
        } else {
            schedule(invoke(PythonOperation.SET_ATTRIBUTE, owner,
                    Value.string(attribute), value));
        }
    }

    /**
     * @param owner
     * @param attribute
     */
    public void delAttribute(Value owner, String attribute) {
        if (invocation != null) {
            invocation.delAttribute(access, owner, attribute);
        } else {
            schedule(invoke(PythonOperation.DELETE_ATTRIBUTE, owner,
                    Value.string(attribute)));
        }
    }

    // --- Subscript ---

    /**
     * @param owner
     * @param key
     * @return The result of the subscript.
     */
    public Value getSubscript(Value owner, Value key) {
        return schedule(invoke(PythonOperation.GET_ITEM, owner, key)).result();
    }

    /**
     * @param target
     * @param lower
     * @param step
     * @param upper
     * @return The result of the slice subscript.
     */
    public Value getSlice(Value target, Value lower, Value step, Value upper) {
        lower = (lower == null) ? None() : lower;
        step = (step == null) ? None() : step;
        upper = (upper == null) ? None() : upper;
        return schedule(
                invoke(PythonOperation.GET_SLICE, target, lower, step, upper)).result();
    }

    /**
     * @param dict
     * @param key
     * @param value
     */
    public void mapStore(Value dict, Value key, Value value) {
        setSubscript(dict, key, value);
    }

    /**
     * @param target
     * @param subscript
     * @param value
     */
    public void setSubscript(Value target, Value subscript, Value value) {
        // TODO: Look at the type of the target, optimize for list|tuple|map
        // TODO: Look at the type of the subscript, optimize for string|number
        schedule(invoke(PythonOperation.SET_ITEM, target, subscript, value));
    }

    /**
     * @param target
     * @param lower
     * @param step
     * @param upper
     * @param value
     */
    public void setSlice(Value target, Value lower, Value step, Value upper,
            Value value) {
        lower = (lower == null) ? None() : lower;
        step = (step == null) ? None() : step;
        upper = (upper == null) ? None() : upper;
        schedule(invoke(PythonOperation.SET_SLICE, target, lower, step, upper,
                value));
    }

    /**
     * @param target
     * @param subscript
     */
    public void delSubscript(Value target, Value subscript) {
        schedule(invoke(PythonOperation.DELETE_ITEM, target, subscript));
    }

    /**
     * @param target
     * @param lower
     * @param step
     * @param upper
     */
    public void delSlice(Value target, Value lower, Value step, Value upper) {
        // NOTE: should this have an option of building a slice and do delete subscript?
        lower = (lower == null) ? None() : lower;
        step = (step == null) ? None() : step;
        upper = (upper == null) ? None() : upper;
        schedule(invoke(PythonOperation.DELETE_SLICE, target, lower, step,
                upper));
    }

    // --- Unpacking ---

    public Value[] unpack(Value value, int count) {
        // TODO: this needs to be thought through
        ArrayValue array;
        if (value instanceof ArrayValue) {
            array = (ArrayValue) value;
        } else {
            array = (ArrayValue) invoke(PythonOperation.UNPACK,
                    Value.integer(count), value).result();
        }
        return array.array(count);
    }

    public Value[] unpackStar(Value value, int before, int after) {
        // TODO: this needs to be thought through
        if (value instanceof ArrayValue) {
            ArrayValue array = (ArrayValue) value;
            Value[] res = array.array();
        }
        return ((ArrayValue) invoke(PythonOperation.UNPACK_STAR,
                Value.integer(before), Value.integer(after), value).result()).array(before
                + after + 1);
    }

    // --- Construction ---

    public Value makeClass(String name, Value[] bases, Variable[] variables,
            Value code) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @param name
     * @param cls
     */
    public void completeClass(String name, Value cls) {
        // TODO: this does nothing yet.
    }

    public Value makeFunction(String internalName, Value code,
            Variable[] closure, Value[] defaults) {
        // TODO Auto-generated method stub
        return null;
    }

    public Value makeGenerator(Value code, Variable[] closure, Value[] defaults) {
        // TODO Auto-generated method stub
        return null;
    }

    // --- Creation ---

    public Value tuple(Value[] content) {
        return invoke(PythonOperation.MAKE_TUPLE, array(content).result()).result();
    }

    public Value list(Value... content) {
        return invoke(PythonOperation.MAKE_LIST, array(content).result()).result();
    }

    public Value makeMap() {
        return invoke(PythonOperation.MAKE_EMPTY_DICT).result();
    }

    // --- Exit ---

    public abstract void returnPythonValue(Value evaluate);

    public abstract Value yeildValue(Value value);

    public void returnLocalDefinitions() {
        returnValue(frame.locals(access));
    }

    public abstract void mirandaReturn();

    // --------

    public final void exec(Value body, Value globals, Value locals) {
        if (globals == null) {
            globals = frame.globals(access);
            locals = frame.locals(access);
        } else if (locals == null) {
            locals = globals;
        }
        invoke(PythonOperation.EXEC, body, globals, locals);
    }

    public void print(boolean nl, Value... values) {
        for (Value value : values) {
            schedule(invoke(PythonOperation.PRINT, value));
        }
        if (nl) schedule(invoke(PythonOperation.PRINT_NEWLINE));
    }

    public void printTo(Value dest, boolean nl, Value... values) {
        for (Value value : values) {
            schedule(invoke(PythonOperation.PRINT_TO, dest, value));
        }
        if (nl) schedule(invoke(PythonOperation.PRINT_NEWLINE_TO, dest));
    }

    public void printExpression(Value value) {
        schedule(invoke(PythonOperation.PRINT_EXPRESSION, value));
    }

    // --- Operators ---

    public Value inplaceOperator(operatorType op, Value lhs, Value rhs) {
        return schedule(
                invoke(PythonOperation.inplaceOperator(compensateDivision(op)),
                        lhs, rhs)).result();
    }

    public Value binaryOperator(operatorType op, Value left, Value right) {
        return schedule(
                invoke(PythonOperation.binaryOperator(compensateDivision(op)),
                        left, right)).result();
    }

    /**
     * @param op
     * @param operand
     * @return the result of the operator
     */
    public Value unaryOperator(unaryopType op, Value operand) {
        // NOTE: should we do optimizations of 'NOT' here?
        return schedule(invoke(PythonOperation.unaryOperator(op), operand)).result();
    }

    public void delete(Variable variable) {
        store(variable, NULL());
    }

    public void listAppend(Value list, Value value) {
        invoke(PythonOperation.APPEND_LIST, list, value);
    }

    /**
     * @param value
     * @return The string representation of value.
     */
    public Value repr(Value value) {
        return schedule(invoke(PythonOperation.STRING_REPRESENTATION, value)).result();
    }

    /**
     * Create a slice.
     * 
     * @param lower
     * @param step
     * @param upper
     * @return A slice.
     */
    public Value slice(Value lower, Value step, Value upper) {
        return invoke(PythonOperation.GET_SLICE, lower, step, upper).result();
    }

    public Value importName(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    public final void importAll(String module) {
        frame.importAll(access, importName(module));
    }

    public Value importModule(String module, int level) {
        // TODO Auto-generated method stub
        return null;
    }

    public Value importFrom(Value module, String internalName) {
        // TODO Auto-generated method stub
        return null;
    }

    public Value loadGlobalConstant(String name) {
        // TODO Auto-generated method stub
        return null;
    }
}
