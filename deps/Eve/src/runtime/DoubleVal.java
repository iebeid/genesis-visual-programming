/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package runtime;

import java.text.DecimalFormat;

/**
 *
 * @author Larry Morell
 */
public class DoubleVal   extends Val
		   implements Number
{
  protected double val;
  protected static int precision = -1;  // -1 means default precision of Java


   /**
	 *
	 * @return the precision of the contained DoubleVal
	 */
	public static int getPrecision() {
		return precision;
	}

	/**
	 * Sets the number of decimal places of the DoubleVal; default is -1
	 * @param precision
	 */
	public static void setPrecision(int precision) {
		DoubleVal.precision = precision;
	}
  /**
	* Create a DoubleVal with value d
	* @param d
	*/
  public DoubleVal( double d )
  {
    val = d;
  }
  public DoubleVal (DoubleVal d){
	  val =d.val;
  }
  /**
	*
	* @return The double value stored in the DoubleVal
	*/
  public double getVal()
  {
    return val;
  }


	@Override
	/**
	 * Returns a string version of the contained double, set to the required precision
	 */
  public String toString()  // using precision set by user
  {
    StringBuffer result= new StringBuffer(100);
    if (precision>=0) {
       if (precision > 40 ) precision=40;  // Limit precision to 40
       StringBuffer format = new StringBuffer (precision+2); // two extra for the 0.
       // Patch to fix the differences between the format interpretation on boole vs my machine
       format.append ('0');
       if (precision  > 0) format.append('.');

       for (int i=0; i < precision; i++) {
          format.append('0');
       }
       DecimalFormat df=new DecimalFormat(new String(format));
       String v=df.format(val);
       result=result.append(v);
    }
    else if ((int) val == val ) {
       int n =  (int) val;
       result = result.append(String.valueOf(n));
    }
    else {
       result = result.append(String.valueOf(val));
    }
    return new String(result);
  }

	/**
	 *
	 * @return  The double value contained within the DoubleVal
	 */
  public double toDouble()
  {
    return val;
  }

  /**
	*
	* @return An int that results from truncating the contained double
	*/
  public int toInt()
  {
    return (int)val;
  }


  /**
	*
	* @param rhs
	* @return The sum of the contained double and rhs
	*/

  /**
	*
	* @param rhs
	* @return The sum of the contained double and rhs
	*/
  public Number add( Value rhs )
  {
    if (rhs instanceof IntVal) {
			return new DoubleVal(val + ((IntVal)rhs).val);
		}
		else if (rhs instanceof DoubleVal) {
			return new DoubleVal(val + ((DoubleVal)rhs).val);
		}
		else
    		throw new UnsupportedOperationException("Cannot add a number and a " + rhs.getClass());
  }

  /**
	*
	* @param rhs
	* @return The difference of the contained Double and rhs
	*/
  public Number sub( Value rhs )
  {
    if (rhs instanceof IntVal) {
			return new DoubleVal(val - ((IntVal)rhs).val);
		}
		else if (rhs instanceof DoubleVal) {
			return new DoubleVal(val - ((DoubleVal)rhs).val);
		}
		else
    		throw new UnsupportedOperationException("Cannot subtract a number and a " + rhs.getClass());
  }

  /**
	*
	* @param rhs
	* @return The product of the contained double and rhs
	*/
  public Number mul( Value rhs )
  {
    if (rhs instanceof IntVal) {
			return new DoubleVal(val * ((IntVal)rhs).val);
		}
		else if (rhs instanceof DoubleVal) {
			return new DoubleVal(val * ((DoubleVal)rhs).val);
		}
		else
    		throw new UnsupportedOperationException("Cannot multiply a number and a " + rhs.getClass());
  }

  /**
	*
	* @param rhs
	* @return The quotient of the contained double and rhs
	*/
  public Number div( Value rhs )
  {
    if (rhs instanceof IntVal) {
			return new DoubleVal(val % ((IntVal)rhs).val);
		}
		else if (rhs instanceof DoubleVal) {
			return new DoubleVal(val % ((DoubleVal)rhs).val);
		}
		else
    		throw new UnsupportedOperationException("Cannot add a number and a " + rhs.getClass());
  }

  /**
	*
	* @param rhs
	* @return The remainder of the contained double and rhs
	*/
  public Number mod( Value rhs )
  {
    if (rhs instanceof IntVal) {
			return new DoubleVal(val - ((IntVal)rhs).val);
		}
		else if (rhs instanceof DoubleVal) {
			return new DoubleVal(val %((DoubleVal)rhs).val);
		}
		else
    		throw new UnsupportedOperationException("Cannot compute the remainder of  a number and a " + rhs.getClass());
  }
 

  /**
	*
	* @param rhs
	* @return Contained double < rhs?
	*/
  public boolean lt( Value rhs )
  {

    DoubleVal d;

    //System.out.println("Returning doubleval: " + val + " < " + rhs.getClass());
    checkBadComparison(this, rhs);
    d = (DoubleVal)rhs;
    return val < d.val;
  }

  /**
	*
	* @param rhs
	* @return Contained double is <= rhs
	*/
  public boolean le( Value rhs )
  {
    DoubleVal d;
    checkBadComparison(this, rhs);
    if ( rhs instanceof DoubleVal ) d = (DoubleVal)rhs;
    else d = new DoubleVal( 0 );
    //System.out.println("Returning doubleval: " + val + " <= " + d.val + "=" + (val <= d.val));
    return val <= d.val;
  }

  /**
	*
	* @param rhs
	* @return Contained double > rhs
	*/
  public boolean gt( Value rhs )
  {
    checkBadComparison(this, rhs);
    DoubleVal d;
    if ( rhs instanceof DoubleVal ) d = (DoubleVal)rhs;
    else d = new DoubleVal( 0 );
    //System.out.println("Returning doubleval: " + val + " > " + d.val + "=" + (val > d.val));
    return val > d.val;
  }

  /**
	*
	* @param rhs
	* @return Contained double >= rhs
	*/
  public boolean ge( Value rhs )
  {
    checkBadComparison(this, rhs);
    DoubleVal d;
    if ( rhs instanceof DoubleVal ) d = (DoubleVal)rhs;
    else d = new DoubleVal( 0 );
    //System.out.println("Returning doubleval: " + val + " >= " + d.val + "=" + (val >= d.val));
    return val >= d.val;
  }

  /**
	*
	* @param rhs
	* @return Contained double = rhs
	*/
  public boolean eq( Value rhs )
  {
    checkBadComparison(this, rhs);
    DoubleVal d;
    if ( rhs instanceof DoubleVal ) d = (DoubleVal)rhs;
    else d = new DoubleVal( 0 );
    //System.out.println("Returning doubleval: " + val + "==" + d.val + "=" + (val == d.val));
    return val == d.val;
  }

  /**
	*
	* @param rhs
	* @return Contained double != rhs
	*/
  public boolean ne( Value rhs )
  {
    DoubleVal d;
    if ( rhs instanceof DoubleVal ) d = (DoubleVal)rhs;
    else d = new DoubleVal( 0 );
    //System.out.println("Returning doubleval: " + val + "!= " + d.val + "!=" + (val != d.val));
    return val != d.val;
  }

	public void setPrecision() {
		precision = -1;
	}

	@Override
	public Value copy() {
		return new DoubleVal(val);
	}


/**
	*
	* @param rhs
	* @return Contained double < rhs?
	*/
 

}  // end class DoubleVal


