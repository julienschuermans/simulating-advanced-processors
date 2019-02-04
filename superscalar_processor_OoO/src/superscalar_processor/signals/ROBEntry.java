package superscalar_processor.signals;


public class ROBEntry {

	public ROBEntry(boolean speculative) {
		this.speculative = speculative;
		ready = false;
		speculationConfirmed = false;
		destinationRegister = 0;
		value = 0;
	}
	
	public boolean ready;
	public boolean speculative;
	public boolean speculationConfirmed;
	public int destinationRegister;
	public int value;
	public OpCode opcode;
	
}
