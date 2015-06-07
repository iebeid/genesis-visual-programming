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
import runtime.StickyNote;

/**
 *
 * @author Larry Morell
 */

public class Let extends GenesisInstruction {

	private static String rule = "<Let> ::= Let <Reference> <AssociationOp> <Expresssion>";
	private static SyntaxRule syntaxRule = new SyntaxRule(GRAMMAR, rule);
	private Instruction lhs;
	private BinaryEval  operator;
	private Instruction rhs;

	public static void initialize() {  // static initialization
		GRAMMAR.addRule(syntaxRule,
				  new Let(),
				  new GenericParser(syntaxRule.lhs()));
	}
   public Let(){ }

	@Override
	public Instruction createInstruction(InstructionList il) {
		Let let = new Let();
		let.lhs = il.getFirst();
		let.operator = (BinaryEval) il.get(1);
		let.rhs = il.get(2);
		return  let;
	}

	@Override
	public InstructionList exec() {
       throw new UnsupportedOperationException("eval() should not be called.");
	}

	@Override
	public StickyNote eval() {
   	StickyNote accumulator = operator.eval(lhs.eval(), rhs.eval());
		return accumulator;
	}
}
