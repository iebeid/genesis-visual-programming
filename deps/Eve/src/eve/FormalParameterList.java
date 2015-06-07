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
import runtime.StickyNote;
import parsing.NopInstruction;
import runtime.EnvironmentVal;
import runtime.ListVal;

/**
 *
 * @author Larry Morell
 */

public class FormalParameterList extends EveInstruction {

	private static String rule = "<FormalParameterList> ::= <FormalParameter> <Separator> <FormalParameterList>";
	private static SyntaxRule syntaxRule = new SyntaxRule(GRAMMAR, rule);

	public static void initialize() {  // static initialization
		GRAMMAR.addRule(syntaxRule,
				  new FormalParameterList(),
				  new GenericParser(syntaxRule.lhs()));
	}

	private InstructionList il;
   public FormalParameterList(){ il = new InstructionList(); }
   public FormalParameterList (InstructionList il) {this.il = il;}
	@Override
	public Instruction createInstruction(InstructionList il) {
		FormalParameterList result = new FormalParameterList();

		Instruction first = il.getFirst();  // first operand
		Instruction second = il.get(2);      // skip separator and get rest

		// See if the second at the end or not

		if ((second instanceof FormalParameterList)) {
			FormalParameterList term = (FormalParameterList) second; // rename it
			term.il.addFirst(first);  // prepend it with the new parameter
			result.il = term.il;
		}
		else {
			result.il.addFirst(il.getFirst());  // already the correct form so keep it
		}
		this.il = result.il; // save it with the local instruction
		return result;
		
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
