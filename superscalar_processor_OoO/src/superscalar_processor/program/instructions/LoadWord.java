package superscalar_processor.program.instructions;

import superscalar_processor.signals.OpCode;

public class LoadWord extends ImmediateInstruction {

	
	public LoadWord(int address, String ramSource, String regDest, int offset) {
		super(address, OpCode.LW, ramSource, regDest, offset);
	}

	@Override
	public String toString() {
		return "lw " + this.getRT() + ", " + String.valueOf(this.getImmediate()) + "(" + this.getRS() + ")";
	}

}
