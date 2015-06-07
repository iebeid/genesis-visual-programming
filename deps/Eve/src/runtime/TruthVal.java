/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package runtime;

/**
 *
 * @author Larry Morell
 */
public class TruthVal extends Val
		  implements Compare, Value
{
  // Static

  // Constructors
  protected boolean val;

	public boolean getVal() {
		return val;
	}
  public TruthVal () { val = false;}

  public TruthVal (TruthVal tv) { val = tv.val;}

  public TruthVal (boolean b) { val = b;}
	@Override
  public String toString()
  {
    return String.valueOf( val );
  }

  public boolean lt( Value rhs ) {
    checkBadComparison(this, rhs);
    boolean ans = false;
    if ( ! (rhs instanceof TruthVal) )
       printBadComparison(this, rhs);
    else
       ans = val == false && ((TruthVal) rhs).val == true;
    return ans;
  }
  public boolean eq( Value rhs ) {
    boolean ans = false;
    if ( ! (rhs instanceof TruthVal) )
       ans = false;
    else
       ans = val == ((TruthVal) rhs).val;
    return ans;

  };

  public boolean le( Value rhs ) {
    checkBadComparison(this, rhs);
    boolean ans = false;
    if ( ! (rhs instanceof TruthVal) )
       printBadComparison(this, rhs);
    else
       ans = !(val == true && ((TruthVal) rhs).val == false);
    return ans;
  };

  public boolean gt( Value rhs ) {
    checkBadComparison(this, rhs);
    boolean ans = false;
    if ( ! (rhs instanceof TruthVal) )
       printBadComparison(this, rhs);
    else
       ans = (val == true && ((TruthVal) rhs).val == false);
    return ans;
  };

  public boolean ge( Value rhs ) {
    checkBadComparison(this, rhs);
    boolean ans = false;
    if ( ! (rhs instanceof TruthVal) )
       printBadComparison(this, rhs);
    else
       ans = !(val == false && ((TruthVal) rhs).val == true);
    return ans;
  };

  public boolean ne( Value rhs ) {
    boolean ans = false;
    if ( ! (rhs instanceof TruthVal) )
       ans = false;
    else
       ans = val != ((TruthVal) rhs).val;
    return ans;
  };

  public Compare add( Value rhs ) {
     System.err.println("Warning: cannot add " + this + " and " + rhs );
     System.exit (1);
     return (new IntVal(0));
  }

	@Override
	public Val copy() {
		return new TruthVal(val);
	}
}

