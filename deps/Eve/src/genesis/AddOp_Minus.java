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
import runtime.Number;
import runtime.DoubleVal;
import runtime.StickyNote;

/**
 *
 * @author Larry Morell
 */

public class AddOp_Minus extends GenesisInstruction
                                 implements BinaryEval {

	private static String rule = "<AddOp> ::= - ";
	private static SyntaxRule syntaxRule = new SyntaxRule(GRAMMAR, rule);
	private final static AddOp_Minus ADD_OP_MINUS = new AddOp_Minus();

	public static void initialize() {  // static initialization
		GRAMMAR.addRule(syntaxRule,
				  new AddOp_Minus(),
				  new GenericParser(syntaxRule.lhs()));
	}
   public AddOp_Minus(){ }

	@Override
	public Instruction createInstruction(InstructionList il) {
		return  ADD_OP_MINUS;
	}

	@Override
	public InstructionList exec() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public StickyNote eval(StickyNote s1, StickyNote s2) {
		StickyNote answer = null;
      try {
          DoubleVal d1 = (DoubleVal)s1.getVal();
          DoubleVal d2 = (DoubleVal)s2.getVal();
   		 answer = new StickyNote( d1.sub(d2));
       }
		 catch (ClassCastException e){
			 printError("Cannot subtract " +  s2 + " from " + s1);
		 }
       return answer;
	}
}
