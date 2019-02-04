package superscalar_processor.model;

public class Adder {
	public Adder() {
		this.inputA = 0;
		this.inputB = 0;
	}

	public int inputA;
	public int inputB;
	public int output;
	

	public void cycle() {
		output = inputA + inputB;
	}
}
