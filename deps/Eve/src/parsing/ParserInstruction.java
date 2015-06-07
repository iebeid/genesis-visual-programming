/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package parsing;

/**
 *
 * @author Larry Morell
 */
abstract public class ParserInstruction extends Instruction {


	@Override
	public InstructionList exec() {
		System.out.print(source);
		return null;   // It's done it's work, nothing more
	}

	@Override
	abstract public Instruction createInstruction(InstructionList il);

}

