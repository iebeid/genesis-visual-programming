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

import parsing.GenericParser;
import parsing.Instruction;
import parsing.InstructionList;
import parsing.SyntaxRule;
//import OriginalRuntime.EnvironmentVal;
//import OriginalRuntime.StickyNote;
import runtime.EnvironmentVal;
import runtime.TruthVal;
import runtime.StickyNote;
/**
 *
 * @author Larry Morell (lmorell@atu.edu)
 */
public class Boolean_False extends EveInstruction {

   // ---------------- Static variables ------------------//
	private static String rule = "<Boolean> ::= false";
	private static SyntaxRule syntaxRule = new SyntaxRule(GRAMMAR, rule);
   final private static StickyNote FALSE_NOTE = new StickyNote(false);
	final private static Boolean_False BOOLEAN__FALSE = new Boolean_False();

   // ---------------- Static methods --------------------//
	public static void initialize() {  // static initialization
		GRAMMAR.addRule(syntaxRule,
				  new Boolean_False(),
				  new GenericParser(syntaxRule.lhs()));
	}

   // ---------------- Instance variables ----------------//

	// ---------------- Constructors ----------------------//
   public Boolean_False () {	}
	// ---------------- Getters/Setters -------------------//

	// ---------------- Other member functions ------------//
		@Override
	public Instruction createInstruction(InstructionList il) {
   	return  BOOLEAN__FALSE;
	}
   @Override
	public StickyNote eval(EnvironmentVal env) {
		return FALSE_NOTE;
	}
}
