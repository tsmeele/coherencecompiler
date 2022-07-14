package nl.tsmeele.grammar;

import nl.tsmeele.compiler.OptionalTerm;

public class MoreRoleNames extends OptionalTerm {

	@SuppressWarnings("rawtypes")
	public MoreRoleNames(){
		super();
		Class[] rule1 = {RoleName.class, MoreRoleNames.class};
		addRule(TokenType.COMMA, rule1);;
	}

}
