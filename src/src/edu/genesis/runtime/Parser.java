/*
 Genesis Java Interpreter
 Translates Genesis to int

 Authors:  Larry Morell and Wes Potts; some minor portions remain from 
 a compiler version written by Andy Bostian for translating to an
 earlier internal form.  Some record definition code supplied by
 Chad Harlan

 Modification History

 The modification history below is not completely accurate, but close.
 Entries here are for new constructs in the language that primarily affected the parser
 See the Modification history for other files for details that relate to those files

 1/27/03 -- began work in java for Genesis (AB)
 2/04    -- Converted grammar to conform to the Yacc grammar (LM)
 3/04    -- Modified structure to include constants for tokens here
 rather than in Tokens.java; this allows references without
 qualification (LM)
 6/14/03 -- Wes Potts: included all "traverse(tn)" debug with the "if (debug)"
 6/15/04 -- Wes Potts: made a change to parsePrimary
 AST wrong for strings
 7/10/04 -- ljm: Added default task to allow for a single statement
 7/15/04 -- ljm: Two cascading if stmts did not have a closing else 
 to print an error
 7/16/04 -- ljm: Removed creation of "Block" in parseFunctionDef  
 7/29/04 -- ljm: disallowed composite tokens to cross newlines, commas, semi's
 7/31/04 -- ljm: Added unary minus; conformed syntax tree of procedure calls
 to that of function calls
 8/2/04  -- ljm: added alias processing for formal parameters
 8/2/04  -- ljm: put in kluge code to handle proc calls subsumed by 'print exp exp ...' proc (call)
 10/16/04 -- ljm: added standalone Generate statement
 10/25/04 -- ljm: added code to parse paren-less unary func/proc calls
 03/24/05 -- ljm: changed parseExpression to parse truth vals and nots
 04/16/05 -- ljm: allowed for "let iterator(n) name exp" to do iter to iter copy
 5/9/05   -- ljm: fixed error processing for missing return in a function
 5/9/05   -- ljm: modified "Generate each(n) from L" without a task to parse as if it had an empty task {}{}{}
 9/21/05  -- ljm: made , significant (but optional) for printlists
 -- fixed true/false so they can be compared as literals
 11/3/05  -- ljm: added a null stmt (only occurs before a rbrace
 5/25/2006  -- ch/ljm: added ability define literal field names for lists/ability to access
 these fields by using subscripts rather than dot notation.  The intent is to
 add operations later that will provide the abilty to redefine said field names
 via either new syntax or new functions/procedures
 7/28/06-- ljm: Made otherwise terminate a SelectList; true does not.
 1/1/07  -- ljm: reworked parsing for procedure and function calls 
 2/1/07 -- ljm: added 'until' clause for iterations and 'when' clause for guarded tasks
 2/1/07 -- ljm: added tokenBreak to provide control in printExpresionList's
 2/1/07 -- ljm: corrected the persistent issue of "looking for eofSym"
 3/26/07 -- ljm: added 'iter' as a parameter passing mechanism
 1/20/08 -- ljm: removed references to GenesisIO; modified processing of source program;
 improved error identification by ensuring all nodes in the tree have contexts
 7/4/08 -- ljm: A function call followed by an 'and' or an 'or' now parses correctly
 2/20/10 -- ljm: modified countReturns to see if there is a top-level 'return' in a function
 4/10/10 -- ljm: fixed mis-identification of "to" as "downto"
 */
