package superscalar_processor.program.instructions;

import superscalar_processor.program.Instruction;
import superscalar_processor.signals.OpCode;

public class Stop extends Instruction{

	public Stop(int address) {
		super(address, OpCode.STOP);
	}

	@Override
	public String toString() {
		return "stop";
	}

}
