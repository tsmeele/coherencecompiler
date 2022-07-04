package nl.tsmeele.generator.plantuml;

import nl.tsmeele.compiler.CodeGenerator;
import nl.tsmeele.compiler.Stackable;
import nl.tsmeele.compiler.StackableFunction;
import nl.tsmeele.compiler.Variable;
import nl.tsmeele.generator.common.StackableFunctionType;

public class FunctionProtocol extends StackableFunction {

	@Override
	public Stackable apply(CodeGenerator code) {
		Variable protocol = code.popVariable();
		System.out.println("title " + protocol.getName());
		return null;
	}
	

}
