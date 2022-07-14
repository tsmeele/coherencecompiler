package nl.tsmeele.grammar;

import nl.tsmeele.compiler.OptionalTerm;
import nl.tsmeele.generator.common.StackableFunctionType;

public class WithClause extends OptionalTerm {

	@SuppressWarnings("rawtypes")
	public WithClause() {
		super();
		Class[] rule = {IntegerExpression.class};
		addRule(TokenType.TEXT_WITH, rule);
		closingTokenType = TokenType.TEXT_THREADS;
		addFunction(StackableFunctionType.WITH_THREADS);
	}

}
