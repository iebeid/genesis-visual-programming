// Node.java
// This class implements a list node for GenesisList.  Because this
// class extends GenesisVal, we can set some particular value to
// the raw list data of a GenesisList.

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


class Node extends GV {
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
        info = new StickyNote(new DoubleVal(d));
        left = null;
        right = null;
        //label = null;                    //added by chad for record label
    }
    public Node (double d, Node p, Node n) {
        info = new StickyNote(new DoubleVal(d));
        left = p;
        right = n;
        //label = null;                    //added by chad for record label
    }
    public Node (String s, Node p, Node n) {
        info = new StickyNote(s);
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
    public Node (OpVal ov){
        info = new StickyNote(new OpVal(ov));
        left = null;
        right = null;
        //label = null;                    //added by chad for record label
    }

    public Node(OpVal ov,Node l, Node r){
        info = new StickyNote(new OpVal(ov));
        left = l;
        right = r;
        //label = null;                    //added by chad for record label
    }

    public Node next() {return right;}
    public Node prev() {return left;}
    public Node right() {return right;}
    public Node left() {return left;}
    public StickyNote getVal() {return info;}
    public boolean eq( GenesisVal rhs )  // eq iff the same node
    {
      System.out.println ("Comparing eq here " + this.getClass() + " " + rhs.getClass());
      return this.equals(rhs);
    }

    public boolean lt( GenesisVal rhs )
    {
      Evaluator.printError( "Cannot compare lists with any other value" );
      System.exit( 1 );
      return false;
    }

    public boolean le( GenesisVal rhs )
    {
      Evaluator.printError( "Cannot compare lists with any other value" );
      return false;
    }

    public boolean gt( GenesisVal rhs )
    {
      Evaluator.printError( "Cannot compare lists with any other value" );
      System.exit( 1 );
      return false;
    }

    public boolean ge( GenesisVal rhs )
    {
      Evaluator.printError( "Cannot compare lists with any other value" );
      System.exit( 1 );
      return false;
    }
    public boolean ne( GenesisVal rhs )
    {
      return (!eq(rhs) );
      
    }

    public GV add( GV rhs ){
      GenesisList gl = new GenesisList(this).copy();
      while (gl.on())gl.move();
      if ( rhs instanceof StringVal ) {
         gl.insert(new StickyNote(rhs));
      }
      return null;
    };

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


}  // end of class Node


class Context {
   public int lineNo;
   public String fileName;
   public int charPos;
   Context() {lineNo = 0; charPos= 0; fileName = "";}
   Context(int ln, int cp ) {lineNo = ln; charPos= cp;}
   Context(int ln, int cp, String fn ) {lineNo = ln; charPos= cp; fileName = "";}
   Context(Token t) {
      lineNo = t.lineNo();charPos= t.charPos(); 
      fileName =t.fileName();
      //System.out.println("Context set to " + this);
   }
   public String toString () {
      String ans = "(" + fileName + "/" + lineNo + "/" + charPos + ")";
      return ans;
   }
} // Context

class TreeNode extends Node {
    Context context;
    int prettyNo; // the pretty-print line number
    private static int prettyPrintNo;
    public TreeNode (){
        context = new Context();
        prettyNo = 0;

    }

    public TreeNode (StickyNote sn, TreeNode p, TreeNode n) {
       super (sn,p,n);
       if (p != null) 
          context = p.context;
       else 
          context = new Context(Parser.t);
       prettyNo = 0;
    }

    public TreeNode (double d) {
       super (d);
       context = new Context(Parser.t);  // must be set manually
       prettyNo = 0;
    }

    public TreeNode (double d, TreeNode p, TreeNode n) {
       super (d,p,n);
       if (p == null)
          context = new Context(Parser.t);
       else
          context = p.context;
       prettyNo = 0;
    }

    public TreeNode (String s, TreeNode p, TreeNode n) {
       super (s,p,n);
       if (p == null)
          context = new Context(Parser.t);
       else
          context = p.context; 
       prettyNo = 0;
    }

