/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package runtime;

/**
 * Abstract class that requires comparison methods to be be implementd
 * @author Larry Morell
 */
 public interface  Compare  extends Value
{
  public abstract boolean lt( Value rhs );
  public abstract boolean le( Value rhs );
  public abstract boolean gt( Value rhs );
  public abstract boolean ge( Value rhs );
  public abstract boolean ne( Value rhs );
  
}

