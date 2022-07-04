package nl.tsmeele.grammar;

import nl.tsmeele.compiler.AST;
import nl.tsmeele.compiler.CodeGenerator;
import nl.tsmeele.compiler.NameSpace;
import nl.tsmeele.compiler.Symbol;
import nl.tsmeele.compiler.Term;
import nl.tsmeele.compiler.TokenList;
import nl.tsmeele.generator.common.StackableFunctionType;

public class AtomicStatement extends Term {
	private Symbol operation = null;

	@Override
	public AST executeParse(TokenList tokens) {
		AST ast = initTree();
		tokens.headTestAndPoll(TokenType.ATOMIC);
		operation = tokens.headTestAndPoll(TokenType.IDENTIFIER).toSymbol(NameSpace.OPERATIONS);
		tokens.headTestAndPoll(TokenType.CURLYOPEN);
		ast.addChild(parse(new Statements(), tokens));
		tokens.headTestAndPoll(TokenType.CURLYCLOSE);
		return ast;
	}

	@Override
	public void executeAnalysis(AST ast) {	
		if (ast.findSymbol(operation) == null) {
			ast.addSymbol(operation);
		}
	}
	
	@Override
	public void executeEvaluateEnter(AST ast, CodeGenerator code) {
		code.registerAsVariable(operation);
		code.pushAsVariable(ast, operation);
		code.push(code.createFunction(StackableFunctionType.ATOMIC_ENTER));
		code.popValue();	// applies the function
	}
	
	@Override
	public void executeEvaluateExit(AST ast, CodeGenerator code) {
		code.pushAsVariable(ast, operation);
		code.push(code.createFunction(StackableFunctionType.ATOMIC_EXIT));
		code.popValue();	// applies the function
	}

}
