package org.python.bytecode;

import java.util.Set;

import org.python.newcompiler.CodeInfo;
import org.python.newcompiler.CompilerFlag;

public class BytecodeInfo implements CodeInfo {

    private final String name;

    private final String filename;

    private final int argcount;

    private final int nlocals;

    private final int stacksize;

    private final Set<CompilerFlag> flags;

    public BytecodeInfo(String name,
                        String filename,
                        int argcount,
                        int nlocals,
                        int stacksize,
                        int flags) {
        this.name = name;
        this.filename = filename;
        this.argcount = argcount;
        this.nlocals = nlocals;
        this.stacksize = stacksize;
        this.flags = CompilerFlag.parseFlags(flags);
    }

    @Override
    public int getArgumentCount() {
        return argcount;
    }

    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    public int getLocalsCount() {
        return nlocals;
    }

    @Override
    public int getMaxStackSize() {
        return stacksize;
    }

    @Override
    public String getName() {
        return name;
    }

    public Set<CompilerFlag> getCompilerFlags() {
        return flags;
    }
}
