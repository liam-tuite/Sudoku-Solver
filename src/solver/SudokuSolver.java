package solver;

import java.util.ArrayList;
import java.util.Iterator;

import gui.SudokuFrame;
import util.Cell;

public class SudokuSolver{
	
	private Cell[] cells;
	private static SudokuFrame frame;
	
	private static SudokuSolver instance;
	
	// In debugging mode, the program loads data from the DEBUG_CELLS array and solves for these values
	private final static boolean DEBUGGING_MODE = false;
	private final static char[] DEBUG_CELLS = {'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', '3', 'X', '8', '5', 'X', 'X', '1', 'X', '2', 'X', 'X', 'X', 'X', 'X', 'X', 'X', '5', 'X', '7', 'X', 'X', 'X', 'X', 'X', '4', 'X', 'X', 'X', '1', 'X', 'X', 'X', '9', 'X', 'X', 'X', 'X', 'X', 'X', 'X', '5', 'X', 'X', 'X', 'X', 'X', 'X', '7', '3', 'X', 'X', '2', 'X', '1', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', '4', 'X', 'X', 'X', '9'};

	public static void main(String[] args){
		
		frame = new SudokuFrame(instance);
		frame.setVisible(true);
		
		instance = new SudokuSolver();
		instance.cells = frame.getCells();
		
		if(DEBUGGING_MODE){
			
			for(int i = 0; i < instance.cells.length; i++){
				instance.cells[i].setValue(DEBUG_CELLS[i]);
			}
			instance.solvePuzzle();
		}		
	}
	
	/**
	 * Attempts to solve the whole puzzle.
	 */
	public void solvePuzzle(){
		
		// prepare the information on the grid
		initCellIndices();
		initCellConnections();
		ArrayList<Cell> unsolvedCells = getUnsolvedCells();

		boolean finished = false;
		while(!finished){
			
			finished = true;
			
			// While there are still Cells to solve
			for(Iterator<Cell> iterator = unsolvedCells.iterator(); iterator.hasNext();){
				
				Cell cell = iterator.next();
				cell.eliminateValues(); // attempt to get definite solution for this Cell using elimination
				
				if(cell.getValue() != 'X'){ // if solution was found
					
					iterator.remove(); // remove this Cell from unsolvedCells
					finished = unsolvedCells.size() == 0; // if there are unsolved cells still, we will iterate the loop least one more time
				}
			}
		}
		
		// If there are still unsolved cells, use brute force
		if(!unsolvedCells.isEmpty()){ // if there are still cells to solve after simple elimination
			solveBruteForce(unsolvedCells);
		}
	}
	
	/**
	 * Returns an ArrayList<Cell> that contains references to all cells that are connected to the argument Cell (i.e.
	 * Cells in the same row, the same column and the same square).
	 * 
	 * @param cell The Cell for which we are finding the connections.
	 * @return An ArrayList<Cell> containing references to all Cells connected to this one.
	 */
	private ArrayList<Cell> getConnectedCells(Cell cell){
		
		ArrayList<Cell> ret = new ArrayList<>();
		
		int n = cell.getIndex();

		int r = (n / 9) * 9; // the index of the beginning of this cell's row
		for(int i = r; i < r + 9; i++){
			if(!ret.contains(cells[i])){
				ret.add(cells[i]);
			}
		}
		
		int c = n % 9; // the index of the beginning of this cell's column
		for(int i = c; i < 81; i += 9){
			if(!ret.contains(cells[i])){
				ret.add(cells[i]);
			}
		}
		
		int dx = c - (c / 3) * 3; // the difference cell's index and the beginning of its box along the x axis
		int dy = r - (r / 27) * 27; // the difference cell's index and the beginning of its box along the y axis
		int s = n - dx - dy; // the index of the beginning of this cell's box
		
		int[] boxIndices = {s, s + 1, s + 2, s + 9, s + 10, s + 11, s + 18, s + 19, s + 20};
		for(int i : boxIndices){
			if(!ret.contains(cells[i])){
				ret.add(cells[i]);
			}
		}
		
		return ret;
	}
	
	/**
	 * Gets an ArrayList<Cell> containing references to all of the Cells in the grid for which the Cell value is unknown.
	 * 
	 * @return An ArrayList<Cell> containing references to all unknown Cells.
	 */
	private ArrayList<Cell> getUnsolvedCells(){

		ArrayList<Cell> unsolvedCells = new ArrayList<>();
		
		for(Cell cell : cells)			
			if(cell.getValue() == 'X' && !unsolvedCells.contains(cell))
				unsolvedCells.add(cell);
		
		return unsolvedCells;
	}
	
	/**
	 * Sets the initial value of the connectedCells member for all Cell objects.
	 */
	private void initCellConnections(){
		
		for(Cell cell : cells)
			cell.setConnectedCells(getConnectedCells(cell));
	}
	
	/**
	 * Initialise each Cell's index value.
	 */
	private void initCellIndices(){
		
		for(int i = 0; i < cells.length; i++)
			cells[i].setIndex(i);
	}
	
	/**
	 * Solves the puzzle with a brute force algorithm, which works as follows:
	 * 1.	The first unsolved Cell takes its first possible value. This Cell is then marked as the 'previous uncertain Cell'
	 * 2.	For the remainder of the unsolved Cells:
	 * 			-	Attach the previous uncertain Cell to the Cell.
	 * 			-	The Cell takes the first possible value.
	 * 			-	If the Cell had no possible value to take:
	 * 					-	Reset the possible values for this Cell.
	 * 					-	Eliminate the current value of the previous uncertain Cell from its possibilities.
	 * 					-	Set the previous uncertain's previous uncertain as the new previous uncertain.
	 * 					-	Continue the loop from the current Cell's previous uncertain.
	 * 			-	Otherwise, set the previous uncertain to be this Cell and continue.
	 * 
	 * @param unsolvedCells
	 */
	private void solveBruteForce(ArrayList<Cell> unsolvedCells){

		Cell first = unsolvedCells.get(0);
		first.takeFirstValue();
		
		Cell previousUncertain = first;
		
		for(int i = 1; i < unsolvedCells.size(); i++){
			
			Cell cell = unsolvedCells.get(i);
			
			cell.setPreviousUncertain(previousUncertain);
			cell.takeFirstValue();
			
			if(cell.getValue() == 'X'){
				cell.resetPossibilities();
				
				previousUncertain.eliminateValue(previousUncertain.getValue());
				previousUncertain.setValue('X');
				
				previousUncertain = previousUncertain.getPreviousUncertain();
				i -= 2;
				continue;
			}
			
			previousUncertain = cell;
		}
	}
}