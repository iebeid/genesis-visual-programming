package eve;


import parsing.GenericParser;
import parsing.Instruction;
import parsing.InstructionList;
import parsing.SyntaxRule;
//import OriginalRuntime.StickyNote;
import runtime.StickyNote;
/**
 *
 * @author Larry Morell
 */
public class Primary_Boolean extends EveInstruction {

	private static String rule = "<Primary> ::= <Boolean>";
	private static SyntaxRule syntaxRule = new SyntaxRule(GRAMMAR, rule);

	public static void initialize() {  // static initialization
		GRAMMAR.addRule(syntaxRule,
				  new Primary_Boolean(),
				  new GenericParser(syntaxRule.lhs()));
	}
   public Primary_Boolean(){ }

	@Override
	public Instruction createInstruction(InstructionList il) {
		return  il.getFirst();
	}

	@Override
	public InstructionList exec() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public StickyNote eval() {
		throw new UnsupportedOperationException("Boolean.eval() should not be called.");
	}
}
