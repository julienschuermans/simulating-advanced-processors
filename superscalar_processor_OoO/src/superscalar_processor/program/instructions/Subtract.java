package superscalar_processor.program.instructions;

import superscalar_processor.signals.OpCode;

public class Subtract extends RegisterInstruction {

	public Subtract(int address, String source1, String source2, String destination) {
		super(address, OpCode.SUB, source1, source2, destination);
	}

	@Override
	public String toString() {
		return "sub " + this.getRD() + ", " + this.getRS() + ", " + this.getRT();
	}

}
