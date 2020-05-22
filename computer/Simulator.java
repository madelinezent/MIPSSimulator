package computer;

public class Simulator {

	public static void main(String[] args) {

		Computer comp;
		
		final int MAX_INSTRUCTIONS = 20; //Must be >0 and <= 200
		final int MAX_DATA_MEMORY = 50; //Must be >0 and <=500
		
		String program[] = { "00000000101001100010000000100000", "00000001001010100100000000100100",
				"00100000010001110000000000000101", "00110001100010110000000000001010", 
				"10001101111011100000000000000101", "10101110001100000000000000000011", 
				"00010010010100110000000000000001", "00000001001010100010000000100000",
				"00001000000000000000000000001010", "00000001001010100010000000100000",
				"00000011001000000000000000001000", "00000001001010100010000000100000",
				"00000000000000000000000000001100"
		        };

		/*
		 * This is the assembly program that was compiled into the binary
		 * program shown above. 
		 * 		 add     $a0, $a1, $a2       #reg: 4-6
         *       and     $t0, $t1, $t2       #reg: 8-10
         *       addi    $a3, $v0, 5         #reg: 7, 2
         *       andi    $t3, $t4, 10        #reg: 11, 12
         *       lw      $t6, 5($t7)         #reg: 14-15
         *       sw      $s0, 3($s1)         #reg: 16-17
         *       beq     $s2, $s3, testBeq   #reg: 18-19
         *       add     $a0, $t1, $t2       #Should not execute
         *                                   #reg: 4, 9, 10       
         *  testBeq:
         *       j   testJump        
         *       add     $a0, $t1, $t2       #Should not execute
         *                                   #reg: 4, 9, 10
         *  testJump:
         *       jr  $t9                     #reg: 25
         *       add $a0, $t1, $t2           #Should not execute
                    #4, 9, 10
         *  testJumpRegister:
         *       syscall                     #terminate
		 */
		comp = new Computer();
		System.out.println("COMPUTER AT START-UP: ");
		comp.display(MAX_INSTRUCTIONS, MAX_DATA_MEMORY);
		
		/* Put every instruction in the program into instruction memory. */
		for (int i = 0; i < program.length; i++) {
		    BitString instr = new BitString();
		    instr.setBits(program[i].toCharArray());
		    comp.loadInstr(i, instr);
		}
		/* Set registers before execution to test if instructions worked. */
		comp.setRegister(5, 5);
		comp.setRegister(6, 5); // result of reg 5 + 6 will go into reg 4
		comp.setRegister(9, 3);
		comp.setRegister(10, 2); // result of reg 9 ^ 10 will go into reg 8
		comp.setRegister(2, 7); // result of (reg2) 7 + 5 should be 12 in reg 7
		comp.setRegister(12, 2); // reg 12 (2) and 10 into reg 11
		comp.setRegister(15, 5); 
		comp.setDataMemoryAdress(10, 18); //reg 15 (5) + 5 = 10 
		comp.setRegister(17, 20); //20 + 3 = 23
		comp.setRegister(16, 43); //sw reg 16 into Mem[23]
		comp.setRegister(18, 7);
		comp.setRegister(19, 7); //reg 18 and 19 are equal so branch should work
		comp.setRegister(25, 48); //reg 25 should be 48 for PC to jr to halt
		
		/*
		 * Expected changes:
		 * Reg 4 should be 10 (result of reg add)
		 * Reg 8 should be 2 (result of reg and)
		 * Reg 7 should be 12 (result of addi)
		 * Reg 11 should be 2 (result of andi)
		 * Reg 14 should be 18 (result of lw)
		 * DataMemory[23] should be 43 (result of sw)
		 * Reg 4 should NOT be 5 or else BEQ, J, or JR did not work
		 */

		/* Execute the program */
		comp.execute();

		/* Show final configuration of computer. */
		System.out.println("COMPUTER AFTER INSTRUCTIONS ARE COMPLETE: ");
		comp.display(MAX_INSTRUCTIONS, MAX_DATA_MEMORY);
	}
}
