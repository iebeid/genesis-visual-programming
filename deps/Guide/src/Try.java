/*
Try.java  -- Program to 

Author: Larry Morell

Modification History
Date        Action
05/04/09  -- Original version

*/

import java.io.*;
import java.lang.Object;
import java.awt.event.*;


public class Try {
   public static void main (String [] args) {
try {
   Runtime rt = Runtime.getRuntime();
   Process proc = rt.exec("/bin/date >> out");

   // Obtain as an input stream, the stdout of the process
   InputStream is = proc.getInputStream(); 

   BufferedReader br = new BufferedReader (new InputStreamReader(is));
   String line = br.readLine(); 
   while (line != null) {
      System.out.println (line);
      line = br.readLine(); 
   }

}
catch (Exception e) {
   System.out.println(e);
   System.exit(1);
}


   }
}

