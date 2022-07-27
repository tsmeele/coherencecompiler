package nl.tsmeele.generator.mcrl2;

import java.util.Set;

/**
 * The class Mcrl2ProgramWriter manages the templates for an Mcrl2 program and related formula source text.
 * Its instances use a set of variables to generate source file text.
 * 
 * NB: Templates for protocol operations are managed elsewhere (by class Mcrl2Variables).  
 * 
 * @author Ton Smeele
 *
 */
public class Mcrl2Template {
	private final String RULER = "%-----";
	private Mcrl2VariableSet vars = null;

	
	public Mcrl2Template(Mcrl2VariableSet vars) {
		this.vars = vars;
	}
	
	public String generateMcrl2Program() {
		return RULER + "\n" + programText() + RULER;
	}
	
	public String generateCoherenceFormula() {
		return RULER + "\n" + coherenceFormulaText() + RULER;
	}
	
	public String generateTerminatesAlwaysFormula() {
		return RULER + "\n" + terminatesAlwaysFormulaText() + RULER;
	}
	
	private String insertProtocolDefinitions() {
		String text = "";
		int i = 0;
		for (String protocol : vars.protocols.keySet()) {
			// add a definition for this protocol, the hashmap key is the protocol name, the value carries the protocol body
			if (vars.addSynchronizedTerminationAction) {
				// we introduce an extra action "doneN" per protocol to be able to assert termination
				text = text.concat(protocol + " = " + vars.protocols.get(protocol) + ".done" + Integer.toString(i) + ";\n");
				i++;
			} else {
				text = text.concat(protocol + " = " + vars.protocols.get(protocol) + ";\n");
			}
		}
		// all protocols will execute interleaved, add the parallel initialization
		String parallelText = "joinedprotocol = " + String.join("||",vars.protocols.keySet());	
		return text + parallelText + ";\n";
	}
	
	
	
	/**
	 * input for the sort "Value"
	 * @return mcrl2 formatted enumeration of all Value
	 */
	private String insertValues() {
		String text = vars.initialValue;
		for (String v :  vars.values) {
			text = text.concat("|" + v);
		}
		return text;
	}
	
	/**
	 * input for the sort "RoleName"
	 * @return mcrl2 formatted enumeration of all RoleName
	 */
	private String insertRoles() {
		String text = vars.dormantRole;
		for (String r : vars.roles) {
			text = text.concat("|" + r);
		}
		return text;
	}
	
	/**
	 * Generates a list of Role and Channel processes that need to be initialized.
	 * The Role processes are derived from the enumeration of RoleNames.
	 * The Channel processes connect each role combination, hence this is a cartesian product 
	 * of all RoleNames, with the exception of the "Dormant" Role. 
	 * 
	 * @return mcrl2 formatted init list of processes
	 */
	private String insertRoleInits() {
		String text = "Role(" + vars.dormantRole + ")";
		for (String r : vars.roles) {
			// add role
			text = text.concat(" || Role(" + r + ")");
			// add all channels from this role to all other roles
			for (String r2 : vars.roles) {
				if (r == r2) continue;
				text = text.concat(" || Chan(" + r + "," + r2 + ")");
			}
		}
		return text;
	}
		
	/**
	 * Generates the process initialization for the Coherence process. 
	 * @return mcrl2 formatted init of a process
	 */
	private String insertCoherenceInit() {
		return "Coherence(" + vars.coherentRoles[0] + "," + vars.coherentRoles[1] + ")"; 
	}
	
	private String insertActDone() {
		if (!vars.addSynchronizedTerminationAction) return "";
		if (vars.protocols.size() < 2) {
			return "done0;";
		}
		String doneText = "done'";
		for (int i = 0; i < vars.protocols.size(); i++) {
			doneText = doneText.concat(",done" + Integer.toString(i));
		}
		return doneText + ";";
	}
	
	private String insertAllowDone() {
		if (!vars.addSynchronizedTerminationAction) return "";
		return vars.protocols.size() < 2 ? ",done0" : ",done'";
	}
	
