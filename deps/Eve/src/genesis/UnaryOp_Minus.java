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
public class UnaryOp_Minus extends GenesisInstruction
                                   implements UnaryEval {

	private static String rule = "<UnaryOp> ::= - ";
	private static SyntaxRule syntaxRule = new SyntaxRule(GRAMMAR, rule);
	private final static UnaryOp_Minus UNARY_OP__MINUS = new UnaryOp_Minus();

	public static void initialize() {  // static initialization
		GRAMMAR.addRule(syntaxRule,
				  new UnaryOp_Minus(),
				  new GenericParser(syntaxRule.lhs()));
	}

	public UnaryOp_Minus() {
	}

	@Override
	public Instruction createInstruction(InstructionList il) {
		return UNARY_OP__MINUS;
	}

	@Override
	public InstructionList exec() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public StickyNote eval() {
		throw new UnsupportedOperationException("eval() should not be called.");
	}

	public StickyNote eval(Instruction instruction) {
		Value val = instruction.eval().getVal();

		if (!(val instanceof DoubleVal)) {
			System.err.println("Cannot negate " + val);
			System.exit(-1);
		}
		double d = ((DoubleVal)val).getVal();
		val = new DoubleVal(-d);
      return new StickyNote(-d);
	}
}
