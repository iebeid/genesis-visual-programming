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
import runtime.StickyNote;
import runtime.TruthVal;

/**
 *
 * @author Larry Morell
 */
public class NotList_Single extends GenesisInstruction
                                     implements UnaryEval {

	private static String rule = "<NotList> ::= not";
	private static SyntaxRule syntaxRule = new SyntaxRule(GRAMMAR, rule);
	private final static NotList_Single NOT_LIST__SINGLE = new NotList_Single();

	public static void initialize() {  // static initialization
		GRAMMAR.addRule(syntaxRule,
				  new NotList_Single(),
				  new GenericParser(syntaxRule.lhs()));
	}

	public NotList_Single() {
	}

	@Override
	public Instruction createInstruction(InstructionList il) {
		return NOT_LIST__SINGLE;
	}

	@Override
	public InstructionList exec() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	final StickyNote TRUE = new StickyNote(true);
	final StickyNote FALSE = new StickyNote(false);

	@Override
	public StickyNote eval(Instruction instruction) {
		StickyNote sn = instruction.eval();
		StickyNote val = sn;
		if (!(val.getVal() instanceof TruthVal)) {
			System.err.println("Cannot negate " + val);
			System.exit(-1);

		}
		if (((TruthVal) val.getVal()).getVal()) {
			return FALSE;
		}
		else {
			return TRUE;
		}
	}
}
