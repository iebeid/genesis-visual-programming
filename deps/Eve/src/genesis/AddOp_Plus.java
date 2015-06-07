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
package genesis;

/**
 *
 * @author Larry Morell (lmorell@atu.edu)
 */
import parsing.GenericParser;
import parsing.Instruction;
import parsing.InstructionList;
import parsing.SyntaxRule;
import runtime.Number;
import runtime.DoubleVal;
import runtime.ListVal;
import runtime.Node;
import runtime.StickyNote;
import runtime.StringVal;
import runtime.Value;

/**
 *
 * @author Larry Morell
 */
public class AddOp_Plus extends GenesisInstruction
                               implements BinaryEval
{

	private final static String rule = "<AddOp> ::= + ";
	private final static SyntaxRule syntaxRule = new SyntaxRule(GRAMMAR, rule);
	private final static AddOp_Plus ADD_OP__PLUS = new AddOp_Plus();

	public static void initialize() {  // static initialization
		GRAMMAR.addRule(syntaxRule,
				  new AddOp_Plus(),
				  new GenericParser(syntaxRule.lhs()));
	}

	public AddOp_Plus() {
	}

	@Override
	public Instruction createInstruction(InstructionList il) {
		return ADD_OP__PLUS;
	}

	@Override
	public InstructionList exec() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public StickyNote eval(StickyNote sn1, StickyNote sn2) {
		StickyNote answer = null;
		// Utility.println("Attempting to add " + s1 + " + " + s2);
		// Utility.println("Attempting to add " + s1.getClass() + " and " + s2); //.getClass());
		// Utility.println("Attempting to add " + tn.left + " and " + tn.left.right); //.getClass());
		Value s1 = sn1.getVal();
		Value s2 = sn2.getVal();
		if (s1 instanceof ListVal || s2 instanceof ListVal) {
			printError("1. Cannot add '" + s1 + " and " + s2);
		}
//		else if (s1 instanceof Node || s2 instanceof Node) {
//
//			// System.out.println("===> Adding");
//
//			if (s1 instanceof Node) {
//				//  System.out.println("===> Adding:" + s1);
//				ListVal gl1 = new ListVal((Node) s1).copy(); // copy the nodes
//				while (gl1.on()) {
//					gl1.move(); // move to the end of list g1
//				}
//				if (s2 instanceof Node) {
//					//System.out.println("Forming gl2");
//					ListVal gl2 = new ListVal((Node) s2);
//					gl2.reset();
//					while (gl2.on()) {
//						// System.out.println ("Evaluator: Appending to " + gl1 );
//						// System.out.println( "Evaluator: the value:" + gl2.get());
//						// System.out.println ("Evaluator: Calling insert with");
//						gl1.insert(new StickyNote(gl2.get()));
//						// System.out.println ("Evaluator: giving ... " + gl1);
//						gl2.move();
//					}
//					// System.out.println ("Done!");
//				}
//				else if (s2 instanceof StringVal) {
//					gl1.insert(new StickyNote(s2));
//				}
//				else if (s2 instanceof Number) {
//					//System.out.println("Appending " + s2 + " to " + gl1);
//					StickyNote temp = scope.find("correct");
//					//       if (temp != null)
//					//         System.out.println ("Correct is: " + temp.getClass() + " at " + (Object)temp);
//					gl1.insert(new StickyNote(s2));
//				}
//				else {
//					printError("2. Cannot add '" + s1 + " and " + s2);
//				}
//				gl1.reset();
//
//				// System.out.println ("Done!" + gl1.toString());
//
//				answer = new StickyNote(gl1.current());
//				// System.out.println ("Done!!"+ gl1.current().info);
//				// System.out.println ("giving ... " + answer);
//			}
//			else {  // s2 must be an instanceof a node
//				ListVal gl2 = new ListVal((Node) s2).copy(); // copy the codes
//				//Utility.println("Copying " + s1 + " to " + s2);
//				gl2.reset();
//				if (s1 instanceof StringVal) {
//					gl2.insert(new StickyNote(s1));
//				}
//				else if (s1 instanceof DoubleVal) {
//					gl2.insert(new StickyNote(s1));
//				}
//				else {
//					printError("3. Cannot add '" + s1 + " and " + s2);
//				}
//				gl2.reset();
//				answer = new StickyNote(gl2.current());
//			}
//
//		}
		else if (s1 instanceof StringVal || s2 instanceof StringVal) {
			String s = "";
			if (s1 instanceof StringVal) {
				if (s2 instanceof StringVal) {
					s = "" + s1 + s2;
				}
				else if (s2 instanceof Number) {
					s = "" + s1 + s2;
				}
				else {
					printError("4. Cannot add '" + s1 + " and " + s2);
				}
				answer = new StickyNote(s);
			}
			else { // s2 must be a string

				if (s1 instanceof Number) {
					s = "" + s1 + s2;
				}
				else {
					printError("5. Cannot add '" + s1 + " and " + s2);
				}
				answer = new StickyNote(s);
			}
		}
		else if (s1 instanceof Number && s2 instanceof Number) { // Both!
			double d1 = ((DoubleVal) s1).getVal();
			double d2 = ((DoubleVal) s2).getVal();
			answer = new StickyNote(d1 + d2);
		}
		else {
			printError("6. Cannot add '" + s1 + " and " + s2);
		}
		return answer;
		// System.out.println("Returning " + answer);
	}
}
