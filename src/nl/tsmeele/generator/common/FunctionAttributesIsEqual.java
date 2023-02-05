package nl.tsmeele.generator.common;

import java.util.ArrayList;

import nl.tsmeele.compiler.CodeGenerator;
import nl.tsmeele.compiler.Stackable;
import nl.tsmeele.compiler.StackableFunction;
import nl.tsmeele.compiler.Value;
import nl.tsmeele.compiler.Variable;

public class FunctionAttributesIsEqual extends StackableFunction {

	@Override
	public Stackable apply(CodeGenerator code) {

		return new Value("==");
	}
}
