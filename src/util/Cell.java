package util;

import java.util.ArrayList;

import javax.swing.JTextField;

@SuppressWarnings("serial")
public class Cell extends JTextField{
	
	private boolean[] possibleValues;
	private char value;
	private int index;
	
	private ArrayList<Cell> connectedCells;
	private Cell previousUncertain;
	
	public Cell(){
		resetPossibilities(); // initialise all possibilities as true (they will be falsified later)
	}
	
	/**
	 * Remove the given value from the possibilities.
	 * 
	 * @param value The possible value we are removing.
	 */
	public void eliminateValue(char value){
		
		if(value != 'X')
			possibleValues[Integer.parseInt("" + value) - 1] = false;
	}
	
	/**
	 * Looks through all connected Cells and eliminates possible values that are already entered in one or more of these
	 * Cells.
	 */
	public void eliminateValues(){
		
		for(Cell c : connectedCells)
			eliminateValue(c.value);
		
		int count = 0; // count all of the possible values remaining
		for(int i = 0; i < possibleValues.length; i++){
			
			if(possibleValues[i]){
				
				count++;
				if(count > 1){
					value = 'X';
					break;
				}
				
				value = Character.forDigit(i + 1, 10); // this gets the value of i + 1 as a character
			}
		}
		setValue(value);
	}
	
	public int getIndex(){
		return this.index;
	}
	
	public Cell getPreviousUncertain(){
		return previousUncertain;
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
		
		switch(text){
		case "1": {value = '1'; break;}
		case "2": {value = '2'; break;}
		case "3": {value = '3'; break;}
		case "4": {value = '4'; break;}
		case "5": {value = '5'; break;}
		case "6": {value = '6'; break;}
		case "7": {value = '7'; break;}
		case "8": {value = '8'; break;}
		case "9": {value = '9'; break;}
		default: value = 'X';
		}
	}
	
	/**
	 * Sets all possible values to "true".
	 */
	public void resetPossibilities(){
		possibleValues = new boolean[]{true, true, true, true, true, true, true, true, true};
	}
	
	public void setConnectedCells(ArrayList<Cell> connectedCells){
		this.connectedCells = connectedCells;
	}
	
	public void setIndex(int index){
		this.index = index;
	}
	
	public void setPreviousUncertain(Cell previousUncertain){
		this.previousUncertain = previousUncertain;
	}
	
	public void setValue(char value){
		
		this.value = value;
		if(value == 'X')
			setText("");
		else
			setText("" + value);
	}
	
	/**
	 * Sets the value of this Cell to the first possible value.
	 */
	public void takeFirstValue(){
	
		eliminateValues();
		for(int i = 0; i < possibleValues.length; i++){
			if(possibleValues[i]){
				setValue(Character.forDigit(i + 1, 10));
				break;
			}
		}
	}
}