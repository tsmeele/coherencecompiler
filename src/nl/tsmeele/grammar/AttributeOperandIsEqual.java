package nl.tsmeele.grammar;

import nl.tsmeele.compiler.Term;

public class AttributeOperandIsEqual extends Term {
	
	@SuppressWarnings("rawtypes")
	public AttributeOperandIsEqual() {
		super();
		Class[] rule = {Attribute.class};
		addRule(TokenType.ISEQUAL, rule);
		addFunction(StackableFunctionType.ATTRIBUTES_IS_EQUAL);
	}
}
