package solver;

import java.util.ArrayList;
import java.util.Iterator;

import gui.SudokuFrame;
import util.Cell;

public class SudokuSolver{
	
	private ArrayList<Cell> solvedCells, unsolvedCells;
	private Cell[] cells;
	private Cell[][] columns, rows, blocks;

	// In debugging mode, the program loads data from the DEBUG_CELLS array and solves for these values
	private final static boolean DEBUGGING_MODE = false;
	private final static char[] DEBUG_CELLS = {'X', 'X', '1', 'X', 'C', 'D', 'E', 'A', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'C', 'X', 'D', 'X', '9', 'F', 'X', '0', 'B', '8', 'X', '3', 'X', 'X', 'X', 'E', '0', 'X', 'B', 'X', 'X', '7', 'X', 'X', '6', 'X', '2', 'A', 'X', '9', 'X', 'X', 'X', 'X', 'X', 'X', 'B', 'X', 'X', '3', 'X', 'E', 'F', '8', '1', 'C', '2', 'X', '1', '7', '0', 'X', 'F', 'A', '2', 'E', '5', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'E', 'X', 'X', '4', '8', 'X', 'X', '9', 'F', 'X', 'X', '2', 'X', 'B', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'A', 'X', 'D', 'A', 'X', 'X', 'X', '0', 'X', 'X', 'X', 'X', '2', 'X', 'D', 'C', 'B', 'X', '1', 'B', '3', '5', 'C', '7', 'X', '6', 'X', 'X', 'F', '4', '1', 'X', 'X', 'A', 'X', 'X', 'X', 'X', 'X', '4', 'X', 'D', 'X', 'X', '9', '7', 'B', 'X', 'X', 'X', '5', 'X', 'X', 'X', 'X', 'B', '8', 'X', 'X', 'X', 'X', 'X', '3', 'X', 'X', 'X', 'X', 'X', 'X', 'X', '8', 'F', '2', '1', 'X', 'X', 'X', 'C', 'E', 'B', '9', 'D', 'X', 'X', 'X', 'X', '9', '3', '1', 'X', 'X', 'X', 'X', 'X', 'X', 'X', '8', 'X', 'X', 'D', 'X', 'X', '3', '5', 'C', 'X', '4', 'X', '1', 'X', 'X', '2', '0', 'B', 'X', 'X', 'X', 'X', '1', 'X', 'X', '8', 'X', 'X', 'X', 'X', 'X', 'X', 'C', 'X', '6', 'X', 'F', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', '1', 'X', '3', 'X'};
	private static SudokuFrame frame;
	private static SudokuSolver instance;
	
	public static char[] values;	
	public final static boolean SUPER_SUDOKU = true; // false if grid is 9x9, true if 16x16
	public final static char[] VALUES_STANDARD = {'1', '2', '3', '4', '5', '6', '7', '8', '9'};
	public final static char[] VALUES_SUPER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

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
		initGridConnections();
		initCellConnections();
		
		partitionCells();
		int unsolvedCellCount = unsolvedCells.size();
		boolean finished = false;

		while(!finished){
			
			eliminateSimple();
			
			int newCount = unsolvedCells.size();
			if(newCount == 0 || newCount != unsolvedCellCount)
				unsolvedCellCount = newCount;
			else
				finished = true;
		}
		
		// If there are still unsolved cells, use brute force
		if(!unsolvedCells.isEmpty()) // if there are still cells to solve after simple elimination
			solveBruteForce(unsolvedCells);
	}
	
	/**
	 * Calls the simple elimination techniques. The more advanced techniques have yet to be implemented.
	 */
	private void eliminateSimple(){
		
		eliminateSoleCandidate();
		eliminateUniqueCandidate();
	}

