package tests;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import computer.BitString;
import computer.Computer;

public class ComputerTests {

    private final static int MAX_MEMORY = 500;
    private final static int MAX_BITS = 32;
    private final static int MAX_INSTR_MEMORY = 200;
    private final static int MAX_REGISTERS = 32;
    private final static int MAX_VALUE = 2_147_483_647; // 2^31 - 1
    private Computer computerTest;

    @Before
    public void setUp() throws Exception {
        computerTest = new Computer();
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
            assertEquals(0, computerTest.getInstrMemoryAddress(i).getValue());
        }
        for (int i = 0; i < MAX_MEMORY; i++) {
            assertEquals(0, computerTest.getMemoryAddress(i).getValue());
        }
    }

    /**
     * Checks to see if illegal words can be loaded into data memory.
     */
    @Test
    public void testIllegalLoadWord() {
        try {
            BitString test = new BitString();
            test.setValue(32);
            computerTest.loadWord(-5, test);
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
        computerTest.loadWord(5, test);
        assertEquals(test, computerTest.getMemoryAddress(5));
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
     * Check if we can set an illegal memory address.
     */
    @Test
    public void testIllegalSetMemoryAddress() {
        try {
            computerTest.setMemoryAddress(MAX_MEMORY, 5);
            fail("testIllegalSetMemoryAddress failed, illegal address allowed.");
        } catch (IllegalArgumentException ie) {
        }
    }

    /**
     * Check if we can set a memory address with a value.
     */
    @Test
    public void testSetMemoryAddress() {
        BitString test = new BitString();
        test.setValue(800);
        computerTest.setMemoryAddress(78, 800);
        assertArrayEquals(computerTest.getMemoryAddress(78).getBits(), test.getBits());
    }

    /**
     * Check setting an illegal instruction memory address.
     */
    @Test
    public void testIllegalSetInstrMemoryAddress() {
        try {
            computerTest.setInstrMemoryAddress(MAX_INSTR_MEMORY, 5);
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
        computerTest.setInstrMemoryAddress(78, 2000);
        assertArrayEquals(computerTest.getInstrMemoryAddress(78).getBits(), test.getBits());
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
        computerTest.loadWord(0, addInstr);
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
        computerTest.loadWord(0, addInstr);
        try {
        computerTest.execute();
        fail("Register add does not correctly measure overflow.");
        } catch(IllegalArgumentException ie) {
        }
    }  
    
    
    
}
