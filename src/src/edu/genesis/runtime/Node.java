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
package edu.genesis.runtime;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

class Node extends GV {

    public StickyNote info;
    public Node left;
    public Node right;
    PrintStream o;
//    public StickyNote label;  // Changed by LJM to record label

    // Constructors
    public Node() {
        info = null;
        left = null;
        right = null;
        //label = null;                    //added by chad for record label
        
                try {
            o = new PrintStream(new File("A.txt"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GenesisVal.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.setOut(o);
        System.setErr(o);
    }

    public Node(StickyNote tok, Node p, Node n) {
        info = tok;
        left = p;
        right = n;
        //label = null;                    //added by chad for record label
                        try {
            o = new PrintStream(new File("A.txt"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GenesisVal.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.setOut(o);
        System.setErr(o);
    }

    public Node(double d) {
        info = new StickyNote(new DoubleVal(d));
        left = null;
        right = null;
        //label = null;                    //added by chad for record label
                        try {
            o = new PrintStream(new File("A.txt"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GenesisVal.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.setOut(o);
        System.setErr(o);
    }

    public Node(double d, Node p, Node n) {
        info = new StickyNote(new DoubleVal(d));
        left = p;
        right = n;
        //label = null;                    //added by chad for record label
                        try {
            o = new PrintStream(new File("A.txt"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GenesisVal.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.setOut(o);
        System.setErr(o);
    }

    public Node(String s, Node p, Node n) {
        info = new StickyNote(s);
        left = p;
        right = n;
        //label = null;                    //added by chad for record label
                        try {
            o = new PrintStream(new File("A.txt"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GenesisVal.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.setOut(o);
        System.setErr(o);
    }

    public Node(StickyNote tok) {
        info = tok;
        left = null;
        right = null;
        //label = null;                    //added by chad for record label
                        try {
            o = new PrintStream(new File("A.txt"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GenesisVal.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.setOut(o);
        System.setErr(o);
    }

    public Node(Node n) {
        info = n.info;
        left = n.left;
        right = n.right;
        //label = null;                    //added by chad for record label
                        try {
            o = new PrintStream(new File("A.txt"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GenesisVal.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.setOut(o);
        System.setErr(o);
    }

    public Node(String s) {
        info = new StickyNote(s);
        left = null;
        right = null;
        //label = null;                    //added by chad for record label
                        try {
            o = new PrintStream(new File("A.txt"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GenesisVal.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.setOut(o);
        System.setErr(o);
    }

    public Node(OpVal ov) {
        info = new StickyNote(new OpVal(ov));
        left = null;
        right = null;
        //label = null;                    //added by chad for record label
                        try {
            o = new PrintStream(new File("A.txt"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GenesisVal.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.setOut(o);
        System.setErr(o);
    }

    public Node(OpVal ov, Node l, Node r) {
        info = new StickyNote(new OpVal(ov));
        left = l;
        right = r;
        //label = null;                    //added by chad for record label
                        try {
            o = new PrintStream(new File("A.txt"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GenesisVal.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.setOut(o);
        System.setErr(o);
    }

    public Node next() {
        return right;
    }

    public Node prev() {
        return left;
    }

    public Node right() {
        return right;
    }

    public Node left() {
        return left;
    }

    public StickyNote getVal() {
        return info;
    }

    @Override
    public boolean eq(GenesisVal rhs) // eq iff the same node
    {
        System.out.println("Comparing eq here " + this.getClass() + " " + rhs.getClass());
        return this.equals(rhs);
    }

    @Override
    public boolean lt(GenesisVal rhs) {
        Evaluator.printError("Cannot compare lists with any other value");
        System.exit(1);
        return false;
    }

    @Override
    public boolean le(GenesisVal rhs) {
        Evaluator.printError("Cannot compare lists with any other value");
        return false;
    }

    @Override
    public boolean gt(GenesisVal rhs) {
        Evaluator.printError("Cannot compare lists with any other value");
        System.exit(1);
        return false;
    }

    @Override
    public boolean ge(GenesisVal rhs) {
        Evaluator.printError("Cannot compare lists with any other value");
        System.exit(1);
        return false;
    }

    @Override
    public boolean ne(GenesisVal rhs) {
        return (!eq(rhs));

    }

    @Override
    public GV add(GV rhs) {
        GenesisList gl = new GenesisList(this).copy();
        while (gl.on()) {
            gl.move();
        }
        if (rhs instanceof StringVal) {
            gl.insert(new StickyNote(rhs));
        }
        return null;
    }

    ;

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
        Node firstNode = null;
        if (source.length() != 0) {
            int curIndex = 0;
            Node node = new Node("" + source.charAt(0));
            firstNode = node;

            curIndex++;
            while (curIndex < source.length()) {
                node.right = new Node(source.substring(curIndex, curIndex + 1));
                node = node.right;
                curIndex++;
            }
        }
        return (firstNode);
    }

    @Override
    public String toXML() {
        return "<NodeValue>"+info.getVal()+"</NodeValue>";
    }
}  // end of class Node

