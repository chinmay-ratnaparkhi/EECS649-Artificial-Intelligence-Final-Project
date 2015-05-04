/* ----------------------------------------------------------------------------
	  This project is authored by 
     
	Joseph Champion 		(2462781)	
	Chinmay Ratnaparkhi		(2736356)

	EECS 649 - Project 04
	Dr. Frank Brown
	05/04/2015
 * ---------------------------------------------------------------------------- 
							A* Sudoku Solver

	This project uses the A* algorithm to solve a given Sudoku puzzle. These
	*.java files can be compiled with the provided makefile and run by typing 
	"java Solver" in the terminal after compilation.
	
	The Solver requests a text file name to read the Start State to begin
	computation. 4 Test files (easy.txt, medium.txt, hard.txt and insane.txt) 
	are provided in the 'test' folder. These files can be loaded by entering
 	their names in the format : "xyz.txt" after running the Solver, since
 	
 	The Solver searches IN THE "TEST" FOLDER BY DEFAULT.

 	If additional test files need to be added, they can be added to the "test"
 	folder.		

*  ---------------------------------------------------------------------------- 
	chinmay.ratnaparkhi@ku.edu
	champion@ku.edu
*  ---------------------------------------------------------------------------- 	
*/
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;


public class AStar 
{

	// Reference to a initial state. 
	private State start = null;
	
	// states to be visited (in a priority queue).
	private Queue<State> frontier = new LinkedList<State>();
	
	
	// map of pairs (hash code of state, reference to that state) keeping all the visited states.
	private Map<String, State> explored = new HashMap<String, State>();
	
	
	//List of solutions found. 
	//private List<State> solution = new LinkedList<State>();	
	private State solution;
	

	// Constructor
	public AStar(State start) {
		this.start = start;
	}
	
	// Returns the solution
	public State getSolution() {
		return solution;
	}
	
	//Performs the A* search. 
	public void findSolution() {
		State currentState = start;
		while (true) {

			// If the puzzle is solved.
			if (currentState.isGoalState()) {

				// label the current state as the solution.
				solution=currentState;
				
				// Terminate the search.
				return;
			}
			else {
				
				//Generate children states
				buildChildren(currentState);
				
				List<State> children = currentState.getChildren();
				for (State child : children) {
					if (explored.containsKey(child.getHashCode())) {
						continue;
					}
					if (!frontier.contains(child)) {
						frontier.add(child);
					}
					else {
						boolean isNewBetter = false;
						State theExisting = null;

						//finding existing in the frontier set
						for (State existing : frontier) {
							if (existing.equals(child)) {
								if (child.getG() < existing.getG()) {
									isNewBetter = true;
									theExisting = existing;
								}
								break;
							}
						}
						if (isNewBetter) {
							frontier.remove(theExisting);
							frontier.add(child);
						}
					}
				}
			}
			explored.put(currentState.getHashCode(), currentState);
			// Stop condition of the algorithm
			if (frontier.isEmpty()) break; 

			// Try the next state
			currentState = frontier.poll();			
		}
	}
    
    public void buildChildren(State parent) {
        int i=0, j=0, k = 0;
        for (k = 0; k < 81; k++) {
            i = k / 9;
            j = k % 9;
            if (parent.getBoard()[i][j] == 0) {
                break;
            }
        }
        List<State> children = new ArrayList<>();
        for (k = 0; k < 9; k++) {
            State child = new State(parent);
            child.setNumber(i, j, (k + 1));
            if (child.isValid()) {
                child.getHeuristic();
                children.add(child);
            }
            parent.setChildren(children);
        }
    }
}