/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package genesis;

import parsing.GenericParser;
import parsing.Instruction;
import parsing.InstructionList;
import parsing.SyntaxRule;
import runtime.DoubleVal;
import runtime.EnvironmentVal;
import runtime.StickyNote;

/**
 *
 * @author Larry Morell
 */
public class UnsignedPrimary_Number extends GenesisInstruction{

	private static String rule = "<UnsignedPrimary> ::= <no>";
	private static SyntaxRule syntaxRule = new SyntaxRule(GRAMMAR, rule);

	public static void initialize() {  // static initialization
		GRAMMAR.addRule(syntaxRule,
				  new UnsignedPrimary_Number(),
				  new GenericParser(syntaxRule.lhs()));
	}
	private double number;
	StickyNote numberNote;

	public UnsignedPrimary_Number() {number = 0;}
   public  UnsignedPrimary_Number(double d) {
		number = d;
		numberNote = new StickyNote(d);
	}

	public UnsignedPrimary_Number(String value) {
	   this(Double.parseDouble(value));
	}
	public Double value() {return number;}

	@Override
	public Instruction createInstruction(InstructionList il) {
      return il.getFirst();
	}

	@Override
	public StickyNote eval() {
		return numberNote;  // We're talking singleton here (almost)
	}
	@Override
	public StickyNote eval(EnvironmentVal env) {
		return numberNote;
	}

}
