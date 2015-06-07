package runtime;



/*
   Based on List.java by Dr. Morell
   Edited by Wes Potts

   Modification ( please forgive the gaps )
   6/15/04  -- Wes Potts: changed toString() a bit
                  1. remove the '*'
                  2. added a member: boolean myQuoteStrings
                  3. used myQuoteStrings to determine whether or not to quote
                       strings in a list 
                  4. added a member to change quoting of strings: 
                     void quoteStrings(boolean)        
                  5. added members for the opening and closing of quoted strings:
                        String quotedStringOpen;
                        String quotedStringClose;
   7/6/04 -- ljm: Modified ListVal operations to ignore any MetaNodes
             This enables the construction of lists which contain meta-
             information, i.e., information about the lists themselves.
             This will enable the future addition of type information

   11/19/05  ljm: MetaNodes now should only appear as the first
                  node of a list.  In the case of a sublist, an
                  additional MN is created, forking the new list
                  into the old.  If a list is created from the
                  MN of another list, then the new list
                  will share the MN.
   12/1/05-- ljm: Replaced string conversion requests in Utility.leaving to prevent
                  trying to print an infinite list. Error out when there is an infinite list
   5/26/06 -- ljm: Fixed bug in search when some values had no fieldnames
   6/6/06  -- ljm: Amended List:toString() to print null values as "null"
   7/26/06 -- ljm: Fixed bug in code for searching for a string subscript
   3/10/07 -- ljm: Removed debug code that caused problems for atfirst
                   Modified atFirst to use pos() == 1
   1/18/09 -- ljm: Fixed problem with comparing lists
   2/09/10 -- ljm: Fixed additional problem with comparing lists built by adding
   2/19/11 -- ljm: Adapted this new package into the Eve runtime environment (no Stickynotes)
*/  

class ListDescriptor {
  public
     //StickyNote parent; // Parent node that references this ListVal
     Node  
       head,  //  points to first node on the linked list
       point ; // points to the node just before the "current"

	public ListDescriptor(ListDescriptor ld) {
		head = ld.head;
		point = ld.point;
	}

	ListDescriptor() {
		head = null;
		point = null;
	}
               //  == null

}

public class ListVal extends Val implements List {

// ---------------- Classwide constants ---------------

   public final static String quotedStringOpen = "\"";
   public final static String quotedStringClose = "\"";
   final static boolean DEBUG = false;
   final static boolean TEST = false;
   public static int MAXPRINT = 10000; // error out if ever try toString a list of size > 10000

// ---------------- Classwide variables ---------------

