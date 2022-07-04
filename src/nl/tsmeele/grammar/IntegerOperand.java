package nl.tsmeele.grammar;


import nl.tsmeele.compiler.AST;
import nl.tsmeele.compiler.CodeGenerator;
import nl.tsmeele.compiler.Term;
import nl.tsmeele.compiler.TokenList;
import nl.tsmeele.compiler.Value;

/**
 * Operand syntax:  ( <IntegerExpression> ) | <VariableIdentifier> | <IntegerLiteral>
 * @author Ton Smeele
 *
 */
public class IntegerOperand extends Term {
	private Value literal = null; 

	
	public String detailInfo() {
		return literal == null ? "" : literal.getString();
	}
	
	@Override
	public AST executeParse(TokenList tokens) {
		AST ast = initTree();
		if (tokens.head(TokenType.BRACKETOPEN)) {
			tokens.pollFirst();
			ast.addChild(parse(new IntegerExpression(), tokens));
			tokens.headTestAndPoll(TokenType.BRACKETCLOSE);
			return ast;
		}
		literal = new Value(tokens.headTestAndPoll(TokenType.INTEGERLITERAL).getSource());
		return ast;
	}

	@Override
	public void executeAnalysis(AST ast) {		
	}


	
	public void executeEvaluateExit(AST ast, CodeGenerator code) {
		// evaluate the expression and calculate resulting value
		
		// option 1: the value is held locally in a literal
		//           (and calculated during evaluation)
		if (literal != null) {
			code.push(literal);
		}
		
		// option 2: value is in an expression
		// value should have been pushed by underlying expression
	}

	
	
	


}
