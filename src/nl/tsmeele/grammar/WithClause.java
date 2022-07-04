package nl.tsmeele.grammar;

import nl.tsmeele.compiler.AST;
import nl.tsmeele.compiler.CodeGenerator;
import nl.tsmeele.compiler.Term;
import nl.tsmeele.compiler.TokenList;
import nl.tsmeele.compiler.Value;

/**
 * Syntax  'with' <IntegerExpression> 'threads' | <empty>
 * @author Ton Smeele
 *
 */
public class WithClause extends Term {
		
	@Override
	public AST executeParse(TokenList tokens) {
		AST ast = initTree();
		if (!tokens.head(TokenType.WITH)) return ast;	// no "with" specified, we will use default
		tokens.headTestAndPoll(TokenType.WITH);
		ast.addChild(parse(new IntegerExpression(),tokens)); 
		tokens.headTestAndPoll(TokenType.THREADS);
		return ast;
	}


	@Override
	public void executeAnalysis(AST ast) {		
	}


	@Override
	public void executeEvaluateExit(AST ast, CodeGenerator code) {
		if (ast.countChildren() == 0 ) {
			// no WITH specified, use default value: 1 thread
			code.push(new Value(1));
		}	
	}

}
