package nl.tsmeele.generator.mcrl2;


import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import nl.tsmeele.compiler.AST;
import nl.tsmeele.compiler.CodeGenerator;
import nl.tsmeele.compiler.GeneratorException;
import nl.tsmeele.compiler.StackableFunction;
import nl.tsmeele.compiler.Variable;
import nl.tsmeele.generator.common.FunctionAddInteger;
import nl.tsmeele.generator.common.FunctionAtomicStatement;
import nl.tsmeele.generator.common.FunctionParticipants;
import nl.tsmeele.generator.common.FunctionRole;
import nl.tsmeele.generator.common.FunctionStatementBlock;
import nl.tsmeele.generator.common.FunctionSubtractInteger;
import nl.tsmeele.generator.common.FunctionWithThreads;
import nl.tsmeele.grammar.StackableFunctionType;

public class Mcrl2Generator extends CodeGenerator {
	private Mcrl2 mcrl2 = new Mcrl2();
	
	public Mcrl2 getMcrl2() {
		return mcrl2;
	}
	
	
	public Mcrl2Generator(PrintStream out) {
		super(out);
	}
	
	@Override
	public void beginEvaluation(AST ast) {
		Mcrl2Checker checker = new Mcrl2Checker();
		try {
			checker.checkMcrl2Installed();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println();
	}
	
	@Override
	public void endEvaluation(AST ast) {
		//System.out.println(mcrl2.toString());
		System.out.println();

	}
	
	
	
	@Override
	public StackableFunction createFunction(StackableFunctionType type) {
		switch (type) {
		// common functions
		case INTEGER_ADDITION: return new FunctionAddInteger();
		case INTEGER_SUBTRACTION: return new FunctionSubtractInteger();
		case WITH_THREADS: return new FunctionWithThreads();
		case ATOMIC: return new FunctionAtomicStatement();
		case ROLE: return new FunctionRole();
		case STATEMENT_BLOCK: return new FunctionStatementBlock();
		case PARTICIPANTS: return new FunctionParticipants();

		
		// mCRL2 specific functions
		case PROTOCOL: return new FunctionProtocol();
		case COMMUNICATION: return new FunctionCommunication();
		case ATOMIC_BLOCK: return new FunctionAtomicBlock();
		case COHERENT: return new FunctionCoherent();
		}
		return null;
	}

	

		
}
