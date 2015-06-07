/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.genesis.runtime;

/**
 *
 * @author Laptop
 */
/*
   MetaNode -- enable nodes to be added to data structures that
   will be ignored by GenesisList.  These nodes can be use for 
   adding  meta-information to data structures.
*/
class MetaNode extends TreeNode {
    public MetaNode() {
        info = new StickyNote("MetaNode");
   }
   // Add logic for comparing lists using eq 
    @Override
   public boolean eq( GenesisVal rhs ) {
      //System.out.println ("eq called on:");
      if  ( ! (rhs instanceof MetaNode) ) {
          return false;
      }
      GenesisList gl1 = new GenesisList(this);
      GenesisList gl2 = new GenesisList((MetaNode)rhs);
      return gl1.equals(gl2);
   } 
}

