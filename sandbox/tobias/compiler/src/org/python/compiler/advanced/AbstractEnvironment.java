package org.python.compiler.advanced;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.python.bytecode.Label;
import org.python.bytecode.VariableContext;
import org.python.compiler.advanced.EnvironmentError.EnvironmentProblem;

abstract class AbstractEnvironment implements Environment, EnvironmentInfo, CodeInfo, ScopeInfo {

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

    /**
     * The policy that decides how 
     */
    protected CompilerPolicy policy;

    AbstractEnvironment(Environment parent, CompilerPolicy policy) {
        this.parent = parent;
        this.policy = policy;
        if (parent != null) {
            flags = parent.getCompilerFlags();
        }
    }

    public void markHasExec() {
        flags.add(CompilerFlag.HAVE_EXEC);
    }
    
    public void markForStarImport() {
        flags.add(CompilerFlag.IMPORT_STAR);
    }

    public GlobalEnvironment getGlobalEnvironment() {
        return parent.getGlobalEnvironment();
    }

    public void addAssignment(String name) {
        if (!(globals.contains(name) || nonlocals.contains(name))) {
            locals.add(name);
        }
    }

    public void addReference(String name) {
        if (!(globals.contains(name) || enclosing.contains(name))) {
            locals.add(name);
        }
    }

    public void addEntryPoint(Label entry) {
        error(EnvironmentError.ILLEGAL_GENERATOR);
    }

    public boolean isReenterable() {
        return false;
    }

    public Iterable<Label> getEntryPoints() {
        throw new UnsupportedOperationException("Only function environments have entry points.");
    }

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

    public void markAsNonlocal(String name) {
        if (globals.contains(name)) {
            error(EnvironmentError.BOTH_NONLOCAL_AND_GLOBAL);
        } else {
            nonlocals.add(name);
            enclosing.add(name);
            locals.remove(name);
        }
    }

    public void addFuture(String feature) throws Exception {
        error(EnvironmentError.FUTURE_OUT_OF_CONTEXT);
    }
    
    public void done() {
        // TODO: implement this
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
    public VariableContext getVariableContextFor(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    public Iterable<String> closureVariables() {
        // TODO Auto-generated method stub
        return null;
    }

    public Set<CompilerFlag> getCompilerFlags() {
        return Collections.unmodifiableSet(flags);
    }

    // CodeInfo
    public int getArgumentCount() {
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

    public Iterable<String> getArgumentNames() {
        // TODO: implement this
        return null;
    }

    public ScopeInfo getScopeInfo() {
        return this;
    }

    // ScopeInfo
    public Iterable<String> getVariableNames() {
        // TODO: implement this
        return null;
    }

    public Iterable<String> getFreeVariableNames() {
        // TODO: implement this
        return null;
    }

    public Iterable<String> getCellVariableNames() {
        // TODO: implement this
        return null;
    }
}
