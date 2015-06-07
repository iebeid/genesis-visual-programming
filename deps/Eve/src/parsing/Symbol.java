/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parsing;

import java.util.Collection;
import java.util.LinkedList;
import java.util.TreeSet;

/**
 *
 * @author lmorell
 */
public class Symbol implements Comparable<Symbol> {

	/******************* Member variables********************/
	SymbolType type;     // indicates whether it is a terminal or non-terminal
	String value;         // indicates the particular  uniq id for the term or non-term
	// like non-terminal N, number is 3.945, id is transformer,
	// operator is like :=, <, <>!, !, etc
	final static boolean debug = false;
	boolean inGrammar = false;
	boolean dl = false;                 // true iff this symble derives lambda
	boolean repeating = false;
	char repetitionChar = '1';
	String separatorString = "";
	Grammar grammar;                    // containing grammar
	private String description; // reserved  for better messages ... future
	LinkedList<TrieNode> locations; // list of locations where this symbol appears

	// Interface implementation
	public int compareTo(Symbol s) {
		return value.compareTo(s.value);
	}

	/****************** Constructors *******************/
	private Symbol(Grammar g, String v) {
		type = SymbolType.NONTERMINAL;
		value = v;
		dl = false;
		description = "No description";
		locations = new LinkedList<TrieNode>();   // not in a trie yet !
		grammar = g;
	}

	// To facilitate symbols being unique to a grammar, we store the list of
	// symbols in the grammar, so that the symbol lists from different grammars
	// will not overlap.
	public static Symbol createSymbol(Grammar g, String v, char repetition, String sep) {
		if (g == null) {
			System.err.println("createSymbol: attempting to create a symbol "
					  + v + " for a non-existent grammar");
			System.exit(1);

		}
		Symbol symbol = g.symbols.get(v);   // first see if it is there
		if (symbol == null) {     // it is not
			symbol = new Symbol(g, v);    // create it and init it with string v
			if (v.equals("")) {
				symbol.setType(SymbolType.EOF);
			}
			else {
				char ch = v.charAt(0);
				// A nonterminal matches "<[a-zA-Z]"
				if (ch == '<' && v.length() > 1 && Character.isLetter(v.charAt(1))) {
					// probably need to generalize this for an arbitrary set of
					// pseudo-nonterminals
					if (v.equals("<id>")) {
						symbol.setType(SymbolType.ID);
					}
					else if (v.equals("<no>")) {
						symbol.setType(SymbolType.NUMBER);
					}
					else if (v.equals("<string>")) {
						symbol.setType(SymbolType.STRING);
					}
					else {
						symbol.setType(SymbolType.NONTERMINAL);
					}
				}
				else { // must be a terminal that does not exist
					if (Character.isLetter(ch)) {
						symbol.setType(SymbolType.ID);
					}
					else if (Character.isDigit(ch)) {
						symbol.setType(SymbolType.NUMBER);
					}
					else if (ch == '"' || ch == '\'') {
						symbol.setType(SymbolType.STRING);
					}
					else if (ch == '\'') {
						symbol.setType(SymbolType.CHAR);
					}
					else {
						symbol.setType(SymbolType.OPERATOR);
					}
					//   symbol.firstSet.put(symbol,null);  // it is its own symbol; there
					// is no TrieNode accessible from
					// a terminal, so the corresponding
					// TrieNode is empty
				}
			}

			symbol.setDerivesLambda(false);
			//g.symbols.put(v, symbol);   // add it to the list of symbols
		}
		else { // the base symbol was there, see if it has the same repetition
			// factor and separator
			if (symbol.repetitionChar != repetition || !symbol.separatorString.equals(sep)) {
				// There is a difference; create a new symbol
				symbol = new Symbol(g, v);    // create it and init it with string v
				if (v.equals("")) {
					symbol.setType(SymbolType.EOF);
				}
				else {
					char ch = v.charAt(0);
					// A nonterminal matches "<[a-zA-Z]"
					if (ch == '<' && v.length() > 1 && Character.isLetter(v.charAt(1))) {
						symbol.setType(SymbolType.NONTERMINAL);
					}
					else { // must be a terminal
						if (Character.isLetter(ch) ) {
							symbol.setType(SymbolType.ID);
						}
						else if (Character.isDigit(ch)) {
							symbol.setType(SymbolType.NUMBER);
						}
						else {
							symbol.setType(SymbolType.OPERATOR);
						}
						//   symbol.firstSet.put(symbol,null);  // it is its own symbol; there
						// is no TrieNode accessible from
						// a terminal, so the corresponding
						// TrieNode is empty
					}
				}
			}
			// the symbol was already there
			else {
				if (Character.isLetter(v.charAt(0)) ) { // if a char or _, then it must be a key
					symbol.inGrammar = true;
					symbol.setType(SymbolType.KEYWORD);

				}
			}
		}
		return symbol;
	}

