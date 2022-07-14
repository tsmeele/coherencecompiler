package nl.tsmeele.grammar;

import nl.tsmeele.compiler.Term;

public class ToRole extends Term {

	@SuppressWarnings("rawtypes")
	public ToRole() {
		super();
		Class[] toRule = {RoleName.class};
		addRule(TokenType.TEXT_TO, toRule);
		variableRequiresPriorDeclaration=true;
	}

}
