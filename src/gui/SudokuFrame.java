package gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import solver.SudokuSolver;
import util.Cell;
import util.ValueLengthException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class SudokuFrame extends JFrame {
	
	private JButton solve_button, printData_button;
	private JPanel contentPane;
	private CellGridPanel cellGridPanel;

	private Cell[] cells;
	private SudokuSolver solver;
	private JPanel buttonPanel;
	
	/**
	 * Create the frame.
	 * 
	 * @param solver The SudokuSolver object that created this frame.
	 */
	public SudokuFrame(SudokuSolver solver){
		setResizable(false);
	
		this.solver = solver;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 300, 330);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		
		cellGridPanel = new CellGridPanel();
		this.cells = cellGridPanel.cells;
		contentPane.add(cellGridPanel);
		
		buttonPanel = new JPanel();
		contentPane.add(buttonPanel);
		
		ButtonClickListener buttonClickListener = new ButtonClickListener();
		
		solve_button = new JButton("Solve");
		buttonPanel.add(solve_button);
		solve_button.setAlignmentX(Component.RIGHT_ALIGNMENT);
		
		printData_button = new JButton("Print Data");
		buttonPanel.add(printData_button);
		printData_button.setAlignmentX(Component.RIGHT_ALIGNMENT);
		printData_button.addActionListener(buttonClickListener);
		solve_button.addActionListener(buttonClickListener);
	}
	
	public Cell[] getCells(){
		return cells;
	}
	
	public void setCells(Cell[] cells){
		this.cells = cells;
	}
	
	/**
	 * Print the value of each Cell, in a format that can be pasted into code in order to construct a debugging scenario
	 * easily.
	 */
	private void printData(){
		// Output the data
		String str = "";
		
		for(int i = 0; i < cells.length; i++)
			str += "'" + cells[i].getValue() + "', ";
		
		str = str.substring(0, str.length() - 2); // remove last comma and space
		System.out.println(str);
	}
	
	private class ButtonClickListener implements ActionListener{
		
		@Override
		public void actionPerformed(ActionEvent e){

			// Store the entered values into the appropriate Cell objects
			try{
				for(Cell cell : cells)
					cell.initValue();
			}catch(ValueLengthException ex){
				ex.printStackTrace();
			}
				
			if(e.getSource().equals(solve_button))
				solver.solvePuzzle(); // Solve the puzzle
			else // The "Print Data" button was clicked
				printData();
		}
	}
}