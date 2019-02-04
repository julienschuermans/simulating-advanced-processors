package superscalar_processor.model;

import java.util.HashMap;
import java.util.Map;

import superscalar_processor.signals.MappingTableEntry;

public class RegisterMappingTable {
	
	public RegisterMappingTable() {
		for (int i = 0; i< 32; i++) {
			entries.put(i, new MappingTableEntry());
		}
	}

	Map<Integer, MappingTableEntry> entries = new HashMap<Integer, MappingTableEntry>();
}
