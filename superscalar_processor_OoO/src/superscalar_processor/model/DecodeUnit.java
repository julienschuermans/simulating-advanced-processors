package superscalar_processor.model;

import superscalar_processor.program.Instruction;
import superscalar_processor.program.instructions.ImmediateInstruction;
import superscalar_processor.program.instructions.JumpInstruction;
import superscalar_processor.program.instructions.RegisterInstruction;
import superscalar_processor.signals.ALUControlSignal;
import superscalar_processor.signals.DecodedInstruction;
import superscalar_processor.signals.InstructionType;
import superscalar_processor.signals.MuxControlSignal;
import superscalar_processor.signals.OpCode;

public class DecodeUnit {

	public int getLatency() {
		return 1;
	}

	private OpCode getOpCode(Instruction instruction) {
		return instruction.getOpcode();
	}

	private InstructionType getInstructionType(Instruction instruction) {
		OpCode opcode = instruction.getOpcode();
		switch (opcode) {
		case ADD:
			return InstructionType.REGISTER;
		case MUL:
			return InstructionType.REGISTER;
		case SLT:
			return InstructionType.REGISTER;
		case SUB:
			return InstructionType.REGISTER;
		case ADDI:
			return InstructionType.IMMEDIATE;
		case BEQ:
			return InstructionType.IMMEDIATE;
		case LW:
			return InstructionType.IMMEDIATE;
		case SW:
			return InstructionType.IMMEDIATE;
		case J:
			return InstructionType.JUMP;
		case NOP:
			return InstructionType.NOP;
		default:
			break;
		}
		return null;
	}

	public DecodedInstruction decode(Instruction instruction, RegisterFile registerFile) {
		int rs = 0;
		int rt = 0;
		int rd = 0;
		int immediate = 0;
		int target = 0;
		OpCode opcode = getOpCode(instruction);
		
		InstructionType instructionType = getInstructionType(instruction);
		switch (instructionType) {
		
		case REGISTER:
			rs = resolveAddress(((RegisterInstruction) instruction).getRS());
			rt = resolveAddress(((RegisterInstruction) instruction).getRT());
			rd = resolveAddress(((RegisterInstruction) instruction).getRD());
			break;
		case IMMEDIATE:
			rs = resolveAddress(((ImmediateInstruction) instruction).getRS());
			rt = resolveAddress(((ImmediateInstruction) instruction).getRT());
			immediate = signExtend(((ImmediateInstruction) instruction).getImmediate());
			break;
		case JUMP:
			target = ((JumpInstruction) instruction).getTargetAddress();
			break;
		default://NOP: InstructionType = NOP
			break;
		}
		
		ALUControlSignal aluControl = ALUControlSignal.ZERO;
		MuxControlSignal aluSrc = MuxControlSignal.ZERO;
		
		
		switch (getOpCode(instruction)) {
		case ADD:
			aluControl = ALUControlSignal.ADD;
			aluSrc = MuxControlSignal.A;
			break;
		case SUB:
			aluControl = ALUControlSignal.SUB;
			aluSrc = MuxControlSignal.A;
			break;
		case SLT:
			aluControl = ALUControlSignal.SLT;
			aluSrc = MuxControlSignal.A;
			break;	
		case ADDI:
			aluControl = ALUControlSignal.ADD;
			aluSrc = MuxControlSignal.B;
			break;
		case MUL:
			aluControl = ALUControlSignal.ZERO;
			aluSrc = MuxControlSignal.ZERO;
			break;
		case BEQ:
			aluControl = ALUControlSignal.SUB;
			aluSrc = MuxControlSignal.A;
			break;
		case J:
			aluControl = ALUControlSignal.ZERO;
			aluSrc = MuxControlSignal.ZERO;
			break;
		case LW:
			aluControl = ALUControlSignal.ADD;
			aluSrc = MuxControlSignal.B;
			break;
		case SW:
			aluControl = ALUControlSignal.ADD;
			aluSrc = MuxControlSignal.B;
			break;
		default:
			break;
		
		}
		
		int programOrder = instruction.programOrder;
		
		return new DecodedInstruction(opcode, rs, rt, rd, immediate, target, aluControl, aluSrc, programOrder);
	}
	
	
	//dummy sign extension implementation because all immediate values are represented as integers anyway.
	private int signExtend(int immediate) {
		return immediate;
	}
	
	private int resolveAddress(String registerName) {
		switch (registerName) {
		case "$zero": return 0;
		
		case "$at": return 1;
		
		case "$v0": return 2;
		case "$v1": return 3;
		
		case "$a0": return 4;
		case "$a1": return 5;
		case "$a2": return 6;
		case "$a3": return 7;
		
		case "$t0": return 8;
		case "$t1": return 9;
		case "$t2": return 10;
		case "$t3": return 11;
		case "$t4": return 12;
		case "$t5": return 13;
		case "$t6": return 14;
		case "$t7": return 15;
		
		case "$s0": return 16;
		case "$s1": return 17;
		case "$s2": return 18;
		case "$s3": return 19;
		case "$s4": return 20;
		case "$s5": return 21;
		case "$s6": return 22;
		case "$s7": return 23;
		
		case "$t8": return 24;
		case "$t9": return 25;
		
		case "$k0": return 26;
		case "$k1": return 27;
		
		case "$gp": return 28;
		case "$sp": return 29;
		case "$fp": return 30;
		case "$ra": return 31;
		default: return 0;
		}
	}

	public boolean issue(OpCode opcode1, OpCode opcode2) {
		if (opcode2 == OpCode.J || opcode2 == OpCode.BEQ) {
			return true;	
		}
		else {
			return false;//no swap necessary
		}
	}
	
}
