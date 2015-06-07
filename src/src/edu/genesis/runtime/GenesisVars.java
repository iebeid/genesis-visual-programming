// GenesisVars.java
// This file contains classes for implementing variables in Genesis.
// Author: Wes Potts
/* Modification history
 3/10/04 -- original version (wdp)
 4/04    -- ljm: added OpVal to hold operators for trees 
 7/5/04  -- ljm: added IntVal 
 7/13/04 -- ljm: stratified GenesisVal by keeping only the eq operator
 and creating an additonal class called GV that requires
 the implementation of the rest of the comparison ops
 3/19/05 -- ljm: added a class for boolean values called TruthVal
 9/21/05 -- ljm: corrected TruthVal < and >= operators
 8/19/09 -- ljm: modified DoubleVal.toString to print integer results if int
 1/18/10 -- ljm: StickyNote::equals always returned 0
 4/21/10 -- ljm: StickyNote::equals died when its val was null
 */
package edu.genesis.runtime;

import java.text.DecimalFormat;
import java.util.Objects;

class StickyNote {

    GenesisVal val;
    GenesisVal label;

    StickyNote() {
        val = null;
        label = null;
    }

    StickyNote(StickyNote d) {
        ///
        val = d.val;
        label = d.label;
    }

    StickyNote(GenesisVal v) {
        val = v;
        label = null;
    }

    StickyNote(String s) {
        val = new StringVal(s);
        label = null;
    }

    StickyNote(double d) {
        val = new DoubleVal(d);
        label = null;
    }

    StickyNote(boolean b) {
        val = new TruthVal(b);
        label = null;
    }

    GenesisVal getVal() {
        return val;
    }

    @Override
    public String toString() {
        if (val == null) {
            return ("<>");
        }
        if (val instanceof Node) {
            // System.out.println("Making Genesis List");
            GenesisList L = new GenesisList((Node) val);
            // System.out.println("Done Making Genesis List");
            return L.toString();
        } else {
            return val.toString();
        }
    }

    boolean equals(StickyNote d) {
        if (val == null) {
            return false;
        }
        // System.out.println ("ComparingSN " + val.getClass() + " with " + d.val.getClass());
        return val.eq(d.val);
    }

    void display() {
        System.out.print(this.toString());
    }

    void displayln() {
        System.out.println(this.toString());
    }

    StickyNote copy() {
        return new StickyNote(this);
    }

    String addr() {
        return super.toString();
    }

    GenesisVal getLabel() {
        return label;
    }

    void setLabel(GenesisVal label) {
        this.label = label;
    }
} // end of class StickyNote

abstract class GenesisVal {

    public void printBadComparison(GenesisVal v1, GenesisVal v2) {
        System.err.println("Cannot compare " + v1 + " with " + v2);
        System.exit(1);
    }

    public void checkBadComparison(GenesisVal v1, GenesisVal v2) {
        if (v1.getClass() != v2.getClass()) {
            System.err.print("Cannot compare '" + v1 + "' with ");
            if (v2 instanceof Node) {
                System.err.println("a List");
            } else {
                System.err.println(v2);
            }
            System.exit(1);
        }
    }

    public abstract boolean eq(GenesisVal rhs);

    String addr() {
        return super.toString();
    }
}

abstract class GV extends GenesisVal {

    public abstract boolean lt(GenesisVal rhs);

    public abstract boolean le(GenesisVal rhs);

    public abstract boolean gt(GenesisVal rhs);

    public abstract boolean ge(GenesisVal rhs);

    public abstract boolean ne(GenesisVal rhs);

    public abstract GV add(GV rhs);
    
    public abstract String toXML();
}

class StringVal extends GV {

    protected String val;

    StringVal(String s) {
        val = s;
    }

    @Override
    public String toString() {
        //System.out.println ("Returning: " + val);
        return val;
    }

