package org.python.compiler.advanced;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.python.antlr.ParseException;
import org.python.antlr.PythonTree;
import org.python.antlr.ast.Expr;
import org.python.antlr.ast.ImportFrom;
import org.python.antlr.ast.Str;
import org.python.antlr.ast.VisitorBase;
import org.python.antlr.ast.alias;
import org.python.antlr.base.stmt;
import org.python.core.CompilerFlags;
import org.python.core.FutureFeature;
import org.python.core.Pragma;
import org.python.core.PragmaReceiver;
import org.python.core.Pragma.PragmaModule;

class PragmaParser {

    private final Map<String, PragmaModule> pragmas = new HashMap<String, PragmaModule>();

    PragmaParser(PragmaModule... modules) {
        for (PragmaModule module : modules) {
            pragmas.put(module.name, module);
        }
    }

    public void checkPragma(ImportFrom node) {
        if (node.from_future_checked) return;
        if (pragmas.containsKey(node.getInternalModule())) {
            throw new ParseException("from " + node.getInternalModule()
                    + " imports must occur " + "at the beginning of the file",
                    node);
        }
        node.from_future_checked = true;
    }

    public void parse(List<? extends stmt> body, CompilerFlags flags) throws Exception {
        PragmaVisitor visitor = new PragmaVisitor(flags);
        for (stmt stmt : body) {
            if (!stmt.accept(visitor)) {
                break;
            }
        }
    }

    private class PragmaVisitor extends VisitorBase<Boolean> {

        Boolean stringResult = Boolean.TRUE;
        private final PragmaReceiver receiver;

        PragmaVisitor(final CompilerFlags flags) {
            receiver = new PragmaReceiver() {

                public /*<T extends Enum<T> & Pragma>*/ void add(Pragma pragma) {
                    if (pragma instanceof FutureFeature) {
                        ((FutureFeature) pragma).setFlag(flags);
                    } else {
                        System.err.println("Warning: Need to implement handling of other pragmas.");
                    }
                }

            };
        }

        @Override
        public void traverse(PythonTree node) throws Exception {
            // do nothing
        }

        @Override
        protected Boolean unhandled_node(PythonTree node) throws Exception {
            return Boolean.FALSE;
        }

        @Override
        public Boolean visitImportFrom(ImportFrom node) throws Exception {
            PragmaModule module = pragmas.get(node.getInternalModule());
            List<alias> names = node.getInternalNames();
            if (names == null || names.isEmpty()) {
                module.getStarPragma().addTo(receiver);
            } else {
                for (alias a : names) {
                    module.getPragma(a.getInternalName()).addTo(receiver);
                }
            }
            return Boolean.FALSE;
        }

        // Allow a doc string before pragmas

        @Override
        public Boolean visitExpr(Expr node) throws Exception {
            return node.getInternalValue().accept(this);
        }

        @Override
        public Boolean visitStr(Str node) throws Exception {
            try {
                return stringResult;
            } finally {
                // only allow ONE doc string
                stringResult = Boolean.FALSE;
            }
        }

    }

}
