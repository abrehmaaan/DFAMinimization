import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class DFA {
	private int noOfSymbols;
	private String nameOfSymbols[];
	private ArrayList<State> DFAStates;
	private ArrayList<State> minDFAStates;
	
	//This function read the file, line by line and create a DFA from the data written in file
	private void readFile(String filename) {
		System.out.println("Input Transition Table: ");
		try {
	      File myObj = new File(filename);
	      Scanner myReader = new Scanner(myObj);
	      String inputSymbols = myReader.nextLine();
	      System.out.println(inputSymbols);
	      String[] arr = inputSymbols.split(", ");
	      noOfSymbols = arr.length - 1;
	      nameOfSymbols = new String[noOfSymbols];
	      for (int i = 1, j = 0; i < arr.length; i++, j++){
	    	  nameOfSymbols[j] = arr[i];
	      }
	      DFAStates = new ArrayList<State>();
	      while (myReader.hasNextLine()) {
	    	  String name;
	    	  String states[] = new String [noOfSymbols];
	    	  boolean initialState = false;
	    	  boolean finalState = false;
	    	  String data = myReader.nextLine();
	    	  System.out.println(data);
	          arr = data.split(", ");
	          if(arr[0].charAt(0) == '*') {
	        	  finalState = true;
	        	  if(arr[0].charAt(1) == 'i') {
	        		  initialState = true;
	        		  name = String.valueOf(arr[0].substring(2));
	        	  }
	        	  else {
	        		  name = String.valueOf(arr[0].substring(1));
	        	  }
	          }
	          else if(arr[0].charAt(0) == 'i') {
	        	  initialState = true;
	        	  if(arr[0].charAt(1) == '*') {
	        		  finalState = true;
	        		  name = String.valueOf(arr[0].substring(2));
	        	  }
	        	  else {
	        		  name = String.valueOf(arr[0].substring(1));
	        	  }
	          }
	          else {
        		  name = arr[0];
	          }
	          for(int i = 1, j = 0; i<=noOfSymbols;i++, j++) {
	        	  states[j] = arr[i];
	          }
	          DFAStates.add(new State(name, states, initialState, finalState));
	        }
	      myReader.close();
	    } catch (FileNotFoundException e) {
	      System.out.println("An error occurred.");
	      e.printStackTrace();
	    }
	}
	
	//This function checks each state and if it is unreachable then remove it from DFA
	public void removeUnreachableStates() {
		System.out.print("\nUnreachable States: ");
		int noOfUnreachableStates = 0;
		for(int i = 0; i < DFAStates.size(); i++) {
			if(DFAStates.get(i).isInitial()) {
				continue;
			}
			boolean unreachable = true;
			String name = DFAStates.get(i).getName();
			for(int j = 0; j < DFAStates.size(); j++) {
				String[] inputSymbols = DFAStates.get(j).getStates();
				for(String is:inputSymbols) {
					if(name.equals(is)) {
						unreachable = false;
						break;
					}
				}
				if(!unreachable) {
					break;
				}
			}
			if(unreachable) {
				noOfUnreachableStates++;
				System.out.print(DFAStates.get(i).getName()+" ");
				DFAStates.remove(i);
			}	
		}
		if(noOfUnreachableStates == 0) {
			System.out.print("No Unreachable State");
		}
		System.out.println();
	}
	
	//This function checks whether a state is moved from previous equivalence to next equivalence
	public boolean moved(State s, ArrayList<ArrayList<State>> curr) {
		for(ArrayList<State> l:curr) {
			for(State i:l) {
				if(i.getName().equals(s.getName())) {
					return true;
				}
			}
		}
		return false;
	}
	
	//This function checks whether the two states map on such states, on all input symbols, that were in the same partition in previous equivalence
	public boolean isSame(State rhs, State lhs, ArrayList<ArrayList<State>> prev) {
		String rhsStates[] = rhs.getStates();
		String lhsStates[] = lhs.getStates();
		boolean found[] = new boolean[noOfSymbols];
		for(int i = 0; i<noOfSymbols;i++) {
			found[i] = false;
		}
		for(int i = 0; i<noOfSymbols;i++) {
			for(ArrayList<State> l:prev) {
				for(State s:l) {
					if(s.getName().equals(rhsStates[i])) {
						for(State ss:l) {
							if(ss.getName().equals(lhsStates[i])) {
								found[i] = true;
							}
						}
					}
				}
			}
		}
		for(int i = 0; i<noOfSymbols;i++) {
			if(!found[i]) {
				return false;
			}
		}
		return true;
	}
	
	
	//This function returns the last equivalence after applying the partitioning method on the DFA
	public ArrayList<ArrayList<State>> partitioningMethod() {
		System.out.println("\nPartitioning Method:");
		ArrayList<ArrayList<State>> prev = new ArrayList<ArrayList<State>>();
		ArrayList<ArrayList<State>> curr = new ArrayList<ArrayList<State>>();
		ArrayList<State> nonAccepting = new ArrayList<State>();
		ArrayList<State> Accepting = new ArrayList<State>();
		for(int i = 0; i < DFAStates.size(); i++) {
			if(DFAStates.get(i).isFinal()) {
				Accepting.add(DFAStates.get(i));
			}
			else {
				nonAccepting.add(DFAStates.get(i));
			}
		}
		curr.add(nonAccepting);
		curr.add(Accepting);
		int p = 0;
		while(prev.size()!=curr.size()) {
			prev.clear();
			prev.addAll(curr);
			System.out.print(p+"-Equivalence: ");
			for(int i = 0; i<prev.size();i++) {
				ArrayList<State> s = prev.get(i);
				System.out.print("{");
				for(int j = 0; j<s.size();j++) {
					State ss = s.get(j);
					System.out.print(ss.getName());
					if(j+1!=s.size()) {
						System.out.print(",");
					}
				}System.out.print("}");
			}
			System.out.println();
			p++;
			curr.clear();
			for(ArrayList<State> l: prev) {
				for(int i = 0; i<l.size();i++) {
					ArrayList<State> set = new ArrayList<State>();
					if(!moved(l.get(i), curr)) {
						State s = l.get(i);
						set.add(s);
						for(int j = 0;j<l.size();j++) {
							if(!moved(l.get(j), curr) && !set.contains(l.get(j))) {
								if(isSame(s, l.get(j), prev))
									set.add(l.get(j));
							}
						}
						curr.add(set);
					}
				}
			}
		}
		System.out.print(p+"-Equivalence: ");
		for(int i = 0; i<curr.size();i++) {
			ArrayList<State> s = curr.get(i);
			System.out.print("{");
			for(int j = 0; j<s.size();j++) {
				State ss = s.get(j);
				System.out.print(ss.getName());
				if(j+1!=s.size()) {
					System.out.print(",");
				}
			}System.out.print("}");
		}
		System.out.println();
		return curr;
	}
	
	//This function takes the partitioned DFA and merge the states that lie in the same partition
	//The function then map the states on merge states
	public void minimizeDFA(ArrayList<ArrayList<State>> partitions) {
		minDFAStates = new ArrayList<State>();
		for(ArrayList<State> l: partitions) {
	    	State s = l.get(0);
	    	String name = s.getName();
	    	String states[] = s.getStates();
	    	boolean initialState = s.isInitial();
	    	boolean finalState = s.isFinal();
	    	for(int i = 1; i<l.size();i++) {
	    		name += l.get(i).getName();
	    		if(!initialState) {
	    			initialState = l.get(i).isInitial();
	    		}
	    		if(!finalState) {
	    			finalState = l.get(i).isFinal();
	    		}
	    	}
	    	minDFAStates.add(new State(name, states, initialState, finalState));
		}
		for(int i = 0; i<minDFAStates.size();i++) {
			State s = minDFAStates.get(i);
			String states[] = s.getStates();
			for(int j = 0; j<noOfSymbols;j++) {
				String state = states[j];
				for(State toMatch: minDFAStates) {
					if(toMatch.getName().contains(state)) {
						states[j] = toMatch.getName();
					}
				}
				minDFAStates.get(i).setStates(states);
			}
		}
	}
	
	//This function writes the minimized DFA on the output file
	public void writeFile(String filename) {
		System.out.println("\nOutput Transition Table: ");
		try {
			FileWriter myWriter = new FileWriter(filename);
			myWriter.write("States/Input Symbols, ");
			for(int i = 0; i< noOfSymbols; i++) {
				myWriter.write(nameOfSymbols[i]);
				if(i!=noOfSymbols-1) {
					myWriter.write(", ");
				}
				else {
					myWriter.write("\n");
				}
			}
			for(State s: minDFAStates) {
				if(s.isInitial()) {
					myWriter.write("i");
				}
				if(s.isFinal()) {
					myWriter.write("*");
				}
				myWriter.write(s.getName()+", ");
				String[] st = s.getStates();
				for(int i = 0; i< noOfSymbols; i++) {
					myWriter.write(st[i]);
					if(i!=noOfSymbols-1) {
						myWriter.write(", ");
					}
					else {
						myWriter.write("\n");
					}
				}
			}
			myWriter.close();
	    } catch (IOException e) {
	    	System.out.println("An error occurred.");
	    	e.printStackTrace();
	    }
		try {
		      File myObj = new File(filename);
		      Scanner myReader = new Scanner(myObj);
		      while (myReader.hasNextLine()) {
		    	  String data = myReader.nextLine();
		    	  System.out.println(data);
		        }
		      myReader.close();
		    } catch (FileNotFoundException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
	}
	
	public static void main(String[] args) {
		DFA dfa = new DFA();
		//Firstly we will read the state transition table from the input file
		dfa.readFile("input.txt");
		//The first step before minimization is to remove unreachable states
		dfa.removeUnreachableStates();
		//Using the partitioning method to return the sets from last partition
		ArrayList<ArrayList<State>> partitions = dfa.partitioningMethod();
		//To merge the states that can not be partitioned and map all the states accordingly
		dfa.minimizeDFA(partitions);
		//Writing the minimized version to the file
		dfa.writeFile("output.txt");
	}
}
