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
        BitString opCodeStr;
        int opCode;
        
        while (true) {
            // Fetch the instruction
            mIR = mMemory[mPC.getValue()];
            mPC.addOne();
            // Use the opcode to determine R, I, or J type
            opCode = mIR.getOpcode();
            // What instruction is this?
            if (opCode == 0) { // R-type
                identifyRFormatInstr();
                return;
            }
            // TODO - Others
        }
    }
    
    /**
     * This method is used to determine the specific type of R-format
     * an instruction a BitString is.
     */
    public void identifyRFormatInstr() {
        int funct = mIR.getFunct();
        if (funct == 32) {
            executeRegAdd();
        } else if (funct == 36) {
            executeRegAnd();
        }
    }
    
    /**
     * Does a register add of the values in Rs and Rt into Rd.
     */
    public void executeRegAdd() {
        int rs = mIR.getRs();
        int rt = mIR.getRt();
        int rd = mIR.getRd();
        int sum = getRegister(rs).getValue2sComp() + getRegister(rt).getValue2sComp();
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
     * Returns the PC value. 
     */
    public BitString getMyPC() {
        return mPC;
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

        for (int i = 0; i < MAX_MEMORY; i++) {
            System.out.printf("%3d ", i);
            mMemory[i].display(true);
            if (i % 3 == 2) {
                System.out.println();
            } else {
                System.out.print("   ");
            }
        }
        System.out.println();

    }
    
    /**
     * Loads a 32 bit word into memory at the given address. 
     * @param address memory address
     * @param word data or instruction or address to be loaded into memory
     */
    public void loadWord(int address, BitString word) {
        if (address < 0 || address >= MAX_MEMORY) {
            throw new IllegalArgumentException("Invalid address");
        }
        mMemory[address] = word;
    }
    
    /** 
     * Returns 32 bits within an inputted register
     * @param the register
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
     * @param int value to change register to
     */
    public void setRegister(int register, int value) {
        if (register < 0 || register >= MAX_REGISTERS || value < MIN_VALUE || value > MAX_VALUE) {
            throw new IllegalArgumentException("Invalid Parameters");
        }
        if (register == 0) {
            throw new IllegalArgumentException("Cannot change the value in regsiter 0!");
        }
        BitString registerValue = new BitString();
        registerValue.setValue2sComp(value);
        mRegisters[register] = registerValue;
    }
    
    /**
     * Gets the value at a data memory address.
     * @param memoryAddress is an int location
     * @return the BitString at the memory address
     */
    public BitString getMemoryAddress(int memoryAddress) {
        if (memoryAddress < 0 || memoryAddress >= MAX_MEMORY) {
            throw new IllegalArgumentException();
        }
        return mMemory[memoryAddress];
    }

    /** 
     * Set the value at a data memory address.
     * @param memoryAddress is an int location  
     * @param value is the int bits to place at a location
     */
    public void setMemoryAddress(int memoryAddress, int value) {
        if (memoryAddress < 0 || memoryAddress >= MAX_MEMORY 
                || value < MIN_VALUE || value > MAX_VALUE) {
            throw new IllegalArgumentException("Invalid Parameters");
        }
        BitString memoryValue = new BitString();
        memoryValue.setValue2sComp(value);
        mMemory[memoryAddress] = memoryValue;
    }
    
    /**
     * Get the instruction memory at a certain address.
     * @param memoryAddress to get value from
     * @return the BitString at the memory address
     */
    public BitString getInstrMemoryAddress(int memoryAddress) {
        if (memoryAddress < 0 || memoryAddress >= MAX_INSTR_MEMORY) {
            throw new IllegalArgumentException();
        }
        return mInstrMemory[memoryAddress];
    }

    /**
     * Sets an instruction memory address to a value.
     * @param memoryAddress is the address to change
     * @param value is the new number to store at that address
     */
    public void setInstrMemoryAddress(int memoryAddress, int value) {
        if (memoryAddress < 0 || memoryAddress >= MAX_INSTR_MEMORY || 
                value < MIN_VALUE || value > MAX_VALUE) {
            throw new IllegalArgumentException("Invalid Parameters");
        }
        BitString memoryValue = new BitString();
        memoryValue.setValue2sComp(value);
        mInstrMemory[memoryAddress] = memoryValue;
    }
    
}

