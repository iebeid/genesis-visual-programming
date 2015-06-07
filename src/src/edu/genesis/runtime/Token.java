/* Token.java -- Scanner for Genesis 

 Modification History 

 The modification history below is not completely accurate, but close.
 Entries here are for new constructs in the language that primarily affected the scanner.
 See the Modification history for other files for details that relate to those files

 Spring, 2003 -- Original version written by Andy Bostian, modeled
 after C++ scanners written by Larry Morell, modeled
 after the Wirth's style
 April-May, 2004 -- Adapted to Genesis 0.1 by Larry Morell primarily
 by moving the token constant names to the Parser
 and adding a dozen or so new tokens
 IO by Wes Potts to use GenesisIO package
 8/24/04 -- ljm: Fixed infinite loop when encountering unbalance double quote
 6/19/2004 -- ljm: Modified to ignore ',' and ';' 
 9/22/2004 -- sm: added /* ... */ /* comments and error processing for floating point numbers
 10/01/2004 -- ljm: added echo (print w/o newline)
 11/19/2005 -- ljm: added // as a comment
 5/24/2006  -- ch: (chad harlan) incorporated -- comments, mod using %
 7/28/06 -- ljm: changed 'otherwise' to return itself rather than truesym
 3/2/07 -- ljm: threw out "of" as a noise word (making length of(l) == length(l)
 4/19/07 -- ljm: \n, \t, \r interpreted as such in strings
 6/25/07 -- ljm: fixed it so "of" could occur singularly between parameters 'First (3) of (L)
 1/20/08 -- ljm: Removed (most) instances of GenesisIO
 */

package edu.genesis.runtime;

import java.io.*;
import java.util.*;

public class Token {

    class PositionInfo { // For tracking the position in scanning

        int lineNo;
        String fileName;
        int charPos;

        public PositionInfo(String fname, int ln, int cp) {
            fileName = fname;
            lineNo = ln;
            charPos = cp;
        }

        String fileName() {
            return fileName;
        }

        int lineNo() {
            return lineNo;
        }

        int charPos() {
            return charPos;
        }
    }
    static Stack<PositionInfo> positionInfoStack = new Stack<>();
    public static boolean debug = false;  // set this to true for massive debugging output
    public static char ch; //global ch 
    public int tokenType;
    public String val;
    public static String fileName;
    static InputStream f = System.in;
    static boolean eof = false;        // set to true when eof is encountered
    static boolean commaBreak = false; // set to true when , or ; is encountered
    static boolean lineBreak = false;  // set to true when \n is encounteredd 
    static boolean semiBreak = false;  // set to true when \n is encounteredd 
    // 6/9/04
    // Wes Potts
    //static GenesisIO in;
    // static int charPos = 1;
    // static int lineNo = 1;
    static int tempCounter = 0;
    static boolean newline = false;
    //public int tokenLineNo;
    // public int tokenCharPos;
    static int bufferPos = 0;  // location within the buffer
    static SourcePgm sourcePgm = null;
    // 6/9/04
    // Wes Potts
    //
    //public Token (String filename)   {

    public Token(String fn) {
        val = "";
        ch = ' ';
        tokenType = Parser.numberSym;
        sourcePgm = new SourcePgm(fn);
        //System.out.println ("Token created: " + this);
    }

    public Token(Token t) {
        val = t.val;
        ch = Token.ch;
        tokenType = t.tokenType;
        sourcePgm = Token.sourcePgm;
        //System.out.println ("Token created: " + this);
    }

    int parseSpace(String str, int index) { // sm
        while (index < (str.length() - 1) && str.substring(index, index + 1).equals(" ")) {
            index++;
        }
        return index;
    }

    public void setSource(String fn) {
        if (sourcePgm == null) {
            sourcePgm = new SourcePgm(fn);
        } else {
            sourcePgm.setFileName(fn);
        }
    }

