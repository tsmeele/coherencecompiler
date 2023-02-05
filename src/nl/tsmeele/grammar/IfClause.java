package nl.tsmeele.grammar;

import nl.tsmeele.compiler.NonConsumingTerm;

public class IfClause extends NonConsumingTerm {
	
	@SuppressWarnings("rawtypes")
	public IfClause() {
		super();
		
		Class[] stringRule = {StringOperandLiteral.class};
		addRule(TokenType.DQSTRINGLITERAL, stringRule);	
		
		Class[] roleconditionRule = {RoleCondition.class};
		addRule(TokenType.IDENTIFIER, roleconditionRule);
	}

}
