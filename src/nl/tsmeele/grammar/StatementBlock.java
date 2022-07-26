package nl.tsmeele.grammar;


import nl.tsmeele.compiler.Term;

public class StatementBlock extends Term {

	@SuppressWarnings("rawtypes")
	public StatementBlock() {
		super();
		Class[] rule = {Statements.class};
		addRule(TokenType.CURLYOPEN, rule);
		closingTokenType = TokenType.CURLYCLOSE;
		addFunction(StackableFunctionType.STATEMENT_BLOCK);
	}
	

}
