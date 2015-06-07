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
import parsing.SyntaxRule;
//import OriginalRuntime.Environment;
//import OriginalRuntime.StickyNote;
import runtime.StickyNote;
/**
 *
 * @author Larry Morell (lmorell@atu.edu)
 */
public class Primary_String extends EveInstruction {
   // ---------------- Inner classes ---------------------//
   // ---------------- Static variables ------------------//
	private static String rule = "<Primary> ::= <EveString>";
	private static SyntaxRule syntaxRule = new SyntaxRule(GRAMMAR, rule);
  // ---------------- Static methods --------------------//
	public static void initialize() {  // static initialization
		GRAMMAR.addRule(syntaxRule,
				  new Primary_String(),
				  new GenericParser(syntaxRule.lhs()));
	}
	// ---------------- Static initialization -------------//
 
   // ---------------- Instance variables ----------------//
   private String string;
	private StickyNote stringNote;
	// ---------------- Constructors ----------------------//
 

	private Primary_String() {
		string = "";
	}
	// ---------------- Getters/Setters -------------------//

	// ---------------- Other member functions ------------//

}