    @Override
    public boolean lt(GenesisVal rhs) {
        checkBadComparison(this, rhs);
        StringVal s;
        if (rhs instanceof StringVal) {
            s = (StringVal) rhs;
        } else {
            s = new StringVal("");
        }
        return val.compareTo(s.val) < 0;
    }

    @Override
    public boolean le(GenesisVal rhs) {
        checkBadComparison(this, rhs);
        StringVal s;
        if (rhs instanceof StringVal) {
            s = (StringVal) rhs;
        } else {
            s = new StringVal("");
        }
        return val.compareTo(s.val) <= 0;
    }

    @Override
    public boolean gt(GenesisVal rhs) {
        checkBadComparison(this, rhs);
        StringVal s;
        if (rhs instanceof StringVal) {
            s = (StringVal) rhs;
        } else {
            s = new StringVal("");
        }
        return val.compareTo(s.val) > 0;
    }

    @Override
    public boolean ge(GenesisVal rhs) {
        checkBadComparison(this, rhs);
        StringVal s;
        if (rhs instanceof StringVal) {
            s = (StringVal) rhs;
        } else {
            s = new StringVal("");
        }
        return val.compareTo(s.val) >= 0;
    }

    @Override
    public boolean equals(Object rhs) {
        StringVal s;
        if (rhs instanceof StringVal) {
            s = (StringVal) rhs;
        } else {
            s = new StringVal("");
        }
        return val.compareTo(s.val) == 0;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.val);
        return hash;
    }

    @Override
    public boolean eq(GenesisVal rhs) {
        StringVal s;
        if (rhs instanceof StringVal) {
            s = (StringVal) rhs;
        } else {
            s = new StringVal("");
        }
        return val.compareTo(s.val) == 0;
    }

    @Override
    public boolean ne(GenesisVal rhs) {
        StringVal s;
        if (rhs instanceof StringVal) {
            s = (StringVal) rhs;
        } else {
            s = new StringVal("");
        }
        return val.compareTo(s.val) != 0;
    }

    public int compareTo(Object o) {
        StringVal s;
        if (o instanceof StringVal) {
            s = (StringVal) o;
        } else {
            return 0;
        }
        return val.compareTo(s.val);
    }

    @Override
    public GV add(GV rhs) {
        if (rhs instanceof DoubleVal) {
            return new StringVal(val + rhs);
        } else if (rhs instanceof StringVal) {
            return new StringVal(val + (StringVal) rhs);
        } else if (rhs instanceof GenesisList) {
            GenesisList gl = ((GenesisList) rhs).copy();
            System.out.println("GL = " + gl);
            gl.reset();
            gl.insert(new StickyNote(this));
            System.out.println("GL = " + gl);
            return gl;
        } else {
            return null;
        }
    }

    @Override
    public String toXML() {
        return "<StringValue>"+val+"</StringValue>";
    }
}  // end class StringVal

abstract class NumberVal extends GV {

    public void setPrecision() {
    }

    ;
  public abstract NumberVal sub(NumberVal rhs);

    public abstract NumberVal mul(NumberVal rhs);

    public abstract NumberVal div(NumberVal rhs);

    public abstract NumberVal mod(NumberVal rhs);

    @Override
    public abstract boolean lt(GenesisVal rhs);

    @Override
    public abstract boolean le(GenesisVal rhs);

    @Override
    public abstract boolean gt(GenesisVal rhs);

    @Override
    public abstract boolean ge(GenesisVal rhs);

    @Override
    public abstract boolean eq(GenesisVal rhs);

    @Override
    public abstract boolean ne(GenesisVal rhs);
}  // end class NumberVal

class DoubleVal extends NumberVal {

    protected double val;
    protected static int precision = -1;  // -1 means default precision of Java

    DoubleVal(double d) {
        val = d;
    }

    public double getVal() {
        return val;
    }

