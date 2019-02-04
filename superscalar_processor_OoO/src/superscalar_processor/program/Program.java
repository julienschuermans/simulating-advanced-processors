package superscalar_processor.program;

import java.util.ArrayList;
import java.util.Iterator;

public class Program {
	
	public Program() {
		this.instructions = new ArrayList<Instruction>();
	}

	public void addInstruction(Instruction instruction) {
		this.instructions.add(instruction);
	}

	public ArrayList<Instruction> getInstructions() {
		return instructions;
	}
	private final ArrayList<Instruction> instructions;
	
	public String toString() {
		Iterator<Instruction> it = instructions.iterator();
		String result = "";
	    while (it.hasNext()) {
	        Instruction instr = it.next();
	        result += instr.toString() + "\n";
	    }
	    return result;
	}
}
