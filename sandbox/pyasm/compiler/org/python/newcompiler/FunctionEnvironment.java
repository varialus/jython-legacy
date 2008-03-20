package org.python.newcompiler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.python.bytecode.Label;

public class FunctionEnvironment extends AbstractEnvironment {

    private List<String> parameters = new ArrayList<String>();

    private boolean generator = false;

    private String vararg = null;

    private String kwarg = null;

    private int parameterCount = 0;

    private List<Label> resumeTable = new LinkedList<Label>();

    public FunctionEnvironment(Environment parent) {
        super(parent);
    }

    public void setVarArg(String name) {
        this.vararg = name;
        parameters.add(name);
    }

    public void setKeywordArg(String name) {
        this.kwarg = name;
        parameters.add(name);
    }

    public void addParameter(String name) {
        if (parameters.add(name)) {
            locals.add(name);
            parameterCount++;
        } else {
            error(EnvironmentError.DUPLICATE_PARAMETER_NAMES);
        }
    }

    @Override
    public void addReference(String name) {
        if (!parameters.contains(name)) {
            super.addReference(name);
        }
    }

    @Override
    public void addEntryPoint(Label entry) {
        this.generator = true;
        resumeTable.add(entry);
    }

    @Override
    public boolean isReenterable() {
        return generator;
    }

    @Override
    public Iterable<Label> getEntryPoints() {
        return Collections.unmodifiableCollection(resumeTable);
    }

    @Override
    public void markAsGlobal(String name) {
        if (parameters.contains(name)) {
            error(EnvironmentError.PARAMETER_AS_GLOBAL);
        } else {
            super.markAsGlobal(name);
        }
    }

    @Override
    public void markAsNonlocal(String name) {
        if (parameters.contains(name)) {
            error(EnvironmentError.PARAMETER_AS_NONLOCAL);
        } else {
            super.markAsNonlocal(name);
        }
    }
}
