package runtime;

// EnvironmentVal.java
// This file contains the EnvironmentVal class
// for the Genesis compiler / interpreter.
// Author: Wes Potts
// 4/9/04
//
// Modified:
//
// creation to 6/14/04        - Some modifications were not logged.
// 6/14/04                - renamed the file
//                           made some methods static
//                           added 2 members for lexical and dynamic scoping 
// -- uh oh ------------- started to make some methods static but couldn't.
//                           They refer to an instance to look up names.
//  7/12/04 -- wdp: added a method to remove all vars except functions 
//              and procedures 
//  7/31/04 -- ljm: added error message for attempt to subscript non-list
//  8/13/04 -- ljm: ensured that copies of stickynotes are made only if necessary
//  9/9/04  -- ljm: added null check for condition on a subscripted list
//  2/14/08 -- ljm: modified wrapList to also wrap strings



import java.io.PrintStream;
import java.util.*;

/**
 * Wrapper for a HashMap to map strings to StickyNotes.  This represents the
 * memory map for Genesis
 * @author Larry Morell <morell@cs.atu.edu>
 */
public class EnvironmentVal extends Val
{
  protected HashMap<String,StickyNote> ht;
                                                    // from which this environment was called
                                               //
  /**
	* Create an empty environment
	*/
  public EnvironmentVal()
  {
    ht = new HashMap<String,StickyNote>();
   
  }


  // lexical scoping by default
  /**
	* Create a new environment with s as the lexically containing environment
	* @param s
	*/
  public EnvironmentVal( EnvironmentVal s )
  {
    ht = new HashMap<String,StickyNote>( s.ht );
    
  }


  // , then dynamic parent
  /**
	* Creates an environment from s and t
	* @param s The lexically containing environment
	* @param t The dynamically containing environment
	*/

  public EnvironmentVal( EnvironmentVal s, EnvironmentVal t )
  {
    ht = new HashMap<String,StickyNote>( s.ht );
    
  }

  /**
   * This method is the "nuts and bolts" of the EnvironmentVal class.  Its function
   * is to insert a (key, value) pair into the internal hash table in
   * the EnvironmentVal.  The key will be a string representing an identifier in
   * the source code.  The value will be a StickyNote representing the
   * right-hand-side of a "let s name v" statement.
	*/
  public StickyNote insert( String key, StickyNote value )
  {
    StickyNote n = null;
	 StickyNote sn = new StickyNote(value);
//    try
//    {
      n = (StickyNote)ht.put( key, sn );
//    }
//    catch ( NullPointerException e )
//    {
//      System.err.println(
//          "Error: passing a null pointer to Scope.insert( String key, Value value )\n\t"
//          + e.toString() );
//    }
    return ht.get(key);
  }
  /**
	* Lookup key in the environment and return the associated StickyNote
	* @param key  -- the lookup key
	* @param value -- the associated value
	* @return
	*/
  StickyNote insert(String key, Value value) {
	  return ht.put(key,new StickyNote(value));
  }

  /**
	* This method copies the info from another EnvironmentVal into the current EnvironmentVal.
	*
	* @param s
	*/

  public void insert( EnvironmentVal s )
  {
    try
    {
      ht.putAll( s.ht );
    }
    catch ( NullPointerException e )
    {
      System.err.println(
          "Error: passing a null pointer to Scope.insert( Scope s )\n\t"
          + e.toString() );
    }
  }

  /**
	*  This method deletes a (key, value) pair from the EnvironmentVal.
	*/
  public StickyNote del( String key )
  {
    StickyNote n = null;
    try
    {
      n = (StickyNote)ht.remove( key );
    }
    catch ( NullPointerException e )
    {
     System.err.println(
          "Error: passing a null string to Scope.del( String key )\n\t" 
          + e.toString() );
    }
    return n;
  }

  /**
	* @return The StickyNote associated with key; null if not found
	*/
  public StickyNote find( String key )
  {
    StickyNote n = (StickyNote)ht.get( key );
      return n;
  }

  /**
	*
	@return Returns the value for "key" or "null" if "key" is not in EnvironmentVal.
       This implements transparency, i.e., if the key denotes an iterator
       for a list, then search returns the value that iterator references
	*/
  public StickyNote search( String key )
  {
     // System.out.println ("Searching for " + key);
    StickyNote  n = (StickyNote)ht.get( key );
           //if (n == null) System.out.println("awgg!!!");
          //System.out.println("n =" + n);
          // System.out.println("n.getClass() =" + n.getClass());
          //System.out.println("n instanceof ListVal n ="+ n +
           //                (n != null && n instanceof ListVal)) ;
   //System.out.println ("Search completed with " + key + " mapped to " +n );
         if (n != null)
            if (n.getVal() instanceof ListVal)
               n =  ((ListVal)n.getVal()).get();
     //System.out.println("Returning from search with: " + n);
    // Note that if n is null, it simply gets returned
    return n;
  }

