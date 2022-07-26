package nl.tsmeele.compiler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

/**
 * StreamFactory provides easy InputStream/PrintStream access to files by wrapping relevant Java IO actions
 * @author Ton Smeele
 *
 */
public class StreamFactory {
	final static boolean AUTOFLUSH = true;

	/**
	 * Opens an InputStream to read from an existing file.
	 * 
	 * @param path name of the file, a null or "" denotes that System.in stream is
	 *             to be used instead of a file
	 * @return InputStream to file
	 * @throws IOException
	 */
	public static InputStream openInputFile(String path) throws IOException {
		if (path == null || path.equals("")) {
			return System.in;
		}
		File file = new File(path);
		if (file.isFile() && file.canRead()) {
			try {
				return new FileInputStream(file);
			} catch (FileNotFoundException e) {
				throw new IOException("File does not exist: " + path);
			}
		}
		throw new IOException("Unable to open and read from file: " + path);
	}

	public static PrintStream overwriteOrCreateOutputFile(String path) throws IOException {
		return openOutputFile(path, false, true);
	}

	public static PrintStream appendOrCreateOutputFile(String path) throws IOException {
		return openOutputFile(path, true, false);
	}

	public static PrintStream createOutputFile(String path) throws IOException {
		return openOutputFile(path, false, false);
	}

	/**
	 * Opens a PrintStream to write to a new or existing file.
	 * The caller is responsible for closing the file by closing the PrintStream.
	 * 
	 * @param path      name of the file, a null or "" denotes that System.out
	 *                  stream is to be used instead of a file
	 * @param append    if true then file may already exist, data will be appended
	 *                  to end of file
	 * @param overwrite if true then file may already exist, file will be
	 *                  overwritten with new data
	 * @return output PrintStream to file
	 * @throws IOException
	 */
	private static PrintStream openOutputFile(String path, boolean append, boolean overwrite) throws IOException {
		if (path == null || path.equals("")) {
			return System.out;
		}
		File file = new File(path);
		if (file.exists()) {
			// are we allowed to change an existing file?
			if (!(append || overwrite)) {
				throw new IOException("Output file already exists: " + path);
			}
		} else {
			// file does not exist, create a new empty file
			try {
				file.createNewFile();
				if (!file.exists()) {
					throw new IOException("Error while creating output file " + path);
				}
			} catch (IOException e) {
				throw new IOException("Unable to create output file: " + path);
			}
		}
		try {
			// either append or overwrite the file
			PrintStream out = new PrintStream(new FileOutputStream(file, append), AUTOFLUSH); 
			return out;  
		} catch (FileNotFoundException e) {
			throw new IOException("Unable to " + (append ? "append to" : "overwrite") + " output file: " + path);
		}
	}

}
