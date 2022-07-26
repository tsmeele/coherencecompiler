package nl.tsmeele.generator.common;

import nl.tsmeele.compiler.CodeGenerator;
import nl.tsmeele.compiler.Stackable;
import nl.tsmeele.compiler.StackableFunction;

public class FunctionParticipants extends StackableFunction {

	@Override
	public Stackable apply(CodeGenerator code) {
		// just clean up the stack, consume the list of roles
		int stackFrameSize = this.getStackFrameSize();
		for (int i = 0; i < stackFrameSize; i++) {
			code.popVariable();
		}
		return null;
	}

	
}
