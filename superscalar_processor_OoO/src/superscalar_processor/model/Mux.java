package superscalar_processor.model;

import superscalar_processor.signals.MuxControlSignal;

public class Mux {
	
	public Mux() {
		this.inputA = 0;
		this.inputB = 0;
		muxControl = MuxControlSignal.ZERO;
	}
	
	
	public int inputA;
	public int inputB;
	
	public MuxControlSignal muxControl;
	
	public int output() {
		switch (this.muxControl) {
		case A: return inputA;
		case B: return inputB;
		case ZERO:
			return 0;
		default:
			return 0;
		}
	}
}
