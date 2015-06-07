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
import runtime.StickyNote;
import runtime.Value;

/**
 *
 * @author Larry Morell
 */
public class MultOp_Mult extends GenesisInstruction
                                 implements BinaryEval {

	private static String rule = "<MultOp> ::= * ";
	private static SyntaxRule syntaxRule = new SyntaxRule(GRAMMAR, rule);
	private final static MultOp_Mult MULT_OP__MULT = new MultOp_Mult();

	public static void initialize() {  // static initialization
		GRAMMAR.addRule(syntaxRule,
				  new MultOp_Mult(),
				  new GenericParser(syntaxRule.lhs()));
	}

	public MultOp_Mult() {
	}

	@Override
	public Instruction createInstruction(InstructionList il) {
		return MULT_OP__MULT;
	}

	@Override
	public InstructionList exec() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public StickyNote eval(StickyNote op1, StickyNote op2) {
		Value val1 = op1.getVal();
		Value val2 = op2.getVal();
		if (!(val1 instanceof DoubleVal) || !(val2 instanceof DoubleVal)) {
			System.err.println("Cannot multiply " + val1 + " and " + val2);
			System.exit(-1);
		}
		double d1 = ((DoubleVal) val1).getVal();
		double d2 = ((DoubleVal) val2).getVal();
		StickyNote sn = new StickyNote(d1 * d2);
		return sn;
	}
}