    @Override
    public String toString() // using precision set by user
    {
        StringBuffer result = new StringBuffer(100);
        if (precision >= 0) {
            if (precision > 40) {
                precision = 40;  // Limit precision to 40
            }
            StringBuffer format = new StringBuffer(precision + 2); // two extra for the 0.
            // Patch to fix the differences between the format interpretation on boole vs my machine
            format.append('0');
            if (precision > 0) {
                format.append('.');
            }

            for (int i = 0; i < precision; i++) {
                format.append('0');
            }
            DecimalFormat df = new DecimalFormat(new String(format));
            String v = df.format(val);
            result = result.append(v);
        } else if ((int) val == val) {
            int n = (int) val;
            result = result.append(String.valueOf(n));
        } else {
            result = result.append(String.valueOf(val));
        }
        return new String(result);
    }

    public double toDouble() {
        return val;
    }

    public int toInt() {
        return (int) val;
    }

    public NumberVal add(NumberVal rhs) {
        return new DoubleVal(val + ((DoubleVal) rhs).getVal());
    }

    @Override
    public GV add(GV rhs) {
        if (rhs instanceof DoubleVal) {
            return new DoubleVal(val + ((DoubleVal) rhs).getVal());
        } else {
            return null;
        }
    }

    @Override
    public NumberVal sub(NumberVal rhs) {
        return new DoubleVal(val - ((DoubleVal) rhs).getVal());
    }

    @Override
    public NumberVal mul(NumberVal rhs) {
        return new DoubleVal(val * ((DoubleVal) rhs).getVal());
    }

    @Override
    public NumberVal div(NumberVal rhs) {
        return new DoubleVal(val / ((DoubleVal) rhs).getVal());
    }

    @Override
    public NumberVal mod(NumberVal rhs) {
        return new DoubleVal(val % ((DoubleVal) rhs).getVal());
    }

    @Override
    public boolean lt(GenesisVal rhs) {

        DoubleVal d;

        //System.out.println("Returning doubleval: " + val + " < " + rhs.getClass());
        checkBadComparison(this, rhs);
        d = (DoubleVal) rhs;
        return val < d.val;
    }

    @Override
    public boolean le(GenesisVal rhs) {
        DoubleVal d;
        checkBadComparison(this, rhs);
        if (rhs instanceof DoubleVal) {
            d = (DoubleVal) rhs;
        } else {
            d = new DoubleVal(0);
        }
        //System.out.println("Returning doubleval: " + val + " <= " + d.val + "=" + (val <= d.val));
        return val <= d.val;
    }

    @Override
    public boolean gt(GenesisVal rhs) {
        checkBadComparison(this, rhs);
        DoubleVal d;
        if (rhs instanceof DoubleVal) {
            d = (DoubleVal) rhs;
        } else {
            d = new DoubleVal(0);
        }
        //System.out.println("Returning doubleval: " + val + " > " + d.val + "=" + (val > d.val));
        return val > d.val;
    }

    @Override
    public boolean ge(GenesisVal rhs) {
        checkBadComparison(this, rhs);
        DoubleVal d;
        if (rhs instanceof DoubleVal) {
            d = (DoubleVal) rhs;
        } else {
            d = new DoubleVal(0);
        }
        //System.out.println("Returning doubleval: " + val + " >= " + d.val + "=" + (val >= d.val));
        return val >= d.val;
    }

    @Override
    public boolean eq(GenesisVal rhs) {
        DoubleVal d;
        if (rhs instanceof DoubleVal) {
            d = (DoubleVal) rhs;
        } else {
            d = new DoubleVal(0);
        }
        //System.out.println("Returning doubleval: " + val + "==" + d.val + "=" + (val == d.val));
        return val == d.val;
    }

    @Override
    public boolean ne(GenesisVal rhs) {
        DoubleVal d;
        if (rhs instanceof DoubleVal) {
            d = (DoubleVal) rhs;
        } else {
            d = new DoubleVal(0);
        }
        //System.out.println("Returning doubleval: " + val + "!= " + d.val + "!=" + (val != d.val));
        return val != d.val;
    }

    @Override
    public String toXML() {
        return "<DoubleValue>"+val+"</DoubleValue>";
    }
}  // end class DoubleVal

