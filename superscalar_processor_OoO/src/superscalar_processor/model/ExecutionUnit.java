package superscalar_processor.model;

import superscalar_processor.signals.OpCode;

public abstract class ExecutionUnit {
	int output = 0;

	public abstract void execute(OpCode opcode);
	
	public int getLatency() {
		return 1;
	}
	
	//dummy shift implementation because all immediate values are represented as integers anyway.
	public int shiftLeft2(int immediate) {
		return immediate; 
	}
}
