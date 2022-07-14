package nl.tsmeele.compiler;

/**
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
	
	public boolean equals(Object obj) {
		Variable v = (Variable) obj;
		return nameSpace.equals(v.nameSpace) && name.equals(v.name);
	}
	
}
