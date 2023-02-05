package nl.tsmeele.grammar;

import nl.tsmeele.compiler.NonConsumingTerm;

public class IntegerOperand extends NonConsumingTerm {

	@SuppressWarnings("rawtypes")
	public IntegerOperand() {
		super();
		Class[] bracketedRule = { IntegerOperandBracketed.class};
		Class[] minusRule = {IntegerOperandMinus.class};
		Class[] integerLiteralRule = {IntegerOperandLiteral.class};
		Class[] booleanLiteralRule = {BooleanOperandLiteral.class};
		addRule(TokenType.BRACKETOPEN, bracketedRule);
		addRule(TokenType.MINUS, minusRule);
		addRule(TokenType.INTEGERLITERAL, integerLiteralRule);
		addRule(TokenType.BOOLEANLITERAL, booleanLiteralRule);
		// TODO: rule3:  <variable>
		
	}

}
