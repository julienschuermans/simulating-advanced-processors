package superscalar_processor.signals;

public class DecodedInstruction {

	public DecodedInstruction(OpCode opcode, int rs, int rt, int rd, int immediate, int target, ALUControlSignal aluControl, MuxControlSignal aluSrc, int ROBaddress) {
		this.opcode = opcode;
		this.rs = rs;
		this.rt = rt;
		this.rd = rd;
		this.immediate = immediate;
		this.target = target;
		this.aluControl = aluControl;
		this.aluSrc = aluSrc;
		this.ROBaddress = ROBaddress;
	}
	
	public final OpCode opcode;
	public final int rs; // register addresses
	public final int rt;
	public final int rd; 
	public final int immediate; // integer
	public final int target; //RAM address
	
	public int rsValue = 0;
	public int rtValue = 0;
	
	public boolean rsValueValid = false;
	public boolean rtValueValid = false;
	
	public int ROBaddress;
	
	
	public final ALUControlSignal aluControl;
	public MuxControlSignal aluSrc;
	

}
