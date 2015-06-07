/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package parsing;

/**
 *
 * @author Larry Morell
 */
public abstract class Parser {
	Symbol symbol;
	Trie trie;
   public Parser () {
		symbol = null; trie = null;
	}

	Grammar getGrammar () {
		return symbol.getGrammar();
	}

	public Parser(Symbol s) {
		symbol = s;
		trie = s.getTrie();
	}

	abstract public Instruction parse (Scanner scanner);


} // End of abstract class Parser
/**
 * Parser for nonterminals with a constant representation (operators and keywords)
 * @author lmorell
 */

/**
 * Special parser for parsing numbers
 * @author lmorell
 */
