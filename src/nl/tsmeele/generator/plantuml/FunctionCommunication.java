package nl.tsmeele.generator.plantuml;

import nl.tsmeele.compiler.CodeGenerator;
import nl.tsmeele.compiler.Stackable;
import nl.tsmeele.compiler.StackableFunction;
import nl.tsmeele.compiler.Value;
import nl.tsmeele.compiler.Variable;
import nl.tsmeele.generator.common.StringTargetCode;

public class FunctionCommunication extends StackableFunction {

	@Override
	public Stackable apply(CodeGenerator code) {
		Variable irodsOperation = code.popVariable();
		code.popValue(); // currently we do not display the 'threads' Value in PlantUML
		Variable toRole = code.popVariable();
		Variable fromRole = code.popVariable();
		
		String fromAttribute = attributeInfo(fromRole);
		String toAttribute = attributeInfo(toRole);
		String details = "(" + fromAttribute + "," + toAttribute + ")";
		if (fromAttribute.equals(toAttribute)) {
			details = "";
		}	
		return new Value (new StringTargetCode(fromRole.getName() + "->" + toRole.getName() + " : " + irodsOperation.getName() + details) );
	}

	
	private String attributeInfo(Variable role) {
		Value attribute = role.getValue();
		if (attribute == null || !attribute.isVariable()) {
			return "";
		}
		return attribute.getVariable().getName();
	}
	
}
