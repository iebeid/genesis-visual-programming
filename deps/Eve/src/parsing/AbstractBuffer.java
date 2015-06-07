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

import java.io.FileReader;

/**
 *
 * @author Larry Morell <morell@cs.atu.edu>
 */
public interface AbstractBuffer {

   AbstractBuffer copy();

	void setSource(String s);
	void setSource(FileReader fr, String fn);
   String getFileName();

	char charAt(int i);

	char peek();  // the next character that get will return

	int currentPos();
   public void advance();

	int lineNumber();  // logical result
	int columnNumber(); // logical result

	boolean eof();

	boolean eoln();

	char get();

	String getBuffer();

	String getId();

	String getNumber();

	String getOperator();

	String getToDelimiter(String delim);

	String getToEoln();

	char nextChar();

	int size();

	void skipBlanks();
}
