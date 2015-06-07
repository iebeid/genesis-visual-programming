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


import java.util.Iterator;
import parsing.GenericParser;
import parsing.Instruction;
import parsing.InstructionList;
import parsing.SyntaxRule;
//import OriginalRuntime.StickyNote;
//import OriginalRuntime.EnvironmentVal;
//import OriginalRuntime.GenesisList;
import runtime.StickyNote;
import runtime.EnvironmentVal;
import parsing.NopInstruction;
import runtime.ListVal;

/**
 *
 * @author Larry Morell
 */

public class InvocationList extends EveInstruction {

	private static String rule = "<InvocationList> ::= <Invocation> . <InvocationList>";
	private static SyntaxRule syntaxRule = new SyntaxRule(GRAMMAR, rule);

	public static void initialize() {  // static initialization
		GRAMMAR.addRule(syntaxRule,
				  new InvocationList(),
				  new GenericParser(syntaxRule.lhs()));
	}

	private InstructionList il;
   public InvocationList(){}
	public InvocationList (Instruction inv) { il = new InstructionList();
	    il.add(inv);
	}

   public InvocationList (InstructionList il) {this.il = il;}
	@Override
	public Instruction createInstruction(InstructionList il) {
		this.il = ((InvocationList) il.get(2)).getInstructionList();
		this.il.addFirst(il.getFirst());  // omit the separator
		return this;
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
	public InstructionList getInstructionList() {
		return il;
	}
}
