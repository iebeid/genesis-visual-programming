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

import java.util.Iterator;
import parsing.InstructionList;
//import OriginalRuntime.GenesisVal;
//import OriginalRuntime.StickyNote;
import runtime.StickyNote;
import runtime.Val;
import runtime.StickyNote;
import runtime.Value;
/**
 * A function definition
 * @author Larry Morell (lmorell@atu.edu)
 */
public class EveMethodVal extends Val {


   // ---------------- Inner classes ---------------------//
   // ---------------- Static variables ------------------//
	// ---------------- Static initialization -------------//
   // ---------------- Static methods --------------------//
   // ---------------- Instance variables ----------------//
InstructionList parameterList;
InstructionList body;

	// ---------------- Constructors ----------------------//
public EveMethodVal(InstructionList pl, InstructionList b) {
	parameterList = pl;
	body = b;
}
	public EveMethodVal(EveMethodVal emv) {
		parameterList = emv.parameterList;
		body = emv.body;
	}

	// ---------------- Getters/Setters -------------------//

	public InstructionList getBody() {
		return body;
	}

	public void setBody(InstructionList body) {
		this.body = body;
	}

	public InstructionList getParameterList() {
		return parameterList;
	}

	public void setParameterList(InstructionList parameterList) {
		this.parameterList = parameterList;
	}
	// ---------------- Other member functions ------------//

	@Override
	public String toString () {
	   StringBuilder result = new StringBuilder("function ");
		result.append("(");
		Iterator it = parameterList.iterator();
		if (it.hasNext()) {
			result = result.append(it.next());
		}
		while(it.hasNext()) {
			result.append(",\n    ").append(it.next());
		}

		result.append(") {\n   ");
      // add in the body
		it = body.iterator();
		if (it.hasNext()) {
			result = result.append(it.next());
		}
		while(it.hasNext()) {
			result.append(";\n   ").append(it.next());
		}

		result.append("\n}");
		return result.toString();
	}

	@Override
	public boolean eq(Value rhs) {
		if (!(rhs instanceof EveMethodVal)) return false;
		return rhs == this;
	}

	public static void main (String args[]) {
		InstructionList pl = new InstructionList();
		pl.add (new EveNumber(8));

		InstructionList body = new InstructionList();
		body.add(new EveString ("abc"));
		body.add(new EveString ("def"));

		System.out.println(body.getFirst().eval());
		System.out.println(pl.getFirst().eval());
		EveMethodVal ev = new EveMethodVal(pl, body);
		System.out.println("ev:" + ev);
//		StickyNote sn = new StickyNote( EveMethodVal(ev));
	//	System.out.println("sn:" + sn);
	}

	public Value copy() {
		return new  EveMethodVal(this);
	}



}
