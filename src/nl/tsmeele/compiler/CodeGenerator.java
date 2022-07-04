package nl.tsmeele.compiler;

import java.util.ArrayList;
import java.util.Stack;

import nl.tsmeele.generator.common.StackableFunctionType;


public abstract class CodeGenerator {
	private Stack<Stackable> stack = new Stack<Stackable>();
	protected ArrayList<Variable> variableList = new ArrayList<Variable>();
	
	
	public abstract StackableFunction createFunction(StackableFunctionType type);

	
	public void beginEvaluation(AST ast) {
		// can override: hook for additional processing at start of evaluation
	}
	
	public void endEvaluation(AST ast) {
		// can override: hook for additional processing at end of evaluation
	}
	
	
	// ---------------------------------------------------------------------------
	// methods available to Term instances during the executeEvaluate phase
	
	public void registerAsVariable(Symbol symbol) {
		if (!isRegistered(symbol)) {
			variableList.add(new Variable(symbol));
		}
	}
	public boolean isRegistered(Symbol symbol) {
		for (Variable v : variableList) {
			// we match on object instance, not on content!
			// there could be symbols with similar name yet different scope
			if (v.getSymbol() == symbol) return true;
		}
		return false;
	}
	

		
	public void push(Stackable item) {
		stack.push(item);
	}
	
	public void pushAsVariable(AST ast, Symbol symbol) {
		push(getVariable(ast,symbol));
	}
	
	public Value popValue() {
		if (stack.peek() == null) {stack.pop(); return null;}
		if (stack.peek().isVariable()) return ((Variable)stack.pop()).getValue();
		if (stack.peek().isValue()) return (Value) stack.pop();
		if (stack.peek().isFunction()) {
			Stackable result = ( (StackableFunction) stack.pop()).apply(this);
			if (result == null) return null;
			stack.push(result);
			return popValue();
		}
		throw new GeneratorException("Unrecognized generator stack item");
	}
	
	public Variable popVariable() {
		if (stack.peek().isVariable()) return ((Variable)stack.pop());
		// not a Variable stackable type 
		stack.pop();
		return null;
	}
	
	public StackableFunction popFunction() {
		if (stack.peek().isFunction()) {
			return (StackableFunction) stack.pop();
		}
		// ignore other type
		stack.pop();
		return null;
	}
	
	
	
	public Variable getVariable(AST ast, Symbol symbol) {
		Symbol scopedSymbol = ast.getScopedSymbol(symbol);
		if (scopedSymbol == null) throw new GeneratorException("Symbol undefined in scope: " + symbol);
		Variable variable = findVariable(scopedSymbol);
		if (variable == null) throw new GeneratorException("Missing registered variable for symbol " + symbol);
		return variable;
	}
	
	private Variable findVariable(Symbol symbol) { 
		for (Variable var : variableList) {
			// note that could be multiple symbols with the same content,
			// we look for an exact object instance match
			if (var.getSymbol() == symbol) return var;
		}
		return null;
	}
	

	
	
	
	
	


}
