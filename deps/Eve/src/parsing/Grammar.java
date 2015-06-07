/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parsing;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Stack;

/**
 * A Grammar is an administrative unit which associates (N,T,P,S) where
 * <ul>
 *  <li>  N is a set of non-terminals, </li>
 *  <li>  T is a set of terminals, </li>
 *  <li>  P is a set of production rules of the form N := (N U T)*, </li>
 *  <li>  S is the default symbol for parsing </li>
 *  </ul>
 *
 * @author Larry Morell (lmorell@atu.edu)
 */
public class Grammar {

	LinkedHashMap<Symbol, Trie> terminals; // quick access to Terminal's
	LinkedHashMap<Symbol, Trie> nonterminals;   // stores all rules
	// Set it up so that there will be only one instance of any given symbol
	// Do so by creating a hash table of symbols already seen
	private Symbol start;
	HashMap<String, Symbol> symbols = new HashMap<String, Symbol>();
	Token current;
	Buffer buffer;
	Stack<Buffer> bufferStack;

	/**
	 * Builds a grammar with no terminals, nonterminals or rules
	 */
	public Grammar() {
		terminals = new LinkedHashMap<Symbol, Trie>(100, 0.75F);
		nonterminals = new LinkedHashMap<Symbol, Trie>(200, 0.75F);
		start = null;
		current = null;
	}

	public boolean isSymbol(String s) {
		return symbols.get(s) != null;
	}

	public boolean isNonterminal(String s) {
		Symbol sym = symbols.get(s);
		if (sym == null) {
			return false;
		}
		return (nonterminals.get(sym) != null);
	}

	public boolean isTerminal(String s) {
		Symbol sym = symbols.get(s);
		if (sym == null) {
			return false;
		}
		return (terminals.get(sym) != null);
	}

	public Symbol createSymbol(String s, char rep, String sep) {
		Symbol symbol = symbols.get(s);
		if (symbol == null) {  // not there
			symbol = Symbol.createSymbol(this, s, rep,sep);    
         Symbol sym = symbols.get(symbol.getValue()); // see if it is defined
			if (sym == null) {
				symbols.put(symbol.getValue(), symbol);
			}
			// Note this adds (s,symbol) to
			// symbols
			// Not sure we need to distiguish between terms and nonterms
			Trie trie = new Trie(symbol);  // new one since the symbol did not exist
			if (symbol.isNonTerminal()) {  // addRule it to the appropriate list
				nonterminals.put(symbol, trie);  // nonterminals here
			} else {
				// here is where we handle the pseudo-nonterminals
				terminals.put(symbol, trie);     // terminals here
			}
		}
		return symbol;
	}

	public  Symbol createSymbol(StringBuilder b,char rep, String sep) {
		return createSymbol(new String(b),rep,sep);
	}
	public Symbol createSymbol(String s) {
		return createSymbol (s,'1',"");
	}
	public Symbol createSymbol(StringBuilder s) {
		return createSymbol (s,'1',"");
	}
	public void deleteSymbol(Symbol s) {
		if (s.isTerminal()) {
			terminals.remove(s);
		} else {
			nonterminals.remove(s);
		}
		symbols.remove(s.value);
	}

	public void addRule(SyntaxRule r, Instruction instr, Parser p) {
		// This builds/extends the trie for the lhs
		// For each symbol in the rhs, addRule it to the trie
		Trie t = nonterminals.get(r.lhs());

		t.addRule(r, instr, p);
	}

	public void setBuffer(Buffer b) {
		buffer = b;
	}

