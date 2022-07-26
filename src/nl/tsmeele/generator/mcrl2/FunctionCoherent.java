package nl.tsmeele.generator.mcrl2;

import java.util.ArrayList;

import nl.tsmeele.compiler.CodeGenerator;
import nl.tsmeele.compiler.Stackable;
import nl.tsmeele.compiler.StackableFunction;
import nl.tsmeele.compiler.Variable;

public class FunctionCoherent extends StackableFunction {
	private ArrayList<Variable> roles = new ArrayList<Variable>();
	
	@Override
	public Stackable apply(CodeGenerator code) {

		int coherentVars = this.getStackFrameSize();
		for (int i = 0; i < coherentVars; i++) {
			roles.add(code.popVariable());
		}
		Mcrl2 mcrl2 = ((Mcrl2Generator)code).getMcrl2();
		mcrl2.codeCoherent(roles);	// register in the model that these variables need to remain coherent
		// the coherent statement does not directly produce any target code 
		return null;
	}
	
	
}
