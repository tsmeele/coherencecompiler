package nl.tsmeele.grammar;

import nl.tsmeele.compiler.Term;
import nl.tsmeele.generator.common.StackableFunctionType;

public class CoherentStatement extends Term {

	public CoherentStatement() {
		super();
		Class[] rule = {CoherentObject.class, MoreCoherentObjects.class};
		addRule(TokenType.TEXT_COHERENT, rule);
	}
}
