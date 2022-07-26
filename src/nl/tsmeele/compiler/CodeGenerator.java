package nl.tsmeele.compiler;

import java.io.PrintStream;

import nl.tsmeele.grammar.StackableFunctionType;

/**
 * CodeGenerator is the backend of the compiler that generates source or binary code for a target.
 * 
 * @author Ton Smeele
 *
 */
public abstract class CodeGenerator {
	private CodeStack stack = new CodeStack();
	public PrintStream out = null;	// stackable functions can use this stream to output (additional) target code

	public CodeGenerator(PrintStream out) {
		this.out = out;
	}
	
	
	public abstract StackableFunction createFunction(StackableFunctionType type);

	public void beginEvaluation(AST ast) {
		// implementations can override: hook for additional processing at start of evaluation
	}

	public void endEvaluation(AST ast) {
		// implementations can override: hook for additional processing at end of evaluation
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
		if (stack.isEmpty()) {
			return null;
		}
		switch (stack.peek().getType()) {
		case VARIABLE:
			return ((Variable)stack.pop()).getValue();
		case VALUE:
			return (Value) stack.pop();
		case FUNCTION: {
			Stackable result = ((StackableFunction) stack.pop()).apply(this);
			if (result == null)
				return null;
			stack.push(result);
			return popValue();
		}
		}
		throw new GeneratorException("Unrecognized generator stack item");
	}

	public Variable popVariable() {
		if (stack.peek().isVariable())
			return ((Variable) stack.pop());
		// not a Variable stackable type, ignore
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
	
	public String getStackContent() {
		return stack.toString();
	}

}
