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

import java.util.ListIterator;
import parsing.Instruction;
import parsing.InstructionList;
import runtime.EnvironmentVal;
import runtime.List;
import runtime.ListVal;
import runtime.Node;
import runtime.StickyNote;
import runtime.Value;


/**
 *
 * @author Larry Morell (lmorell@atu.edu)
 */
public class CodeVal extends runtime.Val implements List{
   // ---------------- Inner classes ---------------------//
   // ---------------- Static variables ------------------//
	// ---------------- Static initialization -------------//
   // ---------------- Static methods --------------------//

   // ---------------- Instance variables ----------------//
   InstructionList il;
   ListIterator<Instruction> iter;

	// ---------------- Constructors ----------------------//
   public CodeVal () {
		super();
		il = new InstructionList();
		iter = il.iterator();
	}
	public CodeVal (InstructionList il) {
		super();
		this.il = new InstructionList(il);
		iter = il.iterator();
	}
   public CodeVal (Instruction instruction){
		super();
		il = new InstructionList(instruction);
		iter = il.iterator();
	}
	public CodeVal (CodeVal cv) {
		super();
		il = cv.il;
		env = cv.env ;  // share the methods
	}
	@Override
	public boolean eq(Value rhs) {
		if (!(rhs instanceof EnvironmentVal)) return false;
		return rhs == this;
	}

	public Value copy() {
		return new CodeVal (this);
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("");
		for (Instruction instr: il ){
			result.append (instr.toString());
			result.append (",\n");

		}
		return result.toString();
	}
	// ---------------- Getters/Setters -------------------//


	// ---------------- Other member functions ------------//
	public InstructionList getInstructionList() {
		return il;
	}
	public StickyNote eval(EnvironmentVal env) {
      StickyNote sn = null;
		for (Instruction instr:il){
  			sn = instr.eval(env);
		}
		return sn;
	}
// Implement List interface functions
	public ListVal add(Value rhs) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public boolean atFirst() {
		if (iter == null) return false;
		return  iter.nextIndex() == 0;
	}

	public boolean atLast() {
      if (iter == null) return false;
		return iter.nextIndex() == il.size() -1;
	}

	public void change(StickyNote e) {  // e better be a CodeVal of length 1
		int pos = iter.nextIndex();
		Instruction instr = null;
		if (e.getVal() instanceof CodeVal) {
			instr = ((CodeVal) e.getVal()).il.getFirst(); // get the first instruction
		}
		il.set(pos,instr);
	}

	public Node current() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void del() {
		iter.next();  // move
      iter.remove(); // and toss what we moved from
	}

	public void display() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void displayln() {
		display();
		System.out.println("");
	}

	public void done() {
      il.clear();
		iter = il.iterator();
	}

	public boolean empty() {
      return il.isEmpty();
	}

	public boolean equals(ListVal e) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public boolean ge(StickyNote rhs) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	/**
	 *
	 * @return The next instruction, wrapped in a StickyNote
	 */
	public StickyNote get() {
      if (iter.nextIndex() < il.size()) {
			return new StickyNote( new CodeVal( il.get(iter.nextIndex() ) ) );
		}
		System.err.println("Attempt to get a non-existent instruction");
		System.exit(1);
		return null;
	}

	Instruction getInstruction() {
		if (iter.nextIndex() < il.size()) {
			return  il.get(iter.nextIndex() )  ;
		}

		System.err.println("Attempt to get a non-existent instruction");
		System.exit(1);
		return null;

	}
	public boolean gt(StickyNote rhs) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public String implode() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void init() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void insert(Value v) {
		Instruction instr = null;
		if (v instanceof CodeVal) {
			instr = ((CodeVal) v).il.getFirst();
		}
		else {
			System.err.println("Attempt to insert a non-instruction to a CodeVal:" + v);
			System.exit(1);
		}
      il.add (iter.nextIndex(),instr);
	}

	public void insert(StickyNote e) {
      insert(e.getVal());
	}

	public void insert(Node n) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public boolean le(StickyNote rhs) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void left() {
		iter.previous();
	}

	public boolean lt(StickyNote rhs) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void move() {
		  iter.next();
	}

	public void move(int n) {
		if ( n < 0 || n > size() )    {
                System.err.println("Error: either passing a negative or " +
                "passing n larger than size() to move(int n)" +
								" \nn= " + n +
								" size()=" + size()
								);
        }

	}

	public void moveTo(int n) {
		iter = il.iterator();
		while (n > 0) {
			iter.next();
		}
	}

	public boolean ne(StickyNote rhs) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public boolean off() {
  		return iter.nextIndex() == il.size();
	}

	public boolean on() {
      if ( iter.nextIndex() == il.size()) return false;
      return true;
	}

	public int pos() {
      return iter.nextIndex();
	}

	public void prev() {
		iter.previous();
	}

	public void reset() {
       iter = il.iterator();
	}

	public StickyNote search(StickyNote A) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public int size() {
      return il.size();
	}

	public String typeName() {
		return "InstructionList";
	}

	public boolean valid() {
	    return true;
	}
}
