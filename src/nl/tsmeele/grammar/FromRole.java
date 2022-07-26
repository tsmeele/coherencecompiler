package nl.tsmeele.grammar;

import nl.tsmeele.compiler.Term;

public class FromRole extends Term {

	@SuppressWarnings("rawtypes")
	public FromRole() {
		super();
		Class[] fromRule = {Role.class};
		addRule(TokenType.TEXT_FROM, fromRule);
		variableRequiresPriorDeclaration=true;
	}
}
