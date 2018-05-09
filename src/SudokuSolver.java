package src;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Iterator;

public class SudokuSolver{
	
	private Cell[] cells;
	
	private static SudokuSolver instance;

	public static void main(String[] args){
		/*
		 * Read in 81 values
		 * For each empty cell:
		 * 	-> Check containing box, row and column
		 * 	-> Eliminate impossible values
		 * 	-> If only one left, set the value
		 * Output the solved grid
		 */
		instance = new SudokuSolver();
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GridFrame frame = new GridFrame(instance);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void setCells(Cell[] cells){
		this.cells = cells;
	}
	
	public void solve(){
		
		boolean finished = false;
		
		ArrayList<Cell> unsolvedCells = getUnsolvedCells();
		ArrayList<Cell> connectedCells = new ArrayList<>();
		
		while(!finished){
			
			finished = true;
			
			for(Iterator<Cell> iterator = unsolvedCells.iterator(); iterator.hasNext();){
				
				Cell cell = iterator.next();
				
				connectedCells = getConnectedCells(cell);
				char value = eliminateSymbols(connectedCells);
				
				if(value != 'X'){
					cell.setValue(value);
					cell.setText("" + value);
					iterator.remove();
					finished = false; // if we have made a change, we will iterate the loop least one more time
				}	
			}
		}
	}
	
	private char eliminateSymbols(ArrayList<Cell> connectedCells){
		
		boolean[] possibleSymbols = {true, true, true, true, true, true, true, true, true};
		
		for(Cell c : connectedCells){
			
			switch(c.getValue()){
			case '1': {possibleSymbols[0] = false; break;}
			case '2': {possibleSymbols[1] = false; break;}
			case '3': {possibleSymbols[2] = false; break;}
			case '4': {possibleSymbols[3] = false; break;}
			case '5': {possibleSymbols[4] = false; break;}
			case '6': {possibleSymbols[5] = false; break;}
			case '7': {possibleSymbols[6] = false; break;}
			case '8': {possibleSymbols[7] = false; break;}
			case '9': {possibleSymbols[8] = false; break;}
			default:;
			}
		}
		
		int count = 0;
		char ret = ' '; // initialise blank char to return
		for(int i = 0; i < possibleSymbols.length; i++){
			
			if(possibleSymbols[i]){
				count++;
				ret = Character.forDigit(i + 1, 10); // this gets the value of i + 1 as a character
			}
		}
		
		// if more than one possibility, return X to indicate uncertainty
		if(count > 1)
			ret = 'X';
		
		// otherwise, return the solution to this cell
		return ret;
	}
	
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
	
	private ArrayList<Cell> getUnsolvedCells(){

		ArrayList<Cell> unsolvedCells = new ArrayList<>();
		
		for(int i = 0; i < cells.length; i++){
			
			cells[i].setIndex(i);
			if(cells[i].getValue() == 'X' && !unsolvedCells.contains(cells[i]))
				unsolvedCells.add(cells[i]);
		}
		
		return unsolvedCells;
	}
}