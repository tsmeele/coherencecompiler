package nl.tsmeele.grammar;


import nl.tsmeele.compiler.Term;

public class ParallelCloseClause extends Term {
	
	public ParallelCloseClause() {
		super();
		addRule(TokenType.SEMICOLON, null);
	}
}
