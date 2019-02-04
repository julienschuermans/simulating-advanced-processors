package superscalar_processor.model;

import java.util.Observable;


public class Clock extends Observable implements Runnable{

	public Clock(int ms, int ns) {
		 this.ms = ms;
		 this.ns = ns;
		 value = false;
		 pauzed = true;
	}
	
	private int ms;
	private int ns;
	public volatile boolean value;
	public boolean pauzed;
	
	public void run() {
		while(!pauzed) {
			tic();
		}
	}

	public void tic() {
		value = true;
		setChanged();
		notifyObservers();
		try {
			Thread.sleep(ms/2, ns/2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		value = false;
		try {
			Thread.sleep(ms/2, ns/2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
