package superscalar_processor.model;

import java.util.Iterator;
import java.util.Observable;

import superscalar_processor.program.Instruction;
import superscalar_processor.program.Program;
import superscalar_processor.program.instructions.NOP;
import superscalar_processor.signals.ALUControlSignal;
import superscalar_processor.signals.BufferedInstruction;
import superscalar_processor.signals.DecodedInstruction;
import superscalar_processor.signals.MuxControlSignal;
import superscalar_processor.signals.OpCode;
import superscalar_processor.signals.ROBEntry;
import superscalar_processor.signals.StoreBufferEntry;

public class Processor extends Observable{

	private Memory memory;
	private RegisterFile registerFile;
	
	public FetchUnit fetchUnit;
	private DecodeUnit decodeUnit;
	
	private InstructionQueue reservationStation;
	private RegisterMappingTable mappingTable;
	
	//pipeline 1 branching + ALU
	private BranchUnit executionUnit1;
	private MemoryAccessUnit memUnit1;
	public IFID IFID1;
	public IDEX IDEX1;
	private EXMEM EXMEM1;
	public MEMWB MEMWB1;
	
	//pipeline 2 load/store
	private IntegerUnit executionUnit2;
	private MemoryAccessUnit memUnit2;
	public IFID IFID2;
	public IDEX IDEX2;
	private EXMEM EXMEM2;
	public MEMWB MEMWB2;
	
	private ReorderBuffer ROB;
	
	public boolean branchPrediction;
	public boolean registerRenaming;
	public boolean outOfOrder;
	public int superscalar;
	
	private boolean speculating = false;
	
	public Processor(int superscalar, boolean registerRenaming, boolean branchPrediction, boolean outOfOrder) {
		this.superscalar = superscalar;
		this.registerRenaming = registerRenaming;
		this.branchPrediction = branchPrediction;
		this.outOfOrder = outOfOrder;
		
		registerFile = new RegisterFile();
		memory = new Memory();	
		
		fetchUnit = new FetchUnit();
		decodeUnit = new DecodeUnit();
		if (branchPrediction) {
			fetchUnit.addBranchPredictor();
		}
		
		reservationStation = new InstructionQueue(0);
		mappingTable = new RegisterMappingTable();
		
		executionUnit1 = new BranchUnit();
		memUnit1 = new MemoryAccessUnit();
		
		IFID1 = new IFID(4);
		IDEX1= new IDEX(4);
		EXMEM1 = new EXMEM(5);
		MEMWB1 = new MEMWB(6);
		addObserver(IFID1);
		addObserver(IDEX1);
		addObserver(EXMEM1);
		addObserver(MEMWB1);
		
		//init pipeline 2
		if (superscalar == 2) {
			executionUnit2 = new IntegerUnit();
			memUnit2 = new MemoryAccessUnit();
			
			IFID2 = new IFID(4);
			IDEX2= new IDEX(4);
			EXMEM2 = new EXMEM(5);
			MEMWB2 = new MEMWB(6);
			addObserver(IFID2);
			addObserver(IDEX2);
			addObserver(EXMEM2);
			addObserver(MEMWB2);
		}
		ROB = new ReorderBuffer(new WritebackUnit());
	}
	
	public void load(Program myProgram) {
		Iterator<Instruction> it = myProgram.getInstructions().iterator();
	    while (it.hasNext()) {
	        Instruction instr = it.next();
	        getMemory().addInstruction(instr);
	    }
	}
	
