package nl.tsmeele.generator.mcrl2;

import nl.tsmeele.compiler.CodeGenerator;
import nl.tsmeele.compiler.GeneratorException;
import nl.tsmeele.compiler.Stackable;
import nl.tsmeele.compiler.StackableFunction;
import nl.tsmeele.compiler.Value;
import nl.tsmeele.compiler.Variable;

public class FunctionRoleCondition extends StackableFunction {
	
	/*
	 *  expected stack structure:
	 *  	 
	 *    value conditiontype ("==",...)
	 *    variable attribute2
	 *    variable attribute1
	 *    variable role
	 */
	
	
	@Override
	public Stackable apply(CodeGenerator code) {
		int stackFrameSize = this.getStackFrameSize();
		if (stackFrameSize != 4) {
			throw new GeneratorException("Configuration error in RoleCondition, stack should have condition, role and two attributes");
		}
		Value condition = code.popValue();
		Variable attribute2 = code.popVariable();
		Variable attribute1 = code.popVariable();
		Variable role = code.popVariable();
				
		Mcrl2 mcrl2 = ((Mcrl2Generator)code).getMcrl2();
		// we encode two code values as one value using a newline character as separator
		// the code values represent scenarios for condition results "true" and "false"
		return new Value(mcrl2.codeTestTrue(role, condition, attribute1, attribute2) + "\n" + 
		mcrl2.codeTestFalse(role, condition, attribute1, attribute2));
	}
}
