package superscalar_processor.model;

import superscalar_processor.signals.OpCode;

public class BranchUnit extends ExecutionUnit{
	
	public BranchUnit() {
		this.multiplier = new Multiplier();
		this.ALU = new ALU();
		this.adder = new Adder();
		this.aluInputMux = new Mux();
		this.branchTargetMux = new Mux();
	}
	
	public Multiplier multiplier;
	public ALU ALU;
	public Adder adder;
	public Mux aluInputMux;
	public Mux branchTargetMux;

	
	@Override
	public void execute(OpCode opcode) {
		adder.cycle();
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
