package util;

@SuppressWarnings("serial")
/**
 * This exception relates to issues with a sudoku value being more than a single character in length.
 */
public class ValueLengthException extends Exception{

	public ValueLengthException(String arg0){
		super(arg0);
	}
}