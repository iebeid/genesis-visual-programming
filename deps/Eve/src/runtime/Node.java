package runtime;

// Node.java



// class extends StickyNote, we can set some particular value to
// the raw list data of a ListVal.

/* Modification History
   5/04  -- Original version
   6/04  -- Morell: Added TreeNode to include context information
                    and to allow OpVal's to be stored as info's (rather than DoubleVal's)
                    to indicate the operator in the AST
   6/16/04 -- ljm: Added calls to super(...) in TreeNode constructors
   7/6/04  -- ljm: Added MetaNode - to hold meta information about lists
   5/06    -- chad:ljm: Added label to nodes for records/hashing
   7/29/06 -- ljm: added pretty-printing routines for TreeNode.toString()
   8/12/06 -- ljm: fixed problem with prettyprinting of Generates w/o tasks
   10/11/08 -- ljm: 'not' was not processed as a unary op
*/

/**
 * Implements two-way nodes which store Values
 * @author Larry Morell <morell@cs.atu.edu>
 */
public class Node extends Val {
    public StickyNote info;
    public Node left;
    public Node right;
//    public StickyNote label;  // Changed by LJM to record label

    // Constructors
    public Node (){
        info = null;
        left = null;
        right = null;
        //label = null;                    //added by chad for record label
    }

    public Node (StickyNote tok, Node p, Node n) {
        info = tok;
        left = p;
        right = n;
        //label = null;                    //added by chad for record label
    }
    public Node (double d) {
        info =(new  StickyNote(d));
        left = null;
        right = null;
        //label = null;                    //added by chad for record label
    }
    public Node (double d, Node p, Node n) {
        info = (new StickyNote(d));
        left = p;
        right = n;
        //label = null;                    //added by chad for record label
    }
    public Node (String s, Node p, Node n) {
        info = (new StickyNote(s));
        left = p;
        right = n;
        //label = null;                    //added by chad for record label
    }

    public Node (StickyNote tok){
        info = tok;
        left = null;
        right = null;
        //label = null;                    //added by chad for record label
    }

    public Node (Node n){
        info = n.info;
        left = n.left;
        right = n.right;
        //label = null;                    //added by chad for record label
    }

    public Node (String s){
        info = new StickyNote(s);
        left = null;
        right = null;
        //label = null;                    //added by chad for record label
    }
   

    public Node next() {return right;}
    public Node prev() {return left;}
    public Node right() {return right;}
    public Node left() {return left;}
    public StickyNote getVal() {return info;}
	@Override
    public boolean eq( Value rhs )  // eq iff the same node
    {
      System.out.println ("Comparing eq here " + this.getClass() + " " + rhs.getClass());
      return this.equals(rhs);
    }

    public boolean lt( StickyNote rhs )
    {
      System.err.println( "Cannot compare lists with any other value" );
      System.exit( 1 );
      return false;
    }

    public boolean le( StickyNote rhs )
    {
		 System.err.println( "Cannot compare lists with any other value" );
      return false;
    }

    public boolean gt( StickyNote rhs )
    {
      System.err.println( "Cannot compare lists with any other value" );
      System.exit( 1 );
      return false;
    }

    public boolean ge( StickyNote rhs )
    {
      System.err.println("Cannot compare lists with any other value" );
      System.exit( 1 );
      return false;
    }
    public boolean ne( StickyNote rhs )
    {
      return (!eq(rhs.getVal()) );
      
    }

//    public Compare add( Compare rhs ){
//      ListVal gl = new ListVal(this).copy();
//      while (gl.on())gl.move();  // move to the end of the list
//      if ( rhs instanceof StringVal ) {
//         gl.insert(new StringVal(rhs));
//      }
//      return null;
//    };

/*
    public void setLabel(StickyNote s) {
         //System.out.println ("Setting label of " + this + " to " + s);
         label = new StickyNote(s);
    }  // added temp for label chad
/*
    public String getLabel() {
       if (label == null) return null;
       System.out.println ("getLabel:" + label.val);
       return label.val.toString();
    }  // added temp for label chad
    public StickyNote getLabel() {
  
       System.out.println ("Node:getLabel:" + label);
       if (label == null) return null;
       return label;
    }  // added temp for label chad

*/
    static public Node str2Node(String source) {
       Node firstNode=null;
       if (source.length() != 0) {
          int curIndex=0;
          Node node=new Node(""+source.charAt(0));
          firstNode=node;
          
          curIndex++;
          while(curIndex<source.length()){
            node.right=new Node(source.substring(curIndex,curIndex+1));
            node=node.right;
            curIndex++;
          }
       }
       return(firstNode);
    }
    public static void main (String args[]) {  // ljm: 7/12/04
       Node n1 = new Node();
       Node n2 = n1;
       if ( n1.equals(n2)) 
          System.out.println("The nodes are equal");
       else
          System.out.println("The nodes are not equal");
      
    }

	@Override
	public Val copy() {
		return new Node (this);
	}


}  // end of class Node

