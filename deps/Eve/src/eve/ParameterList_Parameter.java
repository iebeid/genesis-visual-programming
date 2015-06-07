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


import parsing.GenericParser;
import parsing.Instruction;
import parsing.InstructionList;
import parsing.SyntaxRule;

/**
 *
 * @author Larry Morell
 */

public class ParameterList_Parameter extends EveInstruction {

	private static String rule = "<ParameterList> ::= <Parameter> ";
	private static SyntaxRule syntaxRule = new SyntaxRule(GRAMMAR, rule);

	public static void initialize() {  // static initialization
		GRAMMAR.addRule(syntaxRule,
				  new ParameterList_Parameter(),
				  new GenericParser(syntaxRule.lhs()));
	}
   public ParameterList_Parameter(){ }

   @Override
	public Instruction createInstruction(InstructionList il) {
		ParameterList result = new ParameterList(il.getFirst()); // wrap it
		return result;
	}

}
