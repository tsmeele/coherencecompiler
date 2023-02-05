package nl.tsmeele.generator.plantuml;

import nl.tsmeele.compiler.CodeGenerator;
import nl.tsmeele.compiler.GeneratorException;
import nl.tsmeele.compiler.Stackable;
import nl.tsmeele.compiler.StackableFunction;
import nl.tsmeele.compiler.Value;
import nl.tsmeele.compiler.Variable;
import nl.tsmeele.generator.common.StringTargetCode;

public class FunctionIfThenElse extends StackableFunction {

	@Override
	public Stackable apply(CodeGenerator code) {
		int stackFrameSize = this.getStackFrameSize();
		if (stackFrameSize < 2 || stackFrameSize > 3) {
			throw new GeneratorException("Configuration error in IfStatement, stack should have expression value and one or two results");
		}
		Value elseResult = null;
		if (stackFrameSize == 3) {
			// process the Else result
			elseResult = code.popValue(); 
		}
		Value thenResult = code.popValue();
		String condition = code.popValue().getString();
		
		// we build the choice target code using the then-clause results as a foundation/collector
		thenResult.addFirst(new StringTargetCode("alt " + condition));
		if (elseResult != null) {
			thenResult.addLast(new StringTargetCode("else not " + condition));
			thenResult.addLast(elseResult.getCode());
		}
		thenResult.addLast(new StringTargetCode("end"));
		code.push(thenResult);
		return null;
	}

}
