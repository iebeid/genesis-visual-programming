/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eve;

import parsing.GenericParser;
import parsing.SyntaxRule;

/**
 *
 * @author Larry Morell
 */
public class Primary_Number extends EveInstruction{

	private static String rule = "<Primary> ::= <Number>";
	private static SyntaxRule syntaxRule = new SyntaxRule(GRAMMAR, rule);

	public static void initialize() {  // static initialization
		GRAMMAR.addRule(syntaxRule,
				  new Primary_Number(),
				  new GenericParser(syntaxRule.lhs())
				 
				  );
	}


}
