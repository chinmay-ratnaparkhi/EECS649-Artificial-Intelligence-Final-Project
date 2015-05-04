/* ----------------------------------------------------------------------------
	  This project is authored by 
     
	Joseph Champion 		(2462781)	
	Chinmay Ratnaparkhi		(2736356)

	EECS 649 - Project 04
	Dr. Frank Brown
	05/04/2015
 * ---------------------------------------------------------------------------- 
							A* Sudoku Solver --> Printer.java

	This class creates the Printer object that has the printSudoku method
	which accepts a Sudoku board as a 2-dimensional array and produces a 
	well-formatted output.

*  ---------------------------------------------------------------------------- 
	chinmay.ratnaparkhi@ku.edu
	champion@ku.edu
*  ---------------------------------------------------------------------------- 	
*/
public class Printer {
    public Printer() {}
    public String lineSeparator() {
    	String separator = "-------------------------------\n";
    	return separator;
    }

    private String boxSeparator(int column) {
        if (column != 0 && ((column + 1) % 3 == 0))return " |";
           return " ";
    }

    public String printSudoku(int[][] board) {
        
    	String toBePrinted= lineSeparator();
        for (int i = 0; i < 9; i++) {
            toBePrinted+= "|";
            for (int j = 0; j < 9; j++) {
            	toBePrinted += String.format("%2s", board[i][j]);
                toBePrinted += boxSeparator(j); 	
            }
            
        	toBePrinted += "\n";
        	if (i != 0 && ((i + 1) % 3 == 0)) {
                toBePrinted += lineSeparator();
            }
        }        
        return toBePrinted;
    }
}
