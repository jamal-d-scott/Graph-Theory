import java.util.List;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

public class Graphs {

	public static void main(String[] args) throws FileNotFoundException 
	{
		Scanner kb = new Scanner(System.in);

		//take out of program--only for testing
		String graph;
		graph = "graph.txt";

		/*Easy for debugging. dont have to type in filename. PUT BACK INTO CODE.
		 * System.out.print("Enter the file for the graph: "); 
		 *graph = kb.nextLine();
		 */
		File filename = new File(graph);
		Scanner readFile = new Scanner(filename); //read everything in the file
		int numNodes = readFile.nextInt(); //first number is the amount of nodes
		int matrix[][] = new int [numNodes][numNodes]; //double array to store the nodes and weights

		for( int i = 0; i < numNodes; i++)	//loops through rows
		{  
			int edges = readFile.nextInt(); //first number on line(number of changes needed to be made)
			for( int j = 0; j < edges; j++) 
				matrix[i][readFile.nextInt()] = readFile.nextInt(); //gets next number which is the node that is connected to the current node and sets the weight in that spot in the array
		}
		readFile.close();

		// Display menu. User chooses which option they want to perform
		boolean quit = false;
		while(!quit) 
		{
			System.out.println("\n1. Is Connected");
			System.out.println("2. Minimum Spanning Tree");
			System.out.println("3. Shortest Path");
			System.out.println("4. Is Metric");
			System.out.println("5. Make Metric");
			System.out.println("6. Traveling Salesman Problem");
			System.out.println("7. Approximate TSP");
			System.out.println("8. Quit");
			System.out.print("Make your choice (1 - 8): ");
			String action = kb.next();
			switch(action)
			{
			case "1": 
				if(isConnected(matrix, numNodes))	
					System.out.println("\n" + "Graph is connected" + "\n");
				else
					System.out.print("\n" + "Graph is not connected" + "\n");
				break;
			case "2":
				if( isConnected( matrix, numNodes ) ) 
					minSpanningTree(numNodes, matrix);
				else 
					System.out.println("Error: Graph is not connected.");
				break;
			case "3":
				System.out.print("From which node would you like to find the shortest paths (0 - 5): ");
				int startingNode = kb.nextInt();
				shortestPath(numNodes, matrix, startingNode);
				break; 
			case "4": 
				isMetric(matrix,numNodes);
				break;
			case "5":
				makeMetric(matrix, numNodes);
				break;
			case "6": 
				TSP(matrix,numNodes);
				break;
			case "7": 
				a_TSP(matrix,numNodes);
				break;
			case "8":
				quit = true;
				break;
			default:
				System.out.println("Input not valid. Try again");
			}
		}
		kb.close();
	}

	/* Checks to see if the graph is connected; connections are initialized to false. It passes in the matrix with all of the connections and 
	 * passes in the number of nodes. It loops through the matrix and if the connection is false and not 0 than there is a connection. If the
	 * whole row is zeros and connected is false than the graph is not connected and it returns false. 
	 */
	public static boolean isConnected( int[][] matrix, int numNodes )
	{
		boolean connected;

		for( int i = 0; i < numNodes; i++)
		{  
			connected = false;
			for( int j = 0; j < numNodes; j++ ) 
				if( connected == false && matrix[ i ][ j ] != 0 )
					connected = true;

			if( connected == false )
				return false;
		}
		return true;
	}

