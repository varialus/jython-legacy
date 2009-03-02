package org.python.compiler.flowgraph;

abstract class Instruction {
    static class NullCheck extends Instruction {
        NullCheck(Variable variable) {
            super(variable);
        }
    }

    static class IteratorNext extends Instruction {
        IteratorNext(Variable iterator) {
            super(iterator);
        }
    }

    static class CoroutineLoad extends Instruction {

    }

    static class OneOf extends Instruction {
        OneOf(Variable trueVar, Variable falseVar) {
            // TODO Auto-generated constructor stub
        }
    }

    static class ValueInstruction extends Instruction {
        ValueInstruction(ValueOperation op, Variable[] variables) {
            super(variables);
        }
    }

    static class VoidInstruction extends Instruction {
        VoidInstruction(VoidOperation op, Variable[] variables) {
            super(variables);
        }
    }

    private final Variable[] variables;

    private Instruction(Variable... variables) {
        this.variables = variables;
    }
}
