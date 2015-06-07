public class Utility {
  static boolean DEBUG = false;
  static boolean TEST = false;
  public static int indent = 0;

  static void entering (String msg) { 
    
    if (DEBUG) {
       println("Entering " + msg);
       indent = indent + 2;
    }
  }
  static void leaving (String msg) { 
    if (DEBUG) {
       indent = indent - 2;
       println("Leaving "+msg);
    }
  }
  static void leaving (String msg,Object obj) { 
    if (DEBUG) {
       indent = indent - 2;
       println("Leaving: "+ msg+obj);
    }
  }
  static void println (Object s) {
     for (int i=0; i < indent; i++) System.out.print(' ');
     System.out.println(s);
  }
  static void print (Object s) {
     for (int i=0; i < indent; i++) System.out.print(' ');
     System.out.print(s);
  }
}
