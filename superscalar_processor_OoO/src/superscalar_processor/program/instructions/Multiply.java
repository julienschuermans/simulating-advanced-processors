package superscalar_processor.program.instructions;

import superscalar_processor.signals.OpCode;

public class Multiply extends RegisterInstruction {

	public Multiply(int address, String rs, String rt, String rd) {
		super(address, OpCode.MUL, rs, rt, rd);
	}

	@Override
	public String toString() {
		return "mul " + this.getRD() + ", " + this.getRS() + ", " + this.getRT();
	}

}
