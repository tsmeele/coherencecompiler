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
	
		
	public void push(Stackable item) {
		stack.push(item);
	}
	
	public boolean isEmptyStack() {
		return stack.empty();
	}
	
	public int getStackSize() {
		return stack.size();
	}
	
	public Stackable peek() {
		return stack.peek();
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
	
	

	
	
	
	


}