	//The minSpanningTree method uses Prim's Algorithm to find the minimum spanning tree which includes all nodes but the total weight of the edges is minimized
	public static int[][] minSpanningTree( int numNodes, int[][]matrix )
	{
		int i,j, MSTnodes = 0;
		int MSTmatrix[][] = new int [ numNodes ][ numNodes ]; //create a new matrix for the minimum spanning tree
		boolean[] visited = new boolean[ numNodes ]; //keeps track of the nodes that have been visited

		visited[ 0 ] = true; //have something in the list to compare distances

		while( MSTnodes < numNodes )
		{
			int smallest = Integer.MAX_VALUE;
			int closestunVisitedNode = 0; 
			int closestVisitedNode = 0;
			for( i = 0; i < numNodes; i++ )
			{
				if( visited[ i ] )	
				{ 
					for( j = 0; j < numNodes; j++)
					{
						if( matrix[ i ][ j ] < smallest && matrix[ i ][ j ] != 0 && visited[ j ] == false ) //if the current number is not zero and is less than smallest 
						{ 
							smallest = matrix[ i ][ j ]; //set the spot that was just checked to the smallest
							closestunVisitedNode = j; 
							closestVisitedNode = i;
						}
					}
				}
			}
			MSTmatrix[ closestVisitedNode ][ closestunVisitedNode ] = matrix[ closestVisitedNode ][ closestunVisitedNode ]; //put the smallest distance found into the MST matrix
			MSTmatrix[ closestunVisitedNode ][ closestVisitedNode ] = matrix[ closestVisitedNode ][ closestunVisitedNode ]; //add the node distance that is connected to the current node being looked at 
			visited[ closestunVisitedNode ] = true; 
			MSTnodes++;
		}
		System.out.println("Minimum Tree: \n"+ adjacencyMatrixtoString( MSTmatrix, MSTnodes ) );
		return MSTmatrix;
	}
	//Prints out the amount of edges each node is connected to along with the node and weight that each node is connected to.
	public static String adjacencyMatrixtoString( int[][] matrix, int numNodes )
	{
		String out = numNodes + "\n";  //String to hold the output

		for( int i = 0; i < numNodes; i++ )
		{
			int numEdges = 0; 
			String edgeWeights = ""; 

			for( int j = 0; j < numNodes; j++ )
			{
				if( matrix[ i ][ j ] != 0 )
				{
					numEdges++;  //update the amount of edges
					edgeWeights += j + " " + matrix[ i ][ j ] + " ";  //hold where the connection is and the weight between the nodes
				}
			}
			out += numEdges + " " + edgeWeights.trim() + "\n";	//add the number of edges and the edge weights to the output string
		}
		System.out.println();
		return out;
	}

	/* shortestPath uses Dijkstra's algorithm to find the shortest path to each node. It passes in the number of nodes, the matrix with 
	 * all of the connections, and the starting node of the algorithm. First, it loops through and initializes the visited nodes to false
	 * and the shortest distances to the max value. It then calls the method, shortest, and puts the new shortest distance into the 
	 * shortestDist array.
	 */
	public static void shortestPath( int numNodes, int[][] matrix, int startNode )
	{
		int i;
		boolean [] visited = new boolean[ numNodes ];	//array to check if a node has been visited
		int [] shortestDist = new int[ numNodes ];	//array that holds the shortest distance between the starting node and the node currently being looked at 
		List<ArrayList<Integer>> paths = new ArrayList<ArrayList<Integer>>(); //create a list of arrays 

		for( i = 0; i < numNodes; i++ )
			paths.add( new ArrayList<Integer>() );	//initialize the list of arrays

		for( i = 0; i < numNodes; i++ ){
			visited[ i ] = false;	//set everything to false because it has not been visited 
			shortestDist[ i ] = Integer.MAX_VALUE; //set the array to hold the max value at each index
		}

		shortestDist[ startNode ] = 0;	//set the starting node to zero because the shortest distance to itself is zero
		paths.get(startNode).add(0);
		visited[ startNode ] = true;		//setting the start node to be already visited

		shortestDist = shortest( numNodes, matrix, visited, shortestDist, startNode, paths );

		for( i = 0; i < numNodes; i++ )
		{
			if( shortestDist[ i ] == Integer.MAX_VALUE)	
				System.out.println( i +" (Infinity)" ); //unreachable nodes
			else
			{
				System.out.print( i +": "+ "("+ shortestDist[ i ] + ")\t" );
				for( int j = 0; j < paths.get( i ).size()-1  ; j++ )
					System.out.print( paths.get( i ).get( j ) + " -> " );

				System.out.println( paths.get( i ).get( paths.get( i ).size() - 1) );
			}
		}
		System.out.println();
	} 

