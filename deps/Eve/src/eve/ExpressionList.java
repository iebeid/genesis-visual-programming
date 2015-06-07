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
import runtime.ListVal;
import parsing.NopInstruction;
import runtime.EnvironmentVal;

/**
 *
 * @author Larry Morell
 */

public class ExpressionList extends EveInstruction {

	private static String rule = "<ExpressionList> ::= <Expression> <Separator> <ExpressionList>";
	private static SyntaxRule syntaxRule = new SyntaxRule(GRAMMAR, rule);

	public static void initialize() {  // static initialization
		GRAMMAR.addRule(syntaxRule,
				  new ExpressionList(),
				  new GenericParser(syntaxRule.lhs()));
	}

	private InstructionList il;
   public ExpressionList(){ il = new InstructionList(); }
   public ExpressionList (InstructionList il) {this.il = il;}
	@Override
	public Instruction createInstruction(InstructionList il) {
		ExpressionList result = new ExpressionList();

		Instruction first = il.getFirst();  // first operand
		Instruction second = il.get(2);      // skip separator and get rest

		// See if the second at the end or not

		if ((second instanceof ExpressionList)) {
			ExpressionList term = (ExpressionList) second; // rename it
			term.il.addFirst(first);  // prepend it with the new parameter
			result.il = term.il;
		}
		else {  // it
			result.il.addFirst(second);  // already the correct form so keep it
			result.il.addFirst(first);
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
		StickyNote sn = NULL_NOTE;
		for (Iterator it = il.iterator (); it.hasNext ();) {
          Instruction instruction =(Instruction) it.next ();
			  sn = instruction.eval(env);
		}
		return sn;
	}

	@Override
	public InstructionList getInstructionList() {
		return il;
	}
	@Override
	public String toString (){
		StringBuilder sb = new StringBuilder("");
		for (Instruction instr : il ) {
			sb.append(instr.toString());
			sb.append(";\n");
			sb.append("\n");
		}
		return sb.toString();
	}
}