    public void processInput() {
        if (debug) {
            System.out.println("In Token constructor (String filename) ");
        }
        val = "";
        ch = ' ';
        tokenType = Parser.numberSym;   // Random choice
        sourcePgm.setup();
        // in = io;
        //appendInput(io,fileName); 
    }
    // A stack of size 1
    static int marklineNo;
    static int markcharPos;
    static char markch;
    static int markpos;
    static int marktokenType;
    static String markval;
    static SourcePgm marksourcePgm;

    void mark() {
        // in.mark(100); 
        //debug=true;
        marksourcePgm = new SourcePgm(sourcePgm);  // Save copy 
        //marklineNo = sourcePgm.lineNo();
        ///markcharPos = sourcePgm.charPos();
        markch = ch;
        markpos = bufferPos;
        marktokenType = tokenType;
        markval = val;
        if (debug) {
            System.out.println("Marking input stream");
        }
        if (debug) {
            System.out.println("  ch == '" + ch + "'");
        }
        if (debug) {
            System.out.println("  lineNo == " + sourcePgm.lineNo());
        }
        if (debug) {
            System.out.println("  charPos == " + sourcePgm.charPos());
        }
        if (debug) {
            System.out.println("  markPos == " + markpos);
        }
    }

    void reset() {
        // in.reset(); 
        //System.out.println("reset: debug=" + debug);
        sourcePgm.setLineNo(marklineNo);
        sourcePgm.setCharPos(markcharPos);
        sourcePgm.set(marksourcePgm);
        ch = markch;
        bufferPos = markpos;
        tokenType = marktokenType;
        val = markval;
        if (debug) {
            System.out.println("Reseting input stream:");
        }
        if (debug) {
            System.out.println("  ch == '" + ch + "'");
        }
        if (debug) {
            System.out.println("  lineNo == " + sourcePgm.lineNo());
        }
        if (debug) {
            System.out.println("  charPos == " + sourcePgm.charPos());
        }
    }

    public Token(int n) {  // no get allowed!
        tokenType = n;
    }

    public boolean more() {
        return !eof;
    }

    public String fileName() {
        return sourcePgm.fileName();
    }

    public int lineNo() {
        return sourcePgm.lineNo();
    }

    public int charPos() {
        return sourcePgm.charPos();
    }

    // gnc --  get next character into global static ch
    void gnc() {
        // int c = in.read();
        // debug=false;
        int c;
        if (bufferPos >= sourcePgm.length()) {
            if (debug) {
                System.out.println("End of source pgm encountered at"
                        + bufferPos + sourcePgm.length());
            }
            c = -1;
        } else {
            c = sourcePgm.getChar();
            bufferPos++;
        }
        if (debug) {
            System.out.println("Token.gnc:: Entering: ch = '" + ch + "'" + " bufferPos=" + bufferPos);
        }
        if (debug) {
            System.out.println("Token.gnc:: " + sourcePgm.line());
        }
        if (c == -1) {
            ch = (char) 26; // ^Z  ljm
            eof = true;
        } else { //ljm
            ch = (char) c;
            // if (ch == '\\') {
            // c = in.read();
            // }
            if (ch == '\n') {
                // lineNo++;
                // charPos = 1;
                newline = true;
            } else {
            }
            // charPos++;
        }
        if (debug) {
            System.out.println("Token.gnc:: ch = '" + ch + "'"
                    + " newline=" + newline
                    + " eof=" + eof);
        }
        if (debug) {
            System.out.println("Token.gnc: sourcePgm='" + sourcePgm + "'");
        }
        if (debug) {
            System.out.println("Token.gnc: sourcePgm.pos='" + sourcePgm.charPos() + "'");
        }
    }