	// Helper method that finds the shortest path to each node 
	public static int[] shortest( int numNodes, int[][] matrix, boolean[] visited, int[] shortestDist, int startNode, List<ArrayList<Integer>> paths )
	{
		int shortest, i;
		visited[ startNode ] = true;

		//If the shortestDist value is smaller than what is currently in that spot in the array than the array[i] is updated to the smaller distance.  
		for( i = 0; i < numNodes; i++ )
		{
			if( ( matrix[ startNode ][ i ] + shortestDist[ startNode ] ) < shortestDist[ i ] && matrix[ startNode ][ i ] != 0 )
			{
				shortestDist[ i ] = matrix[ startNode ][ i ] + shortestDist[ startNode ];

				ArrayList<Integer> newPath = new ArrayList<Integer>();

				for( Integer n : paths.get(startNode) )
					newPath.add(n);
				newPath.add(i);
				paths.set(i, newPath);
			}
		}
		shortest = Integer.MAX_VALUE;
		for( i = 0; i < numNodes; i++ )
		{
			if(visited[ i ] != true && shortestDist[ i ] < shortest)	//checks to see if the node has been visited 
			{
				shortest = shortestDist[ i ];	//sets the shortest distance in the shortestDist array at the current node if it is less than what is already in the array 
				startNode = i;	//startNode is updated to the spot in the array..not the weight. 
			}
		}
		if( shortest == Integer.MAX_VALUE ) //base case for recursion
			return shortestDist; 
		else
			return shortest( numNodes,matrix,visited,shortestDist,startNode, paths ); //return the shortest path to get to each node
	}

	//Checks to see if the graph is metric (completely connected and obeys the triangle inequality) 
	public static boolean isMetric( int[][] matrix, int numNodes ) 
	{
		if( !completelyConnected( matrix, numNodes ))
		{
			System.out.println( "Graph is not metric: Graph is not completely connected." );
			return false;
		}
		else if( completelyConnected( matrix, numNodes ) && !triangleInequality( matrix, numNodes ))
		{
			System.out.println( "Graph is not metric: Edges do not obey the triangle inequality." );
			return false;
		}
		else
		{
			System.out.println( "Graph is metric." );	
			return true;
		}
	}

	/*check to see if the graph obeys triangle inequality. If there is a path that is a shorter distance than the direct path than 
	 *it does not obey the triangle inequality theorem 
	 */
	public static boolean triangleInequality( int[][] matrix, int numNodes )
	{
		int i,j;
		boolean[] visited = new boolean[ numNodes ];
		int[] shortestDist = new int[ numNodes ];

		for( j = 0; j < numNodes; j++)	
		{
			visited[ j ] = false;
			shortestDist[ j ] = Integer.MAX_VALUE;
		}

		for( i = 0; i < numNodes; i++ )
		{
			visited[ i ] = true;
			shortestDist[ i ] = 0;
			shortestDist = triangleCheck( matrix, numNodes, visited, shortestDist, i ); 
			//check shortestDist against matrix
			for( j = 0; j < numNodes; j++ )
			{
				if( shortestDist[ j ] < matrix[ i ][ j ] ) //if the path found is shorter than the direct path it doesn't follow the triangle inequality
					return false;

				visited[ j ] = false; 
				shortestDist[ j ] = Integer.MAX_VALUE;
			}
		}
		return true;
	}

	public static int[] triangleCheck( int[][] matrix, int numNodes,boolean[] visited, int[] shortestDist, int startNode )  
	{

		int shortest, i;
		visited[ startNode ] = true;

		//If the shortestDist value is smaller than what is currently in that spot in the array than the array[i] is updated to the smaller distance.  
		for( i = 0; i < numNodes; i++ )
			if( ( matrix[ startNode ][ i ] + shortestDist[ startNode ] ) < shortestDist[ i ] && matrix[ startNode ][ i ] != 0 )
				shortestDist[ i ] = matrix[ startNode ][ i ] + shortestDist[ startNode ];

		shortest = Integer.MAX_VALUE;
		for( i = 0; i < numNodes; i++ )
			if(visited[ i ] != true && shortestDist[ i ] < shortest)	//checks to see if the node has been visited 
			{
				shortest = shortestDist[ i ];	//sets the shortest distance in the shortestDist array at the current node if it is less than what is already in the array 
				startNode = i;	//startNode is updated to the spot in the array..not the weight. 
			}

		if( shortest == Integer.MAX_VALUE ) //base case for recursion
			return shortestDist; 
		else
			return triangleCheck( matrix, numNodes, visited, shortestDist, startNode ); //return the shortest path to get to each node	
	}