   public static boolean myQuoteStrings = true;
   public static String currentIndicator = "*";
   

// ---------------- Instance variables ----------------
   ListDescriptor p;

// ------------------ Constructors --------------------
/**
 * Creates an empty list
 */
public ListVal() {
   if (Utility.DEBUG) Utility.entering("GenesisList");
   init();
   p.head = null;
   p.point = p.head;
   if (Utility.DEBUG) Utility.leaving("GenesisList with" , p.head);
}


/**
 * Creates a list from gl by copying the Values (not the values)
 * @param gl
 */
public ListVal( ListVal gl )
{
   if (Utility.DEBUG) Utility.entering("GenesisList( GenesisList v )");
   // System.out.println ("Forming new GL from " + gl);
   init();
   p.head = gl.p.head; 
   p.point = gl.p.point; 
   // p.parent = (p.head.copy());
   //skipMetaNodes(); 
   //System.out.println ("POS="+ pos());
   //System.out.println ("New list: " + toString());
   if (Utility.DEBUG) Utility.leaving("GenesisList( GenesisList v )");
}

// ------------------ Methods  --------------------
/**
 *  Re-establish the list as empty
 */
public final void init()
{
   if (Utility.DEBUG) Utility.entering("init");
   p =  new ListDescriptor();
   p.head = null;
   p.point = null;
  
   p.point = p.head;
 //   p.parent = new StickyNote(p.head);
   if (Utility.DEBUG) Utility.leaving("init");
}

/*
 * Destroy the list
 */
public void done()
{
   if (Utility.DEBUG) Utility.entering("done");
   p = null;
   if (Utility.DEBUG) Utility.leaving("done");
}

/*
 * Move the current point in the list to the front
 */
public void reset()
{
   if (Utility.DEBUG) Utility.entering("reset");
   p.point = null;
   //skipMetaNodes();
   if (Utility.DEBUG) Utility.leaving("reset");
}

/**
 * @return true iff the list contains no elements
 */
public boolean empty()
{
  return p.head == null;
}

/**
 *  Positioning the current element to its successor; then delete its predecessor
 */
public void del()
{
   if (Utility.DEBUG) Utility.entering("del");
   Node C ;
   StickyNote v;
   if ((p.head == null) || (p.point != null && (p.point.right() == null)))
      System.err.println("delList: No element at current location");
   else if (p.point == null) { //deleting first element
      p.head = p.head.right();
//      p.parent  = p.head;
      if (p.head != null)
         p.head.left = null;
   }
   else { //middle ||  of list
      p.point.right = p.point.right().right();  //C = double succ of point;
      if (p.point.right() != null)
         p.point.right().left = p.point;
   }
   //skipMetaNodes();
   if (Utility.DEBUG) Utility.leaving("del");
}

/**
 *
 * @return The value of the current element of the list
 */
public StickyNote get()
{  
   if (Utility.DEBUG) Utility.entering("get with"+p.point);
   StickyNote v = null ;// = new StickyNote();
   //if (TEST) verify (validRep(Self),Self, "valid Representation");
   //skipMetaNodes();

   if (p.point == null)
      if (p.head == null) {
        // System.out.println("ListVal.get: Attempt to get non-existent element");
        System.err.println("Algorithm is trying to access information from an empty list");
      }
      else // at head of the list
         v = p.head.info;
   else
      if ((p.point.right() == null)) { // after end of the list
         //System.out.println("ListVal.get: Attempt to get non-existent element");
         System.err.println("Algorithm is trying to access an iterator that is past the end of the list");
      }
      else { // on the list 
         v = p.point.right().info;
         if (Utility.DEBUG) System.out.println("On List");
      }
   //if (Utility.DEBUG) Utility.leaving("get with" , v);
   if (Utility.DEBUG) Utility.leaving("get with " + v.addr());
   return v;
}
public void insert(Value v) {
	 insert(new StickyNote(v));
}
/**
 *
 * @param e Insert e after the current point of the list; e becomes the
 * current element of the list
 *
 */
public void insert(StickyNote e)
{
   if (Utility.DEBUG) Utility.entering("insert");
   //Utility.println("Entering insert with:");
   //printNodes();
   //Utility.println("-->Insert Got here" + toString());
   //printDesc();
   
   if ( p == null) {// System.out.println("Calling init"); 
       init(); 
   }
   
   Node n = new Node();
   n.info = e;
   //printNode ("p.head", p.head);

   if (p.point == null){ // adding at the beginning 
              //Utility.println("Adding at the beginning");
       // printDesc();
       //printNode("Adding to",p.head);  
       // Utility.println("parent: " +p.parent);
       n.right =p.head;
       if (p.head != null)
          p.head.left = n;
       p.head = n;
//       p.parent  = p.head;
       p.point = p.head;
       n.left = null;
       //printNode("n",n);  
   }
   else { // add after the beginning 
      n.right = p.point.right();
      if (p.point.right() != null)
         p.point.right().left = n ;
      p.point.right = n;
      n.left = p.point;
      p.point = n;  // move p.pointer to new  element 
   }
  /* Node t = p.head;
   while (t != null) {
     printNode ("Node:", t);
     t = t.right();
   }
  */ 
//   Utility.println("Leaving insert with:");
 //  printNodes();
   if (Utility.DEBUG) Utility.leaving("insert with" ,this);
}

/*
// Insert a label and a StickyNote
void insert (StickyNote sn, String s) {
   insert (sn);
   p.point.setLabel(new StickyNote(s));
   //System.out.println("Label set for :" +this);
}

void insert (StickyNote sn, StickyNote s) {
   insert (sn);
   p.point.setLabel(s);
   //System.out.println("Label set for :" +this);
}
*/
// ljm: 7/6/04
/**
 *
 * @param n insert n after the current element of the list; n becomes the
 * new current element
 */
public void insert(Node n)
{
   if (Utility.DEBUG) Utility.entering("insert");
//   printDesc();
   //if (n instanceof MetaNode) 
      //Utility.println("inserting a MetaNode");
   if ( p == null) {
     //Utility.println("Calling init"); 
     init();
   }
   if (p.point == null){ // adding at the beginning 
//              Utility.println("Adding at the beginning");
//       printDesc();
       n.right =p.head;
       if (p.head != null)
          p.head.left = n;
       p.head = n;
//       p.parent  = p.head;
       p.point = p.head;
       n.left = null;
//       printNode("n",n); 
   }
   else { // add after the beginning 
//      Utility.println("Adding after the beginning");
      n.right = p.point.right();
      if (p.point.right() != null)
         p.point.right().left = n ;
      p.point.right = n;
      n.left = p.point;
      p.point = n;  // move p.pointer to new  element 
   }
   
//   printNode("n",n);
//   printNode("p.head",p.head);
//   printNode("p.point",p.point);
//   Utility.println("-------------------------");

   if (Utility.DEBUG) Utility.leaving("insert");
}

/**
 * Debug routine for printing the internal descriptor
 */
public void printDesc() // print a single list node
{  
   
    Utility.println("p:       "+p);
    Utility.println("p.head:  "+p.head);
    Utility.println("p.point: "+p.point);
}

/**
 * Debug routine for printing the contents of an internal Node in the list
 * @param s
 * @param LN
 */
public void printNode(String s,Node LN) // print a single list node
{  
   //if (Utility.DEBUG) Utility.entering("ListVal::printNode");
   Utility.println(s+": "+LN);
   Utility.print(s+"->info: ");
   if (LN != null) {
      Utility.print(LN.info);
      Utility.print(s+"->left: ");
      Utility.println(LN.left());
      Utility.print(s+"->right: ");
      Utility.println(LN.right);
      Utility.println("--------------------");
   }
   else {
      Utility.println("Node is null");
   }
   //if (Utility.DEBUG) Utility.leaving("ListVal::printNode");
}

/**
 * Debug routine for printing all the Nodes of a list starting at SN
 * @param SN
 */
public void printNodes(Node SN)  // print a list of listnodes
// SN is the node from the source list

{ StickyNote v;
   if (Utility.DEBUG) Utility.entering("GenesisList::printNodes");
   // printDesc(); 
   while (SN != null) {
      //if (! (SN instanceof MetaNode))
         printNode("",SN);
      SN = SN.right();
   }
   if (Utility.DEBUG) Utility.leaving("GenesisList::printNodes");

} // printNodes

/**
 *  Debug routine for printing all the nodes of a list
 */
public void printNodes()  // print a list of listnodes
{ StickyNote v;
   if (Utility.DEBUG) Utility.entering("GenesisList::printNodes");
   // printDesc(); 
   Node SN = p.head;
   while (SN != null) {
      //if (! (SN instanceof MetaNode))
         printNode("",SN);
      SN = SN.right();
   }
   if (Utility.DEBUG) Utility.leaving("GenesisList::printNodes");

} // printNodes

/**
 * Move the current point to the next node if one exists; no movement and
 * no error if one does not exist
 */
public void move()
{
   //Utility.DEBUG = true;
   //if (Utility.DEBUG) Utility.entering("GenesisList::move");
   //skipMetaNodes();
	if (p.point == null) {
			  p.point = p.head;
//			  System.out.println ("Null, Moving to " + p.point);
	}
	else if(p.point.right() != null) {
         p.point = p.point.right();
//   	  System.out.println ("Not null, Moving to " + p.point);

      }
//  printNodes(p.head);
//	System.out.println("Leaving with " + p.point);
  if (Utility.DEBUG) Utility.leaving("GenesisList::move");
}

////////////////////////////////////////////////////
// added by Wes Potts 4/8/04
/**
 * Move current position n nodes to the right if possible; if not possible
 * then move current point past the end of the list
 * @param n
 */
public void move( int n )
{
        if (Utility.DEBUG) Utility.entering("GenesisList:move("+n+")");
        if ( n < 0 || n > size() )
        {
                System.err.println("Error: either passing a negative or " +
                "passing n larger than size() to move(int n)" +
								" \nn= " + n +
								" size()=" + size()
								);
        }
        if ( p.point == null && n > 0 )
        {
                p.point = p.head;
                // skipMetaNodes();
                n--;
        }
        while ( n > 0 && p.point.right() != null )
        {
                p.point = p.point.right();
                // skipMetaNodes();
                n--;
        }
        if (Utility.DEBUG) Utility.leaving("move(int n)");
}

// added by Wes Potts 4/8/04
/**
 * Move to the N-th node of a list; error if Node N does not exist
 * @param n
 */
public void moveTo( int n )
{
        if (Utility.DEBUG) Utility.entering("goToItem("+ n+")");
        n--;
        if ( n < 0 || n > size() )
        {
                System.err.println("Attempt to move beyond the boundaries of a list");
        }
        reset();
        move( n );
        if (Utility.DEBUG) Utility.leaving("goToItem( int n )");
}

// added by Wes Potts 4/8/04
/**
 *  getSubList -- create sublist from list contained in the current node
 */

////////////////////////////////////////////////////
/**
 *
 * @return True iff the current point is off the list
 */
public boolean off() { // Loo
    if (Utility.DEBUG) Utility.entering("GenesisList::off");
    boolean ans =(p.head == null)||
				((p.point != null ) && (p.point.right() == null));
    
    if (Utility.DEBUG) Utility.leaving("off with",ans);
    return ans;
   // ljm: Why isn't the following equivalent???
   // return (p.point == null) || (p.point.right() == null); // short circuit
}

/**
 *
 * @return True iff the current point is on the list
 */
public boolean on() {
    boolean on;
    if (Utility.DEBUG) Utility.entering("GenesisList::on");
    on =  (p.head != null) &&
             ((p.point == null) || p.point.right() != null);
   if (Utility.DEBUG) Utility.leaving("GenesisList::on with ", on);
   return on;
}

/**
 *
 * @return true iff the the current point is on the first node of the list
 */
public boolean atFirst() {
//      return  on() && p.point != null && p.point.right == p.head; // ljm: 3/11/07
     //return p.point == null;
     return pos() == 1;
}

/**
 *
 * @return True iff the current point is on the last node of the list
 */
public boolean atLast() {
    if (Utility.DEBUG) Utility.entering("GenesisList::atLast");
    boolean ans = false;

    Node t;
    if (p.point == null)
       t = p.head;
    else
       t = p.point.right(); 

    if (t == null) ans = false;
	 else if (t.right() == null ) ans = true;
    // printNodes();

    if (Utility.DEBUG) Utility.leaving("GenesisList::atLast with ", ans);
    return ans;
}

/**
 * Change the value of the current node in the list to e
 * @param e
 */
public void change(StickyNote e) {
     if ((p.head == null) || ((p.point != null) &&  (p.point.right() == null)))
        Utility.println ("List.change: no element at current location;"
                            +  " no change");
                        // edited by Wes Potts... think there's a bug here
                        else if ( p.point == null )
                        {
                                p.head.info = e;
                        }
      else {
         p.point.right.info = e; // ljm: 8/11/04
      }
}

// ljm:  Could have a problem here ... what if the "list" that is
//       wrapped is not constructed using ListVal ... i.e.
//       what if it is a "tree" in which the left pointer does not
//       point to the previous node of the list, rather to the 
//       leftmost immediate descendant
//  It is therefore crucially important that this operation never
//  be used on any list not constructed via the ListVal wrapper
/*
void prev()
{
   //Utility.DEBUG = true;
   if (Utility.DEBUG) Utility.entering("prev"+p.point+p.head);
   if (p.point != null && p.point != p.head)
      p.point =  p.point.left;
   //if (p.point == null) {p.point = p.head;}
   //skipLeftMetaNodes();
   if (Utility.DEBUG) Utility.leaving("prev"+(p.point==p.head));
}
*/

// left -- move p.point to the left
//    This peculiar implementation is to ensure that
//    p.point does not move past the first node of the
//    the list, if the list is embedded in another list.
//    In this case p.point should not go left, but should
//    be set to p.head
/**
 * Move current point to the left
 */
public void left()
{
   if (Utility.DEBUG) Utility.entering("left");
//   if (p.point != p.head)  // can move
//      if (p.head.right() == p.point) // to the head
//         p.point = p.head;
//      else                           // or to the left
//         p.point = p.point.left();
	if (p.point != null)
		p.point = p.point.left;
   if (Utility.DEBUG) Utility.leaving("left");
}

/**
 * Same as left()
 */
public void prev() {
    left();
}

/**
 *
 * @return The string "ListVal"
 */
public String typeName()
{  
   return "GenesisList";
}

/**
 *
 * @return The number of nodes in the list
 */
public int size()
{
   Node    n;
   int count;
   if (Utility.DEBUG) Utility.entering("size");
   count = 0;
   n = p.head;
   while (n != null){
   
         count = count + 1;
      n = n.right();
   }
   if (Utility.DEBUG) Utility.leaving("size");

   return count;
}

/**
 *
 * @return The ordinal position of the current point in the list
 */
public int pos()
{
   Node t;  int n ;
   if (Utility.DEBUG) Utility.entering("pos");
      // printDesc();
   if (p.point == null) {
		n = 1;
	}
	else  {
      n = 2; t= p.head;
      //Utility.println("Setting pos to 1");
      //Utility.println("p.head == p.point" + (p.head == p.point));
      //Utility.println("p.point == null" + (p.point == null));
      while (t != p.point){
         t = t.right();
         n = n + 1;

      }
	}
   //Utility.println("Exiting pos with " + n);
   if (Utility.DEBUG) Utility.leaving("pos");
   return n; 
}

/**
 *
 * @return A reference to the current Node of the list
 */
public Node current() {
   //skipMetaNodes();
   if (off()) {
      System.err.println("Attempt to access non-existent element");
   }
   if (p.point == null) return p.head;
   else {//Utility.println("Returing " +  p.point.right);
         // return p.point.right();
         //  Wow 8 years and never discovered that the follwing was correct
         //  instead of the above.  Causes lists not to compare correctly
         //  when current() was used to return the list when adding a single
         //  element to a list
         return p.point;
   }
                
}


/**
 *
 * @param e
 * @return  two Lists are equal if they have the same values in their nodes
 */
public boolean equals(ListVal e)
{
   //System.out.println ("Comparing");
   boolean equal; 

   equal = true;
   int size1 = size(); 
   int size2 = e.size();
   if (e.getClass() != getClass()) {
      equal = false; // System.out.println ("diff class");
   }
   else if (size1 != size2 ) {
      equal = false; // System.out.println ("diff size");
   }
   else if (size1 == 0)  {
      equal = true;  //  System.out.println ("both empty");
   }
   else {

      //System.out.println ("Calling equals with " + this + " and " + e );
      Node    m, n;
       
      m = p.head.right; // m=skipMetaNodes(m);
      n = e.p.head.right;//  n=skipMetaNodes(n);
      //System.out.println ("Comparing " + m.info.getClass()  + " with " +n.info.getClass() );
      while ((m != null) && (m.info.equals(n.info))) {
          m = m.right(); // m=skipMetaNodes(m);
          n = n.right(); // n=skipMetaNodes(n);
          //System.out.println ("Comparing " + m  + " with " +n);
      }
      equal =  m == null;
   }
   return equal;
}

/**
 *
 * @param gv
 * @return Contained list.equals(gv); false if gv is not a list
 */
public boolean eq(Value gv) {
   //System.out.println ("Comparing lists");
   if (! (gv instanceof ListVal) )
      return false;
   return equals((ListVal)(gv));
}

/**
 * Moves the current point in the list to the first node whose contained
 * value matches that in A
 * @param A
 * @return The first StickyNote in the list whose value = A
 */
public StickyNote search(StickyNote A)
{
  StickyNote B = null;
  reset();
  if (on()){
     B = get();
     while (! B.equals(A)){
        move();
        B = get();
     }
  }
  return B;
}
/*
String getLabel () {
   String ans = null;
   if (p.point == null) {
      ans =p.head.getLabel();
   }
   else {
      ans = p.point.next().getLabel();
   }
   return ans;
}
*/
/*
int  search(String s)  // Search for a label in the node
{
   int pos=0;
   reset();
   String l = null;
   if (on()){
      pos = 1;
      l = getLabel();
      //System.out.println ("Comparing " + l + " and " + s + " and " + on());
      while ( on() && (l == null || ! l.equals(s))){
         move();
         pos = pos + 1;
         if (on()) l = getLabel();
         //System.out.println ("Comparing " + l + " and " + s);
      }
  }
  if (l == null || ! l.equals(s)) pos = 0;
   //System.out.println("Returning pos = " + pos);
  return pos;
}
*/

private int recurseLimit = 100;

/**
 *
 * @return A String version of the list of up to the first 1000 elements of a list
 */
public String implode() {
  int maxprintLength = 10;
  String result="";
  StickyNote a;
  int position=pos();
  reset();
  // System.out.println ("Position is now" + position);
  while (on() && maxprintLength > 0){
    maxprintLength --;
    a = get();
    String v = a.toString();
    result += v;
    move();
  }
  if (on()) { result = result + "...rest of string truncated"; }
  moveTo(position);
  // System.out.println ("Position is now" + position);
  return result;
}
/**
 *
 * @return A string version of the list
 */
	@Override
public String toString() {
  ListDescriptor save = new ListDescriptor(p);
  if (Utility.DEBUG) Utility.entering("GenesisList::toString");
  // printNodes(p.head);
  recurseLimit --;

  if (recurseLimit == 0) {
     System.err.println("Too many strings printed ... perhaps you have data loop\n"
    + "in a list?");

  }
  final String Openchar = "<";
  final String  Closechar = ">";
  String result = Openchar;
// printNodes();
  int position=pos();
//  System.out.println("position =" + position);
  // Utility.println("Computed position is " + position);
  int i;
  StickyNote a;
  reset();
  
  i = 01;
  /*
  Utility.println( "[@" 
                       + this.addr() 
                                   + "]\n") ; 
  Node t = p.head; int count = 0;
  while (t != null) {
     if (t instanceof MetaNode) {
        Utility.println ("M("+ t + ":" +t.info + ")");
     }
     else 
        Utility.println ("N("+ t + ":" +t.info +")");
         
     t = t.right(); count++;}
  Utility.println ("toStringing a list with " + count + " nodes");
  */
  int maxprintLength = 1000;
  while (on() && maxprintLength > 0){
     maxprintLength --;
    a = get();
  
    String current = "";
    if (i==position) current = currentIndicator; //currentIndicator ;
    if (true)  {
     //Utility.print("Return from get() with:");
     //Utility.println(a .toString()); // System.exit(1);
    // Utility.println ("Appending '" + a + "'");
       if (a != null) { 
          //System.out.println("Stringing:"+a.addr());
          String v = a.toString();
          if ( a.getVal() instanceof StringVal && myQuoteStrings) {
            result= result + current + quotedStringOpen + v + quotedStringClose;
          }
          else {  result= result + current +v;    }
       }
       else {  result = result + null;   }
    }
    else
       result = result 
             + "[@" 
                       + a.addr() 
                                   + "(" + a.addr() + ")"
                                   + "]\n"
             ; 
   
    // Utility.println("toString:atLast" + atLast());
    if (! atLast())
       result = result + " ";
    //Utility.println("Moving:"+maxprintLength);
    i = i + 1;
    move();
  }
  if (maxprintLength <= 0) {
        System.err.println("Too many elements in the list to convert to printable form (more than " + MAXPRINT + " elements)\n");
        }
 
  if (i==position ) result += currentIndicator;
  result = result + Closechar;
   reset();
  while (position > 1) { move(); position=position-1;}
  if (Utility.DEBUG) Utility.leaving("GenesisList::toString");
//  System.out.println("Moving to" +position);
//  p = save;
//  moveTo(position);
  return result;
}


/**
 * Move the current point past to the next non-MetaNode
 */

/**
/**
 * Sets whether or not to print contained strings with quotes
 * @param q
 */
public static void quoteStrings( boolean q ) { myQuoteStrings = q; }

/**
 * Invokes Utility.print on the list
 */
public void display () { Utility.print(this);}
/**
 * Invokes Utility.println on the list
 */
public void displayln () { Utility.println(this);}

/**
 *
 * @return The list that contains copies of each of the values of the original
 */
public ListVal copy () {
   if (Utility.DEBUG) Utility.entering ("GenesisList::copy");
   int position = pos();
   ListVal gl = new ListVal();
   Node t = p.head;
   // printNodes();
   Node head = null;
   Node prev = null;
   while ( t != null) {
      Node n;
      {
         n = new Node(); 
      }
      if (head == null) head = n;
      n.info = (t.info.copy());
      n.left = prev; 
      if (prev != null) 
         prev.right = n;
      prev = n;
      n.right = null;
      t = t.right();
   }
         //Utility.println("Exiting copy:");
   gl.p.head = head;
   gl.p.point = head; 
   reset();
          // Utility.println("Created:");
   // Utility.println(gl);
   /*while (on()) {
      StickyNote sn = get();
   Utility.println ("+++++++++ Inserting " + sn + " into " + gl);
      gl.insert(sn);
      move(); 
   }
   */
   reset(); gl.reset();
   while (position > 1) {move(); gl.move(); position--;}
   if (Utility.DEBUG) Utility.leaving ("GenesisList::copy");
   return gl;   
}

public boolean lt( StickyNote rhs ) {return false;}
public boolean le( StickyNote rhs ) {return false;}
public boolean gt( StickyNote rhs ) {return false;}
public boolean ge( StickyNote rhs ) {return false;}
public boolean ne( StickyNote rhs ) {return false;}

/**
 *  Copies the list to rhs
 * @param rhs
 * @return
 */
public ListVal add (Value rhs) {
   // Copy nodes in the list

   ListVal gl = copy();

   // Move to the end of the list

   while ( gl.on() ) {
      gl.move();
   }

   if (rhs instanceof ListVal) {
      ListVal rhsList = ((ListVal) rhs).copy();
      rhsList.reset();
      while ( rhsList.on()) {
         StickyNote sn = rhsList.get();
         gl.insert(sn);
         rhsList.move();
      }
   }
   else 
      gl.insert (new StickyNote(rhs.copy()));
 
   return gl;
}
// ------------ Test procedures -----------
/**
 * Test lists
 */
public void ListsTest()
{
   Utility.println("Testing %s for satisfaction of LIST rules\n"
                      + typeName());
   Utility.print("List value:"); 
   Utility.println ("End of LIST tests for "+typeName()+'\n');
}

/**
 * Conduct a self test on list
 */
public static void listsSelfTest()
{
   ListVal    l = new ListVal();
   StickyNote  IO = null;
   l.init(); IO = new StickyNote(5); l.insert(IO);
   l.ListsTest();
   IO = new StickyNote(20); l.insert(IO);
   l.insert(IO);
   l.ListsTest();
}

/**
 *
 * @return true iff the list is > 8000 elements
 */
public boolean noLoops() {
  Node ptr;
  int i ;
  ptr = p.head;
  i = 0;
  while (ptr != null){
     ptr = ptr.right();
     i = i + 1;
     if (i > 8000){
       return false;
     }
  }
  return true;
}

/**
 * @return Debugging true iff  the representation of the list appears valid
 */
               // satisfies obvious properties
public boolean valid() {
  boolean    VR;
  VR = true;
//  with (A.P)^)
  if (p.head == null){
     VR = (p.point == null);
     if (! VR) Utility.println("Violate 1");
  }
  else if (p.point == null){
     VR =  noLoops();
     if (! VR)Utility.println("Violate 2");
  }
  else {
     VR =  noLoops();
     if (! VR) Utility.println("Violate 3");
  }
  return VR;
}

/**
 * Main program to try out some list routines
 * @param a
 */
public static void main (String a[]){
   ListVal l = new ListVal();
   StickyNote ie = new StickyNote(33);
   ListVal xl = new ListVal();
   StickyNote ie1 = new StickyNote(11);
   StickyNote ie2 = new StickyNote(22);

   StickyNote ie3 = new StickyNote(33);
   StickyNote ie4 = new StickyNote(44);
   StickyNote ie5 = new StickyNote(55);
//   xl.insert(ie1); System.out.println("pos=" + xl.pos());
////   xl.displayln();
//
//   //xl.reset();
// //  System.out.println("-----------------");
////   xl.displayln();
//  // System.out.println("-----------------");
//   //System.out.println("atLast: " + xl.atLast());
//
//   xl.insert(ie2);System.out.println("pos=" + xl.pos());
////	xl.printNodes(xl.p.head);
//	xl.displayln();
//
//   xl.insert(ie3); System.out.println("pos=" + xl.pos());
//	xl.printNodes();
//xl.displayln();
//   xl.insert(ie4); System.out.println("pos=" + xl.pos());
//   xl.insert(ie5); System.out.println("pos=" + xl.pos());
//   xl.reset();System.out.println("pos=" + xl.pos());
//	xl.printNodes(xl.p.head);
//	xl.moveTo(3);
//   xl.displayln();
////		System.exit(0);
//   System.out.println("");
//   System.out.println(xl.size());
//   while (xl.on()) {
//      System.out.println("on is true");
//      xl.move();
//      System.out.println("");
//      xl.displayln();
//   }
//   xl.reset();
//
//   System.out.println("-> Processing all but the last" ); // Wes
//   while (!xl.atLast()) {
//      System.out.println("atLast is false");
//      xl.displayln();
//      xl.move();
//   }
//   System.out.println("atLast is " + xl.atLast());
//   xl.displayln();
  System.out.println (ie);
   for ( int i = 1; i <= 4; i++ ) {
     ie = new StickyNote(i);
     l.insert(ie);
	  System.out.println("AFter inserting, pos = " +l.pos());
     l.displayln();
   }

  l.left();
  System.out.println("AFter going left pos="+l.pos());

   l.displayln();
  System.out.println("Resetting:");
  l.reset();
  System.out.println("Aftere reset pos="+l.pos());
  l.displayln();
  l.moveTo(3);
  System.out.println("Before going left pos="+l.pos());
  l.displayln();
   l.left();
 
  System.out.println("After mooving to the lft pos =" + l.pos() );
  l.displayln();
  l.displayln();
	System.out.println("AFter going left pos="+l.pos());

   l.displayln();
	System.out.println("After pos="+l.pos());
//	System.exit(0);
   System.out.println("Displaying backward through the list");
   StickyNote e;
   while (! l.atFirst() ) {
     e = l.get();  
     System.out.println(e);
     l.displayln();
     l.left();
     System.out.println("pos = " +l.pos());
   }
   //System.exit(1);
   l.left();
   l.displayln();
   System.out.println("Starting forward through the list");
   System.out.println("-> Deleting all but the last" ); // Wes
   while ( ! l.atLast() ) { 
     //ie.displayln(); 
     e = l.get();
     System.out.println(e);
     l.del(); l.displayln(); //System.out.println("atLast: " + l.atLast());
   }
   // Now check the modification of iterators
   StickyNote sn = new StickyNote(33.7);
   StickyNote snval = new StickyNote("A value");
   ListVal iter = new ListVal();
   Utility.println ("Got here!"+iter);
   iter.insert(snval);
   snval = new StickyNote("Another value");
   iter.insert(snval);
   iter.displayln();
   System.out.println(sn);
   iter.reset(); iter.del();
   iter.displayln();
   System.out.println(sn);
   iter.del();
   iter.displayln();
   System.out.println(sn);
   DoubleVal x = new DoubleVal(99);
   System.out.println("Address of x: " + x.addr());
   sn = new StickyNote(x);
   System.out.println("Address of sn: " + sn.addr());
   System.out.println("Address of sn : "+ sn .addr());
   ListVal gl = new ListVal();
   gl.insert(sn); gl.reset();
   StickyNote result = gl.get();
   System.out.println("Address of result: " + result.addr());
   System.out.println("Address of result : "+ result .addr());
   System.out.print("gl = ");
   gl.displayln();
   gl.move(); gl.move();
   gl.insert(snval);
   ListVal y = new ListVal(gl);
   gl.insert(snval);
   gl.insert(snval);
   System.out.print("gl = ");
   gl.displayln();
   y.displayln();
   
   ListVal newlist = gl.copy();
	System.out.println("newlist");
   newlist.displayln();
   ListVal nl =  (ListVal) newlist.add(gl);
	newlist.insert(gl);
	newlist.insert(nl);
	newlist.displayln();
   nl.displayln();
}

	private void insert(ListVal gl) {
		throw new UnsupportedOperationException("Not yet implemented");
	}
}
// end of ListVal stuff
/////////////////////////////////////////////////////////////////


