package nl.tsmeele.generator.mcrl2;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.tsmeele.compiler.GeneratorException;

public class Mcrl2 {
	private final String RULER = "%-----";
	private final String PROTOCOL_PREFIX = "p";
	private final String DORMANT_ROLE = "b";
	private final String VALUE_PREFIX = "a";
	private final String START_VALUE = "zero";

	private static int valueSeqNo = 0;

	
	private List<Mcrl2Role> mRoles = new ArrayList<Mcrl2Role>();
	private List<String> values = new ArrayList<String>();
	private Map<String,String> protocols = new HashMap<String,String>();

	private ArrayList<ArrayList<String>> coherentVariableSet = new ArrayList<ArrayList<String>>();
	
	private String[] coherentRole = new String[2];
	
	//private List<String> coherentRoles = new ArrayList<String>();
	
	
	
	public void addCoherentVariables(ArrayList<String> vars) {
		System.out.println("ADD COHERENCE: " + vars);
		coherentVariableSet.add(vars);
	}
	
	
	// how we handle communications:
	//  	From has:	To has:		effect:
	//		no object	no object	new object created at From, new object created at To with same value
	//		no object	object		new object created at From, object at To updated with this value
	//		object		no object	new object created at To with value same as From
	//		object		object		object at To is updated with value same as object at From
	public void addCommunication(String protocol, String from, String to, String operation, String irodsObject) {
		if (irodsObject == null) return;
		// if not known yet, register roles and values.  In addition, assign the toRole the value of the fromRole
		Mcrl2Role fromRole = registerRoleIfNew(from, irodsObject);
		Mcrl2Role toRole = registerRoleIfNew(to,irodsObject);
		if (fromRole.getValue() == null) {
			fromRole.setValue(createValue());	// this includes registration of the newly created value
		}
		toRole.setValue(fromRole.getValue());
		// each protocol is a process that can run interleaved with other processes (= protocols) 
		String mProtocol = registerProtocolIfNew(protocol);
		// we add the communication to the operations that the protocol executes in sequence
		String protocolDefinition = protocols.get(mProtocol);
		String sequence = ".";
		if (protocolDefinition.equals("")) sequence = "";
		protocolDefinition = protocolDefinition.concat(sequence + composeCommunicationText(fromRole.getId(), toRole.getId(), toRole.getValue()) );
		protocols.put(mProtocol, protocolDefinition);
	}
	
	private String composeCommunicationText(String fromRole, String toRole, String fromValue) {
		return "C(" + fromRole + "," + toRole + "," + fromValue + ")";
	}

	private Mcrl2Role registerRoleIfNew(String role, String object) {
		Mcrl2Role mRole = new Mcrl2Role(role, object);
		for (Mcrl2Role r : mRoles) {
			if (r.equals(mRole)) return mRole;
		}
		mRoles.add(mRole);
		return mRole;
	}
	
	private String createValue() {
		String value = VALUE_PREFIX + Integer.toString(valueSeqNo++);
		values.add(value);
		return value;
	}

	

	
	private String registerProtocolIfNew(String protocol) {
		String mProtocol = getProtocol(protocol);
		if (protocols.get(mProtocol) == null) {
			protocols.put(mProtocol, "");
		}
		return mProtocol;
	}
	
	private String getProtocol(String protocol) {
		return PROTOCOL_PREFIX + protocol;
	}
	
	
	
	// TODO: fill in coherentRole with other roles in sequence.
	private void addCoherence() {
		coherentRole[0] = mRoles.get(0).getId();
		coherentRole[1] = mRoles.get(1).getId();
	}
	
	
	
	public void createMcrl2Program(String path) throws IOException, GeneratorException {
		// TODO make coherence more programmable
		addCoherence();
		if (incompleteInput()) {
			throw new GeneratorException("Insufficient data to generate mCRL2 program");
		}
		if (path == null || path.equals("") ) {
			System.out.println(RULER + "\n" + programText() + RULER );
			return;
		}
		BufferedWriter writer = openFile(path);
		writer.write(programText());
		writer.close();
	}
	
