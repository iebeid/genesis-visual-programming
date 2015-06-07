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
package edu.genesis.runtime;

import edu.genesis.view.GenesisDevelopmentEnvironmentViewController;
import java.util.InputMismatchException;
import java.util.Scanner;

public class GenesisInterpreter extends GenesisDevelopmentEnvironmentViewController {

    public Parser p;
    public Evaluator e;
    public EvaluatorXML ax;
    Scope s;
    public static Scanner scanner;
    public static TreeNode tn; // root of the whole AST

    public GenesisInterpreter(String infile, String outfile) {
        p = new Parser(infile);
        e = new Evaluator();
        ax = new EvaluatorXML();
    }

    public void setSource(String filename) {
        p.setSource(filename);
    }
    public static void callGenesis(String args[]) {
        int filevar = 0;
        boolean header = true;
        while (filevar < args.length && args[filevar].charAt(0) == '-') { // commandline argument
            filevar++;
            if (args[0].equals("-d")) {
                Evaluator.debug = true;
            } else if (args[0].equals("-t")) {
                Evaluator.trace = true;
            } else if (args[0].equals("-h")) {
                header = false;
            } else if (args[0].equals("-v")) {
                int version = 0;
                int revision = 80;  // leave formatted as this!
                if (revision > 99) {
                    revision = revision - 99;
                    version = 1;
                }
                System.out.println("Genesis version " + version + "." + revision);
                outputArea.appendText("Genesis version " + version + "." + revision + "\n");
                System.exit(0);
            } else {
                System.out.println("Unknown commandline option:" + args[0] + "... skipping");
                outputArea.appendText("Unknown commandline option:" + args[0] + "... skipping\n");
            }
        }
        GenesisInterpreter interpreter = null;
        if (filevar >= args.length) {
        } else {
            interpreter = new GenesisInterpreter(args[filevar], "");
            while (filevar < args.length) {
                interpreter.setSource(args[filevar]);
                filevar++;
            }
        }
        tn = interpreter.p.parse();
        if (header) {
            System.out.println("Following algorithm\n"
                    + "--------------------");
            outputArea.appendText("Following algorithm\n"
                    + "--------------------\n");
        }
        if (Evaluator.trace) {
            tn.numberTree();
            System.out.println(tn.toString());
            outputArea.appendText(tn.toString());
            System.out.println("Enter line numbers where you would like the interpreter to pause.\n");
            outputArea.appendText("Enter line numbers where you would like the interpreter to pause.\n");
            System.out.println("Typing no numbers implies you want to stop after every statement.\n");
            outputArea.appendText("Typing no numbers implies you want to stop after every statement.\n");
            System.out.println("Terminate list with eof (^D or ^Z).\n");
            outputArea.appendText("Terminate list with eof (^D or ^Z).\n");
            boolean tryAgain;
            scanner = new Scanner(System.in);
            boolean moreInput = true;
            boolean empty = true;
            while (moreInput) {
                do {
                    tryAgain = false;
                    try {
                        Integer bp = scanner.nextInt();
                        if (!Evaluator.stopAt.contains(bp)) {
                            Evaluator.stopAt.add(bp);  // append stopping number onto the end
                            empty = false;
                        }
                    } catch (InputMismatchException ime) {
                        System.out.println("Input must be a number; try again: ");
                        outputArea.appendText("Input must be a number; try again: \n");
                        scanner.nextLine();
                        tryAgain = true;
                    } catch (Exception ie) {
                        // eof
                        moreInput = false;
                    }
                } while (tryAgain);
            }
            if (empty) {
                Evaluator.stopAt.add(0);  // default to a 0
            }
        }
        try {
            interpreter.e.evalProgram(tn);
        } catch (Exception e) {
            Evaluator.printError("Your algorithm is incorrect in some unknown way.\n"
                    + "> If you have the time, please mail a copy of your algorithm to:\n"
                    + ">\n>      morell@cs.atu.edu\n\n"
                    + "> Include in your mail the version of Genesis you are running.\n"
                    + "> To obtain this type:\n"
                    + ">    run -v   // on a Unix-like system\n"
                    + "> or\n"
                    + ">    run +v   // on a Windows system\n"
                    + "\n>\n> Thanks!");
            outputArea.appendText("Your algorithm is incorrect in some unknown way.\n"
                    + "> If you have the time, please mail a copy of your algorithm to:\n"
                    + ">\n>      morell@cs.atu.edu\n\n"
                    + "> Include in your mail the version of Genesis you are running.\n"
                    + "> To obtain this type:\n"
                    + ">    run -v   // on a Unix-like system\n"
                    + "> or\n"
                    + ">    run +v   // on a Windows system\n"
                    + "\n>\n> Thanks!\n");
        }
        if (header) {
            System.out.println("--------------------"
                    + "\nDone following algorithm");
            outputArea.appendText("--------------------"
                    + "\nDone following algorithm\n");
        }
    }

