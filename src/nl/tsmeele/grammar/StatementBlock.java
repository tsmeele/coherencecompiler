package nl.tsmeele.grammar;


import nl.tsmeele.compiler.Term;

public class StatementBlock extends Term {

	@SuppressWarnings("rawtypes")
	public StatementBlock() {
		super();
		Class[] c = {Statement.class, MoreStatements.class};
		addRule(TokenType.CURLYOPEN, c);
		closingTokenType = TokenType.CURLYCLOSE;
	}
	

}