  /**
	*
	* @param key
	* @return the StickyNote associated with the key
	*/
  public StickyNote searchRef( String key )
  {
    StickyNote  n = (StickyNote)ht.get( key );
    return n;
  }

  /////////////////////////////////////////////////
  /**
	*  Removes all variables from the environment except functions and procedures
	*/
  //
public  void deleteAllVars()
  {
    Iterator<String> e = ht.keySet().iterator();
    String s;
    while ( e.hasNext() )
    {
      s = (String)e.next();
      if ( s.charAt( 0 ) != '*' ) del( s );
    }
  }

  /////////////////////////////////////////////////
  // methods for printing the contents of the EnvironmentVal
/**
 * Prints the contents of the environment
 * @param io
 */
public  void printAll( PrintStream io )
  {
    Iterator<String> e = ht.keySet().iterator();
    String s;
    while ( e.hasNext() )
    {
      s = (String)e.next();
      io.println( s + " = " + search( s ) );
    }
  }
  /////////////////////////////////////////////////
// /**
// * This is a utility method for wrapping a raw list value or string as a
// * ListVal.  The parameter "s" is an identifier such as
//  * "myList" where a statement like "let myList name < 1 2 3 >"
//  * appears in the source code.  This method fetches the raw list
//  *data for < 1 2 3 >, wraps it as a ListVal, and returns the
//  * new ListVal.
//  */
//  public ListVal wrapList( String s )
//  {
//    StickyNote sn = search( s );
//    //System.out.println ("Wrapping " + sn);
//    if (sn != null && sn instanceof StringVal) {
//    //System.out.println ("Wrapping " + sn);
//       sn  = Val.createVal(Node.str2Node(sn.toString()));
//        // new ListVal(Node.str2Node(sn.toString()));
//    }
//    if ( sn == null || ! (sn instanceof Node) && sn != null) {   // ljm: 7/31/04 & 8/28/04 &9/8/09
//       System.err.println(s + " is not a list and therefore cannot be subscripted");
//    }
//    ListVal l;
//      // ljm: 7/31/04
//    l = new ListVal( sn );  // ljm: 8/13/04 -- invoke using sn not gv
//    l.reset();
//    return l;
//  }

  ///////////////////////////////////////////////////
  // methods for "naming" and "aliasing"
  // to give a value a name as in "let x name 3"
  // or "let x name max of 4 and 6"

  /**
	* Used for Let s name v
	* @param s
	* @param v
	*/
  public void setName( String s, Value v )
  {
    //System.out.println( "Got here3");
    //System.out.println ("Got here-with " + s + ":" +v);
    StickyNote note = search( s );
    //System.out.println ("Got here-with " + note );
    if ( note != null ){ // if "s" exists in EnvironmentVal, re-assign its value
    // System.out.println ("Mapping1 ");
      note.setVal(v);
    }
    else { // otherwise, create a copy and put "s" into the EnvironmentVal
      insert( s,  new StickyNote(v.copy() ));
    //System.out.println ("Mapping2 " + v);
    }
    
    //note = search( s );
    //System.out.println ("Mapped " + s + " to " + v);
  }

  /**
	* Used for Let s name v
	* @param s
	* @param v
	*/
  public void setIterator( String s, ListVal v )
  {
  /*
    StickyNote glub =  search( s );
    if ( glub != null ) { // if "s" exists in EnvironmentVal, re-assign its value
      System.out.println("setting " + s + " to " +  glub);
      System.out.println("setting " + s + " to " +  glub);
      System.out.println("setting " + s + " to " +  glub.getClass());
      System.out.println("assigning to " + s + " a value of " +  v.getClass());
      ListVal ttt = new ListVal();
      v.reset(); 
      System.out.println("Starting loop " );
      while (v.on()){
         System.out.println("At top of  loop " );
         ttt.insert(v.get());
         System.out.println("After the insert" );
         v.move();
         System.out.println("After the move" );
      }
      StickyNote xxx = Val.createVal();
         System.out.println("After the new" );
      xxx = ttt;
         System.out.println("After the assignment" );
            System.out.println("glub is"+glub );
            glub = new DoubleVal(33.0);
            System.out.println("glub is"+glub );
            System.out.println("s/ttt="+ttt );
      glub = (StickyNote)ttt;
      System.out.println("resetting " + s + " to " +  v);
    }
    else // otherwise, create a Val.createVal and put "s" into the EnvironmentVal
      */
      insert( s, Val.createVal( v ) ); // del'd so iterator will
                                          // modify the original list
                                          // established in evalpipe
      // insert( s,  v.parent() );
  }
  /**
	* Used for Let s1 name s2
	* @param s1
	* @param s2
	*/
  public void setName( String s1, String s2 )
  {
    StickyNote n1 = search( s1 );
    StickyNote n2 = search( s2 );
    if ( n2 != null ) // if the right-hand name is in the EnvironmentVal
    {
      if ( n1 != null ) // if the left-hand name is in the EnvironmentVal,
      n1 = n2; // make s1's value = s2's value

      else     // otherwise, 

        //stick s1 into the EnvironmentVal with a reference to s2's value
        insert( s1,  n2.copy() );
    }
    else // if the right-hand name is not in the EnvironmentVal,
    {    // we have problems
      System.err.println(
          "Trying to name non-existent name " + s2 + " with " + s1 +".");
    }
  }

