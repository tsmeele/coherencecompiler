package nl.tsmeele.compiler;

public abstract class StackableFunction extends Stackable {

	public StackableFunction() {
		super(StackItemType.FUNCTION);
	}
	
	public abstract Stackable apply(CodeGenerator code);
}
