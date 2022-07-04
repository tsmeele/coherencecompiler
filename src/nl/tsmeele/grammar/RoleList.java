package nl.tsmeele.grammar;

import java.util.ArrayList;

import nl.tsmeele.compiler.AST;
import nl.tsmeele.compiler.CodeGenerator;
import nl.tsmeele.compiler.NameSpace;
import nl.tsmeele.compiler.ParseException;
import nl.tsmeele.compiler.Symbol;
import nl.tsmeele.compiler.Term;
import nl.tsmeele.compiler.Token;
import nl.tsmeele.compiler.TokenList;
import nl.tsmeele.compiler.Variable;

public class RoleList extends Term {
	private ArrayList<Symbol> roles = new ArrayList<Symbol>();

	public String detailInfo() {
		return roles.isEmpty() ? "" : roles.toString(); 
	}
	
	@Override
	public AST executeParse(TokenList tokens) throws ParseException {
		AST ast = initTree();
		tokens.headTestAndPoll(TokenType.BRACKETOPEN);
		// first participant, mandatory
		roles.add(tokens.headTestAndPoll(TokenType.IDENTIFIER).toSymbol(NameSpace.PARTICIPANTS)); 
		tokens.headTestAndPoll(TokenType.COMMA);
		// second participant, mandatory
		roles.add(tokens.headTestAndPoll(TokenType.IDENTIFIER).toSymbol(NameSpace.PARTICIPANTS)); 
		while (tokens.head(TokenType.COMMA)) {
			tokens.pollFirst();
			roles.add(tokens.headTestAndPoll(TokenType.IDENTIFIER).toSymbol(NameSpace.PARTICIPANTS)); 
		}
		tokens.headTestAndPoll(TokenType.BRACKETCLOSE);
		return ast;
	}

	@Override
	public void executeAnalysis(AST ast) {
		for (Symbol role : roles) {
			// participant is implicitly defined if not already defined outside current protocol
			if (ast.findSymbol(role) == null) {
				ast.addSymbol(role);
			}
		}
		
	}

	@Override
	public void executeEvaluateEnter(AST ast, CodeGenerator code) {
		for (Symbol role : roles) {
			code.registerAsVariable(role);
		}
	}



}
