package superscalar_processor.program.instructions;

import superscalar_processor.program.Instruction;
import superscalar_processor.signals.OpCode;

public abstract class ImmediateInstruction extends Instruction{

	public ImmediateInstruction(int address, OpCode opcode, String rs, String rt, int immediate) {
		super(address, opcode);
		this.rs = rs;
		this.rt = rt;
		this.immediate = immediate;
	}
	
	private final String rs;
	private final String rt;
	private final int immediate;
	
	public String getRS() {
		return this.rs;
	}

	public String getRT() {
		return this.rt;
	}

	public int getImmediate() {
		return this.immediate;
	}
	
}
