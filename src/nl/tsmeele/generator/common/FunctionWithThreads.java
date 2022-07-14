package nl.tsmeele.generator.common;

import nl.tsmeele.compiler.CodeGenerator;
import nl.tsmeele.compiler.Stackable;
import nl.tsmeele.compiler.StackableFunction;
import nl.tsmeele.compiler.Value;

public class FunctionWithThreads extends StackableFunction {

	@Override
	public Stackable apply(CodeGenerator code) {
		Stackable data = code.peek();
		// if a threads value has been specified, then the data will be a value.
		if (!data.isValue())  {
			return new Value(1);	// use a default # of threads instead
		}
		return code.popValue();
	}
	

}
