package tests;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import computer.Computer;

public class ComputerTests {

    private final static int MAX_MEMORY = 500;
    private final static int MAX_INSTR_MEMORY = 200;
    private final static int MAX_REGISTERS = 32;
    private Computer computerTest;
    
    @Before
    public void setUp() throws Exception {
        computerTest = new Computer();
    }
    
    /** Checks if PC is set to 0, and all registers and memory locations
     * are initialized to 0.
     */
    @Test
    public void testComputerConstructor() {
        assertEquals(0, computerTest.getMyPC().getValue());
        for (int i = 0; i < MAX_REGISTERS; i++) {
            assertEquals(0, computerTest.getRegister(i).getValue());
        }
        for (int i = 0; i < MAX_INSTR_MEMORY; i++) {
            assertEquals(0, computerTest.getInstrMemoryAddress(i).getValue());
        }
        for ()
    }

}
