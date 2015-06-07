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

import eve.Boolean_True;
import eve.Factor_Unary;
import parsing.GenesisScanner;
import parsing.Grammar;
import parsing.Instruction;
import runtime.ListVal;
import runtime.EnvironmentVal;
import runtime.IntVal;
import runtime.StringVal;
import runtime.StickyNote;

/**
 *
 * @author Larry Morell
 */
public class TEST_GENESIS {

	private static void loadClasses() {
		// Temporarily do something dumb to ensure each of the
		// classes are loaded.  This will be replaced by something more elegant
		// later, probably using reflection
		AddOp_Minus.initialize();
		AddOp_Plus.initialize();
		AndOp.initialize();
		ArithmeticExpression.initialize();
		ArithmeticExpression_AddOp.initialize();
		Boolean_False.initialize();
		Boolean_True.initialize();
		ComparisonOp_EQ.initialize();
		ComparisonOp_GE.initialize();
		ComparisonOp_GT.initialize();
		ComparisonOp_LE.initialize();
      ComparisonOp_LT.initialize();
		ComparisonOp_NE.initialize();
		Condition.initialize();
		Condition_ComparisonOp.initialize();
		ConditionPrimary.initialize();
		ConditionPrimary_Not.initialize();
		ConditionTerm.initialize();
		ConditionTerm_And.initialize();
		Expression.initialize();
		Expression_Or.initialize();
		Factor.initialize();
		Factor_Unary.initialize();
		MultOp_Div.initialize();
		MultOp_Mult.initialize();
		NotList_Multiple.initialize();
		NotList_Single.initialize();
		OrOp.initialize();
		Primary_Unqualified.initialize();
		Term.initialize();
		Term_MultOp.initialize();
		UnaryOpList_Multiple.initialize();
		UnaryOpList.initialize();
		UnaryOp_Minus.initialize();
		UnaryOp_Plus.initialize();
		UnsignedPrimary_Boolean.initialize();
		UnsignedPrimary_Expression.initialize();
		UnsignedPrimary_Id.initialize();
		UnsignedPrimary_Number.initialize();
		UnsignedPrimary_String.initialize();
		Primary_Qualified.initialize();

//		QualifiedPrimary_Primary.initialize();
	}
	public static void main(String[] args) {
		EnvironmentVal scope = new EnvironmentVal();
		loadClasses();
		// Now insert some variables and values into the scope
		GenesisInstruction.scope.insert("a", new StickyNote(10));
		GenesisInstruction.scope.insert("b", new StickyNote(20));
		GenesisInstruction.scope.insert("c", new StickyNote(30));
		GenesisInstruction.scope.insert("d", new StickyNote(40));
		GenesisInstruction.scope.insert("s", new StickyNote("string"));
		ListVal l = new ListVal();
		l.insert(new IntVal(1));
		l.insert(new IntVal(2));
		l.insert(new IntVal(3));
		l.insert(new IntVal(4));
		l.reset();
		GenesisInstruction.scope.insert("l", new StickyNote(l));
		
		GenesisInstruction.getGrammar().setStart("<Expression>"); // start symbo	Instruction instruction;l

		// Now create a scanner to look for a single id
		Grammar grammar = GenesisInstruction.getGrammar();
		GenesisScanner scanner = new GenesisScanner(grammar, " (3+4)*(5-3) "); // input
		scanner.get();   // Prime the pump with the first token
		Instruction instr = grammar.parse(scanner);

/*		if (instr instanceof IdEval) {
			System.out.println("Id:" + ((IdEval) instr).eval(scope));
		}
		else { */
			System.out.println("Id:" + instr.eval());
//		}

	}
}
