/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parsing;

import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

/**
 * Represents the rules of a grammar as a trie. A path from the root of the
 * trie to a node that indicates the end of a rule is a rule in the grammar.
 * @author Larry Morell (lmorell@atu.edu)
 */
public class Trie {
   // ----------------------- Static variables ------------------------ //
	final static TerminalParser TERMINAL_PARSER = new TerminalParser();
//	final static NopInstruction NOP_INSTRUCTION = new NopInstruction();

	// -----------------------
	Symbol symbol;  // the contained symbol
	LinkedHashMap<Symbol, TrieNode> firstSet;   // {t in Terminal | this =>* t alpha}

	private TreeSet<TrieNode> firstOf;   // {N in Nonterminal | N => this alpha}
	TrieNode root = null;
	final static boolean debug = false;
	Parser parser;   // The parser associated with the root symbol of this Trie
	// This will most likely be a GenericParser

	/**
	 *  Build a Trie with a root no right-hands sides
	 * @param s Symbol for which this Trie represents the rules
	 */
	public Trie(Symbol s) {
		symbol = s;
		root = new TrieNode(s, this);
		root.instruction = NopInstruction.NOP_INSTRUCTION;
		root.nullablePrev = true;
		firstSet = new LinkedHashMap<Symbol, TrieNode>(); // {t in T | this =>* t alpha}
		firstOf = new TreeSet<TrieNode>();
		parser = TERMINAL_PARSER;  // most symbols are keywords, so default to this
	}

	public Trie(Symbol s, Parser p) {
		this(s);
		parser = p;

	}
	/**
	 * Gets the symbol at the root of this Trie
	 * @return  the symbol at the root of the trie
	 */
	public Symbol getSymbol() {
		return symbol;
	}

	public Instruction getInstruction() {
		return root.instruction;
	}

	/**
	 *
	 * @return The set of symbols of which this symbol is in the first
	 */
	public TreeSet<TrieNode> getFirstOf() {
		return firstOf;
	}

	/**
	 *
	 * @return The root node of the Trie.
	 */
	TrieNode getRoot() {
		return root;
	}

	public Collection<Symbol> getFirstSet() {
		Set<Symbol> keys = firstSet.keySet();
		// If there are no elements in the keySet, then it must be a terminal;
		// Put that terminal in the FirstSet
		if (keys.isEmpty()) {
			firstSet.put(symbol, null);
		}
		// and return it (or whatever was there originally
		return firstSet.keySet();
	}

	public Collection<TrieNode> getFirstNodes() {
		return firstSet.values();
	}

	TrieNode firstNode(Symbol s) {
		TrieNode tn = firstSet.get(s);
		return tn;
	}

	boolean firstContains(Symbol s) {
		return firstSet.keySet().contains(s);
	}

	public Parser getParser() {
		return parser;
	}

	/**
	 * If tn holds a terminal it is added to the first set
	 * otherwise the first set of tn is unioned to the first set
	 * of the trie.
	 * @param tn  tree node that holds the symbol to be updated
	 * #return The set of TrieNodes newly added to the the first set
	 */
	TreeSet<TrieNode> addFirstSymbol(TrieNode tn) {
		boolean success = false;
		Symbol s = tn.getSymbol();  // symbol contained in the node
		TreeSet<TrieNode> update = new TreeSet<TrieNode>();
		if (s.isTerminal()) { // add in terminals
			if (firstSet.put(s, tn) == null) {  // add successful, add to update as well
				update.add(tn);
			}
			else {
				System.err.println("Duplicate " + s + "added to first set for  " + this.symbol);
			}

		}
		else { // tn holds a nonterminal
			// First, update the firstOf set of tn
			// Trie trie1 = tn.getTrie();    // the
			Trie trie1 = tn.getSymbol().getTrie();
			TreeSet<TrieNode> ts1 = trie1.getFirstOf();
			TrieNode tn1 = getRoot();
			boolean add = ts1.add(tn1);


			// for each symbol in the first(tn), add it to the first set of this trie
			Collection<TrieNode> keys = tn.getSymbol().getTrie().getFirstNodes();  // all the symbols to be added
			for (TrieNode trieNode : keys) {
				Symbol sym = trieNode.getSymbol();
				if (sym.isTerminal()) {
					if (firstSet.put(sym, trieNode) == null) { // sym was newly added, record this
						update.add(trieNode);
					}
					else {
						System.out.println("Duplicate " + trieNode + " added to first set for " + symbol + "\n");
					}
				}
				else { /* ignore any nonterminals -- in case these are added in the future */ }
			}  // for
		} // else
		// Return the set of nodes that were newly added to the first set of the lhs
		return update;
	} // addFirstSymbol

	/**
	 *
	 * @param ts set of nodes to be added to the set
	 * @return true if a duplicate was found
	 */
	boolean addFirstSet(TreeSet<TrieNode> ts) {
		// Need to add each one in separately because if any one is
		boolean duplicate = false;
		for (TrieNode tn : ts) {
			if (firstSet.put(tn.getSymbol(), tn) != null) {
				duplicate = true;
				System.err.println("Duplicate add of '" + tn + "' to the first set of trie:\n "
						  + this);
			}
			else {
				if (debug) {
					System.err.println("Non-Duplicate add of " + tn + " to the first set of "
							  + this);
				}
			}
		}
		return !duplicate;
	}

