package superscalar_processor.model;

import java.util.LinkedList;

import superscalar_processor.signals.ALUControlSignal;
import superscalar_processor.signals.BufferedInstruction;
import superscalar_processor.signals.DecodedInstruction;
import superscalar_processor.signals.MuxControlSignal;
import superscalar_processor.signals.OpCode;

public class InstructionQueue {
	
	public InstructionQueue(int initialSize) {
		for (int i = 0; i<initialSize; i++) {
			//insert 'decoded' nops into the buffer
			entries.addLast(new BufferedInstruction(new DecodedInstruction(OpCode.NOP, 0, 0, 0, 0, 0, ALUControlSignal.ZERO, MuxControlSignal.ZERO, 0), 0, 0, false));
		}
	}
	
	public LinkedList<BufferedInstruction> entries = new LinkedList<BufferedInstruction>();
	
	public void clear() {
		entries.clear();
	}
	
}
