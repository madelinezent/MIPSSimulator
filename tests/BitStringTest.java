package tests;

import computer.BitString;
import org.junit.Before;
import org.junit.Test;

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
/*
 * Had to comment out this try block since we can't 
 * test the unsigned max since it would be too large
 * to be a java data type. 
 * 
		try {
			bitString.setValue(65536);
			fail("Can set more than max for unsigned");
		} catch (IllegalArgumentException e) {

		}
*/
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

/* This test is commented out since ints cannot be larger
 * than 2^32-1 or -2^32 so we cannot even test for numbers
 * out of bounds for our MIPS computer since they will 
 * out of bounds on ours.
 * 
 * @Test
	public void testSetValue2sCompInvalid() {
		BitString bitString = new BitString();
		try {
			bitString.setValue2sComp(-2_147_483_649);
			fail("Can set negative value for 2s comp");
		} catch (IllegalArgumentException e) {

		}
		try {
			bitString.setValue2sComp(2_147_483_648);
			fail("Can set more than max for 2s comp");
		} catch (IllegalArgumentException e) {

		}
	}
*/
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
}
