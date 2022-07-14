package nl.tsmeele.compiler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;

import nl.tsmeele.generator.mcrl2.Mcrl2Generator;
import nl.tsmeele.generator.plantuml.PlantumlGenerator;
import nl.tsmeele.grammar.Program;


public class Main {
	static boolean DEBUG = true;
	static boolean generateMcrl2 = false;
	static boolean generatePlantuml = true;
	static boolean onlyShowSyntax = false;


	public static void main(String[] args) throws InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
		if (DEBUG) System.out.println("Coherence Protocol DSL compiler");
		if (onlyShowSyntax) {
			Term g = new Program();
			System.out.println(g.toAllSyntax());
			System.exit(0);
		}
	
		try {
			// (1) LEXICAL SCAN
			TokenList tokens = lexicalScan();
			if (DEBUG) System.out.println(tokens);
			
			// (2) SYNTAX ANALYSIS
			Term startTerm = new Program();
			AST ast = startTerm.parse(tokens.extractStripped());
			if (DEBUG) System.out.println("tree is:\n" + ast.toString());

			
			// (3) SEMANTICS ANALYSIS
			ast.analyze();
			if (DEBUG) System.out.println("Congratulations, semantic analysis revealed no errors!");
			if (DEBUG) System.out.println("tree is:\n" + ast.toString());

			//System.exit(1);

			// (4) CODE GENERATION
			if (generateMcrl2) {
				CodeGenerator code = new Mcrl2Generator();
				ast.evaluate(code);
				if (DEBUG) System.out.println("Completed mCRL2 code generation.");
			}
			if (generatePlantuml) {
				CodeGenerator code = new PlantumlGenerator();
				ast.evaluate(code);
				if (DEBUG) System.out.println("Completed PlantUML code generation.");
			}
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			System.out.println("Compilation aborted due to errors");
			System.exit(1);
		}	
		
	}
	
	
	public static TokenList lexicalScan() {
		LexicalScanner lex = new LexicalScanner();
		System.out.println("Enter data, use CTRL-D or 'EOF' to stop");
		try {
			return lex.scan(new BufferedReader(new InputStreamReader(System.in)));
		}
		catch (IOException e) {
			System.out.println("Error reading input");
			System.exit(1);
		}
		catch (LexicalScanException e) {
			System.out.println(e.getMessage() + "\nConfiguration error detected, lexical scan aborted.");
			System.exit(1);
		}
		return null;
	}
	
	

}