class OpVal extends GenesisVal {

    public static String opStr[] = new String[100];
    public static int prec[] = new int[100];
    private static int opStrCnt = 0;
    public static final OpVal stmtListOp = new OpVal(opStrCnt++);
    public static final OpVal taskDefOp = new OpVal(opStrCnt++);
    public static final OpVal functionDefOp = new OpVal(opStrCnt++);
    public static final OpVal procedureDefOp = new OpVal(opStrCnt++);
    public static final OpVal idNameOp = new OpVal(opStrCnt++);
    public static final OpVal idAliasOp = new OpVal(opStrCnt++);
    public static final OpVal generatorDefOp = new OpVal(opStrCnt++);
    public static final OpVal emitOp = new OpVal(opStrCnt++);// not used
    public static final OpVal modOp = new OpVal(opStrCnt++);    //chad add in for %
    public static final OpVal printOp = new OpVal(opStrCnt++);
    public static final OpVal procedureCallOp = new OpVal(opStrCnt++);
    public static final OpVal pipeOp = new OpVal(opStrCnt++);
    public static final OpVal addOp = new OpVal(opStrCnt++);
    public static final OpVal dotOp = new OpVal(opStrCnt++);    //chad added for records
    public static final OpVal subtractOp = new OpVal(opStrCnt++);
    public static final OpVal multiplyOp = new OpVal(opStrCnt++);
    public static final OpVal divideOp = new OpVal(opStrCnt++);
    public static final OpVal nullOp = new OpVal(opStrCnt++);
    public static final OpVal whileOp = new OpVal(opStrCnt++);
    public static final OpVal selectOp = new OpVal(opStrCnt++);
    public static final OpVal generateStmtOp = new OpVal(opStrCnt++);
    public static final OpVal generatorRefOp = new OpVal(opStrCnt++);
    public static final OpVal guardedStmtOp = new OpVal(opStrCnt++);
    public static final OpVal returnStmtOp = new OpVal(opStrCnt++);
    public static final OpVal stopStmtOp = new OpVal(opStrCnt++);
    public static final OpVal echoStmtOp = new OpVal(opStrCnt++);
    public static final OpVal parameterOp = new OpVal(opStrCnt++);
    public static final OpVal fileOp = new OpVal(opStrCnt++);
    public static final OpVal functionCallOp = new OpVal(opStrCnt++);
    public static final OpVal blockOp = new OpVal(opStrCnt++);// not used
    public static final OpVal idListOp = new OpVal(opStrCnt++);
    public static final OpVal listOp = new OpVal(opStrCnt++);
    public static final OpVal taskOp = new OpVal(opStrCnt++);
    public static final OpVal subscriptOp = new OpVal(opStrCnt++);
    public static final OpVal orOp = new OpVal(opStrCnt++);
    public static final OpVal andOp = new OpVal(opStrCnt++);
    public static final OpVal notOp = new OpVal(opStrCnt++);
    public static final OpVal numberOp = new OpVal(opStrCnt++);
    public static final OpVal stringOp = new OpVal(opStrCnt++);
    public static final OpVal gtOp = new OpVal(opStrCnt++);
    public static final OpVal ltOp = new OpVal(opStrCnt++);
    public static final OpVal geOp = new OpVal(opStrCnt++);
    public static final OpVal leOp = new OpVal(opStrCnt++);
    public static final OpVal eqOp = new OpVal(opStrCnt++);
    public static final OpVal neOp = new OpVal(opStrCnt++);
    public static final OpVal aliasOp = new OpVal(opStrCnt++);
    public static final OpVal debugOp = new OpVal(opStrCnt++);
    public static final OpVal formalOp = new OpVal(opStrCnt++); // formal params -- not used
    public static final OpVal trueOp = new OpVal(opStrCnt++);
    public static final OpVal falseOp = new OpVal(opStrCnt++);
    public static final OpVal otherwiseOp = new OpVal(opStrCnt++);
    public static final OpVal headerOp = new OpVal(opStrCnt++);
    public static final OpVal rangeNxNxN_Op = new OpVal(opStrCnt++);
    public static final OpVal rangeNxN_Op = new OpVal(opStrCnt++);
    public static final OpVal rangeSxS_Op = new OpVal(opStrCnt++);
    public static final OpVal rangeS_Op = new OpVal(opStrCnt++);
    public static final OpVal unaryMinusOp = new OpVal(opStrCnt++);
    public static final OpVal insertBeforeOp = new OpVal(opStrCnt++);
    public static final OpVal insertAfterOp = new OpVal(opStrCnt++);
    public static final OpVal deleteOp = new OpVal(opStrCnt++);
    public static final OpVal appendOp = new OpVal(opStrCnt++);
    public static final OpVal unaliasStmtOp = new OpVal(opStrCnt++);
    public static final OpVal condOp = new OpVal(opStrCnt++);
    public static final OpVal guardedTaskOp = new OpVal(opStrCnt++);
    public static final OpVal generatorCallOp = new OpVal(opStrCnt++);
    public static final OpVal nullStmtOp = new OpVal(opStrCnt++);
    public static final OpVal generatorOp = new OpVal(opStrCnt++);
    public static final OpVal iteratorOp = new OpVal(opStrCnt++);
    public static final OpVal prependOp = new OpVal(opStrCnt++);
    public static final OpVal colonOp = new OpVal(opStrCnt++);
    public static final OpVal downto_rangeNxNxN_Op = new OpVal(opStrCnt++);
    public static final OpVal downto_rangeNxN_Op = new OpVal(opStrCnt++);
    public static final OpVal downto_rangeSxS_Op = new OpVal(opStrCnt++);
    public static final OpVal downto_rangeS_Op = new OpVal(opStrCnt++);
    public static int highest = 6;

