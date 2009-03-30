package org.python.compiler.advanced.flowgraph;


/**
 * Represents the data needed in between setting up a new scope and actually
 * loading it. (with the definition of it in between taking place in another
 * {@link FlowGraphGenerator}).
 * 
 * @author Tobias Ivarsson
 */
public class GeneratedCodeState<PRODUCT> {

    private final PRODUCT name;
    private final PRODUCT closureORbases;
    private final PRODUCT defaultsORclosure;
    private final PRODUCT[] decorators;

    GeneratedCodeState(PRODUCT name, PRODUCT closureORbases,
            PRODUCT defaultsORclosure) {
        this(name, closureORbases, defaultsORclosure, null);
    }

    GeneratedCodeState(PRODUCT name, PRODUCT closureORbases,
            PRODUCT defaultsORclosure, PRODUCT[] decorators) {
        this.name = name;
        this.closureORbases = closureORbases;
        this.defaultsORclosure = defaultsORclosure;
        this.decorators = decorators;
    }

    PRODUCT name() {
        return name;
    }

    PRODUCT funcClosure() {
        return closureORbases;
    }

    PRODUCT classBases() {
        return closureORbases;
    }

    PRODUCT funcDefaults() {
        return defaultsORclosure;
    }

    PRODUCT generatorStartArg() {
        return defaultsORclosure;
    }

    PRODUCT classClosure() {
        return defaultsORclosure;
    }

    PRODUCT[] decorators() {
        return decorators;
    }

}
