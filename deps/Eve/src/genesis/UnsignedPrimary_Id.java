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

/**
 *
 * @author Larry Morell (lmorell@atu.edu)
 */
package genesis;

import parsing.GenericParser;
import parsing.Instruction;
import parsing.InstructionList;
import parsing.SyntaxRule;
import runtime.EnvironmentVal;
import runtime.StickyNote;

/**
 *
 * @author Larry Morell
 */
public class UnsignedPrimary_Id extends GenesisInstruction
                                          implements Evaluate {

	private static String rule = "<UnsignedPrimary> ::= <id>";
	private static SyntaxRule syntaxRule = new SyntaxRule(GRAMMAR, rule);

	public static void initialize() {  // static initialization
		GRAMMAR.addRule(syntaxRule,
				  new UnsignedPrimary_Id(),
				  new GenericParser(syntaxRule.lhs()));
	}
	private String id;
	
   public UnsignedPrimary_Id(){ id = null; }
	public UnsignedPrimary_Id(String string) {
		id = string;
		
	}



	@Override
	public Instruction createInstruction(InstructionList il) {
		return  il.getFirst();
	}

	@Override
	public InstructionList exec() {
		throw new UnsupportedOperationException("Not supported yet.");
	}
   /**
	 *
	 * @return the value of the id from the runtime environment
	 */
	@Override


	public StickyNote eval() {
		return scope.search(id);
	}
	@Override
	public String toString () {
		return id;
	}
}
