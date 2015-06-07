/*
 *  Copyright (C) 2010 Larry Morell <morell@cs.atu.edu>
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

package genesis;

/**
 *
 * @author Larry Morell (lmorell@atu.edu)
 */

import parsing.GenericParser;
import parsing.Instruction;
import parsing.InstructionList;
import parsing.SyntaxRule;
import runtime.StickyNote;
import runtime.TruthVal;

/**
 *
 * @author Larry Morell
 */

public class NotList_Multiple extends GenesisInstruction 
		                                 implements UnaryEval {

	private static String rule = "<NotList> ::= not <NotList>";
	private static SyntaxRule syntaxRule = new SyntaxRule(GRAMMAR, rule);

	public static void initialize() {  // static initialization
		GRAMMAR.addRule(syntaxRule,
				  new NotList_Multiple(),
				  new GenericParser(syntaxRule.lhs()));
	}

	InstructionList il;
   public NotList_Multiple(){ }

	@Override
	public Instruction createInstruction(InstructionList il) {

		NotList_Multiple notList = new NotList_Multiple();

		Instruction first = il.getFirst();
		Instruction second = il.get(1);
		// See if the second is a UnaryOpList_Multiple
		if (second instanceof NotList_Multiple) {
			NotList_Multiple list = (NotList_Multiple) second;
			list.il.addFirst(first);
			notList.il = list.il;
		}
		else {
			notList.il = il;
		}
		this.il = notList.il;
		return notList;
	}

	@Override
	public InstructionList exec() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public StickyNote eval(Instruction theInstruction) {
	boolean negative = false;
		for (Instruction instruction : il) {
				negative = !negative;  // flip it
		}
		StickyNote sn = theInstruction.eval();
		if (!(sn.getVal() instanceof TruthVal)) {
			System.err.println("Attempting to negate a non-truth value:" + sn);
			System.exit(-1);
		}
		if (negative) {
			boolean b = ((TruthVal) sn.getVal()).getVal();
			sn = new  StickyNote(!b);
		}
		return sn;
	}
}
