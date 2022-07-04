package nl.tsmeele.grammar;

import nl.tsmeele.compiler.AST;
import nl.tsmeele.compiler.CodeGenerator;
import nl.tsmeele.compiler.NameSpace;
import nl.tsmeele.compiler.Symbol;
import nl.tsmeele.compiler.Term;
import nl.tsmeele.compiler.Token;
import nl.tsmeele.compiler.TokenList;
import nl.tsmeele.generator.common.StackableFunctionType;

/**
 *  <Protocol> = 'protocol' <identifier>  <rolelist> <block>  
 * @author Ton Smeele
 *
 */
public class Protocol extends Term {
	private Symbol protocolName = null;
	
	public String detailInfo() {
		return protocolName == null ? "" : protocolName.getName() + " " + listSymbols(); 
	}

	@Override
	public AST executeParse(TokenList tokens) {
		AST ast = initTree();
		createSymbolScope();
		tokens.headTestAndPoll(TokenType.PROTOCOL);
		protocolName = tokens.headTestAndPoll(TokenType.IDENTIFIER).toSymbol(NameSpace.PROTOCOLS);
		ast.addChild(parse(new RoleList(), tokens));
		ast.addChild(parse(new ProtocolBlock(), tokens));
		return ast;
	}

	@Override
	public void executeAnalysis(AST ast) {
		ast.testSymbolNotExistsAndAdd(protocolName);
	}

	@Override
	public void executeEvaluateEnter(AST ast, CodeGenerator code) {
		code.registerAsVariable(protocolName);
		code.pushAsVariable(ast, protocolName);
		code.push(code.createFunction(StackableFunctionType.PROTOCOL));
		code.popValue();	// applies the function
	}
		


}
