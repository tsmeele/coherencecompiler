package nl.tsmeele.generator.mcrl2;

import java.util.LinkedList;

import nl.tsmeele.compiler.CodeGenerator;
import nl.tsmeele.compiler.GeneratorException;
import nl.tsmeele.compiler.Stackable;
import nl.tsmeele.compiler.StackableFunction;
import nl.tsmeele.compiler.TargetCode;
import nl.tsmeele.compiler.Value;

public class FunctionProtocol extends StackableFunction {

	@Override
	public Stackable apply(CodeGenerator code) {
		Value statementBlock = code.popValue();
		if (!statementBlock.isCode()) {
			throw new GeneratorException("In Protocol, expected target code for StatementBlock, found " + statementBlock.getValueType());
		}
		code.popVariable();  // ProtocolName
		// assemble all target code so far into a new mMCRL2 protocol
		Mcrl2 mcrl2 = ((Mcrl2Generator)code).getMcrl2();
		mcrl2.codeProtocol(statementBlock);
		// further target code will start from scratch again (as input for a new protocol)
		return null;
	}
	

}
