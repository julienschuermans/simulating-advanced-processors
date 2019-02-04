package superscalar_processor.program.instructions;

import superscalar_processor.signals.OpCode;

public class Add extends RegisterInstruction {

	public Add(int address, String destination, String source1, String source2) {
		super(address, OpCode.ADD, source1, source2, destination);
	}
	
	@Override
	public String toString() {
		return "add " + this.getRD() + ", " + this.getRS() + ", " + this.getRT();
	}
}
