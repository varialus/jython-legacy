package org.python.newcompiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import org.python.bytecode.Label;
import org.python.bytecode.VariableContext;
import org.python.newcompiler.EnvironmentError.EnvironmentProblem;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

abstract class AbstractEnvironment implements Environment, EnvironmentInfo, CodeInfo {

    final Environment parent;

    private int lineNumber = -1;

    /**
     * Variables explicitly marked as global variables.
     */
    protected Set<String> globals = new HashSet<String>();

    /**
     * Variables deduces to be local variables. Assigned and not marked as global or nonlocal.
     */
    protected Set<String> locals = new HashSet<String>();

    /**
     * Variables deduced to exist in some enclosing scope. Not necessarily explicitly marked as
     * nonlocal. This set also contains the variables explicitly marked as nonlocal though.
     */
    protected Set<String> enclosing = new HashSet<String>();

    /**
     * Variables explicitly marks ad nonlocal variables.
     */
    protected Set<String> nonlocals = new HashSet<String>();

    /**
     * Compiler flags defined for this environment.
     */
    protected Set<CompilerFlag> flags = EnumSet.noneOf(CompilerFlag.class);

    private List<EnvironmentProblem> trouble = new LinkedList<EnvironmentProblem>();

    AbstractEnvironment(Environment parent) {
        this.parent = parent;
        if (parent != null) {
            flags = parent.getCompilerFlags();
        }
    }

    @Override
    public void markHasExec() {
        flags.add(CompilerFlag.HAVE_EXEC);
    }

    @Override
    public GlobalEnvironment getGlobalEnvironment() {
        return parent.getGlobalEnvironment();
    }

    @Override
    public void addAssignment(String name) {
        if (!(globals.contains(name) || nonlocals.contains(name))) {
            locals.add(name);
        }
    }

    @Override
    public void addReference(String name) {
        if (!(globals.contains(name) || enclosing.contains(name))) {
            locals.add(name);
        }
    }
    
    @Override
    public void addEntryPoint(Label entry) {
        error(EnvironmentError.ILLEGAL_GENERATOR);
    }
    
    @Override
    public boolean isReenterable() {
        return false;
    }
    
    @Override
    public Iterable<Label> getEntryPoints() {
        throw new NotImplementedException();
    }
    
    @Override
    public void markAsGlobal(String name) {
        if (nonlocals.contains(name)) {
            error(EnvironmentError.BOTH_NONLOCAL_AND_GLOBAL);
        } else {
            globals.add(name);
            if (!locals.remove(name)) {
                enclosing.remove(name);
            }
        }
    }

    @Override
    public void markAsNonlocal(String name) {
        if (globals.contains(name)) {
            error(EnvironmentError.BOTH_NONLOCAL_AND_GLOBAL);
        } else {
            nonlocals.add(name);
            enclosing.add(name);
            locals.remove(name);
        }
    }

    @Override
    public void addFuture(String feature) throws Exception {
        error(EnvironmentError.FUTURE_OUT_OF_CONTEXT);
    }

    /**
     * Mark that there is an error in the
     * 
     * @param error
     */
    protected void error(EnvironmentError error) {
        trouble.add(error.visit(lineNumber));
    }

    // EnvironmentInfo
    @Override
    public VariableContext getVariableContextFor(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Iterable<String> closureVariables() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<CompilerFlag> getCompilerFlags() {
        return Collections.unmodifiableSet(flags);
    }

    public int getArgumentCount() {
        // TODO: implement this
        return 0;
    }

    public int getLocalsCount() {
        // TODO: implement this
        return 0;
    }

    public int getMaxStackSize() {
        // TODO: implement this
        return 0;
    }

    public String getFilename() {
        // TODO: implement this
        return null;
    }

    public String getName() {
        // TODO: implement this
        return null;
    }
}
