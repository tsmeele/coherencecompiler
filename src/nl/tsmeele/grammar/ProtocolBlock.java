package nl.tsmeele.grammar;

import nl.tsmeele.compiler.AST;
import nl.tsmeele.compiler.CodeGenerator;
import nl.tsmeele.compiler.ParseException;
import nl.tsmeele.compiler.Term;
import nl.tsmeele.compiler.TokenList;

/**
 * Syntax  '{' <parallelStatement> '}' | '{' <statement>...  '}'
 * @author Ton Smeele
 *
 */
public class ProtocolBlock extends Term {
	
	@Override
	public AST executeParse(TokenList tokens) throws ParseException {
		AST ast = initTree();
		if (tokens.head(TokenType.PARALLEL)) {
			ast.addChild(parse(new ParallelBlock(), tokens) );
			return ast;
		}
		// no parallel statement, threat as regular statement block
		ast.addChild(parse(new StatementBlock(), tokens));
		return ast;
	}

	@Override
	public void executeAnalysis(AST ast) {	
		ast.addSymbol(SYMBOL_BLOCK );
	}


	@Override
	public void executeEvaluateEnter(AST ast, CodeGenerator code) {
		// TODO Auto-generated method stub
		
	}

}
