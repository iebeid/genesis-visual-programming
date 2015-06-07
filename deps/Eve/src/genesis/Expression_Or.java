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

/**
 *
 * @author Larry Morell
 */
public class Expression_Or extends GenesisInstruction {

	private static String rule = "<Expression> ::= <ConditionTerm> <OrOp> <Expression>  ";
	private static SyntaxRule syntaxRule = new SyntaxRule(GRAMMAR, rule);

	public static void initialize() {  // static initialization
		GRAMMAR.addRule(syntaxRule,
				  new Expression_Or(),
				  new GenericParser(syntaxRule.lhs()));
	}
	private InstructionList il;

	public Expression_Or() {
	}

	@Override
	public Instruction createInstruction(InstructionList il) {
		Expression_Or exp = new Expression_Or();

		Instruction first = il.getFirst();  // first operand
		Instruction second = il.get(1);      // binary operator
		Instruction third = il.get(2);      // second operand
		// See if the third is a Term

		if (third instanceof Expression_Or) {
			Expression_Or term = (Expression_Or) third;
			term.il.addFirst(second);
			term.il.addFirst(first);
			exp.il = term.il;
		}
		else {
			exp.il = il;
		}
		this.il = exp.il; // save it with the local instruction
		return exp;

	}

	@Override
	public InstructionList exec() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public StickyNote eval() {
				StickyNote accumulator = il.getFirst().eval();
		int n = 1;
		while (n < il.size()) {
			// Process a pair (operator, operand), applying the operator to accumulator
			// and the operand
			BinaryEval operator = (BinaryEval) il.get(n);
			StickyNote operand = il.get(n + 1).eval();
			accumulator = operator.eval(accumulator, operand);
			n = n + 2;
		}
		return accumulator;
	}
}
