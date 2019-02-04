package superscalar_processor.model;

import superscalar_processor.program.Instruction;
import superscalar_processor.program.instructions.BranchEqual;
import superscalar_processor.signals.MuxControlSignal;

public class FetchUnit {

	public FetchUnit() {
		pcAdder =  new Adder();
		pcMux = new Mux();
		pcMux.muxControl = MuxControlSignal.A;//do PC+4 until some branch overwrites it.
	}
	
	Adder pcAdder;
	Mux pcMux;
	public BranchPredictor branchPredictor;
	
	public Instruction fetch(int address, Memory mem) {
		return mem.instructions.get(address);
	}

	public int getLatency() {
		return 1;
	}

	public void addBranchPredictor() {
		this.branchPredictor = new BranchPredictor();
	}
	
	public boolean predecode(Instruction instruction) {
		return instruction instanceof BranchEqual;
	}

}
