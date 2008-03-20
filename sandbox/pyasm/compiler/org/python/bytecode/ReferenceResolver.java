package org.python.bytecode;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.python.core.PyObject;
import org.python.newcompiler.YieldPoint;
import org.python.newcompiler.bytecode.BytecodeCompiler;

/**
 * 
 * @author Tobias Ivarsson
 */
public class ReferenceResolver implements CharReader, RawBytecodeVisitor {

    private static class JumpInstruction implements Instruction {

        private static enum JumpKind {
            JUMP_IF_FALSE {

                @Override
                void jump(BytecodeVisitor visitor, Label label) {
                    visitor.visitJumpIfFalse(label);
                }
            },
            JUMP_IF_TRUE {

                @Override
                void jump(BytecodeVisitor visitor, Label label) {
                    visitor.visitJumpIfTrue(label);
                }
            },
            REGULAR_JUMP {

                @Override
                void jump(BytecodeVisitor visitor, Label label) {
                    visitor.visitJump(label);
                }
            };

            abstract void jump(BytecodeVisitor visitor, Label label);
        }

        private final Label label;

        private final JumpKind kind;

        JumpInstruction(Label label, boolean on) {
            this.label = label;
            if (on) {
                kind = JumpKind.JUMP_IF_TRUE;
            } else {
                kind = JumpKind.JUMP_IF_FALSE;
            }
        }

        JumpInstruction(Label label) {
            this.label = label;
            this.kind = JumpKind.REGULAR_JUMP;
        }

        public void accept(BytecodeVisitor visitor) {
            kind.jump(visitor, label);
        }
    }

    private CharReader reader;

    private int pos = 0;

    private int lastPos = 0;

    private ConstantStore store;

    private LineNumberTable lnotab;

    private final SortedMap<Integer, Instruction> instructions = new TreeMap<Integer, Instruction>();

    private final Map<Integer, Label> labels = new HashMap<Integer, Label>();

    private int resumePoint = 0;

    private List<YieldPoint> resumeTable = new LinkedList<YieldPoint>();

    public ReferenceResolver(BytecodeVersion version,
                             ConstantStore store,
                             CharReader reader,
                             LineNumberTable lnotab) {
        this.reader = reader;
        this.store = store;
        this.lnotab = lnotab;
        // Consume the reader and build the instruction list
        version.traverse(this, this);
        // Assert that STOP_CODE is visited at the end
        if (this.reader != null) {
            visitStop(BytecodeInstruction.STOP_CODE);
        }
    }

    public boolean hasData() {
        return reader.hasData();
    }

    public char read() {
        pos++;
        return reader.read();
    }

    private Label label(int addr) {
        Label label = labels.get(addr);
        if (label == null) {
            labels.put(addr, label = new Label());
        }
        return label;
    }

    private Label relative(int delta) {
        return label(pos + delta);
    }

    private String lookup(VariableContext context, int index) {
        switch(context){
            case CLOSURE:
                return store.getClosureName(index);
            case GLOBAL:
                return lookupName(index);
            case LOCAL:
                return store.getVariableName(index);
            case UNQUALIFIED:
                return lookupName(index);
            default:
                return null; // TODO: error
        }
    }

    private String lookupName(int index) {
        return store.getName(index);
    }

    private PyObject lookupConstant(int index) {
        return store.getConstant(index);
    }

    /**
     * Traverse the instructions and pass them on to a {@link BytecodeVisitor}.
     * 
     * @param visitor
     *            The visitor that should receive the instructions.
     */
    public void accept(BytecodeVisitor visitor) {
        if (!resumeTable.isEmpty() && visitor instanceof BytecodeCompiler) {
            BytecodeCompiler compiler = (BytecodeCompiler)visitor;
            compiler.visitResumeTable(new Label(), new Iterable<Label>() {

                public Iterator<Label> iterator() {
                    return new Iterator<Label>() {

                        Iterator<YieldPoint> internal = resumeTable.iterator();

                        public boolean hasNext() {
                            return internal.hasNext();
                        }

                        public Label next() {
                            return internal.next().label;
                        }

                        public void remove() {
                            throw new UnsupportedOperationException("cannot remove element from resume table");
                        }
                    };
                }
            });
        }
        for (Map.Entry<Integer, Instruction> mapping : instructions.entrySet()) {
            lnotab.visitInstruction(visitor, mapping.getKey());
            Label label = labels.get(mapping.getKey());
            if (label != null) {
                visitor.visitLabel(label);
            }
            mapping.getValue().accept(visitor);
        }
    }

    public void visitInstruction(Instruction instr) {
        instructions.put(lastPos, instr);
        lastPos = pos;
    }

