package superscalar_processor.program.instructions;

import superscalar_processor.program.Instruction;
import superscalar_processor.signals.OpCode;

public abstract class JumpInstruction extends Instruction {

	public JumpInstruction(int address, OpCode opcode, int target) {
		super(address, opcode);
		this.target = target;
	}
	
	public int getTargetAddress() {
		return target;
	}

	private final int target;

	

}
