package superscalar_processor.main;

import java.awt.BorderLayout;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JScrollPane;

import superscalar_processor.model.Processor;
import superscalar_processor.program.Instruction;

public class Visuals {

	public Visuals() {
		registerFrame = new JFrame("Registers");
		registerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		registerData = new String[32][2];
		
		nextButton = new JButton("step");
		finishButton = new JButton("execute all");
		
		for (int i=0; i<32; i++) {
			registerData[i][0] = "";
			registerData[i][1] = "";
		}
		
		String[] columns = new String[] {
	            "Name", "Value"
		};
		
		PCData = new String[1][2];
		PCData[0][0] = "";
		PCData[0][1] = "";
		
		registerTable = new JTable(registerData, columns);
		PCtable = new JTable(PCData, columns);
		
		registerFrame.getContentPane().add(registerTable, BorderLayout.CENTER);
		registerFrame.getContentPane().add(PCtable, BorderLayout.SOUTH);
		
		
		memoryFrame = new JFrame("Data Memory");
		memoryFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		memoryData = new String[100][2];
		
		for (int i=0; i<100; i++) {
			memoryData[i][0] = "";
			memoryData[i][1] = "";
		}
		memoryTable = new JTable(memoryData, columns);
		JScrollPane memoryScr = new JScrollPane(memoryTable);
		memoryFrame.getContentPane().add(memoryScr, BorderLayout.CENTER);
		
		
		
		instructionFrame = new JFrame("Instruction Memory");
		instructionFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		memoryInstructions = new String[100][2];
		
		for (int i=0; i<100; i++) {
			memoryInstructions[i][0] = "";
			memoryInstructions[i][1] = "";
		}
		instructionTable = new JTable(memoryInstructions, columns);
		JScrollPane instructionScr = new JScrollPane(instructionTable);
		instructionFrame.getContentPane().add(instructionScr, BorderLayout.CENTER);
		
		buttonFrame = new JFrame("Controls");
		buttonFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		buttonFrame.getContentPane().add(nextButton, BorderLayout.WEST);
		buttonFrame.getContentPane().add(finishButton, BorderLayout.EAST);
		
		registerFrame.setVisible(true);
		registerFrame.setBounds(20, 20, 200, 600);
		
		memoryFrame.setVisible(true);
		memoryFrame.setBounds(220, 20, 200, 600);
		
		instructionFrame.setVisible(true);
		instructionFrame.setBounds(420, 20, 400, 600);
		
		buttonFrame.setVisible(true);
		buttonFrame.setBounds(820, 20, 200, 200);
		
	}
	
	public JFrame registerFrame;
	public JFrame memoryFrame;
	public JFrame instructionFrame;
	public JFrame buttonFrame;
	
	public JTable PCtable;
	public JTable registerTable;
	public JTable memoryTable;
	public JTable instructionTable;
	
	private String[][] registerData;
	private String[][] PCData;
	private String[][] memoryData;
	private String[][] memoryInstructions;
	public JButton nextButton;
	public JButton finishButton;
	
	public void update(Processor p) {
		for (int i = 0; i< 32; i++) {
			registerTable.getModel().setValueAt(p.getRegisterFile().registers.get(i).getName() , i, 0);
			registerTable.getModel().setValueAt(String.valueOf(p.getRegisterFile().registers.get(i).getValue()) , i, 1);
		}
		PCtable.getModel().setValueAt(p.getRegisterFile().getProgramCounter().getName(), 0, 0);
		PCtable.getModel().setValueAt(String.valueOf(p.getRegisterFile().getProgramCounter().getValue()) , 0, 1);
		registerFrame.revalidate();
		registerFrame.repaint();
		
		Iterator<Entry<Integer, Integer>> it = p.getMemory().data.entrySet().iterator();
	    int i = 0;
		while (it.hasNext()) {
	        Map.Entry<Integer, Integer> pair = (Map.Entry<Integer, Integer>)it.next();
	        memoryTable.getModel().setValueAt(String.valueOf(pair.getKey()), i, 0);
	        memoryTable.getModel().setValueAt(String.valueOf(pair.getValue()), i, 1);
	        i += 1;
		}
		memoryFrame.revalidate();
		memoryFrame.repaint();
		
		
		Iterator<Entry<Integer, Instruction>> it2 = p.getMemory().instructions.entrySet().iterator();
	    int j = 0;
		while (it2.hasNext()) {
	        Map.Entry<Integer, Instruction> pair = (Map.Entry<Integer, Instruction>)it2.next();
	        instructionTable.getModel().setValueAt(String.valueOf(pair.getKey()), j, 0);
	        instructionTable.getModel().setValueAt(String.valueOf(pair.getValue()), j, 1);
	        j += 1;
		}
		instructionFrame.revalidate();
		instructionFrame.repaint();
	}
}