    public void visitStop(Instruction stop) {
        visitInstruction(stop);
        reader = null;
        store = null;
    }

    public void visitYield() {
        YieldPoint yield = new YieldPoint(new Label(), ++resumePoint);
        resumeTable.add(yield);
        visitInstruction(yield);
    }

    public void visitAbsouteJump(int addr) {
        visitInstruction(new JumpInstruction(label(addr)));
    }

    public void visitJumpIfFalse(int delta) {
        visitInstruction(new JumpInstruction(relative(delta), false));
    }

    public void visitJumpIfTrue(int delta) {
        visitInstruction(new JumpInstruction(relative(delta), true));
    }

    public void visitRelativeJump(int delta) {
        visitInstruction(new JumpInstruction(relative(delta)));
    }

    public void visitContinue(int addr) {
        final Label label = label(addr);
        visitInstruction(new Instruction() {

            public void accept(BytecodeVisitor visitor) {
                visitor.visitContinue(label);
            }
        });
    }

    public void visitDelete(final VariableContext context, int nameIndex) {
        final String name = lookup(context, nameIndex);
        visitInstruction(new Instruction() {

            public void accept(BytecodeVisitor visitor) {
                visitor.visitDelete(context, name);
            }
        });
    }

    public void visitDeleteAttribute(int nameIndex) {
        final String name = lookupName(nameIndex);
        visitInstruction(new Instruction() {

            public void accept(BytecodeVisitor visitor) {
                visitor.visitDeleteAttribute(name);
            }
        });
    }

    public void visitForIteration(int delta) {
        final Label label = relative(delta);
        visitInstruction(new Instruction() {

            public void accept(BytecodeVisitor visitor) {
                visitor.visitForIteration(label);
            }
        });
    }

    public void visitImportFrom(int nameIndex) {
        final String name = lookupName(nameIndex);
        visitInstruction(new Instruction() {

            public void accept(BytecodeVisitor visitor) {
                visitor.visitImportFrom(name);
            }
        });
    }

    public void visitImportName(int nameIndex) {
        final String name = lookupName(nameIndex);
        visitInstruction(new Instruction() {

            public void accept(BytecodeVisitor visitor) {
                visitor.visitImportName(name);
            }
        });
    }

    public void visitLoad(final VariableContext context, int nameIndex) {
        final String name = lookup(context, nameIndex);
        visitInstruction(new Instruction() {

            public void accept(BytecodeVisitor visitor) {
                visitor.visitLoad(context, name);
            }
        });
    }

    public void visitLoadAttribute(int nameIndex) {
        final String name = lookupName(nameIndex);
        visitInstruction(new Instruction() {

            public void accept(BytecodeVisitor visitor) {
                visitor.visitLoadAttribute(name);
            }
        });
    }

    public void visitLoadClosure(int nameIndex) {
        final String name = lookup(VariableContext.CLOSURE, nameIndex);
        visitInstruction(new Instruction() {

            public void accept(BytecodeVisitor visitor) {
                visitor.visitLoadClosure(name);
            }
        });
    }

    public void visitLoadConstant(int constIndex) {
        final PyObject constant = lookupConstant(constIndex);
        visitInstruction(new Instruction() {

            public void accept(BytecodeVisitor visitor) {
                visitor.visitLoadConstant(constant);
            }
        });
    }

    public void visitSetupExcept(int delta) {
        final Label label = relative(delta);
        visitInstruction(new Instruction() {

            public void accept(BytecodeVisitor visitor) {
                visitor.visitSetupExcept(label);
            }
        });
    }

    public void visitSetupFinally(int delta) {
        final Label label = relative(delta);
        visitInstruction(new Instruction() {

            public void accept(BytecodeVisitor visitor) {
                visitor.visitSetupFinally(label);
            }
        });
    }

    public void visitSetupLoop(int delta) {
        final Label label = relative(delta);
        visitInstruction(new Instruction() {

            public void accept(BytecodeVisitor visitor) {
                visitor.visitSetupLoop(label);
            }
        });
    }

    public void visitStore(final VariableContext context, int nameIndex) {
        final String name = lookup(context, nameIndex);
        visitInstruction(new Instruction() {

            public void accept(BytecodeVisitor visitor) {
                visitor.visitStore(context, name);
            }
        });
    }

    public void visitStoreAttribute(int nameIndex) {
        final String name = lookupName(nameIndex);
        visitInstruction(new Instruction() {

            public void accept(BytecodeVisitor visitor) {
                visitor.visitStoreAttribute(name);
            }
        });
    }
}