    public TreeNode (StickyNote sn){
        super (sn);
        context = new Context(Parser.t); // must be set manually
//       System.out.println("Context for sn of  " + sn  +  " set to " + context);
        prettyNo = 0;
    }

    public TreeNode (TreeNode n){
       super (n);
       context = n.context; // must be set manually
       prettyNo = 0;
    }

    public TreeNode (Node n){
       super (n);
       context = new Context(Parser.t); // must be set manually
       prettyNo = 0;
    }

    public TreeNode (String s){
       super (s);
       context = new Context(); // must be set manually
       prettyNo = 0;
    }

    public TreeNode (Token t){
       super (t.val);
       context = new Context(t);
       //context.lineNo = t.lineNo();
       //context.charPos = t.charPos();
       //context.fileName = t.fileName();
//       System.out.println("Context for tn of token " + t  +  " set to " + context);
       prettyNo = 0;
    }
    public TreeNode (OpVal ov){
       info = new StickyNote(new OpVal(ov));
       context = new Context(Parser.t); // must be set manually
       prettyNo = 0;
    }

    public TreeNode(OpVal ov,TreeNode l, TreeNode r){
       info = new StickyNote(new OpVal(ov));
       left = l;
       right = r;
       if (l != null )
          context = l.context;
       else
          context = new Context(Parser.t);
       prettyNo = 0;
    }

    public void setLineNo(int lineNo) {context.lineNo = lineNo;}
    public void setPrettyNo(int ln) {prettyNo = ln;}
    public void setCharPos(int charPos) {context.charPos = charPos;}
    public void setFileName(String fileName) {context.fileName = fileName;}
    public void setContext(String fileName, int lineNo, int charPos) {
       context.fileName = fileName;
       context.lineNo = lineNo;context.charPos = charPos;
    }
    public void setContext(Token t) {
       context.fileName = t.fileName();
       context.lineNo = t.lineNo();
       context.charPos = t.charPos();}
    public int prettyNo () { return prettyNo; }
    public int lineNo() {return context.lineNo;}
    public int charPos() {return context.charPos;}
    public String fileName() {return context.fileName;}
    private static int prettyIndent = 0;

    String indentString =  "                                                                        ";
    private void appendIndent(StringBuilder sb) {
       sb.append(indentString.substring(0,prettyIndent));
    }
   
    public  String toString () {
              //System.out.println("Entering toString with " 
                     //+info.val + " " 
                     //+info.val.getClass());
       StringBuilder result = new StringBuilder(100);
       result.append(""); // don't know if this is needed, but hey ...
       return new String(this.toStringBuilder(result));
    }

    int precedence (TreeNode tn) {
       int prec=OpVal.highestPrec(); // KLUGE: Fixing problem with a TreeNode that is null
                                     // Further analysis of this is necessary to figure 
                                     // out why a null TreeNode would ever be passed 
       if (tn != null && tn.info.getVal() instanceof OpVal) 
          prec = OpVal.prec[((OpVal)tn.info.getVal()).getVal()];
       return prec;
    }
    private void appendUnaryOp(StringBuilder result, String oper)  {
       int prec1 = precedence(this);
       int prec2 = precedence((TreeNode)left);
       //System.out.println("p1=" + prec1 + " p2=" + prec2);
       result.append(oper);
       if ( prec1 > prec2) {
          result.append('(');
       }
       result.append(left);
       if (prec1 > prec2) {
          result.append(')');
       }
    }
    private void appendBinaryOp(StringBuilder result, String oper)  {
       //System.out.println("Entering appendBinaryOp with "+ oper);
       int prec1 = precedence(this);
       //System.out.println("prec1=" + prec1);
       int prec2 = precedence((TreeNode)left);
       //System.out.println("prec2=" + prec2);
       int prec3 = precedence((TreeNode)left.right);
       //System.out.println("prec3=" + prec3);
       //System.out.println("p1=" + prec1 + " p2=" + prec2);
       if ( prec1 > prec2) {
          result.append('(');
       }
       result.append(left);
       if (prec1 > prec2) {
          result.append(')');
       }
       result.append(oper);
       //System.out.println("p1=" + prec1 + " p3=" + prec3);
       if (prec1 > prec3) {
          result.append('(');
       }
       result.append(left.right);
       if (prec1 > prec3) {
          result.append(')');
       }
    }

