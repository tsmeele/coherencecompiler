package nl.tsmeele.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class CommandLineProcessor parses command line options into a map structure.
 * 
 * Each command line option is evaluated in turn to see if it matches a known, named, pattern.
 * A match is added as a value to a result map, where the key is the name of the pattern.
 * The value part of the map consists of a list of strings. 
 * 
 * The first string in the list is the entire matched option. If the pattern has specified 
 * capture groups, then each result is added to the list. 
 * 
 * It is possible to match multiple command line arguments as a group. In this case the pattern will match
 * with the first argument of the group, and the remaining members are simply added to the result.
 * The number of extra arguments to add to the result is specified as a suffix ":n" to the pattern name,
 * where n is a positive integer.
 * For instance, a pattern named "-f:1" will match the sequence of command line arguments "-f file1.txt".
 * The resulting list is: List<String> = ["-f", "file1.txt"]. 
 * 
 * Objects of class CommandLineProcessor are instantiated with a map of patterns (name -> pattern) can will be
 * used as the set of valid options.
 * 
 * The processor will process command line arguments until the input is exhausted or the input does not match any valid option. 
 * 
 * @author Ton Smeele
 *
 */
public class CommandLineProcessor {
	private Map<String, String> validOptions = null;
	private Pattern keySplitPattern = null;
	private HashMap<String, List<String>> optionsFound = new HashMap<String, List<String>>();
	private ArrayList<String> argumentsFound = new ArrayList<String>();


	public CommandLineProcessor(Map<String, String> validOptions, String[] args) {
		this.validOptions = validOptions;
		keySplitPattern = Pattern.compile("^(.+):(\\d+)$");
		processArguments(args);
	}
	
	public Map<String, List<String>> getOptions() {
		return optionsFound;
	}
	
	public List<String> getArguments() {
		return argumentsFound;
	}
	
	/**
	 * Returns a list of option names. If the option requires additional arguments then the option name will be followed
	 * by a numbered list <arg0>...<argN> to show the number of arguments expected.
	 * @return help text
	 */
	public String getUsageText() {
		String text = "";
		for (String option : validOptions.keySet()) {
			String[] patternName = splitKey(option);
			text = text.concat(patternName[0] + " ");
			if (patternName[1] != null) {
				int additionalArgs = Integer.valueOf(patternName[1]);
				for (int i = 0; i < additionalArgs; i++) {
					text = text.concat("<arg" + Integer.toString(i) + "> ");
				}
			}
			text = text.concat("\n");
		}
		return text;
	}
	

	private void processArguments(String[] args) {
		int index = 0;
		boolean previousOptionHasMatched = true;
		while (index < args.length && previousOptionHasMatched) {
			previousOptionHasMatched = false; 
			// try to match the next command line argument with a valid option
			for (String option : validOptions.keySet()) {
				String pattern = validOptions.get(option);
				List<String> matchedInput = match(pattern, args[index]);
				if (matchedInput == null) {
					// argument does not match this valid option, try next option
					continue;
				}
				// we have a matching option! Now check that we can consume the # of arguments that belong to this option
				String[] patternName = splitKey(option);
				int additionalArgs = 0;
				if (patternName[1] != null) {
					// pattern name has suffix: we will consume additional arguments as specified
					additionalArgs = Integer.valueOf(patternName[1]);
				}
				if (index + additionalArgs >= args.length) {
					// match failed as option specifies additional arguments yet command line will be exhausted
					break;
				}
				previousOptionHasMatched = true;
				// save results
				index++;
				while (additionalArgs > 0 && index < args.length) {
					matchedInput.add(args[index]);
					index++;
					additionalArgs--;
				}
				optionsFound.put(patternName[0], matchedInput);
				break;
			}
		}
		// interpret any remaining arguments as non-option
		while (index < args.length) {
			argumentsFound.add(args[index]);
			index++;
		}
	}

	private String[] splitKey(String key) {
		String[] result = new String[2];
		result[0] = key;
		result[1] = null;
		Matcher m = keySplitPattern.matcher(key);
		if (m.find()) {
			result[0] = m.group(1);
			result[1] = m.group(2);
		}
		return result;
	}
	
	
	private List<String> match(String pattern, String input) {
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(input);
		if (!m.find())
			return null;
		ArrayList<String> result = new ArrayList<String>(); 
		// entire match in group(0), whereas other indexes may contain captured groups
		for (int i = 0; i <= m.groupCount(); i++) {
			result.add(m.group(i));
		}
		return result;
	}

}
