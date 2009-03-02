package org.python.compiler;

import java.util.Map;

import org.python.antlr.ast.Name;
import org.python.antlr.ast.Num;
import org.python.antlr.base.expr;
import org.python.compiler.flowgraph.CodeGraph;
import org.python.compiler.flowgraph.Variable;
import org.python.core.CompilerFlags;
import org.python.core.Py;

public class AdvancedPreferences {

    private interface Setting<T> {

    }

    private enum Booleans implements Setting<Boolean> {
        BOOLEAN_CONSTANTS, CORE_CONSTANTS
    }

    private enum Strings implements Setting<String> {
    }

    private enum Numbers implements Setting<Number> {
    }

    private enum Constant {
        NONE(CodeGraph.None(), null) {
            @Override
            public boolean acceptValue(expr value) {
            	// Only allow identity assignments
                return (value instanceof Name)
                        && ((Name) value).getInternalId().equals("None");
            }
        },
        ELLIPSIS(CodeGraph.Ellipsis(), null) {
            @Override
            public boolean acceptValue(expr value) {
            	// Only allow identity assignments
                return (value instanceof Name)
                        && ((Name) value).getInternalId().equals("Ellipsis");
            }
        },
        TRUE(CodeGraph.True(), null) {
            @Override
            public boolean acceptValue(expr value) {
                if (value instanceof Num) {
                    Num num = (Num) value;
                    return num.equals(1) || num.equals(Py.newInteger(1));
                } else if (value instanceof Name) {
                    Name name = (Name) value;
                    return name.getInternalId().equals("True");
                }
                return false;
            }
        },
        FALSE(CodeGraph.False(), null) {
            @Override
            public boolean acceptValue(expr value) {
                if (value instanceof Num) {
                    Num num = (Num) value;
                    return num.equals(0) || num.equals(Py.newInteger(0));
                } else if (value instanceof Name) {
                    Name name = (Name) value;
                    return name.getInternalId().equals("False");
                }
                return false;
            }
        };
        private final Variable flowConst;
		private final Object directConst;

		private Constant(Variable flowConst, Object directConst) {
			this.flowConst = flowConst;
			this.directConst = directConst;
		}

        public abstract boolean acceptValue(expr value);

        public <T> T getValue(Class<T> type) {
        	if (Variable.class.isAssignableFrom(type)) {
        		return type.cast(flowConst);
        	} else {
        		return type.cast(directConst);
        	}
        }
    }
    
    private static class Checker<T> implements ConstantChecker<T> {
		private final Class<T> type;
		private final Constant constant;

		public Checker(Class<T> type, Constant constant) {
			this.type = type;
			this.constant = constant;
		}

		public boolean acceptValue(expr value) {
			return constant.acceptValue(value);
		}

		public T getConstantValue() {
			return constant.getValue(type);
		}
    	
    }

    private <T> T getSetting(Setting<T> boolean_constants, CompilerFlags flags) {
        // TODO Auto-generated method stub
        return null;
    }

    private <T> void doUpdate(CompilerFlags flags, Class<T> type,
            Map<String, ConstantChecker<T>> constNames) {
        // TODO
        if (getSetting(Booleans.CORE_CONSTANTS, flags)) {
            constNames.put("None", new Checker<T>(type, Constant.NONE));
            constNames.put("Ellipsis", new Checker<T>(type, Constant.ELLIPSIS));
        }
        if (getSetting(Booleans.BOOLEAN_CONSTANTS, flags)) {
            constNames.put("True", new Checker<T>(type,Constant.TRUE));
            constNames.put("False", new Checker<T>(type,Constant.FALSE));
        }
    }

	public ConstraintsDefinition<Variable> forFlowGraph() {
		return new ConstraintsDefinition<Variable>(){
			public void update(CompilerFlags flags, Map<String, ConstantChecker<Variable>> constNames) {
				doUpdate(flags, Variable.class, constNames);
			}
		};
	}

	public ConstraintsDefinition<Object> forDirect() {
		return new ConstraintsDefinition<Object>(){
			public void update(CompilerFlags flags, Map<String, ConstantChecker<Object>> constNames) {
				doUpdate(flags, Object.class, constNames);
			}
		};
	}

}
