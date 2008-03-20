package org.python.newcompiler;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

/**
 * 
 * @author Tobias Ivarsson
 */
public enum CompilerFlag {
    /**
     * TODO: document this flag
     */
    CO_OPTIMIZED(0x0001),
    /**
     * TODO: document this flag
     */
    CO_NEWLOCALS(0x0002),
    /**
     * The compiled code block has a varargs argument.
     */
    CO_VARARGS(0x0004),
    /**
     * The compiled code block has a varkeyword argument.
     */
    CO_VARKEYWORDS(0x0008),
    /**
     * The compiled code block is a generator code block.
     */
    CO_GENERATOR(0x0020),
    /**
     * TODO: document this flag
     */
    CO_NESTED(0x0010),
    /**
     * TODO: document this flag
     */
    CO_GENERATOR_ALLOWED(0x1000),
    /**
     * Standard division of integers returns float, truncating division needs to be enforced.
     */
    CO_FUTURE_DIVISION(0x2000),
    /**
     * Absolute import.
     */
    CO_FUTURE_ABSIMPORT(0x4000),
    /**
     * With statement.
     */
    CO_FUTURE_WITH_STATEMENT(0x8000),
    // Compiler internal flags
    /**
     * Compile a code block that contains an exec-statement.
     */
    HAVE_EXEC,
    /**
     * Compile the code block to store its locals in a dictionary.
     */
    MUTABLE_DICT,
    /**
     * The code block uses names without qualified environment.
     */
    NAME_BASED_LOOKUP;

    private Integer bitFlag;

    private CompilerFlag(Integer flag) {
        this.bitFlag = flag;
    }

    private CompilerFlag() {
        this(null);
    }

    public static Set<CompilerFlag> parseFlags(int flags) {
        EnumSet<CompilerFlag> result = EnumSet.noneOf(CompilerFlag.class);
        for (CompilerFlag flag : CompilerFlag.values()) {
            if (flag.bitIn(flags)) {
                result.add(flag);
            }
        }
        return result;
    }

    public static int toBitFlags(Set<CompilerFlag> flags) {
        int res = 0;
        for (CompilerFlag flag : flags) {
            if (flag.bitFlag != null) {
                res = res | flag.bitFlag;
            }
        }
        return res;
    }

    private boolean bitIn(int flags) {
        if (bitFlag == null) {
            return false;
        } else {
            return (flags & bitFlag) != 0;
        }
    }

    public static Set<CompilerFlag> module(Collection<CompilerFlag> flags) {
        return processFlags(flags, MUTABLE_DICT);
    }

    public static Set<CompilerFlag> interactive(Collection<CompilerFlag> flags) {
        return processFlags(flags);
    }

    public static Set<CompilerFlag> expression(Collection<CompilerFlag> flags) {
        return processFlags(flags);
    }

    private static Set<CompilerFlag> processFlags(Collection<CompilerFlag> flags,
                                                  CompilerFlag... more) {
        EnumSet<CompilerFlag> result;
        if (flags.isEmpty()) {
            result = EnumSet.noneOf(CompilerFlag.class);
        } else {
            result = EnumSet.copyOf(flags);
        }
        for (CompilerFlag flag : more) {
            result.add(flag);
        }
        return result;
    }
}
