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
package parsing;

/**
 * This is the central class of the entire project.  A Generic parser
 * is essentially driven by a grammar stored as a data structure, similar
 * in concept to a table-driven parser.  They differ in their goals and their
 * underlying implementation.
 *
 * In a table-driven parser, primitive parsing actions
 * are stored in a table.  The rows of the table are states and the columns
 * of the table are input tokens. Entries in the table are parse actions.
 * Therefore a given state and input leads to a particular parse action.
 * The parser is fast; the table can be of substantial size.
 *
 * In the generic parser implemented here, the table is replaced with
 * a collection of tries, one trie for each non-terminal in the grammar.
 * A trie for nonterminal N represents all the rules for N.
 * Every nonterminal has a parser associated with it.  The vast
 * majority of these are instances of this generic parser.  The specialized
 * parsers are for nonterminals that traditionally are used to represent
 * the tokens for id, number, and string.
 *
 * The generic parser is implemented in the style of recursive-descent.
 * Given a nonterminal N to parse, it looks up the trie for the nonterminal
 * and then proceeds to parse as directed by the trie.  Each element of
 * the trie is a symbol from a grammar rule. A path from the root of the
 * trie is a prefix of a grammar rule associated with N.  The generic algorithm
 * involves using the current input (supplied by a scanner) to select the
 * correct path to follow.  Once the next node is identified, the parser
 * associated with the symbol in that node is retrieved and executed.
 *
 * Each parser returns a list of instructions, one instruction for each
 * symbol in the rule that is parsed.  The list of instructions returned by
 * parsing a symbol is passed to an routine for creating a single instruction
 * from the list; this single instruction is then appended to the instruction
 * list being constructed by the parser.
 *
 * Parsing using a trie is greedy in the sense that it returns the
 * longest possible parse, which corresponds to the longest possible
 * rule of N that can be matched.  Thus if one rule is a prefix of a
 * second rule, and the input satisfies the second rule, then the
 * then parsing will match the second rule; if parsing cannot extend
 * beyond the first rule, then the first rule is what is matched.
 * Parsing fails if the first rule completes and the next input
 * indicates the second rule should match.  No backtracking occurs!
 *
 * @author Larry Morell
 */
public class GenericParser extends Parser {

	public GenericParser(Symbol s) {
		super(s);
	}

	public GenericParser() {
		throw new UnsupportedOperationException("Not yet implemented");
	}
	// parse -- assumes grammar has been set

	public Instruction parse(Scanner scanner) {
		InstructionList result = new InstructionList();

		Symbol s;
		Token t = scanner.current();  // think ... have we already read this???
		Instruction instr;
		if (trie.getSymbol().isTerminal()) {
			// simple case: invoke the parser associated with the terminal
			// if the symbol matches the  terminal
			if (t.getSymbol() == trie.getRoot().getSymbol()) {
				instr = trie.getInstruction();
				return instr;
			}
			else {
				System.err.println("Expected " + trie.getRoot().getSymbol()
						  + "; found " + t);
				return null;
			}
		}
		else {
			// the root symbol in the trie is a nonterminal
			// complex case: descend the trie associated with the nonterminal
			// invoking the appropriate parsers as we go
			// Start by getting the current input symbol
			s = t.getSymbol();
			// Calling firstNode() returns the TN where the terminal is found
			// This is only useful for determining if the current input symbol
			// is the prefix of the nonterminal at the root
			TrieNode tn = trie.firstNode(s);
			TrieNode parent = trie.getRoot();
			if (tn == null) { // no match
				if (trie.getSymbol().isNullable()) // if the root is nullable, then ok
				{
					//return new NopInstruction(); // yep, it's empty!
					return parent.instruction.createInstruction(result);
				}
				else {  // root not nullable and the next input does not match -- error
					// System.err.println(t + " cannot be found here");
					return null;
				}
			}
			// At this point a legitmate starting point for the rule was found
			// Now proceed downward
		

			tn = parent.getChild(s);  // the child which contains the current symbol
			while (tn != null) {

				s = tn.getSymbol();

				instr = s.parse(scanner);
				if (instr != null) {
					result.add(instr);
					parent = tn;
               s = scanner.current().getSymbol();
					tn = tn.getChild(s);
				}
				else { // parsing failed
					tn = null;
				}
			}
			if (parent.isEndOfRule()) {
				// Here is the main effect.  We have parsed successfully up to and
				// including the parent.  What we do now is to convert the result
				// to a single instruction by invoking the instruction builder
				// associated with the parent.  This ensures uniformity of every
				// parser: all parsing results in a single instruction, built, perhaps,
				// from a list of constituent instructions
				return parent.instruction.createInstruction(result);
			}
			else {
				System.out.println("Failure to recognize a complete rule while parsing");
				return null;
			}
		}
	}

	// test it
	public static void main(String[] args) {
		/* To test the generic parser we will need to have:
		 * A grammar with rules that use the generic parser along with the other parsers.
		 * A scanner initialized with a program to be parsed
		 */
	}
}
