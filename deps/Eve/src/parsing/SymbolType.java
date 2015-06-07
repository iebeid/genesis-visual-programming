/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package parsing;

/**
 *
 * @author lmorell
 */
public enum SymbolType {   ID,  // terminal that begins with a char or underscore, 
                                        // not previously defined
                                 KEYWORD, // terminal that as previously defined
                                 NUMBER,  // terminal that begins with a digit
                                 OPERATOR,   //terminal that begins witha any other char
                                 NONTERMINAL, // only designation for non-terminal
											PSEUDONONTERMINAL, // except for this one!
											STRING,  // String literal  " ... "
											CHAR,     // Character literal 'X'
                                 COMMENT,  // Any form of comment
                                 EOF, // end of file
                                 UNKOWN,   // placeholder for a generic Symbol
   };
