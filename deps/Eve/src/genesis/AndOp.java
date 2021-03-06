/*/*
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
import runtime.Value;

/**
 *
 * @author Larry Morell
 */

public class AndOp extends GenesisInstruction
                    implements BinaryEval {

	private static String rule = "<AndOp> ::= and ";
	private static SyntaxRule syntaxRule = new SyntaxRule(GRAMMAR, rule);
   private static final TruthVal TRUE = new TruthVal(true);
	public static void initialize() {  // static initialization
		GRAMMAR.addRule(syntaxRule,
				  new AndOp(),
				  new GenericParser(syntaxRule.lhs()));
	}
	private final static AndOp AND = new AndOp();
   public AndOp(){ }

	@Override
	public Instruction createInstruction(InstructionList il) {
		return AND;
	}

	@Override
	public InstructionList exec() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public StickyNote eval(StickyNote lh, StickyNote rh) {
		Value lhs = lh.getVal();
		Value rhs = rh.getVal();
		if (!(lhs instanceof TruthVal && rhs instanceof TruthVal)) {
			printError("Cannot 'and' " + lhs + " and " + rhs);
			System.exit(-1);
		}
			return new StickyNote(
				  ((TruthVal)lhs).getVal()
				  &&
				  ((TruthVal)rhs).getVal());
	
	}
}
