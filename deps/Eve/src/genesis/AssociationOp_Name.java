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

package genesis;

/**
 *
 * @author Larry Morell (lmorell@atu.edu)
 */

import parsing.GenericParser;
import parsing.Instruction;
import parsing.InstructionList;
import parsing.SyntaxRule;
// import runtime.StickyNote;
import runtime.StickyNote;


/**
 *
 * @author Larry Morell
 */

public class AssociationOp_Name extends GenesisInstruction 
		  implements BinaryEval {

	private static String rule = "<AssociationOp_Name> ::= name ";
	private static SyntaxRule syntaxRule = new SyntaxRule(GRAMMAR, rule);

	public static void initialize() {  // static initialization
		GRAMMAR.addRule(syntaxRule,
				  new AssociationOp_Name(),
				  new GenericParser(syntaxRule.lhs()));
	}
   public AssociationOp_Name(){ }

	@Override
	public Instruction createInstruction(InstructionList il) {
		return  il.getFirst();
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
	public StickyNote eval(StickyNote op1, StickyNote op2) {
		/*
		 * PLEASE NOTE THAT THIS IS INCOMPLETE !!!!!!
		StickyNote val1 = op1;
		StickyNote val2 = op2;
		if (!(val1 instanceof DoubleVal) || !(val2 instanceof DoubleVal)) {
			System.err.println("Cannot multiply " + val1 + " and " + val2);
			System.exit(-1);
		}
		double d1 = ((DoubleVal) val1);
		double d2 = ((DoubleVal) val2);
		StickyNote sn = new StickyNote(d1 * d2);
		return sn;

		 */
		return null;
	}
}
