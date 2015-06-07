/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package runtime;



/**
 * Implements the integers for Genesis
 * @author Larry Morell
 */
public class IntVal extends Val implements Number
{
  // Static
  protected int val;

 public  IntVal( int d )
  {
    val = d;
  }
 public IntVal (double d){
	 val = (int)d;
 }
  public int getVal()
  {
    return val;
  }

	@Override
  public String toString()
  {
    return String.valueOf( val );
  }

  public int toInt()
  {
    return val;
  }

  public double toDouble () {
	  return val;
  }

  public DoubleVal add( DoubleVal rhs )
  {
    return new DoubleVal( val + rhs.getVal() );
  }

  public IntVal add( IntVal rhs )
  {
    return new IntVal( val + rhs.getVal() );
  }

  public DoubleVal sub( DoubleVal rhs )
  {
	    return new DoubleVal( val - rhs.getVal());
  }

  public IntVal sub (IntVal rhs){
	    return new IntVal( val - rhs.getVal());
  }

  public DoubleVal mul( DoubleVal rhs )
  {
    return new DoubleVal( val * rhs.getVal() );
  }

  public IntVal mul( IntVal rhs )
  {
    return new IntVal( val * rhs.getVal() );
  }

  public DoubleVal div( DoubleVal rhs )
  {
	    return new DoubleVal( val / rhs.getVal());
  }

  public IntVal div (IntVal rhs){
	    return new IntVal( val / rhs.getVal());
  }

 public IntVal mod (IntVal rhs){
	    return new IntVal( val % rhs.getVal());
  }

 public Value copy() {
	 return(new  IntVal(val));
 }
  public boolean lt( Value rhs )
  {
    IntVal d;
    checkBadComparison(this, rhs);
    if ( rhs instanceof IntVal ) d = (IntVal)rhs;
    else d = new IntVal( 0 );
    //System.out.println("Returning intval: " + val + " < " + d.val + "=" + (val < d.val));
    return val < d.val;
  }

  public boolean le( Value rhs )
  {
    IntVal d;
    checkBadComparison(this, rhs);
    if ( rhs instanceof IntVal ) d = (IntVal)rhs;
    else d = new IntVal( 0 );
    //System.out.println("Returning intval: " + val + " <= " + d.val + "=" + (val <= d.val));
    return val <= d.val;
  }

  public boolean gt( Value rhs )
  {
    IntVal d;
    checkBadComparison(this, rhs);
    if ( rhs instanceof IntVal ) d = (IntVal)rhs;
    else d = new IntVal( 0 );
    //System.out.println("Returning intval: " + val + " > " + d.val + "=" + (val > d.val));
    return val > d.val;
  }

  public boolean ge( Value rhs )
  {
    IntVal d;
    checkBadComparison(this, rhs);
    if ( rhs instanceof IntVal ) d = (IntVal)rhs;
    else d = new IntVal( 0 );
    //System.out.println("Returning intval: " + val + " >= " + d.val + "=" + (val >= d.val));
    return val >= d.val;
  }

	@Override
  public boolean eq( Value rhs )
  {
    IntVal d;
    if ( rhs instanceof IntVal ) d = (IntVal)rhs;
    else d = new IntVal( 0 );
    //System.out.println("Returning intval: " + val + "==" + d.val + "=" + (val == d.val));
    return val == d.val;
  }

  public boolean ne( Value rhs )
  {
    IntVal d;
    if ( rhs instanceof IntVal ) d = (IntVal)rhs;
    else d = new IntVal( 0 );
    //System.out.println("Returning intval: " + val + "!= " + d.val + "!=" + (val != d.val));
    return val != d.val;
  }

	public void setPrecision() {
		throw new UnsupportedOperationException("Not supported yet.");
	}


	public Number add(Value rhs) {
		if (rhs instanceof IntVal) {
			return new IntVal(val + ((IntVal)rhs).val);
		}
		else if (rhs instanceof DoubleVal) {
			return new DoubleVal(val + ((DoubleVal)rhs).val);
		}
		else
    		throw new UnsupportedOperationException("Cannot add an integer and a " + rhs.getClass());
	}

	public Number sub(Value rhs) {
		if (rhs instanceof IntVal) {
			return sub ((IntVal) rhs);
		}
		else if (rhs instanceof DoubleVal) {
			return sub ((DoubleVal) rhs);
		}
		else
    		throw new UnsupportedOperationException("Cannot subtract a " + rhs.getClass()
					  + " from an integer");
	}

	public Number mul(Value rhs) {
		if (rhs instanceof IntVal) {
			return new IntVal(val + ((IntVal)rhs).val);
		}
		else if (rhs instanceof DoubleVal) {
			return new DoubleVal(val * ((DoubleVal)rhs).val);
		}
		else
    		throw new UnsupportedOperationException("Cannot multiply an integer and a " + rhs.getClass());
	}

	public Number div(Value rhs) {
		if (rhs instanceof IntVal) {
			return new IntVal(val + ((IntVal)rhs).val);
		}
		else if (rhs instanceof DoubleVal) {
			return new DoubleVal(val + ((DoubleVal)rhs).val);
		}
		else
    		throw new UnsupportedOperationException("Cannot divide an integer by a " + rhs.getClass());
	}

	public Number mod(Value rhs) {
		if (rhs instanceof IntVal) {
			return new IntVal(val + ((IntVal)rhs).val);
		}
		else if (rhs instanceof DoubleVal) {
			return new IntVal(val %((DoubleVal)rhs).val);
		}
		else
    		throw new UnsupportedOperationException("Cannot calculate the mod of an integer by a " + rhs.getClass());
	}



} // End class IntVal

