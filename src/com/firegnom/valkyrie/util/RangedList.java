/*******************************************************************************
 * Copyright (c) 2010 Maciej Kaniewski (mk@firegnom.com).
 * 
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 3 of the License, or
 *    (at your option) any later version.
 * 
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 * 
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software Foundation,
 *    Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 * 
 *    Contributors:
 *     Maciej Kaniewski (mk@firegnom.com) - initial API and implementation
 ******************************************************************************/
package com.firegnom.valkyrie.util;

public class RangedList<E> {
	Node first;
	Node last;

	private class Node {
		int min;
		int max;
		E o;
		Node next;

		public Node(int min, int max, E o) {
			this.min = min;
			this.max = max;
			this.o = o;
		}

	}

	public RangedList() {
	}

	public void add(int min, int max, E o) {
		Node n = new Node(min, max, o);
		if (last == null) {
			first = n;
			last = first;
			return;
		}
		last.next = n;
		last = n;
	}

	public E get(int val) {
		Node data = first;
		while (data != null) {
			if (data.min <= val && data.max >= val) {
				return data.o;
			}
			data = data.next;
		}
		return null;
	}
}
