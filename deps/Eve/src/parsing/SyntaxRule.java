package parsing;

/**
 * Defines the structure of a grammar rule as a sequence of Symbol's.
 * @author Larry Morell
 */
import java.util.LinkedList;

public class SyntaxRule {

	private LinkedList<Symbol> rule;
	private int pos;
	private StringBuffer s;
	Grammar grammar;

	// ------------- Constructors -------------------
	/**
	 * Constructor that parses a rule and store as a sequence of Symbol'sb.
	 * Non-termainals are surrounded by <[letter]...>; blanks are separators
	 * @param g grammar associated with rule r
	 * @param r grammar rule as string of symbols; angle brackets around nonterms
	 */
	public SyntaxRule(Grammar g, String r) {
		grammar = g;
		pos = 0;
		rule = new LinkedList<Symbol>();
		s = new StringBuffer(r);
		Symbol symbol = getSymbol();
		rule.add(symbol);
		symbol = getSymbol();
		if (symbol != null
				  && (symbol.getValue().equals("::=") || symbol.getValue().equals("->"))) {  // skip it
			g.deleteSymbol(symbol);
			symbol = getSymbol();
		}
		while (symbol != null) {
			rule.add(symbol);
//         System.out.println("symbol = '" + symbol + "'");
			symbol = getSymbol();
		}
		// if empty production, set derives lambda in the lhs
		if (rule.size() == 1) {  // no rhs
			rule.get(0).setDerivesLambda(true);
		}
	}

	private void skipBlanks() {
		// System.out.println("got here with pos=" + pos);
//      System.out.println("sb = " + sb);
		while (pos < s.length() && s.charAt(pos) == ' ') {
			pos++;
		}
	}

	final public Symbol getSymbol() {

		Symbol symbol = null;
		skipBlanks();
		char factor = '1';
		String separator = "";
		if (pos < s.length()-1) {
			if (s.charAt(pos) == '<'
					  && Character.isLetter(s.charAt(pos + 1)))
			{  // symbol is a non-terminal
				StringBuilder nonTerminal = new StringBuilder("");
//            pos++;
				// Process up to the first digit, *, ?, or +
				while (pos < s.length() && s.charAt(pos) != '>'
						  && s.charAt(pos) != '?'
						  && s.charAt(pos) != '+'
						  && !Character.isDigit(s.charAt(pos))) {
					nonTerminal.append(s.charAt(pos));
					pos++;
				}
				if (s.charAt(pos) != '>') { // A repetition factor must be present
					if (Character.isDigit(s.charAt(pos))) { // must be numeric
						// ignore for future extension
					}
					else {
						factor = s.charAt(pos);
					}
					// Now check for an optional comma and single character
					pos++;
					if (s.charAt(pos) == ',') {
						pos++;
						separator = "" + (s.charAt(pos));
						pos++;
					}
					if (s.charAt(pos) != '>') {
						System.err.println("Expecting a >");
						System.exit(pos);
					}
				}
				nonTerminal.append(s.charAt(pos));

				// At this point we have found what looks like a non-terminal.
				// if it is actually <id>, <string>, or <no> then it is a pseudo-NT
				// which means it is actually a terminal
				String nt = new String(nonTerminal);
				if (nt.length() != 0) {
					symbol = grammar.createSymbol(nt,factor,separator);  // note that createSymbol handles
					// issues regarding pseudo-nt's
				}
				else {
					System.err.println("Bad grammar non-terminal");
					System.exit(pos);
				}
				pos++;
			}
			else {  // the symbol is a terminal
				StringBuilder terminal = new StringBuilder("");
				while (pos < s.length() && s.charAt(pos) != ' ') {
					terminal.append(s.charAt(pos));
					pos++;
				}
				symbol = grammar.createSymbol(new String(terminal), factor, separator);
			}
		}
		return symbol;
	}

	/**
	 *
	 * @return the number of elements in the right-hand side
	 */
	public int length() {
		return rule.size();
	}

	/**
	 * #return the NonTerminal on the left-hand side
	 */
	public Symbol lhs() {
		return rule.get(0);
	}

	/**
	 *
	 * @return a copy of the rule, with the first element removed
	 */
	public LinkedList<Symbol> rhs() {
		LinkedList<Symbol> r = new LinkedList<Symbol>();
		for (int i = 1; i < rule.size(); i++) {
			r.add(rule.get(i));
		}
		return r;
	}

	/**
	 *
	 * @param i position in the rule
	 * @return the Symbol at that position
	 */
	public Symbol elementAt(int i) {
		return rule.get(i);
	}

	/**
	 *
	 * @return the number of Symbol's in the rule
	 */
	public int size() {
		return rule.size();
	}

	/**
	 *
	 * @return  the rule as a string
	 */
	@Override
	public String toString() {
		int i;
		StringBuffer sb = new StringBuffer();
		sb.append(rule.get(0));
		sb.append(" ::= ");
		for (i = 1; i < size(); i++) {
			sb.append(rule.get(i));
			sb.append(' ');
		}
		return new String(sb);
	}
}
