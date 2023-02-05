package nl.tsmeele.grammar;

import nl.tsmeele.compiler.Term;

public class StringOperandLiteral extends Term {

	public StringOperandLiteral() {
		super();
		addRule(TokenType.DQSTRINGLITERAL, null);
	}
	
}
