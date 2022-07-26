package nl.tsmeele.compiler;

/**
 * Variable keeps information on a variable (namespace::name) and its content.
 * Each variable instance is assigned a unique sequence number to assist code
 * generators to produce unique target names.
 *
 * @author Ton Smeele
 *
 */
public class Variable extends Stackable {
	private static int seq = 0;
	private int id = 0;
	private String nameSpace = null;
	private String name = null;
	private Value value = null;

	public Variable(String nameSpace, String name) {
		super(StackItemType.VARIABLE);
		this.id = seq++;
		this.nameSpace = nameSpace;
		this.name = name;
	}

	public String toString() {
		return name;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getNameSpace() {
		return nameSpace;
	}

	public void setValue(Value value) {
		this.value = value;
	}

	public Value getValue() {
		return value;
	}
	
	@Override
	public boolean equals(Object obj) {
		return getClass() == obj.getClass() &&
				nameSpace.equals( ((Variable)obj).nameSpace) && name.equals( ((Variable)obj).name);
	}

}
