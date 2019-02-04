package superscalar_processor.program.instructions;

import superscalar_processor.program.Instruction;
import superscalar_processor.signals.OpCode;

public class NOP extends Instruction {

	public NOP(int address) {
		super(address, OpCode.NOP);
	}

	@Override
	public String toString() {
		return "nop";
	}

}
