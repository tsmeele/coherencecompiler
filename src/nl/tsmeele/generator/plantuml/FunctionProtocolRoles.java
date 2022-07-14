package nl.tsmeele.generator.plantuml;

import nl.tsmeele.compiler.CodeGenerator;
import nl.tsmeele.compiler.Stackable;
import nl.tsmeele.compiler.StackableFunction;
import nl.tsmeele.compiler.Variable;

public class FunctionProtocolRoles extends StackableFunction {

	@Override
	public Stackable apply(CodeGenerator code) {
		while (!code.isEmptyStack()) {
			Variable role = code.popVariable();
		//	System.out.println("participant " + role.getName());
		}
		return null;
	}

}