    public static int highestPrec() {
        return highest;
    }

    public static void init() {
        opStr[stmtListOp.getVal()] = "stmtListOp";
        opStr[functionDefOp.getVal()] = "functionDefOp";
        opStr[procedureDefOp.getVal()] = "procedureDefOp";
        opStr[taskDefOp.getVal()] = "taskDefOp";
        opStr[idNameOp.getVal()] = "idNameOp";
        opStr[idAliasOp.getVal()] = "idAliasOp";
        opStr[generatorDefOp.getVal()] = "generatorDefOp";
        opStr[emitOp.getVal()] = "emitOp";// not used
        opStr[modOp.getVal()] = "modOp";               // chad add in for %
        opStr[printOp.getVal()] = "printOp";
        opStr[dotOp.getVal()] = "dotOp";               // chad added for record
        opStr[procedureCallOp.getVal()] = "procedureCallOp";
        opStr[pipeOp.getVal()] = "pipeOp";
        opStr[addOp.getVal()] = "addOp";
        opStr[subtractOp.getVal()] = "subtractOp";
        opStr[multiplyOp.getVal()] = "multiplyOp";
        opStr[divideOp.getVal()] = "divideOp";
        opStr[nullOp.getVal()] = "nullOp";
        opStr[whileOp.getVal()] = "whileOp";
        opStr[selectOp.getVal()] = "selectOp";
        opStr[generateStmtOp.getVal()] = "generateStmtOp";
        opStr[generatorRefOp.getVal()] = "generatorRefOp";
        opStr[guardedStmtOp.getVal()] = "guardedStmtOp";
        opStr[returnStmtOp.getVal()] = "returnStmtOp";
        opStr[stopStmtOp.getVal()] = "stopStmtOp";
        opStr[echoStmtOp.getVal()] = "echoStmtOp";
        opStr[parameterOp.getVal()] = "parameterOp";
        opStr[fileOp.getVal()] = "fileOp";
        opStr[functionCallOp.getVal()] = "functionCallOp";
        opStr[blockOp.getVal()] = "blockOp";// not used
        opStr[idListOp.getVal()] = "idListOp"; // not used
        opStr[listOp.getVal()] = "listOp";
        opStr[taskOp.getVal()] = "taskOp";
        opStr[subscriptOp.getVal()] = "subscriptOp";
        opStr[orOp.getVal()] = "orOp";
        opStr[andOp.getVal()] = "andOp";
        opStr[notOp.getVal()] = "notOp";
        opStr[stringOp.getVal()] = "stringOp";
        opStr[numberOp.getVal()] = "numberOp";
        opStr[ltOp.getVal()] = "ltOp";
        opStr[gtOp.getVal()] = "gtOp";
        opStr[leOp.getVal()] = "leOp";
        opStr[geOp.getVal()] = "geOp";
        opStr[eqOp.getVal()] = "eqOp";
        opStr[neOp.getVal()] = "neOp";
        opStr[aliasOp.getVal()] = "aliasOp";
        opStr[iteratorOp.getVal()] = "iteratorOp";
        opStr[debugOp.getVal()] = "debugOp";
        opStr[formalOp.getVal()] = "formalOp"; // not used
        opStr[trueOp.getVal()] = "trueOp";
        opStr[falseOp.getVal()] = "falseOp";
        opStr[otherwiseOp.getVal()] = "otherwiseOp";
        opStr[headerOp.getVal()] = "headerOp";
        opStr[rangeNxNxN_Op.getVal()] = "rangeNxNxN_Op";
        opStr[rangeNxN_Op.getVal()] = "rangeNxN_Op";
        opStr[rangeSxS_Op.getVal()] = "rangeSxS_Op";
        opStr[rangeS_Op.getVal()] = "rangeS_Op";
        opStr[unaryMinusOp.getVal()] = "unaryMinusOp";
        opStr[insertBeforeOp.getVal()] = "insertBeforeOp";
        opStr[insertAfterOp.getVal()] = "insertAfterOp";
        opStr[deleteOp.getVal()] = "deleteOp";
        opStr[appendOp.getVal()] = "appendOp";
        opStr[unaliasStmtOp.getVal()] = "unaliasStmtOp";
        opStr[condOp.getVal()] = "condOp";
        opStr[guardedTaskOp.getVal()] = "guardedTaskOp";
        opStr[generatorCallOp.getVal()] = "generatorCallOp";
        opStr[nullStmtOp.getVal()] = "nullStmtOp";
        opStr[generatorOp.getVal()] = "generatorOp";
        opStr[iteratorOp.getVal()] = "iteratorOp";
        opStr[colonOp.getVal()] = "colonOp";
        opStr[downto_rangeNxNxN_Op.getVal()] = "downto_rangeNxNxN_Op";
        opStr[downto_rangeNxN_Op.getVal()] = "downto_rangeNxN_Op";
        opStr[downto_rangeSxS_Op.getVal()] = "downto_rangeSxS_Op";
        opStr[downto_rangeS_Op.getVal()] = "downto_rangeS_Op";
        // Set precedence
        prec[modOp.getVal()] = highest - 1;
        prec[dotOp.getVal()] = highest;
        prec[addOp.getVal()] = highest - 2;
        prec[subtractOp.getVal()] = highest - 2;
        prec[multiplyOp.getVal()] = highest - 1;
        prec[divideOp.getVal()] = highest - 1;
        prec[functionCallOp.getVal()] = highest;
        prec[subscriptOp.getVal()] = highest;
        prec[orOp.getVal()] = highest - 6;
        prec[andOp.getVal()] = highest - 5;
        prec[notOp.getVal()] = highest - 4;
        prec[stringOp.getVal()] = highest;
        prec[numberOp.getVal()] = highest;
        prec[ltOp.getVal()] = highest - 3;
        prec[gtOp.getVal()] = highest - 3;
        prec[leOp.getVal()] = highest - 3;
        prec[geOp.getVal()] = highest - 3;
        prec[eqOp.getVal()] = highest - 3;
        prec[neOp.getVal()] = highest - 3;
        prec[trueOp.getVal()] = highest;
        prec[falseOp.getVal()] = highest;
        prec[unaryMinusOp.getVal()] = highest;
        prec[colonOp.getVal()] = highest;  // need to think about this
    }
    protected int val;