	/**
	 * Checks each Cell for a 'sole candidate' - a single possible value that the Cell must have.
	 */
	private void eliminateSoleCandidate(){
			
		// first, eliminate from each Cell the possibility of the value of any known Cell it is connected to
		for(Cell solvedCell : solvedCells){
			
			ArrayList<Cell> connectedCells = solvedCell.getConnectedCells();
			
			for(Cell connection : connectedCells)
				connection.eliminateValue(solvedCell.getValue());
		}
		
		// for each unsolved Cell
		for(Iterator<Cell> iterator = unsolvedCells.iterator(); iterator.hasNext();){
			
			Cell cell = iterator.next();
			cell.takeLastValue(); // This Cell takes the single remaining possible value, or remains unchanged 
			
			if(cell.getValue() != 'X') // if solution was found
				iterator.remove(); // remove this Cell from unsolvedCells
		}
	}
	
	/**
	 * Performs 'unique candidate' elimination on each set of containers.
	 */
	private void eliminateUniqueCandidate(){
		
		eliminateUniqueCandidate(columns);
		eliminateUniqueCandidate(rows);
		eliminateUniqueCandidate(blocks);
	}
	
	/**
	 * This method checks a group of 'containers' (which are either columns, rows or blocks) for any values that have only
	 * one potential Cell within a container.
	 * 
	 * @param containers The group of containers (i.e. columns, rows or blocks) that we are checking.
	 */
	private void eliminateUniqueCandidate(Cell[][] containers){
		
		for(Cell[] container : containers){
			
			// keep track of the number of Cells that could contain a certain value
			int[] totals = new int[values.length];
			for(Cell cell : container)
				
				if(cell.getValue() == 'X'){
					boolean[] possibleValues = cell.getPossibleValues();
					for(int i = 0; i < possibleValues.length; i++)
						if(possibleValues[i])
							totals[i]++;
				}
			
			// if any of the possible values can only be in one Cell, set that Cell's value to the possible value
			for(int i = 0; i < totals.length; i++)
				if(totals[i] == 1)
					// look for the Cell with the corresponding possible value and set it
					for(Cell cell : container)
						if(cell.getValue() == 'X')
							if(cell.getPossibleValues()[i]){
								cell.setValue(values[i]);
								
								unsolvedCells.remove(cell);
								break;
							}
		}
	}
	
	/**
	 * Initialises the connected Cells of each Cell in the grid.
	 */
	private void initCellConnections(){

		for(Cell cell : cells){
			ArrayList<Cell> connections = new ArrayList<>();

			for(Cell c : columns[cell.getColIndex()])
				if(!connections.contains(c) && !c.equals(cell))
					connections.add(c);
			for(Cell c : rows[cell.getRowIndex()])
				if(!connections.contains(c) && !c.equals(cell))
					connections.add(c);
			for(Cell c : blocks[cell.getBlockIndex()])
				if(!connections.contains(c) && !c.equals(cell))
					connections.add(c);
			
			cell.setConnectedCells(connections);
		}
	}
	
	/**
	 * Initialises the columns, rows and blocks matrices.
	 */
	private void initGridConnections(){

		int sqrt;
		if(SUPER_SUDOKU){
			sqrt = 4;
		}
		else{
			sqrt = 3;
		}
		int sqrtPow2 = (int) Math.pow(sqrt, 2);
		
		columns = new Cell[sqrtPow2][sqrtPow2];
		rows = new Cell[sqrtPow2][sqrtPow2];
		blocks = new Cell[sqrtPow2][sqrtPow2];
		
		for(int i = 0; i < cells.length; i++){
			
			int colIndex = i % sqrtPow2;
			int rowIndex = i / sqrtPow2;
			int blockIndex = (rowIndex / sqrt) * sqrt + (colIndex / sqrt);
			
			cells[i].setIndices(i, colIndex, rowIndex, blockIndex);
			
			columns[colIndex][rowIndex] = cells[i];
			rows[rowIndex][colIndex] = cells[i];
			blocks[blockIndex][colIndex % sqrt + (rowIndex % sqrt * sqrt)] = cells[i];
		}
	}
	
	/**
	 * Partitions the Cells into the two ArrayLists solvedCells and unsolvedCells.
	 */
	private void partitionCells(){
		
		solvedCells = new ArrayList<>();
		unsolvedCells = new ArrayList<>();
		
		for(Cell c : cells)
			if(c.getValue() == 'X')
				unsolvedCells.add(c);
			else
				solvedCells.add(c);
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