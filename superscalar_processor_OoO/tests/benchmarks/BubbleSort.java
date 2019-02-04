package benchmarks;

import superscalar_processor.main.TestBench;

public class BubbleSort {

	public static void main(String[] args) {
		
		 //default
		boolean branchPrediction = true;
        branchPrediction = Boolean.parseBoolean(args[0]);
		
        int superscalar = 2;
		boolean outOfOrder = true;
		boolean registerRenaming = false;
		
		Boolean[] flags = {registerRenaming, branchPrediction, outOfOrder};
		String filename = "";
		
		if (outOfOrder) {
			if(!branchPrediction) {
				filename = "resources/programs/BubbleSort_ooo.txt";
			}
			else {
				filename = "resources/programs/BubbleSort_ooo_branchprediction.txt";
			}
		}
		else if (superscalar == 2) {
			if (branchPrediction) {
				 filename = "resources/programs/BubbleSort_pipelined_superscalar_branchprediction.txt";
			}
			else {
				filename = "resources/programs/BubbleSort_pipelined_superscalar.txt";
			}
		}
		else {
			if (branchPrediction) {
				filename = "resources/programs/BubbleSort_pipelined_branchPrediction.txt";
			}
			else {
				filename = "resources/programs/BubbleSort_pipelined.txt";
			}
		}
		
		TestBench tb = new TestBench(filename, superscalar, flags, true);

		int minimum = 1;
		int maximum = 10000;
		
		
		// load test vectors into memory
		for (int i = 0; i<10;i++) {
			tb.myProcessor.getMemory().write(268500992+4*i, minimum + (int)(Math.random() * maximum) );//.data base address
		}

		int[] result = tb.run();
		tb.printResults(result, branchPrediction);
	}

}