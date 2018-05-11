package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Arrays;

import javax.swing.JPanel;
import javax.swing.SwingConstants;

import util.Cell;

@SuppressWarnings("serial")
public class CellGridPanel extends JPanel{
	
	protected Cell[] cells;
	
	/**
	 * The indices of all the grid cells to have a grey background (to highlight different squares).
	 */
	private final Integer[] GREY_CELL_INDICES = {3, 4, 5, 12, 13, 14, 21, 22, 23, 27, 28, 29, 33, 34, 35, 36, 37, 38, 42, 43,
			44, 45, 46, 47, 51, 52, 53, 57, 58, 59, 66, 67, 68, 75, 76, 77};

	/**
	 * Create the panel.
	 */
	public CellGridPanel(){
		
		setLayout(new GridLayout(9, 9, 0, 0));
		
		// Construct all 81 Cells
		cells = new Cell[81];
		for(int i = 0; i < cells.length; i++){
			
			cells[i] = new Cell();
			add(cells[i]);
			cells[i].setHorizontalAlignment(SwingConstants.CENTER);
			cells[i].setFont(new Font("Arial", Font.BOLD, 20));
			cells[i].setColumns(5);
			
			if(Arrays.asList(GREY_CELL_INDICES).contains(i))
				cells[i].setBackground(Color.LIGHT_GRAY);
		}
	}
}