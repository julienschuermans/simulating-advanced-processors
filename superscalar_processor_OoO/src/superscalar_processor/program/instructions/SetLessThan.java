package superscalar_processor.program.instructions;

import superscalar_processor.signals.OpCode;

public class SetLessThan extends RegisterInstruction {

	public SetLessThan(int address, String rs, String rt, String rd) {
		super(address, OpCode.SLT, rs, rt, rd);
	}

	@Override
	public String toString() {
		return "slt " + this.getRD() + ", " + this.getRS() + ", " + this.getRT();
	}

}
