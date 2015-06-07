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

package eve;

/**
 *
 * @author Larry Morell (lmorell@atu.edu)
 */

import parsing.GenericParser;
import parsing.Instruction;
import parsing.InstructionList;
import parsing.SyntaxRule;
import runtime.StickyNote;
import runtime.EnvironmentVal;

/**
 *
 * @author Larry Morell
 */

public class FormalParameter_Alias extends EveInstruction {

	private static String rule = "<FormalParameter> ::= alias <Id> ";
	private static SyntaxRule syntaxRule = new SyntaxRule(GRAMMAR, rule);

	public static void initialize() {  // static initialization
		GRAMMAR.addRule(syntaxRule,
				  new FormalParameter_Alias(),
				  new GenericParser(syntaxRule.lhs()));
	}
   InstructionList il;
   public FormalParameter_Alias(){ }

	@Override
	public Instruction createInstruction(InstructionList il) {
		FormalParameter_Alias fp = new FormalParameter_Alias();
		fp.il = il;
		return fp;
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
	public StickyNote eval(EnvironmentVal env) {
		throw new UnsupportedOperationException("eval() should not be called.");
	}
}
