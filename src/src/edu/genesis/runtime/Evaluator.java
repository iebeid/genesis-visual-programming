// Evaluator.java
// Wes Potts/Larry Morell/Surya Muntha
// 6/9/2004 
// // This file implements the evaluator portion of the genesis interpreter. 

/*
 Modification History
 The modification history below is not completely accurate, but close.
 Entries here are for new constructs in the language that require new interpretation
 See the Modification history for other files for details that relate to those files

 6/20/04 -- ljm: Fixed aliases so they worked both in and out of list
 7/09/04 -- wdp: Started on function/procedures
 7/12/04 -- wdp: More function/proc work
 7/15/04 -- ljm: Completed evalFunctionCall for new syntax tree
 7/31/04 -- ljm: Added unary minus
 8/02/04 -- ljm: Added interpretation for function calls with alias parameters
 8/03/04 -- ljm: Added error handling for arithmetic operations
 8/29/04 -- ljm: Added/repaired routines inserting, appending, deleting from lists
 9/13/04 -- ljm: Added interpretation for `true' in a condition
 9/25/04 -- ljm: Added processing for Generate ... from m to n
 9/28/04 -- ljm: Added empty parameter lists e.g. print f(); PrintMenu()
 10/1/04 -- ljm: Added echo for printing w/o newline
 10/2/04 -- ljm: Added capability to define built-in functions and procedures
 10/2/04 -- ljm: Fixed argument list (5, -4);  was interpreted as (5-4)
 10/5/04 -- db:  Fixed generate from an empty list
 10/16/04 -- ljm: Added standalone Generate, iterator, and changed iterators to persist
 1/15/05  -- sm: Formatting numbers to 2 decimal places; type functions; include files
 1/20/05  -- ljm: Added Set precision(n) to allow user to set precision of numbers
 2/18/05  -- ljm: Error message for calling a function in where a procedure was expected
 2/18/05  -- ljm: Error message for comparing anything other than strings and numbers
 2/18/05  -- ljm: Overloaded "+" to work for all combos of lists, strings, and numbers
 3/2/05   -- sm/ljm: Improved error processing for when a procedure is called where a function is expected
 3/15/05  -- ljm: Added processing for TruthVal's (booleans)
 3/22/05  -- ljm: Fixed misc minor irritations: evalRef now returns an empty GenesisList if the id is not found
 5/20/05  -- ljm: Eliminated parseGenerate
 8/21/05 -- ljm: Modified 'Print" to insert spaces between items printed per Andy Bostian
 8/27/05 -- ljm; added quotient_of(1)_divided_by(1), per A.B.
 10/19/05 -- ljm: corrected is(1)_a_list? and is(1)_empty?
 12/01/05 -- ljm: added predefined variable "infinity", an infinite list of 1's; modified call-by-alias
 semantics to define the actual, even if it hasn't been defined before
 Fixed bug in "is (x) empty?" -- always returned false
 5/18/06 -- ljm: Added length_of(1) function to return 0 or length of list
 Modified infinity to be an infinite list of 1's
 5/25/06 -- ch/ljm: Added records that will eventually become hashes; 
 7/27/06 -- lm: Labeled expression in Append lab:val onto L; 
 Insert lab:val before L[i]; Insert lab:val after L[i]
 Let iterator(n) name iterator(m) now works, allowing for 
 a proc for swapping iterators
 7/29/06  -- ljm: Added tracing for -t option to prettyprint the stmt before
 it is executed
 8/12/06 -- ljm: Added minimal user control over tracing 
 11/2/06 -- ljm: Added stop at every line capability
 1/1/07  -- ljm: Made 'and' and 'or' short circuit evaluations
 2/01/07 -- ljm: Added user-defined iterators, e.g. For every other (n) in (L) [+] ...
 2/09/07 -- ljm: Oops ... no error message for Move(n) to (7) where n is undefined
 or for Let L <alias a>, where a is undefined
 3/01/07 -- ljm: added Lisp-like functions: tail(List), sublist(List,n);
 fixed bug in is()empty?
 3/10/07 -- ljm: added next(n), atfirst(n), atlast(n) 
 3/25/07 -- ljm: added 'iter' as a parameter passing mechanism: can now swap iterators!
 3/25/07 -- ljm: can now iterate through a subscripted list
 6/25/07 -- ljm: Added prev, value to provide transparency for a computed iterator
 9/25/07 -- ljm: modified sqrt to return a double
 10/4/07 -- ljm: enhanced debug (with 'd label') to allow you display values at a breakpoint
 11/22/07 -- ljm: Fixed problem with insert after ... to copy the StickyNote being inserted
 1/20/08 -- ljm: Overhaul error messaging processing: created SourcePgm to handle line numbers by file
 2/14/08  -- ljm: Modified Generate to iterate across a computed string; 
 Allow immutable subscripting of a string
 2/15/08  -- ljm: Added head to compute the first element of alias
 6/10/08 -- ljm: Revised insert before to insert a copy of a stickynote rather than the original
 10/1/08 -- ljm: Added 'prepend' as a statement
 10/11/08 -- ljm: Modified trace to print context of a statement using 'L/l/.' 'p' for a var
 Added 'set trace on()  and 'set trace off()'
 4/10/10  -- ljm: split (L) at (m) into (L1,L2) did not create L1 as an empty list when len(L) = 1
 */
