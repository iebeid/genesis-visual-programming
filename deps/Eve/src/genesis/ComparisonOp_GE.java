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
import runtime.Compare;
import runtime.TruthVal;
import runtime.StickyNote;

/**
 *
 * @author Larry Morell
 */

public class ComparisonOp_GE extends GenesisInstruction
                                      implements BinaryEval {

	private static String rule = "<ComparisonOp> ::= >= ";
	private static SyntaxRule syntaxRule = new SyntaxRule(GRAMMAR, rule);
   private final static ComparisonOp_GE COMPARISON_OP__GE = new ComparisonOp_GE();

	public static void initialize() {  // static initialization
		GRAMMAR.addRule(syntaxRule,
				  new ComparisonOp_GE(),
				  new GenericParser(syntaxRule.lhs()));
	}
   public ComparisonOp_GE(){ }

	@Override
	public Instruction createInstruction(InstructionList il) {
		return  COMPARISON_OP__GE;
	}

	@Override
	public InstructionList exec() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public StickyNote eval(StickyNote lhs, StickyNote rhs) {
      return new StickyNote(((Compare)(lhs.getVal())).ge( rhs.getVal() ));
	}
}
