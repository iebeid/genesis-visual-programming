/*
 This  facilities for manipulating heterogeneous
 unbounded lists.  

 Author: Larry Morell

 Access is two-way relative access.

 Inheritance:
 Envs --> Ports --> Collections --> Linears --> Lists

 Modification history

 Dark ages -  an original version of this was written using Turbo 5.0
 in which the: object-orientation was painfully simulated

 11/91     -  The simulated version was changed to true objects.
 11/21/91  -  DONE was corrected to delete all the nodes of the list
 1/26/93   -  copy updated - parameter is now copied to
 1/28/93   -  left corrected! Didn"t work at head of list
 2/10/93   -  Added pos to give the current position in the list
 2/15/93   -  Added search to provide for keyed access movement
 5/17/93   -  Made del && insert virtual
 8/21/93   -  Updated to include SelfTest && Linears (inheritance)
 11/1/93   -  Corrected sizeOf(); access past } of list
 12/3/93   -  Commented out write to default to Linear.WRITE
 5/27/94   -  changed initcopy to ensure that destination ended up
 with same position as source
 5/31/94   -  Major change ... deleted CURRENT by computing it where
 needed from PREV && HEAD
 2/12/96   -  Major, MAJOR change to the semantics of insert; it now
 moves the point after doing the insert, interpreting
 the point as does emacs
 3/10/04   -  Translated to Java
 7/6/04    -  Simplified off and on
 */
package edu.genesis.runtime;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

class List {

// ---------------- Classwide variables ---------------
    final static boolean DEBUG = false;
    final static boolean TEST = false;
// ---------------- Instance variables -----------------
    ListDesc p;
            PrintStream o = null;


    List() {
        Utility.entering("List");
        init();
        Utility.leaving("List");
    }

