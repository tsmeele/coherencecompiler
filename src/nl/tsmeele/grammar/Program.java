package nl.tsmeele.grammar;


import nl.tsmeele.compiler.NonConsumingTerm;

public class Program extends NonConsumingTerm {

	@SuppressWarnings("rawtypes")
	public Program() {
		super();
		createVariableTable();
		Class[] rule1 = {Protocol.class};
		addRule(TokenType.TEXT_PROTOCOL, rule1);
		Class[] rule2 = {ParallelStatement.class};
		addRule(TokenType.TEXT_PARALLEL, rule2);
	}

}
