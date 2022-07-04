package nl.tsmeele.grammar;

import nl.tsmeele.compiler.AST;
import nl.tsmeele.compiler.CodeGenerator;
import nl.tsmeele.compiler.ParseException;
import nl.tsmeele.compiler.Term;
import nl.tsmeele.compiler.TokenList;

public class Statement extends Term {

	@Override
	public AST executeParse(TokenList tokens) throws ParseException {
		AST ast = initTree();
		if (tokens.isEmpty()) return ast;
		TokenType tt = tokens.get(0).getType();
		switch (tt) {
		case FROM: {ast.addChild(parse(new CommunicationStatement(), tokens)); break;}
		case ATOMIC: {ast.addChild(parse(new AtomicStatement(), tokens)); break;}
		default: throw new ParseException("'Unsupported statement type", tokens);
		}
		return ast;
	}

	@Override
	public void executeAnalysis(AST ast) {		
	}

	@Override
	public void executeEvaluateEnter(AST ast, CodeGenerator code) {
		// TODO Auto-generated method stub
		
	}

}
