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

package eve;

/**
 *
 * @author Larry Morell (lmorell@atu.edu)
 */

import parsing.Instruction;
import parsing.InstructionList;
import parsing.SyntaxRule;
//import OriginalRuntime.StickyNote;
//import OriginalRuntime.EnvironmentVal;
import runtime.EnvironmentVal;
import runtime.StickyNote;
/**
 *
 * @author Larry Morell
 */

public class EveNumber extends EveInstruction {

	private static String rule = "<Number> ::= <no> ";
	private static SyntaxRule syntaxRule = new SyntaxRule(GRAMMAR, rule);

	public static void initialize() {  // static initialization
		GRAMMAR.addRule(syntaxRule,
				  new EveNumber(),
				//  new GenericParser(syntaxRule.lhs())
				  new EveNumberParser()
				  );
	}
	private double number;
	StickyNote numberNote;

   public EveNumber(){  number = 0; numberNote = new StickyNote(0);}
   public EveNumber(double d) {
		number = d;
		numberNote = new StickyNote(d);
	}

	public EveNumber(String value) {
	   this(Double.parseDouble(value));
	}

	@Override
	public Double value() {return number;}

	@Override
	public Instruction createInstruction(InstructionList il) {
		return  il.getFirst();
	}
   public String toString  () {
		return "" + number;
	}
	@Override
	public InstructionList exec() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	
	@Override
	public StickyNote eval() {
		return numberNote;
	}
	@Override
	public StickyNote eval(EnvironmentVal env) {
		return numberNote;
	}

}
