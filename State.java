//State class contains all the properties of a state
//It also includes the setters and getters for all properties

public class State {
	private String name;
	private String states[];
	private boolean initialState;
	private boolean finalState;
	
	public State(String n, String [] s, boolean i, boolean f) {
		name = n;
		states = s;
		initialState = i;
		finalState = f;
	}
	
	public void setName(String n) {
		name = n;
	}
	
	public String getName() {
		return name;
	}
	
	public void setStates(String s[]) {
		states = s;
	}
	
	public String[] getStates() {
		return states;
	}
	
	public void setInitial(boolean i) {
		initialState = i;
	}
	
	public boolean isInitial() {
		return initialState;
	}
	
	public void setFinal(boolean f) {
		finalState = f;
	}
	
	public boolean isFinal() {
		return finalState;
	}
}
