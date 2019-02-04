package superscalar_processor.main;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import superscalar_processor.program.Instruction;
import superscalar_processor.program.Program;
import superscalar_processor.program.instructions.Add;
import superscalar_processor.program.instructions.AddImmediate;
import superscalar_processor.program.instructions.BranchEqual;
import superscalar_processor.program.instructions.Jump;
import superscalar_processor.program.instructions.LoadWord;
import superscalar_processor.program.instructions.Multiply;
import superscalar_processor.program.instructions.NOP;
import superscalar_processor.program.instructions.SetLessThan;
import superscalar_processor.program.instructions.Stop;
import superscalar_processor.program.instructions.StoreWord;
import superscalar_processor.program.instructions.Subtract;

public class ProgramParser {

	public Program parse(String programString) {
		Program program = new Program();
		Map<Integer, String> jumps = new HashMap<Integer, String>();
		Map<Integer, String[]> beqs = new HashMap<Integer, String[]>();
		Map<String, Integer> labels = new HashMap<String, Integer>();
		
		String separated[] = programString.split("\\r\\n|\\n|\\r");
		
		int instructionOffset = 4194304;
		int prevAddress = 0;
		int address = 0;
		boolean saveLabel = false;
		String label = "";
				
		for (int i = 0; i < separated.length; i++) {
			if (separated[i].length() == 0) {
				//empty line
				saveLabel = false;
			}
			else if (separated[i].charAt(separated[i].length()-1) == ':') {
				//line is a label definition
				saveLabel = true;
				label = separated[i].substring(0, separated[i].length()-1);
			}
			else {
				if (prevAddress == 0) {
					address = instructionOffset;
				} else {
					address = prevAddress + 4;
				}
				
				if (saveLabel) {
					labels.put(label, address);
					saveLabel = false;
				}
				//new instruction
				Instruction instruction = null;
				
				//filter out tabs
				if (separated[i].substring(0,1).equals("	")) {
					separated[i] = separated[i].split("	")[1];
				}
				
				if (separated[i].substring(0,3).equals("nop")) {
					program.addInstruction(new NOP(address));
				}
				else if (separated[i].substring(0,4).equals("addi")) {
					String[] args = separated[i].substring(5).split(", ");
					String target = args[0]; //rt
					String source = args[1]; //rs
					int value = Integer.parseInt(args[2]); //imm
					instruction = new AddImmediate(address, source, target, value);
					program.addInstruction(instruction);
				}
				else if (separated[i].substring(0,3).equals("add")) {
					String[] args = separated[i].substring(4).split(", ");
					String target = args[0]; //rd
					String source1 = args[1]; //rs
					String source2 = args[2]; //rt
					instruction = new Add(address, target, source1, source2);
					program.addInstruction(instruction);
				}
				else if (separated[i].substring(0,3).equals("sub")) {
					String[] args = separated[i].substring(4).split(", ");
					String target = args[0]; //rd
					String source1 = args[1]; //rs
					String source2 = args[2]; //rt
					instruction = new Subtract(address, source1, source2, target);
					program.addInstruction(instruction);
				}
				else if (separated[i].substring(0,3).equals("slt")) {
					String[] args = separated[i].substring(4).split(", ");
					String target = args[0]; //rd
					String source1 = args[1]; //rs
					String source2 = args[2]; //rt
					instruction = new SetLessThan(address, source1, source2, target);
					program.addInstruction(instruction);
				}
				else if (separated[i].substring(0,3).equals("mul")) {
					String[] args = separated[i].substring(4).split(", ");
					String target = args[0]; //rd
					String source1 = args[1]; //rs
					String source2 = args[2]; //rt
					instruction = new Multiply(address, source1, source2, target);
					program.addInstruction(instruction);
				}
				else if (separated[i].substring(0,1).equals("j")) {
					String target = separated[i].substring(2); //targets are Strings
					jumps.put(address, target);
				}
				else if (separated[i].substring(0,2).equals("lw")) {
					String[] args = separated[i].substring(3).split(", ");
					String target = args[0]; //rt
					String[] source = args[1].split("\\(");
					String mem = source[1].substring(0, source[1].length()-1);
					int offset = Integer.parseInt(source[0]);
					instruction = new LoadWord(address, mem, target, offset);
					program.addInstruction(instruction);
				}
				else if (separated[i].substring(0,2).equals("sw")) {
					String[] args = separated[i].substring(3).split(", ");
					String source = args[0]; //rt
					String[] target = args[1].split("\\(");
					String mem = target[1].substring(0, target[1].length()-1);
					int offset = Integer.parseInt(target[0]);
					instruction = new StoreWord(address, source, mem, offset);
					program.addInstruction(instruction);
				}
				else if (separated[i].substring(0,3).equals("beq")) {
					String[] args = separated[i].substring(4).split(", ");
					String labeltarget = args[2]; //rd
					String source1 = args[0]; //rs
					String source2 = args[1]; //rt
					String beqInfo[] = {source1, source2, labeltarget};
					beqs.put(address, beqInfo);
				}
				
				
				else {
					System.out.println("error: " + separated[i]);
				}
				prevAddress = address;
			}
		}
		program.addInstruction(new NOP(prevAddress + 4));
		program.addInstruction(new NOP(prevAddress + 8));
		program.addInstruction(new NOP(prevAddress + 12));
		program.addInstruction(new NOP(prevAddress + 16));
		program.addInstruction(new Stop(prevAddress + 20));
		
		
		//replace jump labels (str) by ints
		Iterator<Entry<Integer, String>> it = jumps.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<Integer, String> pair = (Map.Entry<Integer, String>)it.next();
	        program.addInstruction(new Jump((Integer) pair.getKey(), labels.get(pair.getValue())));
	    }
	    
	  //replace beq labels (str) by ints
  		Iterator<Entry<Integer, String[]>> beqIt = beqs.entrySet().iterator();
  	    while (beqIt.hasNext()) {
  	        Map.Entry<Integer, String[]> pair = (Map.Entry<Integer, String[]>)beqIt.next();
  	        program.addInstruction(new BranchEqual((Integer) pair.getKey(), pair.getValue()[0], pair.getValue()[1], labels.get(pair.getValue()[2])-4-(Integer) pair.getKey())); // relative offset calculations
  	    }
	    
	    return program;
	}
}
