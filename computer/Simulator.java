package computer;


public class Simulator {

	public static void main(String[] args) {
		Computer comp;

		comp = new Computer();

		// Add instructions
		BitString addiInstr = new BitString();
		// R[2] -> R[1] + 1
		addiInstr.setBits("00100000001000100000000000000001".toCharArray());
		comp.loadInstr(0, addiInstr);

		comp.execute();

		comp.display();
	}
}