    // line number  for the prettyprinted algorithm
    private static int prettyLineNo = 0;  
    private static boolean printLineNo = true;
    private StringBuilder toStringBuilder(StringBuilder result) {
      //System.out.println("Entering with " + info + " " +info.val.getClass());

      if (printLineNo && prettyNo != 0) {
         result.append(prettyNo);
         result.append(": ");
      }
      if (info.val instanceof OpVal) {
         OpVal ov = (OpVal) info.val; 
         
         /* Binary operators */
         if (ov.eq(OpVal.modOp)) {
            appendBinaryOp(result,"%");
         }
         else if (ov.eq(OpVal.addOp)) {
            appendBinaryOp(result,"+");
         }
         else if (ov.eq(OpVal.dotOp)) {
         }
         else if (ov.eq(OpVal.subtractOp)) {
            appendBinaryOp(result,"-");
         }
         else if (ov.eq(OpVal.multiplyOp)) {
            appendBinaryOp(result,"*");
         }
         else if (ov.eq(OpVal.divideOp)) {
            appendBinaryOp(result,"/");
         }
         else if (ov.eq(OpVal.orOp)) {
            appendBinaryOp(result," or ");
         }
         else if (ov.eq(OpVal.andOp)) {
            appendBinaryOp(result," and ");
         }
         else if (ov.eq(OpVal.gtOp)) {
            appendBinaryOp(result," > ");
         }
         else if (ov.eq(OpVal.ltOp)) {
            appendBinaryOp(result," < ");
         }
         else if (ov.eq(OpVal.geOp)) {
            appendBinaryOp(result," >= ");
         }
         else if (ov.eq(OpVal.leOp)) {
            appendBinaryOp(result," <= ");
         }
         else if (ov.eq(OpVal.eqOp)) {
            appendBinaryOp(result," = ");
         }
         else if (ov.eq(OpVal.neOp)) {
            appendBinaryOp(result," != ");
         }
         /* Unary operators */

         else if (ov.eq(OpVal.unaryMinusOp)) {
            appendUnaryOp(result, "-");
         }
         else if (ov.eq(OpVal.numberOp)) {
            result.append(left);
         }
         else if (ov.eq(OpVal.notOp)) {
            appendUnaryOp(result,"not ");
         }
         else if (ov.eq(OpVal.stringOp)) {
            result.append('"');
            String s = ((TreeNode)left).toString();
            for (int i=0; i < s.length(); i++) {
               if (s.charAt(i) == '"') 
                  result.append('\\');
               result.append(s.charAt(i));
            }
            result.append('"');
         }
         else if (ov.eq(OpVal.trueOp)) {
            result.append("true ");
         }
         else if (ov.eq(OpVal.falseOp)) {
            result.append("false ");
         }
         else if (ov.eq(OpVal.whileOp)) {
            result.append(" while (");
            result.append(left);
            result.append(')');
         }
         else if (ov.eq(OpVal.generateStmtOp)) {
            result.append("Generate ");
            result.append(left);
            result.append(" from ");
            result.append(left.right);
            result.append("\n");
         }
         else if (ov.eq(OpVal.generatorRefOp)) {
            result.append(left);
            result.append("(");
            result.append(left.right);
            result.append(")");
         }
         else if (ov.eq(OpVal.headerOp)) {
            result.append(left);
            result.append('(');
            result.append(left.right);
            result.append(')');
         }
         else if (ov.eq(OpVal.rangeNxNxN_Op)) {
            result.append("from ");
            result.append(left);
            result.append(" thru ");
            result.append(left.right);
            result.append("by ");
            result.append(left.right.right);
         }
         else if (ov.eq(OpVal.rangeNxN_Op)) {
            result.append("from ");
            result.append(left);
            result.append(" thru ");
            result.append(left.right);
         }
         else if (ov.eq(OpVal.rangeSxS_Op)) {
            result.append("from '");
            result.append(left);
            result.append("' thru ");
            result.append(left.right);
         }
         else if (ov.eq(OpVal.rangeS_Op)) {
            result.append("from '");
            result.append(left);
         }
            else if (ov.eq(OpVal.parameterOp)) {
               TreeNode t= (TreeNode) left;
               while (t != null) {
                  result.append(t);
                  if (t.right != null) 
                  result.append(", ");
                  t= (TreeNode) t.right;
               }
            }
            else if (ov.eq(OpVal.condOp)) { 
               if (left != null) {
                  result.append('(');
                  result.append(left);
                  result.append(')');
               }
            }
            else if (ov.eq(OpVal.aliasOp)) {
               result.append("alias ");
               result.append(left);
            }
            else if (ov.eq(OpVal.fileOp)) {
               result.append("file ");
               result.append(left);
            }
            else if (ov.eq(OpVal.otherwiseOp)) {
               result.append("otherwise ");
            }
            else if (ov.eq(OpVal.taskOp)) {
               if (left.left != null) {
                  appendIndent(result); result.append("@f:\n"); prettyPrintNo++;
                  prettyIndent = prettyIndent + 3;
                  result.append(left);
                  prettyIndent = prettyIndent - 3;
               }
               if (left.right.left != null) {
                  appendIndent(result); result.append("@i:\n"); prettyPrintNo++;
                  prettyIndent = prettyIndent + 3;
                  result.append(left.right);
                  prettyIndent = prettyIndent - 3;
               }
               if (left.right.right.left != null) {
                  appendIndent(result); result.append("@l:\n"); prettyPrintNo++;
                  prettyIndent = prettyIndent + 3;
                  result.append(left.right.right);
                  prettyIndent = prettyIndent - 3;
               }
            }
            else if (ov.eq(OpVal.subscriptOp)) {
               result.append(left);
               result.append('[');
               result.append(left.right);
               result.append(']');
            }
            else if (ov.eq(OpVal.listOp)) {
               result.append("< ");
               TreeNode t= (TreeNode) left;
               while (t != null) {
                  result.append(t);
                  if (t.right != null)
                     result.append(", ");
                  t= (TreeNode) t.right;
               }
               result.append(">");
            }
            else if (ov.eq(OpVal.guardedStmtOp)) {
               appendIndent(result); 
               result.append(left);
               result.append ("-> \n");
               prettyIndent = prettyIndent+3;
               result.append(left.right);
               prettyIndent = prettyIndent-3;
            }
            else if (ov.eq(OpVal.debugOp)) {
               result.append("DEBUG ");
               result.append(left);
            }
            else if (ov.eq(OpVal.selectOp)) {
               appendIndent(result); 
               result.append("Select \n");
               prettyIndent =  prettyIndent + 3;
               TreeNode t = (TreeNode) left;
               while (t != null) {
                  result.append(t);
                  t = (TreeNode)t.right;
               }
               prettyIndent =  prettyIndent - 3;
            }
            else if (ov.eq(OpVal.functionCallOp)) {
               result.append(left);
               result.append('(');
               result.append(left.right);
               result.append(')');
            }
            else if (ov.eq(OpVal.iteratorOp)) {
               result.append("iter ");
               result.append(left);
            }

         /* Statements */
         else {  
         
            appendIndent(result); 
            if (ov.eq(OpVal.stmtListOp)) {
            //System.out.println("Got to stmtListOp");
               TreeNode t = (TreeNode) left;
               result.append("   {\n");  prettyIndent = prettyIndent+3;
               while (t != null) {
                  result.append(t);
                  t = (TreeNode) t.right;
               } 
               prettyIndent = prettyIndent-3;
               //result.append(prettyNo);
               appendIndent(result); 
               result.append("   }"); 
            }
            else if (ov.eq(OpVal.taskDefOp)) {
            }
            else if (ov.eq(OpVal.functionDefOp)) {
               result.append("Let ");
               result.append(left);
               result.append(" name function "); 

               result.append(left.right);
            }
            else if (ov.eq(OpVal.procedureDefOp)) {
               result.append("Let ");
               result.append(left);
               result.append(" name procedure ");
               result.append(left.right);
            }
            if (ov.eq(OpVal.idNameOp)) {
               result.append("Let ");
               result.append(left);
               result.append(" name ");
               result.append(left.right);
            }
            else if (ov.eq(OpVal.idAliasOp)) {
               result.append("Let ");
               result.append(left);
               result.append(" alias ");
               result.append(left.right);
            }
            else if (ov.eq(OpVal.stopStmtOp)) {
               appendIndent(result);
               result.append("Stop");
            }
            else if (ov.eq(OpVal.returnStmtOp)) {
               result.append("Return ");
               result.append(left);
            }
            else if (ov.eq(OpVal.generatorDefOp)) {
               result.append("Let ");
               result.append(left);
               result.append(left.right);
            }
            else if (ov.eq(OpVal.printOp)) {
               result.append("Print ");
               TreeNode t= (TreeNode) left;
               while (t != null) {
                  GenesisVal gv = t.info.getVal();
                  if (  gv instanceof StringVal 
                     || gv instanceof NumberVal 
                     || (gv instanceof OpVal  
                          && ((OpVal)gv).eq(OpVal.numberOp) 
                              || ((OpVal)gv).eq(OpVal.stringOp) 
                              || ((OpVal)gv).eq(OpVal.trueOp) 
                              || ((OpVal)gv).eq(OpVal.falseOp) 
                              || ((OpVal)gv).eq(OpVal.listOp) 
                              || ((OpVal)gv).eq(OpVal.functionCallOp) 
                         )
                     )
                      result.append(t);
                  else {
                     result.append('(');
                     result.append(t);
                     result.append(')');
                  }
                  result.append(" ");
                  t= (TreeNode) t.right;
               }
            }
            else if (ov.eq(OpVal.echoStmtOp)) {
               result.append("Echo ");
               TreeNode t= (TreeNode) left;
               while (t != null) {
                  GenesisVal gv = t.info.getVal();
                  if (  gv instanceof StringVal 
                     || gv instanceof NumberVal 
                     || (gv instanceof OpVal  
                          && ((OpVal)gv).eq(OpVal.numberOp) 
                              || ((OpVal)gv).eq(OpVal.stringOp) 
                              || ((OpVal)gv).eq(OpVal.trueOp) 
                              || ((OpVal)gv).eq(OpVal.falseOp) 
                              || ((OpVal)gv).eq(OpVal.listOp) 
                         )
                     )
                      result.append(t);
                  else {
                     result.append('(');
                     result.append(t);
                     result.append(')');
                  }
                  result.append(" ");
                  t= (TreeNode) t.right;
               }
            }
            else if (ov.eq(OpVal.procedureCallOp)) {
               result.append(left);
               result.append('(');
               result.append(left.right);
               result.append(')');
            }
            else if (ov.eq(OpVal.pipeOp)) {
               result.append(left);
               prettyIndent= prettyIndent+3;
               Node temp = left.right;
               while (temp != null) {
                  result.append(temp);
                  temp = temp.right;
               }
               prettyIndent= prettyIndent-3;
            }
            else if (ov.eq(OpVal.insertBeforeOp)) {
               result.append("Insert ");
               result.append(left);
               result.append(" before ");
               result.append(left.right);
            }
            else if (ov.eq(OpVal.insertAfterOp)) {
               result.append("Insert ");
               result.append(left);
               result.append(" after ");
               result.append(left.right);
            }
            else if (ov.eq(OpVal.deleteOp)) {
               result.append("Delete ");
               result.append(left);
            }
            else if (ov.eq(OpVal.appendOp)) {
               result.append("Append ");
               result.append(left);
               result.append(" onto ");
               result.append(left.right);
            }
            else if (ov.eq(OpVal.prependOp)) {
               result.append("Prepend ");
               result.append(left);
               result.append(" onto ");
               result.append(left.right);
            }
            else if (ov.eq(OpVal.unaliasStmtOp)) {
               result.append("Unalias ");
               result.append(left);
            }
            else if (ov.eq(OpVal.guardedTaskOp)) {
               TreeNode lst = (TreeNode) left.right.left;
               if (lst.left != null || 
                   lst.right.left != null || 
                   lst.right.right.left != null) {
                  result.append("[+] ");
                  result.append(left);
                  result.append("\n");
                  prettyIndent = prettyIndent + 3;
                  result.append(left.right);
                  prettyIndent = prettyIndent - 3;
               }
            }
            else if (ov.eq(OpVal.generatorCallOp)) {
               result.append("Generator");
            }
            else if (ov.eq(OpVal.nullStmtOp)) {
               result.append("");
            }
            else if (ov.eq(OpVal.emitOp)) {
               result.append("Emit not implemented");
            }
            else if (ov.eq(OpVal.nullOp)) {
            }
            result.append("\n");
         } // end of statements
      }
      else if (info.val instanceof StringVal) {
         result.append(info.val);
      }
      else if (info.val instanceof NumberVal) {
         result.append(info.val);
      }
      else if (info.val instanceof TruthVal) {
         result.append(info.val);
      }
      else if (info.val instanceof GenesisList) {
      }
      else {
         //System.out.println("Got to dump");
         Parser.traverse(this);
      }
           //System.out.println("Returning "+ result);
         return result;
   } // end of toStringBuilder

