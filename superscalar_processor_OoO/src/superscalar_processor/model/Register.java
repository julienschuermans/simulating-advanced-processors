package superscalar_processor.model;

public class Register {

	public Register(int number, String name, int value) {
		this.number = number;
		this.name = name;
		this.setValue(value);
	}

	
	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}


	public int getNumber() {
		return number;
	}


	public String getName() {
		return name;
	}


	private final int number;
	private final String name;
	private int value;
}
