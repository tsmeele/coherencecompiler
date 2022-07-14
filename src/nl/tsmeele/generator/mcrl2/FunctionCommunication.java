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

		Value irodsObject = message.getValue();
		String irodsObjectName = irodsObject == null ? null : irodsObject.getString();
		System.out.println("GENERATOR:\n" + 
				"communicate via " + stream.getId() + " from " + fromRole + " to " + toRole + 
				" via " + threads + " threads, message: " + message.getName() + " object " + (irodsObjectName != null) );

		Mcrl2 mcrl2 = ((Mcrl2Generator)code).getMcrl2();
		mcrl2.addCommunication(stream.getName(), fromRole.getName(), toRole.getName(), message.getName(), irodsObjectName);
		return null;
	}
	
	

	
	
}
