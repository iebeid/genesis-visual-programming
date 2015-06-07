package eve;

/*
 *  Copyright (C) 2011 Larry Morell <morell@cs.atu.edu>
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



/**
 *
 * @author Larry Morell (lmorell@atu.edu)
 */


import java.lang.reflect.Method;
import parsing.Instruction;
import parsing.InstructionList;
import runtime.EnvironmentVal;
import runtime.Number;
import runtime.StickyNote;
import runtime.Value;


/**
 *
 * @author Larry Morell
 */

public class BaseMethods {

   public static int indent = 0;

	private static void printIndent() {
		for (int i=0; i < indent; i++) {
			System.out.print(" ");
		}
	}
	private static void printIncreaseIndent() {
		indent += 3;
		printIndent();
	}
	private static void printDecreaseIndent() {
		indent -= 3;
		printIndent();
	}

   public BaseMethods(){ }

   final static boolean DEBUG = false;
	public InstructionList eval() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public static StickyNote _list(EnvironmentVal env, InstructionList args) {
		return new StickyNote(args);
	}

	public static StickyNote _(EnvironmentVal env, InstructionList args) {
		return _list(env, args);
	}

	public static StickyNote _print(EnvironmentVal env, InstructionList args) {
		String comma ="";
		for (Instruction instr: args){
			System.out.print(comma + instr.eval(env));
			comma = ",";
		}


		return null;
	}
	public static StickyNote _f(EnvironmentVal env, InstructionList args) {
		System.out.println("_f called");
		return Instruction.NULL_NOTE;
	}
   public static StickyNote  _add_number (EnvironmentVal callingEnv, InstructionList args){
		Value p1 = args.getFirst().eval(callingEnv).getVal();
		Value p2 = args.get(1).eval(callingEnv).getVal();
		if (DEBUG) {
			    System.out.print( p1 + ", " + p2);
				
		}

		if (p1 instanceof Number && p2 instanceof Number){
			return (new StickyNote(((Number)p1).add((Value)p2)));
		}
		System.err.println("Cannot add " + p1 + " and " + p2);
		return null;
	}

 public static StickyNote  _mul_number (EnvironmentVal callingEnv, InstructionList args){
		Value p1 = args.getFirst().eval(callingEnv).getVal();
		Value p2 = args.get(1).eval(callingEnv).getVal();
		if (DEBUG) {
			    System.out.print( p1 + ", " + p2);

		}

		if (p1 instanceof Number && p2 instanceof Number){
			//return (new StickyNote(((Number)p1).add((Value)p2)));
                    return (new StickyNote(((Number)p1).mul((Value)p2)));
		}
		System.err.println("Cannot multiply " + p1 + " and " + p2);
		return null;
	}
 public static StickyNote  _div_number (EnvironmentVal callingEnv, InstructionList args){
		Value p1 = args.getFirst().eval(callingEnv).getVal();
		Value p2 = args.get(1).eval(callingEnv).getVal();
		if (DEBUG) {
			    System.out.print( p1 + ", " + p2);

		}

		if (p1 instanceof Number && p2 instanceof Number){
			//return (new StickyNote(((Number)p1).add((Value)p2)));
                    return (new StickyNote(((Number)p1).div((Value)p2)));
		}
		System.err.println("Cannot multiply " + p1 + " and " + p2);
		return null;
	}

 public static StickyNote  _sub_number (EnvironmentVal callingEnv, InstructionList args){
		Value p1 = args.getFirst().eval(callingEnv).getVal();
		Value p2 = args.get(1).eval(callingEnv).getVal();
			if (DEBUG) {
			    System.out.print( p1 + ", " + p2);
			}

		if (p1 instanceof Number && p2 instanceof Number){
			return (new StickyNote(((Number)p1).sub((Number)p2)));
		}
		System.err.println("Cannot subtract " + p1 + " and " + p2);
		return null;
	}
 
 public static StickyNote  _mod_number (EnvironmentVal callingEnv, InstructionList args){
		Value p1 = args.getFirst().eval(callingEnv).getVal();
		Value p2 = args.get(1).eval(callingEnv).getVal();
			if (DEBUG) {
			    System.out.print( p1 + ", " + p2);
			}

		if (p1 instanceof Number && p2 instanceof Number){
			return (new StickyNote(((Number)p1).mod((Number)p2)));
		}
		System.err.println("Cannot subtract " + p1 + " and " + p2);
		return null;
	}

	// _define_method: define a new method for Eve
   public static StickyNote _define_method (EnvironmentVal callingEnv,InstructionList parameterList,InstructionList body ){
		StickyNote sn =  null;// (new EveMethodVal(parameterList, body));
	   return sn;
	}
	/**
	 * Invoke a method by its string name
	 * @param methodName The name of the method to be invoked
	 * @param callingEnv The environment in which to evaluate the arguments
	 * @param args The arguments to the method
	 * @return The computed stickynote from the method call; null if the call fails
	 */
	
   public static StickyNote eval (String methodName, EnvironmentVal callingEnv,
			  InstructionList args) {
		StickyNote sn = null;

		// Use reflection rather than a table lookup saves a lot editing
		
		try {
			Class <?> base = Class.forName("eve.BaseMethods"); // get class by name
			if (DEBUG) {
				System.out.println("" + methodName + "(");
				printIncreaseIndent();
			}

			Method method = base
					  .getMethod(methodName,EnvironmentVal.class, InstructionList.class);
				sn = (StickyNote)
					  method.invoke(null,callingEnv,args);
         if (DEBUG) {
				System.out.println();
				printDecreaseIndent();
				System.out.println("):"  +sn);
				printIndent();
			}


		}
		catch (Exception  e){
			System.err.println(e);
			System.err.println("Cannot invoke " + methodName + " with parameters ");
			for (Instruction instr : args){
				System.err.print(instr.eval(callingEnv) +", ");
			}
		}
		return sn;
	}

	// test calling
   public static  void main (String [] args){
      String[] method = new String[] {"_add_number",
		   "_sub_number", "_mul_number", "_div_number", "_mod_number"
		}	;
		int i = 0;
		// Simulate the result of parsing
		InstructionList il = new  InstructionList();
		il.add(new EveNumber(33)); // parse a 17
		il.add(new EveNumber(17)); // parse a 33

		// Execute each method in method[] for these two arguments
		for (i = 0; i < method.length; i++) {
			EnvironmentVal env = new EnvironmentVal();  // nothing in it
			StickyNote sn = eval (method[i],env, il);
			System.out.println("The result is "+ sn);
		}
		// try out _define_method
		System.exit(0);
      EnvironmentVal env = new EnvironmentVal();
		InstructionList parameterList = new InstructionList();
		parameterList.add(new EveNumber(17));
		InstructionList body = new InstructionList();
		body.add(new EveString ("abc"));

		StickyNote m = _define_method(env, parameterList, body);


	} // end main
}