    OpVal(int d) {
        val = d;
    }

    OpVal(OpVal d) {
        val = d.val;
    }

    public int getVal() {
        return val;
    }

    @Override
    public String toString() {
        //return String.valueOf( val ) ;
        return opStr[val];
    }

    public int toDouble() {
        return val;
    }

    public int toInt() {
        return (int) val;
    }

    @Override
    public boolean eq(GenesisVal rhs) {
        OpVal d;
        if (rhs instanceof OpVal) {
            d = (OpVal) rhs;
        } else {
            d = new OpVal(0);
        }
        return val == d.val;
    }

    /*
     public boolean lt( GenesisVal rhs )
     {
     OpVal d;
     if ( rhs instanceof OpVal ) d = (OpVal)rhs;
     else d = new OpVal( 0 );
     return val < d.val;
     }

     public boolean le( GenesisVal rhs )
     {
     OpVal d;
     if ( rhs instanceof OpVal ) d = (OpVal)rhs;
     else d = new OpVal( 0 );
     return val <= d.val;
     }

     public boolean gt( GenesisVal rhs )
     {
     OpVal d;
     if ( rhs instanceof OpVal ) d = (OpVal)rhs;
     else d = new OpVal( 0 );
     return val > d.val;
     }

     public boolean ge( GenesisVal rhs )
     {
     OpVal d;
     if ( rhs instanceof OpVal ) d = (OpVal)rhs;
     else d = new OpVal( 0 );
     return val >= d.val;
     }

     public boolean ne( GenesisVal rhs )
     {
     OpVal d;
     if ( rhs instanceof OpVal ) d = (OpVal)rhs;
     else d = new OpVal( 0 );
     return val != d.val;
     }
     */
} // End class OpVal