	public Token getToken() {
		Buffer b = this.buffer;
		String sb ;
		String result;
		Token t;
		int endPos;
		b.skipBlanks();
		int ln = b.lineNumber();
		int cn = b.columnNumber();
		if (b.eof()) { // must be eof
			t = new Token(createSymbol(""), b, ln, cn);
		} else if (Character.isLetter(b.peek())) {
			t = new Token(createSymbol(b.getId()), b, ln, cn);
		} else if (Character.isDigit(b.peek())) {
			t = new Token(createSymbol(b.getNumber()), b, ln, cn);
		} else if (b.peek() == '/') {
			int pos = b.currentPos();
			if (b.charAt(pos + 1) == '/') {
				sb = b.getToEoln();
				t = new Token(createSymbol(sb), b, ln, cn);
			} else if (b.nextChar() == '*') {
				b.get();
				b.get();  // advance past the *
				sb ="/*" + b.getToDelimiter("*/");
				t = new Token(createSymbol(b.getToDelimiter("*/")), b,ln, cn);

			} else { // must be an operator starting with /
				sb = b.getOperator();
				t = new Token(createSymbol(b.getOperator()), b, ln, cn);
			}
		} else { // must be an operator of some sort
			sb = b.getOperator();
			t = new Token(createSymbol(sb), b, ln, cn);

		}
		current = t;
		return t;
	}

	public Instruction parse(Symbol s, Scanner scanner) {
		 Instruction result = s.parse(scanner);

		return result;
	}
   public Instruction parse (Scanner scanner) {
		return parse(start,scanner);
	}
	public Token currentToken() {
		return current;
	}

	public void setStart(String s) {
		start = symbols.get(s);
		if (start == null){
			System.err.println("Invalid start symbol:" +'"' + s + '"');
			System.exit(1);
		}
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("<---------------------------------");
		result.append("\nNonTerminals: ");
		for (Trie t : nonterminals.values()) {
			result.append(t.getSymbol());
			result.append(",");
		}
		result.setCharAt(result.length() - 1, ' ');
		result.append("\nTerminals: ");
		for (Symbol t : terminals.keySet()) {
			result.append(t);
			result.append(",");
		}


		result.setCharAt(result.length() - 1, ' ');
		// Now print the First sets of each symbol
		// Now print the First sets of each symbol

		result.append("\nSymbol    FirstSet\n");
		for (Trie t : nonterminals.values()) {
			Symbol s = t.getSymbol();
			result.append(s.getValue());
			result.append("   " );
			result.append(s.getTrie().getFirstSet());
			result.append("\n");
		}
		result.append("--------------------------------->\n");
		return new String(result);
	}

	public Symbol getStart() {
		return start;
	}

	public static void readAllTokens(Buffer buffer, Grammar g) {
		buffer.skipBlanks();
		Token t;
		t = g.getToken();
		while (t.getSymbol().getType() != SymbolType.EOF) {
			System.out.println("Token read: " + t);
			t = g.getToken();
		}
	}

