package nl.tsmeele.grammar;

import nl.tsmeele.compiler.AST;
import nl.tsmeele.compiler.CodeGenerator;
import nl.tsmeele.compiler.Term;
import nl.tsmeele.compiler.Token;
import nl.tsmeele.compiler.TokenList;
import nl.tsmeele.generator.common.FunctionAddInteger;
import nl.tsmeele.generator.common.FunctionSubtractInteger;

public class IntegerExpression extends Term {
	private final TokenType[] operators = 
		{TokenType.PLUS, TokenType.MINUS };
	
	private Token operator = null; 
	
	
	public String detailInfo() {
		return operator == null ? "" : operator.getSource();
	}
	
	@Override
	public AST executeParse(TokenList tokens) {
		AST ast = initTree();
		ast.addChild(parse(new IntegerOperand(), tokens));	// first operand
		if (tokens.isEmpty() || !isOperator(tokens.get(0).getType()) )
				// single operand expression
				return ast; 
		operator = tokens.pollFirst();
		ast.addChild(parse(new IntegerOperand(), tokens));  // second operand
		return ast;
	}

	@Override
	public void executeAnalysis(AST ast) {		
	}

	
	
	private boolean isOperator(TokenType tt) {
		for (int i = 0; i < operators.length; i++) {
			if (tt == operators[i]) return true;
		}
		return false;
	}


	public void executeEvaluateExit(AST ast, CodeGenerator code) {
		// evaluate the expression and calculate resulting value
		// the expression has either one or two operands
		if (operator == null) {
			// no operator present, must be single operand expression
			// operand has already pushed the value, no further action needed
			return;
		}
		switch(operator.getType()) {
			case PLUS: {code.push(new FunctionAddInteger()); return;}
			case MINUS: {code.push(new FunctionSubtractInteger());; return;}
			default: return;
		}
	}
	
	
	
}
