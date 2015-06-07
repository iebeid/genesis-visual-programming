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

import parsing.Instruction;
import parsing.InstructionList;
import parsing.SyntaxRule;
//import OriginalRuntime.StickyNote;
//import OriginalRuntime.EnvironmentVal;
import runtime.EnvironmentVal;
import runtime.StickyNote;
/**
 *
 * @author Larry Morell
 */

public class EveString extends EveInstruction {

	private static String rule = "<EveString> ::= <string> ";
	private static SyntaxRule syntaxRule = new SyntaxRule(GRAMMAR, rule);

	public static void initialize() {  // static initialization
		GRAMMAR.addRule(syntaxRule,
				  new EveString(),
				//  new GenericParser(syntaxRule.lhs())
				  new EveStringParser()
				  );
	}
	private String eveString;
	StickyNote stringNote;

   public EveString(){  eveString = ""; stringNote = new StickyNote("");}
   public EveString(String s) {
		eveString = s;
		stringNote = new StickyNote(s);
	}

	

	@Override
	public String value() {return eveString;}

	@Override
	public Instruction createInstruction(InstructionList il) {
		return  il.getFirst();
	}

	@Override
	public InstructionList exec() {
		throw new UnsupportedOperationException("Not supported yet.");
	}


	@Override
	public StickyNote eval() {
		return stringNote;
	}
	@Override
	public StickyNote eval(EnvironmentVal env) {
		return stringNote;
	}
}