	public static Symbol createSymbol(Grammar g, String v) {
		Symbol s = createSymbol(g, v, '1', "");
		return s;
	}

	public static Symbol createSymbol(Grammar g, String v, String d) {
		Symbol s = createSymbol(g, v, '1', "");
		s.description = d;
		return s;
	}

	/*************** Implementation of Comparable interface *************
	 * @param s symbol to be compared
	 */
	/************** Getters and setters *****************
	 * @return the associated set of first symbols
	 */
	public Trie getTrie() {
		if (isNonTerminal()) {
			return grammar.nonterminals.get(this);
		}
		else {
			Trie trie = grammar.terminals.get(this);
			return trie;
		}
	}

	/**
	 *
	 * @return The first set of the symbol as a Collection
	 */
	public Collection<Symbol> getFirstSet() {
		Trie t = getTrie();
		return getTrie().getFirstSet();
	}

	public SymbolType getType() {
		return type;
	}

	public void setType(SymbolType st) {
		type = st;
	}

	/**
	 * Set the string value of the symbol
	 * @param v
	 */
	public void setValue(String v) {
		value = v;
	}

	public String getValue() {
		return value;
	}

	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	public TreeSet<TrieNode> getFirstOf() {
		return getTrie().getFirstOf();
	}

	public LinkedList<TrieNode> getLocations() {
		return locations;
	}

	// Mutator operators
	// Accessor operators
	public boolean isNonTerminal() {
		return type == SymbolType.NONTERMINAL;
	}

	public boolean isTerminal() {
		return type != SymbolType.NONTERMINAL;  // note this includes PSEUDONONTERMINAL
	}

	public boolean isId() {
		return type == SymbolType.ID;
	}

	public boolean isKeyword() {
		return type == SymbolType.KEYWORD;
	}

	public boolean isNumber() {
		return type == SymbolType.NUMBER;
	}

	public boolean isString() {
		return type == SymbolType.STRING;
	}

	public boolean isEof() {
		return type == SymbolType.EOF;
	}

	public boolean isOperator() {
		return type == SymbolType.OPERATOR;
	}

	 public boolean derivesLambda() {
		return dl;
	}

	 public boolean isNullable() {
		return dl;
	}

	public void setDerivesLambda(boolean derivesLambda) {
		this.dl = derivesLambda;
	}

	// Abstract operations
	/**
	 *
	 * @param in the input stream to parse from
	 * @return true iff the parsing was successful
	 */
	public Instruction parse(Scanner scanner) {
		// Find the parser associated this this symbol
		Trie trie = getTrie();

		Parser p = trie.getParser();  // get the parser associated with this symbol
		return p.parse(scanner);

	}

	public Grammar getGrammar() {
		return grammar;
	}

	// Overriden operations
	@Override
	public String toString() {

		if (debug) {
			return value + "[" + type + "," + dl + "]";
		}
		else {
			return value;
		}
	} //override this

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Symbol other = (Symbol) obj;
		if ((this.value == null) ? (other.value != null) : !this.value.equals(other.value)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 41 * hash + (this.value != null ? this.value.hashCode() : 0);
		return hash;
	}
	/**
	 * @return a description suitable for use in error messages related to the symbol
	 *
	 */
}
