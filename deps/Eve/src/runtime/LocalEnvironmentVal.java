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

import java.util.HashMap;

/**
 *
 * @author Larry Morell (lmorell@atu.edu)
 */
public class LocalEnvironmentVal extends EnvironmentVal{
	private EnvironmentVal callingEnv;
	private Value containingObject;
	private StickyNote functionDef;

	// ---------------- Constructors ----------------------//
 public LocalEnvironmentVal()
  {
    ht = new HashMap<String,StickyNote>();
    callingEnv = null;
    containingObject = null;
	 functionDef = null;
  }


  // lexical scoping by default
  /**
	* Create a new environment with s as the lexically containing environment
	* @param s
	*/
  public LocalEnvironmentVal( LocalEnvironmentVal s )
  {
    ht = new HashMap<String,StickyNote>( s.ht );
    callingEnv = s;
    containingObject = null;
	 functionDef = null;
  }


  // , then dynamic parent
  /**
	* Creates an environment from s and t
	* @param s The lexically containing environment
	* @param t The dynamically containing environment
	*/

  public LocalEnvironmentVal( LocalEnvironmentVal s, LocalEnvironmentVal t )
  {
    ht = new HashMap<String,StickyNote>( s.ht );
    callingEnv = s;
    containingObject = t;
  }

	/**
	 * @return the callingEnv
	 */
	public EnvironmentVal getCallingEnv() {
		return callingEnv;
	}

	/**
	 * @param callingEnv the callingEnv to set
	 */
	public void setCallingEnv(EnvironmentVal callingEnv) {
		this.callingEnv = callingEnv;
	}

	/**
	 * @return the containingObject
	 */
	public Value getContainingObject() {
		return containingObject;
	}

	/**
	 * @param containingObject the containingObject to set
	 */
	public void setContainingObject(Value containingObject) {
		this.containingObject = containingObject;
	}

	/**
	 * @return the functionDef
	 */
	public StickyNote getFunctionDef() {
		return functionDef;
	}

	/**
	 * @param functionDef the functionDef to set
	 */
	public void setFunctionDef(StickyNote functionDef) {
		this.functionDef = functionDef;
	}
	// ---------------- Getters/Setters -------------------//

	// ---------------- Other member functions ------------//
}
