package org.python.compiler.advanced;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.python.antlr.PythonTree;
import org.python.antlr.ast.BinOp;
import org.python.antlr.ast.Name;
import org.python.antlr.ast.Num;
import org.python.antlr.ast.Str;
import org.python.antlr.ast.Tuple;
import org.python.antlr.ast.UnaryOp;
import org.python.antlr.ast.operatorType;
import org.python.antlr.ast.unaryopType;
import org.python.antlr.base.expr;
import org.python.antlr.base.slice;

public final class ConstantPool {
    private final Map<String, Constant> map = new LinkedHashMap<String, Constant>();
    private final Map<String, ConstantChecker> constants = new HashMap<String, ConstantChecker>();

    private enum Builtin {
        True {
            @Override
            <RESULT> RESULT load(ConstantOwner<RESULT> owner) {
                return owner.True();
            }
        },
        False {
            @Override
            <RESULT> RESULT load(ConstantOwner<RESULT> owner) {
                return owner.False();
            }
        },
        None {
            @Override
            <RESULT> RESULT load(ConstantOwner<RESULT> owner) {
                return owner.None();
            }
        },
        Ellipsis {
            @Override
            <RESULT> RESULT load(ConstantOwner<RESULT> owner) {
                return owner.Ellipsis();
            }
        };
        abstract <RESULT> RESULT load(ConstantOwner<RESULT> owner);

        Constant in(ConstantPool pool) {
            return new Constant(pool) {
                @Override
                <RESULT> RESULT loadFrom(ConstantOwner<RESULT> owner) {
                    return Builtin.this.load(owner);
                }

                @Override
                public String toString() {
                    return "Constant:" + Builtin.this.toString();
                }
            };
        }
    }

    public ConstantPool(Collection<ConstantChecker> constants) {
        for (ConstantChecker constant : constants) {
            this.constants.put(constant.name(), constant);
        }
    }

    public <RESULT> void accept(ConstantCodeGenerator<RESULT> builder) {
        for (Constant constant : map.values()) {
            constant.build(builder);
        }
    }

    Constant getConstant(PythonTree expression) {
        // TODO: improve the pre-checking
        if (expression instanceof expr) {
            expr e = (expr) expression;
        } else if (expression instanceof slice) {
            slice s = (slice) expression;
        } else {
            return null;
        }
        return map.get(expression.toStringTree());
    }

    boolean isConstant(String id) {
        return constants.containsKey(id);
    }

    boolean isConstantWithUnacceptedAssignment(String id, expr value) {
        ConstantChecker checker = constants.get(id);
        if (checker != null) {
            return !checker.acceptValue(value);
        }
        return false;
    }

    void createConstant(expr node) throws Exception {
        buildConstant(node);
    }

    void createConstant(slice node) throws Exception {
        buildConstant(node);
    }

    private Constant buildConstant(PythonTree node) {
        String key = node.toStringTree();
        Constant constant = map.get(key);
        if (constant != null) {
            return constant;
        } else if (node instanceof Name) {
            constant = name(((Name) node).getInternalId());
        } else if (node instanceof Num) {
            constant = number(((Num) node).getInternalN());
        } else if (node instanceof Str) {
            constant = string(((Str) node).getInternalS());
        } else if (node instanceof Tuple) {
            List<expr> elts = ((Tuple) node).getInternalElts();
            Constant[] children = new Constant[elts.size()];
            for (int i = 0; i < children.length; i++) {
                Constant child = buildConstant(elts.get(i));
                if (child == null) return null;
                children[i] = child;
            }
            constant = tuple(children);
        } else if (node instanceof UnaryOp) {
            UnaryOp op = (UnaryOp) node;
            constant = buildConstant(op.getInternalOperand());
            if (constant != null)
                constant = unary(op.getInternalOp(), constant);
        } else if (node instanceof BinOp) {
            BinOp op = (BinOp) node;
            Constant left = buildConstant(op.getInternalLeft());
            Constant right = buildConstant(op.getInternalRight());
            if (left != null && right != null) {
                constant = binary(op.getInternalOp(), left, right);
            }
        } else {
            return null;
        }
        if (constant != null) {
            map.put(key, constant);
        }
        return constant;
    }

