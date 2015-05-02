import java.io.*;
import java.util.Scanner;

public class reader {
	public static void main(String[] args) {
    	
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
		          
		          while ((strLine = br.readLine()) != null)   {
		        	  if(strLine.trim().length() != 0)
		        	  {
		        		  
		        		/*  An array called 'Tokens' is created to contain every number in a 
		        		 *  single line, a comma is added after each number.
		        		 */
		        	  	String[] tokens = strLine.split(" ");
		   			  	int i=0;
		        	  	do{
		        			final_input+= tokens[i];
		        		  	final_input+=",";	
		        		  	i++;
		        	  	}
		        	  	while(i<tokens.length);
		        	  }
		          }

		   in.close();
		   /* The very last comma is removed by creating a substring of length one less than
		    * the length of the original string.
		    */
		   final_input = final_input.substring(0, final_input.length()-1);
		   }catch (Exception e){
		     System.err.println("Error: " + e.getMessage());
		   }
		
		//print the final string
		//System.out.println(final_input);
	
    	SudokuState sudokuState = new SudokuState(null);
    	
    	// Assign the final_input to sudokuData for further processing.
        String sudokuData = final_input;
        sudokuState.loadSudokuFromString(sudokuData);
        AStarSearcher solver = new AStarSearcher(sudokuState);


	
	

}
