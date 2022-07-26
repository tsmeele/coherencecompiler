package nl.tsmeele.grammar;

import nl.tsmeele.compiler.Term;

public class EmptyStatement extends Term {
	
	public EmptyStatement() {
		super();
		addRule(TokenType.SEMICOLON, null);
	}

}
