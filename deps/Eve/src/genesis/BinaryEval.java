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

import parsing.Instruction;
import runtime.EnvironmentVal;
import runtime.StickyNote;

/**
 *
 * @author Larry Morell <morell@cs.atu.edu>
 */

/**
 * The two instructions passed in will compute the two operands, which in turn
 * must be evaluated by the current operator
 * @author Larry Morell <morell@cs.atu.edu>
 */
public interface BinaryEval {
   public StickyNote eval(StickyNote i1, StickyNote i2);
}