    private void init() {
        Utility.entering("init");
        p = new ListDesc();
        p.head = null;
        p.point = null;
        Utility.leaving("init");
                try {
            o = new PrintStream(new FileOutputStream("A.txt",true));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GenesisVal.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.setOut(o);
        System.setErr(o);
    }

    void done() {
        Utility.entering("done");
        p = null;
        Utility.leaving("done");
    }

    void reset() {
        Utility.entering("reset");
        p.point = null;

        Utility.leaving("reset");
    }

    boolean empty() {
        return p.head == null;

    }

    void del() {
        Utility.entering("del");
        Node C;
        StickyNote v;
        if ((p.head == null) || (p.point != null && (p.point.right() == null))) {
            System.out.println("delList: No element at current location");
        } else if (p.point == null) { //deleting first element
            p.head = p.head.right();
            if (p.head != null) {
                p.head.left = null;
            }
        } else { //middle || } of list
            p.point.right = p.point.right().right();  //C = double succ of point;
            if (p.point.right() != null) {
                p.point.right().left = p.point;
            }
        }
        Utility.leaving("del");
    }

    StickyNote get() {
        StickyNote v = new StickyNote();
        Utility.entering("get");
        //if (TEST) verify (validRep(Self),Self, "valid Representation");
        if (p.point == null) {
            if (p.head == null) {
                System.out.println("List.get: Attempt to get non-existent element");
                System.out.println("Got here");
            } else // at head of the list
            {
                v = p.head.info.copy();
            }
        } else if ((p.point.right() == null)) // after end of the list
        {
            System.out.println("List.get: Attempt to get non-existent element");
        } else // on the list 
        {
            v = p.point.right().info.copy();
        }
        Utility.leaving("get");
        return v;
    }

    void insert(StickyNote e) {
        Utility.entering("insert");
        StickyNote v;
        if (p == null) {
            init();
        }
        Node n = new Node();
        n.info = e.copy();
        if (p.point == null) { // adding at the beginning 
            n.right = p.head;
            if (p.head != null) {
                p.head.left = n;
            }
            p.head = n;
            p.point = p.head;
            n.left = null;
        } else { // add after the beginning 
            n.right = p.point.right();
            if (p.point.right() != null) {
                p.point.right().left = n;
            }
            p.point.right = n;
            n.left = p.point;
            p.point = n;  // move p.pointer to new  element 
        }
        Utility.leaving("insert");
    }

    void printNode(Node LN) // print a single list node
    {
        System.out.print("info: ");
        LN.info.display();
        System.out.print("right(): ");
        System.out.println(LN.right());
    }

    void printNodes(Node SN) // print a list of listnodes
    // SN is the node from the source list
    // DN is the node in the destination list
    {
        StickyNote v;
        Utility.entering("printNodes");
        while (SN != null) {
            printNode(SN);
            SN = SN.right();
        }

    } // printNodes
    void move() {
        Utility.entering("move");
        if (p.point == null) {
            p.point = p.head;
        } else if ((p.point != null) && (p.point.right() != null)) {
            p.point = p.point.right();
        }
//  printNodes(p.head);
        Utility.leaving("move");
    }

    boolean off() {
        // return (p.head == null) || 
        //       ((p.point != null) && (p.point.right() == null));
        return (p.point == null) || p.point.right() == null;
    }

    boolean on() {
        // boolean on;
        // Utility.entering("on");
        // on = (p.head != null) &&
        // ((p.point == null) || (p.point.right() != null));
        // Utility.leaving("on");
        return (p.point != null) && p.point.right() != null;
        // return on;
    }

    boolean atFirst() {
        return p.point == null;
    }

    boolean atLast() {
        if (p.head == null) {
            return false;
        }
        if (p.point == null && p.head.right() == null) {
            return true;
        }
        if (p.point != null && p.point.right() != null && p.point.right().right() == null) {
            return true;
        }
        // System.out.println ("ans:" + ans);
        return false;
    }

    void change(StickyNote e) {
        if ((p.head == null) || ((p.point != null) && (p.point.right() == null))) {
            System.out.println("List.change: no element at current location;"
                    + " no change");
        } else {
            p.point.info = e.copy();
        }
    }

    void left() {
        Utility.entering("left");
        if (p.point != null) {
            p.point = p.point.left;
        }
        Utility.leaving("left");
    }
    /*
     boolean sizeOf()
     {  
     Utility.entering("sizeOf");
     Node    SN;
     int    Count;
     StickyNote v;
     Count = sizeof(Self);
     SN = p.head;
     while (SN != null){
     v=SN.info;
     Count = Count +  v.sizeOf();
     SN = SN.right();
     }
     Utility.leavingB("sizeOf",Count);
     return Count;
     }
     */

    String typeName() {
        return "List";
    }

    int size() {
        Node SN;
        int count;
        Utility.entering("size");
        count = 0;
        SN = p.head;
        while (SN != null) {
            SN = SN.right();
            count = count + 1;
        }
        Utility.leaving("size");

        return count;
    }

    int pos() {
        Node t;
        int n;
        if (p.point == null) {
            n = 1;
        } else {
            n = 1;
            t = p.head;
            while (t != p.point) {
                t = t.right();
                n = n + 1;
            }
            n = n + 1;
        }
        //System.out.println("Exiting pos with " + n);
        return n;
    }

    boolean equals(List e) {
        Node SN, AN;
        boolean equal;
// two Lists are equal if they have the same values in their nodes 
        equal = true;
        if (e.getClass() != getClass()) {
            equal = false;
        } else if (e.size() != size()) {
            equal = false;
        } else {
            SN = p.head;
            AN = e.p.head;
            while ((SN != null) && (SN.info.equals(AN.info))) {
                SN = SN.right();
                AN = AN.right();
            }
            equal = SN == null;
        }
        return equal;
    }

    StickyNote search(StickyNote A) {
        StickyNote B = null;
        reset();
        if (on()) {
            B = get();
            while (!B.equals(A)) {
                move();
                B = get();
            }
        }
        return B;
    }

    @Override
    public String toString() {

        Utility.entering("Linear::toString");
        final String Openchar = "<";
        final String Closechar = ">";
        String result = Openchar;
        int position = pos();
        int i;
        StickyNote a;
        reset();
        i = 1;
        while (on()) {
            a = get();
            if (i == position) // at current
            {
                result = result + '*';
            }
            result = result + a.toString();
            //System.out.println("toString:atLast" + atLast());
            if (!atLast()) {
                result = result + " ";
            }
            i = i + 1;
            move();
        }

        if (i == position)// at current
        {
            result = result + "*";
        }
        reset();
        result = result + Closechar;
        while (position > 1) {
            move();
            position = position - 1;
        }
        Utility.leaving("Linear::toString");
        return result;
    }

    public void display() {
        System.out.print(this);
    }

    public void displayln() {
        System.out.println(this);
    }
// ------------ Test procedures ----------- 
    void ListsTest() {
        System.out.println("Testing %s for satisfaction of LIST rules\n"
                + typeName());
        System.out.print("List value:");
        System.out.println("End of LIST tests for " + typeName() + '\n');
    }

    @SuppressWarnings("empty-statement")
    static void listsSelfTest() {
        List l = new List();;
        StickyNote IO = new StickyNote();;

        l.init();
        IO = new StickyNote(5);
        l.insert(IO);
        l.ListsTest();
        IO = new StickyNote(20);
        l.insert(IO);
        l.insert(IO);
        l.ListsTest();
    }

    boolean noLoops() {
        Node ptr;
        int i;
        ptr = p.head;
        i = 0;
        while (ptr != null) {
            ptr = ptr.right();
            i = i + 1;
            if (i > 8000) {
                return false;
            }
        }
        return true;
    }

    // valid - true iff  the representation of the value
    // satisfies obvious properties
    boolean valid() {
        boolean VR;
        VR = true;
//  with (A.P)^)
        if (p.head == null) {
            VR = (p.point == null);
            if (!VR) {
                System.out.println("Violate 1");
            }
        } else if (p.point == null) {
            VR = noLoops();
            if (!VR) {
                System.out.println("Violate 2");
            }
        } else {
            VR = noLoops();
            if (!VR) {
                System.out.println("Violate 3");
            }
        }
        return VR;
    }
}
