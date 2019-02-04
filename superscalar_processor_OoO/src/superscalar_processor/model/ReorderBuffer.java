package superscalar_processor.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import superscalar_processor.signals.ROBEntry;
import superscalar_processor.signals.StoreBufferEntry;


public class ReorderBuffer {
	
	private WritebackUnit writebackUnit;

	public ReorderBuffer(WritebackUnit wbUnit) {
		headPointer = 0;
		tailPointer = 0;
		this.writebackUnit = wbUnit;
	}
	
	public Map<Integer, ROBEntry> entries = new HashMap<Integer, ROBEntry>();
	
	private int headPointer;
	public int tailPointer;
	
	public void confirmSpeculation() {
		Iterator<ROBEntry> it = entries.values().iterator();
		while (it.hasNext()) {
			ROBEntry entry = it.next();
			if (entry.speculative) {
				entry.speculationConfirmed = true;
			}
		}
		
		Iterator<StoreBufferEntry> it2 = storeBuffer.values().iterator();
		while (it2.hasNext()) {
			StoreBufferEntry entry = it2.next();
			if (entry.speculative) {
				entry.speculative = false;
			}
		}
		
	}
	
	public void rollBack(RegisterMappingTable mapTable) {
		Iterator<ROBEntry> it = entries.values().iterator();
		while (it.hasNext()) {
			ROBEntry entry = it.next();
			if (entry.speculative) {
				entry.speculationConfirmed = false;
				System.out.println("remove from ROB: " + entry.destinationRegister);
				it.remove();
				mapTable.entries.get(entry.destinationRegister).registerValid = true;
			}
		}
		
		Iterator<StoreBufferEntry> it2 = storeBuffer.values().iterator();
		while (it2.hasNext()) {
			StoreBufferEntry entry = it2.next();
			if (entry.speculative) {
				System.out.println("remove from storeBuf:" + entry.memAddress);
				it2.remove();
			}
			
			else if (!entry.valid) {
				entry.valid = true;
			}
		}
		
	}
	
	public int addEntry(ROBEntry entry) {
		entries.put(headPointer, entry);
		headPointer += 1;
		return headPointer - 1;
	}
	
	private int storeBufferPointer = 0;
	
	public int addStore(StoreBufferEntry e) {
		storeBuffer.put(storeBufferPointer, e);
		storeBufferPointer += 1;
		return storeBufferPointer - 1;
		
	}
	
	public void commit(RegisterFile registerFile, RegisterMappingTable mappingTable) {
		
		Iterator<ROBEntry> it = entries.values().iterator();
		while (it.hasNext()) {
				ROBEntry entry = it.next();
			if ( entry.ready &&(! entry.speculative || entry.speculationConfirmed)) {
				writebackUnit.ROBWriteBack(registerFile, entry.destinationRegister, entry.value);
				mappingTable.entries.get(entry.destinationRegister).registerValid = true;
				System.out.println("ROB commits, unblocks register: " + entry.destinationRegister);
				it.remove();
				tailPointer += 1;	
			}
		}
	}
	
	public Map<Integer, StoreBufferEntry> storeBuffer = new HashMap<Integer, StoreBufferEntry>();
	
	public void write(Memory memory) {
		
		Iterator<StoreBufferEntry> it = storeBuffer.values().iterator();
		while (it.hasNext()) {
			StoreBufferEntry e = it.next();
			System.out.println("storebuf: " + e.memAddress + ", " + e.writeValue + ", valid: " +e.valid);
			if (e.valid && !e.speculative) {
				System.out.println("write ROB mem: " + e.memAddress + ": " + e.writeValue);
				memory.write(e.memAddress, e.writeValue);
				it.remove();	
			}
		}
	}
}
