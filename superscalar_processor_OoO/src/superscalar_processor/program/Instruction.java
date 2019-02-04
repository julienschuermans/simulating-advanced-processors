package superscalar_processor.program;

import superscalar_processor.signals.OpCode;

public abstract class Instruction {

	public Instruction(int address, OpCode opcode) {
		this.address = address;
		this.opcode = opcode;
	}
	
	public int getAddress() {
		return address;
	}

	public final OpCode opcode;
	
	private final int address;
	
	public abstract String toString();

	public OpCode getOpcode() {
		return this.opcode;
	}
	
	public int programOrder;
	
	
	
}
