package nl.tsmeele.generator.mcrl2;

import java.util.LinkedList;

import nl.tsmeele.compiler.CodeGenerator;
import nl.tsmeele.compiler.GeneratorException;
import nl.tsmeele.compiler.Stackable;
import nl.tsmeele.compiler.StackableFunction;
import nl.tsmeele.compiler.TargetCode;
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
		Variable irodsOperation = code.popVariable();
		
		// we build the choice target code using the then-clause results as a foundation/collector
		//     "(" <then> ")" "+" "(" <else> ")"
		String total = "(" + Mcrl2.renderCode(thenResult) + ")+(";
		String elseText = "tau";
		if (elseResult != null) {
			elseText = Mcrl2.renderCode(elseResult); 
		}
		total = total.concat(elseText + ")");
		Value totalCode = new Value(new StringTargetCode(total));
		return totalCode;
	}
}

