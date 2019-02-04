package superscalar_processor.program.instructions;

import superscalar_processor.signals.OpCode;

public class Jump extends JumpInstruction {

	public Jump(int address, int target) {
		super(address, OpCode.J, target);
	}
	
	@Override
	public String toString() {
		return "jump " + String.valueOf(this.getTargetAddress());
	}
}
