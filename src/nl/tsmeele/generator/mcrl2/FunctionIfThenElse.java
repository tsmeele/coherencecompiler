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

/* 
 * Expected stack structure: 2 or 3 frames
 *    value(code)   : (optional, exists if else clause was specified)
 *    value(code)   : result from then clause
 *    value(code)   : result from condition expression
 * 
 * The conditional expression for an If-Then-Else statement may take various forms:
 *  1) Literal string: "text"
 *     The element is a string value
 *  2) Role Condition:  myrole(a==b)
 *     The element is a string value
 *     The string value encodes 2 code values, they are separated by a newline character  
 *     first value: code to include when expression result is True 
 *     second value: code to include when expression result is False
 */

public class FunctionIfThenElse extends StackableFunction {

	@Override
	public Stackable apply(CodeGenerator code) {
		int stackFrameSize = this.getStackFrameSize();
		System.err.println("Stack dump:\n---");
		System.err.println(code);
		System.err.println("---");
		if (stackFrameSize < 2 || stackFrameSize > 3) {
			throw new GeneratorException("Configuration error in IfStatement, stack should have expression value and one or two results");
		}
		Value elseResult = null;
		if (stackFrameSize == 3) {
			// process the Else result
			elseResult = code.popValue(); 
		}
		Value thenResult = code.popValue();
		// default condition is literal string, in this case we produce no additional code
		String codeTrueCondition = "";
		String codeFalseCondition = "";
		// retrieve the condition result from stack, decode if needed
		String conditionResult = code.popValue().getString();
		int separator = conditionResult.indexOf("\n");
		if (separator >= 0) {
			// this is a role-condition: we need to decode the value into 2 code results
			codeTrueCondition = conditionResult.substring(0,separator).concat(".");  // <c-true>
			codeFalseCondition = conditionResult.substring(separator + 1).concat("."); // <c-false>
		}
		// we build the choice target code using the then-clause results as a foundation/collector
		//    "(" <c-true> "(" <then> ")" "+" <c-false> "(" <else> ")"  ")"
		String total = "(" + codeTrueCondition + "(" + Mcrl2.renderCode(thenResult) + ")+"
				     + codeFalseCondition + "(";
		String elseText = "tau";
		if (elseResult != null) {
			elseText = Mcrl2.renderCode(elseResult); 
		}
		total = total.concat(elseText + "))");
		Value totalCode = new Value(new StringTargetCode(total));
		return totalCode;
	}
}

