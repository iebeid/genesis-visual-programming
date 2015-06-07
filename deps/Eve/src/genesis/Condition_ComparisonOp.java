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

public class Condition_ComparisonOp extends GenesisInstruction
                                                {

	private static String rule = "<Condition> ::= <ArithmeticExpression> <ComparisonOp> <ArithmeticExpression>";
	private static SyntaxRule syntaxRule = new SyntaxRule(GRAMMAR, rule);

	public static void initialize() {  // static initialization
		GRAMMAR.addRule(syntaxRule,
				  new Condition_ComparisonOp(),
				  new GenericParser(syntaxRule.lhs()));
	}

	// Instance variables
	Instruction first;
	Instruction second;
	Instruction third;
   public Condition_ComparisonOp(){ }


	@Override
	public Instruction createInstruction(InstructionList il) {
      Condition_ComparisonOp instr = new Condition_ComparisonOp();
		instr.first = il.getFirst();
		instr.second = il.get(1);
		instr.third = il.get(2);
		return  instr;
	}

	@Override
	public InstructionList exec() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public StickyNote eval() {
      StickyNote s1 = first.eval();
      StickyNote s3 = third.eval();
		return ((BinaryEval)second).eval(s1,s3);
	}
}
