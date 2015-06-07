/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eve;

// import OriginalRuntime.StickyNote;
// import OriginalRuntime.EnvironmentVal;
import runtime.StickyNote;
import runtime.EnvironmentVal;
import java.util.Stack;
import parsing.Grammar;
import parsing.Instruction;
import parsing.InstructionList;
import parsing.Source;

/**
 * EVE hides the storage and operation of the runtime system (Elementary Virtual EnvironmentVal)
 * which maintains the data storage for the system (stickynotes and their associated names
 * within scopes.)
 * @author Larry Morell <morell@cs.atu.edu>
 */
public abstract class EveInstruction extends Instruction {
	// Define a possible runtime environment.  In this case we'll just
	// define it to be a stack of SN's.  All Eve instructions can
	// therefore access this static stack.

	public final static Grammar GRAMMAR = new Grammar();  // one grammar for all Eve instructions
	static Stack<StickyNote> stack = new Stack<StickyNote>();
	static Stack<EnvironmentVal> scopeStack = new Stack<EnvironmentVal>();
	static EnvironmentVal scope = null;


	public EveInstruction() {
	}

	public EveInstruction(Instruction nextInstruction) {
		this.nextInstruction = nextInstruction;
	}


	public EveInstruction(Source s, InstructionList parsedList) {
		super(s, parsedList);
	}

	public static Grammar getGrammar() {
		return GRAMMAR;
	}
  @Override
	public Instruction createInstruction(InstructionList il) {
      return il.getFirst();
	}

	public InstructionList exec() {
		throw new UnsupportedOperationException("Exec not supported.");
	}
   // implement required functions with defaults
	public Object value() {return null;}
	public StickyNote eval() {return null;}
	public StickyNote eval(EnvironmentVal env){return null;}
   public InstructionList getInstructionList() {return new InstructionList(this);}
	
}
