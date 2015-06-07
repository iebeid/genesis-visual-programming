/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package runtime;

import eve.CodeVal;
import parsing.InstructionList;

/**
 *
 * @author Larry Morell
 */
public class StickyNote
{
   Value val;

	public StickyNote(InstructionList args) {
		this.val = new CodeVal(args);
	}

	
	public void setVal(Value sn) {
		this.val = val;
	}


  public StickyNote()
  {
    val = null;

  }

  public StickyNote( StickyNote d )
  {
  ///
   val = d.val;
 
  }

  public StickyNote( Value v )
  {
    val = v;
    
  }


  public StickyNote( String s )
  {
    val = new StringVal( s );
    
  }

  public StickyNote( double d )
  {
    val = new DoubleVal( d );
    
  }

 public StickyNote( boolean b )
  {
    val = new TruthVal( b );

  }
/*
  public StickyNote (Environment environment) {
	  val = new EnvironmentVal(environment);

  }
*/
  public Value getVal() { return val;}

	@Override
  public String toString()
  {
    if ( val == null)
       return ("<>");
    if ( val instanceof ListVal )
    {
      // System.out.println("Making Genesis List");
      ListVal L = new ListVal((ListVal)val);
      // System.out.println("Done Making Genesis List");
      return L.toString();
    }
    else {
      return val.toString();
    }
  }

  public boolean equals(StickyNote d) {
     if (val == null) return false;
     // System.out.println ("ComparingStickyNote " + val.getClass() + " with " + d.val.getClass());
     return val.eq(d.val);
  }

  public void display() {System.out.print(this.toString());}
  public void displayln() {System.out.println(this.toString());}
  public StickyNote copy() { return new StickyNote(this);}
  public String addr() {return super.toString();}

  public static void main (String args[]) {

 
}


} // end of class StickyNote

