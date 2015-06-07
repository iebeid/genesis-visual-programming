/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package parsing;

/**
 *
 * @author Larry Morell
 */
public class Source {
   String source;
	int lineNumber;
	int charPos;
	String filename;

	public Source() {
		source = "";
		lineNumber = 0;
		charPos = 0;
		filename = "Unknown";
	}

	public Source(String source, int lineNumber, int charPos, String fileName) {
		this.source = source;
		this.lineNumber = lineNumber;
		this.charPos = charPos;
		this.filename = fileName;
	}

}