	//check to see if the graph is completely connected(all nodes connect to each other)
	public static boolean completelyConnected( int[][] matrix, int numNodes )
	{	
		int i,j;

		for( i = 0; i < numNodes; i++ )  
			for( j = 0; j < numNodes; j++ ) 
				if( matrix[ i ][ j ] == 0 && i != j )
					return false;
		return true;
	}

	//If a graph is not metric (completely connected and obey triangle inequality) this method makes the graph metric (completely connected and obeying the triangle inequality) 
	public static int[][] makeMetric( int[][] matrix, int numNodes )
	{

		int i,j;
		int[] shortestDist = new int[ numNodes ];
		boolean[] visited = new boolean[ numNodes ];

		for( i = 0; i < numNodes; i++ ) 
		{
			for( j = 0; j < numNodes; j++ )	 
			{
				visited[ j ] = false;
				shortestDist[ j ] = Integer.MAX_VALUE;
			}

			visited[ i ] = true; 
			shortestDist[ i ] = 0;
			shortestDist = triangleCheck( matrix, numNodes,visited, shortestDist, i);

			for( j = 0; j < numNodes; j++ )
			{
				if( matrix[ i ][ j ] == 0 && i != j ) 	//if there is not a connection to a node
					matrix[ i ][ j ] = shortestDist[ j ];	//set the connection to the shortest distance 

				if( shortestDist[ j ] < matrix[ i ][ j ] )	//check to make sure that it obeys the triangle inequality 
					matrix[ i ][ j ] = shortestDist[ j ];	
			}
		}
		System.out.println(adjacencyMatrixtoString( matrix, numNodes ));

		return matrix;
	}


	public static void TSP( int[][] matrix, int numNodes )
	{
		int counter = 0, startingNode = 0;
		boolean[] visited = new boolean[ numNodes ];
		int[] current = new int[numNodes + 1];
		int[] best = new int[numNodes + 1];
		best[best.length - 1] = Integer.MAX_VALUE;

		findTSP(matrix, numNodes, 0, visited, current, best,counter);

		if(best[best.length -2] != 0)
		{
			System.out.print("Error: Graph has no tour");
		}
		else
		{
			System.out.print("\n\n"+best[best.length -1] + ": "+startingNode+" -> ");
			for(int i = 0; i < best.length - 2; i++)
				System.out.print( best[ i ] + " -> " );
			System.out.println(best[best.length - 2]+"\n\n");
		}
	}

	public static void findTSP(int[][] matrix, int numNodes, int currentNode, boolean[] visited,int[] current, int[] best,int counter)
	{
		int i = 0, j = 0;

		for( i = 0; i < numNodes; ++i)
			if( visited[ i ] == true)
				j++;

		//System.out.println( visited.length + " " + i + " " + currentNode);
		if( j == visited.length - 1 ) //everything is visited
			if( matrix[ currentNode ][ 0 ] != 0 ) //can we return back to the start node 0? If there is a connection than move on
			{
				current[ currentNode ] = 0; //put the weight/connection in the current array
				current[current.length - 1] += matrix[ currentNode ][ 0 ]; //Updating the weight of tour..last spot in the array is the total weight of the tour
				if( current[ current.length - 1 ] < best[ best.length - 1 ] ) //if the weight of the current tour is < the weight of the best tour
					for( j = 0; j < current.length; j++)
						best[j] = current[j]; //update the best tour
				else if( best[best.length - 1 ] == 0)
					for( j = 0; j < current.length; j++)
						best[j] = current[j]; //update the best tour
			}

		visited[ currentNode ] = true;

		for( i = 0; i < numNodes; i++ ) //can you keep going?
		{
			if(matrix[ currentNode ][ i ] != 0 && visited[ i ] != true)
			{ 
				current[currentNode] = i;
				counter = currentNode;
				current[current.length-1] += matrix[currentNode][i];
				findTSP( matrix, numNodes, i, visited, current, best,counter); //if yes...find another possible tour
			}
		}

		counter++;

		visited[ currentNode ] = false;
	}
	
