package superscalar_processor.model;

import java.util.Observable;
import java.util.Observer;

public abstract class InterStageBuffer implements Observer {

	public InterStageBuffer(int size) {
		this.size = size;
		this.inputs = new Object[size];
		this.outputs = new Object[size];
	}
	
	private int size;
	public Object[] inputs;
	public Object[] outputs;

	
	@Override
	public void update(Observable o, Object arg) {
		for (int i=0; i<size; i++) {
			outputs[i] = inputs[i];
		}
	}


	public abstract void clear();
}
