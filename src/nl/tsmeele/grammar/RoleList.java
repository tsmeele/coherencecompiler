package nl.tsmeele.grammar;


import nl.tsmeele.compiler.Term;
import nl.tsmeele.generator.common.StackableFunctionType;

public class RoleList extends Term {

	@SuppressWarnings("rawtypes")
	public RoleList() {
		super();
		Class[] c = {RoleName.class, MoreRoleNames.class};
		addRule(TokenType.BRACKETOPEN, c);	
		closingTokenType = TokenType.BRACKETCLOSE;
		addFunction(StackableFunctionType.PROTOCOL_ROLES);
	}
	

}
