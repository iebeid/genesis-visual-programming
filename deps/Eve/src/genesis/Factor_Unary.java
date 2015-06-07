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

import eve.*;
import genesis.GenesisInstruction;
import genesis.GenesisInstruction;
import genesis.UnaryEval;
import genesis.UnaryEval;
import parsing.GenericParser;
import parsing.Instruction;
import parsing.InstructionList;
import parsing.SyntaxRule;
import runtime.StickyNote;

/**
 *
 * @author Larry Morell
 */

public class Factor_Unary extends GenesisInstruction {

	private static String rule = "<Factor> ::= <UnaryOpList> <Primary>";
	private static SyntaxRule syntaxRule = new SyntaxRule(GRAMMAR, rule);

	public static void initialize() {  // static initialization
		GRAMMAR.addRule(syntaxRule,
				  new Factor_Unary(),
				  new GenericParser(syntaxRule.lhs()));
	}

   private Instruction unaryOpList;
	private Instruction primary;
   public Factor_Unary(){ }

	@Override
	public Instruction createInstruction(InstructionList il) {
      Factor_Unary fu = new Factor_Unary();
		fu.unaryOpList = il.getFirst();
		fu.primary = il.get(1);
		return  fu;
	}

	@Override
	public InstructionList exec() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public StickyNote eval() {
        return ((UnaryEval)unaryOpList).eval(primary);
	}
}
