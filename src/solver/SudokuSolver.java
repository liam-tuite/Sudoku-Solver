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
	private final static boolean DEBUGGING_MODE = true;
	private final static char[] DEBUG_CELLS = {'X', 'X', 'X', '3', '8', 'X', 'X', '7', '6', 'X', '0', '5', 'A', 'F', '1', 'X', 'X', '6', 'X', '4', 'C', 'A', 'X', '5', '9', 'B', 'X', '2', 'D', 'X', 'X', 'X', '1', '9', '0', 'E', 'D', 'B', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', '6', 'X', '5', 'X', 'X', 'X', '4', '2', 'X', '3', 'X', 'X', 'X', 'X', 'X', '0', 'X', 'X', 'X', 'X', 'X', '3', '9', 'X', 'X', 'X', '7', 'X', 'X', 'X', 'C', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'F', 'X', 'X', 'X', '4', 'E', '0', '7', 'F', 'X', '0', 'X', 'X', 'X', 'X', 'A', 'C', 'X', 'X', 'X', 'X', 'X', '5', 'X', 'X', '6', 'X', 'X', '7', 'X', 'X', 'X', '9', 'X', 'X', 'X', 'X', 'X', 'X', '3', 'X', 'X', 'D', 'X', 'X', 'X', 'X', '5', '6', 'X', 'X', 'X', 'X', 'A', '1', 'E', 'X', 'X', 'F', '7', 'X', '1', 'A', 'D', '8', 'X', 'X', 'X', '0', '9', 'B', '5', 'X', 'X', 'X', 'X', '6', '4', '2', 'X', '0', 'C', 'X', 'X', '3', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', '4', 'X', '5', 'X', 'X', 'X', 'A', 'X', '5', '8', '7', '9', 'X', 'X', 'X', '4', '0', 'X', 'X', 'X', 'B', 'X', 'X', '5', 'X', 'X', 'C', 'X', 'F', 'X', 'X', '1', '2', 'E', 'X', 'D', 'X', 'X', '7', 'C', 'X', 'X', 'A', 'X', 'X', 'X', 'X', '9', 'X', '1', 'F', '8', 'D', '2', 'X', '6', 'X', '3', 'E', 'F', '0', '5', 'X', 'X', 'B', 'A', 'X', 'X'};
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
			if(unsolvedCellCount != unsolvedCells.size())
				unsolvedCellCount = unsolvedCells.size();
			else
				finished = true;
		}
		
		// If there are still unsolved cells, use brute force
		if(!unsolvedCells.isEmpty()) // if there are still cells to solve after simple elimination
			solveBruteForce(unsolvedCells);
	}
	
	private void eliminateSimple(){
		
		eliminateSoleCandidate();
		eliminateUniqueCandidate();
	}

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
	
	private void eliminateUniqueCandidate(){
		
		eliminateUniqueCandidate(columns);
		eliminateUniqueCandidate(rows);
		eliminateUniqueCandidate(blocks);
	}
	
	private void eliminateUniqueCandidate(Cell[][] container){
		
		for(Cell[] cells : container){
			
			// keep track of the number of Cells that could contain a certain value
			int[] totals = new int[values.length];
			for(Cell cell : cells){
				
				if(cell.getValue() == 'X'){
					boolean[] possibleValues = cell.getPossibleValues();
					for(int i = 0; i < possibleValues.length; i++)
						if(possibleValues[i])
							totals[i]++;
				}
			}
			
			// if any of the possible values can only be in one Cell, set that Cell's value to the possible value
			for(int i = 0; i < totals.length; i++){
				if(totals[i] == 1)
					// look for the Cell with the corresponding possible value and set it
					for(Cell cell : cells){
						if(cell.getValue() == 'X'){
							if(cell.getPossibleValues()[i]){
								cell.setValue(values[i]);
								
								unsolvedCells.remove(cell);
								break;
							}
						}
					}
			}
		}
	}
	
	private void initCellConnections(){

		for(Cell cell : cells){
			ArrayList<Cell> connections = new ArrayList<>();

			for(Cell c : columns[cell.getColIndex()])
				if(!c.equals(cell))
					connections.add(c);
			for(Cell c : rows[cell.getRowIndex()])
				if(!c.equals(cell))
					connections.add(c);
			for(Cell c : blocks[cell.getBlockIndex()])
				if(!c.equals(cell))
					connections.add(c);
			
			cell.setConnectedCells(connections);
		}
	}
	
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