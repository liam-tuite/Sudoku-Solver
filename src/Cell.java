package src;

import javax.swing.JTextField;

@SuppressWarnings("serial")
public class Cell extends JTextField{
	
	private char value;
	private int index;
	
	public int getIndex(){
		return this.index;
	}
	
	public char getValue(){
		return this.value;
	}

	public void initValue(){
		
		String text = getText();
		
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
	
	public void setIndex(int index){
		this.index = index;
	}
	
	public void setValue(char value){
		this.value = value;
	}
}