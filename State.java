/* ----------------------------------------------------------------------------
	  This project is authored by 
     
	Joseph Champion 		(2462781)	
	Chinmay Ratnaparkhi		(2736356)

	EECS 649 - Project 04
	Dr. Frank Brown
	05/04/2015
 * ---------------------------------------------------------------------------- 
							A* Sudoku Solver --> State.java

	Holds the state of the board. Checks the present configuration of the board
	for invalidities i.e. checks for duplicates in rows, columns and 3by3 boxes.
	The heuristic function counts the number of 0's in the board to determine
	how far away the current state is from the goal state; the goal state being
	a fully filled board. Finally the isSolved method checks the board for validity
	and completeness to determine if the current state is the solution to the
	given puzzle.

*  ---------------------------------------------------------------------------- 
	chinmay.ratnaparkhi@ku.edu
	champion@ku.edu
*  ---------------------------------------------------------------------------- 	
*/
import java.util.List;
public class State {

	// A* Variables 
	private double g = 0.0;
	private double h = 0.0;
	private final Printer printer;
	
	// Value of g : Cost of reaching this state from the initial state. 
    public double getG() {
        return g;
    }

    // Value of h : Heuristic grade.
    public double getH() {
        return h;
    }

    // Value of f : g + h
    public double getF() {
        return getG() + getH();
    }

    // Check : Goal Reached?
    public boolean isGoalState() {
        return getH() == 0;
    }
    
    public String getHashCode() {        
        return Integer.toString(this.hashCode());
    }

    // State Constructor
    public State(State parent) {
        this.board = new int[9][9];
        if(parent != null){
            int[][] parentSudoku = parent.getBoard();
            for (int row = 0; row < 9; row++) {
                for (int column = 0; column < 9; column++) {
                    this.board[row][column] = parentSudoku[row][column];
                }
            }
        }
        this.printer = new Printer();
    }
    
    // Children State Functions
    private List<State> children = null;
    public List<State> getChildren() {
        return children;
    }
    public void setChildren(List<State> aChildren) {
        children = aChildren;
    }
        
    // Sudoku Board
    private int[][] board;
    public int[][] getBoard() {
        return board;
    }
    
    // Board to String for printing
    public String toString() {
        return printer.printSudoku(board);
    }
    
    
    // Board Validity
    public boolean isValid() {
        return isValid(board);

    }
    
    //Sets value of row and column in Sudoku board
    public void setNumber(int row, int column, int value) {
        try{
        	board[row][column] = value;	
        }
        catch (ArrayIndexOutOfBoundsException e) {
        	System.err.println("row " + row + " column " + column + " value " + value);
        	throw e;
        }
    }
    
    //Gets number in row and column in Sudoku board
    public int getNumber(int row, int column) {
        return board[row][column];
    }
    
    //Loads Sudoku from string, values should be separated by ','
    public void loadSudokuFromString(String sudokuData) {
        if (sudokuData == null) {
            throw new NullPointerException();
        }

        board = new int[9][9];
        int row = 0;
        int column = 0;
       
        // Create an array of 81 pieces of the String separated by commas.
        String[] tokens = sudokuData.split(",");
        
        int toBeAdded = 0;
        // 81 items to be added to the board and translated to integers.
        for (int i=0; i<81; i++) {
        	try{
        		toBeAdded = Integer.parseInt(tokens[i]);	
        	}
            catch (NumberFormatException e) {
                //ignoring
            }
            
            // place toBeAdded at the proper row and column
            setNumber(row, column, toBeAdded);
            column++;
            
            //After using up all the 9 columns, go to next row and start at column 0.
            if (column == 9) {
            	row = row + 1;
            	column = 0;     
            }
        }
        getHeuristic();
    }
   
    /*************** Heuristic Function *********************
    *   This function is designed to calculate the distance 
    *   between the current state and the goal state. The 
    *   function iterates throught the entire board to count
    *   the number of 0's. The larger the number of 0's the
    *   further away you are from the solution. The h of the
    *   current state is set to the heuristic value and it is
    *   returned to the caller as well.
    */
    public double getHeuristic() {
        int counter = 0;
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                if (board[row][column] == 0) {
                    counter++;
                }
            }
        }
        this.h = counter;
        return counter;
    }

    
    private boolean isValid(final int[][] board) {
        
        // THE BOARD SIZE CHECK IS NOT NECESSARY ANY MORE SINCE THE BOARD WILL 
        // ALWAYS BE 9x9.
        // CHINMAY : I'm getting rid of this, is that okay Joe?
        // JOE : Yes.
        //
        /*
        for (int[] row : board) {
            if (row.length != 9) {
                throw new IllegalArgumentException("Invalid board size.");
            }
        }
        */
    

        //
        // Check each row for validity
        // No number should be repeated in a single row
        //
        int[] sudokuRow = new int[9];
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                sudokuRow[column] = board[row][column];
            }
            if (isDuplicatePresent(sudokuRow)) {
                return false;
            }
        }
        
        //
        // Check each column for validity 
        // No number should be repeated in a single column
        //
        int[] sudokuColumn = new int[9];
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                sudokuColumn[column] = board[column][row];
            }
            if (isDuplicatePresent(sudokuColumn)) {
                return false;
            }
        }

        //
        // Check each 3x3 box for validity
        // No number should be repeated in a single 3x3 box
        //
        for (int box = 0; box < 9; box++) {
            int[] threeBythreeBox = new int[9];
            int boxRow = box / 3;
            int boxColumn = box % 3;
            int indexRowOffset = boxRow * 3;
            int indexColumnOffset = boxColumn * 3;
            for (int index = 0; index < 9; index++) {
                threeBythreeBox[index] = board[indexRowOffset + (index / 3)][indexColumnOffset +(index % 3)];
            }
            if (isDuplicatePresent(threeBythreeBox)) {
                return false;                        
            }            
        }
        return true;
    }

    // 
    // Check if a number is repeated in a given set
    // of numbers. The caller function must send in
    // the data in the format of an integer array.
    //
    private boolean isDuplicatePresent(int[] data) {
        for (int i = 0; i < data.length; i++) {
            int value = data[i];
            if (value != 0) {
                for (int j = i + 1; j < data.length; j++) {
                    if (value == data[j]) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Determine if the current board is a solved sudoku state.
    // The solved state can be determined by 2 factors. 
    //
    // 1. The board has to be in a valid state. 
    //    i.e.  No number should appear twice in a
    //    row, column or a 3x3 box.
    //
    // 2. The board should consist of no zeros.
    //    i.e. every cell has to be filled by 
    //    a valid number from 1 to 9.
    //
    public boolean isSolved(final int[][] board) {
    	if (isValid(board)) {
    		for (int row = 0; row < 9; row++) {
    			for (int column = 0; column < 9; column++) {
    				if (board[row][column] == 0) {
    					return false;
    				}
    			}
    		}
    	} else {
    		return false;
    	}

    	return true;
        /*
        * EDIT BY JOE: Not sure if this is will work in all cases,
        * but I think this is a faster alternative than the above.
        */
    	
    	/* COMMENTS - CHINMAY : 
         * We can't use the following line because it only checks the 
    	 * very last cell of the board i.e. board[8][8] to see if it is
    	 * still 0. We need to go through the entire board to make sure
    	 * that all the 0's are now replaced by valid numbers i.e. 1-9. 
    	 */
        //return isValid(board) && board[8][8] != 0;
    }
}
