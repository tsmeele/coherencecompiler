package nl.tsmeele.generator.mcrl2;

import nl.tsmeele.compiler.CodeGenerator;
import nl.tsmeele.compiler.Stackable;
import nl.tsmeele.compiler.StackableFunction;
import nl.tsmeele.compiler.TargetCode;
import nl.tsmeele.compiler.Value;
import nl.tsmeele.compiler.Variable;
import nl.tsmeele.generator.common.StringTargetCode;

public class FunctionCommunication extends StackableFunction {

	@Override
	public Stackable apply(CodeGenerator code) {
		code.popVariable(); // irodsOperation
		code.popValue(); // currently we do not model the 'threads' Value in mCRL2
		Variable toRole = code.popVariable();
		Variable fromRole = code.popVariable();
		
		Mcrl2 mcrl2 = ((Mcrl2Generator)code).getMcrl2();
		TargetCode tc = new StringTargetCode(mcrl2.codeCommunication(fromRole, toRole));
		return new Value(tc);
	}

	
}
