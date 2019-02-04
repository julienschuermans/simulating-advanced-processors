package superscalar_processor.model;

import superscalar_processor.signals.OpCode;

public class IntegerUnit extends ExecutionUnit {
	
	public IntegerUnit() {
		this.multiplier = new Multiplier();
		this.ALU = new ALU();
		this.aluInputMux = new Mux();
	}
	
	
	public Multiplier multiplier;
	public ALU ALU;
	public Mux aluInputMux;
	
	@Override
	public void execute(OpCode opcode) {
		multiplier.cycle();
		ALU.cycle();
	
		if (opcode == OpCode.MUL) {
			output = multiplier.output;
		}
		else {
			output = ALU.output;
		}
	}
}
