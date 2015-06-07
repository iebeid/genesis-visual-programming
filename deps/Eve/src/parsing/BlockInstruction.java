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


//import OriginalRuntime.StickyNote;
import runtime.EnvironmentVal;
import runtime.StickyNote;

/**
 *
 * @author Larry Morell (lmorell@atu.edu)
 */
public class BlockInstruction extends Instruction {
   // ---------------- Instance variables ----------------//
   InstructionList instructionList;
	
	// ---------------- Constructors ----------------------//			  
	public BlockInstruction(InstructionList instructionList){
		this.instructionList = instructionList;
	}
	/**
	 * 
	 * @return The instruction list to be executed is returned so the
	 *         UI can interpret it
	 */
	@Override
	public InstructionList exec() {
      return instructionList;
	}

	@Override
	public Instruction createInstruction(InstructionList il) {
         return new BlockInstruction(il);
	}

	@Override
	public StickyNote eval() {
		for (Instruction instruction : instructionList) {
			System.out.println("Evaling instruction:" +instruction.eval());

		}
		return NULL_NOTE;
	}

	@Override
	public StickyNote eval(EnvironmentVal env) {
		throw new UnsupportedOperationException("Not supported yet.");
	}






	// ---------------- Getters/Setters -------------------//

	// ---------------- Other member functions ------------//
}
