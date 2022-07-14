package nl.tsmeele.grammar;


import nl.tsmeele.compiler.Term;

public class ParallelStatement extends Term {

	@SuppressWarnings("rawtypes")
	public ParallelStatement() {
		super();
		Class[] c = {Protocol.class, ParallelAndClause.class, ParallelCloseClause.class};
		addRule(TokenType.TEXT_PARALLEL, c);
	}

}
