package nl.tsmeele.grammar;

import nl.tsmeele.compiler.AST;
import nl.tsmeele.compiler.CodeGenerator;
import nl.tsmeele.compiler.Term;
import nl.tsmeele.compiler.TokenList;

public class StatementBlock extends Term {
	

	@Override
	public AST executeParse(TokenList tokens) {
		AST ast = initTree();
		createSymbolScope(); // required to scope SYMBOL_BLOCK to this block
		tokens.headTestAndPoll(TokenType.CURLYOPEN);
		ast.addChild(parse(new Statements(), tokens));
		tokens.headTestAndPoll(TokenType.CURLYCLOSE);
		return ast;
	}

	@Override
	public void executeAnalysis(AST ast) {
		ast.addSymbol(SYMBOL_BLOCK );
	}

	@Override
	public void executeEvaluateEnter(AST ast, CodeGenerator code) {
		// in case of parallel execution of statement blocks, the id of the 
		// SYMBOL_BLOCK derived variable is used to identify the execution thread
		code.registerAsVariable(SYMBOL_BLOCK);	
	}
	



}
