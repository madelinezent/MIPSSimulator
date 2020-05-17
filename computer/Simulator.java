package computer;


public class Simulator {

	public static void main(String[] args) {

		Computer comp;

		/************************************** */
		/** The next two variables - program and programSize - */
		/** allow someone using the simulator (such as a grader) */
		/** to decide what program will be simulated. */
		/** The simulation must load and execute */
		/** instructions found in the "program" array. */
		/** For grading purposes, it must be possible for me to */
		/**
		 * - paste in a different set of binary strings to replace the existing
		 * ones
		 */
		/** - recompile your program without further changes */
		/** and see the simulator load and execute the new program. */
		/** Your grade will depend largely on how well that works. */
		/************************************** */

		comp = new Computer();

		// Add instructions
		BitString addiInstr = new BitString();
		addiInstr.setBits("1001100101111111".toCharArray());
		comp.loadWord(0, addiInstr);

		/* execute the program */
		/* During execution, the only output to the screen should be */
		/* the result of executing OUT. */
		comp.execute();

		/* shows final configuration of computer */
		comp.display();
	}
}