	/*
	 * Propagate a set terminals to each nonterminal that left-derives them.
	 * @param update  The set of terminals to be propagated.
	 */
	private void propagateFirst(TreeSet<TrieNode> update) {
		// Union 'update' to the first set of each trie whose root symbol is s
		// where  this.lhs is in the s.firstOf;

		// For each nonterminal N that left-derives the root of this trie
		//    N.firstSet U= update
		// This is accomplished via a standard info-flow, breadth-first,
		// algorithm, starting first at the root of this trie,
		// and propagating from any node which is updated
		//
		// s.addFirstSet(update) returns false if left recursion occurs.
		// Since update never changes, adding update twice will fail and
		// infinite recursion in therefore avoided.

		TreeSet<Symbol> working = new TreeSet<Symbol>();
		working.add(symbol);  // start from the root of this trie

		while (!working.isEmpty()) {
			Symbol w = working.first();  // get the first element nonterm
			working.remove(w);             // toss it
			// For each s, s =>* w alpha
			for (TrieNode tn : w.getFirstOf()) {
				Trie trieToUpdate = tn.getTrie();
				if (trieToUpdate.addFirstSet(update)) { // update firstset
					working.add(tn.getSymbol());  // propagate from s if s.firstSet is updated
				}
				else { // firstSet was not updated, meaning there was a duplicate
					System.err.println("Propagage first could not update" + tn.getSymbol() + " with "
							  + update);
				}
			}  // for
		} // while
	}

	/**
	 * Propagates derives lambda to all nonterminals which left-derive nt.
	 * and updates the first sets, if needed
	 * @param nt The nullable nonterminal that begins the propagation
	 */
	void propagateDL(Symbol nt) { // nt is a nonterminal that is nullable
		/*
		 * We begin with the nonterminal
		 */
		if (nt.isTerminal()) {
			return;
		}
		nt.setDerivesLambda(true);
		LinkedList<Symbol> w = new LinkedList<Symbol>();
		w.add(nt);
		do {
			Symbol n = w.getFirst();
			w.removeFirst();   // extract the first
			// For all the locations that n occurs
			for (TrieNode tn : n.getLocations()) {
				if (tn.nullableRest) {  // there is a nullable path to a rule end
					// Ascend and set nullableRest until root is reached or non-nullable
					// nt is encountered
					do {
						tn = (TrieNode) tn.getParent();
						tn.nullableRest = true;
					} while (tn.symbol.derivesLambda() && tn.getParent() != null);

					// atroot => nullable path all the way from the root to a rule end
					if (tn.getParent() == null
							  && tn.symbol.isNonTerminal()
							  && !tn.symbol.derivesLambda()) {  // last condition to prevent loops
						tn.symbol.setDerivesLambda(true);
						w.add(tn.symbol); // propagateDL the symbol at the root of this trie
					}
				}
				else  // future ??   Removed semicolon here, b/c it looked wrong, but
					// need to think more about this when I am awake.
					// The comment below seemse to imply that the semicolon was wrong
					// since tn is nullable only if we are in the else block
					// Furthermore it appears that that comment should have read:
					// Since tn is nullable, if there is a nullable path to tn, then
					// there may be new elements in the first set of the root of the
					// current trie.   Hence the test for nullablePrev.
				// Update first sets if necessary; since tn is nullable, elements
				// reachable from tn will now contribute to the first set of the lhs
				// at the root of the Trie

				if (tn.nullablePrev) {
					// There is a nullable path all the way to tn, so we do a breadth-
					// first search for all elements reachable via a nullable path
					TreeSet<TrieNode> update = new TreeSet<TrieNode>();
					LinkedList<TrieNode> working = new LinkedList<TrieNode>();
					working.add(tn);

					while (!working.isEmpty()) { // working is a breadth-first Queue
						TrieNode t = working.getFirst();
						working.remove(t);
						Enumeration children = t.children();
						while (children.hasMoreElements()) {
							TrieNode child = (TrieNode) children.nextElement();
							child.nullablePrev = true;
							// We have possible new first elements for our NT in tn
							update.addAll(child.getFirstNodes());
							tn.getTrie().addFirstSymbol(child);
							if (child.symbol.derivesLambda()) {
								working.add(child);
							}
						}
					} // while bf traversal
					// At this point the root of the Trie has
					if (!update.isEmpty()) {
						// This is the sole reason for having trie as a field in
						// tn, to save repeated traversals up the trie to find the root
						// Add it first to tn's root symbol
//                  tn.getTrie().getChild().addFirstSet(update);
						tn.getTrie().propagateFirst(update);
					}
				}
			}
		} while (!w.isEmpty());
	}

	public void setParser(Parser p) {
		parser = p;
	}

