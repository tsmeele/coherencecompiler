package nl.tsmeele.generator.mcrl2;

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
		System.out.println("GENERATOR:\n" + 
				"communicate via " + stream.getId() + " from " + fromRole + " to " + toRole + 
				" via " + threads + " threads, message: " + message.getName());
		return null;
	}
	
	
}
