import java.util.HashMap;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;


public class AStarSearcher {
	
	// Reference to a initial state. 
	private SudokuState start = null;
	
	// states to be visited (in a priority queue).
	private Queue<SudokuState> frontier = new LinkedList<SudokuState>();
	
	
	// map of pairs (hash code of state, reference to that state) keeping all the visited states.
	private Map<String, SudokuState> explored = new HashMap<String, SudokuState>();
	
	
	//List of solutions found. 
	//private List<SudokuState> solution = new LinkedList<SudokuState>();	
	private SudokuState solution;
	

	// Constructor
	public AStarSearcher(SudokuState start) {
		this.start = start;
	}
	
	// Returns the solution
	public SudokuState getSolution() {
		return solution;
	}
	
	//Performs the A* search. 
	public void findSolution() {
		SudokuState currentState = start;
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
				System.out.println("Requested children");
				List<SudokuState> children = currentState.getChildren();
				for (SudokuState child : children) {
					if (explored.containsKey(child.getHashCode())) {
						continue;
					}
					if (!frontier.contains(child)) {
						System.out.println("Added child to frontier");
						frontier.add(child);
					}
					else {
						boolean isNewBetter = false;
						SudokuState theExisting = null;

						//finding existing in the frontier set
						for (SudokuState existing : frontier) {
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
    
    public void buildChildren(SudokuState parent) {
        int i=0, j=0, k = 0;
        for (k = 0; k < 81; k++) {
            i = k / 9;
            j = k % 9;
            if (parent.getBoard()[i][j] == 0) {
                break;
            }
        }
        List<SudokuState> children = new ArrayList<>();
        for (k = 0; k < 9; k++) {
            SudokuState child = new SudokuState(parent);
            child.setNumber(i, j, (k + 1));
            if (child.isValid()) {
            	System.out.println("Heuristic calculation!");
                child.getHeuristic();
                children.add(child);
            }
            parent.setChildren(children);
        }
    }
}