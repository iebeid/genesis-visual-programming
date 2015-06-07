/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parsing;

//import OriginalRuntime.EnvironmentVal;
//import OriginalRuntime.StickyNote;
import runtime.StickyNote;
import runtime.EnvironmentVal;
import runtime.StringVal;

/**
 * An instruction in a virtual machine
 * @author Larry Morell
 */
public abstract class Instruction {
	public final static  StickyNote NULL_NOTE = new StickyNote("No value");
	public static String indent(int n) {
		return "                                                 ".substring(0, n);
	}
	protected InstructionList parsedInstructionList;
	protected Source source;
	protected Instruction nextInstruction = null;

	//Constructors
	public Instruction() {
	}

	public Instruction(Source s, InstructionList il) {
		this();
		parsedInstructionList = il;
		source = s;
	}

	/**
	 * Override this instruction to execute the contained instruction
	 *
	 * @return A list of instructions that need to be executed for accomplishing
	 * this instruction.  null is returned if the instruction is directly interpreted.
	 * This is similar in effect to a macro expansion in which one instruction is
	 * replaced with a 0 or more instructions.  A list of instructions will be
	 * prepended to the the instruction list from which it was executed.
	 */
	public abstract InstructionList exec();

	abstract public Instruction createInstruction(InstructionList il);

	public void printError() {
		System.err.println("Could not execute" + this);
		System.exit(1);
	}

	public void printError(String msg) {
		System.out.println(msg);
	}

	/**
	 * Override this function to pretty print the instruction
	 * @param n  The number of spaces of indentation
	 * @return  A string of a pretty-printed form of the parsed input
	 *
	 */
	public StringBuffer prettyPrint(int n) {
		StringBuffer result = new StringBuffer(1000);
		for (Instruction instr : parsedInstructionList) {

			result.append(indent(n));
			if (instr.isLiteral()) {
				result.append(instr);
			}
			else {  // indent "instructions" which are at the same level
				result.append(instr.prettyPrint(+2));
			}
		}
		return result;
	}

	/**
	 *
	 * @return Returns whether or not this instruction is a literal; a literal instruction
	 * is one which ...??? fill this in!
	 */
	public boolean isLiteral() {
		return false;
	}

	public String getGrammarRule() {
		return "";
	} // default

	public void setGrammarRule() {
	}

	/**
	 * Establish the next instruction to be executed after this one
	 * @param next  The successor to this instruction
	 */
	public void setNextInstruction(Instruction next) {
		nextInstruction = next;
	}

	abstract public StickyNote eval();
   abstract public StickyNote eval(EnvironmentVal env);
}