    private Constant name(final String id) {
        ConstantChecker checker = constants.get(id);
        if (checker != null) {
            try {
                return Builtin.valueOf(id).in(this);
            } catch (IllegalArgumentException ex) {
            } catch (NullPointerException ex) {
            }
            return new Constant(this) {
                @Override
                <RESULT> RESULT loadFrom(ConstantOwner<RESULT> owner) {
                    // TODO Auto-generated method stub
                    return null;
                }

                @Override
                public String toString() {
                    return "Constant:" + id;
                };
            };
        }
        return null;
    }

    private Constant string(final Object str) {
        return new ObjectConstant() {
            @Override
            <RESULT> void build(ConstantCodeGenerator<RESULT> builder) {
                builder.string(this, str.toString());
            }

            @Override
            public String toString() {
                return "Constant:Str[" + str + "]";
            }
        };
    }

    private Constant number(final Object num) {
        return new ObjectConstant() {
            @Override
            <RESULT> void build(ConstantCodeGenerator<RESULT> builder) {
                // TODO: find out the type of num, and call appropriate method
            }

            @Override
            public String toString() {
                return "Constant:Num[" + num + "]";
            }
        };
    }

    private Constant tuple(final Constant[] children) {
        return new ObjectConstant() {
            @Override
            <RESULT> void build(ConstantCodeGenerator<RESULT> builder) {
                RESULT[] tuple = builder.resultArray(children.length);
                for (int i = 0; i < children.length; i++) {
                    tuple[i] = builder.loadConstant(children[i]);
                }
                builder.tuple(this, tuple);
            }

            @Override
            public String toString() {
                return "Constant:Tuple" + Arrays.toString(children);
            }
        };
    }

    private Constant binary(operatorType op, Constant left, Constant right) {
        // Type check
        switch (op) {
        case BitAnd: // II
        case BitOr: // II
        case BitXor: // II
        case LShift: // II
        case RShift: // II
            if (!(left.isInt() && right.isInt())) {
                return null;
            }
            break;
        case Mult: // NN, SI, IS
            if (left.isStr() || right.isStr()) { // one string
                if (left.isInt() || right.isStr()) { // one integer
                    break;
                }
                return null;
            }
            // Same type only
        case Add: // NN, SS
        case Mod: // NN, SS
            if (left.isStr() || right.isStr()) {
                if (left.isStr() && right.isStr()) {
                    break;
                }
            }
        case Pow: // NN
        case Sub: // NN
        case Div: // NN
        case FloorDiv: //NN
            if (left.isNum() && right.isNum()) {
                break;
            }
        default:
            return null;
        }
        // Operation
        switch (op) {
        case Add:
        case Sub:
        case Mult:
        case Div:
        case Mod:
        case Pow:
        case LShift:
        case RShift:
        case BitOr:
        case BitXor:
        case BitAnd:
        case FloorDiv:
        default:
            return null;
        }
    }

    private Constant unary(unaryopType op, Constant constant) {
        // TODO: implement this
        switch (op) {
        case Invert: // I
            if (constant.isInt()) {
                return null;
            } else {
                return null;
            }
        case Not:
            if (constant.isTrue()) {
                return Builtin.True.in(this);
            } else {
                return Builtin.False.in(this);
            }
        case UAdd:
        case USub:
            if (constant.isNum()) {

            }
        default:
        }
        return null;
    }

    private abstract class ObjectConstant extends Constant {
        public ObjectConstant() {
            super(ConstantPool.this);
        }
        
        @Override
        abstract <RESULT> void build(ConstantCodeGenerator<RESULT> builder);

        @Override
        <RESULT> RESULT loadFrom(ConstantOwner<RESULT> owner) {
            return owner.loadConstant(this);
        }
    }
}
