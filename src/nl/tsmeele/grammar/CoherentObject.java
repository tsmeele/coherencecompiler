package nl.tsmeele.grammar;

import nl.tsmeele.compiler.TermOfTerms;
import nl.tsmeele.generator.common.StackableFunctionType;

public class CoherentObject extends TermOfTerms {
	
	public CoherentObject() {
		super();
		Class[] rule = {IrodsObject.class};
		addDefaultRule(rule);
		addFunction(StackableFunctionType.COHERENT);
	}
}
