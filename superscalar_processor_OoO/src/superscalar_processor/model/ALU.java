package superscalar_processor.model;

import superscalar_processor.signals.ALUControlSignal;


public class ALU {
	
	public ALU() {
		this.inputA = 0;
		this.inputB = 0;
		this.output = 0;
		this.controlInput = ALUControlSignal.ZERO;
	}

	public int inputA;
	public int inputB;
	public int output;
	
	public ALUControlSignal controlInput;	
	
	public void cycle() {
		switch (controlInput) {
		
		//arithmetic
		case ADD:
			output = inputA + inputB;
			break;
		case SUB:
			output = inputA - inputB;
			break;
		case SLT:
			if (inputA < inputB) {
				output = 1;
			}
			else {
				output = 0;
			}
			break;
		
		//logic
		case AND:
			output = inputA & inputB;
			break;
		case OR:
			output = inputA | inputB;
			break;
		case ZERO:
			output = 0;
			break;
		default:
			break;
		}
	}
	
}
