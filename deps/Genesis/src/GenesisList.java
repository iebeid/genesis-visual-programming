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
   7/6/04 -- ljm: Modified GenesisList operations to ignore any MetaNodes
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
*/  

class GenesisListDesc {
  public
     StickyNote parent; // Parent node that references this GenesisList
     Node  
       head,  //  points to first node on the linked list
       point ; // points to the node just before the "current"
               //  == null 
}

class GenesisList extends GV {

// ---------------- Classwide constants ---------------

   public final static String quotedStringOpen = "\"";
   public final static String quotedStringClose = "\"";
   final static boolean DEBUG = false;
   final static boolean TEST = false;
   public static int MAXPRINT = 10000; // error out if ever try toString a list of size > 10000

// ---------------- Classwide variables ---------------

   public static boolean myQuoteStrings = true;
   public static String currentIndicator = "";
   

// ---------------- Instance variables ----------------
   GenesisListDesc p;

// ------------------ Constructors --------------------

GenesisList() {
   if (Utility.DEBUG) Utility.entering("GenesisList");
   init();
   // p.head = new MetaNode();
   // p.point = p.head;
   if (Utility.DEBUG) Utility.leaving("GenesisList with" , p.head);
}

// Create a list from a Stickynote that references a Node
GenesisList( StickyNote sn )
{
   Utility.DEBUG = false;
   if (Utility.DEBUG) Utility.entering("GenesisList(StickyNote)");
   init(); // create an empty list
   //p.head = new MetaNode();
   //p.point = p.head;
 //   if (v != null)  // kludge to ensure that the list 
                   // always begins at the first node of the list
 //     while (v.left != null) v = v.left;
   p.parent = sn;
   //System.out.println ("Type is " + sn.val.getClass());
   Node v = (Node) sn.val;
   if ( v instanceof MetaNode ) p.head = v; // reuse it
   else  {
     p.head = new MetaNode();
     //while (v instanceof MetaNode) v = v.right();
     p.head.right = v;
   }
   //while (v instanceof MetaNode) v = v.right();
   //p.head.right = v;  // adjust head
   p.point = p.head;
   p.parent.val = p.head;  // adjust parent
   //skipMetaNodes(); 
   if (Utility.DEBUG) Utility.leaving("GenesisList( Node v )");
}

// Create a list from a Node: reuse the first MN if v is a MN
GenesisList( Node v )
{
   if (Utility.DEBUG) Utility.entering("GenesisList( Node v )");
   init();
   if (v == null) {  
        p.head = new MetaNode();
   }
   else  {  // kludge to ensure that the list 
                   // always begins at the first node of the list
   
      if ( v instanceof MetaNode ) p.head = v; // reuse it
      else  {
        p.head = new MetaNode();
        // while (v instanceof MetaNode) v = v.right();
        p.head.right = v;
      }
   }
   p.point = p.head;
   p.parent = new StickyNote(p.head); // ljm: 8/9/04 compatibility
   //Utility.println("List created from Node:");
   //skipMetaNodes(); 
  //  printNodes();
   //Utility.println("Exiting with:" +v);
   if (Utility.DEBUG) Utility.leaving("GenesisList( Node v )");
}


GenesisList( GenesisList gl )
{
   if (Utility.DEBUG) Utility.entering("GenesisList( GenesisList v )");
   // System.out.println ("Forming new GL from " + gl);
   init();
   p.head = gl.p.head; 
   p.point = gl.p.point; 
   p.parent = new StickyNote(p.head); 
   //skipMetaNodes(); 
   //System.out.println ("POS="+ pos());
   //System.out.println ("New list: " + toString());
   if (Utility.DEBUG) Utility.leaving("GenesisList( GenesisList v )");
}

// ------------------ Methods  --------------------
void init()
{
   if (Utility.DEBUG) Utility.entering("init");
   p =  new GenesisListDesc();
//   p.head = null;
//   p.point = null;
   p.head = new MetaNode();
   p.point = p.head;
   p.parent = new StickyNote(p.head);
   if (Utility.DEBUG) Utility.leaving("init");
}

void done()
{
   if (Utility.DEBUG) Utility.entering("done");
   p = null;
   if (Utility.DEBUG) Utility.leaving("done");
}

void reset()
{
   if (Utility.DEBUG) Utility.entering("reset");
   p.point = p.head; 
   //skipMetaNodes();
   if (Utility.DEBUG) Utility.leaving("reset");
}

boolean empty()
{
  return p.head == null;
}

void del()
{
   if (Utility.DEBUG) Utility.entering("del");
   Node C ;
   StickyNote v;
   if ((p.head == null) || (p.point != null && (p.point.right() == null)))
      System.err.println("delList: No element at current location");
   else if (p.point == null) { //deleting first element
      p.head = p.head.right();
      p.parent.val = p.head;
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

StickyNote get()
{  
   if (Utility.DEBUG) Utility.entering("get with"+p.point);
   StickyNote v = null ;// = new StickyNote();
   //if (TEST) verify (validRep(Self),Self, "valid Representation");
   //skipMetaNodes();

   if (p.point == null)
      if (p.head == null) {
        // System.out.println("GenesisList.get: Attempt to get non-existent element");
        Evaluator.printError("Algorithm is trying to access information from an empty list");
      }
      else // at head of the list
         v = p.head.info;
   else
      if ((p.point.right() == null)) { // after end of the list
         //System.out.println("GenesisList.get: Attempt to get non-existent element");
         Evaluator.printError("Algorithm is trying to access an iterator that is past the end of the list");
      }
      else { // on the list 
         v = p.point.right().info;
         if (Utility.DEBUG) System.out.println("On List");
      }
   //if (Utility.DEBUG) Utility.leaving("get with" , v);
   if (Utility.DEBUG) Utility.leaving("get with " + v.addr());
   return v;
}

// prepend(e) -- Prepend StickNote e to the beginning of the list, without moving point
void prepend(StickyNote e) {
   System.out.println("Entering prepend with" + e);
   if ( p == null) { //System.out.println("Calling init"); 
      init(); 
   }
   Node n = new Node();
   n.info = e;
   n.right =p.head.right;
   if (n.right != null) 
      n.right.left = n;
   p.head = new MetaNode(); /// xxxx
   p.head.right = n;
   n.left = p.head;
   System.out.println("Exiting prepend with" + this);
}

// append(e) -- Append StickNote e to the end of the list, without moving point
void append(StickyNote e) {
   if ( p == null) {// System.out.println("Calling init"); 
       init(); 
   }
   if (p.head == null)
      prepend(e);
   else {
      Node c = p.head;
      while (c.right != null) c = c.right; // Move to last element
      Node n = new Node();
      n.info = e;
      n.right = null;
      n.left = c;
      p.point = n;  // move p.pointer to new  element 
   }
}

void insert(StickyNote e)
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
    skipMetaNodes();
   /*
   if (p.point == null){ // adding at the beginning 
              Utility.println("Adding at the beginning");
       // printDesc();
       //printNode("Adding to",p.head);  
       // Utility.println("parent: " +p.parent);
       n.right =p.head;
       if (p.head != null)
          p.head.left = n;
       p.head = n;
       p.parent.val = p.head;
       p.point = p.head;
       n.left = null;
       //printNode("n",n);  
   }
   else { // add after the beginning 
*/
      n.right = p.point.right();
      if (p.point.right() != null)
         p.point.right().left = n ;
      p.point.right = n;
      n.left = p.point;
      p.point = n;  // move p.pointer to new  element 
  // }
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
// Insert a label and a stickynote
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
void insert(Node n)
{
   if (Utility.DEBUG) Utility.entering("insert");
//   printDesc();
   //if (n instanceof MetaNode) 
      //Utility.println("inserting a MetaNode");
   if ( p == null) {
     //Utility.println("Calling init"); 
     init();
   }
/*
   if (p.point == null){ // adding at the beginning 
//              Utility.println("Adding at the beginning");
//       printDesc();
       n.right =p.head;
       if (p.head != null)
          p.head.left = n;
       p.head = n;
       p.parent.val = p.head;
       p.point = p.head;
       n.left = null;
//       printNode("n",n); 
   }
   else { // add after the beginning 
*/
//      Utility.println("Adding after the beginning");
      n.right = p.point.right();
      if (p.point.right() != null)
         p.point.right().left = n ;
      p.point.right = n;
      n.left = p.point;
      p.point = n;  // move p.pointer to new  element 
   // }
   
//   printNode("n",n);
//   printNode("p.head",p.head);
//   printNode("p.point",p.point);
//   Utility.println("-------------------------");

   if (Utility.DEBUG) Utility.leaving("insert");
}

void printDesc() // print a single list node
{  
   
    Utility.println("p:       "+p);
    Utility.println("p.head:  "+p.head);
    Utility.println("p.point: "+p.point);
}
void printNode(String s,Node LN) // print a single list node
{  
   //if (Utility.DEBUG) Utility.entering("GenesisList::printNode");
   Utility.println(s+": "+LN);
   Utility.print(s+"->info: ");
   if (LN != null) {
      LN.info.displayln();
      Utility.print(s+"->left: ");
      Utility.println(LN.left());
      Utility.print(s+"->right: ");
      Utility.println(LN.right);
      Utility.println("--------------------");
   }
   else {
      Utility.println("Node is null");
   }
   //if (Utility.DEBUG) Utility.leaving("GenesisList::printNode");
}
void printNodes(Node SN)  // print a list of listnodes
// SN is the node from the source list
// DN is the node in the destination list
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

void printNodes()  // print a list of listnodes
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

void move()
{
   //Utility.DEBUG = true;
   if (Utility.DEBUG) Utility.entering("GenesisList::move");
   //skipMetaNodes();
/*
      if (p.point == null) {
         p.point = p.head;
      }
      else 
 */
      if ((p.point.right() != null)){
         p.point = p.point.right();
      }
//  printNodes(p.head);
  if (Utility.DEBUG) Utility.leaving("GenesisList::move");
}

////////////////////////////////////////////////////
// added by Wes Potts 4/8/04
void move( int n )
{
        if (Utility.DEBUG) Utility.entering("GenesisList:move("+n+")");
        if ( n < 0 || n > size() )
        {
                Evaluator.printError("Error: either passing a negative or " +
                "passing n larger than size() to move(int n)" );
        }
/*
        if ( p.point == null && n > 0 )
        {
                p.point = p.head;
                // skipMetaNodes();
                n--;
        }
*/
        while ( n > 0 && p.point.right() != null )
        {
                p.point = p.point.right();
                // skipMetaNodes();
                n--;
        }
        if (Utility.DEBUG) Utility.leaving("move(int n)");
}

// added by Wes Potts 4/8/04
void moveTo( int n )
{
        if (Utility.DEBUG) Utility.entering("goToItem("+ n+")");
        n--;
        if ( n < 0 || n > size() )
        {
                Evaluator.printError("Attempt to move beyond the boundaries of a list");
        }
        reset();
        move( n );
        if (Utility.DEBUG) Utility.leaving("goToItem( int n )");
}

// added by Wes Potts 4/8/04
// getSubList -- create sublist from list contained in the current node

GenesisList getSubList()
{
        if (Utility.DEBUG) Utility.entering( "getSubList()" );
        if ( empty() )  //p.point == null && p.head == null )
        {
                Evaluator.printError("Cannot form a sublist from an empty list");
        }
        GenesisVal v = null;
/*
        if ( p.point == null ) v = p.head.info.val;
        else 
*/
        if ( p.point.right == null )
        {
                Evaluator.printError("Cannot form a sublist  starting past the end of the list" );
        }
        else v = p.point.right.info.val;
        if ( ! (v instanceof Node) )
        {
                Evaluator.printError("Error: trying to form a sublist from "
                                + "a non-list element" );
        }
        if (Utility.DEBUG) Utility.leaving( "getSubList()" );
        return new GenesisList( (Node)v );
}
////////////////////////////////////////////////////

boolean off() { // Loo
    if (Utility.DEBUG) Utility.entering("GenesisList::off");
    boolean ans =(p.head == null) || 
          ((p.point != null) && (p.point.right() == null));
    
    if (Utility.DEBUG) Utility.leaving("off with",new Boolean(ans));
    return ans;
   // ljm: Why isn't the following equivalent???
   // return (p.point == null) || (p.point.right() == null); // short circuit
}

boolean on() {
    boolean on;
    if (Utility.DEBUG) Utility.entering("GenesisList::on");
    // on = (p.head != null) &&
             // ((p.point == null) || (p.point.right() != null));
    on = p.point.right != null;
   if (Utility.DEBUG) Utility.leaving("GenesisList::on with ", new Boolean(on));
   return on;
}


boolean atFirst() {
//      return  on() && p.point != null && p.point.right == p.head; // ljm: 3/11/07
     //return p.point == null;
     return pos() == 1;
}


boolean atLast() {
    if (Utility.DEBUG) Utility.entering("GenesisList::atLast");
    boolean ans = false;
    Node t;
/*
    if (p.point == null)
       t = p.head;
    else
       t = p.point.right(); 
    //t=skipMetaNodes(t);  // advance t so its successor is not a MN
    //if (t.right() != null && t.right().right() == null) 
    if (t 1=
        ans =  true;
    // printNodes();
*/
    ans = p.point.right() != null && p.point.right().right() == null;
    if (Utility.DEBUG) Utility.leaving("GenesisList::atLast with " ,new Boolean(ans) );
    return ans;
}

void change(StickyNote e) {
     // if ((p.head == null) || ((p.point != null) &&  (p.point.right() == null)))
/*
     if (p.point.right() == null)
        Utility.println ("List.change: no element at current location;"
                            +  " no change");
                        // edited by Wes Potts... think there's a bug here
                        else if ( p.point == null )
                        {
                                p.head.info = e;
                        }
      else {
*/
         p.point.right.info = e; // ljm: 8/11/04
      //}
}

// ljm:  Could have a problem here ... what if the "list" that is
//       wrapped is not constructed using GenesisList ... i.e. 
//       what if it is a "tree" in which the left pointer does not
//       point to the previous node of the list, rather to the 
//       leftmost immediate descendant
//  It is therefore crucially important that this operation never
//  be used on any list not constructed via the GenesisList wrapper
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
void left()
{
   if (Utility.DEBUG) Utility.entering("left");
   if (p.point != p.head)  // can move
      if (p.head.right() == p.point) // to the head
         p.point = p.head;  
      else                           // or to the left
         p.point = p.point.left(); 
   if (Utility.DEBUG) Utility.leaving("left");
}

void prev() {
    left();
}
String typeName()
{  
   return "GenesisList";
}

int size()
{
   Node    n;
   int count;
   if (Utility.DEBUG) Utility.entering("size");
   count = 0;
   n = p.head;
   while (n != null){
      if (! (n instanceof MetaNode))
         count = count + 1;
      n = n.right();
   }
   if (Utility.DEBUG) Utility.leaving("size");

   return count;
}

int pos()
{
   Node t;  int n ;
   if (Utility.DEBUG) Utility.entering("pos");
      // printDesc();
   if (p.point.right() == p.head) {
      n = 1;
      //Utility.println("Finalizing pos as 1");
   }
   else {
      n = 1; t= p.head;
      //Utility.println("Setting pos to 1");
      //Utility.println("p.head == p.point" + (p.head == p.point));
      //Utility.println("p.point == null" + (p.point == null));
      while (t != p.point){
         //Utility.println("Bumping pos to " + t);
         t = t.right();
         if (! (t instanceof MetaNode)) {
           //Utility.println("Bumping pos by 1");
            n = n + 1;
         }
      }
   } 
   //Utility.println("Exiting pos with " + n);
   if (Utility.DEBUG) Utility.leaving("pos");
   return n; 
}

Node current() {
   //skipMetaNodes();
   if (off()) {
      Evaluator.printError("Attempt to access non-existent element");
   }
/*
   if (p.point == null) return p.head;
   else 
*/
        {//Utility.println("Returing " +  p.point.right);
         // return p.point.right();
         //  Wow 8 years and never discovered that the follwing was correct
         //  instead of the above.  Causes lists not to compare correctly
         //  when current() was used to return the list when adding a single
         //  element to a list
         return p.point;
   }
                
}

StickyNote parent() {
   return p.parent;
}

boolean equals(GenesisList e)
{
   //System.out.println ("Comparing");
   boolean equal; 
// two Lists are equal if they have the same values in their nodes 
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

public boolean eq(GenesisVal gv) {
   //System.out.println ("Comparing lists");
   if (! (gv instanceof GenesisList) )
      return false;
   return equals((GenesisList)(gv));
}

StickyNote search(StickyNote A)
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
   //}
   return ans;
}
*/

StickyNote getLabel () {
   StickyNote ans = null;
   // System.out.println ("Entering GenesisList::getLabel");
   // retrieve the label associated with the node
/*
   if (p.point == null) {
      ans =new StickyNote(p.head.info.getLabel());
   }
   else {
*/
      ans = new StickyNote(p.point.next().info.getLabel());
   // }
/*
   System.out.println("Returning: " + p);
   System.out.println("Returning: " + p.point);
   System.out.println("Returning: " + p.point.next());
   System.out.println("Returning: " + p.point.next().info);
   System.out.println("Returning: " + p.point.next().info.getLabel());
   System.out.println("Returning: "  + ans);
*/
   return ans;
}

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

int  searchLabel(StickyNote s)  // Search for a label in the node
{
   int pos=0;
   // System.out.println("Returning pos = " + pos);
   reset();
   StickyNote l = null;
   if (on()) {  // list is non-empty
      pos = 1;
      l = getLabel();
      //System.out.println ("Comparing " + l + " and " + s + " and " + on());
      while ( on() && (l == null || ! l.equals(s))) {
         move();
         pos = pos + 1;
         if (on()) l = getLabel();
         //System.out.println ("Comparing " + l + " and " + s);
      }
  }
  if (l == null || ! l.equals(s)) pos = 0;
   //System.out.println("searchLabel: Returning pos = " + pos);
  return pos;
}
private int recurseLimit = 100;

public String implode() {
  int maxprintLength = 1000;
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
public String toString() {

  if (Utility.DEBUG) Utility.entering("GenesisList::toString");
  // printNodes(p.head);
  recurseLimit --;
  if (recurseLimit == 0) {
     Evaluator.printError("Too many strings printed ... perhaps you have data loop\n"
    + "in a list?");

  }
  final String Openchar = "<";
  final String  Closechar = ">";
  String result = Openchar;
// printNodes();
  int position=pos();
  // Utility.println("Computed position is " + position);
  int i;
  StickyNote a; 
  reset();
  
  i = 1;
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
    if (a.getLabel() != null ) {
       //System.out.println("Label found:" +getLabel());
       result = result +  getLabel() + ":";

    }
    String current = "";
    if (i==position) current = currentIndicator; //currentIndicator ;
    if (true)  {
     //Utility.print("Return from get() with:");
     //Utility.println(a.val.toString()); // System.exit(1);
    // Utility.println ("Appending '" + a + "'");
       if (a != null) { 
          //System.out.println("Stringing:"+a.addr());
          String v = a.toString();
          if ( a.val instanceof StringVal && myQuoteStrings) {
            result= result + current + quotedStringOpen + v + quotedStringClose;
          }
          else {
            result= result + current +v;
          }
       }
       else {
          result = result + null;
       }
    //Utility.println ("Aha!!"+result);
    }
    else
       result = result 
             + "[@" 
                       + a.val.addr() 
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
        Evaluator.printError("Too many elements in the list to convert to printable form (more than " + MAXPRINT + " elements)\n");
        
     }

  reset();
  result = result + Closechar;
  while (position > 1) { move(); position=position-1;}
  if (Utility.DEBUG) Utility.leaving("GenesisList::toString");
  return result;
}

private Node skipMetaNodes(Node n) {
   if (n != null)
      while (n.right() instanceof MetaNode) n = n.right();
   return n;
}

// Noet that this routine does nothing now !  -- schedule if for deletion!
private void skipMetaNodes() {
   //if (Utility.DEBUG) Utility.entering("GenesisList::skipMetaNodes");
   Node n; 
/*
   if (p.point == null) { // at beginning of the list
      n = p.head;
      //Utility.println("Setting skipMetaNodes with "+ n);
   }
   else  {
*/
      n = p.point.right(); 
 //  }
/*
   while (n instanceof MetaNode)  {
      //Utility.println("Skipping an interior MetaNode");
      p.point = n;
      n = n.right();
   //Utility.println("Setting  p.point to "+ p.point);
   }
   //Utility.println("Leaving skipMetaNodes with "+ p.point);
   //if (Utility.DEBUG) Utility.leaving("GenesisList::skipMetaNodes");
*/
}

private void skipLeftMetaNodes() {
   while (p.point != null && p.point instanceof MetaNode)
        p.point = p.point.left();
   //if (p.point != null) p.point = p.point.left();
}
public static void quoteStrings( boolean q ) { myQuoteStrings = q; }

public void display () { Utility.print(this);}
public void displayln () { Utility.println(this);}

// Can significantly simplify this since the only metanode is now at the beginning of the list
// Schedule this for  simplification

GenesisList copy () {
   if (Utility.DEBUG) Utility.entering ("GenesisList::copy");
   int position = pos();
   GenesisList gl = new GenesisList();
   Node t = p.head;
   // printNodes();
   Node head = null;
   Node prev = null;
   while ( t != null) {
      Node n;
      if (t instanceof MetaNode) {
          //Utility.println("Making a MN from " + t + "(" + t.info+")");
         n = new MetaNode(); 
      }
      else {
         n = new Node(); 
      }
      if (head == null) head = n;
      n.info = new StickyNote(t.info);
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

public boolean lt( GenesisVal rhs ) {return false;}
public boolean le( GenesisVal rhs ) {return false;}
public boolean gt( GenesisVal rhs ) {return false;}
public boolean ge( GenesisVal rhs ) {return false;}
public boolean ne( GenesisVal rhs ) {return false;}

public GV add (GV rhs) {
   // Copy nodes in the list

   GenesisList gl = copy();

   // Move to the end of the list

   while ( gl.on() ) {
      gl.move();
   }

   if (rhs instanceof GenesisList) {
      GenesisList rhsList = ((GenesisList) rhs).copy();
      rhsList.reset();
      while ( rhsList.on()) {
         StickyNote sn = rhsList.get();
         gl.insert(sn);
         rhsList.move();
      }
   }
   else if (rhs instanceof GV) {
      gl.insert (new StickyNote(rhs));  
   }
   return gl;
}
// ------------ Test procedures ----------- 
void ListsTest()
{
   Utility.println("Testing %s for satisfaction of LIST rules\n"
                      + typeName());
   Utility.print("List value:"); 
   Utility.println ("End of LIST tests for "+typeName()+'\n');
}

static void listsSelfTest()
{
   GenesisList    l = new GenesisList();;
   StickyNote  IO = new StickyNote();;

   l.init(); IO = new StickyNote(5); l.insert(IO);
   l.ListsTest();
   IO = new StickyNote(20); l.insert(IO);
   l.insert(IO);
   l.ListsTest();
}

boolean noLoops() {
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

    // valid - true iff  the representation of the value
               // satisfies obvious properties
boolean valid() {
  boolean    VR;
  VR = true;
//  with (A.P)^)
  VR = p.head == null;
  if (! VR) Utility.println("List has no descriptor");

  VR = p.head == null;
  if (! VR) Utility.println("List current not on a node");
  VR =  noLoops();
  if (! VR)Utility.println("List seemingly has a loop");
  return VR;
}

public static void main (String a[]){
   GenesisList l = new GenesisList();
   StickyNote ie = new StickyNote(33);
   GenesisList xl = new GenesisList();
   StickyNote ie1 = new StickyNote(11);
   StickyNote ie2 = new StickyNote(22);
   StickyNote ie3 = new StickyNote(33);
   StickyNote ie4 = new StickyNote(44);
   StickyNote ie5 = new StickyNote(55);
   xl.insert(ie1); 
   //xl.printNodes(xl.p.head);
   //xl.reset();
 //  System.out.println("-----------------");
//   xl.displayln(); 
  // System.out.println("-----------------");
   //System.out.println("atLast: " + xl.atLast());
   
   xl.insert(ie2); 
   xl.insert(ie3); 
   xl.insert(ie4); 
   xl.insert(ie5); 
   xl.reset();
   xl.displayln();
   System.out.println("");
   System.out.println(xl.size());
   while (xl.on()) {
      System.out.println("on is true"); 
      xl.move();
      System.out.println("");
      xl.displayln();
   }
   xl.reset();
   
   System.out.println("-> Processing all but the last" ); // Wes
   while (!xl.atLast()) {
      System.out.println("atLast is false"); 
      xl.displayln();
      xl.move();
   }
   System.out.println("atLast is " + xl.atLast()); 
   xl.displayln();
  ie.displayln();
   for ( int i = 0; i < 10; i++ ) {
     ie = new StickyNote(i);
     l.insert(ie);
     l.displayln();
   }
   l.left();
   l.displayln();
   System.out.println("Displaying backward through the list");
   StickyNote e;
   while (! l.atFirst() ) {
     e = l.get();  
     e.displayln(); 
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
     e.displayln();
     l.del(); l.displayln(); //System.out.println("atLast: " + l.atLast());
   }
   // Now check the modification of iterators
   StickyNote sn = new StickyNote();
   StickyNote snval = new StickyNote("A value");
   GenesisList iter = new GenesisList(sn);
   // Utility.println ("Got here!"+iter);
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
   System.out.println("Address of sn.val: "+ sn.val.addr());
   GenesisList gl = new GenesisList();
   gl.insert(sn); gl.reset();
   StickyNote result = gl.get();
   System.out.println("Address of result: " + result.addr());
   System.out.println("Address of result.val: "+ result.val.addr());
   System.out.print("gl = ");
   gl.displayln();
   gl.move(); gl.move();
   gl.insert(snval);
   GenesisList y = new GenesisList(gl);
   gl.insert(snval);
   gl.insert(snval);
   System.out.print("gl = ");
   gl.displayln();
   y.displayln();
   
   GenesisList newlist = gl.copy();
   newlist.displayln();
   GenesisList nl =  (GenesisList) newlist.add(gl);
   nl.displayln();
  GenesisList myList = new GenesisList();
   myList.insert(new StickyNote(10));
   myList.insert(new StickyNote(20));
   myList.insert(new StickyNote(30));
   myList.moveTo(2);
   System.out.println("Value at position 2 is " + myList.get());

}
}
// end of GenesisList stuff
/////////////////////////////////////////////////////////////////


