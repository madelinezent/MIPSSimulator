package computer;


/**
 * Computer class comprises of memory, registers, cc and
 * can execute the instructions based on PC and IR 
 * @author mmuppa
 *
 */
public class Computer {

  private final static int MAX_MEMORY = 500;
  private final static int MAX_INSTR_MEMORY = 200;
  private final static int MAX_REGISTERS = 32;
  private final static int MAX_BITS = 32;
  private final static int MAX_VALUE = 2_147_483_647; // 2^31 - 1
  private final static int MIN_VALUE = -2_147_483_648; // -2^31

    private BitString mRegisters[];
    private BitString mMemory[];
    private BitString mInstrMemory[];
    private BitString mPC;
    private BitString mIR;

    /**
     * Initializes all the memory to 0, registers to 0 to 31,
     * PC, and IR to 32 bit 0s.
     * Represents the initial state 
     */
    public Computer() {
        mPC = new BitString();
        mPC.setValue(0);
        mIR = new BitString();
        mIR.setValue(0);
        mRegisters = new BitString[MAX_REGISTERS];
        for (int i = 0; i < MAX_REGISTERS; i++) {
            mRegisters[i] = new BitString();
            mRegisters[i].setValue(0);
        }
        mMemory = new BitString[MAX_MEMORY];
        for (int i = 0; i < MAX_MEMORY; i++) {
            mMemory[i] = new BitString();
            mMemory[i].setValue(0);
        }
        mInstrMemory = new BitString[MAX_INSTR_MEMORY];
        for (int i = 0; i < MAX_INSTR_MEMORY; i++) {
            mInstrMemory[i] = new BitString();
            mInstrMemory[i].setValue(0);
        }
    }

    /**
     * This method will execute all the instructions starting at address 0 
     * till HALT instruction is encountered. 
     */
    public void execute() {
        int opCode;
        while (true) {
            // Fetch the instruction
            mIR = mInstrMemory[mPC.getValue() / 4];
            int newPC = mPC.getValue() + 4;
            mPC.setValue2sComp(newPC);
            // Use the opcode to determine R, I, or J type
            opCode = mIR.getOpcode();
            // What instruction is this?
            if (opCode == 0) { // R-type
                if (identifyRFormatInstr() == 1) { break; }
            } else if (opCode == 2) {
                //execute jump
            } else if (opCode == 4) {
                //execute jal
            } else if (opCode == 4) {
                //execute branch on equal
            } else if (opCode == 8) {
                //execute add immediate
                executeImmAdd();
            } else if (opCode == 12) {
                //execute and immediate
                executeImmAnd();
            } else if (opCode == 35) {
                //execute load word
                executeImmLoadWord();
            } else if (opCode == 43) {
                //execute store word
                executeImmStoreWord();
            }
        }
    }
    
    /**
     * This method is used to determine the specific type of R-format
     * an instruction a BitString is.
     */
    public int identifyRFormatInstr() {
        int funct = mIR.getFunct();
        if (funct == 8) {
            executeJJumpRegister();
        } else if (funct == 12) {  // Syscall, since we didn't need to implement it for the
            return 1;       // assignment requirements, for our purposes it will assume
                            // v0 is 10 and that we are halting
        } else if (funct == 36) {
            executeRegAnd();
        } else if (funct == 32) {
            executeRegAdd();
        }
        return 0;
    }
    
    /**
     * Does a register add of the values in Rs and Rt into Rd.
     */
    public void executeRegAdd() {
        int rs = getRegister(mIR.getRs()).getValue2sComp();
        int rt = getRegister(mIR.getRt()).getValue2sComp();
        int rd = mIR.getRd();
        int sum = rs + rt;
        if ((rs > 0 && rt > 0 && sum < 0) || (rs < 0 && rt < 0 && sum > 0)) {
            throw new IllegalArgumentException("Arithmetic Overflow from register add.");
        }
        setRegister(rd, sum);
    }
    
    /**
     * Does a bitwise and of values in Rs and Rt and puts it in Rd.
     */
    public void executeRegAnd() {
        int rs = mIR.getRs();
        int rt = mIR.getRt();
        int rd = mIR.getRd();
        int and = getRegister(rs).getValue2sComp() & getRegister(rt).getValue2sComp();
        setRegister(rd, and);
    }
    
