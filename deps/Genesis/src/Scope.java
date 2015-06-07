// Scope.java
// This file contains the Scope class
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

import java.util.*;

class Scope
{
  protected Hashtable<String,StickyNote> ht;
  protected Scope lexical_parent;
  protected Scope dynamic_parent;

  public Scope()
  {
    ht = new Hashtable<String,StickyNote>();
    lexical_parent = null;
    dynamic_parent = null;
  }

  // lexical scoping by default
  public Scope( Scope s )
  {
    ht = new Hashtable<String,StickyNote>( s.ht );
    lexical_parent = s;
    dynamic_parent = null;
  }

  // lexical parent, then dynamic parent
  public Scope( Scope s, Scope t )
  {
    ht = new Hashtable<String,StickyNote>( s.ht );
    lexical_parent = s;
    dynamic_parent = t;
  }

  // This method is the "nuts and bolts" of the Scope class.  Its function
  // is to insert a (key, value) pair into the inernal hash table in
  // the Scope.  The key will be a string representing an identifier in
  // the source code.  The value will be a StickyNote representing the
  // right-hand-side of a "let s name v" statement.
  public StickyNote insert( String key, StickyNote value )
  {
    StickyNote n = null;
    try
    {
      n = (StickyNote)ht.put( key, value );
    }
    catch ( NullPointerException e )
    {
      Evaluator.printError( 
          "Error: passing a null pointer to Scope.insert( String key, StickyNote value )\n\t"
          + e.toString() );
    }
    return n;
  }

  // This method copies the info from another Scope into the current Scope.
  public void insert( Scope s )
  {
    try
    {
      ht.putAll( s.ht );
    }
    catch ( NullPointerException e )
    {
      Evaluator.printError( 
          "Error: passing a null pointer to Scope.insert( Scope s )\n\t"
          + e.toString() );
    }
  }

  // This method deletes a (key, value) pair from the Scope.
  public StickyNote del( String key )
  {
    StickyNote n = null;
    try
    {
      n = (StickyNote)ht.remove( key );
    }
    catch ( NullPointerException e )
    {
      Evaluator.printError( 
          "Error: passing a null string to Scope.del( String key )\n\t" 
          + e.toString() );
    }
    return n;
  }

  // find -- return the StickyNote associated with key; null if not found
  public StickyNote find( String key )
  {
    StickyNote n = (StickyNote)ht.get( key );
      return n;
  }

  //  search -- Returns the value for "key" or "null" if "key" is not in Scope.
  //      This implements transparency, i.e., if the key denotes an iterator
  //      for a list, then search returns the value that iterator references 
  public StickyNote search( String key )
  {
     // System.out.println ("Searching for " + key);
    StickyNote  n = (StickyNote)ht.get( key );
           //if (n == null) System.out.println("awgg!!!");
          //System.out.println("n =" + n);
          // System.out.println("n.getClass() =" + n.getClass());
          //System.out.println("n.val instanceof GenesisList n ="+ n +
           //                (n != null && n.val instanceof GenesisList)) ;
   //System.out.println ("Search completed with " + key + " mapped to " +n );
         if (n != null)
            if (n.val instanceof GenesisList) 
               n =  ((GenesisList)n.val).get();
     //System.out.println("Returning from search with: " + n);
    // Note that if n is null, it simply gets returned
    return n;
  }

  public StickyNote searchRef( String key )
  {
    StickyNote  n = (StickyNote)ht.get( key );
    return n;
  }

  /////////////////////////////////////////////////
  // This method removes all variables except functions and procedures
  // from the scope.
  void deleteAllVars()
  {
    Enumeration e = ht.keys();
    String s;
    while ( e.hasMoreElements() )
    {
      s = (String)e.nextElement();
      if ( s.charAt( 0 ) != '*' ) del( s );
    }
  }

