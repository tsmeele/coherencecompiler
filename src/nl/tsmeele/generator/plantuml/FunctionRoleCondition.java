package nl.tsmeele.generator.plantuml;

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
	 *    value condition (e.g. "==")
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
		// represent the conditional expression as a string
		// we display "role(attr1 == attr2)" as "(role.attr1 == role.attr2)" 
		return new Value(
				"(" + role.getName() + "." + attribute1.getName() + " " + 
				condition.getString() + " "
				+ role.getName() + "." + attribute2.getName() + ")"
				);
	}
}
