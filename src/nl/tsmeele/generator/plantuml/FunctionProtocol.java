package nl.tsmeele.generator.plantuml;

import nl.tsmeele.compiler.CodeGenerator;
import nl.tsmeele.compiler.GeneratorException;
import nl.tsmeele.compiler.Stackable;
import nl.tsmeele.compiler.StackableFunction;
import nl.tsmeele.compiler.Value;
import nl.tsmeele.compiler.Variable;
import nl.tsmeele.generator.common.StringTargetCode;

public class FunctionProtocol extends StackableFunction {

	@Override
	public Stackable apply(CodeGenerator code) {
		Value statementBlock = (Value) code.popValue();
		if (!statementBlock.isCode()) {
			throw new GeneratorException("In Protocol, expected a target code for StatementBlock, found " + statementBlock.getValueType());
		}
		Variable protocol = code.popVariable();
		// insert our protocol target code and publish as result
		statementBlock.addFirst(new StringTargetCode("title " + protocol.getName()));
		code.push(statementBlock);
		return null;
	}
	

}
