/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package runtime;



/**
 *
 * @author Larry Morell
 */
public class StringVal extends Val implements Compare
{
  protected String val;

  public StringVal( String s )
  {
    val = s;
  }

	StringVal(Compare rhs) {
	   val = ((StringVal) rhs).val;
	}

	public StringVal(StickyNote s2) {
		throw new UnsupportedOperationException("Not yet implemented");
	}


	@Override
  public String toString()
  {
    //System.out.println ("Returning: " + val);
    return val;
  }

  public boolean lt( Value rhs )
  {
    checkBadComparison(this, rhs);
    StringVal s;
    if ( rhs instanceof StringVal ) s = (StringVal)rhs;
    else s = new StringVal( "" );
    return val.compareTo( s.val ) < 0;
  }

  public boolean le( Value rhs )
  {
    checkBadComparison(this, rhs);
    StringVal s;
    if ( rhs instanceof StringVal ) s = (StringVal)rhs;
    else s = new StringVal( "" );
    return val.compareTo( s.val ) <= 0;
  }

  public boolean gt( Value rhs )
  {
    checkBadComparison(this, rhs);
    StringVal s;
    if ( rhs instanceof StringVal ) s = (StringVal)rhs;
    else s = new StringVal( "" );
    return val.compareTo( s.val ) > 0;
  }

  public boolean ge( Value rhs )
  {
    checkBadComparison(this, rhs);
    StringVal s;
    if ( rhs instanceof StringVal ) s = (StringVal)rhs;
    else s = new StringVal( "" );
    return val.compareTo( s.val ) >= 0;
  }

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 79 * hash + (this.val != null ? this.val.hashCode() : 0);
		return hash;
	}

	@Override
  public boolean equals( Object rhs )
  {
    StringVal s;
    if ( rhs instanceof StringVal ) s = (StringVal)rhs;
    else s = new StringVal( "" );
    return val.compareTo( s.val ) == 0;
  }
  public boolean eq( Value rhs )
  {
    StringVal s;
    if ( rhs instanceof StringVal ) s = (StringVal)rhs;
    else s = new StringVal( "" );
    return val.compareTo( s.val ) == 0;
  }

  public boolean ne( Value rhs )
  {
    StringVal s;
    if ( rhs instanceof StringVal ) s = (StringVal)rhs;
    else s = new StringVal( "" );
    return val.compareTo( s.val ) != 0;
  }
  public int compareTo (Object o) {
    StringVal s;
    if ( o instanceof StringVal ) s = (StringVal)o;
    else return 0;
    return val.compareTo( s.val );
  }

  public Value add( Value rhs )
  {
	 // rely on the toString of primitive type
    if (rhs instanceof DoubleVal)
		  return new StringVal(val + rhs);
    else if (rhs instanceof StringVal)
       return new StringVal( val + (StringVal)rhs);
    else if (rhs instanceof ListVal) { // Append string at the beginning of the list
       ListVal gl = ((ListVal) rhs).copy();
          gl.reset(); gl.insert(new StickyNote(new StringVal(val)));
          return gl;
    }
    else
       return null;
  }

	@Override
	public Val copy() {
		return new StringVal(this);
	}

}  // end class StringVal



