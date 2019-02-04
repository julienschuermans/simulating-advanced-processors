package superscalar_processor.program.instructions;

import superscalar_processor.signals.OpCode;

public class BranchEqual extends ImmediateInstruction {

	public BranchEqual(int address, String source1, String source2, int branchAddr) {
		super(address, OpCode.BEQ, source1, source2, branchAddr);
	}
	
	@Override
	public String toString() {
		return "beq " + this.getRS() + ", " + this.getRT() + ", " + String.valueOf(this.getImmediate());
	}

}
