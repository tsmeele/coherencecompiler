package nl.tsmeele.grammar;

import nl.tsmeele.compiler.TermOfTerms;

public class RoleCondition extends TermOfTerms {
	
	@SuppressWarnings("rawtypes")
	public RoleCondition() {
		super();
		Class[] rule = {Role.class, AttributeConditionBracketed.class};
		addDefaultRule(rule);
		addFunction(StackableFunctionType.ROLE_CONDITION);
	}

	

	
}
