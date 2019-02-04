package superscalar_processor.model;

import superscalar_processor.signals.OpCode;

public class MemoryAccessUnit {

	public MemoryAccessUnit() {
		LMD = 0;
	}
	
	public int getLatency() {
		return 1;
	}

	public void readWrite(Memory mem, OpCode opcode, int ALUoutput, int rtValue) {
		if (opcode == OpCode.LW) {
			LMD = mem.read(ALUoutput);
			
		}
		else if (opcode == OpCode.SW) {
			mem.write(ALUoutput, rtValue);
		}
	}
	
	public void read(Memory mem, int ALUoutput) {
		LMD = mem.read(ALUoutput);
	}
	
	public void write(Memory mem, int ALUoutput, int rtValue) {
		mem.write(ALUoutput, rtValue);
	}
	
	public int LMD;

}
