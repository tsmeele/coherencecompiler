package nl.tsmeele.grammar;

import nl.tsmeele.compiler.Term;

public class ProtectsRole extends Term {

	@SuppressWarnings("rawtypes")
	public ProtectsRole() {
		super();
		Class[] toRule = {Role.class};
		addRule(TokenType.TEXT_PROTECTS, toRule);
		variableRequiresPriorDeclaration=true;
	}

}
