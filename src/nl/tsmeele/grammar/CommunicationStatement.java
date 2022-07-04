package nl.tsmeele.grammar;


import nl.tsmeele.compiler.AST;
import nl.tsmeele.compiler.CodeGenerator;
import nl.tsmeele.compiler.NameSpace;
import nl.tsmeele.compiler.Symbol;
import nl.tsmeele.compiler.Term;
import nl.tsmeele.compiler.TokenList;
import nl.tsmeele.generator.common.StackableFunctionType;

/**
 * Synatx  'from' <participant> 'to' <participant <withClause> <message> ';'
 * @author Ton Smeele
 *
 */
public class CommunicationStatement extends Term {
	private Symbol fromRole = null;
	private Symbol toRole = null;
	
	public String detailInfo() {
		return fromRole == null || toRole == null ? "" : "from: " + fromRole.getName() + 
				"  to: " + toRole.getName();
	}
	
	@Override
	public AST executeParse(TokenList tokens)  {
		AST ast = initTree();
		tokens.headTestAndPoll(TokenType.FROM);
		fromRole = tokens.headTestAndPoll(TokenType.IDENTIFIER).toSymbol(NameSpace.PARTICIPANTS);
		tokens.headTestAndPoll(TokenType.TO);
		toRole = tokens.headTestAndPoll(TokenType.IDENTIFIER).toSymbol(NameSpace.PARTICIPANTS);
		ast.addChild(parse(new WithClause(),tokens));
		ast.addChild(parse(new IrodsMessage(),tokens));
		tokens.headTestAndPoll(TokenType.SEMICOLON);
		return ast;
	}

	@Override
	public void executeAnalysis(AST ast) {
		ast.testSymbolExists(fromRole);
		ast.testSymbolExists(toRole);
	}

	@Override
	public void executeEvaluateEnter(AST ast, CodeGenerator code) {
		code.pushAsVariable(ast, fromRole);
		code.pushAsVariable(ast, toRole);
		code.pushAsVariable(ast, SYMBOL_BLOCK);	// identify parallel execution thread
		return;
	}
	
	@Override
	public void executeEvaluateExit(AST ast, CodeGenerator code) {
		code.push(code.createFunction(StackableFunctionType.COMMUNICATION));
		code.popValue();	// applies the function
	}
	

}
