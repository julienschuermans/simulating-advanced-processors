package superscalar_processor.main;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Observable;
import java.util.Observer;

import superscalar_processor.model.Clock;
import superscalar_processor.model.Processor;
import superscalar_processor.program.Program;
import superscalar_processor.program.instructions.Stop;
import superscalar_processor.signals.DecodedInstruction;

public class TestBench implements Observer {

	private boolean enableGUI;
	
	public TestBench(String benchMarkPath,int superscalar, Boolean[] flags, Boolean enableGUI) {
		if (enableGUI) {
			visuals = new Visuals();
			this.enableGUI = true;
		}
		myParser = new ProgramParser();
		
		boolean registerRenaming = flags[0];
		boolean branchPrediction = flags[1];
		boolean outOfOrder = flags[2];
		
		myProcessor = new Processor(superscalar, registerRenaming, branchPrediction, outOfOrder);
		clk = new Clock(0, 10000);
		clk.addObserver(this);
		
		try {
			programString = readFile(benchMarkPath, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		myProgram = myParser.parse(programString);
		myProcessor.load(myProgram);
	}
	
	private int cycleCounter;
	private int instructionCount;
	private int readCount;
	private int writeCount;
	private int beqCount;
	private int jumpCount;
	private int nopCount;
	
	public int[] run() {
	
		System.out.println("Start execution.");
		instructionCount = 0;
		nopCount = 0;
		cycleCounter = 0;
		readCount = 0;
		writeCount = 0;
		beqCount = 0;
		jumpCount = 0;
		Thread t = new Thread(clk);
		
		while (! hasFinished(myProcessor)) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if (enableGUI) {
				if (clk.pauzed) {
					while((visuals.nextButton.getModel().isPressed() && visuals.nextButton.getModel().isArmed())) {
					    clk.tic();
					    try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					visuals.nextButton.getModel().setPressed(false);
					visuals.nextButton.getModel().setArmed(false);
					
				    if (visuals.finishButton.getModel().isPressed() && visuals.finishButton.getModel().isArmed()) {
				    	clk.pauzed = false;
				    	t.start();
				    }
				}
			} 
			else if (clk.pauzed){
				clk.pauzed = false;
		    	t.start();
			}
		}
		
		clk.pauzed = true;
		System.out.println("\nDone.");
		
		int[] results;
		
		if (myProcessor.branchPrediction) {
			results =  new int[] {cycleCounter, instructionCount, writeCount, readCount, beqCount, jumpCount, nopCount, myProcessor.fetchUnit.branchPredictor.failures,myProcessor.fetchUnit.branchPredictor.successes};	
		}
		else {
			results = new int[] {cycleCounter, instructionCount, writeCount, readCount, beqCount, jumpCount, nopCount};
		}
		return results;
	}
	
	private boolean hasFinished(Processor p) {
		if (p.superscalar != 2) {
			return p.getMemory().instructions.get(p.getRegisterFile().getProgramCounter().getValue()) instanceof Stop;
		}
		else {
			return (p.getMemory().instructions.get(p.getRegisterFile().getProgramCounter().getValue()) instanceof Stop || p.getMemory().instructions.get(p.getRegisterFile().getProgramCounter().getValue() + 4) instanceof Stop);
		}
	}
	
	public String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
	
	public Visuals visuals;
	public ProgramParser myParser;
	public Processor myProcessor;
	public String programString;
	public Program myProgram;
	public Clock clk;
	
	@Override
	public void update(Observable o, Object arg) {
		if (!hasFinished(myProcessor)) {
			cycleProcessor();
		}
	}
	
	private void cycleProcessor() {	
		System.out.println("##################### CYCLE ####################");
		myProcessor.cycle();
		if (enableGUI) {
			visuals.update(myProcessor);
		}
		cycleCounter += 1;
		updateCounters();
	}

	private void updateCounters() {
		switch (((DecodedInstruction) myProcessor.MEMWB1.outputs[0]).opcode) {
		case NOP:
			nopCount += 1;
			break;
		case ADD:
			instructionCount += 1;
			break;
		case ADDI:
			instructionCount += 1;
			break;
		case BEQ:
			instructionCount += 1;
			beqCount += 1;
			break;
		case J:
			instructionCount += 1;
			jumpCount += 1;
			break;
		case LW:
			instructionCount += 1;
			readCount += 1;
			break;
		case MUL:
			instructionCount += 1;
			break;
		case SLT:
			instructionCount += 1;
			break;
		case STOP:
			break;
		case SUB:
			instructionCount += 1;
			break;
		case SW:
			instructionCount += 1;
			writeCount += 1;
			break;
		default:
			break;
		}
		
		if (myProcessor.superscalar == 2) {
			switch (((DecodedInstruction) myProcessor.MEMWB2.outputs[0]).opcode) {
			case NOP:
				nopCount += 1;
				break;
			case ADD:
				instructionCount += 1;
				break;
			case ADDI:
				instructionCount += 1;
				break;
			case BEQ:
				//instructionCount += 1;
				//beqCount += 1;
				break;
			case J:
				//instructionCount += 1;
				//jumpCount += 1;
				break;
			case LW:
				instructionCount += 1;
				readCount += 1;
				break;
			case MUL:
				instructionCount += 1;
				break;
			case SLT:
				instructionCount += 1;
				break;
			case STOP:
				break;
			case SUB:
				instructionCount += 1;
				break;
			case SW:
				instructionCount += 1;
				writeCount += 1;
				break;
			default:
				break;
			}
		}
	}

	public void printResults(int[] result, Boolean branchPrediction) {
		int totalNbCycles = result[0];
		int totalNbInstructions = result[1];
		int writeCount = result[2];
		int readCount = result[3];
		int beqCount = result[4];
		int jumpCount = result[5];
		int nopCount = result[6];
		float CPI = (float) (totalNbCycles/(1.0*totalNbInstructions));
				
		System.out.println("number of cycles: " + String.valueOf(totalNbCycles));
		System.out.println("number of instructions: " + String.valueOf(totalNbInstructions));
		System.out.println("number of nops: " + String.valueOf(nopCount));
		System.out.println("CPI: " + String.valueOf(Math.round(100.0*CPI)/100.0));
		
		System.out.println("\nwrites: " + String.valueOf(writeCount) + " (" + String.valueOf(100*Math.round(1000.0*writeCount/(1.0*totalNbInstructions))/1000.0) + "%)");
		System.out.println("reads: " + String.valueOf(readCount)+ " (" + String.valueOf(100*Math.round(1000.0*readCount/(1.0*totalNbInstructions))/1000.0) + "%)");
		System.out.println("conditional branches: " + String.valueOf(beqCount)+ " (" + String.valueOf(1000*Math.round(100.0*beqCount/(1.0*totalNbInstructions))/1000.0) + "%)");
		System.out.println("unconditional branches: " + String.valueOf(jumpCount)+ " (" + String.valueOf(1000*Math.round(100.0*jumpCount/(1.0*totalNbInstructions))/1000.0) + "%)");
	
		if (branchPrediction) {
			int successes = result[8];
			int failures = result[7];
			int total = successes + failures;
			System.out.println("\nsuccesful branch predictions: " + String.valueOf(100*Math.round(1000.0*successes/(1.0*total))/1000.0) + "% of " + total);
		}
		
	}
}
