package superscalar_processor.model;

import superscalar_processor.signals.OpCode;

public class WritebackUnit {

	public WritebackUnit() {
	}

	public int getLatency() {
		return 1;
	}
	public void writeBack(RegisterFile registerFile, OpCode opcode, int rt, int rd, int ALUresult, int LMD) { //dit is niet ALUresuly, maar executionUnitResult
		if (opcode == OpCode.ADD || opcode == OpCode.SLT || opcode == OpCode.SUB || opcode == OpCode.MUL) {
			registerFile.writeRegister(rd, ALUresult);
		}
		else if (opcode == OpCode.ADDI) {
			registerFile.writeRegister(rt, ALUresult);
		}
		else if (opcode == OpCode.LW) {
			registerFile.writeRegister(rt, LMD);
		}
	}

	public void ROBWriteBack(RegisterFile registerFile,
			int destinationRegister, int value) {
		registerFile.writeRegister(destinationRegister, value);
		
	}
	
}
