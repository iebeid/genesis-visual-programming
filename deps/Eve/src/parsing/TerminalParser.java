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
package parsing;

/**
 *
 * @author Larry Morell (lmorell@atu.edu)
 */
public class TerminalParser extends Parser {
	// ---------------- static variables  ----------------------//
//	final static private Instruction NOP_INSTRUCTION =
//			  new NopInstruction();
	// ---------------- Constructors ----------------------//
	public TerminalParser() {}
	public TerminalParser(Symbol s) {
		symbol = s;
	}

	@Override
	public Instruction  parse(Scanner scanner) {
		// Nothing to do.  The keyword has already been recognized

//		Token current = scanner.current();
//		Symbol s = current.getSymbol();
//
		
//		Instruction instruction = NOP_INSTRUCTION;
		Instruction instruction = new NopInstruction(scanner.current().toString());
		scanner.get();
		return instruction;
	}


	// ---------------- Getters/Setters -------------------//
	// ---------------- Other member functions ------------//
}
