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

public class ComparisonOp_EQ extends GenesisInstruction
                                      implements BinaryEval {

	private static String rule = "<ComparisonOp> ::= = ";
	private static SyntaxRule syntaxRule = new SyntaxRule(GRAMMAR, rule);
   private final static ComparisonOp_EQ COMPARISON_OP__EQ = new ComparisonOp_EQ();

	public static void initialize() {  // static initialization
		GRAMMAR.addRule(syntaxRule,
				  new ComparisonOp_EQ(),
				  new GenericParser(syntaxRule.lhs()));
	}
   public ComparisonOp_EQ(){ }

	@Override
	public Instruction createInstruction(InstructionList il) {
		return  COMPARISON_OP__EQ;
	}

	@Override
	public InstructionList exec() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public StickyNote eval(StickyNote lhs, StickyNote rhs) {
       return new StickyNote(((Compare)(lhs.getVal())).eq( rhs.getVal() ));
	}
}
