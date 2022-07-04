package nl.tsmeele.grammar;

import nl.tsmeele.compiler.AST;
import nl.tsmeele.compiler.Term;
import nl.tsmeele.compiler.TokenList;

public class Statements extends Term {

	@Override
	public AST executeParse(TokenList tokens) {
		AST ast = initTree();
		while (!tokens.head(TokenType.CURLYCLOSE)) {
			ast.addChild(parse(new Statement(), tokens));
		}
		return ast;
	}

	@Override
	public void executeAnalysis(AST ast) {
		// TODO Auto-generated method stub
		
	}

}
