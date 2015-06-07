/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package parsing;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 *
 * @author Larry Morell
 */


public class InstructionList  extends LinkedList<Instruction> {

	public InstructionList() {
		super();
	}
	public InstructionList (InstructionList il) {
		super();
		for (Instruction it : il){
			add(it);  // probably not good enough. Probably need to create a new
			             // instruction for each it.  Hopefully this will be good enough
			             // for now
		}
	}
	public InstructionList (Instruction i) {
		this.add(i);
	}

	@Override
	public ListIterator<Instruction> iterator(){ return listIterator(0); }
}