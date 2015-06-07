/*
 *  Copyright (C) 2011 Larry Morell <morell@cs.atu.edu>
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

package runtime;

/**
 *
 * @author Larry Morell <morell@cs.atu.edu>
 */
public interface List {

	/**
	 * Copies the list to rhs
	 * @param rhs
	 * @return
	 */
	ListVal add(Value rhs);

	/**
	 *
	 * @return true iff the the current point is on the first node of the list
	 */
	boolean atFirst();

	/**
	 *
	 * @return True iff the current point is on the last node of the list
	 */
	boolean atLast();

	/**
	 * Change the value of the current node in the list to e
	 * @param e
	 */
	void change(StickyNote e);

	/**
	 *
	 * @return The list that contains copies of each of the values of the original
	 */
	Value copy();

	/**
	 *
	 * @return A reference to the current Node of the list
	 */
	Node current();

	/**
	 * Positioning the current element to its successor; then delete its predecessor
	 */
	void del();

	/**
	 * Invokes Utility.print on the list
	 */
	void display();

	/**
	 * Invokes Utility.println on the list
	 */
	void displayln();

	void done();

	/**
	 * @return true iff the list contains no elements
	 */
	boolean empty();

	/**
	 *
	 * @param gv
	 * @return Contained list.equals(gv); false if gv is not a list
	 */
	boolean eq(Value gv);

	/**
	 *
	 * @param e
	 * @return  two Lists are equal if they have the same values in their nodes
	 */
	boolean equals(ListVal e);

	boolean ge(StickyNote rhs);

	/**
	 *
	 * @return The value of the current element of the list
	 */
	StickyNote get();

	boolean gt(StickyNote rhs);

	/**
	 *
	 * @return A String version of the list of up to the first 1000 elements of a list
	 */
	String implode();

	/**
	 * Re-establish the list as empty
	 */
	void init();

	void insert(Value v);

	/**
	 *
	 * @param e Insert e after the current point of the list; e becomes the
	 * current element of the list
	 *
	 */
	void insert(StickyNote e);

	/**
	 *
	 * @param n insert n after the current element of the list; n becomes the
	 * new current element
	 */
	void insert(Node n);

	boolean le(StickyNote rhs);

	/**
	 * Move current point to the left
	 */
	void left();

	boolean lt(StickyNote rhs);

	/**
	 * Move the current point to the next node if one exists; no movement and
	 * no error if one does not exist
	 */
	void move();

	/**
	 * Move current position n nodes to the right if possible; if not possible
	 * then move current point past the end of the list
	 * @param n
	 */
	void move(int n);

	/**
	 * Move to the N-th node of a list; error if Node N does not exist
	 * @param n
	 */
	void moveTo(int n);

	boolean ne(StickyNote rhs);

	/**
	 *
	 * @return True iff the current point is off the list
	 */
	boolean off();

	/**
	 *
	 * @return True iff the current point is on the list
	 */
	boolean on();

	/**
	 *
	 * @return The ordinal position of the current point in the list
	 */
	int pos();

	/**
	 * Same as left()
	 */
	void prev();

	void reset();

	/**
	 * Moves the current point in the list to the first node whose contained
	 * value matches that in A
	 * @param A
	 * @return The first StickyNote in the list whose value = A
	 */
	StickyNote search(StickyNote A);

	/**
	 *
	 * @return The number of nodes in the list
	 */
	int size();

	/**
	 *
	 * @return A string version of the list
	 */
	String toString();

	/**
	 *
	 * @return The string "ListVal"
	 */
	String typeName();

	/**
	 * @return Debugging true iff  the representation of the list appears valid
	 */
	boolean valid();

}
