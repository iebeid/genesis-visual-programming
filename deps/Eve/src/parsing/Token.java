package parsing;
/**
 * The Token class associates strings with their source (filename, line, pos)
 * and symbol, and classifies them into one of the following:
 * <ul>
 * <li>&lt;id>  -- a char followed by one or more chars, digits, or underscores</li>
 * <li>&lt;no -- a typical floating point of integer literal </li>
 * <li>&lt;string -- a quote (single or double) followed by zero or more intervening chars
 *      followed by a matching quote (single or double; special characters must be escaped by a backslash.
 *      These include:
 *      <ul>
 *         <li>a backslash </li>
 *         <li>a double quote (if the opening quote is a double quote)</li>
 *         <li>a single quote (if the opening quote is a single quote)</li>
 *         <li>a newline</li>
 *      </ul>
 * </li>
 * <li>&lt;char> </li>
 * <li>&lt;operator</li>
 * </ul>
 *
 * @author Larry Morell <morell@cs.atu.edu>
 */

public class Token {

	//-----------------  Member variables -----------------//

   String value ;             // The string	extracted from the buffer

	protected Symbol symbol;  // the classification of this string
	protected AbstractBuffer buffer;  // the buffer from whence the token was extracted
   protected String fileName;          // the name of the file from whence this token was extracted
   protected int lineNo;                 // the line number in the source file
	protected int charPos;               // the character position in the source file

   //--------------------- Constructors -----------------//

	public Token(Symbol s, AbstractBuffer b, int lineno, int charpos) {
		value = s.getValue(); // not really needed, may save time
		symbol = s;
		buffer = b;
		symbol = s;
		charPos = charpos;
		lineNo = lineno;
		fileName = b.getFileName();
	}



   // Getters
	public AbstractBuffer getBuffer() {
		return buffer;
	}

	public int getCharPos() {
		return charPos;
	}

	public String getFileName() {
		return fileName;
	}

	public int getLineNo() {
		return lineNo;
	}

	public Symbol getSymbol() {
		return symbol;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "[" + symbol + "]";
	}
}
