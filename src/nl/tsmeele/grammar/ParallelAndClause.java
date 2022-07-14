package nl.tsmeele.grammar;


import nl.tsmeele.compiler.Term;

public class ParallelAndClause extends Term {
	
	@SuppressWarnings("rawtypes")
	public ParallelAndClause() {
		super();
		Class[] c = {Protocol.class};
		addRule(TokenType.TEXT_AND, c);
	}

}
