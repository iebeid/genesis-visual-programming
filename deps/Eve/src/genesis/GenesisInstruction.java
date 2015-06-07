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

import parsing.Grammar;
import parsing.Instruction;
import parsing.InstructionList;
//import runtime.EnvironmentVal;
//import runtime.StickyNote;
import runtime.EnvironmentVal;
import runtime.StickyNote;
/**
 *
 * @author Larry Morell
 */
public abstract class GenesisInstruction extends Instruction {

	final static protected Grammar GRAMMAR = new Grammar();
	final static protected StickyNote NULL_NOTE = new StickyNote("No value");
	static EnvironmentVal scope = new EnvironmentVal();
	// default implementation ... future extenstion

	public InstructionList exec() {
		throw new UnsupportedOperationException("Exec not supported yet in Genesis.");
	}

	@Override
	public Instruction createInstruction(InstructionList il) {
		return  il.getFirst();
	}


	@Override
	public StickyNote eval() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public StickyNote eval( EnvironmentVal env) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public static Grammar getGrammar() {
		return GRAMMAR;
	}
}
