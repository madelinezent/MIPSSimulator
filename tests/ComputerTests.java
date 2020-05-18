package tests;

import computer.BitString;
import computer.Computer;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ComputerTests {

    private final static int MAX_MEMORY = 500;
    private final static int MAX_BITS = 32;
    private final static int MAX_INSTR_MEMORY = 200;
    private final static int MAX_REGISTERS = 32;
    private final static int MAX_VALUE = 2_147_483_647; // 2^31 - 1
    private Computer computerTest;
    private BitString halt;

    @Before
    public void setUp() throws Exception {
        computerTest = new Computer();
        halt = new BitString();
        halt.setBits("00000000000000000000000000001100".toCharArray());
    }

    /**
     * Checks if PC is set to 0, and all registers and memory locations are
     * initialized to 0.
     */
    @Test
    public void testComputerConstructor() {
        assertEquals(0, computerTest.getMyPC().getValue());
        assertEquals(MAX_BITS, computerTest.getMyPC().getLength());
        for (int i = 0; i < MAX_REGISTERS; i++) {
            assertEquals(0, computerTest.getRegister(i).getValue());
        }
        for (int i = 0; i < MAX_INSTR_MEMORY; i++) {
            assertEquals(0, computerTest.getInstr(i).getValue());
        }
        for (int i = 0; i < MAX_MEMORY; i++) {
            assertEquals(0, computerTest.getDataMemoryAddress(i).getValue());
        }
    }

    /**
     * Checks to see if illegal words can be loaded into instruction memory.
     */
    @Test
    public void testIllegalLoadWord() {
        try {
            BitString test = new BitString();
            test.setValue(32);
            computerTest.loadInstr(-5, test);
            fail("testIllegalLoadWord failed");
        } catch (IllegalArgumentException ie) {
        }
    }

    /**
     * Checks to see if a word can be correctly loaded into data memory.
     */
    @Test
    public void testLoadWord() {
        BitString test = new BitString();
        test.setValue(32);
        computerTest.loadInstr(5, test);
        assertEquals(test, computerTest.getInstr(5));
    }

    /**
     * Check if we can set registers out of bounds.
     */
    @Test
    public void testIllegalSetRegister() {
        try {
            computerTest.setRegister(32, 5);
            fail("testIllegalSetRegister failed, illegal registers allowed.");
        } catch (IllegalArgumentException ie) {
        }
    }
    
    /**
     * Check if we can illegally change $0.
     */
    @Test
    public void testIllegalSetRegister0() {
        try {
            computerTest.setRegister(0, 5);
            fail("testIllegalSetRegister0 failed, should not be allowed to change $0.");
        } catch (IllegalArgumentException ie) {
        }
    }

    /**
     * Check if we can set and get correct registers.
     */
    @Test
    public void testSetRegister() {
        BitString test = new BitString();
        test.setValue(7);
        computerTest.setRegister(4, 7);
        assertArrayEquals(computerTest.getRegister(4).getBits(), test.getBits());
    }


    /**
     * Check setting an illegal instruction memory address.
     */
    @Test
    public void testIllegalSetInstrMemoryAddress() {
        try {
            BitString test = new BitString();
            test.setValue2sComp(4);
            computerTest.loadInstr(MAX_INSTR_MEMORY, test);
            fail("testIllegalSetInstrMemoryAddress failed, illegal address allowed.");
        } catch (IllegalArgumentException ie) {
        }
    }

    /**
     * Check setting an instruction memory address with a value.
     */
    @Test
    public void testSetInstrMemoryAddress() {
        BitString test = new BitString();
        test.setValue(2000);
        computerTest.loadInstr(78, test);
        assertArrayEquals(computerTest.getInstr(78).getBits(), test.getBits());
    }   

    /**
     * If the halt doesn't work, PC will continue to increment until it
     * throws an array out of bounds exception.
     */
    @Test
    public void testHalt() {
        try {
            computerTest.loadInstr(0, halt);
            computerTest.execute();
        } catch (ArrayIndexOutOfBoundsException ex) {
            fail("Halt instruction doesn't work.");
        }
    }
    
    /**
     * Tests basic functionality of register add.
     * 1. Load instr add $9, $10, $11
     * 2. Calculate reg addition 
     * 3. Test if $10 + $11 = $9
     */
    @Test
    public void testRegAdd() {
        computerTest.setRegister(10, 5);
        computerTest.setRegister(11, 12);
        BitString addInstr = new BitString();
        addInstr.setBits("00000001010010110100100000100000".toCharArray());
        computerTest.loadInstr(0, addInstr);
        computerTest.loadInstr(1, halt);
        computerTest.execute();
        assertEquals(17, computerTest.getRegister(9).getValue2sComp());
    }  
    
    /**
     * Tests for an overflow in reg add.
     * 1. Load instr add $9, $10, $11
     * 2. Fill registers $10 and $11 with the max value
     * 3. Execute should trigger an exception
     */
    @Test
    public void testRegAddOverflow() {
        computerTest.setRegister(10, MAX_VALUE);
        computerTest.setRegister(11, MAX_VALUE);
        BitString addInstr = new BitString();
        addInstr.setBits("00000001010010110100100000100000".toCharArray());
        computerTest.loadInstr(0, addInstr);
        try {
        computerTest.execute();
        fail("Register add does not correctly measure overflow.");
        } catch(IllegalArgumentException ie) {
        }
    }  
    
    /**
     * Tests if the register bitwise works correctly.
     * 1. Load instr and $4, $5, $6
     * 2. Fill register $5 with 48 and $6 with 63
     * 3. Call function and check if Rd was filled with 48
     */
    @Test
    public void testRegAnd() {
        computerTest.setRegister(5, 48);
        computerTest.setRegister(6, 63);
        BitString andInstr = new BitString();
        andInstr.setBits("00000000101001100010000000100100".toCharArray());
        computerTest.loadInstr(0, andInstr);
        computerTest.loadInstr(1, halt);
        computerTest.execute();
        assertEquals(48, computerTest.getRegister(4).getValue2sComp());
    }  
    

    /**
     * Tests if the register bitwise works correctly for negative values.
     * 1. Load instr and $4, $5, $6
     * 2. Fill register $5 with -20 and $6 with -12
     * 3. Call function and check if Rd was filled with -28
     */
    @Test
    public void testRegAndNegative() {
        computerTest.setRegister(5, -20);
        computerTest.setRegister(6, -12);
        BitString andInstr = new BitString();
        andInstr.setBits("00000000101001100010000000100100".toCharArray());
        computerTest.loadInstr(0, andInstr);
        computerTest.loadInstr(1, halt);
        computerTest.execute();
        assertEquals(-28, computerTest.getRegister(4).getValue2sComp());
    }  
    
    /**
     * Tests if the store word instruction works for viable inputs.
     * 1. Load instr memory with sw $t1, 20($t2)
     * 2. Fill register 10 ($t2) with 50, fill register 9 ($t1) with 25
     * 3. See if M[50+20] = 25
     */
    @Test
    public void testImmStoreWord() {
        computerTest.setRegister(10, 50);
        computerTest.setRegister(9, 25);
        BitString swInstr = new BitString();
        swInstr.setBits("10101101010010010000000000010100".toCharArray());
        computerTest.loadInstr(0, swInstr);
        computerTest.loadInstr(1, halt);
        computerTest.execute();
        System.out.println(computerTest.getDataMemoryAddress(70).getValue2sComp());
        assertEquals(25, computerTest.getDataMemoryAddress(70).getValue2sComp());
    }  
    
    /**
     * Tests if the store word instruction works when the memory address
     * calculated is illegal. 
     * 1. Load instr memory with sw $t1, 20($t2)
     * 2. Fill register 10 ($t2) with -30, fill register 9 ($t1) with 25
     * 3. See if M[-30+20] triggers and exception
     */
    @Test
    public void testImmStoreWordIllegal() {
        computerTest.setRegister(10, -30);
        computerTest.setRegister(9, 25);
        BitString swInstr = new BitString();
        swInstr.setBits("10101101010010010000000000010100".toCharArray());
        computerTest.loadInstr(0, swInstr);
        computerTest.loadInstr(1, halt);
        try {
            computerTest.execute();
            fail("SW allows memory to be stored in a negative location.");
        } catch (IllegalArgumentException ie) {
        }
    }
    
    /**
     * Tests if the store word instruction can detect arithmetic overflow from an rs + immediate.
     * 
     * 1. Load instr memory with sw $t1, 20($t2)
     * 2. Fill register 10 ($t2) with MAX_VALUE, fill register 9 ($t1) with 25
     * 3. See if M[MAX_VALUE+20] triggers an exception
     */
    @Test
    public void testImmStoreWordOverflow() {
        computerTest.setRegister(10, MAX_VALUE);
        computerTest.setRegister(9, 25);
        BitString swInstr = new BitString();
        swInstr.setBits("10101101010010010000000000010100".toCharArray());
        computerTest.loadInstr(0, swInstr);
        computerTest.loadInstr(1, halt);
        try {
            computerTest.execute();
            fail("SW allows does not detect arithmetic overflow.");
        } catch (IllegalArgumentException ie) {
        }
    }
    
    /**
     * Tests if the and immediate instruction for viable input. 1. Load instr memory
     * with andi $t1, $t2, 100 
     * 2. Fill register $t2 with 89 
     * 3. See if $t1 becomes the correct andi value of 64
     */
    @Test
    public void testImmAnd() {
        computerTest.setRegister(10, 89);
        BitString andiInstr = new BitString();
        andiInstr.setBits("00110001010010010000000001100100".toCharArray());
        computerTest.loadInstr(0, andiInstr);
        computerTest.loadInstr(1, halt);
        computerTest.execute();
        assertEquals(64, computerTest.getRegister(9).getValue2sComp());
    }

    /**
     * Test add immediate instruction
     */
    @Test
    public void testImmAdd() {
        computerTest.setRegister(10, 10);
        BitString addiInstr = new BitString();
        // R[1] -> R[10] + 1 = 10 + 1
        addiInstr.setBits("00100001010000010000000000000001".toCharArray());
        computerTest.loadInstr(0, addiInstr);
        computerTest.loadInstr(1, halt);
        computerTest.execute();
        assertEquals(11, computerTest.getRegister(1).getValue2sComp());
    }

    /**
     * Test for arithmetic overflow in add immediate instruction
     */
    @Test (expected = IllegalArgumentException.class)
    public void testImmAddIAE() {
        computerTest.setRegister(10, MAX_VALUE);
        BitString addiInstr = new BitString(false, true, false);
        // R[1] -> R[10] + 1 = MAX_VALUE + 1, therefore should be overflow
        addiInstr.setBits("00100001010000010000000000000001".toCharArray());
        computerTest.loadInstr(0, addiInstr);
        computerTest.execute();
    }

    /**
     * Test load word instruction
     */
    @Test
    public void testImmLoadWord() {
        int expectedValue = 10;
        // M[40] = expectedValue
        computerTest.setDataMemoryAdress(40, expectedValue);
        BitString lwInstr = new BitString(false, true, false);
        // R[10] = M[10]
        lwInstr.setBits("10001100000010100000000000101000".toCharArray());
        computerTest.loadInstr(0, lwInstr);
        computerTest.loadInstr(1, halt);
        computerTest.execute();
        assertEquals(expectedValue, computerTest.getRegister(10).getValue2sComp());
    }

    /**
     * Test for arithmetic overflow from result of target offset calculation
     */
    @Test (expected = IllegalArgumentException.class)
    public void testImmLoadWordIAE() {
        int expectedValue = 10;
        BitString lwInstr = new BitString(false, true, false);
        computerTest.setRegister(0, MAX_VALUE);
        // R[10] = M[R[0] + Imm]
        lwInstr.setBits("10001100000010100000000000000001".toCharArray());
        computerTest.loadInstr(0, lwInstr);
        computerTest.execute();
    }

    /**
     * Test for arithmetic overflow from result of target offset calculation
     */
    @Test (expected = ArrayIndexOutOfBoundsException.class)
    public void testImmLoadWordAIOOB() {
        int expectedValue = 10;
        BitString lwInstr = new BitString(false, true, false);
        computerTest.setRegister(1, 500);
        // R[10] = M[R[1] + Imm]
        lwInstr.setBits("10001100001010100000000000000001".toCharArray());
        computerTest.loadInstr(0, lwInstr);
        computerTest.execute();
    }

    /**
     * Tests branch on equal instruction
     */
    @Test
    public void testBranchOnEqual() {
        /*
         * Test currentPC by checking whether or not it incremented by 4. Hopefully not
         */
        int currentPC = computerTest.getMyPC().getValue();
        computerTest.setRegister(9, 10);
        computerTest.setRegister(10, 10);
        BitString beqInstr = new BitString(false, true, false);
        /* Jump PC = PC + 4 + 31. Tests R[9] = R[10] */
        beqInstr.setBits("00010001001010100000000000011111".toCharArray());
        computerTest.loadInstr(0, beqInstr);
        computerTest.loadInstr(1, halt);
        computerTest.execute();
        int newPC = computerTest.getMyPC().getValue();
        assertNotEquals(currentPC, newPC);
    }

    /**
     * Test branch on equal for IllegalArgumentException for when offset calculates past capacity
     * of instruction memory
     */
    @Test (expected = ArrayIndexOutOfBoundsException.class)
    public void testBranchOnEqualAIOOB() {
        computerTest.setRegister(9, 10);
        computerTest.setRegister(10, 10);
        BitString beqInstr = new BitString(false, true, false);
        /* Jump PC = PC + 4 + 255. Tests R[9] = R[10] */
        beqInstr.setBits("00010001001010100000000011111111".toCharArray());
        computerTest.loadInstr(0, beqInstr);
        computerTest.execute();
    }
}


