// I/O class for the Genesis compiler/interpreter
// Author:        Wes Potts
// Date:        3/9/04
//
package edu.genesis.runtime;

import java.io.*;

class GenesisIO {

    private LineNumberReader in;
    private PrintWriter out;

    // standard constructor: uses stdin and stdout
    public GenesisIO(String infile, String outfile) {
        try {
            if (!infile.equals("")) {
                in = new LineNumberReader(new FileReader(infile), 1000);
            } else {
                in = new LineNumberReader(
                        new InputStreamReader(System.in), 1000);
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.toString());
            System.exit(1);
        }
        if (!outfile.equals("")) {
            out = new PrintWriter(System.out, true);
        } else {
            try {
                out = new PrintWriter(new FileWriter(outfile, true));
            } catch (IOException e) {
                System.err.println("Error: " + e.toString());
                System.exit(1);
            }
        }
        out = new PrintWriter(System.out, true);
    }

    public GenesisIO() {
        in = new LineNumberReader(new InputStreamReader(System.in), 1000);
        out = new PrintWriter(System.out, true);
    }

    // methods for setting the Input and Output sources
    // might add support for stuff besides files in the future (ie. sockets)
    public void setInputFile(String infile) {
        try {
            if (!infile.equals("")) {
                in = new LineNumberReader(new FileReader(infile), 1000);
            } else {
                in = new LineNumberReader(
                        new InputStreamReader(System.in), 1000);
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.toString());
            System.exit(1);
        }
    }

    public void setOutputFile(String outfile) {
        try {
            out = new PrintWriter(new FileWriter(outfile, true));
        } catch (IOException e) {
            System.err.println("Error: " + e.toString());
            System.exit(1);
        }
    }

    // IO methods
    //
    // Reads a character.
    public int read() {
        int c = 0;
        try {
            c = in.read();
        } catch (IOException e) {
            System.err.println("Error:\n\tin method GenIO.read()\n\t"
                    + e.toString());
        }
        return c;
    }

    public void reset() {
        try {
            in.reset();
        } catch (IOException e) {
            System.err.println("Error:\n\tin method GenIO.reset()\n\t"
                    + e.toString());
        }
    }

    public void mark(int n) {
        try {
            in.mark(n);
        } catch (IOException e) {
            System.err.println("Error:\n\tin method GenIO.mark()\n\t"
                    + e.toString());
        }
    }
    // Reads an array of characters and stores them in "buff"
    // starting at location "off" and not exceeding "len".
    // Returns the number of characters read.

    public int read(char[] buff, int off, int len) {
        int n = 0;
        try {
            n = in.read(buff, off, len);
        } catch (IOException e) {
            System.err.println(
                    "Error:\n\tin method GenIO.read( char [] buff, int off, int len )\n\t"
                    + e.toString());
        }
        return n;
    }

    // Reads a String.
    public String readLine() {
        String s = "";
        try {
            s = in.readLine();
        } catch (IOException e) {
            System.err.println("Error:\n\tin method GenIO.readLine()\n\t"
                    + e.toString());
        }
        return s;
    }

    // Prints a character.
    public void print(char c) {
        {
            out.print(c);
            out.flush();
        }
    }

    // Prints a String.
    public void print(String s) {
        {
            out.print(s);
            out.flush();
        }
    }

    // Prints a newline.
    public void println() {
        {
            out.println();
            out.flush();
        }
    }

    // Prints a String followed by a newline.
    public void println(String s) {
        {
            out.println(s);
            out.flush();
        }
    }

    // Prints a character array followed by a newline.
    public void println(char[] s) {
        {
            out.println(s);
            out.flush();
        }
    }

    // Prints a character followed by a newline.
    public void println(char c) {
        {
            out.println(c);
            out.flush();
        }
    }

    // Prints a double followed by a newline.
    public void println(double d) {
        {
            out.println(d);
            out.flush();
        }
    }

    // Prints a float followed by a newline.
    public void println(float f) {
        {
            out.println(f);
            out.flush();
        }
    }

    // Prints an int followed by a newline.
    public void println(int i) {
        {
            out.println(i);
            out.flush();
        }
    }

    public int getLineNumber() {
        return in.getLineNumber();
    }
}