  /////////////////////////////////////////////////
  // methods for printing the contents of the Scope
  void printAll( GenesisIO io )
  {
    Enumeration e = ht.keys();
    String s;
    while ( e.hasMoreElements() )
    {
      s = (String)e.nextElement();
      io.println( s + " = " + search( s ) );
    }
  }
  ArrayList<String> printAll( )
  {
    Enumeration e = ht.keys();
    String s = "";
    ArrayList<String> ans = new ArrayList<String>();;
    String name = "";
    while ( e.hasMoreElements() )
    {
      name = (String)e.nextElement();
      ans.add(name);
      //io.println( s + " = " + search( s ) );
      
    }
    return ans;
  }
  /////////////////////////////////////////////////

  // This is a utility method for wrapping a raw list value or string as a
  // GenesisList.  The parameter "s" is an identifier such as
  // "myList" where a statement like "let myList name < 1 2 3 >"
  // appears in the source code.  This method fetches the raw list
  // data for < 1 2 3 >, wraps it as a GenesisList, and returns the
  // new GenesisList.

  GenesisList wrapList( String s )
  {
    StickyNote sn = search( s );
    //System.out.println ("Wrapping " + sn);
    if (sn != null && sn.val instanceof StringVal) {
    //System.out.println ("Wrapping " + sn);
       sn  = new StickyNote(Node.str2Node(sn.toString()));
        // new GenesisList(Node.str2Node(sn.toString()));
    }
    if ( sn == null || ! (sn.val instanceof Node) && sn.val != null) {   // ljm: 7/31/04 & 8/28/04 &9/8/09
       Evaluator.printError(s + " is not a list and therefore cannot be subscripted"); 
    }
    GenesisList l;
      // ljm: 7/31/04
    l = new GenesisList( sn );  // ljm: 8/13/04 -- invoke using sn not gv
    l.reset();
    return l;
  }

  ///////////////////////////////////////////////////
  // methods for "naming" and "aliasing"

  // to give a value a name as in "let x name 3"
  // or "let x name max of 4 and 6"

  public void setName( String s, GenesisVal v )
  {
    //System.out.println( "Got here3");
    //System.out.println ("Got here-with " + s + ":" +v);
    StickyNote note = search( s );
    //System.out.println ("Got here-with " + note );
    if ( note != null ){ // if "s" exists in Scope, re-assign its value
    // System.out.println ("Mapping1 ");
      note.val = v;
    }
    else { // otherwise, create a new StickyNote and put "s" into the Scope
      insert( s, new StickyNote( v ) );
    //System.out.println ("Mapping2 " + v);
    }
    
    //note = search( s );
    //System.out.println ("Mapped " + s + " to " + v);
  }