	public void createCoherenceFormula(String path) throws IOException, GeneratorException {
		addCoherence();
		if (incompleteInput()) {
			throw new GeneratorException("Insufficient data to generate mCRL2 coherence formula");
		}
		if (path == null || path.equals("") ) {
			System.out.println(RULER + "\n" + coherenceFormulaText() + RULER);
			return;
		}
		BufferedWriter writer = openFile(path);
		writer.write(coherenceFormulaText());
		writer.close();
	}
	
	
	private BufferedWriter openFile(String path) throws IOException  {
		return new BufferedWriter(new FileWriter(path));
	}
	
	private boolean incompleteInput() {
		return mRoles.isEmpty() || values.isEmpty() || coherentVariableSet.isEmpty() || coherentVariableSet.get(0).size() < 2;
	}
	
	
	private String insertProtocolDefinitions() {
		String text = "";
		String parallelText = null;
		// add a definition for each protocol
		for (String protocol : protocols.keySet()) {
			text = text.concat(protocol + " = " + protocols.get(protocol) + ";\n");
			if (parallelText == null) {
				parallelText = "iRODSprotocol = " + protocol;
			}
			else {
				parallelText = parallelText.concat(" || " + protocol);
			}
		}
		return text + ";" + parallelText + ";\n";
	}
	
	private String insertValues() {
		String text = START_VALUE;
		for (String v :  values) {
			text = text.concat("|" + v);
		}
		return text;
	}
	
	private String insertRoles() {
		String text = DORMANT_ROLE;
		for (int i = 0; i < mRoles.size(); i++) {
			text = text.concat("|" + mRoles.get(i).getId());
		}
		return text;
	}
	
	private String insertRoleInits() {
		String text = "Role(" + DORMANT_ROLE + ")";
		for (int i = 0; i < mRoles.size(); i++) {
			// add role
			text = text.concat(" || Role(" + mRoles.get(i).getId() + ")");
			// add all channels from this role to all other roles
			for (int j = 0; j <mRoles.size(); j++) {
				if (i == j) continue;
				text = text.concat(" || Chan(" + mRoles.get(i).getId() + "," + mRoles.get(j).getId() + ")");
			}
		}
		return text;
	}
		
	private String insertCoherenceInit() {
		return "Coherence(" + coherentVariableSet.get(0).get(0) + "," + coherentVariableSet.get(0).get(1) + ")"; 
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
			+ "  Role(N:RoleName) = Role'(N," + START_VALUE + ",false," + DORMANT_ROLE + ");\n"
			+ "  Chan(from,to:RoleName) = Channel'(from,to,[],0);\n"
			+ "  Coherence(r1,r2:RoleName) = Coherence'(r1,r2," + START_VALUE + "," + START_VALUE + "," + DORMANT_ROLE + ");\n"
			+ "\n"
			+ "\n"
			+ "init\n"
			+ "\n"
			+ "  allow(\n"
			+ "     { send', receive', lastRole,lastEq, lock',unlock'},\n"
			+ "     comm( \n"
			+ "        {send|enqueue|trackSend -> send', receive|dequeue|updateProp|trackReceive -> receive',\n"
			+ "         lock|lock -> lock', unlock|unlock -> unlock'},\n"
			+ "\n"
			+ "        % these processes occur in every system:\n"
			+ insertCoherenceInit() + " ||\n" 
			+ insertRoleInits() + " ||\n"  
			+ "iRODSprotocol \n"
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
				+ "   [true*.lastRole("+coherentRole[0]+").lastEq(false)]   (                % 1a: AG(last(p) AND p != q  => ...... )\n"
				+ "      [!lastRole("+coherentRole[1]+")*.lastRole("+coherentRole[0]+")] false   &&            % 1b: A(not last(p) U last(q) \n"
				+ "                                                        %     NB: remainder of Until clause \n"
				+ "                                                        %         is checked as part of 1c \n"
				+ "      [!lastRole("+coherentRole[1]+")*.lastRole("+coherentRole[1]+").lastEq(false)] false   % 1c: A(not last(q) U last(q) AND (p==q)) \n"
				+ "   )\n"
				+ "&&\n"
				+ "   % part 2:\n"
				+ "   [true*.lastRole("+coherentRole[1]+").lastEq(false)]   (\n"
				+ "          [!lastRole("+coherentRole[0]+")*.lastRole("+coherentRole[1]+")] false  &&\n"
				+ "          [!lastRole("+coherentRole[0]+")*.lastRole("+coherentRole[0]+").lastEq(false)] false\n"
				+ "   )\n"
				+ ")\n"
				+ "";
		
		
	}
	
}
