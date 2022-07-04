package nl.tsmeele.compiler;

/**
 * Variable is an instantiated Symbol
 * @author Ton Smeele
 *
 */
public class Variable extends Stackable {
	private static int seq = 0;
	private int id = 0;
	private Symbol symbol = null;
	private Value value = null;
	
	
	public Variable(Symbol symbol) {
		super(StackItemType.VARIABLE);
		this.id = seq++;
		this.symbol = symbol;
	}
	
	public String toString() {
		return Integer.toString(id) + "(" + symbol.getName() + ")" ;
	}
	
	public int getId() {
		return id;
	}

	public String getName() {
		return symbol.getName();
	}
	
	public Symbol getSymbol() {
		return symbol;
	}
	
	public void setValue(Value value) {
		this.value = value;
	}
	
	public Value getValue() {
		return value;
	}
	
}
