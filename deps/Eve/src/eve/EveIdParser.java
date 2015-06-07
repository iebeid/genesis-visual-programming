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

import parsing.Instruction;
import parsing.Scanner;
import parsing.TerminalParser;
import parsing.Token;

/**
 *
 * @author Larry Morell (lmorell@atu.edu)
 */
public class EveIdParser extends TerminalParser {
   // ---------------- Inner classes ---------------------//
   // ---------------- Static variables ------------------//
	// ---------------- Static initialization -------------//
   // ---------------- Static methods --------------------//

   // ---------------- Instance variables ----------------//

	// ---------------- Constructors ----------------------//
	public EveIdParser() {	}
	// ---------------- Getters/Setters -------------------//

	// ---------------- Other member functions ------------//
	@Override
	public Instruction parse(Scanner scanner) {

		Token current = scanner.current();  // better be a NumberToken

		Instruction instruction;
		instruction = new EveId(current.getValue());
		scanner.get();
		return instruction;
	}

}
