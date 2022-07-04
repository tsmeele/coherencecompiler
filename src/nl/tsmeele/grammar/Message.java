package nl.tsmeele.grammar;


import nl.tsmeele.compiler.AST;
import nl.tsmeele.compiler.CodeGenerator;
import nl.tsmeele.compiler.Term;
import nl.tsmeele.compiler.Token;
import nl.tsmeele.compiler.TokenList;
import nl.tsmeele.compiler.Value;

/**
 * Syntax:  <IntegerExpression> | <doubleQuotedLiteralString>
 * @author Ton Smeele
 *
 */
public class Message extends Term {
	private Token message = null;
	
	public String detailInfo() {
		return message == null ? "" : message.getSource(); 
	}
	
	@Override
	public AST executeParse(TokenList tokens) {
		AST ast = initTree();
		if (tokens.at(0, TokenType.DQSTRINGLITERAL)) {
			message = tokens.headTestAndPoll(TokenType.DQSTRINGLITERAL);
			return ast;
		}
		ast.addChild(parse(new IntegerExpression(), tokens));
		return ast;
	}

	@Override
	public void executeAnalysis(AST ast) {	
	}


	
	public void executeEvaluateExit(AST ast, CodeGenerator code) {
		if (message != null) {
			// message type is literal string
			code.push(new Value(message.getSource()));
		}
	}
	


}
