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

import parsing.GenericParser;
import parsing.Instruction;
import parsing.InstructionList;
import parsing.SyntaxRule;
import runtime.EnvironmentVal;
import runtime.StickyNote;

/**
 *
 * @author Larry Morell
 */

public class Invocation_Define extends EveInstruction {

	private static String rule = "<Invocation> ::= define ( <Id> , <ExpressionList> ) ";
	private static SyntaxRule syntaxRule = new SyntaxRule(GRAMMAR, rule);

	public static void initialize() {  // static initialization
		GRAMMAR.addRule(syntaxRule,
				  new Invocation_Define(),
				  new GenericParser(syntaxRule.lhs()));
	}
   public Invocation_Define(){ }

	String id;
	Instruction exp;
	@Override
	public Instruction createInstruction(InstructionList il) {
		Invocation_Define instr = new Invocation_Define();
		instr.id = il.get(2).toString();  // the <Id>
		instr.exp = il.get(4);  // the <Factor>
		return  instr;
	}
	@Override
   public String toString () {
		return "define (" + id + "  ,\n   " + exp.toString() + ")\n";
	}
	@Override
	public InstructionList exec() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public StickyNote eval(EnvironmentVal env) {
		return env.insert(id,exp.eval(env));
	}
}
