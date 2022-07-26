package nl.tsmeele.grammar;


import nl.tsmeele.compiler.Term;

public class ProtocolName extends Term {

	public ProtocolName() {
		super();
		addRule(TokenType.IDENTIFIER, null);
	}


}