    public void get() {
        // while (more() && (ch == ' ' || ch == '\n' || ch == '\t' ) 6/19/04 --ljm
        // System.out.println("Entering get with lineno = " + lineNo());
        commaBreak = false;
        semiBreak = false;
        lineBreak = false;
        if (debug) {
            System.out.println("Entering get");
        }
        while (more() && (ch == ' ' || ch == '\n' || ch == '\t' || ch == ';' || ch == ',')) {
            if (ch == '\n') {
                lineBreak = true;
            } else if (ch == ';') {
                semiBreak = true;
            } else if (ch == ',') {
                commaBreak = true;
            }
            gnc();
        }

        //tokenLineNo = lineNo; // ljm -- 6/19/04
        //tokenCharPos = charPos-1; // ljm -- 9/7/04

        val = "";

        if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')
                || (ch == '@')) {
            do {
                val += ch;
                gnc();
            } while (more() && ((ch >= 'a' && ch <= 'z')
                    || (ch >= 'A' && ch <= 'Z')
                    || (ch >= '0' && ch <= '9')
                    || (ch == '_')));
            if (ch == '?') {
                val += ch;
                gnc();
            }
            String v = val;
            val = val.toLowerCase();
            int prevTokenType = tokenType;
            tokenType = Parser.idSym;
            if (val.equals("while")) {
                tokenType = Parser.whileSym;
            } else if (val.equals("let")) {
                tokenType = Parser.letSym;
            } else if (val.equals("alias")) {
                tokenType = Parser.aliasSym;
            } else if (val.equals("generate")) {
                tokenType = Parser.generateSym;
            } else if (val.equals("task")) {
                tokenType = Parser.taskSym;
            } else if (val.equals("function")) {
                tokenType = Parser.functionSym;
            } else if (val.equals("procedure")) {
                tokenType = Parser.procedureSym;
            } else if (val.equals("print")) {
                tokenType = Parser.printSym;
            } else if (val.equals("name")) {
                tokenType = Parser.nameSym;
            } else if (val.equals("emit")) {
                tokenType = Parser.emitSym;
            } else if (val.equals("select")) {
                tokenType = Parser.selectSym;
            } else if (val.equals("to")) {
                tokenType = Parser.toSym;
            } else if (val.equals("through")) {
                tokenType = Parser.toSym;
            } else if (val.equals("thru")) {
                tokenType = Parser.toSym;
            } else if (val.equals("downto")) {
                tokenType = Parser.downtoSym;
            } else if (val.equals("file")) {
                tokenType = Parser.fileSym;
            } else if (val.equals("from")) {
                tokenType = Parser.fromSym;
            } else if (val.equals("by")) {
                tokenType = Parser.bySym;
            } else if (val.equals("call")) {
                tokenType = Parser.callSym;
            } else if (val.equals("return")) {
                tokenType = Parser.returnSym;
            } else if (val.equals("of")) {
                if (prevTokenType == Parser.idSym) {
                    get(); // otherwise leave it
                }
            } else if (val.equals("while")) {
                tokenType = Parser.whileSym;
            } else if (val.equals("@f")) {
                tokenType = Parser.atbeginSym;
            } else if (val.equals("@first")) {
                tokenType = Parser.atbeginSym;
            } else if (val.equals("@l")) {
                tokenType = Parser.atendSym;
            } else if (val.equals("@last")) {
                tokenType = Parser.atendSym;
            } else if (val.equals("@i")) {
                tokenType = Parser.ateachSym;
            } else if (val.equals("@iter")) {
                tokenType = Parser.ateachSym;
            } else if (val.equals("@each")) {
                tokenType = Parser.ateachSym;
            } else if (val.equals("@beginning")) {
                tokenType = Parser.atbeginSym;
            } else if (val.equals("@begin")) {
                tokenType = Parser.atbeginSym;
            } else if (val.equals("@ending")) {
                tokenType = Parser.atendSym;
            } else if (val.equals("@end")) {
                tokenType = Parser.atendSym;
            } else if (val.equals("@each")) {
                tokenType = Parser.ateachSym;
            } else if (val.equals("and")) {
                tokenType = Parser.andSym;
            } else if (val.equals("or")) {
                tokenType = Parser.orSym;
            } else if (val.equals("not")) {
                tokenType = Parser.notSym;
            } else if (val.equals("true")) {
                tokenType = Parser.trueSym;
            } else if (val.equals("false")) {
                tokenType = Parser.falseSym;
            } else if (val.equals("otherwise")) {
                tokenType = Parser.otherwiseSym;
            } else if (val.equals("insert")) {
                tokenType = Parser.insertSym;
            } else if (val.equals("delete")) {
                tokenType = Parser.deleteSym;
            } else if (val.equals("after")) {
                tokenType = Parser.afterSym;
            } else if (val.equals("before")) {
                tokenType = Parser.beforeSym;
            } else if (val.equals("in")) {
                tokenType = Parser.inSym;
            } else if (val.equals("append")) {
                tokenType = Parser.appendSym;
            } else if (val.equals("onto")) {
                tokenType = Parser.toSym;
            } else if (val.equals("stop")) {
                tokenType = Parser.stopSym;
            } else if (val.equals("echo")) {
                tokenType = Parser.echoSym;
            } else if (val.equals("unalias")) {
                tokenType = Parser.unaliasSym;
            } else if (val.equals("generator")) {
                tokenType = Parser.generatorSym;
            } else if (val.equals("when")) {
                tokenType = Parser.whenSym;
            } else if (val.equals("until")) {
                tokenType = Parser.untilSym;
            } else if (val.equals("iter")) {
                tokenType = Parser.iteratorSym;
            } else if (val.equals("prepend")) {
                tokenType = Parser.prependSym;
            }
            /*
             else if(ch == ':'){                              //added for records by chad
             tokenType = Parser.labelSym;              //needs to set tokenType = labelSym
             gnc();
             }
             */
            // val  = v;
        } else if (ch >= '0' && ch <= '9') {
            int pCounter = 0;
            do {

                if (ch == '.') {
                    pCounter++;
                }
                if (pCounter > 1) {
                    System.out.println("\n\n\n**************    ERROR    **************");
                    System.err.println("Error at line " + sourcePgm.lineNo() + ",character "
                            + (sourcePgm.charPos() - 1)
                            + "\n\nInvalid Number Format : Multiple decimal points");
                    System.out.println("*******************************************");
                    System.out.println("\nAlgorithm terminated!");
                    System.exit(1);
                }
                val += ch;

                gnc();
            } while (!eof && ((ch >= '0' && ch <= '9') || ch == '.'));
            tokenType = Parser.numberSym;
        } else {
            val += ch;
            gnc();
            if (val.equals("(")) {
                tokenType = Parser.lparenSym;
            } else if (val.equals(")")) {
                tokenType = Parser.rparenSym;
            } else if (val.equals("*")) {
                tokenType = Parser.starSym;
            } else if (val.equals("%")) {
                tokenType = Parser.percentSym;  // chad added for %
            } else if (val.equals("+")) {
                tokenType = Parser.plusSym;
            } else if (val.equals("{")) {
                tokenType = Parser.lbraceSym;
                if (ch == '+') {
                    mark();
                    gnc();
                    if (ch == '}') {
                        tokenType = Parser.pipeSym;
                        gnc();
                    } else {// Found a {+, better be a number coming up, so we'll call this a lbracket
                        reset(); // reset to the mark ... which is holding at the +
                    }
                }
            } else if (val.equals("}")) {
                tokenType = Parser.rbraceSym;
            } else if (val.equals(";")) {
                tokenType = Parser.semiSym;
            } else if (val.equals(":")) {
                tokenType = Parser.colonSym;
            } else if (val.equals("|")) {
                tokenType = Parser.pipeSym;
            } else if (val.equals(",")) {
                tokenType = Parser.commaSym;
            } else if (val.equals("[")) {
                tokenType = Parser.lbracketSym;
                if (ch == '+') {
                    mark();
                    gnc();
                    if (ch == ']') {
                        tokenType = Parser.pipeSym;
                        gnc();
                    } else {// Found a [+, better be a number coming up, so we'll call this a lbracket
                        reset(); // reset to the mark ... which is holding at the +
                    }
                }
            } else if (val.equals("]")) {
                tokenType = Parser.rbracketSym;
            } else if (val.equals("=")) {
                tokenType = Parser.eqSym;
            } else if (val.equals("&")) {
                tokenType = Parser.andSym;
            } else if (val.equals("/")) {
                if (ch == '/') {
                    while (ch != '\n') {
                        gnc();
                    }
                    tempCounter = 1;
                    //charPos=1;
                    get();  // Note the recursion
                } /*
                 There is a beautiful fault here, took 3 years to find, double read on internal *  
                 else if ( ch=='*') {  // sm: handle multi-line /-* comments *-/
                 boolean mlComment=true;
                 while (more() && mlComment) {
                 gnc();
                 if(ch=='*') {  
                 gnc();
                 if(ch=='/')
                 mlComment=false;
                 gnc();
                 }
                 }
                 if (mlComment)
                 System.err.println("'/*' comment not closed with '*-/'");
                 else {
                 get();  // recursive call ... go get the token after the comment
                 }
                 }
                 */ else if (ch == '*') {  // sm: handle multi-line /* comments */
                    boolean mlComment = true;
                    gnc();
                    while (more() && mlComment) {
                        if (ch == '*') {
                            gnc();
                            if (ch == '/') {
                                mlComment = false;   // Muntha error
                                gnc();
                            }
                        } else {
                            gnc();
                        }
                    }
                    if (mlComment) {
                        System.err.println("'/*' comment not closed with '*/'");
                    } else {
                        get();  // recursive call ... go get the token after the comment
                    }
                } else {
                    tokenType = Parser.slashSym;
                }
            } else if (val.equals("#")) { // single line comment
                while (ch != '\n') {
                    gnc();
                }
                tempCounter = 1;
                //charPos=1;
                get();  // Note the recursion
            } else if (val.equals("<")) {
                tokenType = Parser.ltSym;
                if (ch == '=') {
                    val += ch;
                    tokenType = Parser.leSym;
                    gnc();
                }
            } else if (val.equals("-")) {
                tokenType = Parser.minusSym;
                if (ch == '>') {
                    val += ch;
                    tokenType = Parser.arrowSym;
                    gnc();
                } else if (ch == '-') {//single line comment
                    while (ch != '\n') {
                        gnc();
                    }
                    tempCounter = 1;
                    //charPos = 1;
                    get();
                }
            } else if (val.equals(">")) {
                tokenType = Parser.gtSym;
                if (ch == '=') {
                    val += ch;
                    tokenType = Parser.geSym;
                    gnc();
                }
            } else if (val.equals("!")) {
                tokenType = Parser.notSym;
                if (ch == '=') {
                    val += ch;
                    tokenType = Parser.neSym;
                    gnc();
                }
            } else if (val.equals("\"")) {
                tokenType = Parser.stringSym;
                val = "";
                int lineno = sourcePgm.lineNo();
                while (more() && ch != '"') {
                    if (ch == '\\') {
                        gnc();
                        if (ch == 'n') {
                            ch = '\n';
                        } else if (ch == 't') {
                            ch = '\t';
                        } else if (ch == 'r') {
                            ch = '\r';
                        }
                    }
                    val += ch;
                    gnc();
                }
                if (ch != '"') {
                    System.err.println("Double quotes are not balanced.");
                    System.err.println("There was no matching double quote "
                            + "for one found on or before line "
                            + lineno + '.');
                    System.err.println("The mistake could well be much earlier in the algorithm.");
                    System.err.println("Check all pairs of quotes to find the mistake.");

                    System.exit(1);
                }
                gnc();
                /*
                 if(ch == ':'){                              //added for records by chad
                 tokenType = Parser.labelSym;              //needs to set tokenType = labelSym
                 gnc();
                 }
                 */
            } else if (val.equals("\'")) {
                tokenType = Parser.stringSym;
                val = "";
                int lineno = sourcePgm.lineNo();
                while (more() && ch != '\'') {
                    if (ch == '\\') {
                        gnc();
                    }
                    val += ch;
                    gnc();
                }
                if (ch != '\'') {
                    System.err.println("Double quotes are not balanced.");
                    System.err.println("There was no matching double quote "
                            + "for one found on or before line "
                            + lineno + '.');
                    System.err.println("The mistake could well be much earlier in the algorithm.");
                    System.err.println("Check all pairs of quotes to find the mistake.");

                    System.exit(1);
                }
                gnc();
            } else {
                tokenType = Parser.errSym;
            }

            if (eof) {
                tokenType = Parser.eofSym;
                ch = ' ';
                val = "";
            }
        }
        // debug=false;
        if (debug) {
            System.out.println("Token.get::val = '" + val
                    + "' "
                    + "type = "
                    + tokenType
                    + " ch='"
                    + ch
                    + "' at ("
                    + fileName()
                    + "/"
                    + sourcePgm.fileName()
                    + "/"
                    + sourcePgm.lineNo()
                    + "/"
                    + sourcePgm.charPos()
                    + ")");
        }
        // debug=false;
    } // end get

    @Override
    public String toString() {
        String s;
        if (debug) {
            System.out.println("Looking up " + tokenType);
        }
        if (0 <= tokenType && tokenType < Parser.tokenStrCnt) {
            s =
                    " ("
                    + "type=" + Parser.tokenStr[tokenType] + ":"
                    + tokenType
                    + "@"
                    + sourcePgm.fileName()
                    + ":"
                    + sourcePgm.lineNo()
                    + "/"
                    + sourcePgm.charPos()
                    + "/"
                    + val
                    + ")";
        } else {
            s = "Uninitialized";
        }
        return s;
    }

    public void appendInput(String fileName) {
        GenesisIO io = new GenesisIO();
        //System.out.println ("Setting input source to '" +  fileName +"'");
        io.setInputFile(fileName);

        String line = io.readLine();

        while (line != null) {
            boolean incStmt = false;
            int charIndex = parseSpace(line, 0);
            if (charIndex < (line.length() - 1) && line.charAt(charIndex) == '#') {
                charIndex = parseSpace(line, charIndex + 1);
                if (charIndex < (line.length() - 1) && line.charAt(charIndex) == '+') {
                    charIndex = parseSpace(line, charIndex + 1);
                    char charVal = line.charAt(charIndex);
                    if (charVal >= 'a' && charVal <= 'z' || charVal >= 'A' && charVal <= 'Z' || charVal >= '0' && charVal <= '9') {
                        incStmt = true;
                        int beg = charIndex;
                        line = line + "\n";
                        while (charIndex < (line.length() - 1) && line.charAt(charIndex) != '\n') {
                            charIndex++;
                        }
                        PositionInfo positionInfo =
                                new PositionInfo(fileName(), lineNo() + 1, charPos());
                        positionInfoStack.push(positionInfo);
                        //System.out.println ("Pushing " + (lineNo() + 1) 
                        //+ " onto stack for file" + fileName());
                        sourcePgm.setLineNo(1);
                        sourcePgm.setCharPos(0);
                        fileName = line.substring(beg, charIndex);
                        sourcePgm.setFileName(fileName);
                        appendInput(fileName);
                        positionInfo = positionInfoStack.pop();
                        fileName = positionInfo.fileName();
                        //System.out.println ("Popping " + (lineNo() ) 
                        //+ " onto stack for file" + fileName());
                        sourcePgm.setCharPos(positionInfo.charPos());
                        sourcePgm.setFileName(fileName);
                        sourcePgm.setLineNo(positionInfo.lineNo());
                    }
                }
            }
            if (!incStmt) {
                sourcePgm.append(line + "\n");
                if (debug) {
                    System.out.println("sourcePgm:'" + sourcePgm + "'");
                }
            }
            line = io.readLine();
        }
    }
}