	private String insertCommDone() {
		if (!vars.addSynchronizedTerminationAction) return "";
		if (vars.protocols.size() < 2) {
			return "";
		}
		String doneText = "done0";
		for (int i = 1; i < vars.protocols.size(); i++) {
			doneText = doneText.concat("|done" + Integer.toString(i));
		}
		return doneText + " -> done',";
	}
	
	
private String programText() {
		return 
			"sort RoleName = struct " + insertRoles() + "; \n"
			+ "sort Value = struct " + insertValues() + ";\n"
			+ "sort SyncStates = struct first|second|more;\n"
			+ "\n"
			+ "act  send,enqueue,trackSend,send',\n"
			+ "     receive,dequeue,updateProp,trackReceive, receive' : RoleName # RoleName # Value;\n"
			+ "     lastRole : RoleName;  \n"
			+ "     lastEq : Bool;\n"
			+ "     lock,lock',unlock,unlock':RoleName # RoleName;\n"
			+ "     " + insertActDone() + "\n"
			+ "\n"
			+ "proc\n"
			+ "\n"
			+ "% Participants in our protocol have a process attribute 'prop' that is updated upon a receive\n"
			+ "% As an extension, the property can be locked by another process (the lockholder).\n"
			+ "% NB: requesting process blocks until lock is given\n"
			+ "\n"
			+ "  Role'(N:RoleName,prop:Value,locked:Bool,holder:RoleName) = \n"
			+ "     sum from:RoleName, v:Value . ((N != from) -> updateProp(from,N,v).Role'(N,v,locked,holder)) +\n"
			+ "     sum requester:RoleName . ( (locked && (requester==holder)) -> unlock(requester,N).  \n"
			+ "               Role'(N,prop,false,requester)) +\n"
			+ "     sum requester:RoleName . ( (!locked) -> lock(requester,N).  \n"
			+ "               Role'(N,prop,true,requester)) ;\n"
			+ "\n"
			+ "\n"
			+ "% Channels are implementation of asynchronous communication, maximum queue size 5 is arbitrary\n"
			+ "\n"
			+ "  Channel'(from,to:RoleName,data:List(Value),size:Int) =\n"
			+ "     sum v:Value . ((size < 5) -> enqueue(from,to,v).Channel'(from,to,data <| v,succ(size)) ) +\n"
			+ "     (size > 0)                -> dequeue(from,to,head(data)).Channel'(from,to,tail(data), pred(size) );\n"
			+ "\n"
			+ "\n"
			+ "% Coherence' is a process that maintains the global coherence property 'last'\n"
			+ "% In addition it acts as a probe, to report coherence status information (via actions):\n"
			+ "%   lastRole(role)  : name of role that receives a new value for its prop\n"
			+ "%   lastEq(boolean) : true if the data values of roles coh1 and coh2 are equal (after the receive action)\n"
			+ "% Note that Coherence' is synchronized upon both send and receive. Further actions are blocked until report actions \n"
			+ "% are done. This behavior ensures that the next transition after lastRole is always a lastEq action \n"
			+ "\n"
			+ "  Coherence'(coh1,coh2:RoleName,coh1val,coh2val:Value,last:RoleName) =\n"
			+ "\n"
			+ "     % case 1: coh1 role receives a new value\n"
			+ "     sum from,to:RoleName,v:Value . \n"
			+ "     (to == coh1) -> (trackReceive(from,to,v).   \n"
			+ "               lastRole(to).lastEq(v==coh2val).     \n"
			+ "               Coherence'(coh1,coh2,v,coh2val,to) ) +\n"
			+ "\n"
			+ "     % case 2: coh2 role receives a new value\n"
			+ "     sum from,to:RoleName,v:Value . \n"
			+ "     (to == coh2) -> (trackReceive(from,to,v).\n"
			+ "               lastRole(to).lastEq(v==coh1val).\n"
			+ "               Coherence'(coh1,coh2,coh1val,v,to) ) +\n"
			+ "\n"
			+ "     % case 3: some other role receives a new value\n"
			+ "     sum from,to:RoleName,v:Value . \n"
			+ "     ((to != coh1) && (to != coh2)) -> (trackReceive(from,to,v).\n"
			+ "               lastRole(to).lastEq(coh1val==coh2val).\n"
			+ "               Coherence'(coh1,coh2,coh1val,coh2val,to) ) +\n"
			+ "\n"
			+ "     % case send: just process and ignore\n"
			+ "     sum from,to:RoleName,v:Value . (trackSend(from,to,v).\n"
			+ "               Coherence'(coh1,coh2,coh1val,coh2val,last) ) ;\n"
			+ "\n"
			+ "\n"
			+ "% Data transfer 'Cpq' is a basic construct of our global language:  Cpq --send--> Fpq --receive--> 1\n"
			+ "% mcrl2 operators (sequential,option,parallel,recursion) are used to implement compositional constructs\n"
			+ "% Our local language uses 'send' and 'receive' as its basic constructs. Tau is supported natively by mcrl2.\n"
			+ "    \n"
			+ "  C(from:RoleName,to:RoleName,v:Value) = send(from,to,v).receive(from,to,v); \n"
			+ "\n"
			+ "\n"
			
			+ "\n"
			+ "\n"
			+ insertProtocolDefinitions() + "\n" 
			+ "\n"
			+ "% shorthands added to beautify init process ;)\n"
			+ "\n"
			+ "  Role(N:RoleName) = Role'(N," + vars.initialValue + ",false," + vars.dormantRole + ");\n"
			+ "  Chan(from,to:RoleName) = Channel'(from,to,[],0);\n"
			+ "  Coherence(r1,r2:RoleName) = Coherence'(r1,r2," + vars.initialValue + "," + vars.initialValue + "," + vars.dormantRole + ");\n"
			+ "\n"
			+ "\n"
			+ "init\n"
			+ "\n"
			+ "  allow(\n"
			+ "     { send', receive', lastRole,lastEq, lock',unlock'"
			+ insertAllowDone() + "},\n"
			+ "     comm( \n"
			+ "        {send|enqueue|trackSend -> send', receive|dequeue|updateProp|trackReceive -> receive',\n"
			+ insertCommDone() + "\n"
			+ "         lock|lock -> lock', unlock|unlock -> unlock'},\n"
			+ "\n"
			+ "        % these processes occur in every system:\n"
			+ insertCoherenceInit() + " ||\n" 
			+ insertRoleInits() + " ||\n"  
			+ "joinedprotocol \n"
			+ ") );";
	}

private String coherenceFormulaText() {
	return "% Check if process attributes of role p and role q remain coherent \n"
			+ "% 2022.01 TSM\n"
			+ "\n"
			+ "% Our definition of coherence in CTL:\n"
			+ "%    coherence(p,q)   = follows(p,q) AND follows(q,p)\n"
			+ "%        follows(p,q) = last(p) AND (p!=q) => AX( \n"
			+ "%                          A(not last(p) AND not last(q) U last(q) AND (p==q))  )    \n"
			+ "\n"
			+ "% Implementation of the above CTL formula in mcrl2 muCalculus syntax:\n"
			+ "% part 1: follows(p,q)\n"
			+ "% part 2: follows(q,p)\n"
			+ " \n"
			+ "( \n"
			+ "   % part 1:\n"
			+ "   [true*.lastRole("+vars.coherentRoles[0]+").lastEq(false)]   (                % 1a: AG(last(p) AND p != q  => ...... )\n"
			+ "      [!lastRole("+vars.coherentRoles[1]+")*.lastRole("+vars.coherentRoles[0]+")] false   &&            % 1b: A(not last(p) U last(q) \n"
			+ "                                                        %     NB: remainder of Until clause \n"
			+ "                                                        %         is checked as part of 1c \n"
			+ "      [!lastRole("+vars.coherentRoles[1]+")*.lastRole("+vars.coherentRoles[1]+").lastEq(false)] false   % 1c: A(not last(q) U last(q) AND (p==q)) \n"
			+ "   )\n"
			+ "&&\n"
			+ "   % part 2:\n"
			+ "   [true*.lastRole("+vars.coherentRoles[1]+").lastEq(false)]   (\n"
			+ "          [!lastRole("+vars.coherentRoles[0]+")*.lastRole("+vars.coherentRoles[1]+")] false  &&\n"
			+ "          [!lastRole("+vars.coherentRoles[0]+")*.lastRole("+vars.coherentRoles[0]+").lastEq(false)] false\n"
			+ "   )\n"
			+ ")\n"
			+ "";
}


private String terminatesAlwaysFormulaText() {
	if (vars.protocols.size() < 2) {
		return "[(!done0)*] <true*.done0> true";
	}
	return "[(!done')*] <true*.done'> true";
}



	
}
