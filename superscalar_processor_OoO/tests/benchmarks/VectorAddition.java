package benchmarks;
import superscalar_processor.main.TestBench;

public class VectorAddition {

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
				filename = "resources/programs/vectorAddition_ooo.txt";
			}
			else {
				filename = "resources/programs/vectorAddition_ooo_branchprediction.txt";
			}
		}
		else if (superscalar == 2) {
			if (branchPrediction) {
				 filename = "resources/programs/vectorAddition_pipelined_superscalar_branchprediction.txt";
			}
			else {
				filename = "resources/programs/vectorAddition_pipelined_superscalar.txt";
			}
		}
		else {
			if (branchPrediction) {
				filename = "resources/programs/vectorAddition_pipelined_branchPrediction.txt";
			}
			else {
				filename = "resources/programs/vectorAddition_pipelined.txt";
			}
		}
		
		TestBench tb = new TestBench(filename, superscalar, flags, true);
		
		// load test vectors into memory
		tb.myProcessor.getMemory().write(268500992, 0);//.data base address
		tb.myProcessor.getMemory().write(268500996, 0);
		tb.myProcessor.getMemory().write(268501000, 0);
		tb.myProcessor.getMemory().write(268501004, 0);
		tb.myProcessor.getMemory().write(268501008, 0);
		tb.myProcessor.getMemory().write(268501012, 0);
		tb.myProcessor.getMemory().write(268501016, 0);
		tb.myProcessor.getMemory().write(268501020, 0);
		tb.myProcessor.getMemory().write(268501024, 0);
		tb.myProcessor.getMemory().write(268501028, 0);
		
		tb.myProcessor.getMemory().write(268501032, 0);

		
		tb.myProcessor.getMemory().write(268501992, 1);
		tb.myProcessor.getMemory().write(268501996, 0);
		tb.myProcessor.getMemory().write(268502000, 1);
		tb.myProcessor.getMemory().write(268502004, 0);
		tb.myProcessor.getMemory().write(268502008, 1);
		tb.myProcessor.getMemory().write(268502012, 0);
		tb.myProcessor.getMemory().write(268502016, 1);
		tb.myProcessor.getMemory().write(268502020, 0);
		tb.myProcessor.getMemory().write(268502024, 1);
		tb.myProcessor.getMemory().write(268502028, 0);
		
		tb.myProcessor.getMemory().write(268502032, 0);

		
		tb.myProcessor.getMemory().write(268502992, 1);
		tb.myProcessor.getMemory().write(268502996, 2);
		tb.myProcessor.getMemory().write(268503000, 3);
		tb.myProcessor.getMemory().write(268503004, 4);
		tb.myProcessor.getMemory().write(268503008, 5);
		tb.myProcessor.getMemory().write(268503012, 6);
		tb.myProcessor.getMemory().write(268503016, 7);
		tb.myProcessor.getMemory().write(268503020, 8);
		tb.myProcessor.getMemory().write(268503024, 9);
		tb.myProcessor.getMemory().write(268503028, 10);
		
		tb.myProcessor.getMemory().write(268503032, 0);

		
		int[] result = tb.run();
		tb.printResults(result, branchPrediction);
	}

}
