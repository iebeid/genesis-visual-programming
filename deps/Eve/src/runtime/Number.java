/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package runtime;



/**
 * Abstract class that requires comparison and arithmetic operations to be
 * implemented
 * @author Larry Morell
 */
public interface Number extends Compare
{
  public void setPrecision();
  public abstract Number add(Value rhs);
  public abstract Number sub( Value rhs );
  public abstract Number mul( Value rhs );
  public abstract Number div( Value rhs );
  public abstract Number mod( Value rhs );
  public abstract boolean lt( Value rhs );
  public abstract boolean le( Value rhs );
  public abstract boolean gt( Value rhs );
  public abstract boolean ge( Value rhs );
  public abstract boolean eq( Value rhs );

  public abstract double toDouble();
  public abstract int toInt();
  
}  // end class Number



