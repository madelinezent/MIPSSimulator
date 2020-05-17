package tests;

import org.junit.Before;
import org.junit.Test;

import computer.BitString;

import static org.junit.Assert.*;

/**
 * Tests the BitString class methods.
 * 
 * @author mmupa
 * @author zentm, made minor changes to accomadate 32-bits
 */
public class BitStringTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testBitStringConstructor() {
		BitString bitString = new BitString();
		assertNotNull(bitString);
		assertEquals(bitString.getLength(), 0);
		assertArrayEquals(bitString.getBits(), null);
	}

	//Changed test to go over 32 bits
	@Test
	public void testSetBitsOverLength() {
		BitString bitString = new BitString();
		try {
			bitString.setBits(new char[33]);
			fail("SetBits failed");
		} catch (IllegalArgumentException ie) {

		}
	}

	@Test
	public void testSetBits() {
		BitString bitString = new BitString();
		char test[] = { '1', '0', '1', '0' };
		bitString.setBits(test);
		assertEquals(bitString.getLength(), 4);
		assertArrayEquals(bitString.getBits(), test);
	}

	@Test
	public void testInvert() {
		char allOnes[] = { '1', '1', '1', '1' };
		char allZeros[] = { '0', '0', '0', '0' };
		BitString bitString = new BitString();
		bitString.setBits(allZeros);
		bitString.invert();
		assertArrayEquals(bitString.getBits(), allOnes);
		bitString.invert();
		assertArrayEquals(bitString.getBits(), allZeros);
	}

	@Test
	public void testAddOne() {
		char allZeros[] = { '0', '0', '0', '0' };
		char one[] = { '0', '0', '0', '1' };
		char two[] = { '0', '0', '1', '0' };
		char allOnes[] = { '1', '1', '1', '1' };
		BitString bitString = new BitString();
		bitString.setBits(allZeros);
		bitString.addOne();
		assertArrayEquals(bitString.getBits(), one);
		bitString.setBits(allOnes);
		bitString.addOne();
		assertArrayEquals(bitString.getBits(), allZeros);
		bitString.setBits(one);
		bitString.addOne();
		assertArrayEquals(bitString.getBits(), two);
	}

	@Test
	public void testSetValueInvalid() {

		BitString bitString = new BitString();
		try {
			bitString.setValue(-10);
			fail("Can set negative value for unsigned");
		} catch (IllegalArgumentException e) {
		}
	}

	//changed to represent 32 bits
	@Test
	public void testSetValue() {
		char ten[] = { '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
                '0','0', '0', '0', '0','0', '0', '0', '0', '0', '0', '0', '0', 
                '0', '0', '0', '0', '1', '0', '1', '0' };

		BitString bitString = new BitString();
		bitString.setValue(10);
		assertArrayEquals(bitString.getBits(), ten);

	}

	//Modified test to work for 32 bits. 
	@Test
	public void testSetValue2sComp() {
        char max[] = { '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
                '0', '0', '0', '0', '0', '0', '1', '1', '1', '1', '1', '1', 
                '1','1', '1', '1','1', '1', '1', '1', '1' };
        char min[] = { '1','1', '1', '1', '1', '1', '1','1', '1', '1', '1',
                '1','1', '1', '1', '1','1', '0', '0', '0', '0', '0', '0', 
                '0', '0', '0', '0','0', '0', '0', '0', '0' };	    
//		char max[] = { '0', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1',
//				'1', '1', '1', '1', '1' };
//		char min[] = { '1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
//				'0', '0', '0', '0', '0' };
		BitString bitString = new BitString();
		bitString.setValue2sComp(32767);
		assertArrayEquals(bitString.getBits(), max);
		bitString.setValue2sComp(-32768);
		assertArrayEquals(bitString.getBits(), min);
	}

	@Test
	public void testGetValue() {
		char ten[] = { '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
				'0', '1', '0', '1', '0' };
		BitString bitString = new BitString();
		bitString.setBits(ten);
		assertEquals(bitString.getValue(), 10);

	}

	@Test
	public void testGetValue2sComp() {
		char ones[] = { '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1',
				'1', '1', '1', '1', '1' };
		char min[] = { '1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
				'0', '0', '0', '0', '0' };
		BitString bitString = new BitString();
		bitString.setBits(ones);
		assertEquals(bitString.getValue2sComp(), -1);
		bitString.setBits(min);
		assertEquals(bitString.getValue2sComp(), -32768);
	}

	@Test
	public void testAppend() {
		char fourBits[] = { '0', '0', '0', '0' };
		char eightBits[] = { '1', '0', '0', '0', '0', '0', '0', '0' };
		char twelveBits[] = { '0', '0', '0', '0', '1', '0', '0', '0', '0', '0',
				'0', '0' };
		BitString bitString = new BitString();
		bitString.setBits(fourBits);
		bitString.display(true);
		BitString anotherBitString = new BitString();
		anotherBitString.setBits(eightBits);
		BitString appendedString = bitString.append(anotherBitString);
		appendedString.display(true);
		assertArrayEquals(appendedString.getBits(), twelveBits);
	}

	@Test
	public void testSubstring() {
		char twelveBits[] = { '0', '0', '0', '0', '1', '0', '0', '0', '0', '0',
				'0', '0' };
		char eightBits[] = { '1', '0', '0', '0', '0', '0', '0', '0' };
		BitString bitString = new BitString();
		bitString.setBits(twelveBits);
		BitString partString = bitString.substring(4, 8);
		assertArrayEquals(partString.getBits(), eightBits);
	}
	
	/** Creates a new BitString and checks if it is a R-Format instruction. */
	@Test
	public void testRFormat() {
	    BitString bitString = new BitString(true, false, false);
	    assertEquals(bitString.isRFormat(), true);
	}
	
	/** Creates a new BitString and checks if it is an I-Format instruction. */
    @Test
    public void testIFormat() {
        BitString bitString = new BitString(false, true, false);
        assertEquals(bitString.isIFormat(), true);
    }
	
    /** Creates a new BitString and checks if is is a J-Format instruction. */
    @Test
    public void testJFormat() {
        BitString bitString = new BitString(false, false, true);
        assertEquals(bitString.isJFormat(), true);
    }
	
    /** Check if instruction is set to more than 1 type by setting the 
     * Bitstring to be both an R and I type instruction. */
    @Test
    public void testIllegalInstrType() {
        try {
            @SuppressWarnings("unused")
            BitString bitString = new BitString(true, true, false);
            fail("Test Failed: Instruction must only be 1 type.");
        } catch (IllegalArgumentException ie) {
        }
    }
    
    /** Test if the correct opcode is returned. 
     *  Set bitstring opcode to 6 and see if it returns 6.
     */
    @Test
    public void testOpcode() {
            BitString bitString = new BitString();
            char[] bits = {'0', '0', '0', '1', '1', '0', '0', '0', '1', '0', '1', 
                    '0', '0', '1', '1', '0', '0', '0', '1', '0', '0', '0','0', '0', 
                    '0', '0', '1', '0', '0', '1', '0', '0'};
            bitString.setBits(bits);
            assertEquals(6, bitString.getOpcode());
    }
    
    /** Test if the correct Rs is returned. 
     *  Set Rs to 5 and see if it returns the correct value
     */
    @Test
    public void testRs() {
            BitString bitString = new BitString();
            char[] bits = {'0', '0', '0', '1', '1', '0', '0', '0', '1', '0', '1', 
                    '0', '0', '1', '1', '0', '0', '0', '1', '0', '0', '0','0', '0', 
                    '0', '0', '1', '0', '0', '1', '0', '0'};
            bitString.setBits(bits);
            assertEquals(5, bitString.getRs());
    }
    
    /** Test if the correct Rt is returned. 
     *  Set Rt to 5 and see if it returns the correct value
     */
    @Test
    public void testRt() {
            BitString bitString = new BitString();
            char[] bits = {'0', '0', '0', '1', '1', '0', '0', '0', '1', '0', '1', 
                    '0', '1', '0', '0', '0', '0', '0', '1', '0', '0', '0','0', '0', 
                    '0', '0', '1', '0', '0', '1', '0', '0'};
            bitString.setBits(bits);
            assertEquals(8, bitString.getRt());
    }

    /** Test if the correct Rd is returned. 
     *  Set Rd to 20 and see if it returns the correct value
     */
    @Test
    public void testRd() {
            BitString bitString = new BitString();
            char[] bits = {'0', '0', '0', '1', '1', '0', '0', '0', '1', '0', '1', 
                    '0', '1', '0', '0', '0', '1', '0', '1', '0', '0', '0','0', '0', 
                    '0', '0', '1', '0', '0', '1', '0', '0'};
            bitString.setBits(bits);
            assertEquals(20, bitString.getRd());
    }
    
    /** Test if the correct shamt is returned. 
     *  Set shamt to 16 and see if it returns the correct value
     */
    @Test
    public void testShamt() {
            BitString bitString = new BitString();
            char[] bits = {'0', '0', '0', '1', '1', '0', '0', '0', '1', '0', '1', 
                    '0', '1', '0', '0', '0', '1', '0', '1', '0', '0', '1','0', '0', 
                    '0', '0', '1', '0', '0', '1', '0', '0'};
            bitString.setBits(bits);
            assertEquals(16, bitString.getShamt());
    }
    
    /** Test if the correct funct is returned. 
     *  Set funct to 36 and see if it returns the correct value
     */
    @Test
    public void testFunct() {
            BitString bitString = new BitString();
            char[] bits = {'0', '0', '0', '1', '1', '0', '0', '0', '1', '0', '1', 
                    '0', '1', '0', '0', '0', '1', '0', '1', '0', '0', '1','0', '0', 
                    '0', '0', '1', '0', '0', '1', '0', '0'};
            bitString.setBits(bits);
            assertEquals(36, bitString.getFunct());
    }
    
    /** Test if the correct immediate value is returned. 
     *  Set imm to 7 and see if it returns the correct value
     */
    @Test
    public void testImm() {
            BitString bitString = new BitString();
            char[] bits = {'0', '0', '0', '1', '1', '0', '0', '0', '1', '0', '1', 
                    '0', '1', '0', '0', '0', '0', '0', '0', '0', '0', '0','0', '0', 
                    '0', '0', '0', '0', '0', '1', '1', '1'};
            bitString.setBits(bits);
            assertEquals(7, bitString.getImm());
    }
    
    /** Test if the correct funct is returned. 
     *  Set funct to 36 and see if it returns the correct value
     */
    @Test
    public void testGetAddr() {
            BitString bitString = new BitString();
            char[] bits = {'1', '1', '1', '1', '1', '1', '0', '0', '0', '0', '0', 
                    '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0','0', '0', 
                    '0', '1', '0', '0', '0', '0', '0', '0'};
            bitString.setBits(bits);
            assertEquals(64, bitString.getAddr());
    }
    
}
