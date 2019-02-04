package superscalar_processor.model;


import java.util.Map;
import java.util.TreeMap;

import superscalar_processor.program.Instruction;

public class Memory {

	public void addInstruction(Instruction instr) {
		instructions.put(instr.getAddress(), instr);
	}
	
	public int read(int address) {
		return data.get(address);
	}
	
	public void write(int address, int value) {
		data.put(address, value);
	}
	
	public Map<Integer, Instruction> instructions = new TreeMap<Integer, Instruction>();
	
	public Map<Integer, Integer> data = new TreeMap<Integer, Integer>();
}
