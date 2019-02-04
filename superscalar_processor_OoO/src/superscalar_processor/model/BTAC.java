package superscalar_processor.model;

import java.util.HashMap;
import java.util.Map;

import superscalar_processor.signals.BTACEntry;

public class BTAC {

	public boolean getHistory(int instrAddr) throws NullPointerException {
		return entries.get(instrAddr).history;
	}

	public int getBranchTarget(int instrAddr) {
		return entries.get(instrAddr).branchTarget;
	}
	
	public void updateHistory(int instrAddr, boolean branchTaken) {
		entries.get(instrAddr).history = branchTaken;
	}
	
	Map<Integer, BTACEntry> entries = new HashMap<Integer, BTACEntry>();
}
