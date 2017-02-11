package edu.genesis.runtime;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

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
      PrintStream o = null;
                                      try {
            o = new PrintStream(new FileOutputStream("A.txt",true));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GenesisVal.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.setOut(o);
        System.setErr(o);
     for (int i=0; i < indent; i++) {
          System.out.print(' ');
      }
     System.out.println(s);
  }
  static void print (Object s) {
      PrintStream o = null;
                                      try {
            o = new PrintStream(new FileOutputStream("A.txt",true));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GenesisVal.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.setOut(o);
        System.setErr(o);
     for (int i=0; i < indent; i++) {
          System.out.print(' ');
      }
     System.out.print(s);
  }
}
