package org.python.bytecode;

import java.util.HashMap;
import java.util.Map;

/**
 * This enum represents the different versions of Python byte code with corresponding magic numbers
 * and available opcodes.
 * 
 * @author Tobias Ivarsson
 */
public enum BytecodeVersion {
    /** Release13 */
    Python_1_3(11913),
    /** Release14 */
    Python_1_4(5892),
    /*
     * Beginning of new magic number format:
     * 
     * year-1995, month, day
     */
    // Python 1.5
    /** Release15 - format finalized 1997-01-21 */
    Python_1_5(20121),
    /** Release151p1 - format finalized 1997-01-21 */
    // Python 1.6
    /** Release16 - format finalized 2000-04-28 */
    Python_1_6(50428),
    // Python 2.0
    /** Release20 - format finalized 2000-08-23 */
    Python_2_0(50823),
    // Python 2.1
    /** Release21 - format finalized 2001-02-02 */
    Python_2_1(60202),
    // Python 2.2
    /** Release22 - format finalized 2001-07-17 */
    Python_2_2(60717),
    // Python 2.3
    /** Release23 - format finalized 2001-07-17 */
    Python_2_3a0__1(62011),
    /** TODO */
    Python_2_3a0__2(62021),
    /** TODO */
    Python_2_3a0__3(62011),
    // Python 2.4
    /** TODO */
    Python_2_4a0(62041),
    /** TODO */
    Python_2_4a3(62051),
    /** TODO */
    Python_2_4b1(62061),
    // Python 2.5
    /** TODO */
    Python_2_5a0(62071),
    /** The AST-branch */
    Python_2_5a0__ast(62081),
    /** with statement */
    Python_2_5a0__with(62091),
    /** changed {@link BytecodeInstruction#WITH_CLEANUP} opcode */
    Python_2_5a0__with_changed(62092),
    /** fix of wrong code for <code>for x, in ...</code> */
    Python_2_5b3__fix1(62101),
    /** fix of wrong code for <code>x += yield</code> */
    Python_2_5b3__fix2(62111),
    /**
     * fix of wrong lnotab with for loops and storing constants that should have been removed
     */
    Python_2_5c1(62121),
    /** fix of wrong code for <code>for x, in ...</code> in listcomp/genexp */
    Python_2_5c2(62131),
    // Python 2.6
    /** peephole optimizations and {@link BytecodeInstruction#STORE_MAP} opcode */
    Python_2_6a0(62151),
    // Python 3.0
    /** Python 3000, initial branch */
    Python_3000(3000),
    /** removed {@link BytecodeInstruction#UNARY_CONVERT} */
    Python_3000__remove_convert(3010),
    /** added {@link BytecodeInstruction#BUILD_SET} */
    Python_3000__build_set(3020),
    /** added keyword-only parameters */
    Python_3000__keyword_only(3030),
    /** added signature annotations */
    Python_3000__signature_annotations(3040),
    /** print becomes a function */
    Python_3000__print_function(3050),
    /** PEP 3115: metaclass syntax */
    Python_3000__PEP3115(3060),
    /** PEP 3109: raise changes */
    Python_3000__PEP3109(3070),
    /** PEP 3137: make __file__ and __name__ unicode */
    Python_3000__PEP3137(3080),
    /** killed str8 interning */
    Python_3000__kill_str8_intern(3090),
    /**
     * merge from 2.6a0
     * 
     * @see #Python_2_6a0
     */
    Python_3000__2_6a0(3100),
    /** __file__ points to source file */
    Python_3000__file(3102);

    /**
     * The magic number of this Python byte code representation.
     */
    public final int magicNumber;

    private final Map<Character, BytecodeInstruction> opCodes = new HashMap<Character, BytecodeInstruction>();

    private static final Map<Integer, BytecodeVersion> magicNumbers = new HashMap<Integer, BytecodeVersion>();

    private BytecodeVersion(int magic) {
        this.magicNumber = magic;
    }

    void set(char opCode, BytecodeInstruction instruction) {
        opCodes.put(opCode, instruction);
    }

    /**
     * Get the {@link BytecodeInstruction} object of the specified opcode in this
     * {@link BytecodeVersion}.
     * 
     * @param opCode
     *            The opcode to get the {@link BytecodeInstruction} for.
     * @return The {@link BytecodeInstruction} that corresponds to the given opcode.
     */
    public BytecodeInstruction get(char opCode) {
        return opCodes.get(opCode);
    }

    /**
     * Feeds a visitor by reading opcodes from a char stream.
     * 
     * @param visitor
     *            The visitor to feed with instruction information.
     * @param reader
     *            The char stream to read opcodes from.
     */
    public void traverse(RawBytecodeVisitor visitor, CharReader reader) {
        BytecodeInstruction instr = null;
        while (instr != BytecodeInstruction.STOP_CODE) {
            if (reader.hasData()) {
                instr = get(reader.read());
            } else {
                instr = BytecodeInstruction.STOP_CODE;
            }
            instr.read(this, visitor, reader);
        }
    }

    /**
     * Get the {@link BytecodeVersion} given a corresponding magic number.
     * 
     * @param magic
     *            The magic number to get the {@link BytecodeVersion} for.
     * @return The {@link BytecodeVersion} associated with the magic number.
     */
    public static BytecodeVersion getVersion(int magic) {
        if (magicNumbers.isEmpty()) {
            for (BytecodeVersion version : values()) {
                magicNumbers.put(version.magicNumber, version);
            }
        }
        return magicNumbers.get(magic);
    }
}
