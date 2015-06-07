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
 * Decorator pattern -- descendents of this class will add functionality to
 *    Buffers
 * @author Larry Morell
 */
abstract  class  BufferWrapper implements  AbstractBuffer {

   // ---------------- Instance variables ----------------//
	public AbstractBuffer buffer;  //

	// ---------------- Constructors ----------------------//
   BufferWrapper() { buffer = null;}
	// ---------------- Getters/Setters -------------------//
   public String getFileName() {return buffer.getFileName();}

		// ---------------- Trivial wrap methods  ------------//

		@Override
	public char charAt(int i) {
		return buffer.charAt(i);
	}
	@Override
	public char peek() {
		return buffer.peek();
	}
	@Override
	public int currentPos() {
		return buffer.currentPos();
	}
	@Override
	public boolean eof() {
		return buffer.eof();
	}
	@Override
	public boolean eoln() {
		return buffer.eoln();
	}
	@Override
	public char get() {
		return buffer.get();
	}
	@Override
	public String getBuffer() {
		return getBuffer();
	}
	@Override
	public String getId() {
		StringBuilder sb = new StringBuilder();
		skipBlanks();
		if (!buffer.eof() && ( Character.isLetter(buffer.peek()) || buffer.peek() == '_')) {
			while (!eof() && ( Character.isLetterOrDigit(buffer.peek()) || buffer.peek() == '_')) {
				sb.append(buffer.peek());
				buffer.advance();
			}
		}
		return new String (sb);
	
	}
	@Override
	public String getNumber() {
		return buffer.getNumber();
	}
	@Override
	public String getOperator() {
		return  buffer.getOperator();
	}
	@Override
	public String getToDelimiter(String delim) {
		return buffer.getToDelimiter(delim);
	}
	@Override
	public String getToEoln() {
		return buffer.getToEoln();
	}
	@Override
	public char nextChar() {
		return buffer.nextChar();
	}
	@Override
	public int size() {
		return buffer.size();
	}
	@Override
	public void skipBlanks() {
		while (!eof() && Character.isWhitespace(buffer.peek())) {
			advance();
		}
	}
	@Override
	public int lineNumber() {
		return buffer.lineNumber();
	}
   @Override
	public int columnNumber() {
		return buffer.columnNumber();
	}
	public void advance() {
		buffer.advance();
	}

}

	// ---------------- Other member functions ------------//