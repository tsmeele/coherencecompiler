package nl.tsmeele.generator.mcrl2;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.tsmeele.compiler.AST;
import nl.tsmeele.compiler.CodeGenerator;
import nl.tsmeele.compiler.NameSpace;
import nl.tsmeele.compiler.StackableFunction;
import nl.tsmeele.compiler.Symbol;
import nl.tsmeele.compiler.Value;
import nl.tsmeele.compiler.Variable;
import nl.tsmeele.generator.common.StackableFunctionType;

public class Mcrl2Generator extends CodeGenerator {
	static int idNo = 0;
	

	
	
	@Override
	public void beginEvaluation(AST ast) {
		System.out.println();
	}
	
	@Override
	public void endEvaluation(AST ast) {
		System.out.println();
		getRoles();
		getObjects();
	}
	
	
	@Override
	public StackableFunction createFunction(StackableFunctionType type) {
		switch (type) {
		case PROTOCOL: return new FunctionProtocol();
		case COMMUNICATION: return new FunctionCommunication();
		case ATOMIC_ENTER: return new FunctionAtomicEnter();
		case ATOMIC_EXIT: return new FunctionAtomicExit();
		}
		return null;
	}

	public ArrayList<String> getRoles() {
		ArrayList<String> result = new ArrayList<String>();
		System.out.println("List of the roles:");
		for (Variable v : variableList) {
			if (v.getSymbol().getNameSpace() != NameSpace.PARTICIPANTS) continue;
			System.out.println(v);
		}
		return result;
	}
	
	public Set<String> getObjects() {
		Set<String> result = new HashSet<String>();
		System.out.println("List of the objects:");
		for (Variable v : variableList) {
			Symbol symbol = v.getSymbol();
			Value value = v.getValue();
			if (symbol.getNameSpace() != NameSpace.OPERATIONS ||
				value == null) continue;
			result.add(value.getString());
		}
		System.out.println(result);
		return result;
	}
	

	
	
}
