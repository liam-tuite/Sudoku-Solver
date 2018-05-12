package solver;

import java.util.ArrayList;
import java.util.Iterator;

import gui.SudokuFrame;
import util.Cell;

public class SudokuSolver{
	
	private Cell[] cells;
	private static SudokuFrame frame;
	
	private static SudokuSolver instance;
	
	public final static boolean SUPER_SUDOKU = true; // false if grid is 9x9, true if 16x16
	
	public static char[] values;
	public final static char[] VALUES_STANDARD = {'1', '2', '3', '4', '5', '6', '7', '8', '9'};
	public final static char[] VALUES_SUPER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
	
	// In debugging mode, the program loads data from the DEBUG_CELLS array and solves for these values
	private final static boolean DEBUGGING_MODE = true;
	private final static char[] DEBUG_CELLS = {'C', 'X', 'F', 'X', 'X', '5', '8', 'X', 'B', 'X', 'X', 'X', 'X', 'X', 'X', '6', 'X', 'X', '9', 'X', 'X', 'X', 'A', 'X', 'C', '3', 'X', 'X', '2', 'X', '8', 'X', 'X', 'X', '8', 'X', 'X', 'X', 'X', '4', 'D', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'E', 'X', '6', 'X', 'X', 'X', 'X', '0', 'X', '9', 'X', 'X', 'X', 'D', 'X', 'X', 'X', '4', 'X', '7', 'X', 'A', 'X', 'X', 'E', '1', '0', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', '1', 'X', 'X', 'X', 'X', 'X', 'X', 'D', 'X', 'X', 'X', 'X', '9', 'E', 'F', 'D', 'X', 'X', 'X', 'X', '3', 'X', 'X', 'X', '4', '6', 'X', '2', 'X', 'X', '8', '3', 'X', 'F', 'X', 'C', 'X', 'X', 'X', 'X', '9', 'X', 'X', '6', 'X', '4', '9', 'X', '0', 'X', 'X', 'X', '7', 'D', 'X', '2', 'B', 'F', '4', 'X', 'A', 'X', 'X', 'X', 'X', 'X', 'C', 'B', 'X', 'X', 'X', 'X', 'X', '8', 'X', 'X', 'X', 'A', 'X', '1', 'X', 'X', 'X', 'X', 'X', 'F', 'X', '7', 'X', 'X', 'D', 'C', '2', 'X', '3', 'X', 'X', 'X', 'X', 'X', 'X', 'E', 'X', 'X', '5', 'X', 'X', 'X', 'X', '7', '1', 'X', 'X', 'X', '5', '9', '4', 'X', '0', 'B', 'X', 'X', '0', 'X', 'X', '5', 'X', 'X', '9', '6', 'X', '2', 'E', 'X', 'X', 'X', 'X', 'X', 'X', 'X', '3', 'B', '4', 'X', 'X', '8', '7', 'X', 'X', '5', 'E', 'X', '1', 'X', 'X', 'B', 'C', 'X', 'X', 'X', 'E', 'X', 'X', 'F', 'X', '8', 'X', 'X', 'X'};

	public static void main(String[] args){
		
		if(SUPER_SUDOKU)
			values = VALUES_SUPER;
		else
			values = VALUES_STANDARD;
		
		instance = new SudokuSolver();
		frame = new SudokuFrame(instance);
		frame.setVisible(true);
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
		
		int sqrt;
		if(SUPER_SUDOKU){
			sqrt = 4;
		}
		else{
			sqrt = 3;
		}
		int sqrtPow2 = (int) Math.pow(sqrt, 2);
		int sqrtPow3 = (int) Math.pow(sqrt, 3);
		int sqrtPow4 = (int) Math.pow(sqrt, 4);
		
		int r = (n / sqrtPow2) * sqrtPow2; // the index of the beginning of this cell's row
		for(int i = r; i < r + sqrtPow2; i++){
			if(!ret.contains(cells[i])){
				ret.add(cells[i]);
			}
		}
		
		int c = n % sqrtPow2; // the index of the beginning of this cell's column
		for(int i = c; i < sqrtPow4; i += sqrtPow2){
			if(!ret.contains(cells[i])){
				ret.add(cells[i]);
			}
		}
		
		int dx = c - (c / sqrt) * sqrt; // the difference cell's index and the beginning of its box along the x axis
		int dy = r - (r / sqrtPow3) * sqrtPow3; // the difference cell's index and the beginning of its box along the y axis
		int s = n - dx - dy; // the index of the beginning of this cell's box
		
		int[] boxIndices;
		if(SUPER_SUDOKU)
			boxIndices = new int[]{s, s + 1, s + 2, s + 3, s + 16, s + 17, s + 18, s + 19, s + 32, s + 33, s + 34, s + 35,
					s + 48, s + 49, s + 50, s + 51};
		else
			boxIndices = new int[]{s, s + 1, s + 2, s + 9, s + 10, s + 11, s + 18, s + 19, s + 20};

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