package superscalar_processor.model;

import superscalar_processor.program.instructions.NOP;

public class IFID extends InterStageBuffer {

	public IFID(int size) {
		super(size);
		clear();
	}
	
	@Override
	public void clear() {
		inputs[0] = new NOP(0);
		inputs[1] = 0;
		inputs[2] = 0;
		inputs[3] = false;
		outputs[0] = new NOP(0);
		outputs[1] = 0;
		outputs[2] = 0;
		outputs[3] = false;
	}

}
