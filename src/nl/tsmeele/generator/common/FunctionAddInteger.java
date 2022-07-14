package nl.tsmeele.generator.common;

import nl.tsmeele.compiler.CodeGenerator;
import nl.tsmeele.compiler.Stackable;
import nl.tsmeele.compiler.StackableFunction;
import nl.tsmeele.compiler.Value;

public class FunctionAddInteger extends StackableFunction {


	
	@Override
	public Stackable apply(CodeGenerator code) {
		int operand1 = code.popValue().getInteger();
		int operand2 = code.popValue().getInteger();
		int result = operand1 + operand2;
		System.out.println("addition applied, result is: " + result);
		return new Value(result);
	}
	

}
