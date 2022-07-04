package nl.tsmeele.generator.plantuml;

import nl.tsmeele.compiler.CodeGenerator;
import nl.tsmeele.compiler.Stackable;
import nl.tsmeele.compiler.StackableFunction;
import nl.tsmeele.compiler.Value;
import nl.tsmeele.compiler.Variable;

public class FunctionCommunication extends StackableFunction {

	@Override
	public Stackable apply(CodeGenerator code) {
		Variable message = code.popVariable();
		Value threads = code.popValue();
		Variable stream = code.popVariable();
		Variable toRole = code.popVariable();
		Variable fromRole = code.popVariable();
		String details = "";
		if (message.getValue() != null) {
			details = "(" + message.getValue() + ")";
		}
		System.out.println(fromRole.getName() + "->" + toRole.getName() + " : " + message.getName() + details);
		return null;
	}
	
	
}
