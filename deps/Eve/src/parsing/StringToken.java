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
public class StringToken extends Token {

   // ---------------- Instance variables ----------------//
   String value;  // intentional shadowing; inherited value must be "<string>"
	// ---------------- Constructors ----------------------//
   public StringToken (Symbol s, AbstractBuffer b, int lineno, int charpos) {
		super(s,b,lineno,charpos);
		value = s.getValue();
	   symbol = Symbol.createSymbol(s.getGrammar(), "<string>");  // set the inherited value
	}
	


	// ---------------- Getters/Setters -------------------//
    public String getString() {return value; }
	// ---------------- Other member functions ------------//
	@Override
	public String toString () {return "[" + value + "]"; }

}