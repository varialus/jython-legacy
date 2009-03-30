package org.python.compiler.advanced.sea;

import java.util.Map;

import org.python.compiler.advanced.Constant;
import org.python.compiler.advanced.ConstantCodeGenerator;
import org.python.compiler.sea.GraphBuilder;
import org.thobe.compiler.sea.Value;

class NodeSeaConstantGenerator extends ConstantCodeGenerator<Value> {

    private final GraphBuilder generate;
    private final Map<Constant, Value> constants;

    NodeSeaConstantGenerator(GraphBuilder builder,
            Map<Constant, Value> constants) {
        this.generate = builder;
        this.constants = constants;
    }

    public Value loadConstant(Constant token) {
        final Value result = constants.get(token);
        if (result == null) {
            throw new IllegalStateException("The constant " + token
                    + " has not been defined.");
        }
        return result;
    }

    public Value loadConstant(String name) {
        return generate.loadGlobalConstant(name);
    }

    public Value Ellipsis() {
        return generate.Ellipsis();
    }

    public Value False() {
        return generate.False();
    }

    public Value None() {
        return generate.None();
    }

    public Value True() {
        return generate.True();
    }

    @Override
    public void floatingpoint(Constant token, float number) {
        // TODO Auto-generated method stub

    }

    @Override
    public void floatingpoint(Constant token, double number) {
        // TODO Auto-generated method stub

    }

    @Override
    public void floatingpoint(Constant token, String number) {
        // TODO Auto-generated method stub

    }

    @Override
    public void integer(Constant token, int number) {
        // TODO Auto-generated method stub

    }

    @Override
    public void integer(Constant token, long number) {
        // TODO Auto-generated method stub

    }

    @Override
    public void integer(Constant token, String number) {
        // TODO Auto-generated method stub

    }

    @Override
    public void string(Constant token, String string) {
        Value str = generate.string(string);
        constants.put(token, str);
    }

    @Override
    public Value[] resultArray(int size) {
        return new Value[size];
    }

    @Override
    public void tuple(Constant token, Value... children) {
        Value tuple = generate.tuple(children);
        constants.put(token, tuple);
    }
}
