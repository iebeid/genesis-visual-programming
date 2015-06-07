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

package runtime;

import eve.CodeVal;  // This is particularly unhealthy ... have to think about this

/**
 *
 * @author Larry Morell (lmorell@atu.edu)
 */
public abstract class Val implements Value {

	   // ---------------- Static methods --------------------//

		/**
	 * Prints error message when trying to compare incompatible types
	 * @param v1
	 * @param v2
	 */

  public static void printBadComparison (Value v1, Value v2) {
    System.err.println ("Cannot compare " + v1 + " with " + v2);
    System.exit(1);
  }

  /**
	* Prints error message when trying to compare invalid types
	* @param v1
	* @param v2
	*/
	public static void checkBadComparison(Value v1, Value v2) {
    if (v1.getClass() != v2.getClass()) {
       System.err.print ("Cannot compare '" + v1 + "' with " );
       if (v2 instanceof Node)
          System.err.println ("a List");
       else
          System.err.println(v2);
       System.exit(1);
    }
  }
  public static StickyNote createVal (int n) {
	  return new StickyNote(new IntVal(n));
  }
  public static StickyNote createVal (double n) {
	  return new StickyNote (new DoubleVal(n));
  }
  public static StickyNote createVal (String n) {
	  return new StickyNote(new StringVal(n));
  }
  public static StickyNote createVal (ListVal n) {
	  return new StickyNote(new ListVal(n));
  }
  public static StickyNote createVal (TruthVal n) {
	  return new StickyNote (new TruthVal(n));
  }
  public static StickyNote createVal (CodeVal n) {
	  return new StickyNote( new CodeVal(n));
  }
  public static StickyNote createVal(Node n) {
	  return new StickyNote (new Node(n));
  }
  public static StickyNote createVal (Val n) {
	
			return  new StickyNote(n.copy());  // lazy ... CHECK THIS
	
	
  }
  protected EnvironmentVal env = null;


  /**
	* Abstract function for making eq comparisons
	* @param rhs
	* @return
	*/
  public abstract boolean eq( Value rhs );
  /**
	*
	* @return The string version of the parent class?
	*/
  public String addr() {return super.toString();}
  public boolean ne (Value rhs) { return ! eq(rhs);}



}
