/*
 *  Copyright (C) 2011 Larry Morell <morell@cs.atu.edu>
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eve;

/**
 *
 * @author Larry Morell (lmorell@atu.edu)
 */

import eve.FunctionDefinition;
import parsing.GenericParser;
import parsing.Instruction;
import parsing.InstructionList;
import parsing.NopInstruction;
import parsing.SyntaxRule;
//import OriginalRuntime.StickyNote;
//import OriginalRuntime.EnvironmentVal;
//import OriginalRuntime.EnvironmentVal;
import runtime.StickyNote;
import runtime.EnvironmentVal;
import runtime.LocalEnvironmentVal;
import runtime.Value;

/**
 *
 * @author Larry Morell
 */

public class Invocation extends EveInstruction {

	private static String rule = "<Invocation> ::= <Id> ( <ParameterList> ) ";
	private static SyntaxRule syntaxRule = new SyntaxRule(GRAMMAR, rule);


	public static void initialize() {  // static initialization
		GRAMMAR.addRule(syntaxRule,
				  new Invocation(),
				  new GenericParser(syntaxRule.lhs()));
	}

	Instruction idInstruction;
	String functionName;
	CodeVal actualParameters;

   public Invocation(){ }

	@Override
	public Instruction createInstruction(InstructionList il) {
		Invocation alg = new Invocation();
		alg.idInstruction = il.getFirst();  // the method name as an instruction
		alg.functionName = alg.idInstruction.toString();  // method name as a string
		if ( ! ( il.get(2) instanceof NopInstruction)) { // actual actualParameters exist
		   ParameterList pl = ((ParameterList) il.get(2));
			alg.actualParameters = new CodeVal(pl.getInstructionList()); // the actualParameters to the method
		}
		else
			alg.actualParameters = new CodeVal(); // empty list
		return  alg;
	}

   public String toString() {
		return functionName + "( ... ) ";
	}
	/**
	 * Evaluate a method call
	 * @param env -- the local environment at the point of call
	 * @return
	 */
	@Override
	public StickyNote eval(EnvironmentVal env) {
		if (functionName.charAt(0) == '_') { // the method must be built-in
			return BaseMethods.eval(functionName, env, actualParameters.getInstructionList());
		}
		StickyNote result = NULL_NOTE;
//		// Lookup the function in env
		StickyNote functionSn = env.search(functionName); // replace with a lookup.function(functionName) later
		if (functionSn == null){
			System.err.println("No such method:" + functionName );
			System.exit(1);
		}
		// Setup the local enviroment
		LocalEnvironmentVal lenv = new LocalEnvironmentVal();
		// link it to the calling environment
		StickyNote sn = new StickyNote(env);
		lenv.insert("callingEnv",sn);

		// Link it to the function/method being called
		lenv.setCallingEnv(env);
		lenv.insert("function",functionSn);

		// Link it to the object this function is associated with
		Value containingObject = ((LocalEnvironmentVal) env).getContainingObject();
      lenv.insert("containingObject", new StickyNote(containingObject));
		lenv.setContainingObject(containingObject);

		// Link in the actualParameters and the body
	   CodeVal codeVal = (CodeVal)functionSn.getVal();
		FunctionDefinition functionDefinition =  (FunctionDefinition) codeVal.il.getFirst();
		// First get the first instruction from codeVal; it has to be a functionDef
		// by the way parsing works
		
		CodeVal formalParameters = functionDefinition.getFormalParameters();  // list of actualParameters
		CodeVal body = functionDefinition.getBody();  // list of instructions
      lenv.insert("parameters", new StickyNote(formalParameters));  // store for debugging
      lenv.insert("body", new StickyNote(body));  // store for debugging

		// For now, just temporarily, associate the formal actualParameters with the actual actualParameters
      int i = 0;
	   CodeVal fp = formalParameters;
      CodeVal ap = actualParameters;
		fp.reset();
		ap.reset();

		while (fp.on() && ap.on()) {
			String formal = ((Instruction)fp.getInstruction()).toString(); 
			StickyNote actual = ((Instruction)ap.getInstruction()).eval(env);
//			result = ((CodeVal)formal.getVal()).eval(env);
			env.insert(formal,actual);
			fp.move();
			ap.move();

		}
		InstructionList bodyInstructions = body.getInstructionList();

		for (Instruction instr: bodyInstructions){
		   result = instr.eval(env);  // evaluate each instruction
			// future: if the instruction is a _return execute a return!
			// gotta have a return instruction first!
		}


		return result;
	}

	// test to see if the invoking a user-defined function works
	public static void main (String [] args) {

		// Set up something similar to
		// Let a name 7
		// Let a.b name 9
		// Let a.f name function(n) {17}
		// Create a working env
		EnvironmentVal current = new EnvironmentVal();
      EnvironmentVal envB = new EnvironmentVal();
		envB.insert("value",new StickyNote(9));

		EnvironmentVal envA = new EnvironmentVal();

		envA.insert("value", new StickyNote(7));
		envA.insert("b",new StickyNote(envB));


		EnvironmentVal envF = new EnvironmentVal();
		InstructionList params = new InstructionList();
		Instruction param = new EveId("n");
		params.add(param);

      Instruction body = new EveNumber(17);

		envF.insert("parameters",new StickyNote(new CodeVal(param)));
		envF.insert("body",new StickyNote(new CodeVal(body)));


      envF.insert("enclosing",new StickyNote(envA));
		envA.insert("f",new StickyNote(envF));

		current.insert ("a", new StickyNote(envA));
	}
}
