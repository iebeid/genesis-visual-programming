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
import parsing.*;

/**
 *
 * @author Larry Morell
 */
public class EveStringParser  extends Parser {
	public EveStringParser() { 	}

	@Override

public Instruction parse(Scanner scanner) {
	// Nothing to do.  The number has already been recognized

		Token current = scanner.current();  // better be a NumberToken

		Instruction instruction;
		instruction = new EveString(current.getValue());
		scanner.get();
		return instruction;
	}

}
