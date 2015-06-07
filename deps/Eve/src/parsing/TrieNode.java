package parsing;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Implements the nodes used in a Trie. Each node holds a symbol and related information
 * @author lmorell
 */
public class TrieNode extends DefaultMutableTreeNode
		  implements Comparable<TrieNode> {
	// class fields

	Symbol symbol;   // aliased to userObject
	Instruction instruction; // Builds an instruction list
	boolean nullablePrev;
	boolean nullableRest;  // Some path from a child of this node to a leaf contain symbols
	// that are nullable
	boolean endOfRule; // Does this node form the end of a rule?
	boolean justAdded;  // true iff this is a new node
	Trie trie;           // pointer to the trie of which this node is a part
	// Boy, I don't mind wasting space.  Really though, there
	// are not like to be more than a 1000 nodes in this tree
	// so does it really matter?
	final static boolean debug = false;

	public int compareTo(TrieNode tn) {
		return symbol.compareTo(tn.symbol);
	}

	public TrieNode(Symbol s, Trie t) {
		userObject = s; // These should remain in tandem
		symbol = s;      // because all the implementation code below uses symbol
		nullablePrev = false;
		nullableRest = false;
		endOfRule = false;
		trie = t;
		justAdded = true;
		instruction = null;

	}

	// Getters and setters
	public Instruction getInstruction() {
		return instruction;
	}

	public void setInstruction(Instruction instruction) {
		this.instruction = instruction;
	}

	public Collection<TrieNode> getFirstNodes() {
		if (symbol.isNonTerminal()) {
			return symbol.getTrie().getFirstNodes();
		}
		else {  // it is terminal
			TreeSet<TrieNode> ts = new TreeSet<TrieNode>();  // confirm this
			ts.add(this);
			return ts;
		}
	}

	public Symbol getSymbol() {
		return symbol;
	}

	public Trie getTrie() {
		return trie;
	}

	/**
	 *
	 * @param i  The position of the child from which to retrieve the symbol
	 * @return The node added
	 */
	public Symbol getChild(int i) {
		return ((TrieNode) getChildAt(i)).symbol;

	}

	/**
	 * 
	 * @param symbol The symbol sought
	 * @return
	 */
	/**
	 * 
	 * @param symbol
	 * @return The first child which has symbol in its firstSet
	 */
	public TrieNode getChild(Symbol symbol) {

//	   Collection <TrieNode> children = tn
		Enumeration nodes = children();
		boolean found = false;
		TrieNode tn = null;
		// It is ugly code like this that demands a change to the programming language
		// or at least the design of enumerators
		while (nodes.hasMoreElements() && !found) {
			tn = (TrieNode) nodes.nextElement();
			Collection<Symbol> firstSet = tn.getSymbol().getFirstSet();

			found = firstSet.contains(symbol) || tn.symbol.isNullable() ;
		}
		if (found) {
			return tn;
		}
		else {
			return null;
		}
	}

	public TrieNode getFirst() {
		if (numberOfChildren() == 0) {
			return null;
		}
		return (TrieNode) getFirstChild();
	}

	public boolean isEndOfRule() {
		return endOfRule;
	}

	/**
	 * 
	 * @return The number of children of the node
	 */
	public int numberOfChildren() {
		return getChildCount();
	}

	public void setEndOfRule(boolean endOfRule) {
		this.endOfRule = endOfRule;
	}

	public boolean newNode() {
		return justAdded;
	}
	// Mutators

	/**
	 * Add a symbol as a child of the current node, sorted by s, if it does not exist.
	 * @param s
	 * @return
	 */
	TrieNode addChild(Symbol s) {
		// if it is there already just return it
		TrieNode tn = null;
		int size = getChildCount();
		int i;
		for (i = 0; i < size && s.compareTo(getChild(i)) > 0; i++) {
			//  System.out.println(""+ s + " < " + getChild(i));
		}  // linear search; no body
		if (i < size) { // found a position to insert s into
			if (s.compareTo(getChild(i)) < 0) { // insert at position i
				tn = new TrieNode(s, trie);
				Instruction inst;


				insert(tn, i);

				// link this into the list of TrieNodes
				// associatied with the corresponding symbol
				s.locations.addFirst(tn);
			}
			else { // the value is already at position i
				tn = (TrieNode) getChildAt(i);
				tn.justAdded = false;
			}
		}
		else { // The value was not found, append it
			tn = new TrieNode(s, trie);


			add(tn);
			s.locations.addFirst(tn);  // link this into the list of TrieNodes
			// associatied with the corresponding symbol
		}

		return tn;  // returning this directs the calling alg to continue at tn
	}

	public Instruction createInstruction(InstructionList il) {
		if (instruction == null) {
			System.err.println("Attempt to create an instruction from a null instruction");
			return null;
		}
		return instruction.createInstruction(il);
	}

	@Override
	public String toString() {
		if (debug) {
			return symbol.toString() + " [" + nullablePrev + "," + nullableRest + "]";
		}
		else {
			return symbol.toString();
		}
	}
}
