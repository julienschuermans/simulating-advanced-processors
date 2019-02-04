package superscalar_processor.program.instructions;

import superscalar_processor.signals.OpCode;

public class StoreWord extends ImmediateInstruction {

	public StoreWord(int address, String regSource, String ramDest, int offset) {
		super(address, OpCode.SW, ramDest, regSource, offset);
	}

	@Override
	public String toString() {
		return "sw " + this.getRT() + ", " + String.valueOf(this.getImmediate()) + "(" + this.getRS() + ")";
	}
}
