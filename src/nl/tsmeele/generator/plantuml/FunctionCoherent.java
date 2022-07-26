package nl.tsmeele.generator.plantuml;

import nl.tsmeele.compiler.CodeGenerator;
import nl.tsmeele.compiler.Stackable;
import nl.tsmeele.compiler.StackableFunction;

public class FunctionCoherent extends StackableFunction {

	@Override
	public Stackable apply(CodeGenerator code) {
		// plantuml does not implement any diagram activity for coherent
		// just clean up the stack after executing the coherent statement
		int coherentVars = this.getStackFrameSize();
		for (int i = 0; i < coherentVars; i++) {
			code.popVariable();
		}
		return null;
	}

}
