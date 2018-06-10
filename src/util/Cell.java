package util;

import java.util.ArrayList;

import javax.swing.JTextField;

import solver.SudokuSolver;

@SuppressWarnings("serial")
public class Cell extends JTextField{
	
	private boolean[] possibleValues;
	private char value;
	private int gridIndex, rowIndex, colIndex, blockIndex;
	
	private ArrayList<Cell> connectedCells;
	private Cell previousUncertain;
	
	public Cell(){
		resetPossibilities(); // initialise all possibilities as true (they will be falsified later)
		connectedCells = new ArrayList<>();
	}
	
	/**
	 * Remove the given value from the possibilities.
	 * 
	 * @param value The possible value we are removing.
	 */
	public void eliminateValue(char value){
		
		if(value != 'X')
			for(int i = 0; i < SudokuSolver.values.length; i++)
				if(value == SudokuSolver.values[i])
					possibleValues[i] = false;
	}
	
	public ArrayList<Cell> getConnectedCells(){
		return this.connectedCells;
	}
	
	public int getColIndex(){
		return this.colIndex;
	}
	
	public int getGridIndex(){
		return this.gridIndex;
	}
	
	public boolean[] getPossibleValues(){
		return this.possibleValues;
	}
	
	public Cell getPreviousUncertain(){
		return previousUncertain;
	}
	
	public int getRowIndex(){
		return this.rowIndex;
	}
	
	public int getBlockIndex(){
		return this.blockIndex;
	}
	
	public char getValue(){
		return this.value;
	}

	/**
	 * Initialise this Cell's value from the value entered in this Cell, or set value to 'X' if no value was entered.
	 */
	public void initValue() throws ValueLengthException{
		
		String text = getText();
		
		if(text.length() > 1)
			throw new ValueLengthException("Entered more than one character in a cell.");
		else if(text.length() == 0)
			value = 'X';
		else
			value = text.charAt(0);
	}
	
	/**
	 * Sets all possible values to "true".
	 */
	public void resetPossibilities(){
		
		if(SudokuSolver.SUPER_SUDOKU)
			possibleValues = new boolean[]{true, true, true, true, true, true, true, true, true, true, true, true, true,
					true, true, true};
		else
			possibleValues = new boolean[]{true, true, true, true, true, true, true, true, true};
	}
	
	public void setConnectedCells(ArrayList<Cell> connectedCells){
		this.connectedCells = connectedCells;
	}
	
	public void setIndices(int gridIndex, int colIndex, int rowIndex, int blockIndex){
		
		this.gridIndex = gridIndex;
		this.colIndex = colIndex;
		this.rowIndex = rowIndex;
		this.blockIndex = blockIndex;
	}
	
	public void setPreviousUncertain(Cell previousUncertain){
		this.previousUncertain = previousUncertain;
	}
	
	/**
	 * Sets the Cell's value. This value is certain, so the connectedCells will be updated accordingly.
	 * 
	 * @param value The new value of the Cell.
	 */
	public void setValue(char value){
		setValue(value, false);
	}
	
	/**
	 * Sets the value of this Cell to the first possible value. This is only used in brute force solving, so the value being taken is marked as 'uncertain'.
	 */
	public void takeFirstValue(){
	
		eliminateValues();
		for(int i = 0; i < possibleValues.length; i++){
			if(possibleValues[i]){
				setValue(SudokuSolver.values[i], true);
				break;
			}
		}
	}
	
	/**
	 * Sets the Cell's value to the only remaining possible value, or 'X' if there is more than one possibility.
	 */
	public void takeLastValue(){

		int count = 0; // count all of the possible values remaining
		for(int i = 0; i < possibleValues.length; i++){
			
			if(possibleValues[i]){
				
				count++;
				if(count > 1){
					value = 'X';
					break;
				}
				
				value = SudokuSolver.values[i];
			}
		}
		setValue(value);
	}
	
	/**
	 * Calls the eliminateValue method on each connected Cell.
	 */
	private void eliminateValues(){

		for(Cell c : connectedCells)
			eliminateValue(c.value);
	}
	
	/**
	 * Sets this Cell's value, and updates the text of the Cell accordingly. If this wasn't called during the brute force stage, then all connectedCells
	 * are updated to exclude the possibility of this Cell's new value.
	 * 
	 * @param value The value to be given to this Cell.
	 * @param uncertain Whether we are certain about this value (uncertain is true if this was called during the brute force stage, and false otherwise).
	 */
	private void setValue(char value, boolean uncertain){
		
		this.value = value;
		if(value == 'X')
			setText("");
		else{
			setText("" + value);
			if(!uncertain)
				for(Cell c : connectedCells)
					c.eliminateValue(value);
		}
	}
}