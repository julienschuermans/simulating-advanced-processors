package superscalar_processor.model;

import java.util.HashMap;
import java.util.Map;

public class RegisterFile {
	
	public RegisterFile() {
		registers.put(0, new Register(0, "$zero", 0));
		
		registers.put(1, new Register(1, "$at", 0));
	
		registers.put(2, new Register(2, "$v0", 0));
		registers.put(3, new Register(3, "$v1", 0));
		
		registers.put(4, new Register(4, "$a0", 0));
		registers.put(5, new Register(5, "$a1", 0));
		registers.put(6, new Register(6, "$a2", 0));
		registers.put(7, new Register(7, "$a3", 0));
		
		registers.put(8, new Register(8, "$t0", 0));
		registers.put(9, new Register(9, "$t1", 0));
		registers.put(10, new Register(10, "$t2", 0));
		registers.put(11, new Register(11, "$t3", 0));
		registers.put(12, new Register(12, "$t4", 0));
		registers.put(13, new Register(13, "$t5", 0));
		registers.put(14, new Register(14, "$t6", 0));
		registers.put(15, new Register(15, "$t7", 0));
		
		registers.put(16, new Register(16, "$s0", 0));
		registers.put(17, new Register(17, "$s1", 0));
		registers.put(18, new Register(18, "$s2", 0));
		registers.put(19, new Register(19, "$s3", 0));
		registers.put(20, new Register(20, "$s4", 0));
		registers.put(21, new Register(21, "$s5", 0));
		registers.put(22, new Register(22, "$s6", 0));
		registers.put(23, new Register(23, "$s7", 0));
		
		registers.put(24, new Register(24, "$t8", 0));
		registers.put(25, new Register(25, "$t9", 0));
		
		registers.put(26, new Register(26, "$k0", 0)); //used by kernel for branch prediction
		registers.put(27, new Register(27, "$k1", 0));
		
		registers.put(28, new Register(28, "$gp", 268468224));
		registers.put(29, new Register(29, "$sp", 2147479548));
		registers.put(30, new Register(30, "$fp", 0));
		registers.put(31, new Register(31, "$ra", 0));
		
		
		this.programCounter = new Register(32, "PC", 4194304);
	}
	
	public Register getProgramCounter() {
		return programCounter;
	}
	public void setProgramCounter(int PC) {
		this.programCounter.setValue(PC);
	}
	
	public void writeRegister(int address, int value) {
		registers.get(address).setValue(value);
	}

	public final Map<Integer, Register> registers = new HashMap<Integer, Register>();
	private Register programCounter;
}
