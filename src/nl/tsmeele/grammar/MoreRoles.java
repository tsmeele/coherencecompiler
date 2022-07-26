package nl.tsmeele.grammar;

import nl.tsmeele.compiler.OptionalTerm;

public class MoreRoles extends OptionalTerm {

	@SuppressWarnings("rawtypes")
	public MoreRoles(){
		super();
		Class[] rule1 = {Role.class, MoreRoles.class};
		addRule(TokenType.COMMA, rule1);;
	}

}
