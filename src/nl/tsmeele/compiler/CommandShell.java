package nl.tsmeele.compiler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * CommandShell manages the execution of one or more system commands.
 * 
 * @author Ton Smeele
 *
 */
public class CommandShell {
	private int maxOutputLines = 500;
	int exitValue = 0;
	
	public int getExitValue() {
		return exitValue;
	}
	
	/**
	 * Maximize the process output (stdout + stderr) to a number of lines (and kill the process if it outputs more)
	 * @param maxOutputLines maximum number of output lines allowed. The value 0 means unlimited.
	 */
	public void setMaxOutputLines(int maxOutputLines) {
		this.maxOutputLines = maxOutputLines;
	}
	
	/**
	 * Execute multiple system commands as a pipeline workflow.
	 * 
	 * @param pbList List of commands as ProcessBuilder instances.  
	 * @return stdout output from the last process in the pipeline
	 * @throws IOException
	 */
	public String executePiped(List<ProcessBuilder> pbList) throws IOException {
		// prepare processes for piped activation
		pbList.get(0).inheritIO().redirectOutput(ProcessBuilder.Redirect.PIPE);
		int lastProcess = pbList.size() - 1;
		for (int i = 1; i < lastProcess; i++) {
			pbList.get(i).redirectOutput(ProcessBuilder.Redirect.PIPE);
		}
		// start processes
		pbList.get(lastProcess).redirectError(ProcessBuilder.Redirect.INHERIT);
		List<Process> procs = ProcessBuilder.startPipeline(pbList);
		// collect output from pipe
		InputStream out = procs.get(procs.size() - 1).getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(out));
		String output = collectProcessOutput(reader);
		// make sure all processes are cleaned up (e.g. in case we have collected partial output)
		for (Process p : procs) {
			p.destroy();
		}
		try {
			exitValue = procs.get(lastProcess).exitValue();
		} catch (IllegalThreadStateException e) { 
		}
		return output;
	}
	
	/**
	 * Execute a single system command.  
	 * @param cmd a list of arguments to the command, where the first argument specifies the command name
	 * @return stdout output (includes stderr)
	 * @throws IOException
	 */
	public String execute(List<String> cmd) throws IOException {
		ProcessBuilder pb = new ProcessBuilder(cmd);
		return execute(pb);

	}
	
	/**
	 * Execute a single system command.
	 * @param pb process description to be executed
	 * @return stdout output (includes stderr)
	 * @throws IOException
	 */
	public String execute(ProcessBuilder pb) throws IOException {
		pb.redirectErrorStream(true);
		Process process = pb.start();
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String output = collectProcessOutput(reader);
		if (process.isAlive()) {
			process.destroy();
		}
		try {
			exitValue = process.exitValue();
		} catch (IllegalThreadStateException e) { 
		} 
		return output;
	}
	
	
	private String collectProcessOutput(BufferedReader reader) throws IOException {
		String line = reader.readLine();
		String output = "";
		int lineCounter = 0;
		while (line != null) {
			lineCounter++;
			output = output.concat(line);
			line = reader.readLine();
			if (line != null) {
				output = output.concat("\n");
			}
			if (maxOutputLines != 0 && lineCounter > maxOutputLines) {
				line = null;
			}
		}
		return output;
	}
	
	
	public void testExample() throws IOException {
		ProcessBuilder pb1 = new ProcessBuilder("ls");
		ProcessBuilder pb2 = new ProcessBuilder("sort");
		ProcessBuilder pb3 = new ProcessBuilder("grep", "o");
		List<ProcessBuilder> pbList = List.of(pb1, pb2, pb3);
		System.out.println("Processes in pipeline execution start!");
		System.out.println(executePiped(pbList));
		System.out.println("Execution done! Exit value is: " + getExitValue());
	}
	

}
