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

import parsing.GenericParser;
import parsing.Instruction;
import parsing.InstructionList;
import parsing.NopInstruction;
import parsing.SyntaxRule;
//import OriginalRuntime.EnvironmentVal;
//import OriginalRuntime.StickyNote;
import runtime.StickyNote;
import runtime.EnvironmentVal;
/**
 *
 * @author Larry Morell
 */

public class Invocation_Qualified extends EveInstruction {

	private static String rule = "<Invocation> ::= <Factor> . <id> ( <ParameterList> ) ";
	private static SyntaxRule syntaxRule = new SyntaxRule(GRAMMAR, rule);

   Instruction id1Instruction;
	Instruction id2Instruction;
	String id1, id2;
	InstructionList parameters;
	public static void initialize() {  // static initialization
		GRAMMAR.addRule(syntaxRule,
				  new Invocation_Qualified(),
				  new GenericParser(syntaxRule.lhs()));
	}
   public Invocation_Qualified(){ }

	@Override
	public Instruction createInstruction(InstructionList il) {
		Invocation_Qualified alg = new Invocation_Qualified();
		alg.id1Instruction = il.getFirst();  // the environment in which to execute the method
		id1 = id1Instruction.eval().toString();  // extract the environment name
		id2 = id2Instruction.eval().toString();  // extract the method name
		alg.id2Instruction = il.get(2);       // the method name
		if ( ! ( il.get(4) instanceof NopInstruction)) { // actual parameters exist
		   ParameterList pl = ((ParameterList) il.get(2));
			alg.parameters = pl.getInstructionList(); // the parameters to the method
		}
		else
			alg.parameters = null;
		
		return  alg;
	}

	@Override
	public InstructionList exec() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public StickyNote eval() {
		throw new UnsupportedOperationException("eval() should not be called.");
	}
	@Override
	public StickyNote eval(EnvironmentVal env) {
      if (id2.charAt(0) == '_') { // the method being called is built-in
			// BaseMethods.eval(id1, env, parameterlistInstruction);
  		}
		throw new UnsupportedOperationException("eval() should not be called.");
	}

}
