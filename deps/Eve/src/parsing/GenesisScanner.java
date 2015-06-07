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
 * @author Larry Morell (lmorell@atu.edu)
 */
public class GenesisScanner implements Scanner {

	// ---------------- Static variables ------------------//
	// ---------------- Static initialization -------------//
	// ---------------- Static methods --------------------//
	// ---------------- Instance variables ----------------//
	StackedBuffer buffer;   // The input sourse
	Grammar grammar;          // The grammar this scanner is written for
	GenesisScanner saved;
   Token token;               // The most recent token returned by get()
	// ---------------- Constructors ----------------------//
	public GenesisScanner(Grammar g) {
		grammar = g;
		buffer = new StackedBuffer();
		saved = null;


	}

	public GenesisScanner(Grammar g, FileReader fr, String fn) {
		this(g);
		buffer.setSource(fr, fn);

	}

	public GenesisScanner(Grammar g, String src) {
		this(g);
		buffer.setSource(src);
	}
	// ---------------- Getters/Setters -------------------//

	// ---------------- Other member functions ------------//
	public Token get() {
		// Strategy is to skip whitespace, branch on the encountered character,
		// invoke the appropriate buffer routine, convert if necessary
		// NOTE: the variable ch throughout this code denotes the character
		// the character that will be read by the next call to get()
		Token t;
		String value;
		Symbol symbol;
		buffer.skipBlanks(); // align after ws
		int lineNo = buffer.lineNumber();
		int charPos = buffer.currentPos();

		if (buffer.eof()) { // set up an EOF symbol
			symbol = Symbol.createSymbol(grammar, "");  // creates an EOF symbol
			t = new Token(symbol, buffer, lineNo, charPos);
		}
		else {
			// for each of these we must create a symbol, then form a token from it
			char ch = buffer.peek();
			if (Character.isLetter(ch) || ch == '_') {
				value = buffer.getId();
				symbol = Symbol.createSymbol(grammar, value);
//				boolean b = symbol.isKeyword();
				if (symbol.isKeyword())
					t = new KeywordToken(symbol, buffer, lineNo, charPos);
				else
					t = new IdToken(symbol, buffer, lineNo, charPos);
			}
			else if (Character.isDigit(ch)) {
				value = buffer.getNumber(); // get the string version of the number
				symbol = Symbol.createSymbol(grammar, value);
				t = new NumberToken(symbol, buffer, lineNo, charPos);
			}
			else if (ch == '"' || ch == '\'') {  // it is a literal string
				char terminator = ch;
				StringBuilder val = new StringBuilder();
				// val.append(terminator);
				buffer.advance();
				ch = buffer.peek(); // get the first char after the quote

				while (!buffer.eof() && ch != terminator) {

					if (ch == '\\') {
						buffer.advance();
						ch = buffer.peek();
						if (ch == 'n') {
							ch = '\n';
						}
						else if (ch == 't') {
							ch = '\t';
						}
						else if (ch == 'r') {
							ch = '\r';
						}
						// if the character is anything else, including a '\'
						// then just keep it
					}
					val.append(ch);
					buffer.advance();
					ch = buffer.peek();
				}
				if (ch != terminator) {
					System.err.println("Double quotes are not balanced.");
					System.err.println("There was no matching  quote("
							  + terminator + ") "
							  + "for the quote found on or before line "
							  + lineNo + '.');
					System.err.println("The mistake could well be much earlier in the algorithm.");
					System.err.println("Check all pairs of quotes to find the mistake.");

					System.exit(1);
				}
				// val.append(ch); // Append the "
				ch = buffer.get();
				symbol = Symbol.createSymbol(grammar, new String(val));
				t = new StringToken(symbol, buffer, lineNo, charPos);
			}

			else if (ch == '/') {
				int tempCounter = 0;
				ch = buffer.get();   // toss it
				ch = buffer.peek(); // peek at the next character
				if (ch == '/') {  //  a //-comment found
					buffer.getToEoln();
					tempCounter = 1;
					t = get();  // Note the recursion
				}
				else if (ch == '*') {  // sm: handle multi-line /* comments */
					boolean mlComment = true;
					ch = buffer.get();
					while (!buffer.eof() && mlComment) {
						if (ch == '*') {
							ch = buffer.get();
							if (ch == '/') {
								mlComment = false;   // Muntha error
								ch = buffer.get();
							}
						}
						else {
							ch = buffer.get();
						}
					}
					if (mlComment) {
						System.err.println("'/*' comment not closed with '*/'");
						symbol = Symbol.createSymbol(grammar, "");
						t = new Token(symbol, buffer, lineNo, charPos);
					}
					else {
						t = get();  // recursive call ... go get the token after the comment
					}
				}
				else {
					String operator = "/";
					symbol = Symbol.createSymbol(grammar, operator);
					t = new OperatorToken(symbol, buffer, lineNo, charPos);
				}
			}
			else if (ch == '#') { // single line comment
				buffer.getToEoln();
				t = get(); // note the recursion
			}
			else { // not a digit, letter, quote, or comment, must be an operator
				value = buffer.getOperator();
				symbol = Symbol.createSymbol(grammar, value);
				t = new OperatorToken(symbol, buffer, lineNo, charPos);
			}
		}
      token = t; // save the token for calls to current()
		return t;


	}
	// Future modification: note that grammar is not being copied here.
	// This means that if the grammar is modified after the copy is made,
	// then the copy will have its grammar modified as well.  This would only
	// be of consequence if in the process of backtracking we needed to undo,
	// say, the creation of a grammar rule.   This appears to be unlikely,
	// but this note will serve as a reminder that someday you will be bit
	// by this if you're not careful.
	// To fix it we need some way to copy a grammar.  This would require
	// a massive amount of computation, so it is being avoided here.

	public Scanner copy() {
		GenesisScanner gs = new GenesisScanner(grammar);
		gs.buffer = (StackedBuffer) buffer.copy();
		return gs;

	}

	public void mark() {
		saved = (GenesisScanner) copy();


	}

	public void reset() {
		grammar = saved.grammar;
		buffer = saved.buffer;
		saved = null;


	}

	public boolean eof() {
		buffer.skipBlanks();
		return buffer.eof();

	}

	// Test the scanner
	public static void main(String[] args) {
		// First set up the grammar and some simple rules
		Grammar grammar = Grammar.buildGrammar();
		Token t;
		String[] testInput = new String[]{
			"+ - += =+ /- -/ ", // various operators
			"a b c move def g", // ids
			"17 17.3 49.8 0 12345", // numbers
			"// This is a comment\n1 2 3", // single-line comment
			"#  this is a comment\na b c", // single-line comment
			"/* multi\nline\n  comment*/ with other ", // multi-line comment
			"'a string'",
			"'a string\n across several lines '",
			"\"a string\"",
			"\"a string \n across \n several lines\"",

		};
		for (int i = 0; i < testInput.length; i++) {
			String source = testInput[i];
			System.out.println("Source =" + source);

			GenesisScanner scanner = new GenesisScanner(grammar, source);

			do {
				t = scanner.get();
				System.out.println(t);
			} while (!scanner.eof());
		}
	}

	public Token current() {
		return this.token;
	}
}
