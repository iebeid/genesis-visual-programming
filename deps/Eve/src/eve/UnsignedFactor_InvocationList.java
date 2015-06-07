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

import java.util.Iterator;
import parsing.GenericParser;
import parsing.Instruction;
import parsing.InstructionList;
import parsing.NopInstruction;
import parsing.SyntaxRule;
//import OriginalRuntime.StickyNote;
//import OriginalRuntime.EnvironmentVal;
//import OriginalRuntime.GenesisList;
import runtime.StickyNote;
import runtime.EnvironmentVal;
import runtime.ListVal;
/**
 *
 * @author Larry Morell
 */

public class UnsignedFactor_InvocationList extends EveInstruction {

	private static String rule = "<UnsignedFactor> ::= <Primary> . <InvocationList> ";
	private static SyntaxRule syntaxRule = new SyntaxRule(GRAMMAR, rule);

	public static void initialize() {  // static initialization
		GRAMMAR.addRule(syntaxRule,
				  new UnsignedFactor_InvocationList(),
				  new GenericParser(syntaxRule.lhs()));
	}

	InstructionList il;
   public UnsignedFactor_InvocationList(){ }

	@Override
	public Instruction createInstruction(InstructionList il) {
		// il will be of the form [Primary] [.] [InvocationList]
		// Need to put the [Primary] on the front of the the [InvcationList]
		((EveInstruction) il.get(2)).getInstructionList().addFirst(il.getFirst());
		// --- il.get(2) = [InvocationList] ------------ //     =[Primary]
		// And return it
		return il.get(2);
	}

	@Override
	public StickyNote eval(EnvironmentVal env) {
		ListVal list = new ListVal();
		for (Iterator it = il.iterator (); it.hasNext ();) {
          Instruction instruction =(Instruction) it.next ();
			 if (! (instruction instanceof NopInstruction)) {
				 StickyNote sn = instruction.eval(env);

				 list.insert(sn);
			 }
		}
		return new StickyNote(list);
	}

	@Override
	public InstructionList getInstructionList () {
		return il;
	}
}
