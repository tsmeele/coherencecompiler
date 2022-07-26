package nl.tsmeele.grammar;

import nl.tsmeele.compiler.TermOfTerms;

public class IntegerExpression extends TermOfTerms {

	@SuppressWarnings("rawtypes")
	public IntegerExpression() {
		super();
		Class[] rule  = {IntegerOperand.class, IntegerOperations.class};
		addDefaultRule(rule);	
	}

}
