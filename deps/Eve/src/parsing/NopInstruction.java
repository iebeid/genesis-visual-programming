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

package parsing;

//import OriginalRuntime.EnvironmentVal;
//import OriginalRuntime.StickyNote;
import runtime.EnvironmentVal;
import runtime.StickyNote;

/**
 *
 * @author Larry Morell (lmorell@atu.edu)
 */
public class NopInstruction extends Instruction {
	public final static NopInstruction NOP_INSTRUCTION = new NopInstruction("NOP");
	public final static NopInstruction NOP_EMPTY = new NopInstruction("NOP_EMPTY");
	String tag ;


	public NopInstruction(String tag){
       this.tag = tag;
	}
	// ---------------- Other member functions ------------//
	@Override
	/**
	 * Do nothing
	 */

	public InstructionList exec() {
		return null;
	}

	@Override
	public Instruction createInstruction(InstructionList il) {
		return new NopInstruction (il.toString());
	}

	@Override
	public StickyNote eval() {
       return NULL_NOTE;
	}

	@Override
	public StickyNote eval(EnvironmentVal env) {
		return NULL_NOTE;
	}

	public String toString (){
		return tag;
	}


}
