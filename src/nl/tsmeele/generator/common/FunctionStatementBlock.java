package nl.tsmeele.generator.common;

import nl.tsmeele.compiler.CodeGenerator;
import nl.tsmeele.compiler.GeneratorException;
import nl.tsmeele.compiler.Stackable;
import nl.tsmeele.compiler.StackableFunction;
import nl.tsmeele.compiler.Value;

public class FunctionStatementBlock extends StackableFunction {

	@Override
	public Stackable apply(CodeGenerator code) {
		int stackFrameSize = this.getStackFrameSize();
		if (stackFrameSize == 0) {
			// no target code produced by the statements in this block
			return null;
		}
		// glue the code from the statements together
		// we work from the last statement back to the beginning 
		Value result = code.popValue(); // last statement used as a results collector
		if (!result.isCode() ) {
			throw new GeneratorException("Expected target code while processing StatementBlock, found " + result.toString());
		}
		for (int i = 1; i < stackFrameSize; i++) {
			Value v = code.popValue();
			if (!v.isCode()) {
				throw new GeneratorException("Expected target code while processing StatementBlock, found " + v.toString());
			}
			result.addFirst(v.getCode());
		}
		code.push(result);
		return null;
	}

}