class IntVal extends NumberVal {
    // Static 

    protected int val;

    IntVal(int d) {
        val = d;
    }

    public int getVal() {
        return val;
    }

    @Override
    public String toString() {
        return String.valueOf(val);
    }

    public int toInt() {
        return val;
    }

    public NumberVal add(NumberVal rhs) {
        return new IntVal(val + ((IntVal) rhs).getVal());
    }

    @Override
    public GV add(GV rhs) {
        return new IntVal(val + ((IntVal) rhs).getVal());
    }

    @Override
    public NumberVal sub(NumberVal rhs) {
        return new IntVal(val - ((IntVal) rhs).getVal());
    }

    @Override
    public NumberVal mul(NumberVal rhs) {
        return new IntVal(val * ((IntVal) rhs).getVal());
    }

    @Override
    public NumberVal div(NumberVal rhs) {
        return new IntVal(val / ((IntVal) rhs).getVal());
    }

    @Override
    public NumberVal mod(NumberVal rhs) {
        return new IntVal(val % ((IntVal) rhs).getVal());
    }

    @Override
    public boolean lt(GenesisVal rhs) {
        IntVal d;
        checkBadComparison(this, rhs);
        if (rhs instanceof IntVal) {
            d = (IntVal) rhs;
        } else {
            d = new IntVal(0);
        }
        //System.out.println("Returning intval: " + val + " < " + d.val + "=" + (val < d.val));
        return val < d.val;
    }

    @Override
    public boolean le(GenesisVal rhs) {
        IntVal d;
        checkBadComparison(this, rhs);
        if (rhs instanceof IntVal) {
            d = (IntVal) rhs;
        } else {
            d = new IntVal(0);
        }
        //System.out.println("Returning intval: " + val + " <= " + d.val + "=" + (val <= d.val));
        return val <= d.val;
    }

