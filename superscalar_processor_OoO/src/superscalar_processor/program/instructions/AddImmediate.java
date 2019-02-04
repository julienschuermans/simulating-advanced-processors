package superscalar_processor.program.instructions;

import superscalar_processor.signals.OpCode;

public class AddImmediate extends ImmediateInstruction {

	public AddImmediate(int address, String source, String destination, int value) {
		super(address, OpCode.ADDI, source, destination, value);
	}
	
	@Override
	public String toString() {
		return "addi " + this.getRT() + ", " + this.getRS() + ", " + String.valueOf(this.getImmediate());
	}
}
