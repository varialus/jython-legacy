package org.python.compiler.advanced;

import java.util.Collection;
import java.util.LinkedList;

import org.python.antlr.ast.Name;
import org.python.antlr.ast.Num;
import org.python.antlr.base.expr;
import org.python.core.CompilerFlags;
import org.python.core.Py;

public class Preferences implements ConstraintsDefinition {
    private interface Preference<T> {
        T value(); // FIXME: temporary solution
    }

    private enum BooleanSetting implements Preference<Boolean> {
        BOOLEAN_CONSTANTS, CORE_CONSTANTS;

        public Boolean value() {
            return false; // FIXME: temporary solution
        }
    }

    private enum Constant implements ConstantChecker {
        None {
            @Override
            public boolean acceptValue(expr value) {
                // Only allow identity assignments
                return (value instanceof Name)
                        && ((Name) value).getInternalId().equals("None");
            }
        },
        Ellipsis {
            @Override
            public boolean acceptValue(expr value) {
                // Only allow identity assignments
                return (value instanceof Name)
                        && ((Name) value).getInternalId().equals("Ellipsis");
            }
        },
        True {
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
        False {
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

        public abstract boolean acceptValue(expr value);
    }

    private <T> T getSetting(Preference<T> setting, CompilerFlags flags) {
        // TODO Auto-generated method stub
        return setting.value(); // FIXME: temporary solution
    }

    public Collection<ConstantChecker> apply(CompilerFlags flags) {
        Collection<ConstantChecker> result = new LinkedList<ConstantChecker>();
        if (getSetting(BooleanSetting.BOOLEAN_CONSTANTS, flags)) {
            result.add(Constant.True);
            result.add(Constant.False);
        }
        if (getSetting(BooleanSetting.CORE_CONSTANTS, flags)) {
            result.add(Constant.Ellipsis);
            result.add(Constant.None);
        }
        return result;
    }
}
