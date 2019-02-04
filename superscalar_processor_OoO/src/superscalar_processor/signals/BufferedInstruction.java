package superscalar_processor.signals;


public class BufferedInstruction {
	
	public BufferedInstruction(DecodedInstruction i, int nextPC, int branchInstrAddr, boolean predictTaken) {
		this.branchInstrAddress = branchInstrAddr;
		this.instruction = i;
		this.nextPC = nextPC;
		this.predictTaken = predictTaken;
	}

	public DecodedInstruction instruction;
	
	public int nextPC;//for branch offset calculations
	public int branchInstrAddress;
	public boolean predictTaken;
	
	public boolean ready = true;
}
