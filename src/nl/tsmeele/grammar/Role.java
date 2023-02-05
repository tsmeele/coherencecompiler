package nl.tsmeele.grammar;

import nl.tsmeele.compiler.TermOfTerms;

public class Role extends TermOfTerms {

	@SuppressWarnings("rawtypes")
	public Role() {
		super();
		Class[] roleRule = { RoleName.class, DottedAttribute.class };
		addDefaultRule(roleRule);
		addFunction(StackableFunctionType.ROLE);
	}
}
