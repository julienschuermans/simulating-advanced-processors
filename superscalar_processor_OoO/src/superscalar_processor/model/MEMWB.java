package superscalar_processor.model;

import superscalar_processor.signals.ALUControlSignal;
import superscalar_processor.signals.DecodedInstruction;
import superscalar_processor.signals.MuxControlSignal;
import superscalar_processor.signals.OpCode;

public class MEMWB extends InterStageBuffer {

	public MEMWB(int size) {
		super(size);
		clear();
	}

	@Override
	public void clear() {
		inputs[0] = new DecodedInstruction(OpCode.NOP, 0, 0, 0, 0, 0, ALUControlSignal.ZERO, MuxControlSignal.ZERO, 0);
		inputs[1] = 0;
		inputs[2] = 0;
		inputs[3] = 0;
		inputs[4] = 0;
		inputs[5] = 0;
		
		
		outputs[0] = new DecodedInstruction(OpCode.NOP, 0, 0, 0, 0, 0, ALUControlSignal.ZERO, MuxControlSignal.ZERO, 0);
		outputs[1] = 0;
		outputs[2] = 0;
		outputs[3] = 0;
		outputs[4] = 0;
		outputs[5] = 0;
	}

}
