// PascalTextReader.java -- simplify reading of input
//   allows you to use the same routines
//   for reading from a file or reading from the keyboard
//                    Based on the way things are done in Pascal.
//  Author: Larry Morell
//  Date: June 5, 2003 (from an earlier version)
package edu.genesis.runtime;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

class PascalTextReader {

    public final String WHITESPACE = " \t\n\r";
    private String infileName;
    private BufferedReader infile;
    private int ch; // to hold the next character to read
    private boolean eoln; // whether the last char a line has been read
    private boolean eof;  // whether the last char of the file has been read
    PrintStream o;

    PascalTextReader() {
                
                        try {
            o = new PrintStream(new FileOutputStream("A.txt",true));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GenesisVal.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.setOut(o);
        System.setErr(o);
        infileName = "";
        infile = new BufferedReader(new InputStreamReader(System.in));
        eoln = true;
        eof = false;
    }

    PascalTextReader(String fileName) {
                
                        try {
            o = new PrintStream(new FileOutputStream("A.txt",true));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GenesisVal.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.setOut(o);
        System.setErr(o);
        infileName = fileName;
        try {
            if (infileName.equals("")) {
                infile = new BufferedReader(new InputStreamReader(System.in));
            } else {
                infile = new BufferedReader(new FileReader(infileName));
            }
        } catch (IOException e) {
            System.out.println("Input error:");
            System.out.println(e);
            System.exit(-1);
        }
        eoln = true;
        eof = false;
    }

    public boolean eoln() {
        return eoln;
    }

    public boolean moreInput() {
        return true;
    }

    public boolean eof() {
        return this.eof;
    }

    public boolean moreInput(String skipchars) {
        return true;
    }

    public char readChar() {
        try {
            infile.mark(1);
            ch = infile.read();
            if (ch == -1) {
                eof = true;  // flag for eof function
            }
        } catch (IOException e) {
            System.out.println("Input error:");
            System.out.println(e);
            System.exit(-1);
        }
        return (char) ch;
    }

    // Skip chars in string s 
    @SuppressWarnings("empty-statement")
    public void skip(String s) {
        while ((ch = readChar()) != (char) -1 && (s.indexOf((char) ch) != -1));
        if (ch != -1) {
            reset();
        }
    }

    @SuppressWarnings("empty-statement")
    public void skipTo(String s) {
        while ((ch = readChar()) != (char) -1 && (s.indexOf((char) ch) == -1));
        reset(); // backup one char
    }

    private void reset() {
        try {
            infile.reset();
        } catch (IOException e) {
            System.out.println("Input error:");
            System.out.println(e);
            System.exit(-1);
        }
    }

    public String gather(String s) {
        String buffer = "";
        while ((ch = readChar()) != (char) -1 && (s.indexOf((char) ch) != -1)) {
            buffer = buffer + (char) ch;
        }
        reset(); // move back to mark to re-read most recently read char
        return buffer;
    }

    public String gatherTo(String s) {
        String buffer = "";
        while ((ch = readChar()) != (char) -1 && (s.indexOf((char) ch) == -1)) {
            buffer = buffer + (char) ch;
        }
        reset();
        return buffer;
    }

    public int readInt() {
        skip(" \t\n\r");
        boolean negative = false;
        if (readChar() == '-') {
            negative = true;
        } else {
            reset();
        }
        String number = gather("0123456789");
        int no = Integer.parseInt(number);
        if (negative) {
            no = -no;
        }
        return no;
    }

    public long readLong() {
        long no = 0;
        do {
            skip(" \t\n\r");
            boolean negative = false;
            if (ch == '-') {
                negative = true;
                readChar();
            }
            String number = gather("0123456789");
            try {
                no = Long.parseLong(number);
                if (negative) {
                    no = -no;
                }
            } catch (Exception e) {
                System.out.println("Your number was not legally formatted: " + no + ":" + e);
                reset();
            }
        } while (no == 0);
        return no;
    }

    public float readFloat() {
        skip(" \t\n\r");
        boolean negative = false;
        if (readChar() == '-') {
            negative = true;
        } else {
            reset();
        } // move back
        String number = gather("0123456789");
        if ((ch = readChar()) == '.') {
            number = number + (char) ch + gather("0123456789");
        } else {
            reset();
        }
        float no = Float.parseFloat(number);
        if (negative) {
            no = -no;
        }
        return no;
    }

    public double readDouble() {
        skip(" \t\n\r");
        boolean negative = false;
        if (readChar() == '-') {
            negative = true;
        } else {
            reset();
        }

        String number = gather("0123456789");
        if ((ch = readChar()) == '.') {
            number = number + (char) ch + gather("0123456789");
        } else {
            reset();
        }
        double no = Double.parseDouble(number);
        if (negative) {
            no = -no;
        }
        return no;
    }

    public String readLine() {
        String line = gatherTo("\r\n");
        char c = readChar(); // toss newline
        if (c == '\r') {
            readChar();
        }
        return line;
    }
}
