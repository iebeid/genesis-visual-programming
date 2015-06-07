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
package eve;

/**
 *
 * @author Larry Morell (lmorell@atu.edu)
 */
import parsing.GenericParser;
import parsing.Instruction;
import parsing.InstructionList;
import parsing.SyntaxRule;
//import OriginalRuntime.EnvironmentVal;
//import OriginalRuntime.StickyNote;
import runtime.StickyNote;
import runtime.EnvironmentVal;
import runtime.StringVal;
/**
 *
 * @author Larry Morell
 */
public class UnaryOp_Plus extends EveInstruction
                                  implements UnaryEval {

	private static String rule = "<UnaryOp> ::= + ";
	private static SyntaxRule syntaxRule = new SyntaxRule(GRAMMAR, rule);
	private final static UnaryOp_Plus UNARY_OP__PLUS = new UnaryOp_Plus();


	public static void initialize() {  // static initialization
		GRAMMAR.addRule(syntaxRule,
				  new UnaryOp_Plus(),
				  new GenericParser(syntaxRule.lhs()));
	}

	public UnaryOp_Plus() {
	}

	@Override
	public Instruction createInstruction(InstructionList il) {
		return UNARY_OP__PLUS;
	}

	@Override
	public InstructionList exec() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public StickyNote eval(EnvironmentVal env, Instruction instruction) {
		StickyNote sn = instruction.eval();
		return sn;
	}
	final StickyNote PLUS = new StickyNote("+");
	@Override
   public StickyNote eval(EnvironmentVal env){
		return PLUS;
	}
}