	/**
	 * Adds a rule to the trie.
	 * @param r The rule to be added.
	 */
	void addRule(SyntaxRule r, Instruction instr, Parser p) {
		/* This is the heart of the class.  A rule is added by descending
		 * through the existing Trie, adding nodes as needed.
		 * The following invariants are maintained for the trie's nodes:
		 *    nullablePrev = true iff the path from a root to the parent of node
		 *                        is nullable
		 *    nullableRest = true iff the path after the current node to the
		 *               end of the rule is nullable
		 */

		parser = p;
		int i;
		Symbol lhs = r.elementAt(0);
		System.out.println("Adding grammar rule " + r);
		TreeSet<TrieNode> update = new TreeSet<TrieNode>();
		if (!r.lhs().equals(symbol)) {
			System.err.println("Attempting to add a rule with lhs of " + r.lhs()
					  + "to a Trie for " + root);
			System.exit(1);
		}

		//  For each symbol in the rhs rule r, addRule it to the trie
		TrieNode tn = root;
		TrieNode prev = root;
		tn.nullablePrev = true;  // for propagation purposes

		Symbol s;

		if (r.size() > 1) {  // not the empty rule
			// add in the first symbol; if the number of children changes, then
			// that was a new node.
			prev = tn;

			tn = tn.addChild(r.elementAt(1));
			if (tn.newNode()) {
				tn.nullablePrev = true;    // null occurs before the first of rhs


				update.addAll(addFirstSymbol(tn));
			}
			// update.add(tn);

			for (i = 2; i < r.size(); i++) {
				s = r.elementAt(i);
				if (tn.nullablePrev && tn.symbol.derivesLambda()) {
					tn = tn.addChild(s);
					if (tn.newNode()) {  // newly added node
						tn.nullablePrev = true;
						// Update first set of the lhs to include the first set of s
						// Union the newly added element to update
						update.addAll(addFirstSymbol(tn));
					}
					// update.add(tn);
				}
				else {
					tn = tn.addChild(s);
				}
			}
			// We have just added in the last node of a rule
			tn.setEndOfRule(true);
			if (tn.isLeaf()) {
				tn.nullableRest = true;
			}
			else {
				tn.nullableRest = false;
			}
			// IMPORTANT: there may be an issue here.  If a shortened version
			// of a rule is added after a longer version of a rule, then
			// if the tail is nullable, then we have an ambiguity in the
			// language b/c there are two different parses.  So I have assumed
			// this will not be true and set nullableRest to false.
			// The problem is that I don't think the processing will actually
			// catch the ambiguity.
			// If we've changed the first set, propagate it
			if (!update.isEmpty()) {  // First(lhs) updated; propagate
				propagateFirst(update);
			}

			// If this and all previous nodes are nullable, propagate root symbol
			if (tn.symbol.derivesLambda() && tn.nullablePrev) {
				propagateDL(tn.symbol);
			}
		} // rule size > 1
		else { // we have a null production; propagateDL
			s = r.elementAt(0);
			System.out.println("Adding null production for " + s);
			propagateDL(s);
			// Add in the instruction at the root of the trie

		}
		// tn is at root if this is a null production, otherwise
		// it is
		tn.instruction = instr;
		// System.out.println("--------------------");
	}

	void dftraverse(TrieNode tn, int indentation, StringBuffer buffer) {
		if (tn == null) {
			return;
		}
		for (int i = 0; i < indentation; i++) {
			buffer.append(' ');
		}

		buffer = buffer.append(tn);
		buffer.append("\n");
		int count = 0;
		// Ok, I simply don't get this.  To get rid of an "unchecked conversion"
		// I had to drop the generic from Enumeration<TrieNode> e.
		// But then I had to cast e.nextElement(), which I personally consider
		// worse.  So why doesn't the compiler complain about an "unchecked cast"
		// at that point when converting the object returned by nextElement???
		// I am mystified.
		// In looking online, I found that Java does not know the parameter
		// type at runtime which means that a weird situation can occur in
		// which a variant on a parameterized type can be assigned to a variable,
		// but no error will be issued until a member of the variable.   The
		// error would be like "wrong class exception". Same article indicates
		// that you cannot create an array whose base type is a parameterized
		// type, b/c an A[] is super class of B[] if A is a super class of B.
		// However A[] is not a super class of A<E>[].
		// Apparently it is more difficulty to debug a situation in which
		// the program dies due to a method is invoked on an object that
		// does not have that object, by, say, assigning a List<String>
		// to a List<Integer>, and then trying to invoke a List<Integer>
		// operation on it. (Java apparently does not make the type tag
		// available at runtime, so it will allow the statement.  My
		// remaining question is, how doe it know that wrong operation is
		// being invoked?  Is is possible some operations will actually
		// work?  If it detects an error for each operation, why cannot
		// the JRE detect the error as assignment?

		Enumeration e = (Enumeration) tn.children();
		indentation += 3;
		while (e.hasMoreElements()) {
			dftraverse((TrieNode) e.nextElement(), indentation, buffer);
//         buffer.append("\n");
		}
		indentation -= 3;
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer(1000);
		dftraverse(root, 0, result);
		return result.toString();
	}
}