    /**
     * Does an add immediate operation storing R[rt] with R[rs] + SignExtImm
     */
    public void executeImmAdd() {
        int rs = mIR.getRs();
        int rt = mIR.getRt();
        int imm = mIR.getImm();
        int valueRs = mRegisters[rs].getValue2sComp();
        int sum = valueRs + imm;
        if ((valueRs > 0 && imm > 0 && sum < 0) || (valueRs < 0 && imm < 0 && sum > 0)) {
            throw new IllegalArgumentException("Arithmetic Overflow from immediate add.");
        }
        setRegister(rt, sum);
    }
    
    /**
     * Does an add immediate operation storing R[rt] = R[rs] and SignExtImm
     */
     public void executeImmAnd() {
         int rs = mIR.getRs();
         int rt = mIR.getRt();
         int imm = mIR.getImm();
         int valueRs = mRegisters[rs].getValue2sComp();
         int and = valueRs & imm;
         setRegister(rt, and);
     }

    /**
     * Does an immediate load word operation storing R[rt] with
     * M[R[rs] + SignExtImm]
     */
    public void executeImmLoadWord() {
        int rt = mIR.getRt();
        int rs = mIR.getRs();
        int valueRs = mRegisters[rs].getValue2sComp();
        int imm = mIR.getImm();
        // Calculate target address
        int offset = valueRs + imm;
        // Check if R[rs] + SignExtImm creates arithmetic overflow
        if ((valueRs > 0 && imm > 0 && offset < 0)
                || (valueRs < 0 && imm < 0 && offset > 0)) {
            throw new IllegalArgumentException("Arithmetic Overflow from offset add.");
        }

        // Check if R[rs] + SignExtImm is out of bounds of mMemory
        if (offset > MAX_MEMORY || offset < 0) {
            throw new ArrayIndexOutOfBoundsException("Offset to large/small");
        }
        BitString dataMemory = getDataMemoryAddress(offset);
        // Load BitString into register
        setRegister(rt, dataMemory);
    }
    
    /** 
     * Set the value at a data memory address to the word at Rt.
     * R[rt] = M[R[rs] + SignExtImm]
     */
    public void executeImmStoreWord() {
        int rs = getRegister(mIR.getRs()).getValue2sComp();
        int imm = mIR.getImm();
        int memoryAddress = rs + imm;
        // Check if R[rs] + SignExtImm creates arithmetic overflow
        if ((rs > 0 && imm > 0 && memoryAddress < 0)
                || (rs < 0 && imm < 0 && memoryAddress > 0)) {
            throw new IllegalArgumentException("Arithmetic Overflow from offset add.");
        }
        if (memoryAddress < 0 || memoryAddress >= MAX_MEMORY) {
            throw new IllegalArgumentException("Invalid Parameters");
        }
        mMemory[memoryAddress] = getRegister(mIR.getRt());
    }

    /**
     * Does an immediate branch on equal operation storing PC with PC + 4 +
     * BranchAddr if R[rs] == R[rt]
     */
    public void executeImmBEQ() {
        int getRsValue = getRegister(mIR.getRs()).getValue2sComp();
        int getRtValue = getRegister(mIR.getRt()).getValue2sComp();
        if (getRsValue == getRtValue) {
            int newPC = mPC.getValue() + 4 + (mIR.getImm() * 4);
            if (newPC > MAX_INSTR_MEMORY || newPC < 0) {
                throw new ArrayIndexOutOfBoundsException("PC is too large/small");
            }
            mPC.setValue(newPC);
        }
    }

    /**
     * Does an jump operation storing PC with JumpAddr
     */
    public void executeJJump() {
        BitString concatenation = mPC.substring(0, 4);

        BitString address = mIR.substring(6, 26);
        /* Append address onto top 4 bits */
        concatenation.append(address);

        /* Get two zeros to append on the end */
        BitString zeros = new BitString();
        zeros.setBits("00".toCharArray());
        concatenation.append(zeros);

        if (concatenation.getValue2sComp() < 0
                || concatenation.getValue2sComp() > MAX_INSTR_MEMORY) {
            throw new ArrayIndexOutOfBoundsException("Offset to large/small");
        }
        mPC = concatenation;
    }
    
    /**
     * Does a jump operation to a register.
     */
    public void executeJJumpRegister() {
       int rs = mIR.getRs();
       int rsValue = getRegister(rs).getValue2sComp();
       if (rsValue / 4 > MAX_INSTR_MEMORY || rsValue < 0) {
           throw new ArrayIndexOutOfBoundsException("Invalid JR input");
       }
       mPC.setValue2sComp(rsValue);
    }
    