	public void cycle() {
		//shift all interstagebuffers
		setChanged();
		notifyObservers();
		
		//FETCH
		System.out.println("PC: " + getRegisterFile().getProgramCounter().getValue() + "\n");
		boolean isConditionalBranch = false; //signals the executionUnit whether it's speculatively executing instructions or not
//		System.out.println("in reservation station:");
//		Iterator<BufferedInstruction> it2 = reservationStation.entries.iterator();
//		while (it2.hasNext()) {
//			BufferedInstruction i = it2.next();
//			System.out.print(i.instruction.opcode + " - ");
//		}
//		System.out.println("");
		
		
		if (reservationStation.entries.size() < 3) {
			
			int PC_step = 4*superscalar;
			fetchUnit.pcMux.inputA = getRegisterFile().getProgramCounter().getValue() + PC_step;
			
			Instruction instruction1 = fetchUnit.fetch(getRegisterFile().getProgramCounter().getValue(), getMemory());
			//instruction1.programOrder = fetchNumber;
			//fetchNumber += 1;
			IFID1.inputs[0] = instruction1;
			IFID1.inputs[1] = getRegisterFile().getProgramCounter().getValue() + 4;
			System.out.println("fetch1 " + instruction1);
			
			Instruction instruction2 = null;
			
			if (superscalar == 2) {
				instruction2 = fetchUnit.fetch(getRegisterFile().getProgramCounter().getValue()+4, getMemory());
				System.out.println("fetch2 " + instruction2);
				IFID2.inputs[0] = instruction2;
				IFID2.inputs[1] = getRegisterFile().getProgramCounter().getValue() + 8;
			}
			
			
			if (branchPrediction) { //if beq-> next PC decided by fetch.branchpredictor. else -> determined by ALUoutput
				isConditionalBranch = fetchUnit.predecode(instruction1);
				if (isConditionalBranch) {
					//speculating = true;
					boolean predictTaken = fetchUnit.branchPredictor.predict(instruction1);
					
					//boolean predictTaken = true;
					int targetAddress = fetchUnit.branchPredictor.getBranchTarget(instruction1);
					IFID1.inputs[3] = predictTaken;
					
					if (superscalar == 2 && predictTaken) {
						//overwrite instruction2
						instruction2 = new NOP(0);
						IFID2.inputs[0] = instruction2;
					}
					
					if (predictTaken) {
						getRegisterFile().writeRegister(26, getRegisterFile().getProgramCounter().getValue() + 4);//store PC + 4
						getRegisterFile().setProgramCounter(getRegisterFile().getProgramCounter().getValue() + 4 + targetAddress);
					}
					else {
						getRegisterFile().writeRegister(27, getRegisterFile().getProgramCounter().getValue() + 4 + targetAddress);
						getRegisterFile().setProgramCounter(getRegisterFile().getProgramCounter().getValue() + PC_step);
						
					}
					IFID1.inputs[2] = instruction1.getAddress();
				}
				else {
					IFID1.inputs[2] = 0;
				}
			}
			
			//check if instr2 is a branch
			if (superscalar == 2 && branchPrediction && !isConditionalBranch) {
				isConditionalBranch = fetchUnit.predecode(instruction2);
				if (isConditionalBranch) {
					boolean predictTaken = fetchUnit.branchPredictor.predict(instruction2);
					int targetAddress = fetchUnit.branchPredictor.getBranchTarget(instruction2);
					IFID2.inputs[3] = predictTaken;			
					
					if (predictTaken) {
						getRegisterFile().writeRegister(26, getRegisterFile().getProgramCounter().getValue() + 8);
						getRegisterFile().setProgramCounter(getRegisterFile().getProgramCounter().getValue() + 8 + targetAddress);
					}
					else {
						getRegisterFile().writeRegister(27, getRegisterFile().getProgramCounter().getValue() + 8 + targetAddress);
						getRegisterFile().setProgramCounter(getRegisterFile().getProgramCounter().getValue() + PC_step);
						
					}
					IFID2.inputs[2] = instruction2.getAddress();
				}
				else {
					IFID2.inputs[2] = 0;
				}
			}
			
			//mechanism to clear invalid values from outputs when fetching after you haven't fetched for a while
			if (IFID1.outputs[0] == null) {
				IFID1.outputs[0] = new NOP(0);
				IFID1.outputs[1] = 0;
				IFID1.outputs[2] = 0;
				IFID1.outputs[3] = false;
				
				if (superscalar == 2) {
					IFID2.outputs[0] = new NOP(0);
					IFID2.outputs[1] = 0;
					IFID2.outputs[2] = 0;
					IFID2.outputs[3] = false;
				}
			}
			
		}
		else {
			//if you don't fetch you want to have the next PC the next cycle (instead of PC+4)
			fetchUnit.pcMux.inputA = getRegisterFile().getProgramCounter().getValue();
			IFID1.inputs[0] = null;
		}
		
		//DECODE
		boolean swapPipes = false;
		if (IFID1.outputs[0] != null) {
			DecodedInstruction result1 = decodeUnit.decode((Instruction) IFID1.outputs[0], registerFile);
			
			reservationStation.entries.addLast(new BufferedInstruction(result1, (int) IFID1.outputs[1], (int) IFID1.outputs[2], (boolean) IFID1.outputs[3]));
			OpCode op1 = result1.opcode;
			//all opcodes that the ROB has to write back
			if (op1 == OpCode.ADD || op1 == OpCode.SLT || op1 == OpCode.SUB || op1 == OpCode.MUL || op1 == OpCode.ADDI || op1 == OpCode.LW) {
				int ROBnum = 0;
				if (!branchPrediction) {
					ROBnum = ROB.addEntry(new ROBEntry(false));
				}
				else {
					if (speculating) {
						ROBnum = ROB.addEntry(new ROBEntry(true));
					}
					else {
						ROBnum = ROB.addEntry(new ROBEntry(false));
					}
				}
				
				reservationStation.entries.getLast().instruction.ROBaddress = ROBnum;
				if (op1 == OpCode.ADD || op1 == OpCode.SLT || op1 == OpCode.SUB || op1 == OpCode.MUL) {
					mappingTable.entries.get(reservationStation.entries.getLast().instruction.rd).registerValid = false;
					//System.out.println("blocking: " + reservationStation.entries.getLast().instruction.rd);
					mappingTable.entries.get(reservationStation.entries.getLast().instruction.rd).ROBentry = ROBnum;
					ROB.entries.get(ROBnum).destinationRegister = reservationStation.entries.getLast().instruction.rd;
				}
				else if (op1 == OpCode.ADDI || op1 == OpCode.LW) {
					mappingTable.entries.get(reservationStation.entries.getLast().instruction.rt).registerValid = false;
					mappingTable.entries.get(reservationStation.entries.getLast().instruction.rt).ROBentry = ROBnum;
					//System.out.println("blocking: " + reservationStation.entries.getLast().instruction.rt);
					ROB.entries.get(ROBnum).destinationRegister = reservationStation.entries.getLast().instruction.rt;
				}
			}
			else if (op1 == OpCode.BEQ) {
				speculating = true;
			}
			
			
			DecodedInstruction result2 = null;
			
			if (superscalar == 2) {
				result2 = decodeUnit.decode((Instruction) IFID2.outputs[0], registerFile);
				swapPipes = decodeUnit.issue(result1.opcode, result2.opcode);
				reservationStation.entries.addLast(new BufferedInstruction(result2, (int) IFID2.outputs[1], (int) IFID2.outputs[2], (boolean) IFID2.outputs[3]));
			
				OpCode op2 = result2.opcode;
			
				if (op2 == OpCode.ADD || op2 == OpCode.SLT || op2 == OpCode.SUB || op2 == OpCode.MUL || op2 == OpCode.ADDI || op2 == OpCode.LW) {
					int ROBnum = 0;
					if (!branchPrediction) {
						ROBnum = ROB.addEntry(new ROBEntry(false));
					}
					else {
						if (speculating) {
							ROBnum = ROB.addEntry(new ROBEntry(true));
						}
						else {
							ROBnum = ROB.addEntry(new ROBEntry(false));
						}
					}
					reservationStation.entries.getLast().instruction.ROBaddress = ROBnum;
					if (op2 == OpCode.ADD || op2 == OpCode.SLT || op2 == OpCode.SUB || op2 == OpCode.MUL) {
						mappingTable.entries.get(reservationStation.entries.getLast().instruction.rd).registerValid = false;
						mappingTable.entries.get(reservationStation.entries.getLast().instruction.rd).ROBentry = ROBnum;
						//System.out.println("blocking: " + reservationStation.entries.getLast().instruction.rd);
						ROB.entries.get(ROBnum).destinationRegister = reservationStation.entries.getLast().instruction.rd;
					}
					else if (op2 == OpCode.ADDI || op2 == OpCode.LW) {
						mappingTable.entries.get(reservationStation.entries.getLast().instruction.rt).registerValid = false;
						mappingTable.entries.get(reservationStation.entries.getLast().instruction.rt).ROBentry = ROBnum;
						//System.out.println("blocking: " + reservationStation.entries.getLast().instruction.rt);
						ROB.entries.get(ROBnum).destinationRegister = reservationStation.entries.getLast().instruction.rt;

					}
				}
				else if (op2 == OpCode.BEQ) {
					speculating = true;
				}
			}
			IFID1.outputs[0] = null;
		}
		//ISSUE
		int rs1 = reservationStation.entries.peekFirst().instruction.rs;
		int rt1 = reservationStation.entries.peekFirst().instruction.rt;
		
		
		BufferedInstruction buf1 = null;
		boolean firstRemoved = false;
		
			
		if (mappingTable.entries.get(rs1).registerValid || (reservationStation.entries.peekFirst().instruction.opcode != OpCode.BEQ && mappingTable.entries.get(rs1).ROBentry == reservationStation.entries.peekFirst().instruction.ROBaddress)) {
			if(!reservationStation.entries.peekFirst().instruction.rsValueValid) {
				reservationStation.entries.peekFirst().instruction.rsValue = registerFile.registers.get(rs1).getValue();
				reservationStation.entries.peekFirst().instruction.rsValueValid = true;
			}
		}
		
		if (mappingTable.entries.get(rt1).registerValid || (reservationStation.entries.peekFirst().instruction.opcode != OpCode.BEQ && mappingTable.entries.get(rt1).ROBentry == reservationStation.entries.peekFirst().instruction.ROBaddress)) {
			if (!reservationStation.entries.peekFirst().instruction.rtValueValid) {
				reservationStation.entries.peekFirst().instruction.rtValue = registerFile.registers.get(rt1).getValue();
				reservationStation.entries.peekFirst().instruction.rtValueValid = true;
			}
		}

		if ((reservationStation.entries.peekFirst().instruction.rsValueValid && reservationStation.entries.peekFirst().instruction.rtValueValid)
			|| (reservationStation.entries.peekFirst().instruction.opcode != OpCode.BEQ && mappingTable.entries.get(rs1).ROBentry == reservationStation.entries.peekFirst().instruction.ROBaddress && mappingTable.entries.get(rt1).ROBentry == reservationStation.entries.peekFirst().instruction.ROBaddress)) {
			buf1 = reservationStation.entries.remove();
			firstRemoved = true;
			System.out.println("issue1 " + buf1.instruction.opcode + ", rsValue: " + registerFile.registers.get(rs1).getValue()+ ", rtValue: " + registerFile.registers.get(rt1).getValue());
		}
		else {
			if (!reservationStation.entries.peekFirst().instruction.rsValueValid) {
				System.out.println(reservationStation.entries.peekFirst().instruction.opcode +  " waiting for rs: " + reservationStation.entries.peekFirst().instruction.rs);
			}
			else if (!reservationStation.entries.peekFirst().instruction.rtValueValid) {
				System.out.println(reservationStation.entries.peekFirst().instruction.opcode +  " waiting for rt: " + reservationStation.entries.peekFirst().instruction.rt);
			}
			buf1 = new BufferedInstruction(new DecodedInstruction(OpCode.NOP, 0, 0, 0, 0, 0, ALUControlSignal.ZERO, MuxControlSignal.ZERO, 0), 0, 0, false);
		}
	
		
		// same for buf2
			
		BufferedInstruction buf2 = null;
		if (superscalar == 2) {
			int rs2;
			int rt2;
			if (firstRemoved) {
				rs2 = reservationStation.entries.peekFirst().instruction.rs;
				rt2 = reservationStation.entries.peekFirst().instruction.rt;
				
				if (mappingTable.entries.get(rs2).registerValid || (reservationStation.entries.peekFirst().instruction.opcode != OpCode.BEQ && mappingTable.entries.get(rs2).ROBentry == reservationStation.entries.peekFirst().instruction.ROBaddress)) {
					if( !reservationStation.entries.peekFirst().instruction.rsValueValid) {
						reservationStation.entries.peekFirst().instruction.rsValue = registerFile.registers.get(rs2).getValue();
						reservationStation.entries.peekFirst().instruction.rsValueValid = true;
					}
				}
	
				if (mappingTable.entries.get(rt2).registerValid || (reservationStation.entries.peekFirst().instruction.opcode != OpCode.BEQ && mappingTable.entries.get(rt2).ROBentry == reservationStation.entries.peekFirst().instruction.ROBaddress)) {
					if (!reservationStation.entries.peekFirst().instruction.rtValueValid) {
		
						reservationStation.entries.peekFirst().instruction.rtValue = registerFile.registers.get(rt2).getValue();
						reservationStation.entries.peekFirst().instruction.rtValueValid = true;
					}
				}

				if ((reservationStation.entries.peekFirst().instruction.rsValueValid && reservationStation.entries.peekFirst().instruction.rtValueValid)
				|| (reservationStation.entries.peekFirst().instruction.opcode != OpCode.BEQ&& mappingTable.entries.get(rs2).ROBentry == reservationStation.entries.peekFirst().instruction.ROBaddress && mappingTable.entries.get(rt2).ROBentry == reservationStation.entries.peekFirst().instruction.ROBaddress) ) {
					buf2 = reservationStation.entries.remove();
					firstRemoved = true;
					System.out.println("issue2a " + buf2.instruction.opcode + ", rsValue: " + registerFile.registers.get(rs2).getValue()+ ", rtValue: " + registerFile.registers.get(rt2).getValue());
				}
				else {
					if (!reservationStation.entries.peekFirst().instruction.rsValueValid) {
						System.out.println(reservationStation.entries.peekFirst().instruction.opcode +  " waiting for rs: " + reservationStation.entries.peekFirst().instruction.rs);
					}
					else if (!reservationStation.entries.peekFirst().instruction.rtValueValid) {
						System.out.println(reservationStation.entries.peekFirst().instruction.opcode +  " waiting for rt: " + reservationStation.entries.peekFirst().instruction.rt);
					}
					
					buf2 = new BufferedInstruction(new DecodedInstruction(OpCode.NOP, 0, 0, 0, 0, 0, ALUControlSignal.ZERO, MuxControlSignal.ZERO, 0), 0, 0, false);
				}
			}
			else {// issue second instruction without the first one == out of order!!!
				rs2 = reservationStation.entries.get(1).instruction.rs;
				rt2 = reservationStation.entries.get(1).instruction.rt;
				
				if (mappingTable.entries.get(rs2).registerValid || (reservationStation.entries.get(1).instruction.opcode != OpCode.BEQ &&  mappingTable.entries.get(rs2).ROBentry == reservationStation.entries.get(1).instruction.ROBaddress)) {
					if (!reservationStation.entries.get(1).instruction.rsValueValid) {
						reservationStation.entries.get(1).instruction.rsValue = registerFile.registers.get(rs2).getValue();
						reservationStation.entries.get(1).instruction.rsValueValid = true;
					}
				}
	
				if (mappingTable.entries.get(rt2).registerValid || (reservationStation.entries.get(1).instruction.opcode != OpCode.BEQ && mappingTable.entries.get(rt2).ROBentry == reservationStation.entries.get(1).instruction.ROBaddress) ){
					if (!reservationStation.entries.get(1).instruction.rtValueValid) {
						reservationStation.entries.get(1).instruction.rtValue = registerFile.registers.get(rt2).getValue();
						reservationStation.entries.get(1).instruction.rtValueValid = true;
					}
				}
	
				if ( (reservationStation.entries.get(1).instruction.rsValueValid && reservationStation.entries.get(1).instruction.rtValueValid) 
					|| (reservationStation.entries.get(1).instruction.opcode != OpCode.BEQ && mappingTable.entries.get(rs2).ROBentry == reservationStation.entries.get(1).instruction.ROBaddress && mappingTable.entries.get(rt2).ROBentry == reservationStation.entries.get(1).instruction.ROBaddress) ) {
					buf2 = reservationStation.entries.remove(1);
					System.out.println("issue2b " + buf2.instruction.opcode + ", rsValue: " + registerFile.registers.get(rs2).getValue()+ ", rtValue: " + registerFile.registers.get(rt2).getValue());
				}
				else {
					if (!reservationStation.entries.get(1).instruction.rsValueValid) {
						System.out.println(reservationStation.entries.get(1).instruction.opcode +  " waiting for rs: " + reservationStation.entries.get(1).instruction.rs);
					}
					else if (!reservationStation.entries.get(1).instruction.rtValueValid) {
						System.out.println(reservationStation.entries.get(1).instruction.opcode +  " waiting for rt: " + reservationStation.entries.get(1).instruction.rt);
					}
					
					buf2 = new BufferedInstruction(new DecodedInstruction(OpCode.NOP, 0, 0, 0, 0, 0, ALUControlSignal.ZERO, MuxControlSignal.ZERO, 0), 0, 0, false);
				}	
			}
		}
		
		IDEX1.inputs[0] = buf1.instruction;
		IDEX1.inputs[1] = buf1.nextPC;//PC+4
		IDEX1.inputs[2] = buf1.branchInstrAddress; //branchInstrAddress
		IDEX1.inputs[3] = buf1.predictTaken; //predictTaken
		
		if (superscalar == 2) {
			IDEX2.inputs[0] = buf2.instruction;
			//not used
			IDEX2.inputs[1] = buf2.nextPC;
			IDEX2.inputs[2] = buf2.branchInstrAddress;
			IDEX2.inputs[3] = buf2.predictTaken;
			swapPipes = decodeUnit.issue(buf1.instruction.opcode, buf2.instruction.opcode);
			if (swapPipes) {
				//swap beq or jump to pipeline 1
				IDEX1.inputs[0] = buf2.instruction;
				IDEX1.inputs[1] = buf2.nextPC;//PC+4
				IDEX1.inputs[2] = buf2.branchInstrAddress; //branchInstrAddress
				IDEX1.inputs[3] = buf2.predictTaken; //predictTaken
				
				IDEX2.inputs[0] = buf1.instruction;
				//not used
				IDEX2.inputs[1] = buf1.nextPC;//PC+8
				IDEX2.inputs[2] = buf1.branchInstrAddress; //branchInstrAddress
				IDEX2.inputs[3] = buf1.predictTaken; //predictTaken
			}
		}
		
		if (buf1.instruction.opcode == OpCode.SW) {
			buf1.instruction.ROBaddress = ROB.addStore( new StoreBufferEntry(buf1.instruction.rtValue));
			if (branchPrediction && speculating) {
				ROB.storeBuffer.get(buf1.instruction.ROBaddress).speculative = true;
				//System.out.println("this store is speculative: " + ROB.storeBuffer.get(buf1.instruction.ROBaddress).memAddress + ", " + ROB.storeBuffer.get(buf1.instruction.ROBaddress).writeValue);
			}
		}
		
		if (outOfOrder && buf2.instruction.opcode == OpCode.SW) {
			buf2.instruction.ROBaddress = ROB.addStore( new StoreBufferEntry(buf2.instruction.rtValue));
			if (branchPrediction && speculating) {
				ROB.storeBuffer.get(buf2.instruction.ROBaddress).speculative = true;
			}
		}
		
		//EXECUTE
		DecodedInstruction i1 = (DecodedInstruction) IDEX1.outputs[0];
		
		executionUnit1.aluInputMux.inputA = i1.rtValue;
		executionUnit1.aluInputMux.inputB = i1.immediate; //result.immediate
		executionUnit1.aluInputMux.muxControl = i1.aluSrc;//result.aluSrc
		
		executionUnit1.ALU.inputA = i1.rsValue;//rsValue
		executionUnit1.ALU.inputB = executionUnit1.aluInputMux.output();
		executionUnit1.ALU.controlInput = i1.aluControl;//result.aluControl
		
		executionUnit1.multiplier.inputA = i1.rsValue;//rsValue
		executionUnit1.multiplier.inputB = i1.rtValue; //rtValue
	
		executionUnit1.adder.inputA = (int) IDEX1.outputs[1];//PC + 4 OR PC + 8
		executionUnit1.adder.inputB = executionUnit1.shiftLeft2(i1.immediate); //result.immediate
		
		executionUnit1.execute(i1.opcode);
		executionUnit1.branchTargetMux.inputA = executionUnit1.adder.output;
		executionUnit1.branchTargetMux.inputB = i1.target;//result.target
	
				
		DecodedInstruction i2 = null;
		if (superscalar == 2) {
			i2 = (DecodedInstruction) IDEX2.outputs[0];
			executionUnit2.aluInputMux.inputA = i2.rtValue; //rtValue
			executionUnit2.aluInputMux.inputB = i2.immediate; //result.immediate
			executionUnit2.aluInputMux.muxControl = i2.aluSrc;//result.aluSrc
			
			executionUnit2.ALU.inputA = i2.rsValue;//rsValue
			executionUnit2.ALU.inputB = executionUnit2.aluInputMux.output();
			executionUnit2.ALU.controlInput = i2.aluControl;//result.aluControl
			
			executionUnit2.multiplier.inputA = i2.rsValue;//rsValue
			executionUnit2.multiplier.inputB = i2.rtValue; //rtValue
			executionUnit2.execute(i2.opcode);
		}
		
		boolean rollback = false;
		
		//update PC only IDEX1 = branch pipe
		if (i1.opcode == OpCode.BEQ) {
			if (executionUnit1.output == 0) {
				//branch taken
				if (!branchPrediction) { //PC+4+offset or PC+8 + offset after branch is taken without prediction
					fetchUnit.pcMux.muxControl = MuxControlSignal.B;
					executionUnit1.branchTargetMux.muxControl = MuxControlSignal.A;
					//System.out.println("branch taken");
					reservationStation.clear(); //all prefetched instructions are discarded 
				}
				else {
					speculating = false;
					//System.out.println("no longer speculating");
					fetchUnit.branchPredictor.updateBranchTaken((int) IDEX1.outputs[2]);//address of the branch instruction
					if (!(boolean) IDEX1.outputs[3]) { //!predictTaken
						rollback = true;
						getRegisterFile().setProgramCounter(getRegisterFile().registers.get(27).getValue());
						fetchUnit.branchPredictor.failures += 1;
						//System.out.println("taken - not taken");
						ROB.rollBack(mappingTable);
					}
					else {
						fetchUnit.branchPredictor.successes += 1;
						//System.out.println("taken - taken");
						ROB.confirmSpeculation();
					}
				}
			}
			else {
				//branch not taken
				if (branchPrediction) {
					speculating = false;
					//System.out.println("no longer speculating");
					fetchUnit.branchPredictor.updateBranchNotTaken((int) IDEX1.outputs[2]);
					if ((boolean) IDEX1.outputs[3]) { //predict taken
						rollback = true;
						getRegisterFile().setProgramCounter(getRegisterFile().registers.get(26).getValue());
						fetchUnit.branchPredictor.failures += 1;
						//System.out.println("not taken - taken");
						ROB.rollBack(mappingTable);
					}
					else {
						fetchUnit.branchPredictor.successes += 1;
						//System.out.println("not taken - not taken");
						ROB.confirmSpeculation();
					}
				}
				else {
					// PC+4 or PC+8 stepping if branch not taken and no prediction scheme
					fetchUnit.pcMux.muxControl = MuxControlSignal.A;
					executionUnit1.branchTargetMux.muxControl = MuxControlSignal.A;
				}
			}
		}
		else if (i1.opcode == OpCode.J) {
			//select jump-target input from branchtargetmux
			fetchUnit.pcMux.muxControl = MuxControlSignal.B;
			executionUnit1.branchTargetMux.muxControl = MuxControlSignal.B;
			
		}
		else {
			//pc-input is gonna be PC+4 or PC+8 for all other instructions
			fetchUnit.pcMux.muxControl = MuxControlSignal.A;
			executionUnit1.branchTargetMux.muxControl = MuxControlSignal.ZERO;
			
		}
		
		fetchUnit.pcMux.inputB = executionUnit1.branchTargetMux.output(); //calculated branch target from previous instruction
		
		if (!isConditionalBranch && !rollback) { //update PC's with result from execute stage if there is no beq in the fetch stage, AND if the beq in the exec stage doesnt rollback
			getRegisterFile().setProgramCounter(fetchUnit.pcMux.output());
		}
		
		EXMEM1.inputs[0] = IDEX1.outputs[0];
		EXMEM1.inputs[1] = IDEX1.outputs[1];
		EXMEM1.inputs[2] = IDEX1.outputs[2];
		EXMEM1.inputs[3] = IDEX1.outputs[3];
		EXMEM1.inputs[4] = executionUnit1.output;
		
		if (superscalar == 2) {
			EXMEM2.inputs[0] = IDEX2.outputs[0];
			EXMEM2.inputs[1] = IDEX2.outputs[1];
			EXMEM2.inputs[2] = IDEX2.outputs[2];
			EXMEM2.inputs[3] = IDEX2.outputs[3];
			EXMEM2.inputs[4] = executionUnit2.output;
		}
		
		
		if (rollback) {
			System.out.println("rollback");
			if (!outOfOrder) {
				if (superscalar == 2) {
					IFID2.clear();
					IDEX2.clear();
					// not predicted, but taken
					if (!(boolean) IDEX1.outputs[3] && executionUnit1.output == 0) {
						EXMEM2.clear();
					}
				}
				IFID1.clear();
				IDEX1.clear();
			}
			else {
				reservationStation.clear();
				IFID1.clear();
				IFID2.clear();
			}
		}

		i1 = (DecodedInstruction) EXMEM1.outputs[0];
		if (!outOfOrder) {
			memUnit1.readWrite(memory, i1.opcode, (int) EXMEM1.outputs[4], i1.rtValue);
		}
		else if (i1.opcode == OpCode.LW){
			memUnit1.read(memory, (int) EXMEM1.outputs[4]);
		}
		MEMWB1.inputs[0] = EXMEM1.outputs[0];
		MEMWB1.inputs[1] = EXMEM1.outputs[1];
		MEMWB1.inputs[2] = EXMEM1.outputs[2];
		MEMWB1.inputs[3] = EXMEM1.outputs[3];
		MEMWB1.inputs[4] = EXMEM1.outputs[4];
		MEMWB1.inputs[5] = memUnit1.LMD;
		
		if (superscalar == 2) {
			i2 = (DecodedInstruction) EXMEM2.outputs[0];
			if (!outOfOrder) {
				memUnit2.readWrite(memory, i2.opcode, (int) EXMEM2.outputs[4], i2.rtValue);
			}
			else if (i2.opcode == OpCode.LW){
				memUnit2.read(memory, (int) EXMEM2.outputs[4]);
			}
			MEMWB2.inputs[0] = EXMEM2.outputs[0];
			MEMWB2.inputs[1] = EXMEM2.outputs[1];
			MEMWB2.inputs[2] = EXMEM2.outputs[2];
			MEMWB2.inputs[3] = EXMEM2.outputs[3];
			MEMWB2.inputs[4] = EXMEM2.outputs[4];
			MEMWB2.inputs[5] = memUnit2.LMD;
		}
		
		//ROB
		i1 = (DecodedInstruction) MEMWB1.outputs[0];
		System.out.println("pipeline 1 finished " + i1.opcode);
		if (superscalar == 2) {
			i2 = (DecodedInstruction) MEMWB2.outputs[0];
			System.out.println("pipeline 2 finished " + i2.opcode);
		}
		
		if (!ROB.entries.isEmpty()) {
			//finished instructions get their ROB entry cleared
			if (i1.opcode != OpCode.NOP) {
				
				if (i1.opcode == OpCode.ADD || i1.opcode == OpCode.SLT || i1.opcode == OpCode.SUB || i1.opcode == OpCode.MUL) {
					ROB.entries.get(i1.ROBaddress).ready = true;
					ROB.entries.get(i1.ROBaddress).value = (int) MEMWB1.outputs[4]; //ALUresult
				}
				else if (i1.opcode == OpCode.ADDI&& ROB.entries.containsKey(i1.ROBaddress)) {
					ROB.entries.get(i1.ROBaddress).ready = true;
					ROB.entries.get(i1.ROBaddress).value = (int) MEMWB1.outputs[4]; //ALUresult
				}
				else if (i1.opcode == OpCode.LW && ROB.entries.containsKey(i1.ROBaddress)) {
					ROB.entries.get(i1.ROBaddress).ready = true;
					ROB.entries.get(i1.ROBaddress).value = (int) MEMWB1.outputs[5]; //LMD
				}
			}
			
			if (superscalar == 2) {				
			
				if (i2.opcode != OpCode.NOP) {
					if (i2.opcode == OpCode.ADD || i2.opcode == OpCode.SLT || i2.opcode == OpCode.SUB || i2.opcode == OpCode.MUL) {
						ROB.entries.get(i2.ROBaddress).ready = true;
						ROB.entries.get(i2.ROBaddress).value = (int) MEMWB2.outputs[4]; //ALUresult
					}
					else if (i2.opcode == OpCode.ADDI && ROB.entries.containsKey(i2.ROBaddress)) {
						ROB.entries.get(i2.ROBaddress).ready = true;
						ROB.entries.get(i2.ROBaddress).value = (int) MEMWB2.outputs[4]; //ALUresult
						
					}
					else if (i2.opcode == OpCode.LW && ROB.entries.containsKey(i2.ROBaddress)) {
						ROB.entries.get(i2.ROBaddress).ready = true;
						ROB.entries.get(i2.ROBaddress).value = (int) MEMWB2.outputs[5]; //LMD
					}
				}
			}
		}
		
		if (ROB.storeBuffer.containsKey(i1.ROBaddress) && i1.opcode == OpCode.SW) {
			ROB.storeBuffer.get(i1.ROBaddress).memAddress = (int) MEMWB1.outputs[4];
			ROB.storeBuffer.get(i1.ROBaddress).valid = true;
		}
		
		if (ROB.storeBuffer.containsKey(i2.ROBaddress) && i2.opcode == OpCode.SW) {
			ROB.storeBuffer.get(i2.ROBaddress).memAddress = (int) MEMWB2.outputs[4];
			ROB.storeBuffer.get(i2.ROBaddress).valid = true;
		}
		
		System.out.println("ROB entries: ");
		Iterator<ROBEntry> it = ROB.entries.values().iterator();
		while (it.hasNext()) {
			ROBEntry e = it.next();
			System.out.print("reg: " + e.destinationRegister);
			System.out.println(", ready: " + e.ready);
		}
		
		if (outOfOrder) {
			ROB.write(memory);
		}
		
		ROB.commit(registerFile, mappingTable);	
	}
	
	public Memory getMemory() {
		return memory;
	}

	public void setMemory(Memory memory) {
		this.memory = memory;
	}

	public RegisterFile getRegisterFile() {
		return registerFile;
	}

	public void setRegisterFile(RegisterFile registerFile) {
		this.registerFile = registerFile;
	}
}
