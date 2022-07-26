package nl.tsmeele.generator.common;

import nl.tsmeele.compiler.CodeGenerator;
import nl.tsmeele.compiler.Stackable;
import nl.tsmeele.compiler.StackableFunction;
import nl.tsmeele.compiler.Value;

public class FunctionWithThreads extends StackableFunction {	
	
	@Override
	public Stackable apply(CodeGenerator code) {
		if (this.getStackFrameSize() > 0) {
			return code.popValue();
		}
		// no value has been generated in the With clause, we will use a default value
		return new Value(1);
	}
	

}
