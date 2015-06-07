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
import runtime.DoubleVal;
import runtime.StickyNote;
import runtime.Value;

/**
 *
 * @author Larry Morell
 */
public class UnaryOpList_Multiple extends GenesisInstruction
		  implements UnaryEval {

	private static String rule = "<UnaryOpList> ::= <UnaryOp> <UnaryOpList>";
	private static SyntaxRule syntaxRule = new SyntaxRule(GRAMMAR, rule);

	public static void initialize() {  // static initialization
		GRAMMAR.addRule(syntaxRule,
				  new UnaryOpList_Multiple(),
				  new GenericParser(syntaxRule.lhs()));
	}
	private InstructionList il;

	public UnaryOpList_Multiple() {
	}

	public UnaryOpList_Multiple(Instruction operator, Instruction instruction) {
	}

	@Override
	public Instruction createInstruction(InstructionList il) {

		UnaryOpList_Multiple unaryOpList = new UnaryOpList_Multiple();

		Instruction first = il.getFirst();
		Instruction second = il.get(1);
		// See if the second is a UnaryOpList_Multiple
		if (second instanceof UnaryOpList_Multiple) {
			UnaryOpList_Multiple uol = (UnaryOpList_Multiple) second;
			uol.il.addFirst(first);
			unaryOpList.il = uol.il;
		}
		else {
			unaryOpList.il = il;
		}
		this.il = unaryOpList.il;
		return unaryOpList;
	}

	@Override
	public InstructionList exec() {
		throw new UnsupportedOperationException("Not supported yet.");


	}

	public StickyNote eval(Instruction theInstruction) {
		// walk down the list and reduce it to a single sign
		boolean negative = false;
		for (Instruction instruction : il) {
			if (instruction instanceof UnaryOp_Minus) {
				negative = !negative;  // flip it
			}
		}
		Value sn = theInstruction.eval().getVal();
		if (!(sn instanceof DoubleVal)) {
			System.err.println("Attempting to negate a non-number:" + sn);
			System.exit(-1);
		}
		if (negative) {
			double d = ((DoubleVal) sn).getVal();
			sn = new DoubleVal(-d);
		}
		return new StickyNote(sn);
	}

}
