package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Arrays;

import javax.swing.JPanel;
import javax.swing.SwingConstants;

import solver.SudokuSolver;
import util.Cell;

@SuppressWarnings("serial")
public class CellGridPanel extends JPanel{
	
	protected Cell[] cells;
	
	/**
	 * The indices of all the grid cells to have a grey background (to highlight different squares).
	 */
	private final Integer[] GREY_CELL_INDICES_SUPER = {4, 5, 6, 7, 12, 13, 14, 15, 20, 21, 22, 23, 28, 29, 30, 31, 36, 37, 38,
			39, 44, 45, 46, 47, 52, 53, 54, 55, 60, 61, 62, 63, 64, 65, 66, 67, 72, 73, 74, 75, 80, 81, 82, 83, 88, 89, 90,
			91, 96, 97, 98, 99, 104, 105, 106, 107, 112, 113, 114, 115, 120, 121, 122, 123, 132, 133, 134, 135, 140, 141,
			142, 143, 148, 149, 150, 151, 156, 157, 158, 159, 164, 165, 166, 167, 172, 173, 174, 175, 180, 181, 182, 183,
			188, 189, 190, 191, 192, 193, 194, 195, 200, 201, 202, 203, 208, 209, 210, 211, 216, 217, 218, 219, 224, 225,
			226, 227, 232, 233, 234, 235, 240, 241, 242, 243, 248, 249, 250, 251};
	private final Integer[] GREY_CELL_INDICES_STANDARD = {3, 4, 5, 12, 13, 14, 21, 22, 23, 27, 28, 29, 33, 34, 35, 36, 37, 38,
			42, 43, 44, 45, 46, 47, 51, 52, 53, 57, 58, 59, 66, 67, 68, 75, 76, 77};

	/**
	 * Create the panel.
	 */
	public CellGridPanel(){
		
		Integer[] greyCellIndices;
		
		if(SudokuSolver.SUPER_SUDOKU){
			setLayout(new GridLayout(16, 16, 0, 0));
			cells = new Cell[256];
			greyCellIndices = GREY_CELL_INDICES_SUPER;
		}
		else{
			setLayout(new GridLayout(9, 9, 0, 0));
			cells = new Cell[81];
			greyCellIndices = GREY_CELL_INDICES_STANDARD;
		}
		
		// Construct all Cells
		for(int i = 0; i < cells.length; i++){
			
			cells[i] = new Cell();
			add(cells[i]);
			cells[i].setHorizontalAlignment(SwingConstants.CENTER);
			cells[i].setFont(new Font("Arial", Font.BOLD, 20));
			cells[i].setColumns(5);
			
			if(Arrays.asList(greyCellIndices).contains(i))
				cells[i].setBackground(Color.LIGHT_GRAY);
		}
	}
}