package edu.genesis.runtime;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Parser {

    public static boolean debug = false;
    public static String tokenStr[] = new String[100];
    public static int tokenStrCnt = 0;
    public static final int aliasSym = tokenStrCnt++;
    public static final int andSym = tokenStrCnt++;
    public static final int arrowSym = tokenStrCnt++;
    public static final int atbeginSym = tokenStrCnt++;
    public static final int ateachSym = tokenStrCnt++;
    public static final int atendSym = tokenStrCnt++;
    public static final int bySym = tokenStrCnt++;
    public static final int callSym = tokenStrCnt++;
    public static final int colonSym = tokenStrCnt++;
    public static final int commaSym = tokenStrCnt++;
    public static final int dotSym = tokenStrCnt++;    //chad added for record label
    public static final int emitSym = tokenStrCnt++;
    public static final int eofSym = tokenStrCnt++;
    public static final int eqSym = tokenStrCnt++;
    public static final int errSym = tokenStrCnt++;
    public static final int fileSym = tokenStrCnt++;
    public static final int fromSym = tokenStrCnt++;
    public static final int functionSym = tokenStrCnt++;
    public static final int geSym = tokenStrCnt++;
    public static final int generateSym = tokenStrCnt++;
    public static final int generatorSym = tokenStrCnt++;
    public static final int gtSym = tokenStrCnt++;
    public static final int idSym = tokenStrCnt++;
    public static final int labelSym = tokenStrCnt++;  //chad added for record label
    public static final int lbraceSym = tokenStrCnt++;
    public static final int lbracketSym = tokenStrCnt++;
    public static final int leSym = tokenStrCnt++;
    public static final int letSym = tokenStrCnt++;
    public static final int lparenSym = tokenStrCnt++;
    public static final int ltSym = tokenStrCnt++;
    public static final int minusSym = tokenStrCnt++;
    public static final int nameSym = tokenStrCnt++;
    public static final int neSym = tokenStrCnt++;
    public static final int notSym = tokenStrCnt++;
    public static final int numberSym = tokenStrCnt++;
    public static final int orSym = tokenStrCnt++;
    public static final int percentSym = tokenStrCnt++;  //chad added for %
    public static final int pipeSym = tokenStrCnt++;
    public static final int plusSym = tokenStrCnt++;
    public static final int printSym = tokenStrCnt++;
    public static final int procedureSym = tokenStrCnt++;
    public static final int rbraceSym = tokenStrCnt++;
    public static final int rbracketSym = tokenStrCnt++;
    public static final int returnSym = tokenStrCnt++;
    public static final int rparenSym = tokenStrCnt++;
    public static final int selectSym = tokenStrCnt++;
    public static final int semiSym = tokenStrCnt++;
    public static final int singlequoteSym = tokenStrCnt++;
    public static final int slashSym = tokenStrCnt++;
    public static final int starSym = tokenStrCnt++;
    public static final int stringSym = tokenStrCnt++;
    public static final int toSym = tokenStrCnt++;
    public static final int throughSym = tokenStrCnt++;
    public static final int whileSym = tokenStrCnt++;
    public static final int trueSym = tokenStrCnt++;
    public static final int falseSym = tokenStrCnt++;
    public static final int otherwiseSym = tokenStrCnt++;
    public static final int insertSym = tokenStrCnt++;
    public static final int deleteSym = tokenStrCnt++;
    public static final int afterSym = tokenStrCnt++;
    public static final int beforeSym = tokenStrCnt++;
    public static final int inSym = tokenStrCnt++;
    public static final int appendSym = tokenStrCnt++;
    public static final int prependSym = tokenStrCnt++;
    public static final int ontoSym = tokenStrCnt++;
    public static final int stopSym = tokenStrCnt++;
    public static final int echoSym = tokenStrCnt++;
    public static final int unaliasSym = tokenStrCnt++;
    public static final int taskSym = tokenStrCnt++;
    public static final int untilSym = tokenStrCnt++;
    public static final int whenSym = tokenStrCnt++;
    public static final int iteratorSym = tokenStrCnt++;
    public static final int downtoSym = tokenStrCnt++;
    private static Hashtable<String, String> symtab = new Hashtable<>();
    public static Token t;
    static int indent = 0;
    private static int prettyIndent = 0;
    // 6/09/04 - Wes Potts
    // added a String member to hold the genesis source filename
    private static String sourcefile;
    
    PrintStream o;

    public void init() {
        /* Set up built-in functions */
        symtab.put("integer", "function");
        symtab.put("integer()", "function");
        symtab.put("fractional", "function");
        symtab.put("fractional()", "function");
        symtab.put("remainder", "function");
        symtab.put("remainder()", "function");
        symtab.put("remainder()divided", "function");
        symtab.put("remainder()divided_by", "function");
        symtab.put("remainder()divided_by()", "function");
        symtab.put("quotient", "function");
        symtab.put("quotient()", "function");
        symtab.put("quotient()divided", "function");
        symtab.put("quotient()divided_by", "function");
        symtab.put("quotient()divided_by()", "function");
        symtab.put("move", "procedure");
        symtab.put("move()", "procedure");
        symtab.put("reset", "procedure");
        symtab.put("reset()", "procedure");
        symtab.put("move", "procedure");
        symtab.put("move()", "procedure");
        symtab.put("move()backward", "procedure");
        symtab.put("move()forward", "procedure");
        symtab.put("move()to", "procedure");
        symtab.put("move()to()", "procedure");
        symtab.put("position", "function");
        symtab.put("position()", "function");
        symtab.put("on", "function");
        symtab.put("on()", "function");
        symtab.put("off", "function");
        symtab.put("off()", "function");
        symtab.put("head", "function");
        symtab.put("head()", "function");
        symtab.put("tail", "function");
        symtab.put("tail()", "function");
        symtab.put("split", "procedure");
        symtab.put("split()", "procedure");
        symtab.put("split()at", "procedure");
        symtab.put("split()at()", "procedure");
        symtab.put("split()at()into", "procedure");
        symtab.put("split()at()into()", "procedure");
        symtab.put("square", "function");
        symtab.put("square_root", "function");
        symtab.put("square_root()", "function");
        symtab.put("explode", "function");
        symtab.put("explode()", "function");
        symtab.put("iterator", "function");
        symtab.put("iterator()", "function");
        symtab.put("is", "function");
        symtab.put("is()", "function");
        symtab.put("is()a", "function");
        symtab.put("is()a_number?", "function");
        symtab.put("is()a_string?", "function");
        symtab.put("is()a_truth", "function");
        symtab.put("is()a_truth_value?", "function");
        symtab.put("is()a_list?", "function");
        symtab.put("is()defined?", "function");
        symtab.put("is()empty?", "function");
        symtab.put("is()an", "function");
        symtab.put("is()an_iterator?", "function");
        symtab.put("type", "function");
        symtab.put("type()", "function");
        symtab.put("set", "procedure");
        symtab.put("set_precision", "procedure");
        symtab.put("set_precision()", "procedure");
        symtab.put("read", "procedure");
        symtab.put("read_char", "procedure");
        symtab.put("read_char()", "procedure");
        symtab.put("read_char()from", "procedure");
        symtab.put("read_char()from()", "procedure");
        symtab.put("sublist", "function");
        symtab.put("sublist()", "function");
        symtab.put("random", "function");
        symtab.put("random()", "function");
        symtab.put("length", "function");
        symtab.put("length()", "function");
        symtab.put("succ", "function");
        symtab.put("succ()", "function");
        symtab.put("next", "function");
        symtab.put("next()", "function");
        symtab.put("prev", "function");
        symtab.put("prev()", "function");
        symtab.put("value", "function");
        symtab.put("value()", "function");
        symtab.put("atfirst", "function");
        symtab.put("atfirst()", "function");
        symtab.put("atlast", "function");
        symtab.put("atlast()", "function");
        symtab.put("set_trace", "procedure");
        symtab.put("set_trace_on", "procedure");
        symtab.put("set_trace_on()", "procedure");
        symtab.put("set_trace_off", "procedure");
        symtab.put("set_trace_off()", "procedure");

        tokenStr[aliasSym] = "keyword 'alias'";
        tokenStr[andSym] = "keyword 'and'";
        tokenStr[arrowSym] = "symbol '->'";
        tokenStr[atbeginSym] = "symbol '@first'";
        tokenStr[ateachSym] = "symbol '@iter'";
        tokenStr[atendSym] = "symbol '@last'";
        tokenStr[bySym] = "keyword 'by'";
        tokenStr[callSym] = "keyword 'call'";
        tokenStr[colonSym] = "symbol ':'";
        tokenStr[commaSym] = "symbol ','";
        tokenStr[dotSym] = "symbol '.'";    //chad added for record label
        tokenStr[emitSym] = "keyword 'emit'";
        tokenStr[eofSym] = "keyword 'eof'";
        tokenStr[eqSym] = "keyword 'eq'";
        tokenStr[errSym] = "erroneous input";
        tokenStr[fileSym] = "keyword 'file'";
        tokenStr[fromSym] = "keyword 'from'";
        tokenStr[functionSym] = "keyword 'function'";
        tokenStr[geSym] = "symbol '>='";
        tokenStr[generateSym] = "keyword 'generate'";
        tokenStr[generatorSym] = "keyword 'generator'";
        tokenStr[gtSym] = "symbol '>''";
        tokenStr[idSym] = "name";
        tokenStr[labelSym] = "label";                 //chad added for record label      
        tokenStr[lbraceSym] = "symbol '{'";
        tokenStr[lbracketSym] = "symbol '['";
        tokenStr[leSym] = "symbol '<='";
        tokenStr[letSym] = "keyword 'let'";
        tokenStr[lparenSym] = "symbol '('";
        tokenStr[ltSym] = "symbol '<'";
        tokenStr[minusSym] = "symbol '-'";
        tokenStr[nameSym] = "keyword 'name'";
        tokenStr[neSym] = "symbol '!='";
        tokenStr[notSym] = "keyword 'not'";
        tokenStr[numberSym] = "number";
        tokenStr[percentSym] = "symbol '%'";  // chad added for %
        tokenStr[pipeSym] = "symbol '[+]'";
        tokenStr[plusSym] = "symbol '+'";
        tokenStr[printSym] = "keyword 'print'";
        tokenStr[procedureSym] = "keyword 'procedure'";
        tokenStr[rbraceSym] = "symbol '}'";
        tokenStr[rbracketSym] = "symbol ']'";
        tokenStr[returnSym] = "keyword 'return'";
        tokenStr[rparenSym] = "symbol ')'";
        tokenStr[selectSym] = "keyword 'select'";
        tokenStr[semiSym] = "symbol ';'";
        tokenStr[singlequoteSym] = "symbol \"'\"";
        tokenStr[slashSym] = "symbol '/'";
        tokenStr[starSym] = "symbol '*'";
        tokenStr[stringSym] = "string'";
        tokenStr[toSym] = "keyword 'to'";
        tokenStr[downtoSym] = "keyword 'downto'";
        tokenStr[throughSym] = "keyword 'through'";
        tokenStr[whileSym] = "keyword 'while'";
        tokenStr[trueSym] = "keyword 'true'";
        tokenStr[falseSym] = "keyword 'false'";
        tokenStr[otherwiseSym] = "keyword 'otherwise'";
        tokenStr[insertSym] = "keyword 'insert'";
        tokenStr[deleteSym] = "keyword 'delete'";
        tokenStr[afterSym] = "keyword 'after'";
        tokenStr[beforeSym] = "keyword 'before'";
        tokenStr[inSym] = "keyword 'in'";
        tokenStr[appendSym] = "keyword 'append'";
        tokenStr[prependSym] = "keyword 'prepend'";
        tokenStr[ontoSym] = "keyword 'onto'";
        tokenStr[stopSym] = "keyword 'stop'";
        tokenStr[echoSym] = "keyword 'echo'";
        tokenStr[unaliasSym] = "keyword 'unalias'";
        tokenStr[taskSym] = "keyword 'task'";
        tokenStr[untilSym] = "keyword 'until'";
        tokenStr[whenSym] = "keyword 'when'";
        tokenStr[iteratorSym] = "keyword 'iterator'";
        
        
                        try {
            o = new PrintStream(new FileOutputStream("A.txt",true));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GenesisVal.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.setOut(o);
        System.setErr(o);
    }

// 6/9/04
// Wes Potts
    Parser(String filename) {
        init();
        OpVal.init();
        t = new Token(filename);
    }

    public void setSource(String fileName) {
        t.setSource(fileName);
        t.appendInput(fileName);
        t.processInput();
    }

// ---------------
// sinfo -- returns the string version of the node
    static String sinfo(TreeNode n) {
        String ans = "";
        if (n.info.getVal() instanceof StringVal) {
            ans = ans + '"' + n.info.toString() + '"'
                    + "(" + n.fileName() + ":" + n.lineNo() + ":" + n.charPos() + ")";
        } else {

            ans = ans + n.info.toString()
                    + "(" + n.fileName() + ":" + n.lineNo() + ":" + n.charPos() + ")";
        }
        return ans;
    }

    static String rootinfo(TreeNode n) {
        String ans = "";
        if (n.info.getVal() instanceof OpVal) {
            OpVal o = (OpVal) (n.info.getVal());

            // edited by Wes 6/14/04
            if (debug) {
                ans = ans + OpVal.opStr[ o.getVal()]
                        + "(" + n.fileName() + ":" + n.lineNo() + ":" + n.charPos() + ")";
            } else {
                ans = ans + OpVal.opStr[ o.getVal()];
            }
        } else {
            // edited by Wes 6/14/04
            if (debug) {
                ans = ans + (n.info.toString())
                        + "(" + n.fileName() + "::" + n.lineNo() + ":" + n.charPos() + ")";
            } else {
                ans = ans + (n.info.toString());
            }
        }
        return ans;
    }

    static void traverse2(TreeNode tn) {
        if (tn != null) {
            for (int i = 0; i < indent; i++) {
                System.err.print(' ');
            }
            System.out.println(rootinfo(tn));
            indent++;
            traverse2((TreeNode) tn.left);
            traverse2((TreeNode) tn.right);
            indent--;
        }
    }

// print interior nodes one way, leaves another way
    static void traverse(TreeNode tn) {
        if (tn != null) {
            for (int i = 0; i < indent; i++) {
                System.out.println(" \n");
            }
            if (tn.left == null) { // leaf node
                if (tn.info.getVal() instanceof OpVal) { // bizarre error omitting this brace and its matching
                    OpVal ov = (OpVal) tn.info.getVal();
                    if (ov.eq(OpVal.stmtListOp)) {
                        System.out.println(rootinfo(tn) + "\n");
                    } else {
                        System.out.println(sinfo(tn) + "\n");
                    }
                } else {
                    System.out.println(sinfo(tn) + "\n");
                }
            } else {
                System.out.println(rootinfo(tn) + "\n");
            }
            indent = indent + 3;
            TreeNode tree = (TreeNode) tn.left;
            while (tree != null) {
                traverse(tree);
                tree = (TreeNode) tree.right;
            }
            indent = indent - 3;
        }
    }
// prettyPrint -- come as close as possible to printing the original Genesis code

    private static void printIndent() {
        int i;
        StringBuilder sb = new StringBuilder(prettyIndent);
        for (i = 0; i < prettyIndent; i++) {
            sb.append(' ');
        }
        System.out.print(sb);
    }

    static void prettyPrint(TreeNode tn) {
        traverse(tn);
    }

// check -- true iff the current token (tree) is contained in the TokenTypeSet s
    boolean check(int s[]) {
        int i;
        for (i = 0; i < s.length; i++) {
            if (t.tokenType == s[i]) {
                return true;
            }
        }
        return false;
    }

// check -- true iff the current token (tree) is tokenType s
    boolean check(int s) {
        return t.tokenType == s;
    }

// consume -- replace the current token (tree) with the next one
    void consume() {
        t.get();
    }

    void error(int s[]) {
        System.out.println("\n");
        System.out.println("\n");
        System.out.println("\n");
        System.out.println("*************     ERROR     *************** \n");
        System.out.println("Error in file " + t.fileName()
                + " at line " + t.lineNo() + ", character " + t.charPos() + "\n");
        System.out.println("Looking for any of the following: \n");
        int i;
        for (i = 0; i < s.length; i++) {
            System.out.println("   " + tokenStr[s[i]] + "\n");
        }
        System.out.println("\nFound: " + tokenStr[t.tokenType] + "\n");
        System.out.println("*******************************************\n");
        System.out.println("Algorithm terminated!\n");
        System.out.println("\n");
        System.out.println(Quote.getMessage() + "\n");
        System.exit(1);
    }

    void printError(String msg) {
        System.out.println("\n");
        System.out.println("\n");
        System.out.println("\n");
        System.out.println("*************     ERROR     ***************\n");
        System.out.println("Error in file " + t.fileName()
                + " at line " + t.lineNo() + ", character " + t.charPos() + "\n");
        System.out.println(msg + "\n");
        System.out.println("*******************************************\n");
        System.out.println("Algorithm terminated! \n");
        System.out.println("\n");
        System.out.println(Quote.getMessage() + "\n");
        System.exit(1);
    }
// verify -- if current token (tree) is in in the set TokenTypeSet s, 
//           then get the next token
//           else error message and halt the program

    void verify(int s[]) {
        Token c, d;
        if (check(s)) {
            t.get();
        } else {
            error(s);
        }
    }

// verify -- if current token (tree) is in in the set TokenTypeSet s, 
//           then get the next token
//           else error message and halt the program
    void verify(int s) {
        Token c, d;
        if (check(s)) {
            t.get();
        } else {
            System.out.println("\n");
            System.out.println("\n");
            System.out.println("\n");
            System.out.println("************     ERROR     ************** \n");
            System.out.println("Error in file " + t.fileName()
                    + " at line " + t.lineNo() + ", character " + t.charPos() + "\n");
            System.out.println("Looking for the following: \n");
            int i;
            System.out.println("   " + tokenStr[s] + "\n"); //+ ":" + s);
            System.out.println("\nFound: " + tokenStr[t.tokenType] + "\n");

            System.out.println("***************************************** \n");
            System.out.println("Algorithm terminated! \n");
            System.out.println("\n");
            System.exit(1);
        }

    }
    final int[] firstOfStmt = new int[]{
        idSym,
        numberSym,
        appendSym,
        prependSym,
        callSym,
        deleteSym,
        echoSym,
        generateSym,
        insertSym,
        lbraceSym,
        letSym,
        printSym,
        returnSym,
        selectSym,
        stopSym,
        taskSym,
        unaliasSym,
        whileSym,
        untilSym
    };
    final int[] firstOfExpression = new int[]{
        plusSym,
        minusSym,
        numberSym,
        stringSym,
        aliasSym,
        idSym,
        lparenSym,
        notSym,
        trueSym,
        falseSym,};
    final int[] firstOfPrintExpression = new int[]{
        plusSym,
        minusSym,
        numberSym,
        stringSym,
        aliasSym,
        idSym,
        lparenSym,
        notSym,
        trueSym,
        falseSym,
        ltSym,
        lbracketSym
    };
    final int[] reservedWordorId = new int[]{
        idSym,
        andSym,
        orSym,
        bySym,
        callSym,
        emitSym,
        fileSym,
        fromSym,
        notSym,
        toSym,
        downtoSym,
        throughSym,
        whileSym,
        trueSym,
        falseSym,
        insertSym,
        deleteSym,
        inSym,
        appendSym,
        ontoSym,
        stopSym,
        unaliasSym,
        otherwiseSym,
        untilSym,
        whenSym,};

    /*
     Notation for trees.  I have used a linearized notation for trees here
     to indicate the kind of tree being built.  For example to indicate the
     tree:
     OpVal.ifOp
     |
     --------------------
     |        |         |  
     Cond     Then       Else

     the following linearized notation will be used:

     OpVal.ifOp
     Cond
     Then
     Else

     Entries that end in "OpVal.*Op" indicate the information stored
     in the tree node at that point.  

     Entries that do not end in "OpVal.*Op" indicate that a subtree appears
     at this point.
  
     In many cases calling a procedure does not build any structure
     other than exactly what is built by parsing one of the rhs's.
     In such cases the Build: clause below in the comments simply
     says "transparent". 

     The above would translate into the following TreeNode structure
    
     ------------
     |OpVal.ifOp|
     |----------|
     |    |  0  |
     --|---------
     |
     V 
     ------------      ------------      ------------
     |   COND   |      |   THEN   |      |   ELSE   |
     |----------|      |----------|      |----------|
     |    |   --|----> |    |   --|----> |    |  0  |
     --|---------      --|---------      --|---------
     |                 |                 |
     V                 V                 V 
     Tree for Cond       Tree for Then      Tree for Else 
   
     where:
     COND, THEN and ELSE represent the root of the three
     separate subtrees.  For example, COND could be a
     conditional operator, THEN and ELSE ould be OpVal.stmtListOp

     */
    public TreeNode parse() {
        t.get();
        return parseProgram();
    }

    TreeNode parseProgram() {
        /**
         * ***********************
         * Program : <StmtList> Builds : StmtList
         *
         *************************
         */
        if (debug) {
            System.out.println("Entering parseProgram \n");
        }
        TreeNode tn = parseStmtList();
        // edited by Wes Potts 6/14/04
        // the traverse() is not part of the if (debug) and I'm guessing
        // it should be
        if (debug) {
            System.out.println("Leaving parseProgram \n");
            traverse(tn);
        }
        if (check(eofSym)) {
            verify(eofSym);
        } else {
            verify(firstOfStmt);
        }
        return tn;
    }

    void repairPrint() {
    }

    TreeNode parseStmtList() {
        /**
         * *********************
         * StmtList : Stmt | Stmt StmtList Builds: OpVal.stmtListOp Stmt Stmt
         * ... *********************
         */
        if (debug) {
            System.out.println("Entering parseStmtList \n");
        }
        TreeNode root = new TreeNode(OpVal.stmtListOp);
        TreeNode tn = parseStmt();
        TreeNode treeVal = tn;
        // Awful kluge for repairing a procedure call that got buried in a print statement
        if (treeVal != null && testOp(treeVal, OpVal.printOp)) {
            TreeNode temp = (TreeNode) treeVal.left;
            while (temp.right() != null && !testOp(temp.right(), OpVal.procedureCallOp)) {
                temp = (TreeNode) temp.right;
            }
            if (temp.right() != null) {  // we have the kludge!
                treeVal.right = (TreeNode) temp.right(); // link the proc call as a stmt
                temp.right = null; // remove the proc call from the print stmt
                treeVal = (TreeNode) treeVal.right; // move tree to the proc stmt
            }
        }
        while (check(firstOfStmt)) {
            treeVal.right = parseStmt();
            treeVal = (TreeNode) treeVal.right;
            // Awful kluge for repairing a procedure call that got buried in a print statement
            if (testOp(treeVal, OpVal.printOp)) {
                TreeNode temp = (TreeNode) treeVal.left;
                while (temp.right() != null && !testOp(temp.right(), OpVal.procedureCallOp)) {
                    temp = (TreeNode) temp.right;
                }
                if (temp.right() != null) {  // we have the kludge!
                    treeVal.right = (TreeNode) temp.right(); // link the proc call as a stmtm
                    temp.right = null; // remove the proc call from the print stmt
                    treeVal = (TreeNode) treeVal.right; // move tree to the proc stmt
                }
            }
        }
        root.left = tn;
        if (debug) {
            System.out.println("Leaving parseStmtList \n");
            traverse(tn);
        }
        return root;
    }

    /**
     * ********************
     * Stmt : LetStmt | EmitStmt | PrintStmt | ReturnStmt | SelectStmt |
     * PipeStmt | Block | ProcedureCall
     *
     * Builds: transparent *********************
     */
    TreeNode parseStmt() {
        if (debug) {
            System.out.println("Entering parseStmt \n");
        }
        TreeNode tn = null;   /// PERHAPS TreeNode(nullStmt)
        if (check(letSym)) {
            tn = parseLetStmt();
        } else if (check(printSym)) {
            tn = parsePrintStmt();
        } else if (check(selectSym)) {
            tn = parseSelectStmt();
        } else if (check(insertSym)) {
            tn = parseInsertStmt();
        } else if (check(deleteSym)) {
            tn = parseDeleteStmt();
        } else if (check(appendSym)) {
            tn = parseAppendStmt();
        } else if (check(prependSym)) {
            tn = parsePrependStmt();
        } else if (check(callSym)) {
            tn = parseProcedureOrGeneratorCall();
        } else if (check(generateSym)) {
            tn = parseIterationStmt();
        } else if (check(returnSym)) {
            tn = parseReturnStmt();
        } else if (check(lbraceSym)) {
            tn = parseBlock();
        } else if (check(stopSym)) {
            tn = parseStopStmt();
        } else if (check(echoSym)) {
            tn = parseEchoStmt();
        } else if (check(unaliasSym)) {
            tn = parseUnaliasStmt();
        } else if (check(whileSym)) {
            tn = parseWhileStmt();
        } else if (check(untilSym)) {
            tn = parseUntilStmt();
        } else if (check(new int[]{idSym})) { //, numberSym})) {  // to come some year
            tn = parseProcedureOrGeneratorCall();
        } // Check to see if this code is NOT NEEDED
        else if (check(rbraceSym)) {
            tn = new TreeNode(OpVal.nullStmtOp, tn, null);
        } else if (!"".equals(t.val)) // Somehow there is no more input ... treat as null stmt
        {
            printError("A statement cannot begin with " + t.val);
        }

        if (debug) {
            System.out.println("Leaving parseStmt");
            traverse(tn);
        }
        return tn;
    }

    TreeNode parseBlock() {
        /**
         * ********************
         * Block: LBRACE StmtList RBRACE
         *
         * Builds: StmtList ********************
         */
        if (debug) {
            System.out.println("Entering parseBlock \n");
        }
        verify(lbraceSym);
        TreeNode tn = parseStmtList();
        verify(rbraceSym);
        if (debug) {
            System.out.println("Leaving parseBlock \n");
            traverse(tn);
        }
        return tn;
    }

    TreeNode parseTaskBlock() {
        /**
         * ********************
         * Block: LBRACE Task RBRACE
         *
         * Builds: StmtList ********************
         */
        if (debug) {
            System.out.println("Entering parseTask \n");
        }
        verify(lbraceSym);
        TreeNode tn = parseTask();
        verify(rbraceSym);
        if (debug) {
            System.out.println("Leaving parseTask \n");
            traverse(tn);
        }
        return tn;
    }


    /*
     countReturns -- count the number of returns within a subtree
     */
    int countReturns(Node tn) {
        int count = 0;
        while (tn != null && count == 0) {
            if (testOp(tn, OpVal.returnStmtOp)) {
                return 1;
            }
            tn = tn.right;
        }
        return 0;
    }

    /**
     * ***********************
     * Def : Let <Id> Reference <Exp> ; Builds id <Reference>Op id Exp
     *
     * Def : Let <SubscriptedId> Reference <Exp> ; Builds id <Reference>Op
     * SubscriptedId Exp
     *
     * Def : Let <IdParameterList> Name <FunctionDef> ; Builds
     * OpVal.functionDefOp IdParameterList FunctionDef
     *
     *
     * Def : Let <Id> Reference <ProcedureDef> ; Builds OpVal.procedureDefOp
     * IdParameterList FunctionDef
     *
     *
     * //Def : Let <Id> Name <GeneratorDef> ; Builds OpVal.generatorDefOp
     * IdParameterList generatorDef ************************
     */
    TreeNode parseLetStmt() {
        if (debug) {
            System.out.println("Entering parseLetStmt \n");
        }
        TreeNode tn = null;  // Bad form
        verify(letSym);

        if (check(reservedWordorId) || check(lparenSym)) { // maybe a proc or func or task
            if (t.val.equals("iterator")) {  //special case for iterator!
                tn = parseExpression(true); // note this will do weird things, e.g., 
                // if we write iterator + x
                verify(nameSym);
                tn.right = parseExpression(true);
                tn = new TreeNode(OpVal.idNameOp, tn, null);
            } else { // Normal case ...  Let something name somethingelse
                boolean foundParameter = false;
                if (check(reservedWordorId)) { // maybe a proc or func or task
                    t.tokenType = idSym;  // change it to an id since we're parsing a Let stmt
                    tn = parseIdSeq(); // have to place this lower in the tree ... see below
                    while (check(new int[]{lbracketSym})) {   // added by morell 6/7/04  array subscr on lhs
                        verify(lbracketSym);
                        tn.right = parseExpression(true);
                        tn = new TreeNode(OpVal.subscriptOp, tn, null);
                        verify(rbracketSym);
                    }
                }
                if (check(lparenSym)) { // must be a proc/func/task
                    foundParameter = true;
                    TreeNode treeNode = parseParmIdList();  // ljm -- 6/19/04
                    if (tn == null) {
                        tn = treeNode;
                    } else {
                        tn.right = treeNode.left;               // ljm -- 6/19/04
                        treeNode.left = tn;                     // ljm -- 6/19/04
                    }
                    tn = treeNode;
                    if (debug) {
                        TreeNode d = new TreeNode(OpVal.debugOp, tn, null);
                        traverse(d);
                    }
                }
                String mangledName = "";
                if (check(nameSym)) {  // not if it is a nameSym
                    verify(nameSym);
                    // Now at this point in the original language we could not
                    tn = new TreeNode(OpVal.idNameOp, tn, null);
                } else if (check(aliasSym)) { // not if it is an alias
                    verify(aliasSym);
                    tn = new TreeNode(OpVal.idAliasOp, tn, null);
                } else {
                    verify(new int[]{nameSym, aliasSym}); // ljm: 01/01/07
                }
                if (!check(new int[]{procedureSym, functionSym, generatorSym, taskSym})) {   // We have an ordinary "Let a name expr"
                    tn.left.right = parseExpression(true);
                    // This is an incredible kluge below.  This is to patch the
                    // situation in which we write : Let f(x) name exp
                    // In this case f(x) was parsed as a header, but needs
                    // to be turned into a function call
                    if (testOp(tn.left, OpVal.headerOp)) {
                        tn.left.info = new StickyNote(OpVal.functionCallOp);
                    }
                } else {  // We are defining a function, procedure, generator or task
                    mangledName = mangle((TreeNode) tn.left);
//System.out.println("Processing mangledName=" + mangledName);
                    if (check(functionSym)) { // Defining a function!
                        tn = (TreeNode) tn.left;
                        verify(functionSym);
                        TreeNode temp = new TreeNode(OpVal.functionDefOp, (TreeNode) tn, null);
                        symtab.put(mangledName, "function");
                        //System.out.println("Function1:'" +mangledName + "' entered into symtab");
                        if (!check(lbraceSym)) {
                            //    New version: Let min name Function min of (a) and (b) {...}
                            //    Must create two versions of the function:
                            //    1. min(a,b)
                            //    2. min of (a) and (b)
                            //       
                        } else { // Original version: Let min of (a) and (b) name function {...}
                            tn.right = parseFunctionDef();
                            TreeNode tempNode = (TreeNode) temp.left.right.left;

                            //System.out.println ("tempnode=" + tempNode);
                            if (tempNode == null) {
                                printError("Error:No return statement in the function definition");
                            }
                            int returnCount = countReturns(tempNode);
                            //System.out.println("returnCount is " + returnCount);
                            if (returnCount == 0) {
                                printError("The function definition for " + mangledName
                                        + " should contain at least one 'Return'");
                            }

                            //tn = new TreeNode(OpVal.functionDefOp,tn, null);  
                            tn = temp;
                            //String mangledName = mangle(tn);
                            //symtab.put(mangledName, "function"); 
                            if (debug) {
                                System.out.println("Entering: " + mangledName + " as "
                                        + (String) symtab.get(mangledName) + "\n");
                            }
                            //System.out.println("Function:'" +mangledName + "' entered into symtab");
                        }
                    } else if (check(procedureSym)) {
                        tn = (TreeNode) tn.left;
                        TreeNode temp = new TreeNode(OpVal.procedureDefOp, tn, null);

                        symtab.put(mangledName, "procedure");
                        tn.right = parseProcedureDef();
                        // tn = new TreeNode(OpVal.procedureDefOp,tn, null);  
                        tn = temp;
                        if (debug) {
                            System.out.println("Entering: " + mangledName + " as "
                                    + (String) symtab.get(mangledName) + "\n");
                        }
                    } else if (check(taskSym)) {
                        tn = (TreeNode) tn.left;
                        TreeNode temp = new TreeNode(OpVal.taskDefOp, tn, null);
                        symtab.put(mangledName, "task");
                        tn.right = parseTaskDef();
                        tn = temp;


                    } else if (check(generatorSym)) {
                        tn = (TreeNode) tn.left;
                        TreeNode temp = new TreeNode(OpVal.generatorDefOp, tn, null);
                        symtab.put(mangledName, "generator");

                        tn.right = parseTaskDef(); // Hey generators and tasks have 
                        // the same structure, so keep 
                        // things simple
                        tn = temp;
                        if (debug) {
                            System.out.println("Entering: " + mangledName + " as "
                                    + (String) symtab.get(mangledName) + "\n");
                        }
                    }
                    // Closing else intentionally omitted since it is not necessary
                }

            }
        } else {
            error(new int[]{idSym, lparenSym});
        }
        if (debug) {
            System.out.println("Leaving parseLetStmt \n");
            TreeNode d = new TreeNode(OpVal.debugOp, tn, null);
            traverse(d);
        }
        return tn;
    }

    TreeNode parsePrintStmt() {
        /**
         * *********************
         * EmitStmt : 'print' <Expression> Builds : OpVal.printOp Expression
         * *********************
         */
        if (debug) {
            System.out.println("Entering parsePrintStmt \n");
        }
        TreeNode tn = null;

        Context context = new Context(t);

        verify(printSym);
        tn = parsePrintExpressionList();
        tn = new TreeNode(OpVal.printOp, tn, null);
        if (debug) {
            System.out.println("Leaving parsePrintStmt \n");
            traverse(tn);
        }
        tn.context = context;

        return tn;
    }

    TreeNode parseEchoStmt() {
        /**
         * *********************
         * EmitStmt : 'print' <Expression> Builds : OpVal.printOp Expression
         * *********************
         */
        if (debug) {
            System.out.println("Entering parsePrintStmt \n");
        }
        TreeNode tn = null;

        verify(echoSym);
        tn = parsePrintExpressionList();
        tn = new TreeNode(OpVal.echoStmtOp, tn, null);
        if (debug) {
            System.out.println("Leaving parseEchoStmt \n");
            traverse(tn);
        }
        return tn;
    }

    TreeNode parseProcedureOrGeneratorCall() {
        /**
         * ************************
         * ProcedureCall :[Call] Id '(' ExpressionList ')' [WHILE|UNTIL] Builds
         * : OpVal.procedureCallOp Id ExpressionList ************************
         */
        if (debug) {
            System.out.println("Entering parseProcedureOrGeneratorCall \n");
        }
        if (check(callSym)) {
            verify(callSym); // toss away ... just punctuation
        }
        TreeNode tn = parsePrimaryOrFunction(true);

        if (!testOp(tn, OpVal.procedureCallOp) && !testOp(tn, OpVal.generatorCallOp)) {
            if (!testOp(tn, OpVal.functionCallOp)) {
                printError("'" + tn.info.val + "' has not been defined as a procedure or generator, but it is being called as one" + t);
            } else {
                printError("'" + tn.left.info.val
                        + "' has been defined as a function\n"
                        + "but it is being called as if it were a procedure.\n"
                        + "If it is supposed to be an argument to another function or procedure\n"
                        + "surround it with parentheses as in (" + tn.left.info.val + "(...))");
            }
        }
        if (testOp(tn, OpVal.generatorCallOp)) {
            Node n = tn.left.right;
            while (n.right != null) {
                n = n.right;
            }
            if (check(whileSym)) {
                n.right = parseWhile();
            } else if (check(untilSym)) {
                n.right = parseUntil();
            }

            if (check(pipeSym)) {
                verify(pipeSym);
                tn.right = parsePipeline();
            } else {  // A generate without a pipe ... construct the missing tree, BU, R2L
                TreeNode tmp = new TreeNode(OpVal.stmtListOp);
                tmp = new TreeNode(OpVal.stmtListOp, null, tmp);
                tmp = new TreeNode(OpVal.stmtListOp, null, tmp);
                tmp = new TreeNode(OpVal.taskOp, tmp, null);
                tmp = new TreeNode(OpVal.condOp, null, tmp);
                tn.right = new TreeNode(OpVal.guardedTaskOp, tmp, null);
            }
            tn = new TreeNode(OpVal.pipeOp, tn, null);
        }
        if (debug) {
            System.out.println("Leaving parseIterationStmt \n");
            traverse(tn);
        }
        if (debug) {
            System.out.println("Leaving parseProcedureCall \n");
            traverse(tn);
        }
        if (debug) {
            System.out.println("ppc: \n");
            traverse(tn);
        }
        return tn;
    }

    /**
     * ***********************
     * PipeStmt : GenerateStmt PIPE Pipeline Builds: OpVal.pipeOp GenerateStmt
     * Pipeline PipeStmt : GenerateStmt Builds: Transparent
     * ***********************
     */
    TreeNode parseIterationStmt() {
        if (debug) {
            System.out.println("Entering parseIterationStmt \n");
        }
        TreeNode tn = parseGenerateStmt();
        if (check(pipeSym)) {
            verify(pipeSym);
            tn.right = parsePipeline();
        } else {  // A generate without a pipe ... construct the missing tree, BU, R2L
            TreeNode tmp = new TreeNode(OpVal.stmtListOp);
            tmp = new TreeNode(OpVal.stmtListOp, null, tmp);
            tmp = new TreeNode(OpVal.stmtListOp, null, tmp);
            tmp = new TreeNode(OpVal.taskOp, tmp, null);
            tmp = new TreeNode(OpVal.condOp, null, tmp);
            tn.right = new TreeNode(OpVal.guardedTaskOp, tmp, null);
        }
        tn = new TreeNode(OpVal.pipeOp, tn, null);
        if (debug) {
            System.out.println("Leaving parseIterationStmt \n");
            traverse(tn);
        }
        return tn;
    }

    /**
     * ************************
     * Pipeline : Task | Task PIPE Pipeline Builds: Task ( list of Tasks) Task
     * Task ... ************************
     */
    TreeNode parsePipeline() {
        if (debug) {
            System.out.println("Entering parsePipeline \n");
        }
        TreeNode tn;
        tn = parseGuardedTask();
        if (check(pipeSym)) {
            verify(pipeSym);
            tn.right = parsePipeline();
        }

        if (debug) {
            System.out.println("Leaving parsePipeline \n");
            traverse(tn);
        }
        return tn;
    }

    /**
     * *************************
     * GuardedTask : Cond Task
     *
     * Builds: OpVal.guardedTaskOp OpVal.condOp OpVal.taskOp
     * *************************
     */
    TreeNode parseGuardedTask() {
        if (debug) {
            System.out.println("Entering parseGuardedTask \n");
        }
        TreeNode tn;

        if (check(whenSym)) {
            verify(whenSym);
            tn = parseExpression(true);
        } else {
            tn = null;
        }
        tn = new TreeNode(OpVal.condOp, tn, null);
        TreeNode temp = parseTask();
        tn.right = new TreeNode(OpVal.taskOp, temp, null);
        tn = new TreeNode(OpVal.guardedTaskOp, tn, null);
        if (debug) {
            System.out.println("Leaving parseGuardedTask \n");
            traverse(tn);
        }
        return tn;
    }

    /**
     * *************************
     * Task : ATBEGIN ':' Stmt ATEACH ':' Stmt ATEND ':' Stmt | ATBEGIN ':' Stmt
     * ATEACH ':' Stmt | ATBEGIN ':' Stmt ATEND ':' Stmt | ATBEGIN ':' Stmt |
     * ATEACH ':' Stmt ATEND ':' Stmt | ATEACH ':' Stmt | ATEND ':' Stmt | Stmt
     *
     * Builds: OpVal.taskOp Stmt (null if omitted) Stmt (null if omitted) Stmt
     * (null if omitted)
     *
     **************************
     */
    TreeNode parseTask() {
        if (debug) {
            System.out.println("Entering parseTask");
        }
        TreeNode tn;
        if (check(new int[]{atbeginSym, ateachSym, atendSym})) { // complex case
            if (check(atbeginSym)) {
                verify(atbeginSym);
                verify(colonSym);
                tn = parseStmt();
            } else {
                tn = new TreeNode(OpVal.stmtListOp);
            }

            if (check(ateachSym)) {
                verify(ateachSym);
                verify(colonSym);
                tn.right = parseStmt();
            } else {
                tn.right = new TreeNode(OpVal.stmtListOp);
            }

            if (check(atendSym)) {
                verify(atendSym);
                verify(colonSym);
                tn.right.right = parseStmt();
            } else {
                tn.right.right = new TreeNode(OpVal.stmtListOp);
            }
        } else {
            // Now for the patch ... if none of the above were true then there
            // had better be a single stmt which will be interpreted as an @each
            if (!check(firstOfStmt)) { // ljm: 7/15/04
                error(firstOfStmt);
            }
            tn = new TreeNode(OpVal.stmtListOp);
            tn.right = parseStmt();
            tn.right.right = new TreeNode(OpVal.stmtListOp);
        }
        //tn = new TreeNode (OpVal.taskOp,tn, null);
        if (debug) {
            System.out.println("Leaving parseTask");
            traverse(tn);
        }
        return tn;
    }

    /**
     * ********************
     * While : | WHILE Condition
     *
     * Builds: OpVal.whileOp Condition ********************
     */
    TreeNode parseWhile() {
        if (debug) {
            System.out.println("Entering parseWhile");
        }
        TreeNode tn;
        verify(whileSym);
        tn = parseExpression(true);
        tn = new TreeNode(OpVal.whileOp, tn, null);
        if (debug) {
            System.out.println("Leaving parseWhile");
            traverse(tn);
        }
        return tn;
    }

    /**
     * ********************
     * Until : | UNTIL Condition
     *
     * Builds: OpVal.whileOp notOp Condition ********************
     */
    TreeNode parseUntil() {
        if (debug) {
            System.out.println("Entering parseUntil");
        }
        TreeNode tn;
        verify(untilSym);
        tn = parseExpression(true);
        tn = new TreeNode(OpVal.notOp, tn, null);
        tn = new TreeNode(OpVal.whileOp, tn, null);
        if (debug) {
            System.out.println("Leaving parseUntil");
            traverse(tn);
        }
        return tn;
    }

    TreeNode parseWhileStmt() {
        if (debug) {
            System.out.println("Entering parseWhile");
        }
        TreeNode tn;
        verify(whileSym);
        tn = parseExpression(true);
        tn = new TreeNode(OpVal.whileOp, tn, null);
        if (debug) {
            System.out.println("Leaving parseWhile");
            traverse(tn);
        }
        if (check(pipeSym)) {
            verify(pipeSym);
            tn.left.right = parsePipeline();
        }
        return tn;
    }

    TreeNode parseUntilStmt() {
        if (debug) {
            System.out.println("Entering parseWhile");
        }
        TreeNode tn;
        verify(untilSym);
        tn = parseExpression(true);
        tn = new TreeNode(OpVal.notOp, tn, null);
        tn = new TreeNode(OpVal.whileOp, tn, null);
        if (debug) {
            System.out.println("Leaving parseWhile");
            traverse(tn);
        }
        if (check(pipeSym)) {
            verify(pipeSym);
            tn.left.right = parsePipeline();
        }
        return tn;
    }

    /**
     * ***********************
     * Source : Range | List | ID | FunctionCall | File ;;
     *
     * Builds: transparent
     *
     ************************
     */
    TreeNode parseSource() {
        if (debug) {
            System.out.println("Entering parseSource");
        }
        TreeNode tn;
        if (check(new int[]{ltSym,
                    lbracketSym,})) {
            if (check(ltSym)) {
                tn = parseList(gtSym);
            } else {
                tn = parseList(rbracketSym);
            }
        } else if (check(lparenSym)) {  // for future expansion using
            tn = parseRange();
        } else if (check(firstOfExpression)) {
            tn = parseRange();
        } else if (check(fileSym)) {
            tn = parseFile();
        } else { // can'tree happen
            tn = new TreeNode(t);
            tn.setContext(t);
            verify(idSym);
        }
        if (debug) {
            System.out.println("Leaving parseSource");
            traverse(tn);
        }
        return tn;
    }

    TreeNode parseReturnStmt() {
        /**
         * *********************
         * ReturnStmt : 'return' Expression Builds: OpVal.returnOp Expression
         * *********************
         */
        if (debug) {
            System.out.println("Entering parseReturnStmt");
        }
        TreeNode tn = null;

        verify(returnSym);
        tn = parseExpression(true);
        tn = new TreeNode(OpVal.returnStmtOp, tn, null);
        if (debug) {
            System.out.println("Leaving parseReturnStmt");
            traverse(tn);
        }
        return tn;
    }

    TreeNode parseStopStmt() {
        /**
         * *********************
         * StopStmt : 'stop' Builds: OpVal.stopOp *********************
         */
        if (debug) {
            System.out.println("Entering parseStopStmt");
        }
        TreeNode tn = null;
        Context context = new Context(t);
        // System.out.println("Setting contxt for stop to (@" + tree.lineNo() + "/" + tree.charPos() +")");
        // System.out.println("Token is "+ tree);
        verify(stopSym);
        tn = new TreeNode(OpVal.stopStmtOp, tn, null);
        tn.context = context;
        if (debug) {
            System.out.println("Leaving parseStopStmt");
            traverse(tn);
        }
        return tn;
    }

    TreeNode parseSelectStmt() {
        /**
         * *********************
         * SelectStmt : 'select' SelectBody Builds: SelectBody (transparently)
         *
         ***********************
         */
//debug=true;
        if (debug) {
            System.out.println("Entering parseSelectStmt()");
        }

        verify(selectSym);
        TreeNode tn = parseSelectBody();
        if (debug) {
            System.out.println("Leaving parseSelectStmt()");
            traverse(tn);
        }
        return tn;
    }

    /**
     * ********************
     *
     * SelectBody: SelectList | '{' SelectList '}'
     *
     * Builds : OpVal.selectOp GuardedStmt GuardedStmt GuardedStmt GuardedStmt
     * GuardedStmt * ********************
     */
    TreeNode parseSelectBody() {
        if (debug) {
            System.out.println("Entering parseSelectBody()");
        }
        TreeNode tn = null;
        if (check(lbraceSym)) {
            consume();
            tn = parseSelectList();
            verify(rbraceSym);
        } else {
            tn = parseSelectList();
        }
        tn = new TreeNode(OpVal.selectOp, tn, null);
        if (debug) {
            System.out.println("Leaving parseSelectBody()");
            traverse(tn);
        }
        return tn;
    }
    /**
     * ********************
     * SelectList : GuardedStmt | GuardedStmt SelectList Builds: GuardedStmt
     * GuardedStmt GuardedStmt ... ********************
     */
    int otherwisecount;

    TreeNode parseSelectList() {
        if (debug) {
            System.out.println("Entering parseSelectList");
        }

        TreeNode tn;
        if (check(new int[]{
                    otherwiseSym
                })) {
            Context context = new Context(t);
            tn = new TreeNode(OpVal.otherwiseOp);
            tn.setContext(t);
            consume();
            verify(arrowSym);
            tn.right = parseStmt();
            tn = new TreeNode(OpVal.guardedStmtOp, tn, null);
            if (debug) {
                System.out.println("Leaving parseGuardedStmt");
                traverse(tn);
            }
        } else {
            //System.out.println("Sym  =" + tree + "  "+ check(trueSym));
       /* 
             Right here ... need to modify this to properly parse Select's
             We need to Mark, and parse a guarded statement.  If it fails
             we need to back up to the mark, fill in the tn->right with null,
             and return the tn, which will have the select found to that point
             Token.debug=false;
             tree.mark();
             if (Token.debug)  System.out.println("Marking " + Token.markpos + "  " + Token.markch);
             tn = parseExpression(true);
             if (Token.debug)  System.out.println("Expression returned" + tn); 
             tree.reset();
             if (Token.debug) System.out.println("Resetting to" + Token.markpos + "  " + Token.markch );
       

             if (testOp(tn, OpVal.procedureCallOp)) {
             System.out.println("Found procedure call where a condition was expected");
             return null;   
             }
             */
            tn = parseGuardedStmt();
            if (check(new int[]{
                        idSym,
                        numberSym,
                        lparenSym,
                        trueSym,
                        falseSym,
                        notSym,
                        otherwiseSym
                    })) {

                tn.right = parseSelectList();
            }
        }
        // debug=true;
        if (debug) {
            System.out.println("Leaving parseSelectList");
            traverse(tn);
        }
        return tn;
    }

    TreeNode parseGuardedStmt() {
        if (debug) {
            System.out.println("Entering parseGuardedStmt");
        }
        TreeNode tn = parseExpression(true);
        verify(arrowSym);
        tn.right = parseStmt();
        tn = new TreeNode(OpVal.guardedStmtOp, tn, null);
        if (debug) {
            System.out.println("Leaving parseGuardedStmt");
            traverse(tn);
        }
        return tn;
    }

    /*
     File : FILESYM STRING
     */
    TreeNode parseFile() {
        if (debug) {
            System.out.println("Entering parseFile");
        }
        TreeNode tn;
        verify(fileSym);
        tn = new TreeNode(t);
        verify(new int[]{idSym, stringSym});
        tn = new TreeNode(OpVal.fileOp, tn, null);
        if (debug) {
            System.out.println("Leaving parseFile");
            traverse(tn);
        }
        return tn;
    }

    /*
     Range :  NUMBER TO NUMBER
     |  NUMBER TO NUMBER BY NUMBER
     |  STRING TO STRING
     |  STRING  
     */
    TreeNode parseRange() {
        if (debug) {
            System.out.println("Entering parseRange");
        }
        TreeNode tn;
        if (check(firstOfExpression)) {
            tn = parseExpression(true);
            if (check(toSym)) {
                verify(toSym);
                tn.right = parseExpression(true);
                if (check(bySym)) {
                    verify(bySym);
                    tn.right.right = parseExpression(true);
                    tn = new TreeNode(OpVal.rangeNxNxN_Op, tn, null);
                } else {
                    tn = new TreeNode(OpVal.rangeNxN_Op, tn, null);
                }
            } else if (check(downtoSym)) {
                verify(downtoSym);
                tn.right = parseExpression(true);
                if (check(bySym)) {
                    verify(bySym);
                    tn.right.right = parseExpression(true);
                    tn = new TreeNode(OpVal.downto_rangeNxNxN_Op, tn, null);
                } else {
                    tn = new TreeNode(OpVal.downto_rangeNxN_Op, tn, null);
                }
            }
            // else ... it is simply an expression ... implying a list
        } else if (check(stringSym)) {
            tn = new TreeNode(t); // put the string into a node
            tn.setContext(t);
            verify(stringSym);
            if (check(toSym)) {
                verify(toSym);
                tn.right = new TreeNode(t); // put second string in
                tn.setContext(t);
                verify(stringSym);
                tn = new TreeNode(OpVal.rangeSxS_Op, tn, null);
            } else if (check(downtoSym)) {
                verify(downtoSym);
                tn.right = new TreeNode(t); // put second string in
                tn.setContext(t);
                verify(stringSym);
                tn = new TreeNode(OpVal.downto_rangeSxS_Op, tn, null);
            } else {
                tn = new TreeNode(t);
                tn.setContext(t);
                tn = new TreeNode(OpVal.downto_rangeS_Op, tn, null);
            }
        } else {
            return tn = null;
        }
        if (debug) {
            System.out.println("Leaving parseRange");
            traverse(tn);
        }
        return tn;
    }

    TreeNode parsePrintExpressionList() {
        /**
         * ***************************
         * ExpressionList : Expression | Id [','] ExpressionList
         * ***************************
         */
        //    debug=true;
        if (debug) {
            System.out.println("Entering parseExpressionList");
        }
        TreeNode tn = parseExpression(false);
        //System.out.println ("tn="+tn);
        if (testOp(tn, OpVal.procedureCallOp)) { // proc call included in print expr list
            // do nothing ... just exit .... see kluge processing in parseStmtList
        } else if (!Token.semiBreak && check(firstOfPrintExpression)) { // try it and see
            //verify (commaSym);
            //System.out.println ("firstOfPrintExpression is true\n");
            tn.right = parsePrintExpressionList();
        }

        if (debug) {
            System.out.println("Leaving parseExpressionList");
            traverse(tn);
        }
        return tn;
    }

    TreeNode parseExpressionList() {
        /**
         * ***************************
         * ExpressionList : Id | Id [','] ExpressionList
         * ***************************
         */
        if (debug) {
            System.out.println("Entering parseExpressionList");
        }
        TreeNode tn;
        if (check(rparenSym)) {  // No parameter
            tn = null;
        } else { // There should be a parameter
            tn = parseExpression(false);
            if (testOp(tn, OpVal.procedureCallOp)) {
                printError("Error: Found procedure call in Expression List ");
            } else if (check(firstOfExpression)) { // try it and see
                //verify (commaSym);
                tn.right = parseExpressionList();
            }
        }
        if (debug) {
            System.out.println("Leaving parseExpressionList");
            traverse(tn);
        }
        return tn;
    }

    TreeNode parseExpression(boolean withFunction) {
        /**
         * ***********************
         * Exp : CondExp or CondTerm | CondTerm Builds: OpVal.orOp
         * OpVal.condExpOp OpVal.condTermOp ***********************
         */
        if (debug) {
            System.out.println("Entering parseExpression");
        }
        TreeNode tn = parseCondTerm(withFunction);
        while (check(orSym)) {
            verify(orSym);
            tn.right = parseCondTerm(withFunction);
            tn = new TreeNode(OpVal.orOp, tn, null);
        }
        if (debug) {
            System.out.println("Leaving Expression OpVal.Op with " + tn.info);
            traverse(tn);
        }

        return tn;
    }

    TreeNode parseCondTerm(boolean withFunction) {
        /**
         * ***********************
         * CondTerm : CondTerm and CondPrimary | CondPrimary
         *
         * Builds: OpVal.andOp OpVal.condTermOp OpVal.condprimaryOp
         * ***********************
         */
        if (debug) {
            System.out.println("Entering parseCondTerm");
        }
        TreeNode tn = parseCondPrimary(withFunction);
        while (check(andSym)) {
            verify(andSym);
            tn.right = parseCondPrimary(withFunction);
            tn = new TreeNode(OpVal.andOp, tn, null);
        }
        if (debug) {
            System.out.println("Leaving CondTerm OpVal.Op with " + tn.info);
            traverse(tn);
        }
        return tn;
    }

    TreeNode parseCondPrimary(boolean withFunction) {
        /**
         * ***********************
         * CondPrimary : Condition Builds: transparent
         *
         * CondPrimary: not ConditionPrimary Builds: OpVal.notOp
         * OpVal.ConditionPrimaryOp ***********************
         */
        if (debug) {
            System.out.println("Entering parseCondPrimary");
        }
        TreeNode tn = null;

        if (check(notSym)) {
            int cnt = 0;
            do {
                verify(notSym);
                cnt++;
            } while (check(notSym));
            if (cnt % 2 == 1) // odd number of not's ... why not do some optimization!
            {
                tn = new TreeNode(OpVal.notOp, parseCondition(withFunction), null);
            } else {
                tn = parseCondition(withFunction);
            }
        } else {
            tn = parseCondition(withFunction);
        }
        if (debug) {
            System.out.println("Leaving CondPrimary OpVal.Op with " + tn.info);
            traverse(tn);
        }
        return tn;
    }

    TreeNode parseCondition(boolean withFunction) {
        /**
         * *************************
         * Condition : Expression operator Expression | Expression
         * *************************
         */
        if (debug) {
            System.out.println("Entering parseCondition");
        }
        TreeNode tn = null;
        Context context = new Context(t);

        /*
         ljm: 9/21/05 removed the following because early recognition
         of literals 'true' and 'false' meant that they could not 
         be compared (even though expressions that compared the results
         of logical expressions could be compared).

         if (check(trueSym)) {
         tn = new TreeNode (OpVal.trueOp);
         tn.context = context;
         consume();
         }
         else if (check(falseSym)) {
         tn = new TreeNode (OpVal.falseOp);
         tn.context = context;
         consume();
         }
         else if (check(otherwiseSym)) {
         tn = new TreeNode (OpVal.otherwiseOp);
         tn.context = context;
         consume();
         }
         else  */
        {
            TreeNode e = parseArithmeticExpression(true);

            //TreeNode tn = new TreeNode (tree.tokenType, e, null);
            if (!(Token.commaBreak || Token.lineBreak)
                    && check(new int[]{ltSym, leSym, gtSym, geSym, eqSym, neSym})) {
                if (t.tokenType == ltSym) {
                    tn = new TreeNode(OpVal.ltOp, e, null);
                } else if (t.tokenType == leSym) {
                    tn = new TreeNode(OpVal.leOp, e, null);
                } else if (t.tokenType == gtSym) {
                    tn = new TreeNode(OpVal.gtOp, e, null);
                } else if (t.tokenType == geSym) {
                    tn = new TreeNode(OpVal.geOp, e, null);
                } else if (t.tokenType == geSym) {
                    tn = new TreeNode(OpVal.geOp, e, null);
                } else if (t.tokenType == eqSym) {
                    tn = new TreeNode(OpVal.eqOp, e, null);
                } else if (t.tokenType == neSym) {
                    tn = new TreeNode(OpVal.neOp, e, null);
                }

                verify(new int[]{ltSym, leSym, gtSym, geSym, eqSym, neSym});
                e.right = parseArithmeticExpression(true);
            } else {  // just a lonely expression; 
                tn = e;
            }
            if (debug) {
                System.out.println("Leaving parseCondition");
                traverse(tn);
            }
        }
        return tn;
    }

    TreeNode parseArithmeticExpression(boolean withFunction) {
        /**
         * ***********************
         * Exp : Exp + Term | Exp - Term | Term ***********************
         */
        if (debug) {
            System.out.println("Entering parseArithmeticExpression");
        }
        TreeNode tn = parseTerm(withFunction);
        while (check(new int[]{plusSym, minusSym})
                && !(Token.commaBreak)) {
            if (debug) {
                System.out.println("Entering parseArithmeticExpression Loop");
            }
            if (check(plusSym)) {
                verify(plusSym);
                tn.right = parseTerm(withFunction);
                tn = new TreeNode(OpVal.addOp, tn, null);
            } else if (check(minusSym)) {
                verify(minusSym);
                tn.right = parseTerm(withFunction);
                tn = new TreeNode(OpVal.subtractOp, tn, null);
            }
        }
        if (debug) {
            System.out.println("Leaving ArithmeticExpression OpVal.Op with " + tn.info);
            traverse(tn);
        }
        return tn;
    }

    TreeNode parseTerm(boolean withFunction) {
        /**
         * ***********************
         * Term : Term * Primary | Term / Primary | Term % Primary | Primary
         *
         * Builds: OpVal.divideOp OpVal.primaryOp OpVal.primaryOp or Builds:
         * OpVal.multiplyOp OpVal.primaryOp OpVal.primaryOp or Builds:
         * OpVal.modOp OpVal.primaryOp OpVal.primaryOp ***********************
         */
        if (debug) {
            System.out.println("Entering parseTerm");
        }

        TreeNode tn;
        tn = parsePrimaryOrFunction(withFunction);
        // System.out.println(tn);
        while (check(new int[]{starSym, slashSym, percentSym})) {
            if (check(starSym)) {
                verify(starSym);
                tn.right = parsePrimaryOrFunction(withFunction);
                tn = new TreeNode(OpVal.multiplyOp, tn, null);
            } else if (check(percentSym)) {                     //chad added for %
                verify(percentSym);
                tn.right = parsePrimaryOrFunction(withFunction);
                tn = new TreeNode(OpVal.modOp, tn, null);
            } else {
                verify(slashSym);
                tn.right = parsePrimaryOrFunction(withFunction);
                tn = new TreeNode(OpVal.divideOp, tn, null);
            }
        }
        if (debug) {
            System.out.println("Leaving parseTerm OpVal.Op with " + tn.info);
            traverse(tn);
        }
        return tn;
    }

    TreeNode parsePrimaryOrFunction(boolean withFunction) {
        if (debug) {
            System.out.println("Entering parsePrimaryOrFunction");
        }
        String name = "";
        TreeNode tn;
        //if (check (new int [] {lparenSym, numberSym, stringSym})) { // Leading parameter

        //tn = parsePossibleFunctionCall(name);
        //} 
        // else 
        if (check(reservedWordorId)) // Leading id
        {
            tn = parsePossibleFunctionCall(withFunction, name);
        } else {
            tn = parsePrimary();
        }
        return tn;
    }

    TreeNode parsePossibleFunctionCall(boolean withFunction, String name) {
        //debug = true;
        if (debug) {
            System.out.println("Entering parsePossibleFunctionCall with" + name);
        }
        // Note that the only way to get here is if the next input is an id or a reserved word
        boolean possibleFunctionCall = false;
        boolean realFunctionCall = false;
        TreeNode tn = new TreeNode(); // dummy node
        tn.setContext(t);
        TreeNode n = tn;
        String newname;
        if (!"".equals(name)) {
            newname = name + "_" + t.val;
        } else {
            newname = t.val;
        }

        String partialname = newname;
        if (debug) {
            System.out.println("Here1: " + t + "/" + newname + "/" + partialname);
        }
        Object obj = symtab.get(newname);
        if (debug) {
            System.out.println("Here2: " + t + "/" + obj + "/" + partialname);
        }
        if (debug) {
            System.out.println("Checking for reserved word or id" + t);
        }
        while (check(reservedWordorId) && obj != null) { // newname is a valid extension
            name = newname;
            possibleFunctionCall = true;
            verify(reservedWordorId);
            if (debug) {
                System.out.println("Here3:" + t + "/" + newname + "/" + partialname);
            }
            if (check(reservedWordorId)) { // see if this id can be appended
                String oldnewname = newname;
                String oldpartialname = partialname;
                newname = name + "_" + t.val;
                partialname = partialname + "_" + t.val;
                obj = symtab.get(newname);
                if (debug) {
                    System.out.println("Here4:" + t + "/" + newname + "/" + partialname);
                }
                //verify(reservedWordorId);
                if (debug) {
                    System.out.println("Here5:" + t + "/" + obj + "/" + name);
                }
                if (obj == null) { // revert back to previous name
                    partialname = oldpartialname;
                    newname = oldnewname;
                } else {
                    while (check(reservedWordorId) && obj != null) {
                        name = newname;
                        verify(reservedWordorId);
                        newname = name + "_" + t.val;
                        if (debug) {
                            System.out.println("Here6" + t + "/" + newname + "/" + partialname);
                        }
                        obj = symtab.get(newname);
                        if (obj != null) {
                            partialname = partialname + "_" + t.val;
                        }
                    }
                }
                if (debug) {
                    System.out.println("Here7:" + t + "/" + newname + "/" + partialname);
                }

            }
            // We have now constructed a partial function call that may
            // be parameter-extendable.
            // See if we can parameter-extend it by  
            TreeNode prev = n;
            n.right = new TreeNode(partialname);
            n = (TreeNode) n.right;
            n.setContext(t);

            newname = name + "()";
            if (debug) {
                System.out.println("Here8:" + t + "/" + newname + "/" + partialname);
            }
            obj = symtab.get(newname);
            if (debug) {
                System.out.println("Here9:" + t + "/" + obj + "/" + partialname);
            }
            if (debug) {
                System.out.println("Here9.5:" + t + "/" + obj + "/" + partialname);
            }
            //if (obj == null && realFunctionCall) {  //  function not found, backup to previous
            //n.right = null;
            //}
            //else {  // We found a symtab entry for this
            if (obj != null) {
                if (check(lparenSym)) {
                    name = newname;
                    realFunctionCall = true;  // if it looks like a duck ...
                    verify(lparenSym);
                    n.right = new TreeNode(OpVal.parameterOp,
                            parseExpressionList(), null);
                    // System.out.println("Here4:" + tree.val );
                    // traverse(n);
                    n = (TreeNode) n.right;
                    Node node = n.left;
                    int count = 0;
                    verify(rparenSym);
                    if (debug) {
                        System.out.println("Here10:" + n);
                    }
                } else {
                    while (check(numberSym) || check(stringSym)) {

                        boolean firsttime = true;
                        if (check(numberSym)) {
                            if (debug) {
                                System.out.println("Extending routine name " + newname + " with " + t);
                            }
                            name = newname;
                            realFunctionCall = true;  // if it looks like a duck ...
                            TreeNode nn = new TreeNode(OpVal.numberOp);
                            TreeNode temp = new TreeNode(Double.parseDouble(t.val));
                            temp.setContext(t);
                            nn.setContext(t);
                            nn.left = temp;
                            verify(numberSym);
                            if (firsttime) {
                                n.right = new TreeNode(OpVal.parameterOp, nn, null);
                                firsttime = false;
                            } else { // chase down the tree and add this at the bottom
                                Node tt = n.right.left;
                                while (tt.right != null) {
                                    tt = tt.right;
                                }
                                tt.right = nn;
                            }
                            // System.out.println("Here4:" + tree.val );
                            // traverse(n);
                            n = (TreeNode) n.right;
                            Node node = n.left;
                            int count = 0;
                            if (debug) {
                                System.out.println("Here10:" + n);
                            }
                        } else if (check(stringSym)) {
                            if (debug) {
                                System.out.println("Extending routine name " + newname + " with " + t);
                            }
                            name = newname;
                            realFunctionCall = true;  // if it looks like a duck ...
                            TreeNode sn = new TreeNode(OpVal.stringOp);
                            sn.setContext(t);
                            sn.left = new TreeNode(t);
                            verify(stringSym);

                            n.right = new TreeNode(OpVal.parameterOp, sn, null);
                            // System.out.println("Here4:" + tree.val );
                            // traverse(n);
                            n = (TreeNode) n.right;
                            Node node;
                            node = n.left;
                            int count = 0;
                            if (debug) {
                                System.out.println("Here10:" + n);
                            }
                        }
                    }
                }
                newname = newname + t.val;
                partialname = t.val;
                obj = symtab.get(newname);
            }
            // else -- it is not parameter-extendable and not name-extendable
            //         hence the loop will terminate
        }
        // At this point we may have exited the loop because we cannot extend
        // the potential function call either by name or param; now we verify that the 
        // entire call is legitimate
        // Also at this point, n is the tail of the list
        //debug=true; 
        if (debug) {
            System.out.println("NEWNAME is " + newname);
        }
        if (debug) {
            System.out.println("NAME is " + name);
        }
        if (debug) {
            System.out.println("possible function call is " + possibleFunctionCall);
        }
        if (debug) {
            System.out.println("real function call is " + realFunctionCall);
        }
        if (realFunctionCall) {
            if (debug) {
                System.out.println("Real function call: NAME is " + name);
            }
            obj = symtab.get(name);
            if (debug) {
                System.out.println("Here11: obj= " + (String) obj);
            }
            if (obj == null || "partial".equals((String) obj)) {
                printError("Error: " + name + " is not a valid function call");
            }
            switch ((String) obj) {
                case "function":
                    tn = new TreeNode(OpVal.functionCallOp, (TreeNode) tn.right(), null);
                    if (debug) {
                        System.out.println("Here12: tn=");
                    }
                    if (debug) {
                        traverse(tn);
                    }
                    break;
                case "procedure":
                    tn = new TreeNode(OpVal.procedureCallOp, (TreeNode) tn.right(), null);
                    break;
                case "generator":
                    tn = new TreeNode(OpVal.generatorCallOp, (TreeNode) tn.right(), null);
                    break;
            }
        } else { // not a function call ... just a lonely variable
            // awgh or some reserved word!
            if (debug) {
                System.out.println("Lonely variable: " + name + "/" + t.val + "/" + possibleFunctionCall);
            }
            //tn = new TreeNode (tree.val); verify(idSym);
            if (!possibleFunctionCall) {  // The id was not extended, just a lonely name
                if (check(idSym)) {
                    tn = new TreeNode(t.val);
                    verify(idSym);
                } else if (check(trueSym)) {
                    // System.out.println ("found true");
                    tn = new TreeNode(OpVal.trueOp);
                    verify(trueSym);
                } else if (check(falseSym)) {
                    tn = new TreeNode(OpVal.falseOp);
                    verify(falseSym);
                } else {
                    printError("Reserved word '"
                            + t.val
                            + "' used as a label");
                }
                tn.setContext(t);
            } else {
                tn = new TreeNode(name);
            }
            while (check(lbracketSym)) {   // added by morell 6/7/04  array subscr
                verify(lbracketSym);
                tn.right = parseExpression(true);
                tn = new TreeNode(OpVal.subscriptOp, tn, null);
                verify(rbracketSym);
            }

        }
        if (debug) {
            System.out.println("Returning " + tn.info + "from parsePossibleFunctionCall");
        }
        if (debug) {
            traverse2(tn);
        }
        //debug = false;
        return tn;
    }

    TreeNode parsePrimary() {
        /**
         * ***********************
         * Primary : idSym | numberSym | '(' Expression ')' | Primary ':'
         * Primary | List | String | TruthVal | not LogicalExpression Builds:
         * transparent
         *
         * Primary : idSym '(' ExpressionList ')' Builds: OpVal.functionCallOp
         * idSym OpVal.expressionListOp ***********************
         */
        //  debug=true;
        if (debug) {
            System.out.println("Entering parsePrimary");
        }
        TreeNode tn = null;
        boolean negative = false;
        while (check(new int[]{minusSym, plusSym})) {
            if (check(minusSym)) {
                negative = !negative;
            }
            t.get();
        }
        if (check(idSym)) { // its 
            tn = new TreeNode(t.val);
            tn.setContext(t);
            verify(idSym);
            if (!Token.commaBreak && !Token.lineBreak) {
                while (check(lbracketSym)) {   // ljm: 6/7/04
                    verify(lbracketSym);
                    tn.right = parseExpression(true);
                    tn = new TreeNode(OpVal.subscriptOp, tn, null);
                    verify(rbracketSym);
                }
            }
        } else if (check(trueSym)) {
            tn = new TreeNode(OpVal.trueOp);
            tn.setContext(t);
            tn.setCharPos(t.charPos());
            verify(trueSym);
        } else if (check(falseSym)) {
            tn = new TreeNode(OpVal.falseOp);
            tn.setContext(t);
            verify(falseSym);
        } else if (check(numberSym)) {
            tn = new TreeNode(OpVal.numberOp);
            TreeNode temp = new TreeNode(Double.parseDouble(t.val));
            temp.setContext(t);
            tn.setContext(t);
            tn.left = temp;
            verify(numberSym);
        } else if (check(lparenSym)) {
            verify(lparenSym);
            tn = parseExpression(true);
            verify(rparenSym);
        } /*
         else if (check (colonSym)){
         verify (colonSym);
         TreeNode temp = parseExpression(true);  // get the value of the exp
         verify(colonSym);              //gets next token
         tn = parseExpression(true);    //gets next expression
         tn.setLabel(temp.info);         //when recursive call comes back sticks in label to that node, hopefully
         System.out.println ("setting label to " + temp.info.getVal());
         }
         */ else if (check(ltSym)) {
            tn = parseList(gtSym);
        } else if (check(lbracketSym)) {
            tn = parseList(rbracketSym);
        } else if (check(stringSym)) {
            tn = new TreeNode(OpVal.stringOp);
            tn.setContext(t);
            tn.left = new TreeNode(t);
            verify(stringSym);
        } else {
            verify(new int[]{idSym, numberSym, lparenSym, lbracketSym,
                        ltSym, dotSym});
        }
        if (negative) {
            tn = new TreeNode(OpVal.unaryMinusOp, tn, null);
        }
        // If the primary is followed by : then it is a label
        if (check(colonSym)) {
            verify(colonSym);
            tn.right = parsePrimary();
            tn = new TreeNode(OpVal.colonOp, tn, null);
        }
        if (debug) {
            System.out.println("Leaving Primary OpVal.Op with " + tn.info);
            traverse(tn);
        }
        // if we didn'tree get an ID or a '('  or ...
        // verify crashes and says that it needed one of these

        return tn;
    }

    /**
     * *************************
     * List: [ ListSeq ] | < ListSeq >
     *
     * Builds: OpVal.listOp ListSeq *************************
     */
    TreeNode parseList(int matchtype) {
        if (debug) {
            System.out.println("Entering parseList looking for " + tokenStr[matchtype]);
        }
        verify(new int[]{ltSym, lbracketSym});
        TreeNode tn;
        TreeNode treeVal2 = parseListSeq(matchtype);
        if (treeVal2 == null) {
            tn = new TreeNode(OpVal.listOp);
        } else {
            tn = new TreeNode(OpVal.listOp, treeVal2, null);
        }

        verify(new int[]{gtSym, rbracketSym});

        if (debug) {
            System.out.println("Leaving parseList");
            traverse(tn);
        }
        return tn;
    }

    /**
     * *************************
     * ListSeq : // empty | Atom ListSeq | List ListSeq
     *
     * Builds: Atom or List or alias Atom Atom or List or alias Atom Atom or
     * List or alias Atom ... *************************
     */
    TreeNode parseListSeq(int matchtype) {
        if (debug) {
            System.out.println("Entering parseListSeq");
        }
        TreeNode tn = null;
        if (check(matchtype)) {
            return null;
        }
        if (check(ltSym)) {
            tn = parseList(gtSym);
        } else if (check(lbracketSym)) {
            tn = parseList(rbracketSym);
        } else if (check(aliasSym)) {
            verify(aliasSym);
            tn = new TreeNode(OpVal.aliasOp, parsePrimary(), null);
        } else if (check(new int[]{trueSym, falseSym, minusSym, plusSym, numberSym, idSym, stringSym, lparenSym /*, labelSym */})) // Fix
        /*
         if (check(labelSym)){                                                    //added for records chad 
         Token temp;                    //saves label token
         temp = new Token(tree);           // copy the current Token tree
         verify(labelSym);              //gets next token
         tn = parsePrimary();           //parse id
         tn.setLabel(temp.val);         //when recursive call comes back sticks in label to that node, hopefully
         //System.out.println ("Set label:"+ temp.val);
         //System.out.println ("label: "+ tn.getLabel());
         //traverse(tn);
         }
         else
         */ // Don'tree change this to parseExpression or there are big problems !!!
        {
            tn = parsePrimary();
        } //tn = parseExpression(true); // Careful ... should true be here?
        else {
            verify(new int[]{numberSym, idSym, stringSym, lparenSym, labelSym});
        }

        tn.right = parseListSeq(matchtype);
        if (debug) {
            System.out.println("Leaving parseListSeq");
            traverse(tn);
        }
        return tn;
    }

    TreeNode parseGenerateStmt() {
        /**
         * ***********************
         * GenerateStmt : 'generate' GeneratorRef 'from' Source | 'generate'
         * GeneratorRef 'from' Source While | 'generate' GeneratorRef While |
         * 'generate' GeneratorRef Until Builds OpVal.generateOp GeneratorRef
         * Source While (not present ... if not present!)
         * ***********************
         */
        if (debug) {
            System.out.println("Entering parseGenerateStmt");
        }
        verify(generateSym);
        TreeNode tn = parseGeneratorRef();
        if (check(fromSym)) {
            verify(fromSym);
            tn.right = parseSource();
            if (check(whileSym)) {
                tn.right.right = parseWhile();
            } else if (check(untilSym)) {
                tn.right.right = parseUntil();
            }
        } else {
            tn.right = tn.left.right;
        }
        if (check(whileSym)) {
            tn.right = parseWhile();
        } else if (check(untilSym)) {
            tn.right = parseUntil();
        }
        tn = new TreeNode(OpVal.generateStmtOp, tn, null);
        if (debug) {
            System.out.println("Leaving parseGenerateStmt");
            traverse(tn);
        }
        return tn;
    }

    TreeNode parseGeneratorRef() {
        /**
         * *************************
         * GeneratorRef : Id '(' ParameterList ')'
         *
         * Builds : OpVal.generatorRefOp Id ParameterList
         * **************************
         */
        if (debug) {
            System.out.println("Entering parseGeneratorRef");
        }
        TreeNode tn = new TreeNode(t.val);
        tn.setContext(t);
        verify(idSym);
        verify(lparenSym);
        tn.right = parseParameterList();
        verify(rparenSym);
        tn = new TreeNode(OpVal.generatorRefOp, tn, null);
        if (debug) {
            System.out.println("Leaving parseGeneratorRef");
            traverse(tn);
        }
        return tn;
    }

    TreeNode parseParameterList() {
        /**
         * *************************
         * //ParameterList : ID //| ID ',' ParameterList ljm -- 6/19/04 //| ID
         * ParameterList ljm -- 6/19/04 //| null Each ID may be optionally
         * preceded by 'alias' or 'iter'
         *
         * Builds: ID ID ID ... *************************
         */
        if (debug) {
            System.out.println("Entering parseParameterList");
        }
        TreeNode tn = null;
        if (check(idSym)) {
            tn = new TreeNode(t.val);
            tn.setContext(t);
            verify(idSym);
        } else if (check(aliasSym)) {
            verify(aliasSym);
            tn = new TreeNode(OpVal.aliasOp, parsePrimary(), null);
        } else if (check(iteratorSym)) {
            verify(iteratorSym);
            tn = new TreeNode(OpVal.iteratorOp, parsePrimary(), null);
        }
        if (check(new int[]{idSym, aliasSym, iteratorSym})) {
            tn.right = parseParameterList();
        }

        if (debug) {
            System.out.println("Leaving parseParameterList");
            traverse(tn);
        }
        return tn;
    }

    /*
     IdParmList : IdList Parameter
     | IdList Parameter ParmIdList

     Builds: [Id]
     Parameter 
     Id
     ...
     Parameter
     [Id]
     */
    TreeNode parseIdParmList() {
        if (debug) {
            System.out.println("Entering parseIdParmList");
        }
        TreeNode tn = null;
        tn = parseIdSeq();
        if (check(lparenSym)) {
            tn.right = parsePIList();
        }
        if (debug) {
            System.out.println("Leaving parseIdParmList");
            traverse(tn);
        }
        return tn;
    }

    /*
     IPList : IdList Parameter
     | IdLIst
     | IdList Parameter IPList
     */
    TreeNode parseIPList() {
        if (debug) {
            System.out.println("Entering parseIPList");
        }
        TreeNode tn = parseIdSeq();
        if (check(lparenSym)) {
            tn.right = parseParameter();
        }
        if (check(reservedWordorId)) {
            // if (check(idSym)) {
            tn.right.right = parseIPList();
        }
        if (debug) {
            System.out.println("Leaving parseIPList");
            TreeNode d = new TreeNode(OpVal.debugOp, tn, null);
            traverse(d);
        }

        return tn;

    }

    /*
     ParmIdList : Parameter IdList 
     | Parameter IPList ParmIdList
     */
    TreeNode parseParmIdList() {
        // debug=true;
        if (debug) {
            System.out.println("Entering parseParmIdList");
        }
        TreeNode tn = null;
        tn = parseParameter();
        //if (check (idSym))
        if (check(reservedWordorId)) {
            tn.right = parseIPList();
        }
        if (debug) {
            System.out.println("Leaving parseParmIdList");
            TreeNode d = new TreeNode(OpVal.debugOp, tn, null);
            traverse(d);
        }
        tn = new TreeNode(OpVal.headerOp, tn, null);
        return tn;
    }

    /*
     PIList : Parameter IdList
     | Parameter
     | Parameter IdList PIList
     */
    TreeNode parsePIList() {
        if (debug) {
            System.out.println("Entering parsePIList");
        }
        TreeNode tn = parseParameter();
        if (check(reservedWordorId)) //if (check (idSym))
        {
            tn.right = parseIdSeq();
        }
        if (check(reservedWordorId)) {
            //if (check(idSym)) {
            tn.right.right = parsePIList();
        }
        if (debug) {
            System.out.println("Leaving parsePIList");
            traverse(tn);
        }
        return tn;
    }

    /*

     IdList : ID
     | ID IdList
     This one is unusual ... it builds the string consisting of all the
     id's concatenated together, separate by underscore and returns
     that string (rather than a TreeNode)
     */
    String parseIdString() {
        if (debug) {
            System.out.println("Entering parseIdString");
        }
        String ans = t.val.toString();  // Get the val whether it be a reserved word or id

        verify(reservedWordorId);
        //verify(idSym);
        if (check(reservedWordorId)) //if (check(idSym))
        {
            ans = ans + "_" + parseIdString();
        }
        if (debug) {
            System.out.println("Leaving parseIdString with ");
//            System.out.println('"' +ans + '"'); 
        }
        return ans;
    }

    TreeNode parseIdSeq() {
        if (debug) {
            System.out.println("Entering parseIdSeq");
        }
        String idString = parseIdString();
        TreeNode tn = new TreeNode(idString);
        tn.setContext(t);
        if (debug) {
            System.out.println("Leaving parseIdSeq");
            traverse(tn);
        }
        return tn;
    }

    /*
     Parameter : LPAREN ID RPAREN
     */
    TreeNode parseParameter() {
        if (debug) {
            System.out.println("Entering parseParameter");
        }
        verify(lparenSym);
        TreeNode tn = new TreeNode(t);
        //verify(idSym);
        tn = parseParameterList();
        //tn = parseId
        verify(rparenSym);
        tn = new TreeNode(OpVal.parameterOp, tn, null);
        if (debug) {
            System.out.println("Leaving parseParameter");
            traverse(tn);
        }
        return tn;
    }

    /*
     FunctionDef :
     FUNCTION   Block
     */
    boolean testOp(Node tn, GenesisVal v) {
        // System.out.println ("testop: Comparing " + tn.info.val + " with " + v  +" giving " + tn.info.val.eq (v) );
        return tn.info.val.eq(v);
    }

    static boolean testType(Node tn, GenesisVal v) {
        // for tests like
        //          if ( testType( tn, StringVal ) )
        //  or      if ( testType( tn, OpVal ) )
        return tn.info.val.getClass() == v.getClass();
    }

    String mangle(TreeNode tn) {
        //debug=true;
        if (debug) {
            System.out.println("mangling:" + tn);
        }
        Node n = tn;
        if (n != null) {
            n = n.left();
        }
        int i;
        String fname = "";  // was  = "*" ... mangling different for the parser
        boolean done = false;
        while (n != null && !done) {
            i = 0;
            //System.out.println("Processing: " +n);
            if (testType(n, new StringVal(""))) {
                String s = n.info.val.toString();
                // System.out.println("Processing " + fname);
                //fname += s;
                for (i = 0; i < s.length(); i++) {
                    if (s.charAt(i) == '_') {
                        Object obj = symtab.get(fname + s.substring(0, i));
                        if (obj == null) // Not there,  overwrite!
                        {
                            symtab.put(fname + s.substring(0, i), "partial");
                        }
                        // System.out.println( "Found subprog def:" + 
                        //             fname + s.substring(0,i) + " as " + (String) obj);
                    }
                    if (s.charAt(i) == ')') {
                        Object obj = symtab.get(fname + s.substring(0, i + 1));
                        if (obj == null) // Not there,  overwrite!
                        {
                            symtab.put(fname + s.substring(0, i + 1), "partial");
                        }
//                 System.out.println( "Found subprog def:" + 
                        //                             fname  + s.substring(0,i+1)+ " as " + (String) obj);
                    }

                }
                fname += s;
                Object obj = symtab.get(fname);
                if (obj == null) // Not there,  overwrite!
                {
                    symtab.put(fname, "partial");
                }

//          System.out.println( "Found subprog def:" + 
                //                             fname + " as " + (String) obj);
                n = n.right();
            } else if (testType(n, OpVal.parameterOp)
                    && testOp(n, OpVal.parameterOp)) {
                Node p = n.left();
                fname += "()";
                Object obj = symtab.get(fname);
                if (obj == null) // Not there,  overwrite!
                {
                    symtab.put(fname, "partial");
                }
//          System.out.println( "Found: subprog def:" + 
                //fname + " as " + (String) obj);

                n = n.right();
            } else {
                //printError("In mangle: NOT SUPPOSED TO BE HERE:" + fname );
                fname = tn.toString();
                done = true;
            }
        }

        if (debug) {
            System.out.println("Returning" + fname);
        }
        return fname;
    }

    TreeNode parseFunctionDef() {
        if (debug) {
            System.out.println("Entering parseFunctionDef");
        }
        // verify(functionSym); // Moved to the point of call
        TreeNode tn = parseBlock();
        // tn = new TreeNode (OpVal.blockOp,tn,null); // ljm: 07/16/04
        if (debug) {
            System.out.println("Leaving parseFunctionDef");
            traverse(tn);
        }
        return tn;
    }

    TreeNode parseProcedureDef() {
        /**
         * ***********************
         * ProcedureDef : Procedure <Block>
         *
         * Builds: Block
         *
         ************************
         */
        if (debug) {
            System.out.println("Entering parseProcedureDef");
        }
        verify(procedureSym);
        TreeNode tn = parseBlock();
        if (debug) {
            System.out.println("Leaving parseProcedureDef");
            traverse(tn);
        }

        return tn;
    }

    TreeNode parseGeneratorDef() {
        /**
         * ***********************
         * ProcedureDef : Generator <Block>
         *
         * Builds: Block
         *
         ************************
         */
        if (debug) {
            System.out.println("Entering parseProcedureDef");
        }
        verify(generatorSym);
        TreeNode tn = parseBlock();
        if (debug) {
            System.out.println("Leaving parseProcedureDef");
            traverse(tn);
        }

        return tn;
    }

    TreeNode parseTaskDef() {
        /**
         * ***********************
         * ProcedureDef : [Task|Generator] <Block>
         *
         * Builds: Block
         *
         ************************
         */
        //debug=true;
        if (debug) {
            System.out.println("Entering parseTaskDef");
        }
        TreeNode tn = null;
        if (check(taskSym)) {
            verify(taskSym);
            tn = new TreeNode(OpVal.taskOp, null, null);
        } else {
            verify(generatorSym);
            tn = new TreeNode(OpVal.generatorOp, null, null);
        }
        // no else needed since this proc is called from w/in the
        // context of either a generatorSym or taskSym
        tn.left = parseTaskBlock();
//debug=true;
        if (debug) {
            System.out.println("Leaving parseTaskDef");
            traverse(tn);
        }
//debug=false;
//System.exit(1);    
        return tn;
    }

    /**
     * ******************
     * InsertStmt : 'insert' Expression-1 'before' Expression-2 InsertStmt :
     * 'insert' Expression-1 'after' Expression-2
     *
     * NB: Expression-2 must resolve to a list position Expression-3 must
     * resolve to a list * Builds: insertBeforeOp (or insertAfterOp)
     * Expression-1 Expression-2 Expression-3
     *
     *******************
     */
    TreeNode parseInsertStmt() {
        TreeNode e1, tn = null;
        verify(insertSym);
        /*
         if (check (labelSym)) {
         Token temp = new Token(tree);     // copy the current Token tree
         verify(labelSym);              // gets next token
         e1 = parseExpression(true);
         e1.setLabel(temp.val);         // label the exp node
         }
         else 
         */
        e1 = parseExpression(true);

        if (check(beforeSym)) {
            verify(beforeSym);
            tn = new TreeNode(OpVal.insertBeforeOp);
        } else if (check(afterSym)) {
            verify(afterSym);
            tn = new TreeNode(OpVal.insertAfterOp);
        } else {
            verify(new int[]{afterSym, beforeSym});
        }
        TreeNode e2 = parseExpression(true);
        tn.left = e1;
        e1.right = e2; // e2.right = e3;
        return tn;
    }

    /**
     * ******************
     ********************
     */
    TreeNode parseDeleteStmt() {
        verify(deleteSym);
        TreeNode e1 = parseExpression(true);
        //verify(fromSym);
        //TreeNode e2 = parseExpression(true);
        // e1.right = e2;
        TreeNode tn = new TreeNode(OpVal.deleteOp, e1, null);

        return tn;
    }

    /**
     * ******************
     ********************
     */
    TreeNode parseAppendStmt() {
        verify(appendSym);
        TreeNode e1;
        /*
         if (check (labelSym)) {
         Token temp = new Token(tree);     // copy the current Token tree
         verify(labelSym);              // gets next token
         e1 = parseExpression(true);
         e1.setLabel(temp.val);         // label the exp node
         }
         else 
         */
        e1 = parseExpression(true);

        //verify(fromSym);
        //TreeNode e2 = parseExpression(true);
        // e1.right = e2;
        verify(toSym);
        TreeNode e2 = parseExpression(true);
        e1.right = e2;
        TreeNode tn = new TreeNode(OpVal.appendOp, e1, null);

        return tn;
    }

    TreeNode parsePrependStmt() {
        verify(prependSym);
        TreeNode e1;
        /*
         if (check (labelSym)) {
         Token temp = new Token(tree);     // copy the current Token tree
         verify(labelSym);              // gets next token
         e1 = parseExpression(true);
         e1.setLabel(temp.val);         // label the exp node
         }
         else 
         */
        e1 = parseExpression(true);
        //verify(fromSym);
        //TreeNode e2 = parseExpression(true);
        // e1.right = e2;
        verify(toSym);
        TreeNode e2 = parseExpression(true);
        e1.right = e2;
        TreeNode tn = new TreeNode(OpVal.prependOp, e1, null);

        return tn;
    }

    TreeNode parseUnaliasStmt() {
        /**
         * *********************
         * UnaliasStmt : 'unalias' id Builds: OpVal.unaliasStmtOp
         * *********************
         */
        if (debug) {
            System.out.println("Entering parseUnaliasStmt");
        }
        Context context = new Context(t);
        // System.out.println("Setting contxt for stop to (@" + tree.lineNo() + "/" + tree.charPos() +")");
        // System.out.println("Token is "+ tree);
        verify(unaliasSym);
        TreeNode e1 = parseExpression(true);
        TreeNode tn = new TreeNode(OpVal.unaliasStmtOp, e1, null);
        tn.context = context;
        if (debug) {
            System.out.println("Leaving parseUnaliasStmt");
            traverse(tn);
        }
        return tn;
    }
} // class Parser
