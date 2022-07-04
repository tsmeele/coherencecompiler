package nl.tsmeele.grammar;

import nl.tsmeele.compiler.AST;
import nl.tsmeele.compiler.CodeGenerator;
import nl.tsmeele.compiler.ParseException;
import nl.tsmeele.compiler.Term;
import nl.tsmeele.compiler.TokenList;

public class ParallelBlock extends Term {

	@Override
	public AST executeParse(TokenList tokens) throws ParseException {
		AST ast = initTree();
		tokens.headTestAndPoll(TokenType.PARALLEL);
		ast.addChild(parse(new StatementBlock(), tokens));
		tokens.headTestAndPoll(TokenType.AND);
		ast.addChild(parse(new StatementBlock(), tokens));
		//tokens.headTestAndPoll(TokenType.SEMICOLON);
		return ast;
	}

	@Override
	public void executeAnalysis(AST ast) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void executeEvaluateEnter(AST ast, CodeGenerator code) {
		// TODO Auto-generated method stub
		
	}

}
