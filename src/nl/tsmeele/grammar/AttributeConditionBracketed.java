package nl.tsmeele.grammar;

import nl.tsmeele.compiler.Term;

public class AttributeConditionBracketed extends Term {
	
	@SuppressWarnings("rawtypes")
	public AttributeConditionBracketed( ) {
		super();
		Class[] rule1 = {Attribute.class, AttributeOperandIsEqual.class};
		addRule(TokenType.BRACKETOPEN, rule1);
		closingTokenType = TokenType.BRACKETCLOSE;
	}

}
