package genesis;


import parsing.GenericParser;
import parsing.Instruction;
import parsing.InstructionList;
import parsing.SyntaxRule;
import runtime.StickyNote;

/**
 *
 * @author Larry Morell
 */
public class UnsignedPrimary_Boolean extends GenesisInstruction {

	private static String rule = "<UnsignedPrimary> ::= <Boolean>";
	private static SyntaxRule syntaxRule = new SyntaxRule(GRAMMAR, rule);

	public static void initialize() {  // static initialization
		GRAMMAR.addRule(syntaxRule,
				  new UnsignedPrimary_Boolean(),
				  new GenericParser(syntaxRule.lhs()));
	}
   public UnsignedPrimary_Boolean(){ }

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
