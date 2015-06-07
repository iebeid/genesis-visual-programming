/*
    SourcePgm  -- class for storing and accessing the source program

Modification history:

1/08 -- original version
1/20/08 -- ljm: integrated with Tokens, Parser, and GenesisInterpreter for better identification
           of files and line numbers
*/
import java.io.*;
import java.util.*;

class SourcePgm {  // information about the source program
   // For every line in the source file we need to know what file it came from 
   class SourceLine {
      String fileName;
      String line;
      int lineNo;
      public SourceLine (String fn, String ln, int lno) {
         fileName = fn;
         line = ln;
         lineNo = lno;
      }
      public String toString() {
         String ans = fileName + "(" + lineNo + "):" + line ;
         return ans;
      }
      public String toString(boolean withfilenames) {
         String ans = (withfilenames?fileName + "(" + lineNo + "):" : "") + line ;
         return ans;
      }
      String getFileName() {
         return fileName;
      }
      void setLine(String ln) {
         line = ln;
      }
      void setLineNo(int ln) {
         lineNo = ln;
      }
      String getLine() {
         return line;
      }
      int getLineNo() {
         return lineNo;
      }
   }

   // Attributes
   String fileName;    //  current file name of lines
   String line;        //  current line 
   int lineNo;         //  current line number, relative to file 
   int charPos;        //  char position within the current line     
   int sourceLineIndex;      //  index into the sourceLines vector
   static int totalChars;     //  total number of characters in the source program
   static int totalLines;     //  total number of lines in the source program

   Stack <Integer> lineCount;   // for tracking line numbers within a file
   
   Vector <SourceLine> sourceLines; 

   // Constructor(s)
   public SourcePgm (String fn){
      sourceLines = new Vector<SourceLine>();  
      fileName = fn;
      line = null;
      // charPos = 0;
      lineNo = 1;
      sourceLineIndex = 0;
      //System.out.println("Exiting constructor for SourcePgm ("+fn+")");
   }    

   public SourcePgm (SourcePgm sp){
      sourceLines = sp.sourceLines;
      fileName = sp.fileName;
      line = sp.line;
      // charPos = 0;
      lineNo = sp.lineNo;
      sourceLineIndex = sp.sourceLineIndex;
      //System.out.println("Exiting constructor for SourcePgm ("+fn+")");
   }    
   
   public void set(SourcePgm sp){
      sourceLines = sp.sourceLines;
      fileName = sp.fileName;
      line = sp.line;
      // charPos = 0;
      lineNo = sp.lineNo;
      sourceLineIndex = sp.sourceLineIndex;
      //System.out.println("Exiting constructor for SourcePgm ("+fn+")");
   }    
   
   // methods
   // setters and getters
   void setLineNo (int ln) {
      lineNo = ln; 
      
   }
   int lineNo () {
      return lineNo;
   }

   String line() {
     return line;
   }
   void setLine (String ln) {
      line = ln; 
   }
   void setCharPos (int cp) {
      charPos = cp; 
   }
   int charPos () {
      return charPos;
   }
   void setFileName (String fn) {
      fileName = fn; 
   }
   String fileName () {
      return fileName;
   }
   public String toString () {
     String ans = "";
     for (int i=0; i < sourceLines.size(); i++) ans = ans + sourceLines.elementAt(i);
     return ans;
   }

   public String toString (boolean withfilenames) {
     String ans = "";
     for (int i=0; i < sourceLines.size(); i++) ans = ans + sourceLines.elementAt(i).toString(withfilenames);
     return ans;
   }

   void append (String line) {
      SourceLine sl = new SourceLine(fileName, line, lineNo);
      sourceLines.add(sl);
      lineNo++;
      totalLines++;
      totalChars = totalChars + line.length();
   }

   //  -- prepare for reading by establishing the first line
   void setup() {
      fileName = "";
      if (sourceLines.size() > 0) {
         SourceLine sl = sourceLines.elementAt(0);
         line = sl.getLine();
         fileName = sl.getFileName();
      }
      lineNo = 1;
      charPos = 0;
   }
   char getChar () {  // get the current char and advance
      char ch;
      if (charPos >= line.length())  {
         sourceLineIndex++;
         if (sourceLineIndex < sourceLines.size()) {
            SourceLine sl = sourceLines.elementAt(sourceLineIndex);
            line = sl.getLine(); 
            fileName = sl.getFileName();
            lineNo = sl.getLineNo();
            charPos = 0;
         }
         
      }
      if (line != null)  {
         //System.out.println("Source line=" + line);
         //System.out.println("charPos=" + charPos);
         ch = line.charAt(charPos);
         charPos++;
      }
      else
         ch = (char)0; // return null character;
      return ch; 
   }
   int length () { return totalChars;}
} // class SourcePgm
