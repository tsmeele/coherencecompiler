package nl.tsmeele.grammar;

import nl.tsmeele.compiler.AST;
import nl.tsmeele.compiler.CodeGenerator;
import nl.tsmeele.compiler.NameSpace;
import nl.tsmeele.compiler.Symbol;
import nl.tsmeele.compiler.Term;
import nl.tsmeele.compiler.TokenList;
import nl.tsmeele.compiler.Value;
import nl.tsmeele.compiler.Variable;

/**
 * Syntax: <operation>  [ '(' <irodsObject> ')' ] 
 * @author Ton Smeele
 *
 */
public class IrodsMessage extends Term {
	private Symbol operation = null;
	private Symbol irodsObject = null;

	@Override
	public AST executeParse(TokenList tokens) {
		AST ast = initTree();
		operation = tokens.headTestAndPoll(TokenType.IDENTIFIER).toSymbol(NameSpace.OPERATIONS);
		if (tokens.head(TokenType.BRACKETOPEN)) {
			// process optional iRODS object specification
			tokens.headTestAndPoll(TokenType.BRACKETOPEN);
			irodsObject = tokens.headTestAndPoll(TokenType.IDENTIFIER).toSymbol(NameSpace.VARIABLES);
			tokens.headTestAndPoll(TokenType.BRACKETCLOSE);
		}
		return ast;
	}

	@Override
	public void executeAnalysis(AST ast) {
		if (ast.findSymbol(operation) == null) {
			ast.addSymbol(operation);
		}
		if (irodsObject != null && ast.findSymbol(irodsObject) == null) {
			ast.addSymbol(irodsObject);
		}		
	}
	
	public void executeEvaluateEnter(AST ast, CodeGenerator code) {
		code.registerAsVariable(operation);
		code.pushAsVariable(ast, operation);
		// the name of the irodsObject (if any) is passed as the value assigned to the operation
		if (irodsObject != null) {
			Variable varOperation = code.popVariable();
			varOperation.setValue(new Value(irodsObject.getName()));
			code.push(varOperation);
		}
	}
	
	
	public void executeEvaluateExit(AST ast, CodeGenerator code) {
		

	}
	

}