    public static void callGenesisXML(String args[]) {
        int filevar = 0;
        boolean header = true;
        while (filevar < args.length && args[filevar].charAt(0) == '-') { // commandline argument
            filevar++;
            if (args[0].equals("-d")) {
                EvaluatorXML.debug = true;
            } else if (args[0].equals("-x")) {
                EvaluatorXML.xml = true;
            } else if (args[0].equals("-t")) {
                EvaluatorXML.trace = true;
            } else if (args[0].equals("-h")) {
                header = false;
            } else if (args[0].equals("-v")) {
                int version = 0;
                int revision = 80;  // leave formatted as this!
                if (revision > 99) {
                    revision = revision - 99;
                    version = 1;
                }
                outputArea.appendText("Genesis version " + version + "." + revision + "\n");
                System.exit(0);
            } else {
                outputArea.appendText("Unknown commandline option:" + args[0] + "... skipping\n");
            }
        }
        GenesisInterpreter interpreter = null;
        if (filevar >= args.length) {
        } else {
            interpreter = new GenesisInterpreter(args[filevar], "");
            while (filevar < args.length) {
                interpreter.setSource(args[filevar]);
                filevar++;
            }
        }
        tn = interpreter.p.parse();
        if (header) {
            outputArea.appendText("Following algorithm\n"
                    + "--------------------\n");
        }
        if (EvaluatorXML.trace) {
            tn.numberTree();
            outputArea.appendText(tn.toString() + "\n");
            outputArea.appendText("Enter line numbers where you would like the interpreter to pause.\n");
            outputArea.appendText("Typing no numbers implies you want to stop after every statement.\n");
            outputArea.appendText("Terminate list with eof (^D or ^Z).\n");
            boolean tryAgain;
            scanner = new Scanner(System.in);
            boolean moreInput = true;
            boolean empty = true;
            while (moreInput) {
                do {
                    tryAgain = false;
                    try {
                        Integer bp = scanner.nextInt();
                        if (!EvaluatorXML.stopAt.contains(bp)) {
                            EvaluatorXML.stopAt.add(bp);  // append stopping number onto the end
                            empty = false;
                        }
                    } catch (InputMismatchException ime) {
                        outputArea.appendText("Input must be a number; try again: \n");
                        scanner.nextLine();
                        tryAgain = true;
                    } catch (Exception ie) {
                        // eof
                        moreInput = false;
                    }
                } while (tryAgain);
            }
            if (empty) {
                EvaluatorXML.stopAt.add(0);  // default to a 0
            }
        }
        try {
            interpreter.ax.evalProgram(tn);
        } catch (Exception e) {
            e.printStackTrace();
            EvaluatorXML.printError("Your algorithm is incorrect in some unknown way.\n"
                    + "> If you have the time, please mail a copy of your algorithm to:\n"
                    + ">\n>      morell@cs.atu.edu\n\n"
                    + "> Include in your mail the version of Genesis you are running.\n"
                    + "> To obtain this type:\n"
                    + ">    run -v   // on a Unix-like system\n"
                    + "> or\n"
                    + ">    run +v   // on a Windows system\n"
                    + "\n>\n> Thanks!");
            outputArea.appendText("Your algorithm is incorrect in some unknown way.\n"
                    + "> If you have the time, please mail a copy of your algorithm to:\n"
                    + ">\n>      morell@cs.atu.edu\n\n"
                    + "> Include in your mail the version of Genesis you are running.\n"
                    + "> To obtain this type:\n"
                    + ">    run -v   // on a Unix-like system\n"
                    + "> or\n"
                    + ">    run +v   // on a Windows system\n"
                    + "\n>\n> Thanks!\n");
        }
        if (header) {
            outputArea.appendText("--------------------"
                    + "\nDone following algorithm\n");
        }
    }
}