   void numberTree() {
         
      if (info.val instanceof OpVal) {
         OpVal ov = (OpVal) info.val; 
         if (// ov.eq(OpVal.generateStmtOp) 
             //  || ov.eq(OpVal.headerOp) 
             ov.eq(OpVal.pipeOp) 
             //|| ov.eq(OpVal.taskOp) 
             || ov.eq(OpVal.guardedStmtOp) 
             || ov.eq(OpVal.selectOp) 
             //|| ov.eq(OpVal.stmtListOp) 
             || ov.eq(OpVal.taskDefOp) 
             || ov.eq(OpVal.functionDefOp) 
             || ov.eq(OpVal.procedureDefOp) 
             || ov.eq(OpVal.idNameOp) 
             || ov.eq(OpVal.idAliasOp) 
             || ov.eq(OpVal.stopStmtOp) 
             || ov.eq(OpVal.returnStmtOp) 
             || ov.eq(OpVal.generatorDefOp) 
             || ov.eq(OpVal.printOp) 
             || ov.eq(OpVal.echoStmtOp) 
             || ov.eq(OpVal.procedureCallOp) 
             || ov.eq(OpVal.pipeOp) 
             || ov.eq(OpVal.insertBeforeOp) 
             || ov.eq(OpVal.insertAfterOp) 
             || ov.eq(OpVal.deleteOp) 
             || ov.eq(OpVal.appendOp) 
             || ov.eq(OpVal.unaliasStmtOp) 
             //|| ov.eq(OpVal.guardedTaskOp) 
             || ov.eq(OpVal.generatorCallOp) 
             || ov.eq(OpVal.nullStmtOp) 
             || ov.eq(OpVal.whileOp) 
            ) {  // stmt found; increment the global pretty print number
                 // and put that number in the current node
                prettyPrintNo++; prettyNo = prettyPrintNo;
             }
      }
      if (left != null) { 
         ((TreeNode)left).numberTree();
      }
      if (right != null) { 
         ((TreeNode)right).numberTree();
      }

   }
}  // end of class TreeNode
/*
   MetaNode -- enable nodes to be added to data structures that
   will be ignored by GenesisList.  These nodes can be use for 
   adding  meta-information to data structures.
*/

class MetaNode extends TreeNode {
    public MetaNode() {
        info = new StickyNote("MetaNode");
   }
   // Add logic for comparing lists using eq 
   public boolean eq( GenesisVal rhs ) {
      //System.out.println ("eq called on:");
      if  ( ! (rhs instanceof MetaNode) ) {
          return false;
      }
      GenesisList gl1 = new GenesisList(this);
      GenesisList gl2 = new GenesisList((MetaNode)rhs);
      return gl1.equals(gl2);
   } 
}



