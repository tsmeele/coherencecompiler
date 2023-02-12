package nl.tsmeele.generator.mcrl2;


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
				text = text.concat(protocol + " = (" + vars.protocols.get(protocol) + ").done" + Integer.toString(i) + ";\n");
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
		String text = Mcrl2VariableSet.initialValue;
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
		String text = Mcrl2VariableSet.dormantRole;
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
		String text = "Role(" + Mcrl2VariableSet.dormantRole + ")";
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
		return "Coherence(" + vars.coherentAttributes[0].getRole() + "," + vars.coherentAttributes[1].getRole() + "," +
				vars.coherentAttributes[0].getAttr() + "," + vars.coherentAttributes[1].getAttr() + ")"; 
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
	
private String 	programText() {
	    return 
	    	"% Coherence model Global and Local Language\n" + 
	     	"% 2022.08 TSM\n" + 
	     	"%\n" + 
	     	"sort RoleName = struct " + insertRoles() + "; \n" +
			"sort Value = struct Lock|Unlock|Ack|" + insertValues() + ";\n" +
			"sort RequesterRole = RoleName;\n" + 
			"sort FromRole = RoleName;\n" +
			"sort Attribute = Nat;\n" + 
			"sort AttrMap = Attribute -> Value;\n" + 
			"sort Condition = struct EQ|NEQ;\n" +
			"sort LockStatusMap = Attribute -> Bool;\n" + 
			"sort LockHolderMap = Attribute -> RoleName;\n" + 
			"\n" + 
			"act  send,enqueue,syncCoherence,send',\n" + 
			"     receive,dequeue,updateProp,updateCoh,receive' : FromRole # RoleName # Attribute # Value;\n" + 
			"     lastRoleAttr : RoleName # Attribute;  \n" + 
			"     lastEq : Bool;\n" + 
		    "     test, testProp, syncCoherenceOnTest,test' : Condition # RoleName # Attribute # Attribute;\n" +
			"     " + insertActDone() + "\n" +
			"\n" + 
			"proc\n" + 
			"\n" + 
			"% Roles carry process attributes that can be updated upon a receive\n" + 

			"  Role'(me:RoleName,props:AttrMap, lockStatusMap:LockStatusMap, lockHolderMap:LockHolderMap) = \n" + 
			"     sum from:RoleName, attr:Attribute, v:Value . (\n" + 
			"         (me != from) -> ( \n" +
			"\n" +                       
			"                        % unlock request for unlocked attr or for locked attr where role is the lock holder\n" +
			"              (v == Unlock && ( (lockStatusMap(attr) && lockHolderMap(attr) == from) || \n" +
			"                                 lockStatusMap(attr) == false) ) -> \n" +
			"                         updateProp(from,me,attr,v).Role'(me,props,lockStatusMap[attr->false],lockHolderMap) + \n" +
            "\n" +
			"                        % lock request and attribute is not yet locked or role is the lock holder (and locks again)\n" +
			"              (v == Lock && (lockStatusMap(attr)==false || (lockStatusMap(attr) && lockHolderMap(attr)==from) ) ) ->\n" +
			"                         updateProp(from,me,attr,v).Role'(me,props,lockStatusMap[attr->true],lockHolderMap[attr->from]) +\n" + 
            "\n" +
			"                        % acknowledge message (in response to lock/unlock request)\n" +
			"              (v == Ack) ->\n" +
			"                         updateProp(from,me,attr,v).Role'(me,props,lockStatusMap,lockHolderMap) +\n" +
			"              (v != Ack && v != Unlock && v != Lock) -> ( \n" +
            "\n" +
			"                        % attribute data update for unlocked attr or attr where role is lock holder\n" +
			"                   ( (lockStatusMap(attr) && lockHolderMap(attr) == from) || lockStatusMap(attr) == false ) ->\n" +  
			"                         updateProp(from,me,attr,v).Role'(me,props[attr->v],lockStatusMap,lockHolderMap) \n" +
			"              )\n" +
			"         ) \n" +
			"     ) +\n" +
		    " sum attrA,attrB:Attribute . ( (attrA != attrB ) -> \n" +
		    "          (props(attrA) == props(attrB) " + 
		    "&& props(attrA) != zero && props(attrB) != zero" +
		     ") -> \n" +
		    "             (testProp(EQ,me,attrA,attrB). \n" +
		    "                     Role'(me,props,lockStatusMap,lockHolderMap)) \n" +
		   	"	       <> (testProp(NEQ,me,attrA,attrB). \n" +
		    "                     Role'(me,props,lockStatusMap,lockHolderMap)) \n" +
		    "    ); \n" +
	     "\n" + 
	     "\n" + 
	     "% Channels are implementation of asynchronous communication, maximum queue size 5 is arbitrary\n" + 
	     "% The queue holds (attribute,value) pairs (stored in separate lists).\n" + 
	     "\n" + 
	     "  Channel'(from,to:RoleName,queueA:List(Attribute),queueV:List(Value),size:Int) =\n" + 
	     "     sum attr:Attribute,v:Value . (\n" + 
	     "         (size < 5) -> enqueue(from,to,attr,v).\n" + 
	     "                          Channel'(from,to,queueA <| attr, queueV <| v, succ(size)) ) +\n" + 
	     "         (size > 0) -> dequeue(from,to,head(queueA), head(queueV)).\n" + 
	     "                          Channel'(from,to,tail(queueA),tail(queueV), pred(size) \n" + 
	     "     );\n" + 
	     "\n" + 
	     "\n" + 
	     "% Coherence' is a process that maintains the global coherence property 'last'\n" + 
	     "% In addition it acts as a probe, to report coherence status information (via actions):\n" + 
	     "%   lastRoleAttr(role,attribute)  : references the attribute that has received a new value\n" + 
	     "%   lastEq(boolean) : true if the data values of the monitored attributes are equal (after a receive action)\n" + 
	     "% Note that Coherence' is synchronized upon both send and receive. \n" + 
	     "% Further actions are blocked until the actions of Coherence' are done. \n" + 
	     "% This behavior ensures that the next transition after lastRoleAttr is always a lastEq action \n" + 
	     "\n" + 
	     "  Coherence'(role1,role2:RoleName,attr1,attr2:Attribute,value1,value2:Value,\n" + 
	     "             lastRole:RoleName,lastAttr:Attribute) =    % lastX state included for monitoring during simulation\n" + 
	     "\n" +  
         "            % test case: just synchronize on action and keep same state \n" +
         "sum condition:Condition,from:RoleName,attrA,attrB:Attribute . ( \n" +
         "   syncCoherenceOnTest(condition,from,attrA,attrB).\n" +
         "         Coherence'(role1,role2,attr1,attr2,value1,value2,lastRole,lastAttr)\n" +
         ") + \n" +
		 "sum from,to:RoleName,attr:Attribute,v:Value . (\n" + 
		 "\n" +
		 "            % send case: just synchronize on action and keep same state\n" +
		 "   syncCoherence(from,to,attr,v).\n" +
		 "         Coherence'(role1,role2,attr1,attr2,value1,value2,lastRole,lastAttr) +\n" +	
		 "\n" +
		 "            % special receive cases: received value is related to an (un)lock request\n" +
		 "           % we synchronize and keep same state\n" +
		 "  (v == Lock || v == Unlock || v == Ack) -> updateCoh(from,to,attr,v) .\n" + 
		 "           Coherence'(role1,role2,attr1,attr2,value1,value2,lastRole,lastAttr) +\n" +
		 "  (v != Lock && v != Unlock && v != Ack) -> (\n" +
		 "\n" +
		 "    % all other receive cases, we need to update coherence state information:\n" +
		 "\n" +
		 "            % receive case 1: the attribute of role1 receives a new value\n" + 
		 "   (to == role1 && attr == attr1) -> updateCoh(from,to,attr,v).   \n" +
		 "            lastRoleAttr(to,attr).lastEq(v==value2).     \n" +
		 "            Coherence'(role1,role2,attr1,attr2,v,value2,to,attr) +\n" + 
		 "\n" +
		 "             % receive case 2: the attribute of role2 receives a new value\n" +
		 "    (to == role2 && attr == attr2) -> updateCoh(from,to,attr,v).\n" +
		 "             lastRoleAttr(to,attr).lastEq(v==value1).\n" +
		 "             Coherence'(role1,role2,attr1,attr2,value1,v,to,attr) +\n" +
		 "\n" +
		 "             % receive case 3: some other role receives a new value\n" +
		 "    ( (to != role1 || attr != attr1) && (to != role2 || attr != attr2) ) -> updateCoh(from,to,attr,v).\n" +
		 "             lastRoleAttr(to,attr).lastEq(value1==value2).\n" +
		 "             Coherence'(role1,role2,attr1,attr2,value1,value2,to,attr)\n" + 
		 "\n" +
		 ") );\n" +


	     "\n" + 
	     "% Data transfer 'Cpq' is a basic construct of our global language:  Cpq --send--> Fpq --receive--> 1\n" + 
	     "% mcrl2 operators (sequential,option,parallel,recursion) are used to implement compositional constructs\n" + 
	     "% Our local language uses 'send' and 'receive' as its basic constructs. Tau is supported natively by mcrl2.\n" + 
	     "    \n" + 
	     "  C(from:RoleName,to:RoleName,attr:Attribute, v:Value) = \n" + 
	     "       send(from,to,attr,v).receive(from,to,attr,v); \n" + 
	     "\n" + 
	     
		 "% lock and unlock communications consist of a request and a response (acknowledge)\n" +  
		 "% NB: the ack msg is targeted at a role and includes a dummy attribute '0'\n" +
		 "  lock(from:RoleName,to:RoleName,attr:Attribute) =\n" +
		 "       C(from,to,attr,Lock).C(to,from,0, Ack);\n" +
		 "\n" +
		 "  unlock(from:RoleName,to:RoleName,attr:Attribute) =\n" + 
		 "       C(from,to,attr,Unlock).C(to,from,0,Ack);\n" +

	     "\n" + 
	     "% ------------\n" + 
	     insertProtocolDefinitions() + "\n" +
	     "% ------------\n" + 
	     "\n" + 
	     "\n" + 
	     "% shorthands added to beautify init process ;)\n" + 
	     "\n" + 	     
	     "  Role(role:RoleName) = Role'(role,lambda n:Attribute . " + 
	     Mcrl2VariableSet.initialValue + ", lambda n:Attribute . false, lambda n:Attribute . role);\n" + 
	     "  Chan(from,to:RoleName) = Channel'(from,to,[],[],0);\n" + 
	     "  Coherence(role1,role2:RoleName, attr1,attr2:Nat) = Coherence'(role1,role2,attr1,attr2," + 
	            Mcrl2VariableSet.initialValue + "," + Mcrl2VariableSet.initialValue + "," + 
	            Mcrl2VariableSet.dormantRole + ",0);\n" + 
	     "\n" + 
	     "\n" + 
	     "init\n" + 
	     "\n" + 
	     "  allow(\n" + 
	     "     { send', receive', test', lastRoleAttr,lastEq" + 
	     insertAllowDone() + "},\n" +
	     "     comm( {\n" + 
	     insertCommDone() + "\n" +
	     "        test|testProp|syncCoherenceOnTest -> test',\n" +
	     "        send|enqueue|syncCoherence -> send', receive|dequeue|updateProp|updateCoh -> receive'},\n" + 
	     "\n" + 
	     "        % processes that occur in every system:\n" + 
	     insertCoherenceInit() + " ||\n" + 
	     "        % processes that are protocol-specific:\n" +
	     insertRoleInits() + " ||\n" + 
	     "joinedprotocol \n" +
	     ") );";	    
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
			+ "   [true*.lastRoleAttr(" + cohVars(0) +").lastEq(false)]   (                % 1a: AG(last(p) AND p != q  => ...... )\n"
			+ "      [!lastRoleAttr(" + cohVars(1) +")*.lastRoleAttr("+ cohVars(0) +")] false   &&            % 1b: A(not last(p) U last(q) \n"
			+ "                                                        %     NB: remainder of Until clause \n"
			+ "                                                        %         is checked as part of 1c \n"
			+ "      [!lastRoleAttr("+ cohVars(1) +")*.lastRoleAttr("+ cohVars(1) +").lastEq(false)] false   % 1c: A(not last(q) U last(q) AND (p==q)) \n"
			+ "   )\n"
			+ "&&\n"
			+ "   % part 2:\n"
			+ "   [true*.lastRoleAttr("+ cohVars(1) +").lastEq(false)]   (\n"
			+ "          [!lastRoleAttr("+ cohVars(0) +")*.lastRoleAttr("+ cohVars(1) +")] false  &&\n"
			+ "          [!lastRoleAttr("+ cohVars(0) +")*.lastRoleAttr("+ cohVars(0) +").lastEq(false)] false\n"
			+ "   )\n"
			+ ")\n"
			+ "";
}

private String cohVars(int i) {
	return vars.coherentAttributes[i].getRole() + "," + vars.coherentAttributes[i].getAttr();
}


private String terminatesAlwaysFormulaText() {
	if (vars.protocols.size() < 2) {
		return "[(!done0)*] <true*.done0> true";
	}
	return "[(!done')*] <true*.done'> true";
}



	
}