	public static void testToken(Grammar g) {
		System.out.println("Buffer test:");
		Buffer in = new Buffer("a");
		readAllTokens(in, g);
		in = new Buffer("    17.3");
		readAllTokens(in, g);
		in = new Buffer("/* this is the time for a \n comment on \n several lines */ \n");
		readAllTokens(in, g);
		in = new Buffer("// This is a comment \n wowie");
		readAllTokens(in, g);
		in = new Buffer("\n \t \r:= ");
		readAllTokens(in, g);
		in = new Buffer("/;");
		readAllTokens(in, g);
		in = new Buffer("This is a line of ids\n"
				  + "with some numbers:: 3 43 14.7 -55.883\n"
				  + "and some operators: + - * / // a comment\n"
				  + "/* a more \n    complicated \n comment */\n"
				  + "and some more weird operators: *^**%%%* ~#!#$$$^$@@`");
		readAllTokens(in, g);
		System.out.println("--------------------");
	}
	/**
	 * Builds a grammar from the following rules:
	 * <pgm> ::= <stmt>
	 * <stmt> ::= <move>
	 * <stmt> ::= <print>
	 * <move> ::= move <id>
	 * <print> ::= print <id>
	 * @return
	 */
   public static Grammar buildGrammar() {
       Grammar g = new Grammar();
		 String [] rules = new String [] {
	        " <pgm> ::= <stmt>",
           " <stmt> ::= <move>",
        	  " <stmt> ::= <print>",
           "<move> ::= move <id>",
	        "<print> ::=  <id> print",
		 };
		 // Add the rules to the grammar
		 SyntaxRule rule;
		 for (int i=0; i <rules.length; i ++)  {
           rule = new SyntaxRule(g,rules[i]);
			  g.addRule(rule,null,null);  // no instruction, no parser
		}
		 return g;
	}
	public static void test() {
		System.out.println("Grammar test");
		// Set up the grammar
		Grammar g = new Grammar();
		// testToken(g);
		// Put in a single rule
		Symbol s = g.createSymbol ("<pr>");
		//g.addRule(new SyntaxRule(g, "<pr> ::= print <no>\n"), new GenericParser(s));  // fix me
		// Add in an artificial rule for <no>
		s = g.createSymbol("<no>");
		//g.addRule(new SyntaxRule(g, "<no> 123"), new NoParser(s));  // fix me
		// Set up the input source and get the first token
		g.setBuffer(new Buffer("5"));
		Token t = g.getToken();
		// Ensure the first token is "print"
		System.out.println("First Token is " + t);
		// Set up the start symbol
		g.setStart("<no>");
		// Now let's start the parsing parade!
		Scanner scanner = new GenesisScanner (g,"parse this");
		g.parse(g.getStart(),scanner);
		/*
		GenesisIO infile = new GenesisIO("grammar.dat", "");
		if (infile == null) {
		System.err.println("File not found: ");
		System.exit(1);
		}
		String line = infile.readLine();
		while (line != null) {
		if (!line.matches("^ *$") && !line.regionMatches(0, "//", 0, 2)) {
		g.addRule(new SyntaxRule(g, line));
		System.out.println("Grammar:\n" + g.toString());
		} else {
		System.out.println(line);
		}
		line = infile.readLine();
		}
		 */
		//
		// Now add the predefined

		System.out.println("Grammar:\n" + g.toString());
	}
}
/*
Procedure ReadGrammar(Var  fn: string);
Procedure PrintGrammar(Var F: Text; Var G: Grammar);
Procedure PrintAllNonTerms (Var F: Text; G: Grammar);
Procedure PrintAllTerms (Var F: Text; G: Grammar);
Procedure PrintFirstSet (Var F: text; G: Grammar);
Procedure PrintFollowSet (Var F: text; G: Grammar);
Function NontermCnt (G:Grammar):integer;
Function TermCnt (G: Grammar):integer;
Function RuleCnt (G: Grammar ):integer;
Function StartSymbol (G: Grammar): Nonterm;
Function NontermStr  ( n : Nonterm ): SymbolStr;
Function TermStr ( t: Term ): SymbolStr;
Function Nthterm ( n: integer): term;
Function NthNonterm ( n : integer ): Nonterm;
Function NontermNum ( N: Nonterm): integer;
Function TermNum ( T: term): integer;
Function SyntaxRule (  i: integer; j: integer ): Symbol;
Function FormatRule ( i: integer; j: integer ): Symbol;
Function NumberOfRules ( n: Nonterm ):integer;
Function LengthOfSRule ( i:integer ): integer;
Function LengthOfFrule ( i: integer ): integer;
Function RuleNo (n: Nonterm; i: integer ): integer;
Procedure SymbolIndex ( i : integer; var n : Nonterm; var j : integer  );
Function KeyStroke ( n: Nonterm; i: integer ):char;
Function Isterm ( S: Symbol): Boolean;
Function IsNonterm ( S: Symbol): Boolean;
Function Termpos (S: Symbol): integer;
Function NontermPos (S: Symbol): integer;
Procedure First (G:  Grammar; S: Symbol; Var TS: Termset);
Procedure CopyTail ( Var T: Srule; P: Integer; Pos: integer);
Procedure ComputeFirst ( x: SRule;  (* working string *)
k: integer; (* length of working string *)
var Result: Termset);
 */