  public void setIterator( String s, GenesisList v )
  {
  /*
    StickyNote glub =  search( s );
    if ( glub != null ) { // if "s" exists in Scope, re-assign its value
      System.out.println("setting " + s + " to " +  glub);
      System.out.println("setting " + s + " to " +  glub.val);
      System.out.println("setting " + s + " to " +  glub.getClass());
      System.out.println("assigning to " + s + " a value of " +  v.getClass());
      GenesisList ttt = new GenesisList();
      v.reset(); 
      System.out.println("Starting loop " );
      while (v.on()){
         System.out.println("At top of  loop " );
         ttt.insert(v.get());
         System.out.println("After the insert" );
         v.move();
         System.out.println("After the move" );
      }
      StickyNote xxx = new StickyNote();
         System.out.println("After the new" );
      xxx.val = ttt;
         System.out.println("After the assignment" );
            System.out.println("glub is"+glub );
            glub.val = new DoubleVal(33.0);
            System.out.println("glub.val is"+glub.val );
            System.out.println("s/ttt="+ttt );
      glub.val = (GenesisVal)ttt;
      System.out.println("resetting " + s + " to " +  v);
    }
    else // otherwise, create a new StickyNote and put "s" into the Scope
      */
      insert( s, new StickyNote( v ) ); // del'd so iterator will
                                          // modify the original list
                                          // established in evalpipe
      // insert( s,  v.parent() );
  }
  public void setName( String s1, String s2 )
  {
    StickyNote n1 = search( s1 );
    StickyNote n2 = search( s2 );
    if ( n2 != null ) // if the right-hand name is in the Scope
    {
      if ( n1 != null ) // if the left-hand name is in the Scope,
      n1.val = n2.val; // make s1's value = s2's value

      else     // otherwise, 

        //stick s1 into the Scope with a reference to s2's value
        insert( s1, new StickyNote( n2 ) );
    }
    else // if the right-hand name is not in the Scope,
    {    // we have problems
      Evaluator.printError( 
          "Trying to name non-existent name " + s2 + " with " + s1 +".");
    }
  }
  public void alias( String s1, String s2 )
  {
    StickyNote note = search( s2 );
    if ( note != null ) // if "s2" exists in Scope, make "s1" alias it
      insert( s1, note );
    else // otherwise, we have problems
    {
      Evaluator.printError( 
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
  // GenesisList l = new GenesisList();
  // l.insert( name( new DoubleVal( 13 ) ) );
  // l.insert( alias( "x" ) );
  // l.insert( name( new StringVal( "Wes" ) ) );
  //
  // will create the appropriate list for 
  // "let l name < 13 alias x "Wes" >
  public StickyNote name( GenesisVal v )
  {
    return new StickyNote( v );
  }

  public StickyNote name( String s )
  {
    StickyNote sn = search(s);
    if (sn == null) return null;  // if search returns null, return it
    return new StickyNote( search( s ) );
  }

  public StickyNote name( GenesisList l )
  {
    return new StickyNote( l.p.head );
  }

  public StickyNote alias( String s )
  {
    return search( s );
  }

  public StickyNote alias( GenesisList l )
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
  // These methods with a GenesisList for the first parameter allow
  // the user to change an individual element of the list, as in:
  // "let myList[3] name 15"
  //
  // The trick is to remember that when calling these methods, you
  // must pass the ENTIRE list as the first parameter.
  public void name( GenesisList l, GenesisVal v )
  {
    name( l.get(), v );
  }

  public void name( GenesisList l, String s )
  {
    name( l.get(), s );
  }

  public void name( GenesisList l, StickyNote n )
  {
    name( l.get(), n.val );
  }

  public void name( GenesisList l, GenesisList r ) // left, right
  {
    name( l.get(), name( r ) );
  }

  public void alias( GenesisList l, String s )
  {
    l.change( search( s ) );
  }

  public void alias( GenesisList l, StickyNote n )
  {
    l.change( n );
  }

  public void alias( GenesisList l, GenesisList r )
  {
    l.change( alias( r ) );
  }

  // utility methods for the preceding
  public void name( StickyNote note, GenesisVal v )
  {
    note.val = v;
  }

  public void name( StickyNote n, StickyNote m )
  {
    n.val = m.val;
  }

  public void name( StickyNote note, String s )
  {
    note.val = search( s ).val;
  }
  ////////////////////

  ///////////////////////////
  // These are for statements like:
  // "let x name l[3]"
  public void setName( String s, StickyNote note )
  {
    //System.out.println( "Got here1");
    setName( s, note.val );
  }

  public void setName( String s, GenesisList l )
  {
    //System.out.println( "Got here2");
    setName( s, l.p.head );
  }

  public void alias( String s, GenesisList l )
  {
    alias( s, l.p.head.info );
  }


  public void nameList( String s, GenesisList l )
  {
    StickyNote note = search( s );
    if ( note != null ) // if "s" exists in Scope, re-assign its value
      note.val = l;
    else // otherwise, create a new StickyNote and put "s" into the Scope
      insert( s, new StickyNote( l ) );
  }



  public void alias( String s, StickyNote n )
  {
    insert( s, n );
  }
  ///////////////////////////
  ///////////////////////////////////////////////////
}

