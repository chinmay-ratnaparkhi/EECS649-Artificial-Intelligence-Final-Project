import java.util.Scanner;
import java.io.*;

public class SudokuSolver {
	
    public static void main(String[] args) {
    	
    	// Timer variables
    	long begin=0, finish=0;
    	
    	/* On launch the system requests the name of the file that contains the Start State of the Sudoku 
    	 * puzzle. The file MUST BE space/tab delimited. The string is formatted according to the input 
    	 * requirements of the subsequently called methods. 
    	 */	
    	String final_input="";
		String read_from="";
		System.out.print("Please enter the name of the file containing the Start State : ");
		Scanner scanner = new Scanner(System.in);
		read_from = scanner.nextLine();
		scanner.close();
		try{
		    FileInputStream fstream = new FileInputStream(read_from);
		    DataInputStream in = new DataInputStream(fstream);
		    BufferedReader br = new BufferedReader(new InputStreamReader(in));
		    String strLine;
		          
		    while ((strLine = br.readLine()) != null){
		    	if(strLine.trim().length() != 0){
		    		/*  An array called 'elements' is created to contain every number in a 
		    		 *  single line, a comma is added after each number.
		    		 */
		    		String[] elements = strLine.split(" ");
		    		int i= 0;
		        	  	do{
		        			final_input+= elements[i];
		        		  	final_input+=",";	
		        		  	i++;
		        	  	}
		        	  	while(i<elements.length);
		        	  }
		          }

		   in.close();
		   /* The very last comma is removed by creating a substring of length one less than
		    * the length of the original string.
		    */
		   final_input = final_input.substring(0, final_input.length()-1);
		   }catch (Exception e){
		     System.err.println("Error: " + e.getMessage());
		     System.exit(0);
		   }
	
    	SudokuState startState = new SudokuState(null);
        startState.loadSudokuFromString(final_input);
        AStarSearcher solver = new AStarSearcher(startState);

        // Perform the Timed solution Search
        begin= System.currentTimeMillis();
        solver.findSolution();
        finish = System.currentTimeMillis();
        
        // Retrieve solution from the solver and print         
        if(solver.getSolution()!=null){
        	System.out.println("\nSolution time " + (finish - begin) + " ms");
        	System.out.println("The most efficient solution : \n");
        	System.out.println(solver.getSolution().toString());       	
        }else{
        	System.out.println("The provided puzzle has no solutions.");
        }
               
    }
}
