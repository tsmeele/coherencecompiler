package nl.tsmeele.generator.mcrl2;

import java.io.File;
import java.io.IOException;
import java.util.List;

import nl.tsmeele.compiler.CommandShell;
import nl.tsmeele.compiler.FileHandler;
import nl.tsmeele.compiler.GeneratorException;

/**
 * Mcrl2Checker model checks if input models meet certain properties using mCRL2 as the underlying model checker tool.
 * Input models are specified as variable sets. The variables are used to populate a prepared mCRL2 template program.
 * 
 * NB: depends on mCRL2 toolset.  
 * 
 * @author Ton Smeele
 *
 */
public class Mcrl2Checker {
	private final String MCRL22LPS = "mcrl22lps";
	private final String LPS2LTS = "lps2lts";
	private final String LPS2PBES = "lps2pbes";
	private final String PBES2BOOL = "pbes2bool";
	private final String LTSCOMPARE = "ltscompare";
	private static FileHandler fh = new FileHandler();
	private static CommandShell cmd = new CommandShell();
	private boolean keepSourceFiles = false;
	
	
	public void checkMcrl2Installed() throws IOException {
		List<String> which = List.of("which", MCRL22LPS, LPS2LTS, LPS2PBES, PBES2BOOL, LTSCOMPARE);
		ProcessBuilder pb = new ProcessBuilder(which);
		cmd.execute(pb);
		if (cmd.getExitValue() != 0) {
			throw new RuntimeException("Model checking: The Mcrl2 code generator depends on the mCRL2 toolset (see https:////www.mcrl2.org)\n" +
										"Please install: " + which.subList(1, which.size()));
		}
	}
	
	public void setKeepSourceFiles(boolean b) {
		keepSourceFiles = b;
	}
	
	public boolean isCoherent(Mcrl2VariableSet vars) throws IOException {
		vars.addSynchronizedTerminationAction = false;
		Mcrl2Template template = new Mcrl2Template(vars);
		File source = fh.createTempFile(template.generateMcrl2Program());
		File formula = fh.createTempFile(template.generateCoherenceFormula());
		boolean result = hasProperty(source, formula);
		if (!keepSourceFiles) {
			source.delete();
			formula.delete();
		}
		return result;
	}
	
	public boolean isDeadlockFree(Mcrl2VariableSet vars) throws IOException {
		// to establish freedom of deadlock, we test that all interleaved protocols can terminate.
		// to do so, we append a synchronized "done" action to each protocol 
		vars.addSynchronizedTerminationAction = true;
		Mcrl2Template template = new Mcrl2Template(vars);
		File source = fh.createTempFile(template.generateMcrl2Program());
		File formula = fh.createTempFile(template.generateTerminatesAlwaysFormula());
		boolean result = hasProperty(source, formula);
		if (!keepSourceFiles) {
			source.delete();
			formula.delete();
		}
		return result;
	}
	
	public boolean isWeaklyBisimilar(Mcrl2VariableSet vars1, Mcrl2VariableSet vars2) throws IOException {
		vars1.addSynchronizedTerminationAction = false;
		Mcrl2Template template1 = new Mcrl2Template(vars1);
		File source1 = fh.createTempFile(template1.generateMcrl2Program());
		File lts1 = File.createTempFile("lts", null);
		source2lts(source1, lts1);
		
		vars2.addSynchronizedTerminationAction = false;
		Mcrl2Template template2 = new Mcrl2Template(vars2);
		File source2 = fh.createTempFile(template2.generateMcrl2Program());
		File lts2 = File.createTempFile("lts", null);
		source2lts(source2, lts2);
		ProcessBuilder pb = new ProcessBuilder(LTSCOMPARE, "-eweak-bisim", "--in1=lts", "--in2=lts", lts1.getAbsolutePath(), lts2.getAbsolutePath());
		String output = cmd.execute(pb);
		if (!keepSourceFiles) {
			source1.delete();
			source2.delete();
		}
		lts1.delete();
		lts2.delete();
		boolean result = output.contains("true");
		if (!result && !output.contains("false")) {
			throw new GeneratorException("Model checking: Unable to perform model check bisimulation");
		}
		return result;
	}
	
	
	private boolean hasProperty(File source, File formula) throws IOException {
		ProcessBuilder pb1 = new ProcessBuilder(MCRL22LPS, "-lregular", "-q", source.getAbsolutePath());
		ProcessBuilder pb2 = new ProcessBuilder(LPS2PBES, "--formula=" + formula.getAbsolutePath());
		ProcessBuilder pb3 = new ProcessBuilder(PBES2BOOL);
		List<ProcessBuilder> pipeline = List.of(pb1, pb2, pb3);
		String output = cmd.executePiped(pipeline);
		boolean result = output.contains("true");
		if (!result && !output.contains("false")) {
			throw new GeneratorException("Model checking: Unable to perform model check property");
		}
		return result;
	}
	
	private void source2lts(File source, File lts) throws IOException {
		File lps = File.createTempFile("lps", null);
		ProcessBuilder pb1 = new ProcessBuilder(MCRL22LPS, "-lregular", "-q", source.getAbsolutePath(), lps.getAbsolutePath());
		String output = cmd.execute(pb1);
		if (output == null || !output.equals("")) {
			lps.delete();
			throw new GeneratorException("Model checking: Unable to generate lps file from mcrl2 source file");
		}
		// we use an lps tempfile as a workaround, since we have experienced input file errors when passing the lps via the pipe to lps2lts 
		ProcessBuilder pb2 = new ProcessBuilder(LPS2LTS, "-q", "--save-at-end", "--rewriter=jittyc", "--cached", lps.getAbsolutePath(), lts.getAbsolutePath());
		output = cmd.execute(pb2);
		lps.delete();
		if (output == null || !output.equals("")) {
			throw new GeneratorException("Model checking: Unable to generate lts file from lps input file");
		}
	}	

}
