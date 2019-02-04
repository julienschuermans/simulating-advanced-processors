package superscalar_processor.signals;

public class StoreBufferEntry {
	
	public StoreBufferEntry(int rtValue) {
		this.writeValue = rtValue;
	}
	
	public int id;
	
	public int memAddress = 0;
	public int writeValue = 0;
	public boolean valid = false;

	public boolean speculative = false;
}
