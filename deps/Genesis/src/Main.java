public class Main {
 
   public static void main (String args[]) {
      GenesisIO io = new GenesisIO();
      double d = Math.random()*1000;
      int n = (int) d;
      System.out.println(n);
      String [][] s = new String [][] {
         {"Hello", "there"   },
         {"Larry", "Morell"  },
         {"Thou", "foul", "man!"   }
      };
      Node m = new Node();
      m.setLabel("one");
      System.out.println (m.getLabel());
      GenesisList gl= new GenesisList();
      gl.insert(new StickyNote("abc"));
      gl.get();
   }
}
