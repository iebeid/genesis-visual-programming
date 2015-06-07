// GenesisInterpreter.java
// Wes Potts
// 6/14/04
//
// This file implements the Genesis interpreter.
//
// Modification History
// Date    Modification
// 8/27/04  ljm: Added -v option for printing the current version
// 11/05/04 ljm: Added procesing multiple files on the command line
// 5/29/06  ljm: Extended so algorithms can be supplied via stdin
// 8/1/06   ljm: Set flags to be proceeded by '-' rather than '+'
// 3/8/07   ljm: Modified output format to move the "------------"


import java.io.*;
import java.util.InputMismatchException;
import java.util.Scanner;
public class GenesisInterpreter
{
  Parser p;
  Evaluator e;
  public  static Scanner scanner;
  GenesisInterpreter(String infile, String outfile)
  {
    p = new Parser(infile);
    e = new Evaluator();
  }
  public void setSource( String filename )
  {
    //io.setInputFile(filename );
    p.setSource(filename);
  }

  public static TreeNode tn; // root of the whole AST
  public static void main ( String args[] )
  {
    int filevar = 0;
    boolean header = true;
    while (filevar < args.length && args[filevar].charAt(0) == '-') { // commandline argument
       filevar++;
       if (args[0].equals("-d")) {
          Evaluator.debug = true;
       }
       else if (args[0].equals("-t")) {
          Evaluator.trace = true;
       }
       else if (args[0].equals("-h")) {
          header = false;
       }
       else if (args[0].equals("-v")) {
          int version=0;
          int revision=80;  // leave formatted as this!
          if (revision > 99) {
             revision = revision-99;
             version=1;
          }
          System.out.println("Genesis version "+version+"."+revision);
          System.exit(0);
       }
       else {
          System.out.println("Unknown commandline option:" +args[0] + "... skipping");
       } 
    }
    GenesisInterpreter interpreter; 
    if (filevar >= args.length) {

       //System.out.println("Usage: java GenesisInterpreter filename.gen");
       //System.exit(1);
       interpreter = new GenesisInterpreter("","");
       interpreter.setSource("");
    }
    else {
       interpreter = new GenesisInterpreter(args[filevar],"");
       while (filevar < args.length) {
          interpreter.setSource(args[filevar]);
          filevar++;
       }
    }
    tn = interpreter.p.parse();
    if (header) {
       System.out.println("Following algorithm\n" 
                             +"--------------------" 
                             );
    }
    if (Evaluator.trace) {
       tn.numberTree();
       System.out.println(tn.toString());
       System.out.println("Enter line numbers where you would like the interpreter to pause.");
       System.out.println("Typing no numbers implies you want to stop after every statement.");
       System.out.println("Terminate list with eof (^D or ^Z).");
       boolean tryAgain;
       scanner = new Scanner(System.in);
       boolean moreInput = true;
       boolean empty = true;
       while (moreInput) {
          do {
             tryAgain=false;
             try {
                Integer bp = scanner.nextInt(); 
                if (! Evaluator.stopAt.contains (bp))  {
                   Evaluator.stopAt.add(bp);  // append stopping number onto the end
                   empty = false;
                }    
                // System.out.println ("bp = " + bp);
             }
             catch (InputMismatchException ime) {
                System.out.println ("Input must be a number; try again: ");
                scanner.nextLine();
                tryAgain = true;
             }
             catch (Exception ie) {
                // eof
                moreInput = false;
                // System.out.println( "Eof encountered");
             }
          } while  (tryAgain);
       }
       if (empty)
          Evaluator.stopAt.add(0);  // default to a 0
    }
    try {
    interpreter.e.evalProgram( tn );
    }
    catch ( Exception e) {
       Evaluator.printError("Your algorithm is incorrect in some unknown way.\n"
         + "> If you have the time, please mail a copy of your algorithm to:\n"
         + ">\n>      morell@cs.atu.edu\n\n"
         + "> Include in your mail the version of Genesis you are running.\n"
         + "> To obtain this type:\n"
         + ">    run -v   // on a Unix-like system\n"
         + "> or\n"
         + ">    run +v   // on a Windows system\n"
         + "\n>\n> Thanks!");
    }
    if (header) {
       System.out.println("--------------------"
                            +"\nDone following algorithm.\n" 
                            + Evaluator.stmtCount + " steps run.");
    }
  }
}
