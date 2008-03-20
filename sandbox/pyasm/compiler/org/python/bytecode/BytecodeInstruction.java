package org.python.bytecode;

public enum BytecodeInstruction implements Instruction, RawInstruction {
    /** Marks the end of the byte code segment. */
    STOP_CODE(0, false, null, null) {

        @Override
        public void acceptRaw(RawBytecodeVisitor visitor) {
            visitor.visitStop(this);
        }

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitStop();
        }
    },
    /**
     * Remove the top stack element.
     * 
     * Stack: element -> -
     */
    POP_TOP(1, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitPop();
        }
    },
    /**
     * Swap the top two stack elements.
     * 
     * Stack: element1, element2 -> element2, element1
     */
    ROT_TWO(2, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitRot(2);
        }
    },
    /**
     * Rotate down the top stack element to position three.
     * 
     * Stack: element1, element2, element3 -> element2, element3, element1
     */
    ROT_THREE(3, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitRot(3);
        }
    },
    /**
     * Duplicate the top stack element.
     * 
     * Stack: element -> element, element
     */
    DUP_TOP(4, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitDup(1);
        }
    },
    /**
     * Rotate down the top stack element to position four.
     * 
     * Stack: element1, element2, element3, element4 -> element2, element3, element4, element1
     */
    ROT_FOUR(5, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitRot(4);
        }
    },
    /** Do-nothing instruction. */
    NOP(9, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitNop();
        }
    },
    /**
     * Apply the positive operator to the top stack element.
     * 
     * Stack: element -> +element
     */
    UNARY_POSITIVE(10, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitUnaryOperator(UnaryOperator.POSITIVE);
        }
    },
    /**
     * Apply the negative operator to the top stack element.
     * 
     * Stack: element -> -element
     */
    UNARY_NEGATIVE(11, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitUnaryOperator(UnaryOperator.NEGATIVE);
        }
    },
    /**
     * Apply the not operator to the top stack element.
     * 
     * Stack: element -> not element
     */
    UNARY_NOT(12, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitUnaryOperator(UnaryOperator.NOT);
        }
    },
    /**
     * Apply the conversion operator to the top stack element.
     * 
     * Stack: element -> repr(element)
     */
    UNARY_CONVERT(13, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitUnaryOperator(UnaryOperator.CONVERT);
        }
    },
    /**
     * Apply the inversion operator to the top stack element.
     * 
     * Stack: element -> ~element
     */
    UNARY_INVERT(15, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitUnaryOperator(UnaryOperator.INVERT);
        }
    },
    /**
     * TODO: document this
     */
    SET_ADD(17, false, BytecodeVersion.Python_3000__build_set, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitSetAdd();
        }
    },
    /**
     * Append the top stack element to the list in the second stack element.
     * 
     * Stack: element1, element2 -> -
     * 
     * Side effect: list.append(element2, element1)
     */
    LIST_APPEND(18, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitListAppend();
        }
    },
    /**
     * Raise the second stack element to the power of the top stack element.
     * 
     * Stack: element1, element2 -> element2 ** element1
     */
    BINARY_POWER(19, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitBinaryOperator(BinaryOperator.POWER);
        }
    },
    /**
     * Multiply the second stack element by the top stack element.
     * 
     * Stack: element1, element2 -> element2 * element1
     */
    BINARY_MULTIPLY(20, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitBinaryOperator(BinaryOperator.MULTIPLY);
        }
    },
    /**
     * Divide the second stack element by the top stack element.
     * 
     * Stack: element1, element2 -> element2 / element1
     */
    BINARY_DIVIDE(21, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitBinaryOperator(BinaryOperator.DIVIDE);
        }
    },
    /**
     * Compute the top stack element modulo of the second stack element.
     * 
     * Stack: element1, element2 -> element2 % element1
     */
    BINARY_MODULO(22, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitBinaryOperator(BinaryOperator.MODULO);
        }
    },
    /**
     * Add the top stack element to the second stack element.
     * 
     * Stack: element1, element2 -> element2 + element1
     */
    BINARY_ADD(23, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitBinaryOperator(BinaryOperator.ADD);
        }
    },
    /**
     * Remove the top stack element from the second stack element.
     * 
     * Stack: element1, element2 -> element2 - element1
     */
    BINARY_SUBTRACT(24, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitBinaryOperator(BinaryOperator.SUBTRACT);
        }
    },
    /**
     * Load the top stack element subscript of the second stack element.
     * 
     * Stack: element1, element2 -> element2[element1]
     */
    BINARY_SUBSCR(25, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitLoadSubscript();
        }
    },
    /**
     * Floor divide the second stack element by the top stack element.
     * 
     * Stack: element1, element2 -> element2 // element1
     */
    BINARY_FLOOR_DIVIDE(26, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitBinaryOperator(BinaryOperator.FLOOR_DIVIDE);
        }
    },
    /**
     * True divide the second stack element by the top stack element.
     * 
     * Stack: element1, element2 -> element2 / element1
     */
    BINARY_TRUE_DIVIDE(27, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitBinaryOperator(BinaryOperator.TRUE_DIVIDE);
        }
    },
    /**
     * Floor divide the second stack element by the top stack element in-place.
     * 
     * Stack: element1, element2 -> element2 // element1
     */
    INPLACE_FLOOR_DIVIDE(28, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitInplaceOperator(BinaryOperator.FLOOR_DIVIDE);
        }
    },
    /**
     * True divide the second stack element by the top stack element in-place.
     * 
     * Stack: element1, element2 -> element2 / element1
     */
    INPLACE_TRUE_DIVIDE(29, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitInplaceOperator(BinaryOperator.TRUE_DIVIDE);
        }
    },
    /**
     * Load the entire sequence slice of the top stack element.
     * 
     * Stack: element -> element[:]
     */
    SLICE__0(30, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitLoadSlice(SliceMode.PLUS_0);
        }
    },
    /**
     * Load the slice from the top stack element of the second stack element.
     * 
     * Stack: element1, element2 -> element2[element1:]
     */
    SLICE__1(31, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitLoadSlice(SliceMode.PLUS_1);
        }
    },
    /**
     * Load the slice to the top stack element of the second stack element.
     * 
     * Stack: element1, element2 -> element2[:element1]
     */
    SLICE__2(32, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitLoadSlice(SliceMode.PLUS_2);
        }
    },
    /**
     * Load the slice from the second stack element to the top stack element of the third stack
     * element.
     * 
     * Stack: element1, element2, element3 -> element3[element2:element1]
     */
    SLICE__3(33, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitLoadSlice(SliceMode.PLUS_3);
        }
    },
    /**
     * Store the second stack element in the entire sequence slice of the top stack element.
     * 
     * Stack: element1, element2 -> -
     * 
     * Side effect: element1[:] = element2
     */
    STORE_SLICE__0(40, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitStoreSlice(SliceMode.PLUS_0);
        }
    },
    /**
     * Store the third stack element in the slice from the first stack element of the second stack
     * element.
     * 
     * Stack: element1, element2, element3 -> -
     * 
     * Side effect: element2[element1:] = element3
     */
    STORE_SLICE__1(41, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitStoreSlice(SliceMode.PLUS_1);
        }
    },
    /**
     * Store the third stack element in the slice to the first stack element of the second stack
     * element.
     * 
     * Stack: element1, element2, element3 -> -
     * 
     * Side effect: element2[:element1] = element3
     */
    STORE_SLICE__2(42, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitStoreSlice(SliceMode.PLUS_2);
        }
    },
    /**
     * Store the fourth stack element in the slice from the second stack element to the first stack
     * element of the third stack element.
     * 
     * Stack: element1, element2, element3, element4 -> -
     * 
     * Side effect: element3[element2:element1] = element4
     */
    STORE_SLICE__3(43, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitStoreSlice(SliceMode.PLUS_3);
        }
    },
    /**
     * Delete the entire sequence slice of the top stack element.
     * 
     * Stack: element1 -> -
     * 
     * Side effect: del element3[:]
     */
    DELETE_SLICE__0(50, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitDeleteSlice(SliceMode.PLUS_0);
        }
    },
    /**
     * Delete the slice from the top stack element of the second stack element.
     * 
     * Stack: element1, element2 -> -
     * 
     * Side effect: element2[element1:]
     */
    DELETE_SLICE__1(51, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitDeleteSlice(SliceMode.PLUS_1);
        }
    },
    /**
     * Delete the slice to the top stack element of the second stack element.
     * 
     * Stack: element1, element2 -> -
     * 
     * Side effect: element2[:element1]
     */
    DELETE_SLICE__2(52, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitDeleteSlice(SliceMode.PLUS_2);
        }
    },
    /**
     * Delete the slice from the second stack element to the top stack element of the third stack
     * element.
     * 
     * Stack: element1, element2, element3 -> -
     * 
     * Side effect: element3[element2:element1]
     */
    DELETE_SLICE__3(53, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitDeleteSlice(SliceMode.PLUS_3);
        }
    },
    /**
     * Store a key and value pair in a dictionary. Pops the key and value while leaving the
     * dictionary on the stack.
     * 
     * Stack: elementA, elementB, elementC -> elementX
     * 
     * Side effect: elementX[elementY] = elementZ
     * 
     * TODO: mapping from A,B,C to X,Y,Z
     */
    STORE_MAP(54, false, BytecodeVersion.Python_2_6a0, null) {
    },
    /**
     * Add the top stack element to the second stack element in-place.
     * 
     * Stack: element1, element2 -> element2 + element1
     */
    INPLACE_ADD(55, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitInplaceOperator(BinaryOperator.ADD);
        }
    },
    /**
     * Subtract the top stack element from the second stack element in-place.
     * 
     * Stack: element1, element2 -> element2 + element1
     */
    INPLACE_SUBTRACT(56, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitInplaceOperator(BinaryOperator.SUBTRACT);
        }
    },
    /**
     * Multiply the second stack element by the top stack element in-place.
     * 
     * Stack: element1, element2 -> element2 * element1
     */
    INPLACE_MULTIPLY(57, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitInplaceOperator(BinaryOperator.MULTIPLY);
        }
    },
    /**
     * Divide the second stack element by the top stack element in-place.
     * 
     * Stack: element1, element2 -> element2 / element1
     */
    INPLACE_DIVIDE(58, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitInplaceOperator(BinaryOperator.DIVIDE);
        }
    },
    /**
     * Compute the top stack element modulo of the second stack element in-place.
     * 
     * Stack: element1, element2 -> element2 % element1
     */
    INPLACE_MODULO(59, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitInplaceOperator(BinaryOperator.MODULO);
        }
    },
    /**
     * Store the third stack element in the top stack element subscript of the second stack element.
     * 
     * Stack: element1, element2, element3 -> -
     * 
     * Side effect: element2[element1] = element3
     */
    STORE_SUBSCR(60, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitStoreSubscript();
        }
    },
    /**
     * Delete the item at the top stack position in the second stack element.
     * 
     * Stack: element1, element2 -> -
     * 
     * Side effect: del element2[element1]
     */
    DELETE_SUBSCR(61, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitDeleteSubscript();
        }
    },
    /**
     * Left shift the second stack element by the top stack element.
     * 
     * Stack: element1, element2 -> element2 << element1
     */
    BINARY_LSHIFT(62, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitBinaryOperator(BinaryOperator.SHIFT_LEFT);
        }
    },
    /**
     * Right shift the second stack element by the top stack element.
     * 
     * Stack: element1, element2 -> element2 >> element1
     */
    BINARY_RSHIFT(63, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitBinaryOperator(BinaryOperator.SHIFT_RIGHT);
        }
    },
    /**
     * Compute the logic and between the second stack element and the top stack element.
     * 
     * Stack: element1, element2 -> element2 & element1
     */
    BINARY_AND(64, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitBinaryOperator(BinaryOperator.AND);
        }
    },
    /**
     * Compute the exclusive logic or between the second stack element and the top stack element.
     * 
     * Stack: element1, element2 -> element2 ^ element1
     */
    BINARY_XOR(65, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitBinaryOperator(BinaryOperator.XOR);
        }
    },
    /**
     * Compute the inclusive logic or between the second stack element and the top stack element.
     * 
     * Stack: element1, element2 -> element2 | element1
     */
    BINARY_OR(66, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitBinaryOperator(BinaryOperator.OR);
        }
    },
    /**
     * Raise the second stack element to the power of the top stack element in-place.
     * 
     * Stack: element1, element2 -> element2 ** element1
     */
    INPLACE_POWER(67, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitInplaceOperator(BinaryOperator.POWER);
        }
    },
    /**
     * Get the iterator from the top stack element.
     * 
     * Stack: element -> iter(element)
     */
    GET_ITER(68, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitUnaryOperator(UnaryOperator.ITERATOR);
        }
    },
    /**
     * TODO: document this
     */
    STORE_LOCALS(69, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            // TODO implement this
            super.accept(visitor);
        }
    },
    /**
     * Print the top stack element in interactive mode.
     * 
     * Stack: element -> -
     * 
     * Side effect: print element
     */
    PRINT_EXPR(70, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitPrintExpression();
        }
    },
    /**
     * Print the top stack element.
     * 
     * Stack: element -> -
     * 
     * Side effect: print element,
     */
    PRINT_ITEM(71, false, null, BytecodeVersion.Python_3000) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitPrintItem();
        }
    },
    /**
     * TODO: document this
     */
    LOAD_BUILD_CLASS(71, false, BytecodeVersion.Python_3000, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            // TODO: implement this
            super.accept(visitor);
        }
    },
    /**
     * Print a newline.
     * 
     * Stack: - -> -
     * 
     * Side effect: print
     */
    PRINT_NEWLINE(72, false, null, BytecodeVersion.Python_3000) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitPrintNewline();
        }
    },
    /**
     * Print the second stack element to the file object at the top stack element.
     * 
     * Stack: element1, element2 -> -
     * 
     * Side effect: print >>element1, element2
     */
    PRINT_ITEM_TO(73, false, null, BytecodeVersion.Python_3000) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitPrintItemTo();
        }
    },
    /**
     * Print a newline to the file object at the top stack element.
     * 
     * Stack: element -> -
     * 
     * Side effect: print >>element
     */
    PRINT_NEWLINE_TO(74, false, null, BytecodeVersion.Python_3000) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitPrintNewlineTo();
        }
    },
    /**
     * Left shift the second stack element by the top stack element in-place.
     * 
     * Stack: element1, element2 -> element2 << element1
     */
    INPLACE_LSHIFT(75, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitInplaceOperator(BinaryOperator.SHIFT_LEFT);
        }
    },
    /**
     * Right shift the second stack element by the top stack element in-place.
     * 
     * Stack: element1, element2 -> element2 << element1
     */
    INPLACE_RSHIFT(76, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitInplaceOperator(BinaryOperator.SHIFT_RIGHT);
        }
    },
    /**
     * Compute the logic and between the second stack element and the top stack element in-place.
     * 
     * Stack: element1, element2 -> element2 & element1
     */
    INPLACE_AND(77, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitInplaceOperator(BinaryOperator.AND);
        }
    },
    /**
     * Compute the exclusive logic or between the second stack element and the top stack element
     * in-place.
     * 
     * Stack: element1, element2 -> element2 ^ element1
     */
    INPLACE_XOR(78, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitInplaceOperator(BinaryOperator.XOR);
        }
    },
    /**
     * Compute the inclusive logic or between the second stack element and the top stack element
     * in-place.
     * 
     * Stack: element1, element2 -> element2 | element1
     */
    INPLACE_OR(79, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitInplaceOperator(BinaryOperator.OR);
        }
    },
    /** Break out of the nearest loop. */
    BREAK_LOOP(80, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitBreak();
        }
    },
    /**
     * Invoke the context manager exit function from the top of the stack with the current exception
     * status.
     * 
     * Stack: element -> -
     * 
     * Side effect: element(*sys.exc_info())
     */
    WITH_CLEANUP(81, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitWithCleanup();
        }
    },
    /**
     * Load the local variables to the top of the stack.
     * 
     * Stack: - -> locals()
     */
    LOAD_LOCALS(82, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitLoadLocals();
        }
    },
    /**
     * Return the top stack value.
     */
    RETURN_VALUE(83, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitReturn();
        }
    },
    /**
     * Import all the names from module on the top of the stack.
     * 
     * Stack: module -> -
     * 
     * Side effects: stores a lot of names...
     */
    IMPORT_STAR(84, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitImportAll();
        }
    },
    /**
     * Execute the code element in the third stack element in the global context of the second stack
     * element and the local context of the top stack element.
     * 
     * Stack: element1, element2, element3 -> -
     * 
     * Side effect: exec element3, element2, element1
     */
    EXEC_STMT(85, false, null, BytecodeVersion.Python_3000) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitExec();
        }
    },
    /**
     * TODO: document this
     */
    MAKE_BYTES(85, false, BytecodeVersion.Python_3000, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            // TODO: implement this
            super.accept(visitor);
        }
    },
    /**
     * Yield the top stack value.
     * 
     * Stack: element -> sent-value
     */
    YIELD_VALUE(86, false, null, null) {

        @Override
        public void acceptRaw(RawBytecodeVisitor visitor) {
            visitor.visitYield();
        }
    },
    /** Pop the closest block on the block stack. */
    POP_BLOCK(87, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitPopBlock();
        }
    },
    /** Terminate a finally clause. */
    END_FINALLY(88, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitEndFinally();
        }
    },
    /**
     * Build a class.
     * 
     * Stack: element1(dict), element2(bases), element3(name) -> class
     */
    BUILD_CLASS(89, false, null, null) {

        @Override
        public void accept(BytecodeVisitor visitor) {
            visitor.visitBuildClass();
        }
    },
    /**
     * Store the top stack value in the variable named co_names[index], where index is the opcode
     * argument.
     * 
     * Stack: element -> -
     * 
     * Side effect: local[co_names[index]] = element
     */
    STORE_NAME(90, true, null, null) {

        @Override
        protected void acceptRaw(RawBytecodeVisitor visitor, int argument) {
            visitor.visitStore(VariableContext.UNQUALIFIED, argument);
        }
    },
    /**
     * Delete variable named co_names[index], where index is the opcode argument.
     * 
     * Stack: - -> -
     * 
     * Side effect: del local[co_names[index]]
     */
    DELETE_NAME(91, true, null, null) {

        @Override
        protected void acceptRaw(RawBytecodeVisitor visitor, int argument) {
            visitor.visitDelete(VariableContext.UNQUALIFIED, argument);
        }
    },
    /**
     * Unpack count arguments from the iterable in the top stack element, where count is the opcode
     * argument.
     * 
     * Stack: element -> [elements]*count
     */
    UNPACK_SEQUENCE(92, true, null, null) {

        @Override
        protected void acceptRaw(RawBytecodeVisitor visitor, final int argument) {
            visitor.visitInstruction(new Instruction() {

                public void accept(BytecodeVisitor visitor) {
                    visitor.visitUnpackSequence(argument, false, 0);
                }
            });
        }
    },
    /**
     * Get the next element from the iterator at the top of the stack, or jump to the relative
     * address given by the opcode argument.
     * 
     * Stack: element1(iterator) -> element1, element2(iterator.next())
     */
    FOR_ITER(93, true, null, null) {

        @Override
        protected void acceptRaw(RawBytecodeVisitor visitor, int argument) {
            visitor.visitForIteration(argument);
        }
    },
    /**
     * TODO: document this
     */
    UNPACK_EX(94, false, null, null) {

        @Override
        protected void acceptRaw(RawBytecodeVisitor visitor, int argument) {
            final int after = argument >> 8;
            final int before = argument % (1 << 8);
            visitor.visitInstruction(new Instruction() {

                public void accept(BytecodeVisitor visitor) {
                    visitor.visitUnpackSequence(before, true, after);
                }
            });
        }
    },
    /**
     * Store the second stack element at the attribute with the name at co_names[index] of the top
     * stack element, where index is the opcode argument.
     * 
     * Stack: element1, element2 -> -
     * 
     * Side effect: setattr(element1, co_names[index], element2)
     */
    STORE_ATTR(95, true, null, null) {

        @Override
        protected void acceptRaw(RawBytecodeVisitor visitor, int argument) {
            visitor.visitStoreAttribute(argument);
        }
    },
    /**
     * Delete the attribute with the name at co_names[index] of the top stack element, where index
     * is the opcode argument.
     * 
     * Stack: element1 -> -
     * 
     * Side effect: delattr(element1, co_names[index])
     */
    DELETE_ATTR(96, true, null, null) {

        @Override
        protected void acceptRaw(RawBytecodeVisitor visitor, int argument) {
            visitor.visitDeleteAttribute(argument);
        }
    },
    /**
     * Store the top stack element in the global variable with the name from co_names[index], where
     * index is the opcode argument.
     * 
     * Stack: element -> -
     * 
     * Side effect: globals()[co_names[index]] = element
     */
    STORE_GLOBAL(97, true, null, null) {

        @Override
        protected void acceptRaw(RawBytecodeVisitor visitor, int argument) {
            visitor.visitStore(VariableContext.GLOBAL, argument);
        }
    },
    /**
     * Deletes the global variable with the name from co_names[index], where index is the opcode
     * argument.
     * 
     * Stack: - -> -
     * 
     * Side effect: del globals()[co_names[index]]
     */
    DELETE_GLOBAL(98, true, null, null) {

        @Override
        protected void acceptRaw(RawBytecodeVisitor visitor, int argument) {
            visitor.visitDelete(VariableContext.GLOBAL, argument);
        }
    },
    /**
     * Duplicate the top X stack elements.
     * 
     * Stack: element1, element2, ... -> element1, element2, ..., element1, element2, ...
     */
    DUP_TOPX(99, true, null, null) {

        @Override
        protected void acceptRaw(RawBytecodeVisitor visitor, final int argument) {
            visitor.visitInstruction(new Instruction() {

                public void accept(BytecodeVisitor visitor) {
                    visitor.visitDup(argument);
                }
            });
        }
    },
    /**
     * Loads the constant in co_consts[index] to the stack, where index is the opcode argument.
     * 
     * Stack: - -> co_consts[index]
     */
    LOAD_CONST(100, true, null, null) {

        @Override
        protected void acceptRaw(RawBytecodeVisitor visitor, int argument) {
            visitor.visitLoadConstant(argument);
        }
    },
    /**
     * Load the value from the variable named co_names[index] to the stack, where index is the
     * opcode argument.
     * 
     * Stack: - -> local[co_names[index]]
     */
    LOAD_NAME(101, true, null, null) {

        @Override
        protected void acceptRaw(RawBytecodeVisitor visitor, int argument) {
            visitor.visitLoad(VariableContext.UNQUALIFIED, argument);
        }
    },
    /**
     * Builds a tuple of the size specified in the opcode argument from that many elements from the
     * stack.
     * 
     * Stack: element1, ... -> tuple(elemen1, ...)
     */
    BUILD_TUPLE(102, true, null, null) {

        @Override
        protected void acceptRaw(RawBytecodeVisitor visitor, final int argument) {
            visitor.visitInstruction(new Instruction() {

                public void accept(BytecodeVisitor visitor) {
                    visitor.visitBuildTuple(argument);
                }
            });
        }
    },
    /**
     * Builds a list of the size specified in the opcode argument from that many elements from the
     * stack.
     * 
     * Stack: element1, ... -> list(elemen1, ...)
     */
    BUILD_LIST(103, true, null, null) {

        @Override
        protected void acceptRaw(RawBytecodeVisitor visitor, final int argument) {
            visitor.visitInstruction(new Instruction() {

                public void accept(BytecodeVisitor visitor) {
                    visitor.visitBuildList(argument);
                }
            });
        }
    },
    /**
     * Build a map. The argument is expected to be zero.
     * 
     * Stack: - -> {}
     */
    BUILD_MAP(104, true, null, BytecodeVersion.Python_3000) {

        @Override
        protected void acceptRaw(RawBytecodeVisitor visitor, final int argument) {
            visitor.visitInstruction(new Instruction() {

                public void accept(BytecodeVisitor visitor) {
                    visitor.visitBuildMap(argument);
                }
            });
        }
    },
    /**
     * TODO: document this
     */
    BUILD_SET(104, false, BytecodeVersion.Python_3000, null) {

        @Override
        protected void acceptRaw(RawBytecodeVisitor visitor, final int argument) {
            visitor.visitInstruction(new Instruction() {

                public void accept(BytecodeVisitor visitor) {
                    visitor.visitBuildSet(argument);
                }
            });
        }
    },
    /**
     * Load the attribute named co_names[index] from the top stack element to the stack.
     * 
     * Stack: element -> getattr(element, co_names[index])
     */
    LOAD_ATTR(105, true, null, BytecodeVersion.Python_3000) {

        @Override
        protected void acceptRaw(RawBytecodeVisitor visitor, int argument) {
            visitor.visitLoadAttribute(argument);
        }
    },
    /**
     * @see #BUILD_MAP
     */
    py3k_BUILD_MAP(105, false, BytecodeVersion.Python_3000, null) {

        @Override
        protected void acceptRaw(RawBytecodeVisitor visitor, int argument) {
            BUILD_MAP.acceptRaw(visitor, argument);
        }
    },
    /**
     * Compare the second stack element to the first stack element using the comparison operator
     * identified by the opcode argument.
     * 
     * Stack: element1, element2 -> element2 OP element1
     */
    COMPARE_OP(106, true, null, BytecodeVersion.Python_3000) {

        @Override
        protected void acceptRaw(RawBytecodeVisitor visitor, int argument) {
            final ComparisonOperator operator = ComparisonOperator.values()[argument];
            visitor.visitInstruction(new Instruction() {

                public void accept(BytecodeVisitor visitor) {
                    visitor.visitCompareOperator(operator);
                }
            });
        }
    },
    /**
     * @see #LOAD_ATTR
     */
    py3k_LOAD_ATTR(106, false, BytecodeVersion.Python_3000, null) {

        @Override
        protected void acceptRaw(RawBytecodeVisitor visitor, int argument) {
            LOAD_ATTR.acceptRaw(visitor, argument);
        }
    },
    /**
     * Imports the module named co_names[index] to the stack, where index is the opcode argument.
     * 
     * Stack: - -> module
     */
    IMPORT_NAME(107, true, null, BytecodeVersion.Python_3000) {

        @Override
        protected void acceptRaw(RawBytecodeVisitor visitor, int argument) {
            visitor.visitImportName(argument);
        }
    },
    /**
     * @see #COMPARE_OP
     */
    py3k_COMPARE_OP(107, false, BytecodeVersion.Python_3000, null) {

        @Override
        protected void acceptRaw(RawBytecodeVisitor visitor, int argument) {
            COMPARE_OP.acceptRaw(visitor, argument);
        }
    },
    /**
     * Import the name co_names[index] from the module at the top of the stack, where index is the
     * opcode argument.
     * 
     * Stack: module -> getattr(module, co_names[index])
     */
    IMPORT_FROM(108, true, null, BytecodeVersion.Python_3000) {

        @Override
        protected void acceptRaw(RawBytecodeVisitor visitor, int argument) {
            visitor.visitImportFrom(argument);
        }
    },
    /**
     * @see #IMPORT_NAME
     */
    py3k_IMPORT_NAME(108, false, BytecodeVersion.Python_3000, null) {

        @Override
        protected void acceptRaw(RawBytecodeVisitor visitor, int argument) {
            IMPORT_NAME.acceptRaw(visitor, argument);
        }
    },
    /**
     * @see #IMPORT_FROM
     */
    py3k_IMPORT_FROM(109, false, BytecodeVersion.Python_3000, null) {

        @Override
        protected void acceptRaw(RawBytecodeVisitor visitor, int argument) {
            IMPORT_FROM.acceptRaw(visitor, argument);
        }
    },
    /**
     * Increase the program counter by the argument of the opcode.
     * 
     * Stack: - -> -
     */
    JUMP_FORWARD(110, true, null, null) {

        @Override
        protected void acceptRaw(RawBytecodeVisitor visitor, int argument) {
            visitor.visitRelativeJump(argument);
        }
    },
    /**
     * Increase the program counter by the argument of the opcode if the element on the top of the
     * stack is false.
     * 
     * Stack: element -> element
     */
    JUMP_IF_FALSE(111, true, null, null) {

        @Override
        protected void acceptRaw(RawBytecodeVisitor visitor, int argument) {
            visitor.visitJumpIfFalse(argument);
        }
    },
    /**
     * Increase the program counter by the argument of the opcode if the element on the top of the
     * stack is true.
     * 
     * Stack: element -> element
     */
    JUMP_IF_TRUE(112, true, null, null) {

        @Override
        protected void acceptRaw(RawBytecodeVisitor visitor, int argument) {
            visitor.visitJumpIfTrue(argument);
        }
    },
    /**
     * Set the program counter to the value of the argument of the opcode.
     * 
     * Stack: - -> -
     */
    JUMP_ABSOLUTE(113, true, null, null) {

        @Override
        protected void acceptRaw(RawBytecodeVisitor visitor, int argument) {
            visitor.visitAbsouteJump(argument);
        }
    },
    /**
     * Loads the global name co_names[index] to the stack, where index is the argument of the
     * opcode.
     * 
     * Stack: - -> globals()[co_names[index]]
     */
    LOAD_GLOBAL(116, true, null, null) {

        @Override
        protected void acceptRaw(RawBytecodeVisitor visitor, int argument) {
            visitor.visitLoad(VariableContext.GLOBAL, argument);
        }
    },
    /**
     * Jumps to the next iteration of the current loop.
     * 
     * Stack: - -> -
     */
    CONTINUE_LOOP(119, true, null, null) {

        @Override
        protected void acceptRaw(RawBytecodeVisitor visitor, int argument) {
            visitor.visitContinue(argument);
        }
    },
    /**
     * Setup a new loop block on the block stack.
     * 
     * Stack: - -> -
     */
    SETUP_LOOP(120, true, null, null) {

        @Override
        protected void acceptRaw(RawBytecodeVisitor visitor, int argument) {
            visitor.visitSetupLoop(argument);
        }
    },
    /**
     * Setup a new exception handling block on the block stack.
     * 
     * Stack: - -> -
     */
    SETUP_EXCEPT(121, true, null, null) {

        @Override
        protected void acceptRaw(RawBytecodeVisitor visitor, int argument) {
            visitor.visitSetupExcept(argument);
        }
    },
    /**
     * Setup a new finally block on the block stack.
     * 
     * Stack: - -> -
     */
    SETUP_FINALLY(122, true, null, null) {

        @Override
        protected void acceptRaw(RawBytecodeVisitor visitor, int argument) {
            visitor.visitSetupFinally(argument);
        }
    },
    /**
     * Load the local variable at the index given by the opcode argument to the stack.
     * 
     * Stack: - -> element
     */
    LOAD_FAST(124, true, null, null) {

        @Override
        protected void acceptRaw(RawBytecodeVisitor visitor, int argument) {
            visitor.visitLoad(VariableContext.LOCAL, argument);
        }
    },
    /**
     * Store the element on the top of the stack to the local variable at the index given by the
     * opcode argument.
     * 
     * Stack: element -> -
     * 
     * Side effect: Store local variable
     */
    STORE_FAST(125, true, null, null) {

        @Override
        protected void acceptRaw(RawBytecodeVisitor visitor, int argument) {
            visitor.visitStore(VariableContext.LOCAL, argument);
        }
    },
    /**
     * Delete the local variable at the index given by the opcode argument.
     * 
     * Stack: - -> -
     * 
     * Side effect: Delete local variable
     */
    DELETE_FAST(126, true, null, null) {

        @Override
        protected void acceptRaw(RawBytecodeVisitor visitor, int argument) {
            visitor.visitDelete(VariableContext.LOCAL, argument);
        }
    },
    /**
     * Raise the X elements from the stack, where X is the opcode argument.
     * 
     * Stack: [exception, [value, [trace]]] -> -
     * 
     * Side effect: raise [exception, [value, [trace]]]
     * 
     * If X is zero, the exception will be retrieved from sys.exc_info()
     */
    RAISE_VARARGS(130, true, null, null) {

        @Override
        protected void acceptRaw(RawBytecodeVisitor visitor, final int argument) {
            visitor.visitInstruction(new Instruction() {

                public void accept(BytecodeVisitor visitor) {
                    visitor.visitRaise(argument);
                }
            });
        }
    },
    /**
     * Calls the function on the stack with the arguments on the stack above the function. The
     * function is called with numKeys keyword arguments, and numArgs positional arguments, where
     * numKeys and numArgs are specified by the opcode arguments.
     * 
     * Stack: [key,arg]*numKeys, [arg]*numArgs, function -> return-value
     */
    CALL_FUNCTION(131, true, null, null) {

        @Override
        protected void acceptRaw(RawBytecodeVisitor visitor, int argument) {
            final int numKey = argument >> 8;
            final int numPos = argument % (1 << 8);
            visitor.visitInstruction(new Instruction() {

                public void accept(BytecodeVisitor visitor) {
                    visitor.visitCall(false, false, numPos, numKey);
                }
            });
        }
    },
    /**
     * Makes a function from a code object found on the top of the stack and count default variables
     * found on the stack below the code object, where count is the argument of the opcode.
     * 
     * Stack: code, [default_arg]*count -> function
     */
    MAKE_FUNCTION(132, true, null, null) {

        @Override
        protected void acceptRaw(RawBytecodeVisitor visitor, final int argument) {
            visitor.visitInstruction(new Instruction() {

                public void accept(BytecodeVisitor visitor) {
                    visitor.visitBuildFunction(false, argument);
                }
            });
        }
    },
    /**
     * Create a slice object from count items from the top of the stack.
     * 
     * Stack: element1, element2[, element3] -> slice([element3,] element2, element1)
     */
    BUILD_SLICE(133, true, null, null) {

        @Override
        protected void acceptRaw(RawBytecodeVisitor visitor, final int argument) {
            visitor.visitInstruction(new Instruction() {

                public void accept(BytecodeVisitor visitor) {
                    visitor.visitBuildSlice(argument);
                }
            });
        }
    },
    /**
     * Makes a function from a code object found on the top of the stack, free_count closure
     * variables and count default variables found on the stack below the code object, where count
     * is the argument of the opcode. free_count is the number of free variables specified by the
     * code object.
     * 
     * Stack: code, tuple([cell_vars]*free_count), [default_arg]*count -> function
     */
    MAKE_CLOSURE(134, true, null, null) {

        @Override
        protected void acceptRaw(RawBytecodeVisitor visitor, final int argument) {
            visitor.visitInstruction(new Instruction() {

                public void accept(BytecodeVisitor visitor) {
                    visitor.visitBuildFunction(true, argument);
                }
            });
        }
    },
    /**
     * Load the closure variable from the current scope with the index specified by the opcode
     * argument.
     * 
     * Stack: - -> closure_variable[index]
     */
    LOAD_CLOSURE(135, true, null, null) {

        @Override
        protected void acceptRaw(RawBytecodeVisitor visitor, final int argument) {
            visitor.visitLoadClosure(argument);
        }
    },
    /**
     * Load the value of the closure variable with index specified by the opcode argument.
     * 
     * Stack: - -> get_value(closure_variable[index])
     */
    LOAD_DEREF(136, true, null, null) {

        @Override
        protected void acceptRaw(RawBytecodeVisitor visitor, int argument) {
            visitor.visitLoad(VariableContext.CLOSURE, argument);
        }
    },
    /**
     * Store the value from the top of the stack to the closure variable with the index specified by
     * the opcode argument.
     * 
     * Stack: element -> -
     * 
     * Side effect: set_value(closure_variable[index], element)
     */
    STORE_DEREF(137, true, null, null) {

        @Override
        protected void acceptRaw(RawBytecodeVisitor visitor, int argument) {
            visitor.visitStore(VariableContext.CLOSURE, argument);
        }
    },
    /**
     * Calls the function on the stack with the arguments on the stack above the function. The
     * function is called with numKeys keyword arguments, and numArgs positional arguments, where
     * numKeys and numArgs are specified by the opcode arguments.
     * 
     * Stack: varargs, [key,arg]*numKeys, [arg]*numArgs, function -> return-value
     */
    CALL_FUNCTION_VAR(140, true, null, null) {

        @Override
        protected void acceptRaw(RawBytecodeVisitor visitor, int argument) {
            final int numKey = argument >> 8;
            final int numPos = argument % (1 << 8);
            visitor.visitInstruction(new Instruction() {

                public void accept(BytecodeVisitor visitor) {
                    visitor.visitCall(true, false, numPos, numKey);
                }
            });
        }
    },
    /**
     * Calls the function on the stack with the arguments on the stack above the function. The
     * function is called with numKeys keyword arguments, and numArgs positional arguments, where
     * numKeys and numArgs are specified by the opcode arguments.
     * 
     * Stack: keywordargs, [key,arg]*numKeys, [arg]*numArgs, function -> return-value
     */
    CALL_FUNCTION_KW(141, true, null, null) {

        @Override
        protected void acceptRaw(RawBytecodeVisitor visitor, int argument) {
            final int numKey = argument >> 8;
            final int numPos = argument % (1 << 8);
            visitor.visitInstruction(new Instruction() {

                public void accept(BytecodeVisitor visitor) {
                    visitor.visitCall(false, true, numPos, numKey);
                }
            });
        }
    },
    /**
     * Calls the function on the stack with the arguments on the stack above the function. The
     * function is called with numKeys keyword arguments, and numArgs positional arguments, where
     * numKeys and numArgs are specified by the opcode arguments.
     * 
     * Stack: keyword[key,arg]*numKeys, [arg]*numArgs, function -> return-value
     */
    CALL_FUNCTION_VAR_KW(142, true, null, null) {

        @Override
        protected void acceptRaw(RawBytecodeVisitor visitor, int argument) {
            final int numKey = argument >> 8;
            final int numPos = argument % (1 << 8);
            visitor.visitInstruction(new Instruction() {

                public void accept(BytecodeVisitor visitor) {
                    visitor.visitCall(true, true, numPos, numKey);
                }
            });
        }
    },
    /**
     * Extends the size of the argument of the next opcode. The argument of this opcode is taken as
     * the most significant bytes.
     */
    EXTENDED_ARG(143, true, null, null) {

        @Override
        void read(BytecodeVersion version, RawBytecodeVisitor visitor, CharReader reader) {
            int hi = readArg(reader);
            BytecodeInstruction instruction = version.get(reader.read());
            if (instruction == EXTENDED_ARG) {
                throw new RuntimeException("Argument extension overflow!");
            }
            if (!instruction.haveArgument) {
                throw new RuntimeException("Cannot extend argument of instruction without argument!");
            }
            int lo = readArg(reader);
            instruction.acceptRaw(visitor, (hi << 16) + lo);
        }
    };

    public final boolean haveArgument;

    public final int opcode;

    public final BytecodeVersion introduced;

    public final BytecodeVersion removed;

    private BytecodeInstruction(int opcode,
                                boolean haveArgument,
                                BytecodeVersion introduced,
                                BytecodeVersion removed) {
        this.opcode = opcode;
        this.haveArgument = haveArgument;
        this.introduced = introduced;
        this.removed = removed;
        BytecodeVersion[] versions = BytecodeVersion.values();
        int first = (introduced != null) ? introduced.ordinal() : 0;
        int last = (removed != null) ? removed.ordinal() : versions.length;
        for (int i = first; i < last; i++) {
            versions[i].set((char)opcode, this);
        }
    }

    void read(BytecodeVersion version, RawBytecodeVisitor visitor, CharReader reader) {
        if (haveArgument) {
            this.acceptRaw(visitor, readArg(reader));
        } else {
            this.acceptRaw(visitor);
        }
    }

    void acceptRaw(RawBytecodeVisitor visitor, int argument) {
        throw new RuntimeException("Unimplemented OpCode: " + this + " (arg=" + argument + ")");
    }

    public void acceptRaw(RawBytecodeVisitor visitor) {
        visitor.visitInstruction(this);
    }

    public void accept(BytecodeVisitor visitor) {
        throw new RuntimeException("Unimplemented OpCode: " + this + " (without argument)");
    }

    /**
     * Read an argument from the specified reader.
     * 
     * @param reader
     *            The reader to read the argument from.
     * @return The argument read from the reader.
     */
    protected final int readArg(CharReader reader) {
        int lo = reader.read();
        int hi = reader.read();
        return lo + (hi << 8);
    }
}
