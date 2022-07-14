package nl.tsmeele.grammar;

import nl.tsmeele.compiler.Term;
import nl.tsmeele.generator.common.StackableFunctionType;

public class AtomicName extends Term {
	
	public AtomicName() {
		addRule(TokenType.IDENTIFIER, null);
		addFunction(StackableFunctionType.ATOMIC_ENTER);
	}

}
