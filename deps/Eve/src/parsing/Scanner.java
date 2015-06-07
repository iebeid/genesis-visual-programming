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
 * Provides a rudimentary mechanism for mapping strings found in a input stream
 * to tokens.
 * @author Larry Morell
 */
  public interface  Scanner {
   /**
	 *
	 * @return The token most recently retrieved via get()
	 */
   public Token current();
	/**
	 * returns the next token in the input stream associated with the scanner
	 * @return
	 */
	public Token get();

	/**
	 * Returns a copy of the state of the scanner that will not be modified
	 * by further scanner activity.
	 * @return
	 */
   public Scanner copy();

	/**
	 * Save the current state of the scanner for later
	 */
	public void mark();

	/**
	 * Restores the state to that previously marked
	 */
	public void reset();
   /**
	 * 
	 * @return Returns true iff no more tokens can be read by the scanner.
	 * Note that this may advance the read point past existing whitespace
	 */
	public boolean eof();
}


