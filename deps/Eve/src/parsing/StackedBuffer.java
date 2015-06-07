/*
 *  Copyright (C) 2010 Larry Morell <morell@cs.atu.edu>
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package parsing;

import java.io.FileReader;
import java.util.Stack;

/**
 * A StackedBuffer implements a stack of buffers, which gives the impression
 * of a continuous stream of access through nested buffers, effectively hiding the
 * nested buffers from a client program which only needs to request a change of
 * input streams, either by supplying a file name or a string.  When the substituted
 * buffer is read to completion, reading continues smoothly from the previously
 * stacked buffer.  Tokens cannot be broken across buffers.
 * @author Larry Morell
 */
public class StackedBuffer extends BufferWrapper {
   // ---------------- Instance variables ----------------//
	Stack<AbstractBuffer> stack;
	// ---------------- Constructors ----------------------//
	/**
	 * Default constructor: no buffer, empty stack
	 */
	protected StackedBuffer() {
		super();
		buffer = new Buffer("");
		stack = new Stack<AbstractBuffer>();
	}

	/**
	 * Supply a buffer as the starting source
	 * @param b Buffer to be wrapped
	 */
	public StackedBuffer(AbstractBuffer b) {
		super();
		stack = new Stack<AbstractBuffer>();  // create an empty stack
      buffer = b.copy();  // invoke the overriden copy in b
	}
	
	/**
	 * Copy constructor
	 * @param sbw
	 */
	public StackedBuffer(StackedBuffer sbw) {
		StackedBuffer temp = new StackedBuffer();
	   temp.stack.addAll(stack);
	}
	/**
	 * 
	 * @return a copy of the stacked buffer
	 */
 

   /**
	 * Establish a new reading source for this buffer from the specified string
	 * @param s -- the string to read from
	 */
	public void setSource(String s) {
	
		if (buffer != null)
   		stack.push(buffer.copy());   // copy current buffer onto the stack
		buffer.setSource(s);                   // Set the source to the string
	}

	/**
	 * Establish a new reading source for this buffer from the specified file
	 * @param fileReader -- reader for accessing the file
	 * @param fileName -- the file name of the file
	 */
	public void setSource(FileReader fileReader, String fileName) {
		AbstractBuffer ab = buffer.copy();
		stack.push(ab);
		buffer.setSource(fileReader,fileName);
	}

	
	public AbstractBuffer copy() {
		return new StackedBuffer(this);
	}
	@Override
   public boolean eof() {
		while (buffer.eof() && ! stack.empty()) {
			buffer = stack.pop();
		}
		return buffer.eof();
	}

	@Override
   public char get() {
		char ch;
		if (!eof())
			return buffer.get();
   	else return '\0';
	}
	@Override
   public void advance(){
		if ( !eof())
			buffer.advance();
	}

	// ---------------- Getters/Setters -------------------//

	// test routine
	public static void check(boolean b, String msg) {
		Buffer.check(b,msg);
	}
	public static void main (String[] args) {
	 /* The thing to test is the ability to start with one buffer, switch to
	 * another buffer, finish reading from that buffer, then switch back
	 */
		Buffer b = new Buffer("This is the first buffer");
		// Read 2 words
		StackedBuffer sb = new StackedBuffer(b);
		String w = sb.getId();
		check (w.equals("This"),"getId worked");
		w = sb.getId();
		check (w.equals("is"),"getId worked");
		// Now switch to a new buffer
      sb.setSource("1 2 3 4");
		w = sb.getNumber();
		check (w.equals("1"),"getId worked");
		w = sb.getNumber();
		check (w.equals("2"),"getId worked");
		w = sb.getNumber();
		check (w.equals("3"),"getId worked");
		w = sb.getNumber();
		check (w.equals("4"),"getId worked");
		w = sb.getId();
		check (w.equals("the"),"getId worked");


	}



}