package edu.genesis.runtime;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Evaluator{

    GenesisIO out;
    Scope scope;
    static int tempNo = 0;
    public static boolean debug = false;
    public static boolean trace = false;
    public static boolean autotrace = false;
    public static int autoindent = 0;
    private static TreeNode root;  // root of the tree
    public static TreeNode currentStmt;
    PrintStream o;

    private void init() {
        /* Add built-in function calls to the scope */
        scope.setName("*integer(1)", new TreeNode("function"));
        scope.setName("*is(1)a_number?", new TreeNode("function"));
        scope.setName("*is(1)a_truth_value?", new TreeNode("function"));
        scope.setName("*is(1)a_string?", new TreeNode("function"));
        scope.setName("*is(1)a_list?", new TreeNode("function"));
        scope.setName("*is(1)an_iterator?", new TreeNode("function"));
        scope.setName("*is(1)defined?", new TreeNode("function"));
        scope.setName("*is(1)empty?", new TreeNode("function"));
        // scope.setName("*concat(*)", new TreeNode("function") );
        scope.setName("*fractional(1)", new TreeNode("function"));
        scope.setName("*on(1)", new TreeNode("function"));
        scope.setName("*off(1)", new TreeNode("function"));
        scope.setName("*atfirst(1)", new TreeNode("function"));
        scope.setName("*atlast(1)", new TreeNode("function"));
        scope.setName("*remainder(1)divided_by(1)", new TreeNode("function"));
        scope.setName("*quotient(1)divided_by(1)", new TreeNode("function"));
        scope.setName("*position(1)", new TreeNode("function"));
        scope.setName("*move(1)", new TreeNode("procedure"));
        scope.setName("*move(1)to(1)", new TreeNode("procedure"));
        scope.setName("*move(1)backward", new TreeNode("procedure"));
        scope.setName("*move(1)forward", new TreeNode("procedure"));
        scope.setName("*reset(1)", new TreeNode("procedure"));
        scope.setName("*square_root(1)", new TreeNode("function"));
        scope.setName("*explode(1)", new TreeNode("function"));
        scope.setName("*iterator(1)", new TreeNode("function"));
        scope.setName("*iterator(2)", new TreeNode("function"));
        scope.setName("*type(1)", new TreeNode("function"));
        scope.setName("*set_precision(1)", new TreeNode("procedure"));
        scope.setName("*random(0)", new TreeNode("function"));
        scope.setName("*length(1)", new TreeNode("function"));
        scope.setName("*sublist(2)", new TreeNode("function"));
        scope.setName("*tail(1)", new TreeNode("function"));
        scope.setName("*head(1)", new TreeNode("function"));
        scope.setName("*split(1)at(1)into(2)", new TreeNode("procedure"));
        scope.setName("*next(1)", new TreeNode("function"));
        scope.setName("*prev(1)", new TreeNode("function"));
        scope.setName("*value(1)", new TreeNode("function"));
        scope.setName("*succ(1)", new TreeNode("function"));
        scope.setName("*set_trace_on(0)", new TreeNode("procedure"));
        scope.setName("*set_trace_off(0)", new TreeNode("procedure"));
        MetaNode inf = new MetaNode();
        inf.left = null;
        inf.right = new Node(1, inf, null);
        inf.right.right = inf.right;  // create circular list
        scope.setName("infinity", inf);
        try {
            o = new PrintStream(new FileOutputStream("A.txt",true));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Evaluator.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.setOut(o);
        System.setErr(o);
    }

    public Evaluator() {
        // out = new GenesisIO();
        scope = new Scope();
        init();
        
    }
    // Instances of GenesisVal's to use in testType and testOp
    private final StringVal STRING_VAL = new StringVal("");
    private final DoubleVal DOUBLE_VAL = new DoubleVal(1.0);
    private final IntVal INT_VAL = new IntVal(1);
    private final OpVal OP_VAL = new OpVal(0);
    private final Node NODE_VAL = new Node();
    // private final Iterator ITERATOR_VAL = new Iterator();

    static void print(String s) {
        System.out.println(s);
    }

    ;
  boolean testOp(Node tn, GenesisVal v) {
        return tn.info.val.eq(v);
    }

    static boolean testType(Node tn, GenesisVal v) {
        // for tests like
        //                 if ( testType( tn, StringVal ) )
        //         or        if ( testType( tn, OpVal ) )
        return tn.info.val.getClass() == v.getClass();
    }

    boolean isIterator(String s) {
        StickyNote sn = scope.find(s);
        if (sn == null) // if it is not in the scope
        {
            return false;
        } else {           //  see if it's value is an Iterator
            return (sn.val instanceof GenesisList);
        }
    }

    public static void printError(String msg, TreeNode tn) {
        System.out.println("");
        if (tn != null) {
            System.out.println("> In file '"
                    + tn.fileName()
                    + "' error occurred on or about line "
                    + tn.lineNo()
                    + ", column "
                    + tn.charPos()
                    + ".");
        } else {
            System.out.println("> Error occurred.");
        }
        System.out.println("> " + msg);
        while (tn != null && tn.lineNo() == 0) {
            tn = (TreeNode) tn.left;
        }
        System.out.println("> Stopping algorithm.");
        System.out.println("");
        System.out.println("Done interpreting program\n--------------\n");
        System.out.println("\"" + Quote.getMessage() + "\"");
        //System.exit(1);
    }

    public static void printError(String msg, Node n) {
        TreeNode tn = (TreeNode) n;
        printError(msg, tn);
    }

    public static void printError(String msg) {
        TreeNode tn = (TreeNode) currentStmt;
        printError(msg, tn);
    }
    // getLabel -- returns the label associated with tn

    String getLabel(Node tn) {
        String ans = "Fix me";
        return ans;

    }
    // ---------------- begin eval routines ------------------ //

    public void evalProgram(Node tn) {
        root = (TreeNode) tn;
        evalStmtList(tn);
    }

    StickyNote evalStmtList(Node tn) {
        StickyNote result = null;
        tn = tn.left();
        while (tn != null) {
            if (testType(tn, OpVal.returnStmtOp)
                    && testOp(tn, OpVal.returnStmtOp)) {
                return evalStmt(tn);
            } else if (testType(tn, OpVal.stopStmtOp)
                    && testOp(tn, OpVal.stopStmtOp)) {  // special case 
                if (trace) {
                    System.out.println("==> Executing:");
                    System.out.println(tn.toString()+"\n");
                }
                evalStop(tn);
            } else {
                result = evalStmt(tn);
            }
            tn = tn.right();
        }
        return result;
    }
    public static Vector<Integer> stopAt = new Vector<>(20);

    TreeNode findContext(TreeNode context, TreeNode tn, Node key) {
        TreeNode result;
        if (tn == null) {
            result = null;
        } else if (tn == key) {
            result = context;
        } else {
            if (testOp(tn, OpVal.procedureDefOp)
                    || testOp(tn, OpVal.functionDefOp)
                    || testOp(tn, OpVal.generatorDefOp)) {
                context = tn;
            }
            {
                result = findContext(context, (TreeNode) tn.left(), key);
                if (result == null) {
                    result = findContext(context, (TreeNode) tn.right(), key);
                }
            }
        }
        return result;
    }

    TreeNode findContext(Node tn) { // Search the tree for the containing procedure
        if (tn == null) {
            return (TreeNode) tn;
        }
        TreeNode context = findContext(root, root, tn);
        return context;
    }

    StickyNote evalStmt(Node tn) {
        if (autotrace && !testOp(tn, OpVal.stmtListOp)) {
            System.out.println("==> ");
            System.out.println(tn.toString());
        } else if (trace && !testOp(tn, OpVal.stmtListOp)) {
            // Check to see if Vector contains the current line number
            if (stopAt.contains(new Integer(((TreeNode) tn).prettyNo()))
                    || stopAt.contains(0)) {
                System.out.println("==> "); // Executing instruction:"+ ((TreeNode) tn).prettyNo());
                System.out.println(tn.toString());  // will invoke toString, a pretty printer
                int x;
                boolean doAgain = true;
                Scanner scanner = new Scanner(System.in);
                while (doAgain) {
                    System.out.println("> ");
                    try {
                        x = System.in.read();
                        if (x >= 0) {
                            char ch = (char) x;
                            if (ch == 'n') {
                                doAgain = false;
                            } else if (ch == 'L') {
                                System.out.println(root.toXML());  // will invoke toString, a pretty printer
                                
                            } else if (ch == 'l') {
                                TreeNode context = findContext(tn);
                                System.out.println(context.toString());  // will invoke toString, a pretty printer
                            } else if (ch == '.') {
                                System.out.println(tn.toString());  // will invoke toString, a pretty printer
                            } else if (ch == 'q') {
                                System.out.println("Stopping ...");  // will invoke toString, a pretty printer
                                //System.exit(0);
                            } else if (ch == '?') {
                                System.out.println("\n>[n,h,.,l,L,p,q,r,s?]: ");
                            } else if (ch == 'p') {
                                String id = scanner.next();

                                StickyNote n = scope.alias(id);  // SN assoc with id

                                if (n == null) {
                                    //   printError("\tIdentifier '" + tn.info.toString() + "' is not associated with a value", tn );
                                    System.out.println(id + ": " + "no value associated with '" + id + "'");
                                } else {
                                    System.out.println(id + ": " + n);
                                }
                            } else if (ch == 's') {
                                int len = stopAt.size();
                                System.out.println("\nBreakpoint(s): ");
                                for (int i = 0; i < len; i++) {
                                    System.out.println("" + stopAt.get(i) + ' ');
                                }
                                System.out.println("\n\nSet which breakpoint? ");
                                Integer bp = scanner.nextInt();
                                int pos = stopAt.indexOf(bp);
                                if (pos == -1) {
                                    stopAt.add(bp);
                                }
                                // System.out.println ("[n,s,r,h]: ");
                            } else if (ch == 'r') {
                                int len = stopAt.size();
                                System.out.println("\nBreakpoint(s): ");
                                for (int i = 0; i < len; i++) {
                                    System.out.println("" + stopAt.get(i) + ' ');
                                }
                                System.out.println("\n\nDelete which breakpoint? ");
                                Integer del = scanner.nextInt();
                                int pos = stopAt.indexOf(del);
                                if (pos >= 0) {
                                    stopAt.removeElementAt(pos);
                                }
                                //  System.out.println ("[n,s,r,h]: ");
                            } else if (ch == 'h') {
                                System.out.println("Input         Meaning");
                                System.out.println("-----         -------");
                                System.out.println("  p label  Display the value associated with 'label'");
                                System.out.println("  n        Advance to next breakpoint");
                                System.out.println("  h        Print this help");
                                System.out.println("  .        List the current instruction");
                                System.out.println("  l        List the containing procedure or function");
                                System.out.println("  L        List the whole algorithm");
                                System.out.println("  q        Quit");
                                System.out.println("  r        Remove breakpoint(s)");
                                System.out.println("  s        Set breakpoint(s)");
                                System.out.println("  ?        Print short help");
                                //   System.out.println ("\n[n,s,r,h]: ");
                            }
                            scanner.nextLine();
                        }
                    } catch (IOException io) {
                        System.out.println("IOerror:" + io);
                    }
                }
            }
        }
        currentStmt = (TreeNode) tn;

        //System.out.println("Executing:" +tn);
        if (testOp(tn, OpVal.idNameOp)) {
            return evalIdName(tn);
        } else if (testOp(tn, OpVal.idAliasOp)) {
            return evalIdAlias(tn);
        } else if (testOp(tn, OpVal.printOp)) {
            return evalPrint(tn);
        } else if (testOp(tn, OpVal.selectOp)) {
            return evalSelect(tn);
        } else if (testOp(tn, OpVal.stmtListOp)) {
            return evalStmtList(tn);
        } else if (testOp(tn, OpVal.functionDefOp)) {
            return evalFunctionDef(tn);
        } else if (testOp(tn, OpVal.procedureDefOp)) {
            return evalFunctionDef(tn);
        } else if (testOp(tn, OpVal.generatorDefOp)) {
            return evalFunctionDef(tn);
        } else if (testOp(tn, OpVal.returnStmtOp)) {
            return evalExpression(tn.left());
        } else if (testOp(tn, OpVal.procedureCallOp)) {
            evalFunctionCall(tn);
        } else if (testOp(tn, OpVal.whileOp)) {
            evalWhileStmt(tn);
        } else if (testOp(tn, OpVal.pipeOp)) {
            evalPipe(tn);
        } else if (testOp(tn, OpVal.deleteOp)) {
            evalDelete(tn);
        } else if (testOp(tn, OpVal.appendOp)) {
            evalAppend(tn);
        } else if (testOp(tn, OpVal.prependOp)) {
            evalPrepend(tn);
        } else if (testOp(tn, OpVal.insertAfterOp)) {
            evalInsertAfter(tn);
        } else if (testOp(tn, OpVal.insertBeforeOp)) {
            evalInsertBefore(tn);
        } else if (testOp(tn, OpVal.stopStmtOp)) {
            evalStop(tn);
        } else if (testOp(tn, OpVal.echoStmtOp)) {
            evalEcho(tn, false);
        } //else if ( testOp( tn, OpVal.generateStmtOp ) ) evalGenerate( tn ); // standalone
        else if (testOp(tn, OpVal.unaliasStmtOp)) {
            evalUnalias(tn); // standalone
        } else if (testOp(tn, OpVal.functionCallOp)) {
            String fname = buildMangledFunctionCallName(tn);
            printError("Calling function " + fname + " as a procedure", tn);
        } else if (testOp(tn, OpVal.generatorCallOp)) {
            evalFunctionCall(tn);
        } else if (testOp(tn, OpVal.nullStmtOp)) /* Do nothing */; else {
            printError("In evalStmt: NOT SUPPOSED TO BE HERE! " + tn.getVal(), tn);
            //System.exit(1);
        }
        return null;
    }

    void letIdNameSN(String id, StickyNote sn) {
        if (sn.val instanceof GenesisList) { // sn is an iterator, copy it
            //System.out.println ("Assigning an iterator");
            GenesisList t = new GenesisList((GenesisList) sn.val);
            sn = new StickyNote(t);
        }
        if (isIterator(id)) {
            //System.out.println("letIdNameSN: Let " + id + " name " + sn);
            //System.out.println("let Iterator Name:" +id + " " + sn.val);
            sn = new StickyNote(sn);
            //                           SN    GV=Node   
            GenesisList gl = (GenesisList) (scope.find(id).getVal());
            if (gl.empty() || gl.off()) { // adding the first node to the list
                gl.insert(sn);
                //System.out.println ("Off the list");
            } else {  // on the list 
                StickyNote note = gl.get();
                note.val = sn.getVal();
                // scope.name(n.info,evalExpression( tn.left().right() ).getVal()); 
            }
        } else {
            // debug= true;
            if (debug) {
                System.out.println("Assigning " + id + "-to-" + sn + " of type " + sn.val.getClass());
            }
            scope.setName(id, sn);
            // debug = false;

        }
        //System.out.println ("Retrieving sn associated with " + id + "=" + scope.search(id));
    }

    StickyNote evalIdName(Node tn) {
        StickyNote result = evalExpression(tn.left().right());
        evalIdNameValue(tn, result);
        return result;
    }

    StickyNote evalIdNameValue(Node tn, StickyNote result) {
        //    System.out.println("Evalling 0 "); Parser.prettyPrint((TreeNode)tn);
        // System.out.println("Left: " +tn.left); 
        if (autotrace) {
            System.out.println(tn.left() + ": " + result);
        }
        if (testType(tn.left(), STRING_VAL)) {
            // System.out.println("Evalling 1 ");
            StickyNote sn = tn.left().info;
            
            String id = sn.toString();
            //System.out.println("Associating " + id + " with result = " + result);
            //System.out.println("Associating " + id + " with result.addr = " + result.addr());
            //System.out.println("Associating " + id + " with result.val.addr = " + result.val.addr());
            letIdNameSN(id, result);
            // System.out.println("Got back id/result " + id + "/" + result);
            
            result = scope.search(id);
            
          
            // System.out.println ("Returning!!! " + result );
        } // This tests whether tn.left() is an OpVal.
        // OpVal.subscriptOp happens to be an OpVal and it's handy.
        else if (testOp(tn.left(), OpVal.subscriptOp)) {
            // System.out.println("Evalling 2 "); Parser.prettyPrint((TreeNode)tn.left());
            if (testOp(tn.left(), OpVal.subscriptOp)) {
                GenesisList l = evalSubscript(tn.left());
                scope.name(l, result);
            }
        } else if (testOp(tn.left(), OpVal.functionCallOp)) {
            // System.out.println("Evalling 3 ");
            // Evaluate the lhs to get its sn, assign to that sn
            // System.out.println ("Got here" );
            StickyNote sn = evalExp(tn.left());
            // System.out.println ("Got " + sn);
            sn.val = result.getVal();
            // System.out.println ("Computed " + sn.val); 
        }
        return result;
    }

    void letIdAliasSN(String id, StickyNote sn) {
        if (isIterator(id)) {
            //System.out.println("terator Alias:" +id);
            //                           SN    GV=Node   
            //Node n = (Node) (scope.search(id).getVal());
            GenesisList gl = (GenesisList) (scope.find(id).val);
            //System.out.println("gl = " + gl);
            if (gl.empty() || gl.off()) { // adding the first node to the list
                gl.insert(sn);
            } else {  // on the list 
                //n.info = sn; 
                gl.change(sn);
            }
        } else {
            scope.alias(id, sn);
        }
    }

    StickyNote evalIdAlias(Node tn) {
        StickyNote result = evalExpression(tn.left().right());
        if (testType(tn.left(), new StringVal(""))) {
            String id = tn.left().info.toString();
            //System.out.println("Got here" + id);
            letIdAliasSN(id, result);
        } // This tests whether tn.left() is an OpVal.
        // OpVal.subscriptOp happens to be an OpVal and it's handy.
        else if (testType(tn.left(), OpVal.subscriptOp)) {
            if (testOp(tn.left(), OpVal.subscriptOp)) {
                GenesisList l = evalSubscript(tn.left());
                scope.alias(l, result);
            }
        }
        return result;
    }

    GenesisList evalSubscript(Node tn) {
        // first, get the list
        GenesisList l = null;
        //System.out.println ("evalSubscript:" + tn);
        //  System.out.println("Wrapping?" );
        if (testType(tn.left(), new StringVal(""))) {
            String id = tn.left().info.toString();
            l = scope.wrapList(id);
        } else if (testType(tn.left(), OpVal.subscriptOp) || testType(tn.left(), OpVal.dotOp)) {
            if (testOp(tn.left(), OpVal.subscriptOp) || testType(tn.left(), OpVal.dotOp)) //added dotOp chad
            {
                //System.out.println ("getting sublist for subscript");
                l = evalSubscript(tn.left()).getSubList();
            } else {
                printError("In evalSubscript( Node ):\n"
                        + "\ttn.left() is not OpVal.subscriptOp", tn);
            }
        } else {
            printError("In evalSubscript( Node ):\n"
                    + "\ttn.left() is neither a StringVal or an OpVal", tn);
        }
        // now, go to the right position
        //    1. get the index
        StickyNote n = evalExpression(tn.left().right());
        //System.out.println ("evaling" + tn.left().right() + ":" + n);
        DoubleVal d = null;
        int i = 0;
        if (n.val instanceof DoubleVal) {
            d = (DoubleVal) (n.val);
            //   2. check to make sure it's in range
            i = d.toInt();
            //System.out.println("Subscript "+ i );
            if (i < 1 || i > l.size()) {
                printError("Subscript " + i + " out of range for list "
                        + l, tn);
            }

        } // else if (n.val instanceof StringVal ) {
        // Hey this means I cannot use a number as a label!
        else { // n is not a number, search in the labels

            // System.out.println ("Searching " + n.val); 
            //i = l.search(("" +n.val).toLowerCase());       
            i = l.searchLabel(n);
            //System.out.println ("Found at pos  " + i); 
            if (i < 1 || i > l.size()) {
                printError("'" + n.val + "' is not a valid field name for"
                        + l, tn);
            }
        }
        /*
         else
         {
         printError( "Subscript " + n+  "  is not a number or a string",tn );
         }
         */
        l.moveTo(i);

        return l;
    }

    String addr(Object t) {
        return t.toString();
    }

    GenesisList evalRef(Node tn) {
        GenesisList result = null;
        if (debug) {
            System.out.println("Evaluating reference expression: ");
            Parser.prettyPrint((TreeNode) tn);
        }
        // if the expression is an identifier, return an alias to it
        if (tn.info.val instanceof StringVal) {
            // System.out.println ("evaling label: " + tn.info.val.toString());
            String id = tn.info.toString();
            // System.out.println ("Searching for "+ id);
            StickyNote n = scope.searchRef(id);  // SN assoc with id  ... 
            // Better be a GenesisList or null (empty list)
            if (n == null) //  either a null iterator or a name that was never defined
            {
                //printError("Label '" + id + "' references no value", tn);
                result = new GenesisList();  // Very speculative
            } else {
                if (debug) {
                    System.out.println("Value/class of expression is " + n.val + "" + n.val.getClass());
                }
                if (!(n.val instanceof GenesisList)
                        && !(n.val instanceof Node)
                        && (n.val != null)) {
                    printError("Label '" + id + " ' does not reference a value in a list", tn);
                }
                if (n.val instanceof GenesisList) {
                    result = (GenesisList) n.val;
                    if (debug) {
                        System.out.println("n.val is a GenesisList" + n.val);
                    }
                } else if (n.val instanceof Node) {
                    //System.out.println ("n.val is a Node" + n.val);
                    // System.out.println ("Got here");
                    result = new GenesisList((Node) n.val);
                    // System.out.println ("Moving to the end");
                    while (result.on()) {
                        result.move();
                    } // to indicate it's a top-level list
                } else // n.val must be null
                {
                    result = new GenesisList(n); // The n is remembered as the parent in the list
                }
            }
            // the GenesisList
        } // StringVal
        else if (testOp(tn, OpVal.subscriptOp)) { // e.g. L[3]
            result = (GenesisList) evalSubscript(tn); // ptr to current node in list
        } else {
            printError("Algorithm is attempting to interpret "
                    + evalExpression(tn)
                    + " as a reference to a list ", tn);
            //printError( "evalRef... trying to interpret operator: " + tn.getVal(),tn);
            //System.exit(1);
            return null;
        }
        // debug = true;
        if (debug) {
            System.out.println("evalRef returns " + result + "/" + result.getClass());
        }
        return result;
    }

    StickyNote evalExp(Node tn) // Same as evalExpression, but doesn't evaluate
    {
        // debug = true;
        StickyNote result;
        if (debug) {
            System.out.println("Evaluating expression: ");
            Parser.prettyPrint((TreeNode) tn);
        }
        // if the expression is an identifier, return an alias to it
        if (tn.info.val instanceof StringVal) {
            // System.out.println ("evaling label: " + tn.info.val.toString());
            String id = tn.info.toString();
            StickyNote n = scope.find(id);  // SN assoc with id
            // System.out.println ("result is: " + n.val.getClass() + " at " + (Object)n);

            if (debug) {
                System.out.println("evalExp returning:" + n.toString());
            }
            // return (n);
            result = n;
            //return new StickyNote(n);
        } // StringVal
        else if (testOp(tn, OpVal.procedureCallOp)) {
            result = null;
            printError("Procedure '" + buildMangledFunctionCallName(tn)
                    + "' cannot be used in an expression.", tn);
        } else if (testOp(tn, OpVal.stringOp) || testOp(tn, OpVal.numberOp)) {
            result = tn.left().info;
        } else if (testOp(tn, OpVal.functionCallOp)) {
            result = evalFunctionCall(tn);
            //System.out.println("got back1");
        } else if (testOp(tn, OpVal.addOp)) {
            result = evalAddOp(tn);
        } else if (testOp(tn, OpVal.subtractOp)) {
            result = evalSubtractOp(tn);
        } else if (testOp(tn, OpVal.multiplyOp)) {
            result = evalMultiplyOp(tn);
        } else if (testOp(tn, OpVal.unaryMinusOp)) {
            result = evalUnaryMinusOp(tn);
        } else if (testOp(tn, OpVal.divideOp)) {
            result = evalDivideOp(tn);
        } else if (testOp(tn, OpVal.modOp)) {
            result = evalModOp(tn);  // chad added for %
        } else if (testOp(tn, OpVal.ltOp)
                || testOp(tn, OpVal.leOp)
                || testOp(tn, OpVal.gtOp)
                || testOp(tn, OpVal.geOp)
                || testOp(tn, OpVal.eqOp)
                || testOp(tn, OpVal.neOp)) {
            return new StickyNote(evalCondition(tn));

        } else if (testOp(tn, OpVal.listOp)) {
            result = evalListOp(tn);
        } else if (testOp(tn, OpVal.aliasOp)) {
            String id = tn.left.info.toString();
            if (isIterator(id)) {
                //System.out.println("Processing aliased iterator");
                // Node n = (Node) (scope.search(id).getVal());
                // result =  n.getVal();
                GenesisList gl = (GenesisList) scope.find(id).val;
                //System.out.println("gl = " + gl);
                //System.out.println("gl.get.addr = " + gl.get().val.addr());
                result = gl.get();
            } else {
                result = scope.alias(id);
            }
        } else if (testOp(tn, OpVal.subscriptOp)) {
            result = evalSubscript(tn).get();
        } else if (testOp(tn, OpVal.trueOp) || testOp(tn, OpVal.otherwiseOp)) {
            // System.out.println( "Evaling true"); 
            result = new StickyNote(true);
        } else if (testOp(tn, OpVal.falseOp)) {
            result = new StickyNote(false);
        } else if (testOp(tn, OpVal.notOp)
                || testOp(tn, OpVal.orOp)
                || testOp(tn, OpVal.andOp)) {
            result = new StickyNote(evalCondition(tn));
        } else {
            printError("evalExpression... trying to interpret operator: " + tn.getVal(), tn);
            return null;
        }
        //debug=true;
        if (debug) {
            System.out.println("evalExpression returns " + result);
        }
        return result;
    } // evalExp

    StickyNote evalExpression(Node tn) {
        //debug = true;
        StickyNote result;
        if (debug) {
            System.out.println("Evaluating expression: ");
            Parser.prettyPrint((TreeNode) tn);
        }
        //debug = false;
        // if the expression is an identifier, return an alias to it
        if (tn.info.val instanceof StringVal) {
            // System.out.println ("evaling label: " + tn.info.val.toString());
            String id = tn.info.toString();
            StickyNote n = scope.alias(id);  // SN assoc with id
            // System.out.println ("result is: " + n.val.getClass() + " at " + (Object)n);

            if (n == null) {
                printError("\tIdentifier '" + tn.info.toString() + "' is not associated with a value", tn);
            }
            if (n.val instanceof GenesisList) {
                // System.out.println("Iterator found for list:"+n.val);  
                n = ((GenesisList) n.val).get();
                // System.out.println("Iterator found: "+n);  
            }
            //out.println("evalExpression returning:" + n.toString()); 
            // return (n);
            result = n;
            //return new StickyNote(n);
        } // StringVal
        else if (testOp(tn, OpVal.procedureCallOp)) {
            result = null;
            printError("Procedure '" + buildMangledFunctionCallName(tn)
                    + "' cannot be used in an expression.", tn);
        } else if (testOp(tn, OpVal.stringOp) || testOp(tn, OpVal.numberOp)) {
            result = tn.left().info;
        } else if (testOp(tn, OpVal.functionCallOp)) {
            // debug = true;
            result = evalFunctionCall(tn);
            //System.out.println("Got back2");
            //  debug = false;
        } else if (testOp(tn, OpVal.addOp)) {
            result = evalAddOp(tn);
        } else if (testOp(tn, OpVal.subtractOp)) {
            result = evalSubtractOp(tn);
        } else if (testOp(tn, OpVal.multiplyOp)) {
            result = evalMultiplyOp(tn);
        } else if (testOp(tn, OpVal.unaryMinusOp)) {
            result = evalUnaryMinusOp(tn);
        } else if (testOp(tn, OpVal.divideOp)) {
            result = evalDivideOp(tn);
        } else if (testOp(tn, OpVal.modOp)) {
            result = evalModOp(tn);  // chad added for %
        } else if (testOp(tn, OpVal.ltOp)
                || testOp(tn, OpVal.leOp)
                || testOp(tn, OpVal.gtOp)
                || testOp(tn, OpVal.geOp)
                || testOp(tn, OpVal.eqOp)
                || testOp(tn, OpVal.neOp)) {
            return new StickyNote(evalCondition(tn));

        } else if (testOp(tn, OpVal.colonOp)) {
            result = evalColonOp(tn);
        } else if (testOp(tn, OpVal.listOp)) {
            result = evalListOp(tn);
        } else if (testOp(tn, OpVal.aliasOp)) {
            String id = tn.left.info.toString();
            //     System.out.println("Processing aliased id");
            if (isIterator(id)) {
                //System.out.println("Processing aliased iterator");
                // Node n = (Node) (scope.search(id).getVal());
                // result =  n.getVal();
                GenesisList gl = (GenesisList) scope.find(id).val;
                //System.out.println("gl = " + gl);
                //System.out.println("gl.get.addr = " + gl.get().val.addr());
                result = gl.get();
            } else {
                result = scope.alias(id);
                if (result == null) {
                    printError("Attempt to establish a list alias for '" + id
                            + "', which is not associated with a value");
                }
            }
        } else if (testOp(tn, OpVal.subscriptOp)) {
            result = evalSubscript(tn).get();
        } else if (testOp(tn, OpVal.trueOp) || testOp(tn, OpVal.otherwiseOp)) {
            // System.out.println( "Evaling true"); 
            result = new StickyNote(true);
        } else if (testOp(tn, OpVal.falseOp)) {
            result = new StickyNote(false);
        } else if (testOp(tn, OpVal.notOp)
                || testOp(tn, OpVal.orOp)
                || testOp(tn, OpVal.andOp)) {
            result = new StickyNote(evalCondition(tn));
        } else {
            printError("evalExpression... trying to interpret operator: " + tn.getVal(), tn);
            return null;
        }
        // debug=true;
        if (debug) {
            System.out.println("Expression value:" + result + "type: " + result.val.getClass());
               
        }
        //System.out.println("Returning from Expression ");
        return result;
    } // evalExpression


    /*
     .__________________________________.
     |   +   || Number | String  | List |
     |_______||________|_________|______|
     |-------||--------|---------|------|
     | Number|| Number | String  | List |
     |-------||--------|---------|------|
     | String|| String | String  | List |
     |-------||--------|---------|------|
     | List  || List   | List    | List |
     .----------------------------------.

     Note that List + List merges the two lists.
     List + <List> will include the second list in the first 
     */
    StickyNote evalAddOp(Node tn) {
        StickyNote answer = null;
        StickyNote s1 = evalExpression(tn.left());
        StickyNote s2 = evalExpression(tn.left().right());
        // Utility.println("Attempting to add " + s1 + " + " + s2);
        // Utility.println("Attempting to add " + s1.val.getClass() + " and " + s2.val); //.getClass());
        // Utility.println("Attempting to add " + tn.left + " and " + tn.left.right); //.getClass());
        if (s1.val instanceof GenesisList || s2.val instanceof GenesisList) {
            printError("1. Addition not possible for '" + tn + "'", tn);
        } else if (s1.val instanceof Node || s2.val instanceof Node) {
            // System.out.println("===> Adding");

            if (s1.val instanceof Node) {
                //  System.out.println("===> Adding:" + s1);
                GenesisList gl1 = new GenesisList((Node) s1.val).copy(); // copy the nodes
                while (gl1.on()) {
                    gl1.move();
                } // move to the end of list g1
                if (s2.val instanceof Node) {
                    //System.out.println("Forming gl2");
                    GenesisList gl2 = new GenesisList((Node) s2.val);
                    gl2.reset();
                    while (gl2.on()) {
                        // System.out.println ("Evaluator: Appending to " + gl1 );
                        // System.out.println( "Evaluator: the value:" + gl2.get());
                        // System.out.println ("Evaluator: Calling insert with");
                        gl1.insert(new StickyNote(gl2.get()));
                        // System.out.println ("Evaluator: giving ... " + gl1);
                        gl2.move();
                    }
                    // System.out.println ("Done!");
                } else if (s2.val instanceof StringVal) {
                    gl1.insert(new StickyNote(s2));
                } else if (s2.val instanceof NumberVal) {
                    //System.out.println("Appending " + s2 + " to " + gl1);
                    StickyNote temp = scope.find("correct");
                    //       if (temp != null) 
                    //         System.out.println ("Correct is: " + temp.val.getClass() + " at " + (Object)temp);
                    gl1.insert(new StickyNote(s2));
                } else {
                    printError("2. Addition not possible for '" + tn + "'.", tn);
                }
                gl1.reset();

                // System.out.println ("Done!" + gl1.toString());

                answer = new StickyNote(gl1.current());
                // System.out.println ("Done!!"+ gl1.current().info);
                // System.out.println ("giving ... " + answer);
            } else {  // s2 must be an instanceof a node
                GenesisList gl2 = new GenesisList((Node) s2.val).copy(); // copy the codes
                //Utility.println("Copying " + s1 + " to " + s2);
                gl2.reset();
                if (s1.val instanceof StringVal) {
                    gl2.insert(new StickyNote(s1));
                } else if (s1.val instanceof DoubleVal) {
                    gl2.insert(new StickyNote(s1));
                } else {
                    printError("3. Addition not possible for '" + tn.info + "'!!!!", tn);
                }
                gl2.reset();
                answer = new StickyNote(gl2.current());
            }

        } else if (s1.val instanceof StringVal || s2.val instanceof StringVal) {
            String s = "";
            if (s1.val instanceof StringVal) {
                if (s2.val instanceof StringVal) {
                    s = "" + s1 + s2;
                } else if (s2.val instanceof NumberVal) {
                    s = "" + s1 + s2;
                } else {
                    printError("4. Addition not possible for '" + tn + "'!!", tn);
                }
                answer = new StickyNote(s);
            } else { // s2 must be a string 

                if (s1.val instanceof NumberVal) {
                    s = "" + s1 + s2;
                } else {
                    printError("5. Addition not possible for '" + tn + "'!!", tn);
                }
                answer = new StickyNote(s);
            }
        } else if (s1.val instanceof NumberVal && s2.val instanceof NumberVal) { // Both!
            double d1 = ((DoubleVal) s1.val).getVal();
            double d2 = ((DoubleVal) s2.val).getVal();
            answer = new StickyNote(d1 + d2);
        } else {
            printError("6. Addition not possible for: '"
                    + s1
                    + " and " + s2 + "'!!!", tn);
        }
        // System.out.println("Returning " + answer);
        return answer;
    }

    StickyNote evalSubtractOp(Node tn) {
        DoubleVal d1 = null, d2 = null;
        try {
            d1 = (DoubleVal) (evalExpression(tn.left()).val);
            d2 = (DoubleVal) (evalExpression(tn.left().right()).val);
        } catch (ClassCastException e) {
            printError("Both operands must be numbers to perform subtraction", tn);
        }
        return new StickyNote(d1.sub(d2));
    }

    StickyNote evalMultiplyOp(Node tn) {
        DoubleVal d1 = null, d2 = null;
        try {
            d1 = (DoubleVal) (evalExpression(tn.left()).val);
            d2 = (DoubleVal) (evalExpression(tn.left().right()).val);
        } catch (ClassCastException e) {
            printError("Both operands must be numbers to perform multiplication", tn);
            //System.exit(1);
        }
        return new StickyNote(d1.mul(d2));
    }

    StickyNote evalDivideOp(Node tn) {
        DoubleVal d1 = null, d2 = null;
        try {
            d1 = (DoubleVal) (evalExpression(tn.left()).val);
            d2 = (DoubleVal) (evalExpression(tn.left().right()).val);
        } catch (ClassCastException e) {
            printError("Both operands must be numbers to perform division", tn);
        }
        return new StickyNote(d1.div(d2));
    }

    // chad added for %
    StickyNote evalModOp(Node tn) {
        DoubleVal d1 = null, d2 = null;
        try {
            d1 = (DoubleVal) (evalExpression(tn.left()).val);
            d2 = (DoubleVal) (evalExpression(tn.left().right()).val);
        } catch (ClassCastException e) {
            printError("Both operands must be numbers to perform mod", tn);
        }
        return new StickyNote(d1.mod(d2));
    }// end evalModOp

    StickyNote evalUnaryMinusOp(Node tn) {
        DoubleVal d1 = null;
        try {
            d1 = (DoubleVal) (evalExpression(tn.left()).val);
        } catch (ClassCastException e) {
            printError("Unary minus can only be applied to a number", tn);
            System.exit(1);
        }
        return new StickyNote(-d1.toDouble());
    }//end evalUnaryMinusOp

    StickyNote evalColonOp(Node tn) {
        // System.out.println("evalColonOp:" );
        // Parser.prettyPrint((TreeNode)tn);
        StickyNote d = null;
        try {
            d = (evalExpression(tn.left().right())); // get the actual expression
            // System.out.println ("Setting d to " +d);
            // System.out.println ("Setting d's label to " +evalExpression(tn.left()));
            d.setLabel(evalExpression(tn.left()).val);    // associate the label
        } catch (ClassCastException e) {
            printError("Both operands must be numbers to perform division", tn);
        }
        return d;
    }

    StickyNote evalListOp(Node tn) {
        //MetaNode mn = new MetaNode();
        GenesisList l = new GenesisList();

        // System.out.println("evalListOp: " + tn);
        GenesisList.quoteStrings(true);
        tn = tn.left();
        while (tn != null) {
            if (testOp(tn, OpVal.aliasOp)) {      // ljm -- 6/20/04
                StickyNote result = evalExpression(tn);
                // System.out.println("Inserting address in list:" + result.val.addr());
         /*
                 if (tn.getLabel() == null )
                 l.insert( evalExpression( tn ) );
                 else
                 l.insert( evalExpression( tn ), getLabel( tn) );
                 */
                l.insert(evalExpression(tn));
            } else {
                // wdp -- 6/22/04
                // same meaning, but for clarity and consistency 
                // ljm -- 7/6/04  but there is no such function ... all 'name' ops are procs
                l.insert(new StickyNote(evalExpression(tn)));
                /*
                 if (tn.getLabel() == null )
                 l.insert(new StickyNote(evalExpression(tn)));
                 else
                 l.insert(new StickyNote(evalExpression(tn)), getLabel(tn) );
                 // l.insert( scope.name( evalExpression( tn ) ));  
                 */
            }
            // System.out.println("Resulting list is:" +l);
            tn = tn.right();
        }
        //l.reset();  l.insert(new MetaNode());
        if (debug) {
            System.out.println("evalListOp returning " + l);
        }
        // System.out.println ("Exiting ..."); System.exit(1);

        if (debug) {
            System.out.println("Expression value:" + l + "type: " + l.getClass());
        }
        StickyNote stickynote = scope.alias(l);
        if (debug) {
            System.out.println("Expression value:" + stickynote + "type: " + stickynote.val.getClass());
        }
        return stickynote;
    }//end evalListOp

    StickyNote evalSelect(Node tn) {
        if (debug) {
            System.out.println("Executing Select");
            Parser.prettyPrint((TreeNode) tn);
        }
        Node cond, stmt;
        tn = tn.left();
        while (tn != null) // for each guardedStmt
        {
            //    System.out.println( "evalSelect:\t\tgetnfo = " + getnfo( tn ) );

            cond = tn.left();
            stmt = cond.right();

            if (evalCondition(cond) == true) {
                return evalStmt(stmt);
            }

            tn = tn.right();
        }

        return null;
    }//end evalSelect

    boolean evalCondition(Node tn) {
        StickyNote lhs, rhs;
        boolean l, r;
        // System.out.println("Entering evalCondition with" + tn.info);
        //Parser.traverse((TreeNode)tn);
        if (testOp(tn, OpVal.trueOp)) {
            return true;
        }
        if (testOp(tn, OpVal.otherwiseOp)) {
            return true;
        }
        if (testOp(tn, OpVal.falseOp)) {
            return false;
        }

        if (testOp(tn, OpVal.orOp)) {
            l = evalCondition(tn.left());
            if (l) {
                return true;
            }
            r = evalCondition(tn.left().right());
            return r;
        } else if (testOp(tn, OpVal.andOp)) {
            l = evalCondition(tn.left());
            if (!l) {
                return false;
            }
            r = evalCondition(tn.left().right());
            return r;
        } else if (testOp(tn, OpVal.notOp)) {
            l = evalCondition(tn.left());
            return !l;
        }
        // System.out.println("After test for " + OpVal.trueOp + testOp(tn,OpVal.trueOp));
        if (testOp(tn, OpVal.ltOp)
                || testOp(tn, OpVal.leOp)
                || testOp(tn, OpVal.gtOp)
                || testOp(tn, OpVal.geOp)
                || testOp(tn, OpVal.eqOp)
                || testOp(tn, OpVal.neOp)) {

            lhs = evalExpression(tn.left());
            rhs = evalExpression(tn.left().right());
            //System.out.println ("Comparing code " + tn.left() + " with " + tn.left().right());
            //System.out.println ("Comparing value" + lhs.val + " with " + rhs.val);
            //System.out.println ("Comparing class" + lhs.val.getClass() + " with " + rhs.val.getClass());
            // OpVal.ltOp, OpVal.leOp, OpVal.gtOp, OpVal.geOp, OpVal.eqOp, OpVal.neOp
            if (!(lhs.val instanceof GV) || !(rhs.val instanceof GV)) {
                // System.out.println (lhs.val instanceof GV);
                // System.out.println (rhs.val instanceof GV);
                return (false); // Just to get around the inability of the compiler to
            }
            if (testOp(tn, OpVal.ltOp)) {
                return ((GV) (lhs.val)).lt(rhs.val);
            } else if (testOp(tn, OpVal.leOp)) {
                return ((GV) (lhs.val)).le(rhs.val);
            } else if (testOp(tn, OpVal.gtOp)) {
                return ((GV) (lhs.val)).gt(rhs.val);
            } else if (testOp(tn, OpVal.geOp)) {
                return ((GV) (lhs.val)).ge(rhs.val);
            } else if (testOp(tn, OpVal.eqOp)) {
                // if (lhs.val instanceof MetaNode )  {lhs =  ((MetaNode)lhs.val).right;}
                //if (rhs.val instanceof MetaNode )  {rhs =  ((MetaNode)lhs.val).right;}
                //System.out.println ("Calling eq for "+ lhs.val + " and " + rhs.val); 
                return ((GV) (lhs.val)).eq(rhs.val);
            } else if (testOp(tn, OpVal.neOp)) {
                return ((GV) (lhs.val)).ne(rhs.val); // ljm -- 6/20/04
            }
        } else // must be a function call, a variable or a literal constant
        if (testOp(tn, OpVal.trueOp)) {
            return (true);
        } else if (testOp(tn, OpVal.falseOp)) {
            return (false);
        } else {
            StickyNote sn = evalExpression(tn);
            // System.out.println("Class of sn is "+ sn.val.getClass());
            if (sn.val instanceof TruthVal) {
                return ((TruthVal) sn.val).val;
            } else {
                printError("Evaluating a condition did not yield a truth value: " + sn, tn);
            }
        }

        return (false); // Just to get around the inability of the compiler to
        // detect the exit in the previous call
    }//end evalCondition

    StickyNote evalEmit(Node tn) {
        System.out.println("Emit not implemented");
        return null;
    }//end evalEmit

    StickyNote evalConcat(Node tn, boolean appendSpace) {
        // System.out.println("Entering evalConcat");
        StickyNote sn = null;
        StringBuffer result = new StringBuffer(1000);
        tn = tn.left(); // skip down to the stuff to print
        while (tn != null) {
            // out.println ("evaling expression for printing: "+tn.info.toString());
            sn = evalExpression(tn);
            result = result.append(sn.toString());
            if (tn.right() != null && appendSpace) {
                result = result.append(" ");
            }
            tn = tn.right();
        }
        // System.out.println("Leaving evalConcat");
        sn = new StickyNote(result.toString());
        return sn;
    }//end evalConcat

    StickyNote evalEcho(Node tn, boolean appendSpace) {
        // System.out.println("Entering evalEcho");
        StickyNote sn = evalConcat(tn, appendSpace);
        System.out.println(sn.toString());
        tn = tn.right();
        // System.out.println("Leaving evalEcho");
        return sn;
    }// end evalEcho

    StickyNote evalPrint(Node tn) {
        if (debug) {
            System.out.println("Executing evalPrint");
            Parser.prettyPrint((TreeNode) tn);
        }
        StickyNote sn = evalEcho(tn, true);
        System.out.println(""); // ljm -- 6/19/04
        return sn;
    }// end evalPrint

    String buildMangledDefName(Node tn) {
        String fname = "*";
        Node n = tn.left();
        if (n != null) {
            n = n.left();
        }
        int i;
        while (n != null) {
            i = 0;
            if (testType(n, new StringVal(""))) {
                fname += n.info.val.toString();
                n = n.right();
            } else if (testType(n, OpVal.parameterOp)
                    && testOp(n, OpVal.parameterOp)) {
                Node p = n.left();
                fname += "(";
                while (p != null) {
                    i++;
                    p = p.right();
                }

                fname += "" + i + ")";
                n = n.right();
            } else if (testType(n, OpVal.whileOp)) {
                n = n.right();
            } else {
                System.out.println("In mangledDefName: NOT SUPPOSED TO BE HERE: " + fname);
            }
        }
        return fname;
    }//end buildMangledDefName

    StickyNote evalFunctionDef(Node tn) {
        // debug=true;
        if (debug) {
            System.out.println("Executing evalFunctionDef");
            Parser.prettyPrint((TreeNode) tn);
        }
        String name = buildMangledDefName(tn);
        if (autotrace) {
            System.out.println("Defining:" + name);
        }
        // first, build the mangled function name
        // then, just "name" it
        scope.setName(buildMangledDefName(tn), tn);
        return tn.getVal();
    }// end evalFunctionDef

    String buildMangledFunctionCallName(Node tn) {
        String fname = "*";
        Node n = tn.left();
        int i;

        //System.out.println("----->");
        while (n != null) {
            i = 0;
            //System.out.println(fname);
            if (testType(n, new StringVal(""))) {
                fname += n.info.val.toString();
                n = n.right();
            } else if (testType(n, OpVal.parameterOp)
                    && testOp(n, OpVal.parameterOp)) {
                Node p = n.left();
                fname += "(";
                while (p != null) {
                    i++;
                    p = p.right();
                }

                fname += "" + i + ")";
                n = n.right();
            } else if (testType(n, OpVal.whileOp)) { //skip
                n = n.right();
            } else {
                System.out.println("In buildMangledFunctionCallName: NOT SUPPOSED TO BE HERE" + n.getVal());
                System.exit(1);
            }
        }
        // System.out.println( "Got fname = "+ fname );
        return fname;
    }//end buildMangledFunctionCallName

    StickyNote evalFunctionCall(Node tn) {
        if (debug) { // && ! testOp ( tn, OpVal.stmtListOp)) 
            System.out.println("Executing evalFunctionCall:");
            Parser.prettyPrint((TreeNode) tn);
        }
        StickyNote returnVal = null;  // To store the returned value

        // first, build the mangled name and collect the parameters
        String fname = buildMangledFunctionCallName(tn);
        if (autotrace) {
            System.out.println("Calling:" + fname);
            autoindent = autoindent + 4;
        }
        if (debug) {
            System.out.println("fname = " + fname);
        }
        // now, set up scope
        //
        // this copies all function entries, then sets the lexical parent
        Scope myScope = new Scope(scope);
        myScope.deleteAllVars();

        // now the parameters
        //
        // grab the call 
        Node call = tn.left(); // skip to the function invocation
        //debug=true;
        if (debug) {
            System.out.println("Grabbing definition for  " + fname);
        }

        // next, grab the definition
        // First check to see if it is a built-in function or procedure
        // This will be true in the scope we have "function" or "procedure" associated
        // with the mangled name
        StickyNote temp = scope.name(fname);
        // System.out.println("Got back");
        if (temp == null) {
            printError("The function " + fname + " has not been defined", tn);
        }
        Node def = (Node) (scope.name(fname).val);

        if (debug) {
            System.out.println("Got to here with fname=" + fname);
        }
        if (debug) {
            System.out.println("def is " + def.info + def.info.getClass());
        }
        if (def.info.val.toString().equals("procedure")) {  // built-in procedure
            if (debug) {
                System.out.println("Calling builtin procedure " + fname);
            }

            if (fname.equals("*move(1)") || fname.equals("*move(1)forward")) {
                GenesisList argument;
                if (call.right.left.info.val instanceof StringVal) {
                    argument = evalRef(call.right.left);  // A GenesisList
                } else {
                    StickyNote sn = evalExpression(call.right.left);
                    if (!(sn.val instanceof GenesisList)) {
                        printError("Trying to move in the non-list '" + sn.val
                                + "' which is of type "
                                + sn.val.getClass(), tn);
                    }
                    argument = (GenesisList) (sn.val);
                }

                // GenesisList.currentIndicator = "*"; 
                // System.out.println ("Moving: " +argument);
                argument.move();
                // System.out.println ("Moved: " +argument);
            } /*
             * Move(A) to (B): A must be a label; B is either a label
             * or B is an expression.
             *
             * Care must be taken, because we don't want to evaluate B
             * if B is a label and that label names an iterator,  
             *
             * If B is a label that names an iterator
             *    Set A to reference the same node as is referenced by B
             * else 
             *    Evaluate B
             *    if its value is an iterator
             *       Set A to reference the same node as this iterator
             *    else if its value is a number
             *       Move A to that position in the list
             *    else 
             *       Error, must evaluate to an iterator or a number
             */ else if (fname.equals("*move(1)to(1)")) {
                Node iter1 = call.right.left;
                String name1 = iter1.info.toString();
                Node iter2 = call.right.right.right.left;
                String name2 = iter2.info.toString();
                StickyNote s1 = scope.find(name1);
                StickyNote s2 = scope.find(name2);
                //System.out.println ("Setting " + iter1.info.val + " to " + iter2.info.val);
                // First: moving to a named iterator
                if (s2 != null && s2.val instanceof GenesisList) {
                    GenesisList gl2;
                    gl2 = (GenesisList) s2.val;
                    if (s1 == null) {  // source not in scope; set it up as an iterator
                        GenesisList gl1 = new GenesisList(gl2);
                        int pos = gl2.pos();
                        //System.out.println("Pos" + pos);
                        gl1.moveTo(pos);
                        //System.out.println(name1 +":" + gl1);
                        //scope.alias(name1,gl1); 
                        letIdNameSN(name1, new StickyNote(gl1));
                        //System.out.println(name1 +":" + scope.find(name1));
                    } else {
                        s1.val = new GenesisList(gl2);
                    }
                } else { // s2 is null or s2 is not a GenesisList (an iterator)
                    StickyNote val2 = evalExpression(iter2);
                    // RHS an iterator? Then moving to a previously computed iterator
                    // System.out.println ("Setting " + iter1.info.val + " to " + iter2.info.val);
                    if (val2.val instanceof GenesisList) {
                        GenesisList gl2;
                        gl2 = (GenesisList) val2.val;
                        // System.out.println("Processing: " + val2 );
                        if (s1 == null) {  // source not in scope; set it up as an iterator
                            GenesisList gl1 = new GenesisList(gl2);
                            int pos = gl2.pos();
                            //System.out.println("Pos" + pos);
                            gl1.moveTo(pos);
                            //System.out.println(name1 +":" + gl1);
                            //scope.alias(name1,gl1); 
                            letIdNameSN(name1, new StickyNote(gl1));
                            //System.out.println(name1 +":" + scope.find(name1));
                        } else {
                            s1.val = new GenesisList(gl2);
                        }
                    } else if (val2.val instanceof DoubleVal) { //moving to a position
                        // System.out.println ("Setting " + iter1.info.val + " to " + iter2.info.val + s1);
                        int d = ((DoubleVal) val2.val).toInt();
                        if (s1 == null) {  // source not in scope; set it up as an iterator
                            printError("Cannot move non-iterator " + name1
                                    + " to  a position");
                        } else if (!(s1.val instanceof GenesisList)) {
                            printError("Cannot move non-iterator " + name1
                                    + " to  a position");
                        } else {
                            //System.out.println ("Moving to new list");
                            ((GenesisList) s1.val).moveTo(d);
                        }
                    } else {
                        printError(name2 + "does not reference a valid location on a list-3");
                    }
                }
            } else if (fname.equals("*move(1)backward")) {
                GenesisList arg1 = evalRef(call.right.left);
                if (!(arg1 instanceof GenesisList)) {
                    printError("Trying to move in non-list " + arg1 + "' which is of type " + arg1.getClass(), tn);
                }
                arg1.prev();
            } else if (fname.equals("*reset(1)")) {
                GenesisList arg1 = evalRef(call.right.left);
                if (!(arg1 instanceof GenesisList)) {
                    printError("Trying to move in non-list " + arg1 + "' which is of type " + arg1.getClass(), tn);
                }
                arg1.reset();
            } else if (fname.equals("*move(1)to(1)")) {
                GenesisList arg1 = evalRef(call.right.left);
                GenesisVal argument2 = evalExpression(call.right.right.right.left).val;
                if (!(arg1 instanceof GenesisList)) {
                    printError("Trying to move in non-list " + arg1 + "' which is of type " + arg1.getClass(), tn);
                }
                int d = ((DoubleVal) argument2).toInt();
                arg1.moveTo(d);
            } else if (fname.equals("*set_precision(1)")) {
                GenesisVal argument = evalExpression(call.right.left).val;
                if (!(argument instanceof NumberVal)) {
                    printError("Error:Trying to set floating point precision using " + argument + ",which is of type" + argument.getClass(), tn);
                }
                DoubleVal.precision = ((DoubleVal) argument).toInt();

            } else if (fname.equals("*split(1)at(1)into(2)")) {
                /*
                 procedureCallOp
                 "split"(1:7)
                 parameterOp
                 "l"(1:11)
                 "at"(1:14)
                 parameterOp
                 numberOp
                 5.0(1:15)
                 "into"(1:23)
                 parameterOp
                 "l1"(1:30)
                 "l2"(2:1)
                 */
                GenesisVal arg1 = evalExpression(call.right.left).val;
                GenesisVal arg2 = evalExpression(call.right.right.right.left).val;

                Node dest1;
                dest1 = new TreeNode(OpVal.idNameOp,
                        (TreeNode) call.right.right.right.right.right.left,
                        null);
                Node dest2 = new TreeNode(OpVal.idNameOp,
                        (TreeNode) call.right.right.right.right.right.left.right,
                        null);
                // System.out.println("dest1:" + dest1);
                // System.out.println("dest1:" + dest2);
                // Must split at offset into two lists
                if (!(arg1 instanceof Node)) {
                    printError("First value passed to split must be a list; found: "
                            + arg1);
                }
                if (!(arg2 instanceof NumberVal)) {
                    printError("Second value passed to split must be a number; found: "
                            + arg2);

                }
                Node source = (Node) arg1;
                double offset = ((DoubleVal) arg2).getVal();
                // System.out.println("source:" + source);
                // System.out.println("offset:" + offset);
                StickyNote result = null;

                //if (offset  < 1) { 
                //printError("Second value must be a positive integer; found: " + offset);
                //}
                if (offset <= 1) { // dest1 is empty, dest2 names source
                    //Node n = null;
                    Node n = new MetaNode();
                    evalIdNameValue(dest1, new StickyNote(n));
                    evalIdNameValue(dest2, new StickyNote(source));
                } else {
                    // System.out.println("calling evalIdNameValue with " + dest1 + source);
                    evalIdNameValue(dest1, new StickyNote(source));
                    while (source != null && offset > 1) {
                        source = source.next();
                        offset = offset - 1;
                    }
                    Node n = new MetaNode();
                    if (source != null) {
                        n.right = source.next(); // setup new list
                        source.right = null;       //break the list
                    }
                    evalIdNameValue(dest2, new StickyNote(n));

                }
            } else if (fname.equals("*set_trace_on(0)")) {
                //System.out.println("Tracing turned on");
                autotrace = true;
            } else if (fname.equals("*set_trace_off(0)")) {
                //System.out.println("Tracing turned off");
                autotrace = false;
            }
            returnVal = new StickyNote();
        } else if (def.info.val.toString().equals("function")) {  // built-in function
            if (debug) {
                System.out.println("Calling builtin function " + fname);
            }
            if (fname.equals("*integer(1)")) {
                GenesisVal argument = evalExpression(call.right.left).val;
                if (!(argument instanceof NumberVal)) {
                    printError("Trying to compute the integer portion of " + argument + "' which is of type " + argument.getClass(), tn);

                }
                returnVal = new StickyNote(((DoubleVal) argument).toInt());
            } else if (fname.equals("*on(1)")) {
                GenesisVal arg1;
                if (call.right.left.info.val instanceof StringVal) {
                    arg1 = evalRef(call.right.left);
                } else {
                    arg1 = evalExpression(call.right.left).val;
                }
                if (!(arg1 instanceof GenesisList)) {
                    printError("Trying to access a non-list" + arg1 + "' which is of type " + arg1.getClass(), tn);

                }
                if (((GenesisList) arg1).on()) {
                    returnVal = new StickyNote(new TruthVal(true));
                } else {
                    returnVal = new StickyNote(new TruthVal(false));
                }
            } else if (fname.equals("*atfirst(1)")) {
                GenesisVal arg1;
                if (call.right.left.info.val instanceof StringVal) {
                    arg1 = evalRef(call.right.left);
                } else {
                    arg1 = evalExpression(call.right.left).val;
                }
                if (!(arg1 instanceof GenesisList)) {
                    printError("Trying to access a non-list" + arg1 + "' which is of type " + arg1.getClass(), tn);

                }
                if (((GenesisList) arg1).atFirst()) {
                    returnVal = new StickyNote(new TruthVal(true));
                } else {
                    returnVal = new StickyNote(new TruthVal(false));
                }
            } else if (fname.equals("*atlast(1)")) {
                GenesisVal arg1;
                if (call.right.left.info.val instanceof StringVal) {
                    arg1 = evalRef(call.right.left);
                } else {
                    arg1 = evalExpression(call.right.left).val;
                }
                if (!(arg1 instanceof GenesisList)) {
                    printError("Trying to access a non-list" + arg1 + "' which is of type " + arg1.getClass(), tn);

                }
                if (((GenesisList) arg1).atLast()) {
                    returnVal = new StickyNote(new TruthVal(true));
                } else {
                    returnVal = new StickyNote(new TruthVal(false));
                }
            } else if (fname.equals("*off(1)")) {
                GenesisVal arg1;
                if (call.right.left.info.val instanceof StringVal) {
                    arg1 = evalRef(call.right.left);
                } else {
                    arg1 = evalExpression(call.right.left).val;
                }
                if (!(arg1 instanceof GenesisList)) {
                    printError("Trying to access a non-list" + arg1 + "' which is of type " + arg1.getClass(), tn);

                }
                if (((GenesisList) arg1).on()) {
                    returnVal = new StickyNote(new TruthVal(false));
                } else {
                    returnVal = new StickyNote(new TruthVal(true));
                }
            } else if (fname.equals("*position(1)")) {
                GenesisList arg1 = evalRef(call.right.left);
                if (!(arg1 instanceof GenesisList)) {
                    printError("Trying to move in non-list " + arg1 + "' which is of type " + arg1.getClass(), tn);


                }
                // GenesisList.currentIndicator = "*";
                //System.out.println("Computing position in" + arg1);
                int d = arg1.pos();
                returnVal = new StickyNote(new DoubleVal(d));
            } else if (fname.equals("*fractional(1)")) {
                GenesisVal argument = evalExpression(call.right.left).val;
                if (!(argument instanceof NumberVal)) {
                    printError("Trying to compute the fractional portion of " + argument + "' which is of type " + argument.getClass(), tn);

                }
                double d = ((DoubleVal) argument).toDouble();
                d = d - (int) d;
                if (d == 0.0) {
                    d = 0.0;   // to avoid the negative zero of Java
                }
                returnVal = new StickyNote(new DoubleVal(d));
            } else if (fname.equals("*remainder(1)divided_by(1)")) {
                GenesisVal arg1 = evalExpression(call.right.left).val;
                GenesisVal argument2 = evalExpression(call.right.right.right.left).val;
                if (!(arg1 instanceof NumberVal) || !(argument2 instanceof NumberVal)) {
                    printError("Error: Trying to compute the remainder  of " + arg1 + "' divided by  " + argument2 + ".  One or both of these are not numbers", tn);

                } else if (((DoubleVal) argument2).toInt() == 0) {
                    printError("Error: Trying to divide by zero", tn);
                }

                int d1 = ((DoubleVal) arg1).toInt();
                int d2 = ((DoubleVal) argument2).toInt();
                return (new StickyNote(d1 % d2));

            } else if (fname.equals("*quotient(1)divided_by(1)")) {
                GenesisVal arg1 = evalExpression(call.right.left).val;
                GenesisVal argument2 = evalExpression(call.right.right.right.left).val;
                if (!(arg1 instanceof NumberVal) || !(argument2 instanceof NumberVal)) {
                    printError("Error: Trying to compute the quotient  of " + arg1 + "' divided by  " + argument2 + ".  One or both of these are not numbers", tn);

                } else if (((DoubleVal) argument2).toInt() == 0) {
                    printError("Error: Trying to divide by zero", tn);
                }

                int d1 = ((DoubleVal) arg1).toInt();
                int d2 = ((DoubleVal) argument2).toInt();
                return (new StickyNote(d1 / d2));

            } //else if (fname.equals("*concat(*)")) { //  Work on this later ... for var # of params
            //}
            else if (fname.equals("*square_root(1)")) {
                GenesisVal argument = evalExpression(call.right.left).val;
                if (!(argument instanceof NumberVal)) {
                    printError("Error:Trying to compute the square root of " + argument + ",which is of type" + argument.getClass(),
                            tn);
                } else if (((DoubleVal) argument).toInt() < 0) {
                    printError("Error:Trying to compute the square root of " + argument + ",which is a negative number", tn);
                }

                // int d =((DoubleVal)argument).toInt();
                // return(new StickyNote(Math.sqrt(d)));
                double d = ((DoubleVal) argument).toDouble();
                return (new StickyNote(Math.sqrt(d)));
            } else if (fname.equals("*random(0)")) {
                int i = (int) (2000000000 * Math.random());
                return (new StickyNote(i));
            } else if (fname.equals("*explode(1)")) {
                GenesisVal argument = evalExpression(call.right.left).val;
                if (!(argument instanceof StringVal)) {
                    printError("Error: Trying to explode " + argument + ", which is not a string", tn);
                }
                String source = ((StringVal) argument).toString();

                Node firstNode = null;
                if (source.length() != 0) {
                    int curIndex = 0;
                    Node node = new Node("" + source.charAt(0));
                    firstNode = node;

                    curIndex++;
                    while (curIndex < source.length()) {
                        node.right = new Node(source.substring(curIndex, curIndex + 1));
                        node = node.right;
                        curIndex++;
                    }
                }

                return (new StickyNote(firstNode));
            } else if (fname.equals("*is(1)a_number?")) {
                if (!(call.right.left.info.val instanceof StringVal)) {
                    return (new StickyNote(false));
                }
                GenesisVal argument = evalExpression(call.right.left).val;
                if (argument instanceof NumberVal) {
                    return (new StickyNote(true));
                } else {
                    return (new StickyNote(false));
                }
            } else if (fname.equals("*is(1)a_truth_value?")) {
                //if (!(call.right.left.info.val instanceof StringVal))  // see if its a name
                //   return(new StickyNote(false));
                GenesisVal argument = evalExpression(call.right.left).val;
                if (argument instanceof TruthVal) {
                    return (new StickyNote(true));
                } else {
                    return (new StickyNote(false));
                }
            } else if (fname.equals("*is(1)a_string?")) {
                //if(!(call.right.left.info.val instanceof StringVal)) return(new StickyNote(false));
                GenesisVal argument = evalExpression(call.right.left).val;
                if (argument instanceof StringVal) {
                    return (new StickyNote(true));
                } else {
                    return (new StickyNote(false));
                }
            } else if (fname.equals("*is(1)an_iterator?")) {
                if (call.right.left.info.val instanceof StringVal) {
                    StickyNote sn = scope.searchRef(call.right.left.info.val.toString());
                    return (new StickyNote(sn.val instanceof GenesisList));
                }
                GenesisVal argument = evalExpression(call.right.left).val;
                if (argument instanceof GenesisList) {
                    return (new StickyNote(true));
                } else {
                    return (new StickyNote(false));
                }
            } else if (fname.equals("*is(1)a_list?")) {
                //if(!(call.right.left.info.val instanceof StringVal)) return(new StickyNote(false));
                GenesisVal argument = evalExpression(call.right.left).val;
                //System.out.println (fname + argument.getClass());
                if ((argument instanceof Node)) {
                    return (new StickyNote(true));
                } else {
                    return (new StickyNote(false));
                }
            } else if (fname.equals("*is(1)defined?")) {
                if (!(call.right.left.info.val instanceof StringVal)) {
                    return (new StickyNote(false));
                }
                StickyNote sn = scope.searchRef(call.right.left.info.val.toString());
                return new StickyNote(sn != null);
            } else if (fname.equals("*is(1)empty?")) {
                // if(!(call.right.left.info.val instanceof StringVal)) return(new StickyNote(false));
                GenesisVal argument = evalExpression(call.right.left).val;
                if (!(argument instanceof Node)) {
                    printError("Attempt to ask if  nonlist '" + argument + "' is empty", tn);
                    return (new StickyNote(false));
                }
                while (argument instanceof MetaNode) {
                    argument = ((Node) argument).right;
                }
                if (argument == null) {
                    return (new StickyNote(true));
                } else {
                    return (new StickyNote(false));
                }
            } else if (fname.equals("*type(1)")) {
                // if(!(call.right.left.info.val instanceof StringVal)) return(new StickyNote("Unknown type"));
                GenesisVal argument = evalExpression(call.right.left).val;
                if (argument instanceof NumberVal) {
                    return (new StickyNote("Number"));
                } else if (argument instanceof StringVal) {
                    return (new StickyNote("String"));
                } else if (argument instanceof Node) {
                    return (new StickyNote("List"));
                } else if (argument instanceof GenesisList) {
                    return (new StickyNote("Iterator"));
                } else if (argument instanceof TruthVal) {
                    return (new StickyNote("TruthValue"));
                }
            } else if (fname.equals("*iterator(1)")) {
                /*
                 # iterator(a[2])
                 functionCallOp   
                 "iterator"(2:20)      <-- call
                 parameterOp
                 subscriptOp
                 "a"(2:24)
                 numberOp
                 2.0(2:23)
                 OR:

                 functionCallOp
                 "iterator"(t.gen:4:22)  <-- call
                 parameterOp
                 "n"(t.gen:4:24)

                 */
                //GenesisList.currentIndicator = "*"; // debug = true;
                //debug = true;
                if (debug) {
                    System.out.println("Processing iterator(" + call.right.left.info.val.toString() + ")");
                }
                if (call.right.left.info.val instanceof StringVal) {
                    /*
                     * There is a tricky issue resolved here.  A call to iterator on the lhs of a Let as in
                     * Let iterator(m) name iterator(n)
                     * must return the stickynote associated with m and not a new stickynote
                     */
                    String str = call.right.left.info.val.toString();
                    //System.out.println("Searching for " +str);
                    StickyNote sn = scope.searchRef(str);   // search for corresponding to iterator
                    if (sn == null) {
                        printError("Iterator() is trying to form an iterator from a name with no associated value: " + str);
                    }
                    //System.out.println ("Found: ("  + sn.val.getClass() +")");
                    if (sn.val instanceof GenesisList) {
                        //System.out.println ("Got in here2");
                        //System.out.println ("Iterator for " + sn.val + "Pos:"+ ((GenesisList) sn.val).pos());
                        //StickyNote s = new StickyNote(new GenesisList((GenesisList) sn.val));
                        //System.out.println ("Iterator for " + s.val + "Pos:" + ((GenesisList) s.val).pos());

                        return (sn);
                        //return(s);
                    } else if (sn.val instanceof Node) {

                        GenesisList gl = new GenesisList(sn);
                        return new StickyNote(gl);
                    }
                    printError("Iterator() is trying to form an iterator from non-iterator: " + sn, tn);
                }
                //System.out.println("EvalRefing" + call.right.left.info);
                GenesisVal argument = evalRef(call.right.left);
                //GenesisVal argument = evalExpression(call.right.left).val; 
                //System.out.println("Back:" + call.right.left+ ' ' + argument );
                //GenesisVal argument = scope.searchRef(call.right.left); 
                if (!(argument instanceof GenesisList) && !(argument instanceof Node)) {
                    printError("Iterator() is trying to form an iterator from  " + argument
                            + ". which needs to be an iterator, a list or a position in an list.", tn);
                }
                //System.out.println("Returning"+ (argument instanceof GenesisList));
                if (argument instanceof GenesisList) {  // The arg is an iterator
                    //System.out.println("Creating new GL from " + argument);
                    GenesisList gl = new GenesisList((GenesisList) argument);
                    // System.out.println("New GL list created " + gl);
                    // gl.reset();
                    //System.out.println("Returning -- " + gl);
                    return (new StickyNote(gl));
                } else { // arg is a Node, i.e. some raw list; make up an iterator for it
                    return (new StickyNote(new GenesisList((Node) argument)));
                }
            } // *iterator
            else if (fname.equals("*iterator(2)")) {
                /*
                 # iterator(a,n)
                 functionCallOp
                 "iterator"(1:15)   <-- call
                 parameterOp
                 "l"(1:20)
                 "n"(2:1)
                 */
                //GenesisList.currentIndicator = "*"; // debug = true;
                //debug = true;
                if (debug) {
                    System.out.println("Processing iterator(" + call.right.left.info.val.toString() + ")");
                }
                if (call.right.left.info.val instanceof StringVal) {
                    /*
                     * There is a tricky issue resolved here.  A call to iterator on the lhs of a Let as in
                     * Let iterator(m) name iterator(n)
                     * must return the stickynote associated with m and not a new stickynote
                     */
                    String str = call.right.left.info.val.toString();
                    // System.out.println("Searching for " +str);
                    StickyNote sn = scope.searchRef(str);   // search for corresponding to iterator
                    if (sn == null) {
                        printError("Iterator() is trying to form an iterator from a non-existent value: " + str);
                    }
                    //System.out.println ("Found: ("  + sn.val.getClass() +")");
                    if (sn.val instanceof GenesisList) {
                        //System.out.println ("Got in here");
                        //System.out.println ("Iterator for " + sn.val + "Pos:"+ ((GenesisList) sn.val).pos());
                        //StickyNote s = new StickyNote(new GenesisList((GenesisList) sn.val));
                        //System.out.println ("Iterator for " + s.val + "Pos:" + ((GenesisList) s.val).pos());
                        //System.out.println ("Returning iterator for " + s);
                        return (sn);
                        //return(s);
                    }
                    printError("Iterator() is trying to form an iterator from non-iterator: " + sn, tn);
                }
                //System.out.println("EvalRefing" + call.right.left.info);
                GenesisVal argument = evalRef(call.right.left);
                //GenesisVal argument = evalExpression(call.right.left).val; 
                //System.out.println("Back" + call.right.left.info);
                //GenesisVal argument = scope.searchRef(call.right.left); 
                //System.out.println (argument.getClass()); 
                if (!(argument instanceof GenesisList) && !(argument instanceof Node)) {
                    printError("Iterator() is trying to form an iterator from  " + argument
                            + ". which needs to be an iterator.", tn);
                }
                //System.out.println("Returning"+ (argument instanceof GenesisList));
                if (argument instanceof GenesisList) {  // The arg is an iterator
                    //System.out.println("Creating new GL from " + argument);
                    GenesisList gl = new GenesisList((GenesisList) argument);
                    // System.out.println("New GL list created " + gl);
                    //gl.reset();
                    //System.out.println("Returning --" + gl);
                    return (new StickyNote(gl));
                } else { // arg is a Node, i.e. some raw list; make up an iterator for it
                    return (new StickyNote(new GenesisList((Node) argument)));
                }
            } // *iterator
            else if (fname.equals("*sublist(2)")) {
                /*
                 sublist(L,2)
                 functionCallOp
                 "sublist"(1:18)<---- call
                 parameterOp  
                 "L"(1:23)  <--- arg1
                 numberOp   <--- arg2
                 2.0(1:21)
                 */
                GenesisVal arg1 = evalExpression(call.right.left).val;
                GenesisVal arg2 = evalExpression(call.right.left.right).val;
                // arg1 should be a Node 
                //System.out.println("arg1 is " + arg1);
                if (!(arg1 instanceof Node)) {
                    printError("Attempting to compute the sublist of '"
                            + call.right.left + "' which is not a list:" + arg1, tn);
                }
                if (!(arg2 instanceof NumberVal)) {
                    printError("Attempting to compute the sublist of '"
                            + call.right.left + "' starting at:" + arg2
                            + " which is not a number.", tn);
                }
                Node n = (Node) arg1;
                double d = ((DoubleVal) arg2).getVal();
                // System.out.println ("Moving from " + n.info + n.getClass());
                while (d > 1 && n != null) {
                    // System.out.println ("Moving  with d="+ d);
                    if (!(n instanceof MetaNode)) {
                        d = d - 1;
                    }
                    n = n.right();
                    //System.out.println ("Moved to from " + n.info + n.getClass() 
                    //                + "with d="+ d);
                }

                //System.out.println ("Done moving" + n);
                if (n == null) {
                    n = new MetaNode();
                }
                return (new StickyNote(n));
            } else if (fname.equals("*next(1)") || fname.equals("*succ(1)")) {
                /*
                 next of(L)
                 functionCallOp
                 "next" (1:18)<---- call
                 parameterOp  
                 "n"(1:23)  <--- arg1
                 */
                GenesisVal arg1;
                if (call.right.left.info.val instanceof StringVal) {
                    arg1 = evalRef(call.right.left);
                } else {
                    arg1 = evalExpression(call.right.left).val;
                }
                //System.out.println("arg1 is " + arg1);
                // arg1 should be a Node 
          /*
                 if (arg1 == null) {
                 printError( "'next' can only be applied to an iterator positioned on a list.\n> "
                 + call.right.left
                 + " is not a non-empty list."
                 );
                 }
                 */
                if (!(arg1 instanceof GenesisList)) {
                    printError("Attempting to compute the next of '"
                            + call.right.left + "' which is not an iterator:" + arg1, tn);
                }
                GenesisList list = new GenesisList((GenesisList) arg1);
                list.move();
                return (new StickyNote(list));
            } else if (fname.equals("*prev(1)")) {
                /*
                 prev of(n)
                 functionCallOp
                 "prev" (1:18)<---- call
                 parameterOp  
                 "n"(1:23)  <--- arg1
                 */
                GenesisVal arg1;
                if (call.right.left.info.val instanceof StringVal) {
                    arg1 = evalRef(call.right.left);
                } else {
                    arg1 = evalExpression(call.right.left).val;
                }
                //System.out.println("arg1 is " + arg1);
                // arg1 should be a Node 
          /*
                 if (arg1 == null) {
                 printError( "'next' can only be applied to an iterator positioned on a list.\n> "
                 + call.right.left
                 + " is not a non-empty list."
                 );
                 }
                 */
                if (!(arg1 instanceof GenesisList)) {
                    printError("Attempting to compute the prev of '"
                            + call.right.left + "' which is not an iterator:" + arg1, tn);
                }
                GenesisList list = new GenesisList((GenesisList) arg1);
                list.prev();
                return (new StickyNote(list));
            } else if (fname.equals("*value(1)")) {
                /*
                 prev of(n)
                 functionCallOp
                 "value" (1:18)<---- call
                 parameterOp  
                 "n"(1:23)  <--- arg1
                 */
                StickyNote arg1;
                arg1 = evalExpression(call.right.left);
                if (arg1.val instanceof GenesisList) {
                    arg1 = ((GenesisList) (arg1.val)).get();
                }
                return (arg1);
            } else if (fname.equals("*head(1)")) {
                /*
                 head of(L)
                 functionCallOp
                 "tail" (1:18)<---- call
                 parameterOp  
                 "L"(1:23)  <--- arg1
                 */
                GenesisVal arg1 = evalExpression(call.right.left).val;
                //System.out.println("arg1 is " + arg1);
                // arg1 should be a Node 
          /*
                 if (arg1 == null) {
                 printError( "'tail' can only be applied to a non-empty list.\n> "
                 + call.right.left
                 + " is not a non-empty list."
                 );
                 }
                 */
                if (!(arg1 instanceof Node)) {
                    printError("Attempting to compute the head of '"
                            + call.right.left + "' which is not a list:" + arg1, tn);
                }
                Node n = (Node) arg1;
                // System.out.println ("Moving from " + n.info + n.getClass());
                while (n != null && n instanceof MetaNode) {
                    n = n.right();
                }
                if (n == null) {
                    printError("Attempting to compute the head of " + call.right.left
                            + " which is an empty list ");
                }
                return (new StickyNote(n.info));
            } else if (fname.equals("*tail(1)")) {
                /*
                 tail of(L)
                 functionCallOp
                 "tail" (1:18)<---- call
                 parameterOp  
                 "L"(1:23)  <--- arg1
                 */
                GenesisVal arg1 = evalExpression(call.right.left).val;
                //System.out.println("arg1 is " + arg1);
                // arg1 should be a Node 
          /*
                 if (arg1 == null) {
                 printError( "'tail' can only be applied to a non-empty list.\n> "
                 + call.right.left
                 + " is not a non-empty list."
                 );
                 }
                 */
                if (!(arg1 instanceof Node)) {
                    printError("Attempting to compute the tail of '"
                            + call.right.left + "' which is not a list:" + arg1, tn);
                }
                Node n = (Node) arg1;
                double d = 2;
                // System.out.println ("Moving from " + n.info + n.getClass());
                while (d > 1 && n != null) {
                    // System.out.println ("Moving  with d="+ d);
                    if (!(n instanceof MetaNode)) {
                        d = d - 1;
                    }
                    n = n.right();
                    //System.out.println ("Moved to from " + n.info + n.getClass() 
                    //                + "with d="+ d);
                }

                // System.out.println ("Done moving" + n);
                if (n == null) {
                    n = new MetaNode();
                }
                return (new StickyNote(n));
            } else if (fname.equals("*sublist(2)")) {
                /*
                 sublist(L,2)
                 functionCallOp
                 "sublist"(1:18)<---- call
                 parameterOp  
                 "L"(1:23)  <--- arg1
                 numberOp   <--- arg2
                 2.0(1:21)
                 */
                GenesisVal arg1 = evalExpression(call.right.left).val;
                GenesisVal arg2 = evalExpression(call.right.left.right).val;
                // arg1 should be a Node 
                if (!(arg1 instanceof Node)) {
                    printError("Attempting to compute the sublist of '"
                            + call.right.left + "' which is not a list:" + arg1, tn);
                }
                if (!(arg2 instanceof NumberVal)) {
                    printError("Attempting to compute the sublist of '"
                            + call.right.left + "' starting at:" + arg2
                            + " which is not a number", tn);
                }
                Node n = (Node) arg1;
                double d = ((DoubleVal) arg2).getVal();
                // System.out.println ("Moving from " + n.info + n.getClass());
                while (d > 1 && n != null) {
                    // System.out.println ("Moving  with d="+ d);
                    if (!(n instanceof MetaNode)) {
                        d = d - 1;
                    }
                    n = n.right();
                    //System.out.println ("Moved to from " + n.info + n.getClass() 
                    //                + "with d="+ d);
                }

                // System.out.println ("Done moving" + n);
                return (new StickyNote(n));
            } else if (fname.equals("*length(1)") || fname.equals("*size(1)")) { // return the length of the list 
                // if it is a list, otherwise return 0
                GenesisVal argument = evalExpression(call.right.left).val;
                int count = 0;
                if (argument instanceof Node) {
                    Node n = (Node) argument;
                    do {
                        if (!(n instanceof MetaNode)) {
                            count++;
                        }
                        n = n.next();
                    } while (n != null);
                }
                return new StickyNote(count);

            } else {
                printError("Unknown built-in function or procedure '" + fname + "'", tn);
            }
        } else {
            def = def.left().left(); //skip to the function signature

            // walk through the headers linking the parameters
            while (call != null && def != null) {
                if (testType(call, OpVal.parameterOp)) {
                    if (debug) {
                        System.out.println("Formal parameter is '" + def.left.info.val.toString() + "'");
                    }
                    if (debug) {
                        System.out.println("Actual parameter is '" + evalExpression(call.left) + "'");
                    }
                    // Loop through ever parameter (ljm: 8/02/04)
                    TreeNode formalParam = (TreeNode) def.left();
                    TreeNode actualParam = (TreeNode) call.left();
                    while (formalParam != null && actualParam != null) {
                        // StickyNote  sn = evalExpression(actualParam);;
                        //    if (sn.val instanceof GenesisList) { // value being passed is an iterator
                        //      sn = new StickyNote (new GenesisList((GenesisList)sn.val));
                        //  }
                        if (testOp(formalParam, OpVal.aliasOp)) {
                            // letIdAliasSN (myScope, formalParam.left().info.val.toString(), evalExpression( actualParam ) );
                            // See if the calling param is an id and is in the scope. If not add it to the scope
                            //System.out.println("Mapping aliased " + formalParam + " to "+ actualParam);
                            if (actualParam.info.val instanceof StringVal) {
                                StickyNote n = scope.find(actualParam.info.val.toString());  // SN assoc with id
                                if (n == null) {
                                    scope.setName(actualParam.info.val.toString(), new StickyNote());
                                }
                            }

                            myScope.alias(formalParam.left().info.val.toString(), evalExpression(actualParam));
                        } else if (testOp(formalParam, OpVal.iteratorOp)) {
                            //System.out.println("Mapping iterator " + formalParam + " to "+ actualParam);
                            StickyNote n = null;
                            if (!(actualParam.info.val instanceof StringVal)) {
                                printError("Cannot link "
                                        + formalParam.left() + " to "
                                        + actualParam);
                            } else {
                                n = scope.searchRef(actualParam.info.val.toString());
                                //System.out.println("Got n " + n);
                                if (n == null) {
                                    printError("Cannot link "
                                            + formalParam.left() + " to "
                                            + actualParam);
                                }
                            }

                            //System.out.println ( "Aliasing " +formalParam.left().info.val.toString()

                            //+ " to " + n );
                            myScope.alias(formalParam.left().info.val.toString(), n);
                        } else {
                            StickyNote sn = evalExpression(actualParam);
                            myScope.setName(formalParam.info.val.toString(), sn);
                            //letIdNameSN (myScope, 
                            //           formalParam.info.val.toString(), 
                            //          evalExpression( actualParam ) );
                            //String id = actualParam.info.val.toString();
                        }
                        formalParam = (TreeNode) formalParam.right();
                        actualParam = (TreeNode) actualParam.right();
                    }
                    if (formalParam != null) {
                        printError("More formal parameters than actual parameters", tn);
                    }
                    if (actualParam != null) {
                        printError("More actual parameters than formal parameters", tn);
                    }
                    //System.out.println("Passing " + evalExpression(call.left));
                }
                def = (TreeNode) def.right();
                call = (TreeNode) call.right();
            }
            // Now, we should have all the parameters linked.
            // Adjust the environment. 
            Scope savedScope = scope;
            scope = myScope;
            def = (Node) (scope.name(fname).val);
            // Execute the function block.
            // if ( testOp( def, OpVal.functionDefOp ) )// ljm: 7/16/04
            //    returnVal = evalStmtList( def.left().right().left() );: 7/16/04
            // else: 7/16/04
            returnVal = evalStmtList(def.left().right());
            // Restore the environment.
            scope = savedScope;
        }
        if (debug) {
            System.out.println("Exiting evalFunctionCallOp" + returnVal);
        }
        return returnVal;
    }//end evalFunctionCall

    // wdp -- 6/22/04
    // started work here
    // ljm -- Continued

    /*
     Strategy: First get this to work for single iterators over single lists
     Once that is working, then extend to multiple iterators 
     For single iterator over a single list, we have the following format:
     Generate each(x) from L  | ....
     what we need to do is:
     1. Find out the name of the iterator (x); place it in the scope
     with a stickynote that contains an iterator to the first member of L
     (Later: if L is not a list, generate a Node for each value that should
     be in the list: e.g. 1 through 10)
     2. Now interpret the "initialization" code for each filter (taskOp) associated
     with the generate
     3. Now run the "body" of each filter while the iterator is still on the list
     4. After the iterator becomes null, run the "finalization" of each filter
     
     
     */
    StickyNote evalPipe(Node tn) {
        StickyNote result = null;
        //debug = true;
        if (testOp(tn.left, OpVal.generatorCallOp)) {
            evalUserPipe(tn);
        } else if (testOp(tn.left, OpVal.generateStmtOp)) {
            Node pipeline = tn.left;
            GenesisList list;
            String listName = "";
            String iteratorName = pipeline.left.left.right.info.toString();  // get iterator name
       /*
             This is the start of code for handling iterators that are persistent
             if (isIterator(iteratorName)) {
             StickyNote sn = scope.find(iteratorName);// (Returns Sticky note to the GenesisList)
             list = (GenesisList) (sn.val);
             }
             else
             */
            StickyNote sn;
            tempNo = tempNo + 1;
            boolean genesisListFound = false;
            String stringName = "";
            if (testOp(pipeline.left.right, OpVal.rangeNxN_Op)
                    || testOp(pipeline.left.right, OpVal.rangeNxNxN_Op)) {
                listName = "$temp" + tempNo;
                StickyNote e1 = evalExpression(pipeline.left.right.left);
//          System.out.println("Interpreting rangeNxN");
                StickyNote e2 = evalExpression(pipeline.left.right.left.right);
                if (!(e1.val instanceof DoubleVal && e2.val instanceof DoubleVal)) {
                    printError("Cannot iterate from " + e1 + " to " + e2, tn);
                }

                double i1 = ((DoubleVal) e1.val).toDouble();
                double i2 = ((DoubleVal) e2.val).toDouble();
                double incr = 1.0;
                if (testOp(pipeline.left.right, OpVal.rangeNxNxN_Op)) {
                    StickyNote e3 = evalExpression(pipeline.left.right.left.right.right);
                    incr = ((DoubleVal) e3.val).toDouble();
                    if (incr <= 0) {
                        printError("Increment in a range must be positive, not" + incr);
                    }
                }
                //System.out.println("i1="+i1);
                //System.out.println("i2="+i2);

                GenesisList gl = new GenesisList();
                if (i1 <= i2) {
                    for (double i = i1; i <= i2; i = i + incr) {
                        DoubleVal d = new DoubleVal(i);
                        gl.insert(new StickyNote(d));
                    }
                }
                /*
                 else {
                 for (double i=i1; i >= i2; i=i-incr) {
                 DoubleVal d = new DoubleVal(i);
                 gl.insert(new StickyNote(d));
                 }
                 }
                 */
                //System.out.println("List generated"); gl.displayln();
                listName = "$temp" + tempNo;
                result = scope.alias(gl);
                letIdNameSN(listName, result);
            } else if (testOp(pipeline.left.right, OpVal.downto_rangeNxN_Op)
                    || testOp(pipeline.left.right, OpVal.downto_rangeNxNxN_Op)) {
                listName = "$temp" + tempNo;

                StickyNote e1 = evalExpression(pipeline.left.right.left);
//          System.out.println("Interpreting rangeNxN");
                StickyNote e2 = evalExpression(pipeline.left.right.left.right);
                if (!(e1.val instanceof DoubleVal && e2.val instanceof DoubleVal)) {
                    printError("Cannot iterate from " + e1 + " to " + e2, tn);
                }

                double i1 = ((DoubleVal) e1.val).toDouble();
                double i2 = ((DoubleVal) e2.val).toDouble();
                double incr = 1.0;
                if (testOp(pipeline.left.right, OpVal.downto_rangeNxNxN_Op)) {
                    StickyNote e3 = evalExpression(pipeline.left.right.left.right.right);
                    incr = ((DoubleVal) e3.val).toDouble();
                    if (incr <= 0) {
                        printError("Increment in a range must be positive, not" + incr);
                    }
                }
                //System.out.println("i1="+i1);
                //System.out.println("i2="+i2);
                //System.out.println("incr="+incr);

                GenesisList gl = new GenesisList();
                if (i1 >= i2) {
                    for (double i = i1; i >= i2; i = i - incr) {
                        DoubleVal d = new DoubleVal(i);
                        gl.insert(new StickyNote(d));
                    }
                }
                /*
                 else {
                 for (double i=i1; i <= i2; i=i+incr) {
                 DoubleVal d = new DoubleVal(i);
                 gl.insert(new StickyNote(d));
                 }
                 // System.out.println("Negative List i2=" + i2 + " i1=" + i1); gl.displayln();
                 }
                 */
                //System.out.println("List generated"); gl.displayln();
                listName = "$temp" + tempNo;
                result = scope.alias(gl);
                letIdNameSN(listName, result);
            } else if (testOp(pipeline.left.right, OpVal.listOp)) { // A literal list is supplied
                // Create a temporary name for a literal list
                listName = "$temp" + tempNo;
                letIdNameSN(listName, evalListOp(pipeline.left.right));
            } else if (testOp(pipeline.left.right, OpVal.functionCallOp)) { // a function call is supplied
                // Create a list by calling a function
                listName = "$temp" + tempNo;
                StickyNote s = evalFunctionCall(pipeline.left.right);
                if (s.getVal() instanceof StringVal) { // change it to a list
                    String source = s.toString();
                    Node firstNode = Node.str2Node(source);
                    s = new StickyNote(firstNode);
                }
                //System.out.println("Got back3");
                if (s.val instanceof GenesisList) // bizarre patch needed
                {
                    genesisListFound = true;
                }
                letIdNameSN(listName, s);
            } /*
             pipeOp
             generateStmtOp
             generatorRefOp
             "each"(2:15)
             "n"(2:18)
             subscriptOp
             "a"(2:27)
             numberOp
             1.0(2:26)

             */ else if (testOp(pipeline.left.right, OpVal.subscriptOp)) {
                // Create a list by evaling the expression
                listName = "$temp" + tempNo;
                StickyNote s = evalExpression(pipeline.left.right);
                if (s.getVal() instanceof StringVal) { // change it to a list
                    String source = s.toString();
                    Node firstNode = Node.str2Node(source);
                    s = new StickyNote(firstNode);
                }
                if (s.val instanceof GenesisList) // bizarre patch needed
                {
                    genesisListFound = true;
                }
                letIdNameSN(listName, s);
            } else if (testOp(pipeline.left.right, OpVal.stringOp)) {
                //System.out.println ("Processing a string as a list");
                listName = "$temp" + tempNo;
                StickyNote s = evalExpression(pipeline.left.right);

                String source = s.toString();
                Node firstNode = Node.str2Node(source);
                s = new StickyNote(firstNode);
                if (s.val instanceof GenesisList) // bizarre patch needed
                {
                    genesisListFound = true;
                }
                letIdNameSN(listName, s);

            } else {
                listName = pipeline.left.right.info.toString(); // list to iterate across
            }

            // System.out.println("evalPipe: listName=" +listName);
            if (genesisListFound) {
                sn = scope.find(listName); // SN of first node of the list
            } else {
                sn = scope.search(listName); // SN of first node of the list
            }          // System.out.println("Evaluating pipeline using " + listName);
            if (sn == null) {  // treat whatever is there as a string and explode it
                printError("" + evalExpression(pipeline.left.right) + " does not name a list", pipeline.left.right);
            }
            if (sn.getVal() instanceof StringVal) { // Convert it to a string
                String source = sn.toString();
                Node firstNode = Node.str2Node(source);
                stringName = listName;
                sn = new StickyNote(firstNode);
            }
            GenesisVal gv = sn.getVal(); // first node of the list
            // System.out.println("Evaluating pipeline using " + gv);
            if (gv != null && !(gv instanceof Node) && !(gv instanceof GenesisList)) {
                printError("" + listName + " does not name a list " + gv.getClass(), pipeline.left.right);
            }

            //System.out.println ("Interpreting Generate " + iteratorName + " from " + listName);
            if (gv instanceof GenesisList) {
                list = (GenesisList) gv;// new GenesisList ((GenesisList)gv);
            } else {
                // Utility.println ("Creating new list from " + sn);
                //printNodes(sn);
                list = new GenesisList(sn);
                //Utility.println ("Created new list ");
                // list.printNodes();
            }

            // System.out.println (listName + "=");
            // Utility.DEBUG = true;
            // list.displayln();
            scope.setIterator(iteratorName, list); //assoc. iteratorName w/ the list
            //System.out.println("s " + iteratorName + " a GenesisList? ");
            //System.out.println (scope.search(iteratorName).getVal());
            //GenesisList xxx = (GenesisList) (scope.search(iteratorName).getVal());
            // System.out.println(""+ (temp  instanceof Iterator ));
            // System.out.println("terator found");
            // Now begin the interpretation
            //Interpret each filter's initialization
            Node guardedFilter = pipeline.right();
            Node filter;
            while (guardedFilter != null) {
                filter = guardedFilter.left.right;
                result = evalStmt(filter.left());
                guardedFilter = guardedFilter.right();
            }
            // Check to see if there is a `while' condition
            boolean condition;
            Node cond = pipeline.left.right.right;
            if (cond == null) { // nope, not there
                condition = true;
            } else {
                condition = list.on() && evalCondition(cond.left);
            }

            // Interpret each body, while iterating through 'list'
            int i = 1;
            // list.reset();
            while (list.on() && condition) {
                // Interpret each body
                // System.out.println("Processing filter for: " + list);
                scope.setIterator(iteratorName, list);
                // System.out.println("Using iterator" + scope.search(iteratorName));
                guardedFilter = pipeline.right;
                StickyNote current = list.get();
                while (guardedFilter != null) {
                    //System.out.println("Calling evalStmt");
                    filter = guardedFilter.left.right;
                    Node guard = guardedFilter.left.left;
                    if (guard == null || evalCondition(guard)) {
                        result = evalStmt(filter.left().right());
                    }
                    // list = (GenesisList) scope.searchRef(iteratorName).val;
                    //scope.name(listName,list);
                    guardedFilter = guardedFilter.right();
                }
                // Don't move if the current node of the list has been modified
                // due to a deletion of the current node
                // 
                // System.out.println ("Iterator is:");

                //list = (GenesisList) scope.searchRef(iteratorName).val;
                //scope.name(listName,list);

                if (list.on() && list.get() == current) { // ljm: 8/28/04
                    list.move();
                    // System.out.println("Moving ... current node not modified");
                }
                //else
                //System.out.println("Not moving ... current node modified");
                if (cond == null) { // nope, not there
                    condition = true;
                } else {
                    condition = list.on() && evalCondition(cond.left);
                    //System.out.println("condition:" + condition);
                }

                //System.out.println("Condition is " + condition);
            }

            //Interpret each filter's finalization
            // System.out.println("Processing finalization");
            guardedFilter = pipeline.right;
            while (guardedFilter != null) {
                filter = guardedFilter.left.right;
                result = evalStmt(filter.left().right().right());
                guardedFilter = guardedFilter.right();
            }
            //if (list.off())     
            //  scope.del(iteratorName);  // ljm: allow for persistence when still on the list
            // Now if the original list was a string, put it back 
            if (!"".equals(stringName)) {
                //System.out.println("list.pos:" + list.pos());
                letIdNameSN(stringName, new StickyNote(list.implode()));
            }
        } else {
            printError("Malformed pipeline: expecting a Generate statement ", tn);
        }
        return result;
    }//end evalPipe

    StickyNote evalWhileStmt(Node tn) {
        //System.out.println ("Evaluating while statement" + tn.left.right);

        StickyNote result = null;
        Node guardedFilter = tn.left().right();
        Node filter;
        while (guardedFilter != null) {
            filter = guardedFilter.left.right;
            result = evalStmt(filter.left());
            guardedFilter = guardedFilter.right();
        }
        // Check to see if there is a `while' condition
        Node cond = tn.left;
        // Interpret each body, while iterating through 'list'
        int i = 1;
        // list.reset();
        while (evalCondition(cond)) {
            // Interpret each body
            // System.out.println("Processing filter for: " + list);
            // System.out.println("Using iterator" + scope.search(iteratorName));
            guardedFilter = tn.left.right;
            while (guardedFilter != null) {
                //System.out.println("Calling evalStmt");
                filter = guardedFilter.left.right;
                Node guard = guardedFilter.left.left;
                if (guard == null || evalCondition(guard)) {
                    result = evalStmt(filter.left().right());
                }
                guardedFilter = guardedFilter.right();
            }
        }

        //Interpret each filter's finalization
        // System.out.println("Processing finalization");
        guardedFilter = tn.left().right;
        while (guardedFilter != null) {
            filter = guardedFilter.left.right;
            result = evalStmt(filter.left().right().right());
            guardedFilter = guardedFilter.right();
        }
        return result;
    } //evalWhileStmt

    @SuppressWarnings({"empty-statement", "empty-statement"})
    StickyNote evalUserPipe(Node tn) {
        if (debug) {
            System.out.println("Entering evalUserPipe with " + tn);
        }
        StickyNote returnVal = null;  // To store the returned value
        StickyNote result = null;
        if (testOp(tn.left, OpVal.generatorCallOp)) {
            Node pipeline = tn.left;
//debug = true;
            // first, build the mangled name and collect the parameters
            String fname = buildMangledFunctionCallName(pipeline);
            if (debug) {
                System.out.println("fname = " + fname);
            }
            // now, set up scope
            //
            // this copies all function entries, then sets the lexical parent
            Scope myScope = new Scope(scope);
            myScope.deleteAllVars();

            // now the parameters
            //
            // grab the call 
            Node call = tn.left().left; // skip to the generator invocation
            //debug=true;
            if (debug) {
                System.out.println("Grabbing definition for  " + fname);
            }

            // next, grab the definition
            StickyNote temp = scope.name(fname);
            // temp refers to the subtree rooted at generatorDefOp
            // System.out.println("Got back");
            if (temp == null) {
                printError("The function " + fname + " has not been defined", tn);
            }
            Node def = (Node) (temp.val);

            if (debug) {
                System.out.println("1. Got to here with fname=" + fname);
            }
            if (debug) {
                System.out.println("def is " + def.info + def.info.getClass());
            }

            def = def.left().left(); //skip to the function signature

            // walk through the headers linking the parameters
            while (call != null && def != null) {
                if (testType(call, OpVal.parameterOp)) {
                    if (debug) {
                        System.out.println("Formal parameter is '" + def.left.info.val.toString() + "'");
                    }
                    if (debug) {
                        System.out.println("Actual parameter is '" + evalExpression(call.left) + "'");
                    }
                    // Loop through ever parameter (ljm: 8/02/04)
                    TreeNode formalParam = (TreeNode) def.left();
                    TreeNode actualParam = (TreeNode) call.left();
                    while (formalParam != null && actualParam != null) {
                        if (testType(formalParam, OpVal.aliasOp)) {
                            // letIdAliasSN (myScope, formalParam.left().info.val.toString(), evalExpression( actualParam ) );
                            // See if the calling param is an id and is in the scope. If not add it to the scope
                            if (actualParam.info.val instanceof StringVal) {
                                StickyNote n = scope.find(actualParam.info.val.toString());  // SN assoc with id
                                if (n == null) {
                                    scope.setName(actualParam.info.val.toString(), new StickyNote());
                                }
                            }

                            myScope.alias(formalParam.left().info.val.toString(), evalExpression(actualParam));
                        } else {
                            StickyNote sn = evalExpression(actualParam);;
                            myScope.setName(formalParam.info.val.toString(), sn);
                            //letIdNameSN (myScope, 
                            //           formalParam.info.val.toString(), 
                            //          evalExpression( actualParam ) );
                            //String id = actualParam.info.val.toString();
                        }
                        formalParam = (TreeNode) formalParam.right();
                        actualParam = (TreeNode) actualParam.right();
                    }
                    if (formalParam != null) {
                        printError("More formal parameters than actual parameters", tn);
                    }
                    if (actualParam != null) {
                        printError("More actual parameters than formal parameters", tn);
                    }
                    //System.out.println("Passing " + evalExpression(call.left));
                }
                def = (TreeNode) def.right();
                call = (TreeNode) call.right();
            }
            // Now, we should have all the parameters linked.
            if (debug) {
                System.out.println("Parameters matched");
            }
            Scope saveScope;
            boolean condition;  // Should we keep going?

            def = (Node) (temp.val);
            Node genfirst = def.left.right.left;  // generator's @first:steps
            Node geniter = genfirst.right;        // generator's @iter: steps
            Node genlast = geniter.right;         // generator's @last: steps

            Node itercond = pipeline.left.right;  // iteration condition (if any)
            // Now advance it  until a while is found
            while (itercond != null && !testOp(itercond, OpVal.whileOp)) {
                itercond = itercond.right;
                if (debug) {
                    System.out.println("Evaling in loop:" + itercond);
                }
            }

            if (debug) {
                System.out.println("Parameters matched");
            }
            // 1. Execute the generator's initialization
            if (debug) {
                System.out.println("Generator @first");
            }
            def = (Node) (temp.val);
            Scope savedScope = scope;
            scope = myScope;
            evalStmt(genfirst);
            scope = savedScope;

            // 2. Run each task's initialization
            // Now begin the interpretation
            //Interpret each filter's initialization
            if (debug) {
                System.out.println("Task:init");
            }
            Node guardedFilter = pipeline.right();
            Node filter;
            while (guardedFilter != null) {
                filter = guardedFilter.left.right;
                result = evalStmt(filter.left());
                guardedFilter = guardedFilter.right();
            }

            // 3. Run the generator's "Are we done?" code
            if (debug) {
                System.out.println("Generator @last");
            }
            savedScope = scope;  // save the scope
            scope = myScope;
            StickyNote cond = evalStmt(genlast);
            if (debug) {
                System.out.println("@last truth value" + cond);
            }
            if (!(cond.val instanceof TruthVal)) {
                printError("@last in a generator must return a truth value", genlast);
            }
            condition = ((TruthVal) cond.val).val;
            scope = savedScope;  // restore scope
            if (debug) {
                System.out.println("Generator condition = " + condition);
            }

            // 4. Evaluate the condition in the global environment
            // Check to see if there is a `while' condition
            if (condition) // Short circuit the evaluation of the itercond
            {
                if (itercond != null) { // nope, not there
                    condition = evalCondition(itercond.left);
                }
            }
            if (debug) {
                System.out.println("Iteration condition = " + condition);
            }

            // 5.   if not, then run each task's iteration code and
            //continue with step 3
            if (debug) {
                System.out.println("Iterator @iter");
            }
            // Interpret each body, while iterating 
            int i = 1;
            while (condition) {
                // Interpret each body
                // System.out.println("Processing filter for: " + list);
                guardedFilter = pipeline.right;
                while (guardedFilter != null) {
                    //System.out.println("Calling evalStmt");
                    filter = guardedFilter.left.right;
                    Node guard = guardedFilter.left.left;
                    if (guard == null || evalCondition(guard)) {
                        result = evalStmt(filter.left().right());
                    }
                    guardedFilter = guardedFilter.right();
                }
                savedScope = scope;
                scope = myScope;
                evalStmt(geniter);
                scope = savedScope;
                if (debug) {
                    System.out.println("Evaluating condition");
                }
                // Check to see if were done ... same as code before the loop
                // 3. Run the generator's "Are we done?" code
                if (debug) {
                    System.out.println("Generator @last");
                }
                savedScope = scope;  // save the scope
                scope = myScope;
                cond = evalStmt(genlast);
                if (debug) {
                    System.out.println("@last truth value" + cond);
                }
                if (!(cond.val instanceof TruthVal)) {
                    printError("@last in a generator must return a truth value", genlast);
                }
                condition = ((TruthVal) cond.val).val;
                scope = savedScope;  // restore scope
                if (debug) {
                    System.out.println("Generator condition = " + condition);
                }

                // 4. Evaluate the condition in the global environment
                // Check to see if there is a `while' condition
                if (condition) // Short circuit the evaluation of the itercond
                {
                    if (itercond != null) { // nope, not there
                        condition = evalCondition(itercond.left);
                    }
                }
                if (debug) {
                    System.out.println("Iteration condition = " + condition);
                }
            }

            //Interpret each filter's finalization
            // System.out.println("Processing finalization");
            guardedFilter = pipeline.right;
            while (guardedFilter != null) {
                filter = guardedFilter.left.right;
                result = evalStmt(filter.left().right().right());
                guardedFilter = guardedFilter.right();
            }
        } else {
            printError("Malformed pipeline: expecting a Generate statement ", tn);
        }
        return result;
    }//end evalPipe

    Node traverse(Node root, Node key) {
        // System.out.println("Traversing " + new StickyNote(root) + " " + root);
        // System.out.println("Looking for " + new StickyNote(key) + " "  + key);
        Node n = root;
        Node result = null;
        while (n != null) {
            GenesisVal gv = n.info.getVal();
            // System.out.println("Looping through  " + new StickyNote(n));
            if (gv instanceof Node) {
                if (((Node) gv) == key) {
                    return (Node) n;
                } else {
                    result = traverse((Node) gv, key);
                    if (result != null) {
                        return result;
                    }
                }
            }
            n = n.right;
        }
        // System.out.println("Returning " + result);
        return result;
    }//end traverse

    StickyNote evalInsertBefore(Node tn) {
        if (debug) {
            Parser.prettyPrint((TreeNode) tn);
        }
        Node exp = tn.left;
        StickyNote expVal = new StickyNote(evalExpression(exp));
        // String iter = tn.left.right.getVal().toString();   // iterator to use for the position
        //System.out.println("insert " + expVal + " before ");
        GenesisList current = evalRef(tn.left.right);
        //current.prev();
        current.insert(expVal);
        /*
         if (expVal.getLabel() == null )
         current.insert(expVal);
         else {
         // System.out.println("label is " + exp.getLabel());
         // System.out.println("expVal is " + expVal);
         current.insert( expVal, getLabel(exp) );
         }
         */
        //current.move();
        return expVal;
    }//end evalInsertBefore
/*
     insertAfterOp
     Value to be inserted
     Iterator desiginating the position after which to insert
     */

    StickyNote evalInsertAfter(Node tn) {
        if (debug) {
            Parser.prettyPrint((TreeNode) tn);
        }
        Node exp = tn.left;
        GenesisList current = evalRef(exp.right);
        StickyNote expVal = new StickyNote(evalExpression(exp));
        // String iter = tn.left.right.getVal().toString();   // iterator to use for the position
        //System.out.println("insert " + expVal + " after ");
        current.move();
        current.insert(expVal);
        /* 
         if (exp.getLabel() == null )
         current.insert(expVal);
         else {
         // System.out.println("label is " + exp.getLabel());
         // System.out.println("expVal is " + expVal);
         current.insert( expVal, getLabel(exp) );
         }
         */
        //current.insert(expVal);
        current.prev();  // -- unnecessary ... current is local????
        current.prev();  // -- unnecessary ... current is local????
        return expVal;
    }//end evalInsertAfter

    StickyNote evalDelete(Node tn) {
        // debug = true;
        if (debug) {
            System.out.println("Calling Delete with");
            Parser.prettyPrint((TreeNode) tn);
        }
        GenesisList current = evalRef(tn.left);

        // System.out.println("Before deleteing");
        // System.out.println("current.parent().addr() = " + current.parent().addr());
        // System.out.println("current.current().val.addr() = " + current.current().addr());
        //current.displayln();
        // String iter = tn.left.right.getVal().toString();   // iterator to use for the position
        // current.displayln();
        current.del();
        // System.out.println("After deleteing");
        // current.displayln();
        // System.out.println("l's val.addr() = " + scope.search("l").val.addr());
        // System.out.println("current.parent().addr() = " + current.parent().addr());
        // System.out.println("current.current().val.addr() = " + current.current().addr());
        // current.displayln();
        // if (tn.left
        return null;
    }//end evalDelete

    /*
     evalAppend: 
     This uses a difficult-to-remember representation trick.
     Suppose you say:

     Append "x" onto L
     vs
     Append "x" onto L[3]

     In the latter case the GenesisList that is returned is positioned
     at position 3 in the list; in the former case the Genesis list
     that is returned from evalRef(lst) is moved off the end of the list
     to reflect the fact that we are at the top-level list. 

     All this because Java does not have call-by-reference parameters!
     */
    // evalAppend -- append exp onto list-exp
    StickyNote evalAppend(Node tn) {

        Node exp = tn.left;
        Node lst = tn.left.right;
        StickyNote expVal = new StickyNote(evalExpression(exp));
        GenesisList current = evalRef(lst);
        StickyNote mycurrent = evalExpression(lst);
        // System.out.println("Appending " + expVal + " onto "); current.displayln();
        if (current.on()) { // list-exp is subscripted 
            // or accessed through an iterator
            StickyNote sn = current.get();
            if (!(sn.val instanceof Node)
                    && !(sn.val instanceof GenesisList)
                    && (sn.val != null)) {
                printError("Attempting to append onto non-list '"
                        + sn);
            }
            current = new GenesisList(sn);
        }
        while (current.on()) {
            current.move();
        }  // move to end of list
        //System.out.println("Appending " + expVal + " onto " + mycurrent);
        //GenesisVal label = expVal.getLabel();
        // System.out.println ("evalAppend: appending " + expVal.getLabel() + expVal);
        current.insert(expVal);  // insert it with or without label
     /*
         if (label == null )
         current.insert(expVal);
         else {
         // System.out.println("label is " + exp.getLabel());
         // System.out.println("expVal is " + expVal);
         current.insert( expVal, getLabel(exp) );
         }
         */
        current.reset();
        // out.println("got here");
        // current.displayln();
        // out.println("got there");
        current.get();
        return evalExpression(lst);
    }//end evalAppend

    // See notes above for evalAppend
    StickyNote evalPrepend(Node tn) {
        Node exp = tn.left;
        Node lst = tn.left.right;
        StickyNote expVal = new StickyNote(evalExpression(exp));
        GenesisList current = evalRef(lst); // node of the list

        // System.out.println("Prepending " + expVal + " onto "); current.displayln();
        if (current.on()) { // list-exp is subscripted 
            // or accessed through an iterator
            StickyNote sn = current.get();
            if (!(sn.val instanceof Node)
                    && !(sn.val instanceof GenesisList)
                    && (sn.val != null)) {
                printError("Attempting to prepend onto non-list '"
                        + sn);
            }
            current = new GenesisList(sn);
            letIdNameSN(lst.toString(), new StickyNote(current.current()));
        }
        current.reset();  // move to front end of list
        current.insert(expVal);
        current.reset();
        //current.prepend(expVal);
        letIdNameSN(lst.toString(), new StickyNote(current.current()));
        return evalExpression(lst);
        //return expVal;

    }//end prepend

    /*
     StickyNote evalInsertBefore( Node tn ) {
     if (debug) Parser.prettyPrint((TreeNode)tn);
     Node exp = tn.left;
     StickyNote expVal =new StickyNote(evalExpression(exp));

     // String iter = tn.left.right.getVal().toString();   // iterator to use for the position
     GenesisList current = evalRef(tn.left.right);
     current.reset();
     // System.out.println("insert " + expVal + " before " + current );
     //current.move();
     //current.prev();
     if (exp.getLabel() == null )
     current.insert(expVal);
     else {
     // System.out.println("label is " + exp.getLabel());
     // System.out.println("expVal is " + expVal);
     current.insert( expVal, exp.getLabel() );
     }
     //current.move();
     // System.out.println("Gives " + current );
     // System.out.println("Gives " + tn.left.right );
     current.reset();
     letIdNameSN(tn.left.right.toString(),new StickyNote(current.current()));
  
     
     return expVal;
     */
    void evalStop(Node tn) {
        System.out.println("Stop statement encountered at line "
                + ((TreeNode) tn).context.lineNo);
        System.exit(0);
    }//end evalStop

    StickyNote evalUnalias(Node tn) {

        if (debug) {
            Parser.prettyPrint((TreeNode) tn);
            System.out.println("Unaliasing " + tn.left.info.toString());
        }
        return scope.del(tn.left.info.toString());
    }//end evalUnalias
}
