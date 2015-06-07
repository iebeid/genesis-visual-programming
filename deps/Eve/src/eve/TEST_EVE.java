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
package eve;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import parsing.GenesisScanner;
import parsing.Grammar;
import parsing.Instruction;
//import OriginalRuntime.EnvironmentVal;
//import OriginalRuntime.Value;
import runtime.LocalEnvironmentVal;
import runtime.StickyNote;
/**
 *
 * @author Larry Morell
 */
public class TEST_EVE {

	public static void run(Instruction instr) {
		instr = instr.createInstruction(null);
		instr.exec();
	}
	private static void loadClasses() {
		// Temporarily do something dumb to ensure each of the
		// classes are loaded.  This will be replaced by something more elegant
		// later, probably using reflection

// Load elementary syntactic units
		Invocation_Id.initialize();
//
		Invocation_Define.initialize();
		Boolean_False.initialize();
		Boolean_True.initialize();

 		EveId.initialize();
		EveNumber.initialize();
		EveString.initialize();
//      ALiteral.initialize();
		Primary_Boolean.initialize();
	   Primary_Invocation.initialize();
		Primary_Number.initialize();
		Primary_String.initialize();

		InvocationList.initialize();
		InvocationList_Invocation.initialize();

		UnsignedFactor.initialize();
		UnsignedFactor_InvocationList.initialize();

		Factor.initialize();
		Factor_Unary.initialize();
		UnaryOp_Minus.initialize();
		UnaryOp_Plus.initialize();
		UnaryOpList.initialize();
		UnaryOpList_Multiple.initialize();

		Parameter_Factor.initialize();

		Separator_Comma.initialize();
		Separator_Semicolon.initialize();

		ParameterList.initialize();
		ParameterList_Empty.initialize();
		ParameterList_Parameter.initialize();

		Invocation.initialize();

		FormalParameter.initialize();
		FormalParameterList.initialize();
		FormalParameterList_Empty.initialize();
		FormalParameterList_FormalParameter.initialize();

		FunctionDefinition.initialize();
//		Invocation_Empty.initialize();

		Expression_Term.initialize();
		Expression_FunctionDefinition.initialize();
		Term_Factor.initialize();
		ExpressionList.initialize();
		ExpressionList_Empty.initialize();
		ExpressionList_Expression.initialize();
				  

	}


  /**
	 * Fetch the entire contents of a text file, and return it in a String.
	 * This style of implementation does not throw Exceptions to the caller.
	 * Adapted from  http://www.javapractices.com/topic/TopicAction.do?Id=42
  	 *
	 * @param aFile is a file which already exists and can be read.
  */
  static public String readFile(File aFile) {
    //...checks on aFile are elided
    StringBuilder contents = new StringBuilder();

    try {
      //use buffering, reading one line at a time
      //FileReader always assumes default encoding is OK!
      BufferedReader input =  new BufferedReader(new FileReader(aFile));
      try {
        String line = null; //not declared within while loop
        /*
        * readLine is a bit quirky :
        * it returns the content of a line MINUS the newline.
        * it returns null only for the END of the stream.
        * it returns an empty String if two newlines appear in a row.
        */
        while (( line = input.readLine()) != null){
          contents.append(line);
          contents.append(System.getProperty("line.separator"));
        }
      }
      finally {
        input.close();
      }
    }
    catch (IOException ex){
		 System.out.println(ex);
		 System.exit(1);
    }

    return contents.toString();
  }
	public static void main(String[] args) {
		// Temporarily do something dumb to ensure each of the
		// classes are loaded  This will be replaced by something more elegant
		// later, probably using reflection
	   loadClasses();
		EveInstruction.getGrammar().setStart("<ExpressionList>");
      //EveInstruction.getGrammar().setStart("<ParameterList>");
		// Now create a scanner
		Grammar grammar = EveInstruction.getGrammar();
		GenesisScanner scanner ;

	//scanner = new GenesisScanner(grammar,"- - -3, 4");]
   scanner = new GenesisScanner(grammar,"dog");

			//		scanner = new GenesisScanner(grammar, "_list(dog,_list(dog,cat),6)");
			//		scanner = new GenesisScanner(grammar, "_sub_number (8,9)");
			//		scanner = new GenesisScanner(grammar, "_sub_number (dog,9)");
			//		scanner = new GenesisScanner(grammar,"_add_number ( _sub_number(dog,8), _sub_number(dog,3))");
			//   scanner = new GenesisScanner(grammar,"false");
			//		scanner = new GenesisScanner(grammar, "define ( worm , define (worm,33) )");
			//	     scanner = new GenesisScanner (grammar, "define (worm, 99)");
			//		scanner = new GenesisScanner(grammar,"_list(_list(p1,p2,p3),_list(dog,cat,pig))");
			//		scanner = new GenesisScanner(grammar,
			//				  "define(f,_("
			//				                 + "_(p1),"
			//				                 + "_(_print(p1))"
			//								  + ")"
			//						  + ")");
			//		scanner = new GenesisScanner(grammar,"_print('hello',goodbye')");
	  File inputFile = new File(args[0]);
	  String input = readFile(inputFile);

		
		scanner = new GenesisScanner(grammar,input);
		scanner.get();   // Prime the pump with the first token
		Instruction instr = grammar.parse(scanner);
      // Set up an environment with some virs
	
      LocalEnvironmentVal env = new LocalEnvironmentVal();
		env.insert("dog",new StickyNote(88));
		env.insert("cat",new StickyNote("abc"));
		env.insert("pig",new StickyNote(false));

		// Now execute the instructions
		if (instr != null){
			System.out.println(instr.eval(env));
//			scanner = new GenesisScanner(grammar,"f(3)");
//			scanner.get();
//			EveInstruction.getGrammar().setStart("<Invocation>");
// 			instr = grammar.parse(scanner);
//			System.out.println(instr.eval(env));
		}
		else
			System.err.println("Syntax error in your algorithm.");
	}
}
