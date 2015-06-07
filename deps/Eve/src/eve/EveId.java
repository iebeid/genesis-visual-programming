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
import parsing.SyntaxRule;
//import OriginalRuntime.EnvironmentVal;
//import OriginalRuntime.StickyNote;
import runtime.EnvironmentVal;
import runtime.StickyNote;


/**
 *
 * @author Larry Morell (lmorell@atu.edu)
 */
public class EveId extends EveInstruction {
   // ---------------- Inner classes ---------------------//
   // ---------------- Static variables ------------------//

	private static String rule = "<Id> ::= <id> ";
	private static SyntaxRule syntaxRule = new SyntaxRule(GRAMMAR, rule);

	// ---------------- Static initialization -------------//
   // ---------------- Static methods --------------------//
  public static void initialize() {  // static initialization
		GRAMMAR.addRule(syntaxRule,
				  new EveId(),
				//  new GenericParser(syntaxRule.lhs())
				  new EveIdParser()
				  );
	}
   // ---------------- Instance variables ----------------//
	private String id;
	// ---------------- Constructors ----------------------//
   public EveId() {id = "";}
	public EveId(String string) {id = string ;}
	// ---------------- Getters/Setters -------------------//

	// ---------------- Other member functions ------------//
	@Override
  public StickyNote eval(EnvironmentVal env) {
		return env.search(id);
	}
	@Override
	public String toString () {
		return id;
	}


}
