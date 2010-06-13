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
//   Node.java
//   Java Spatial Index Library
//   Copyright (C) 2002 Infomatiq Limited
//  
//  This library is free software; you can redistribute it and/or
//  modify it under the terms of the GNU Lesser General Public
//  License as published by the Free Software Foundation; either
//  version 2.1 of the License, or (at your option) any later version.
//  
//  This library is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//  Lesser General Public License for more details.
//  
//  You should have received a copy of the GNU Lesser General Public
//  License along with this library; if not, write to the Free Software
//  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

package com.firegnom.valkyrie.util.rtree;

import java.io.Serializable;

import com.firegnom.valkyrie.util.Rectangle;

// TODO: Auto-generated Javadoc
/**
 * <p>
 * Used by RTree. There are no public methods in this class.
 * </p>
 * 
 * @author aled.morris@infomatiq.co.uk
 * @version 1.0b2
 */
public class Node implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The node id. */
	int nodeId = 0;
	
	/** The mbr. */
	Rectangle mbr = null;
	
	/** The entries. */
	Rectangle[] entries = null;
	
	/** The ids. */
	int[] ids = null;
	
	/** The level. */
	int level;
	
	/** The entry count. */
	int entryCount;

	/**
	 * Instantiates a new node.
	 *
	 * @param nodeId the node id
	 * @param level the level
	 * @param maxNodeEntries the max node entries
	 */
	Node(final int nodeId, final int level, final int maxNodeEntries) {
		this.nodeId = nodeId;
		this.level = level;
		entries = new Rectangle[maxNodeEntries];
		ids = new int[maxNodeEntries];
	}

	/**
	 * Adds the entry.
	 *
	 * @param r the r
	 * @param id the id
	 */
	void addEntry(final Rectangle r, final int id) {
		ids[entryCount] = id;
		entries[entryCount] = r.copy();
		entryCount++;
		if (mbr == null) {
			mbr = r.copy();
		} else {
			mbr.add(r);
		}
	}

	/**
	 * Adds the entry no copy.
	 *
	 * @param r the r
	 * @param id the id
	 */
	void addEntryNoCopy(final Rectangle r, final int id) {
		ids[entryCount] = id;
		entries[entryCount] = r;
		entryCount++;
		if (mbr == null) {
			mbr = r.copy();
		} else {
			mbr.add(r);
		}
	}

	// delete entry. This is done by setting it to null and copying the last
	// entry into its space.
	/**
	 * Delete entry.
	 *
	 * @param i the i
	 * @param minNodeEntries the min node entries
	 */
	void deleteEntry(final int i, final int minNodeEntries) {
		final int lastIndex = entryCount - 1;
		final Rectangle deletedRectangle = entries[i];
		entries[i] = null;
		if (i != lastIndex) {
			entries[i] = entries[lastIndex];
			ids[i] = ids[lastIndex];
			entries[lastIndex] = null;
		}
		entryCount--;

		// if there are at least minNodeEntries, adjust the MBR.
		// otherwise, don't bother, as the node will be
		// eliminated anyway.
		if (entryCount >= minNodeEntries) {
			recalculateMBR(deletedRectangle);
		}
	}

	// Return the index of the found entry, or -1 if not found
	/**
	 * Find entry.
	 *
	 * @param r the r
	 * @param id the id
	 * @return the int
	 */
	int findEntry(final Rectangle r, final int id) {
		for (int i = 0; i < entryCount; i++) {
			if (id == ids[i] && r.equals(entries[i])) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Gets the entry.
	 *
	 * @param index the index
	 * @return the entry
	 */
	public Rectangle getEntry(final int index) {
		if (index < entryCount) {
			return entries[index];
		}
		return null;
	}

	/**
	 * Gets the entry count.
	 *
	 * @return the entry count
	 */
	public int getEntryCount() {
		return entryCount;
	}

	/**
	 * Gets the id.
	 *
	 * @param index the index
	 * @return the id
	 */
	public int getId(final int index) {
		if (index < entryCount) {
			return ids[index];
		}
		return -1;
	}

	/**
	 * Gets the level.
	 *
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * Gets the mBR.
	 *
	 * @return the mBR
	 */
	public Rectangle getMBR() {
		return mbr;
	}

	/**
	 * Checks if is leaf.
	 *
	 * @return true, if is leaf
	 */
	boolean isLeaf() {
		return (level == 1);
	}

	// oldRectangle is a rectangle that has just been deleted or made smaller.
	// Thus, the MBR is only recalculated if the OldRectangle influenced the old
	// MBR
	/**
	 * Recalculate mbr.
	 *
	 * @param deletedRectangle the deleted rectangle
	 */
	void recalculateMBR(final Rectangle deletedRectangle) {
		if (mbr.edgeOverlaps(deletedRectangle)) {
			mbr.set(entries[0].min, entries[0].max);

			for (int i = 1; i < entryCount; i++) {
				mbr.add(entries[i]);
			}
		}
	}

	/**
	 * eliminate null entries, move all entries to the start of the source node.
	 *
	 * @param rtree the rtree
	 */
	void reorganize(final RTree rtree) {
		int countdownIndex = rtree.maxNodeEntries - 1;
		for (int index = 0; index < entryCount; index++) {
			if (entries[index] == null) {
				while (entries[countdownIndex] == null
						&& countdownIndex > index) {
					countdownIndex--;
				}
				entries[index] = entries[countdownIndex];
				ids[index] = ids[countdownIndex];
				entries[countdownIndex] = null;
			}
		}
	}
}