    @Override
    public boolean gt(GenesisVal rhs) {
        IntVal d;
        checkBadComparison(this, rhs);
        if (rhs instanceof IntVal) {
            d = (IntVal) rhs;
        } else {
            d = new IntVal(0);
        }
        //System.out.println("Returning intval: " + val + " > " + d.val + "=" + (val > d.val));
        return val > d.val;
    }

    @Override
    public boolean ge(GenesisVal rhs) {
        IntVal d;
        checkBadComparison(this, rhs);
        if (rhs instanceof IntVal) {
            d = (IntVal) rhs;
        } else {
            d = new IntVal(0);
        }
        //System.out.println("Returning intval: " + val + " >= " + d.val + "=" + (val >= d.val));
        return val >= d.val;
    }

    @Override
    public boolean eq(GenesisVal rhs) {
        IntVal d;
        if (rhs instanceof IntVal) {
            d = (IntVal) rhs;
        } else {
            d = new IntVal(0);
        }
        //System.out.println("Returning intval: " + val + "==" + d.val + "=" + (val == d.val));
        return val == d.val;
    }

    @Override
    public boolean ne(GenesisVal rhs) {
        IntVal d;
        if (rhs instanceof IntVal) {
            d = (IntVal) rhs;
        } else {
            d = new IntVal(0);
        }
        //System.out.println("Returning intval: " + val + "!= " + d.val + "!=" + (val != d.val));
        return val != d.val;
    }

    @Override
    public String toXML() {
        return "<IntValue>"+val+"</IntValue>";
    }
} // End class IntVal
class TruthVal extends GV {
    // Static 

    // Constructors
    protected boolean val;

    TruthVal() {
        val = false;
    }

    TruthVal(TruthVal tv) {
        val = tv.val;
    }

    TruthVal(boolean b) {
        val = b;
    }

    @Override
    public String toString() {
        return String.valueOf(val);
    }

    @Override
    public boolean lt(GenesisVal rhs) {
        checkBadComparison(this, rhs);
        boolean ans = false;
        if (!(rhs instanceof TruthVal)) {
            printBadComparison(this, rhs);
        } else {
            ans = val == false && ((TruthVal) rhs).val == true;
        }
        return ans;
    }

    @Override
    public boolean eq(GenesisVal rhs) {
        boolean ans = false;
        if (!(rhs instanceof TruthVal)) {
            ans = false;
        } else {
            ans = val == ((TruthVal) rhs).val;
        }
        return ans;

    }

    ;

    @Override
    public boolean le(GenesisVal rhs) {
        checkBadComparison(this, rhs);
        boolean ans = false;
        if (!(rhs instanceof TruthVal)) {
            printBadComparison(this, rhs);
        } else {
            ans = !(val == true && ((TruthVal) rhs).val == false);
        }
        return ans;
    }

    ;

    @Override
    public boolean gt(GenesisVal rhs) {
        checkBadComparison(this, rhs);
        boolean ans = false;
        if (!(rhs instanceof TruthVal)) {
            printBadComparison(this, rhs);
        } else {
            ans = (val == true && ((TruthVal) rhs).val == false);
        }
        return ans;
    }

    ;

    @Override
    public boolean ge(GenesisVal rhs) {
        checkBadComparison(this, rhs);
        boolean ans = false;
        if (!(rhs instanceof TruthVal)) {
            printBadComparison(this, rhs);
        } else {
            ans = !(val == false && ((TruthVal) rhs).val == true);
        }
        return ans;
    }

    ;

    @Override
    public boolean ne(GenesisVal rhs) {
        boolean ans = false;
        if (!(rhs instanceof TruthVal)) {
            ans = false;
        } else {
            ans = val != ((TruthVal) rhs).val;
        }
        return ans;
    }

    ;

    @Override
    public GV add(GV rhs) {
        System.err.println("Warning: cannot add " + this + " and " + rhs);
        System.exit(1);
        return (new IntVal(0));
    }

    @Override
    public String toXML() {
        return "<TruthValue>"+val+"</TruthValue>";
    }
}