	//Interface for approximating the TSP
	public static void a_TSP(int[][] matrix, int numNodes)
	{
		//First we must check to see if the graph is metric before approximating the TSP
		if(isMetric(matrix,numNodes))
		{
			//Set a variable for our starting node.
			int startingNode = 0;
			
			boolean[] visitedNodes = new boolean[numNodes];
			
			//Here we make a new matrix which will now be the minimum spanning tree
			//of the metric graph that was provided.
			int[][] MST_TSP = minSpanningTree(numNodes,matrix );
			
			approximateTSP(MST_TSP, startingNode,numNodes,visitedNodes);
		}
		
		//If the graph is not metric, then we cannot proceed.
		else
			System.out.print("Error: Graph is not metric.");

	}
	
	//Method for approximating the TSP
	public static void approximateTSP(int[][] MST_TSP, int startingNode,int numNodes, boolean[] visitedNodes)
	{	
		//We make a stack to hold the values of visited nodes.
		Stack<Integer> stack = new Stack<Integer>();
		List<Integer> list = new ArrayList<Integer>();

		//Since we're starting at the 'starting node' 
		//we push it onto the stack and mark it as true so we do not
		//return to this node when entering the depth first traversal.
		stack.push(startingNode);
		visitedNodes[startingNode] = true;
		
		//Since the starting node was 'visited' we can document the path.
		list.add(startingNode);

		//This loop will continuously traverse nodes based off of what we pushed onto the stack
		//Initially, node 0 is placed first, then the next nodes
		//according to the depth first search below.
		//The loop will exit once all nodes have been visited.
		while(!stack.isEmpty())
		{
			int topMost_Visited, nextNode;

			//Create a variable which will hold the topmost value
			//on the stack so that we have a way of checking what we
			//can/cannot visit.
			topMost_Visited = (stack.peek()).intValue();

			//Here we need to find the next unvisited node to traverse to, there is a method which
			//handles this
			nextNode = findUnvisitedNodes(topMost_Visited, MST_TSP,numNodes,visitedNodes);
			
			//***SEE findUnvisitedNodes(); METHOD***
			//The next adjacent node in the matrix hasn't been visited, this will
			//be the node that we traverse to next.
			if(nextNode != -1)
			{
				//flag the node as being visited once we visit it.
				visitedNodes[nextNode] = true;
				
				//Here we document the node that we traversed to.
				list.add(nextNode);

				//Add this node to the stack since we visited it.
				stack.push(nextNode);

			}
			else
				//We pop off items from the stack only when we've traversed all nodes
				//thus breaking us out of the while loop.
				stack.pop();
		}


		//print out the path.
		for(int i = 0; i < list.size() -1 ; i++)
			System.out.print( list.get(i) + " -> " );
		System.out.println(list.get(list.size()-1)+"\n\n");
		
		//Call a method to loop back through out visited boolean array and clear it out for future use.
		unvisit(visitedNodes);
		//Min tree //Depth 1st search //reorganize path based on location //print path array
	}

	//Method for finding the next unvisited node in the matrix
	public static int findUnvisitedNodes(int n, int[][] MST_TSP,int numNodes,boolean[] visitedNodes)
	{
		for(int i = 0; i < numNodes; i++)
		{
			//We need to check if the number is greater than '0' because 0's mean
			//that no edge exists between two nodes at that location.
			if(MST_TSP[n][i] > 0)
			{
				//If the value we find hasn't been visited, this will be our 
				//nextNode variable to traverse to in the DF-search.
				if(!visitedNodes[i])
				{
					//return the value we found.
					return(i);
				}
			}
		}
		//We return -1 indicating that all nodes were already visited.
		return -1;
	}

	public static void unvisit(boolean[] visitedNodes)
	{
		for(int i = 0; i < visitedNodes.length; i++ )
		{
			visitedNodes[i] = false;
		}
	}
}


