package superscalar_processor.program.instructions;

import superscalar_processor.program.Instruction;
import superscalar_processor.signals.OpCode;

public abstract class RegisterInstruction extends Instruction {

	public RegisterInstruction(int address, OpCode opcode, String rs, String rt, String rd) {
		super(address, opcode);
		this.rs = rs;
		this.rt = rt;
		this.rd = rd;
	}
	
	
	public String getRD() {
		return rd;
	}

	public String getRS() {
		return rs;
	}

	public String getRT() {
		return rt;
	}

	private final String rd;
	private final String rs;
	private final String rt;

}
