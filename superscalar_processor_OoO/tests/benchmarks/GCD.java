package benchmarks;

import superscalar_processor.main.TestBench;

public class GCD {

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
			if (!branchPrediction) {
				filename = "resources/programs/GCD_ooo.txt";
			}
			else {
				filename = "resources/programs/GCD_ooo_branchprediction.txt";
			}
		}
		else if (superscalar == 2) {
			if (branchPrediction) {
				 filename = "resources/programs/GCD_recursive_pipelined_superscalar_branchprediction.txt";
			}
			else {
				filename = "resources/programs/GCD_recursive_pipelined_superscalar.txt";
			}
		}
		else {
			if (branchPrediction) {
				filename = "resources/programs/GCD_recursive_pipelined_branchPrediction.txt";
			}
			else {
				filename = "resources/programs/GCD_recursive_pipelined.txt";
			}
		}
		
		TestBench tb = new TestBench(filename, superscalar, flags, true);
		
		int minimum = 1;
		int maximum = 1000000;
		int rand1 = minimum + (int)(Math.random() * maximum); 
		int rand2 = minimum + (int)(Math.random() * maximum);
		
		if (rand1 > rand2) {
			int temp = rand1;
			rand1 = rand2;
			rand2 = temp;
		}
		
		tb.myProcessor.getMemory().write(268503992, rand1);
		tb.myProcessor.getMemory().write(268504992, rand2);
		tb.myProcessor.getMemory().write(268505992, 0);
		
		int[] result = tb.run();
		
		tb.printResults(result, branchPrediction);
		
	}

}
