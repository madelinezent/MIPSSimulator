package computer;


public class Simulator {

	public static void main(String[] args) {
		Computer comp;

		comp = new Computer();

		// Add instructions
		BitString addiInstr = new BitString();
		addiInstr.setBits("1001100101111111".toCharArray());
		comp.loadInstr(0, addiInstr);

		comp.execute();

		comp.display();
	}
}
