package nl.tsmeele.compiler;

/**
 * Stackable objects can be pushed on/pulled from a stack (provided by a code
 * generator). Subclasses are required to specify a stack item type in their
 * constructor.
 * 
 * @author Ton Smeele
 *
 */
public abstract class Stackable {

	protected enum StackItemType {
		VALUE, VARIABLE, FUNCTION;
	}

	private StackItemType type = null;

	public Stackable(StackItemType type) {
		this.type = type;
	}

	public StackItemType getType() {
		return type;
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
