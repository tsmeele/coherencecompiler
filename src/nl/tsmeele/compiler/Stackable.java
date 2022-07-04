package nl.tsmeele.compiler;

public abstract class Stackable {
	
	protected enum StackItemType {
		VALUE, VARIABLE, FUNCTION;
	}
	
	private StackItemType type = null;
	
	public Stackable(StackItemType type) {
		this.type = type;
	}
	
	
	
	public boolean isValue() {
		return type == StackItemType.VALUE;
	}
	
	public boolean isVariable() {
		return type == StackItemType.VARIABLE;
	}
	
	public boolean isFunction() {
		return type == StackItemType.FUNCTION;
	}
	

	

}
