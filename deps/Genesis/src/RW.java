import java.io.*;

public class RW {
   public static void main (String[] args) {
     PrintWriter out=null;
     // write to an output file
     try
       {
         out = new PrintWriter( new FileWriter( "out.dat" , true ) );
       }
     catch ( IOException e )
       {
         System.err.println( "Error: " + e.toString() );
         System.exit( 1 );
       }
     out.println("This is a line of output");
     out.close();

     // read from a file
     LineNumberReader in=null;
     try
       {
         in = new LineNumberReader( new FileReader( "out.dat" ), 1000 );
       }
     catch ( IOException e )
       {
         System.err.println( "Error: " + e.toString() );
         System.exit( 1 );
       }
      String line = "";
      try {
         line = in.readLine();
      }
     catch ( IOException e )
       {
         System.err.println( "Error: " + e.toString() );
         System.exit( 1 );
       }
       System.out.println("Read:" + line) ;
 
   }
}
