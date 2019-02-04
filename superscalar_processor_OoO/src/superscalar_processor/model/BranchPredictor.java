package superscalar_processor.model;

import superscalar_processor.program.Instruction;
import superscalar_processor.program.instructions.BranchEqual;
import superscalar_processor.signals.BTACEntry;

public class BranchPredictor {

	private BTAC BTAC;
	public int failures;
	public int successes;


	public BranchPredictor() {
		this.BTAC = new BTAC();
		this.successes = 0;
		this.failures = 0;
	}

	public boolean predict(Instruction branchInstruction) {
		try {
			//1-bit dynamic predictor
			boolean test =  BTAC.getHistory(branchInstruction.getAddress());
			return test;
			//static predictor: return false/true;
		}
		catch (NullPointerException e) {
			//add to BTAC, always predict NOT taken the first time
			BTAC.entries.put(branchInstruction.getAddress(), new BTACEntry(((BranchEqual) branchInstruction).getImmediate(), false));
			return false;
		}
	}
	
	public int getBranchTarget(Instruction branchInstruction) {
		return BTAC.getBranchTarget(branchInstruction.getAddress());
	}
	
	public void updateBranchTaken(int instrAddress) {
		BTAC.updateHistory(instrAddress, true);
	}
	
	public void updateBranchNotTaken(int instrAddress) {
		BTAC.updateHistory(instrAddress, false);
	}	
}