  /**
	* Used for Let s1 alias sw
	* @param s1
	* @param s2
	*/
  public void alias( String s1, String s2 )
  {
    StickyNote note = search( s2 );
    if ( note != null ) // if "s2" exists in EnvironmentVal, make "s1" alias it
      insert( s1, note );
    else // otherwise, we have problems
    {
		 System.err.println(
          "Trying to alias non-existent name " + s2 + " with " + s1 +".");
    }
  }
  ///////////////////////////////////////////////////

  ///////////////////////////////////////////////////
  // I think these will help with lists

  //////////
  // These single parameter methods are for returning the appropriate 
  // "thing" to insert into a list.
  //
  // For example:
  // ListVal l = new ListVal();
  // l.insert( name( new DoubleVal( 13 ) ) );
  // l.insert( alias( "x" ) );
  // l.insert( name( new StringVal( "Wes" ) ) );
  //
  // will create the appropriate list for 
  // "let l name < 13 alias x "Wes" >

  /**
	* Wraps v as a StickyNote
	* @param v
	* @return
	*/
  public StickyNote name( StickyNote v )
  {
    return v.copy();
  }

  /**
	* Wraps s as a StickyNote
	* @param s
	* @return
	*/
  public StickyNote name( String s )
  {
    StickyNote sn = search(s);
    if (sn == null) return null;  // if search returns null, return it
    return sn.copy();
  }

  /**
	* Wrapes l as a StickyNote
	* @param l
	* @return
	*/
  public StickyNote name( ListVal l )
  {
    return Val.createVal( l.p.head );
  }

  /**
	*
	* @param s
	* @return The StickyNote associated with s
	*/
  public StickyNote alias( String s )
  {
    return search( s );
  }

  /**
	*
	* @param l
	* @return The StickyNote associated with l
	*/
  public StickyNote alias( ListVal l )
  {
    // I think this is an error.
    // Attempted to correct on Monday, June 7, 2004.
    // Wes Potts
    //
    //return l.p.head.info;

    // I think... in this case, since we're dealing with raw list nodes,
    // a name is the same as an alias.
    return name( l );
  }
  //////////

  ////////////////////
  // These methods with a ListVal for the first parameter allow
  // the user to change an individual element of the list, as in:
  // "let myList[3] name 15"
  //
  // The trick is to remember that when calling these methods, you
  // must pass the ENTIRE list as the first parameter.

  /**
	* Same Name(l.get(),v)
	* @param l
	* @param v
	*/
  public void name( ListVal l, StickyNote v )
  {
    name( l.get(), v );
  }

  public void name( ListVal l, String s )
  {
    name( l.get(), s );
  }

 

  public void name( ListVal l, ListVal r ) // left, right
  {
    name( l.get(), name( r ) );
  }

  public void alias( ListVal l, String s )
  {
    l.change( search( s ) );
  }

  public void alias( ListVal l, StickyNote n )
  {
    l.change( n );
  }

  public void alias( ListVal l, ListVal r )
  {
    l.change( alias( r ) );
  }

  // utility methods for the preceding

  public void name( StickyNote n, StickyNote m )
  {
    n = m.copy();
  }

  public void name( StickyNote note, String s )
  {
    note = search( s );
  }
  ////////////////////

  ///////////////////////////
  // These are for statements like:
  // "let x name l[3]"
 

  public void setName( String s, ListVal l )
  {
    //System.out.println( "Got here2");
    setName( s, l.p.head );
  }

  public void alias( String s, ListVal l )
  {
    alias( s, l.p.head.info );
  }


  public void nameList( String s, ListVal l )
  {
    StickyNote note = search( s );
    if ( note != null ) // if "s" exists in EnvironmentVal, re-assign its value
      note.setVal(l);
    else // otherwise, create a Val.createVal and put "s" into the EnvironmentVal
      insert( s, Val.createVal( l ) );
  }

/**
 * insert (s,n) into the environment
 * @param s
 * @param n
 */

  public void alias( String s, StickyNote n )
  {
    insert( s, n );
  }

	public Value copy() {
		return new EnvironmentVal(this);
	}

	// Rethink this later!
	@Override
	public boolean eq(Value rhs) {
	 return  ((rhs instanceof EnvironmentVal) && this == rhs);
	}
  ///////////////////////////
  ///////////////////////////////////////////////////
}

