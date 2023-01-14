package nl.tsmeele.compiler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import static java.util.Map.entry;

import nl.tsmeele.generator.mcrl2.Mcrl2Checker;
import nl.tsmeele.generator.mcrl2.Mcrl2Generator;
import nl.tsmeele.generator.mcrl2.Mcrl2VariableSet;
import nl.tsmeele.generator.plantuml.PlantumlGenerator;
import nl.tsmeele.grammar.Program;


public class Main {
	static final String PROGRAMNAME = "coherence";
	static boolean DEBUG = true;
	static boolean generateMcrl2 = true;
	static boolean includeBisimulationTest = false;
	static boolean generatePlantuml = false;
	static boolean showSyntax = false;
	static boolean showUsage = false;
	static InputStream in = null;
	static PrintStream out = System.out;
	
	final static Map<String,String> commandLineOptions = Map.ofEntries(
			entry("--debug","^-d$|^--debug$"),
			entry("--help", "^-h$|^--help$"),
			entry("--syntax","^-s$|^--syntax$"),
			entry("--mcrl2", "^-m$|^--mcrl2$"),
			entry("--bisimtest", "^-b$|^--bisimtest$"),
			entry("--plantuml","^-p$|^--plantuml$")
			);


	public static void main(String[] args) throws IOException {
		
		String phase = null;
		
			phase = "Preparation";
			processCommandLine(args);
		try {
			if (DEBUG) System.err.println("DEBUG MODE\n" + PROGRAMNAME + "\n");
		
			if (showSyntax) {
				Term g = new Program();
				out.println(g.toAllSyntax());
				System.exit(0);
			}
			
			// if appropriate, provide user with data entry instructions
			if (in.equals(System.in)) { 
				System.out.println("Enter source text, end with CTRL-D or a line consisting of 'EOF':");
			}
		
			// (1) LEXICAL SCAN
			phase = "Lexican scan";
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			LexicalScanner lex = new LexicalScanner();
			TokenList tokens = lex.scan(reader);
			
			if (DEBUG) System.err.println(tokens);
			
			// (2) SYNTAX ANALYSIS
			phase = "Syntax analysis (parse)";
			Term startTerm = new Program();
			AST ast = startTerm.parse(tokens.extractStripped());
			if (DEBUG) System.err.println("tree is:\n" + ast.toString());

			
			// (3) SEMANTICS ANALYSIS
			phase = "Semantic analysis";
			ast.analyze();
			if (DEBUG) System.err.println("Congratulations, semantic analysis revealed no errors!");
			if (DEBUG) System.err.println("tree is:\n" + ast.toString());


			if (!(generatePlantuml || generateMcrl2)) {
				System.exit(0);
			}
			
			// (4) CODE GENERATION
			phase = "Code generation";
			if (generateMcrl2) {
				Mcrl2Generator code = new Mcrl2Generator(out);
				ast.evaluate(code);
				if (DEBUG) System.err.println("Completed mCRL2 code generation.");
				
				// create a model from the evaluated source code, also derive its local protocol projection
				Mcrl2VariableSet mVars = code.getMcrl2().populateModel();
				if (DEBUG) System.err.println(code.getMcrl2().toString());
				//if (DEBUG) System.err.println(mVars);
				Mcrl2VariableSet mVarsLocal = mVars.project2localprotocols();
				if (DEBUG) System.err.println("resulting local protocol vars is:\n" + mVarsLocal);
				
				// execute model checker tests
				Mcrl2Checker checker = new Mcrl2Checker();
				if (DEBUG) checker.setKeepSourceFiles(true);
				System.out.println("mCRL2 MODEL CHECKING RESULTS USING COHERENCE MODEL:\n");
				System.out.print("Protocol is free of deadlocks? : ");
				System.out.println(checker.isDeadlockFree(mVars));
				System.out.print("Protocol can be implemented?   : ");
				if (includeBisimulationTest) {
					System.out.print("(preparing/testing, may take long) ");
					System.out.println(checker.isWeaklyBisimilar(mVars,mVarsLocal));
				} else {
					System.out.println("(test not included)");
				}
				// if required, test coherence
				if (mVars.coherentAttrSets.size() > 0) {
					// protocol includes coherence requirements
					int count = 1;
					mVars.initializeCoherenceVariations();
					System.out.print("Running coherence tests: ");
					boolean coherentTotal = true;
					while(coherentTotal == true && mVars.renderCoherenceVariation() ) {
						System.out.print(".." + count);
						boolean test = checker.isCoherent(mVars);
						coherentTotal = coherentTotal && test;
						count++;
					}
					System.out.println("\nProtocol protects coherence? :  " + coherentTotal);
				}		
			}
			
			if (generatePlantuml) {
				CodeGenerator code = new PlantumlGenerator(out);
				ast.evaluate(code);
				if (DEBUG) System.err.println("Completed PlantUML code generation.");
			}
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			System.err.println("Compilation aborted in " + phase + " phase due to errors");
			System.exit(1);
		}	
		
	}
	

	private static void processCommandLine(String[] args) throws IOException {
		CommandLineProcessor commandLine = new CommandLineProcessor(commandLineOptions, args);
		Map<String,List<String>> cmd = commandLine.getOptions();
		DEBUG = DEBUG || cmd.containsKey("--debug");
		showSyntax = showSyntax || cmd.containsKey("--syntax");
		showUsage = showUsage || cmd.containsKey("--help");
		generatePlantuml = generatePlantuml || cmd.containsKey("--plantuml");
		generateMcrl2 = generateMcrl2 || cmd.containsKey("--mcrl2");
		includeBisimulationTest = includeBisimulationTest || cmd.containsKey("--bisimtest");
		
		if (showUsage) {
			System.out.println("Usage: " + PROGRAMNAME + " [options] inputfile outputfile\nValid options are:\n" + commandLine.getUsageText() + "\n");
			System.exit(0);
		}
		
		List<String> files = commandLine.getArguments();
		// redirect input to file, unless name is absent or "-" 
		String inputFile = files.size() < 1 || files.get(0).equals("-") ? null : files.get(0);
		in = StreamFactory.openInputFile(inputFile);	

		// redirect output to file, unless name is absent or "-"
		String outputFile = files.size() < 2 || files.get(0).equals("-") ? null : files.get(1);
		out = StreamFactory.createOutputFile(outputFile);
		

	}
	
	

}
