package nl.tsmeele.compiler;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

public class FileHandler {
	private StreamFactory streamFactory = new StreamFactory();

	/**
	 * Creates a new file with specified text content. Should the file already exist
	 * or otherwise not be able to be created, an exception is raised.
	 * 
	 * @param path    name and location of the file
	 * @param content data to be written to the file
	 * @throws IOException
	 */
	public void createFile(String path, String content) throws IOException {
		PrintStream out = streamFactory.createOutputFile(path);
		out.println(content);
		out.close();
	}

	/**
	 * Writes text content to a file. If the file already exists, it is truncated
	 * before the content is written. If the file does not exist, it is created.
	 * 
	 * @param path    name and location of the file
	 * @param content data to be written to the file
	 * @throws IOException
	 */
	public void overwriteOrCreateFile(String path, String content) throws IOException {
		PrintStream out = streamFactory.overwriteOrCreateOutputFile(path);
		out.println(content);
		out.close();
	}

	public File createTempFile(String content) throws IOException {
		File tempFile = File.createTempFile("FileHandler", null);
		overwriteOrCreateFile(tempFile.getAbsolutePath(), content);
		return tempFile;
	}
}