    /** 
     * Returns the PC value. 
     */
    public BitString getMyPC() {
        return mPC;
    }

    /** 
     * Returns 32 bits within an inputted register
     * @param register
     * @return the 32 character BitString in the register
     */
    public BitString getRegister(int register) {
        if (register < 0 || register >= MAX_REGISTERS) {
            throw new IllegalArgumentException();
        }
        return mRegisters[register];
    }

    /**
     * Sets the value within a register.
     * @param register to change value
     * @param value to change register to
     */
    public void setRegister(int register, int value) {
        if (register < 0 || register >= MAX_REGISTERS || value < MIN_VALUE || value > MAX_VALUE) {
            throw new IllegalArgumentException("Invalid Parameters");
        }
        if (register == 0) {
            throw new IllegalArgumentException("Cannot change the value in register 0!");
        }
        BitString registerValue = new BitString();
        registerValue.setValue2sComp(value);
        mRegisters[register] = registerValue;
    }

    /**
     * Sets the value within a register directly to a BitString.
     * @param register to change value
     * @param value to change register to a given BitString
     */
    public void setRegister(int register, BitString value) {
        if (register < 0 || register >= MAX_REGISTERS || value.getValue() < MIN_VALUE
                || value.getValue() > MAX_VALUE) {
            throw new IllegalArgumentException("Invalid Parameters");
        }
        if (register == 0) {
            throw new IllegalArgumentException("Cannot change the value in register 0!");
        }
        mRegisters[register] = value;
    }

    /**
     * Gets the instruction memory at a certain address.
     * @param memoryAddress to get value from
     * @return the BitString at the memory address
     */
    public BitString getInstr(int memoryAddress) {
        if (memoryAddress < 0 || memoryAddress >= MAX_INSTR_MEMORY) {
            throw new IllegalArgumentException();
        }
        return mInstrMemory[memoryAddress];
    }

    /**
     * Sets an instruction memory address to a word.
     * @param memoryAddress is the address to change
     * @param word is the new number to store at that address
     */
    public void loadInstr(int memoryAddress, BitString word) {
        if (memoryAddress < 0 || memoryAddress >= MAX_INSTR_MEMORY || 
                word.getLength() != MAX_BITS) {
            throw new IllegalArgumentException("Invalid Parameters");
        }
        mInstrMemory[memoryAddress] = word;
    }

    /**
     * Returns the BitString located at a calculated target address in the
     * Data Memory unit
     * @param offset the calculated target address
     * @return BitString located at calculated target address
     */
    public BitString getDataMemoryAddress(int offset) {
        if (offset < 0 || offset > MAX_MEMORY) {
            throw new IllegalArgumentException("Offset too large/small");
        }
        return mMemory[offset];
    }

    public void setDataMemoryAdress(int offset, int value) {
        if (offset < 0 || offset > MAX_MEMORY) {
            throw new IllegalArgumentException("Offset too large/small");
        }
        BitString storeValue = new BitString();
        storeValue.setValue(value);
        mMemory[offset] = storeValue;
    }
    
    /**
     * Displays the computer's state on the console.
     */
    public void display() {
        System.out.print("\nPC ");
        mPC.display(true);
        System.out.print("   ");

        System.out.print("IR ");
        mPC.display(true);
        System.out.print("   ");
        System.out.println();

        for (int i = 0; i < MAX_REGISTERS; i++) {
            System.out.printf("R%d ", i);
            mRegisters[i].display(true);
            if (i % 3 == 2) {
                System.out.println();
            } else {
                System.out.print("   ");
            }
        }
        System.out.println();

        for (int i = 0; i < MAX_INSTR_MEMORY; i++) {
            System.out.printf("IM%3d ", i);
            mInstrMemory[i].display(true);
            if (i % 3 == 2) {
                System.out.println();
            } else {
                System.out.print("   ");
            }
        }
        System.out.println();

        for (int i = 0; i < MAX_MEMORY; i++) {
            System.out.printf("DM%3d ", i);
            mMemory[i].display(true);
            if (i % 3 == 2) {
                System.out.println();
            } else {
                System.out.print("   ");
            }
        }
        System.out.println();
    }
}