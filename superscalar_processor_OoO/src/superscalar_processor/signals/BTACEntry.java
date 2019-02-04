package superscalar_processor.signals;

public class BTACEntry { 
	  public int branchTarget;
	  public boolean history; 
	  
	  public BTACEntry(int branchAddr, boolean history) { 
	    this.branchTarget = branchAddr; 
	    this.history = history; 
	  } 
	} 
