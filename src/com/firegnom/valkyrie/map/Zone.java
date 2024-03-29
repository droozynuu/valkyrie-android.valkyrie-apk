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
package com.firegnom.valkyrie.map;

import gnu.trove.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.Properties;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Picture;

import com.firegnom.valkyrie.action.ActionIndex;
import com.firegnom.valkyrie.engine.GameController;
import com.firegnom.valkyrie.map.pathfinding.AStarPathFinder;
import com.firegnom.valkyrie.map.pathfinding.Mover;
import com.firegnom.valkyrie.map.pathfinding.Pathfindable;
import com.firegnom.valkyrie.util.RangedList;
import com.firegnom.valkyrie.util.Rectangle;
import com.firegnom.valkyrie.util.ResourceLoader;

// TODO: Auto-generated Javadoc
/**
 * The Class Zone.
 */
public class Zone implements Pathfindable {

	/** The Constant MOVE_MATRIX. */
	public static final String MOVE_MATRIX = "_moveMatrix";
	
	/** The Constant MAX_MOVES_PATH. */
	public static final int MAX_MOVES_PATH = 15;
	
	/** The draw objects. */
	private boolean drawObjects = true;

	/** The width. */
	public int width;
	
	/** The height. */
	public int height;
	
	/** The tile width. */
	public int tileWidth;
	
	/** The tile height. */
	public int tileHeight;

	/** The tiles location. */
	public String tilesLocation;
	
	/** The context actions. */
	public ActionIndex contextActions;
	
	/** The map objects. */
	public MapObjectsIndex mapObjects;

	/** The props. */
	public Properties props;

	/** The tile sets. */
	public RangedList<StringTileSet> tileSets;
	
	/** The layers. */
	public ArrayList<Layer> layers;
	
	/** The active tiles. */
	public TIntObjectHashMap<String> activeTiles;
	// protected ArrayList objectGroups;

	/** The version. */
	public int version;
	
	/** The name. */
	public String name;

	/** The move matrix. */
	public short[][] moveMatrix;
	
	/** The id. */
	public int id;
	
	/** The finder. */
	public AStarPathFinder finder;
	
	/** The move matrix first gid. */
	public int moveMatrixFirstGid;
	
	/** The Constant MOVE_MATRIX_TILE_MATRIX. */
	public static final short[][] MOVE_MATRIX_TILE_MATRIX = {
	/* 0 */{ 1, 1, 1, 1 },
	/* 1 */{ 1, 0, 0, 0 },
	/* 2 */{ 0, 0, 1, 0 },
	/* 3 */{ 0, 1, 0, 0 },
	/* 4 */{ 0, 0, 0, 1 },
	/* 5 */{ 1, 0, 1, 0 },
	/* 6 */{ 0, 1, 0, 1 },
	/* 7 */{ 0, 0, 1, 1 },
	/* 8 */{ 1, 1, 0, 0 },
	/* 9 */{ 1, 0, 1, 1 },
	/* 10 */{ 1, 1, 0, 1 },
	/* 11 */{ 0, 1, 1, 1 },
	/* 12 */{ 1, 1, 1, 0 },
	/* 13 */{ 1, 0, 0, 1 },
	/* 14 */{ 0, 1, 1, 0 } };
	
	/** The PATHFINDE r_ ma x_ complicity. */
	private static int PATHFINDER_MAX_COMPLICITY = 800;

	/**
	 * Instantiates a new zone.
	 *
	 * @param name the name
	 * @param width2 the width2
	 * @param height2 the height2
	 * @param tileWidth2 the tile width2
	 * @param tileHeight2 the tile height2
	 */
	public Zone(String name, int width2, int height2, int tileWidth2,
			int tileHeight2) {
		this.name = name;
		// this.version = version;
		mapObjects = new MapObjectsIndex();
		contextActions = new ActionIndex();
		this.width = width2;
		this.height = height2;
		this.tileWidth = tileWidth2;
		this.tileHeight = tileHeight2;
		props = new Properties();
		this.finder = new AStarPathFinder(this, Zone.MAX_MOVES_PATH,
				Zone.PATHFINDER_MAX_COMPLICITY, true);
		tileSets = new RangedList<StringTileSet>();
		layers = new ArrayList<Layer>();
		activeTiles = new TIntObjectHashMap<String>();
	}

	// Functions used in path finding algorithm
	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.map.pathfinding.Pathfindable#getCost(com.firegnom.valkyrie.map.pathfinding.Mover, int, int, int, int)
	 */
	@Override
	public float getCost(Mover mover, int sx, int sy, int tx, int ty) {
		return 1;
	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.map.pathfinding.Pathfindable#getHeightInTiles()
	 */
	@Override
	public int getHeightInTiles() {
		return height * 2;
	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.map.pathfinding.Pathfindable#getWidthInTiles()
	 */
	@Override
	public int getWidthInTiles() {
		return width * 2;
	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.map.pathfinding.Pathfindable#pathFinderVisited(int, int)
	 */
	@Override
	public void pathFinderVisited(int x, int y) {
	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.map.pathfinding.Pathfindable#blocked(com.firegnom.valkyrie.map.pathfinding.Mover, int, int)
	 */
	@Override
	public boolean blocked(Mover mover, int x, int y) {
		return moveMatrix[x][y] != 0;
	}

	/**
	 * Find a tile for a given global tile id.
	 *
	 * @param gid The global tile id we're looking for
	 * @return The tileset in which that tile lives or null if the gid is not
	 * defined
	 */
	public StringTileSet findTileSet(int gid) {
		return tileSets.get(gid);
	}

	/** The p. */
	Picture p = new Picture();
	
	/** The paint. */
	Paint paint = new Paint();
	
	/** The c. */
	Canvas c;

	/**
	 * Render zone.
	 *
	 * @param x the x
	 * @param y the y
	 * @param screenWidth the screen width
	 * @param screenHeight the screen height
	 * @param rl the rl
	 * @return the picture
	 */
	public Picture renderZone(int x, int y, int screenWidth, int screenHeight,
			ResourceLoader rl) {
		int startx = (x * -1) / this.tileWidth;
		int canvasX = (x) % this.tileWidth;
		int starty = (y * -1) / this.tileHeight;
		int canvasY = (y) % this.tileHeight;
		int endx = startx + screenWidth / tileWidth + 2;
		int endy = starty + screenHeight / tileHeight + 2;
		endx = (endx >= width) ? width : endx;
		endy = (endy >= width) ? width : endy;
		GameController sc = GameController.getInstance();

		c = p.beginRecording(screenWidth + 2 * tileWidth, screenHeight + 2
				* tileHeight);
		int gid, x1, y1, il = 0, len = layers.size();
		Layer l;
		for (il = 0; il < len; il++) {
			l = layers.get(il);
			if (!GameController.SHOW_MOVE_MATRIX
					&& l.name.equals(Zone.MOVE_MATRIX)) {
				continue;
			}
			for (x1 = startx; x1 < endx; x1++) {
				for (y1 = starty; y1 < endy; y1++) {
					try {
						gid = l.data[x1][y1];
					} catch (ArrayIndexOutOfBoundsException e) {
						gid = 0;
					}
					if (gid > 0) {
						Bitmap img = rl.getBitmapResource(
								activeTiles.get(l.data[x1][y1]), false);
						if (img != null) {
							c.drawBitmap(img, ((x1 - startx) * tileWidth)
									+ canvasX, ((y1 - starty) * tileHeight)
									+ canvasY, paint);
						} else {
							c.drawRect(((x1 - startx) * tileWidth) + canvasX,
									((y1 - starty) * tileHeight) + canvasY,
									((x1 - startx) * tileWidth) + canvasX
											+ tileWidth,
									((y1 - starty) * tileHeight) + canvasY
											+ tileHeight, sc.outOfMemoryPaint);
						}
					}

				}
			}
		}
		// draw map objects ;
		if (drawObjects) {

			ArrayList<MapObject> objects = mapObjects.get(new Rectangle(-1 * x,
					-1 * y, -1 * x + screenWidth, -1 * y + screenHeight));
			for (MapObject mapObject : objects) {
				Bitmap img = sc.rl.getBitmapResource(mapObject.image, false);
				if (img != null) {
					c.drawBitmap(
							sc.rl.getBitmapResource(mapObject.image, false),
							mapObject.x + sc.sX, mapObject.y + sc.sY, paint);
				} else {
					c.drawRect(mapObject.x + sc.sX, mapObject.y + sc.sY,
							mapObject.x + sc.sX + mapObject.width, mapObject.y
									+ sc.sY + mapObject.height,
							sc.outOfMemoryPaint);
				}
			}
		}

		p.endRecording();
		return p;
	}

	/**
	 * Builds the move matrix.
	 *
	 * @param l the l
	 */
	public void buildMoveMatrix(Layer l) {
		int firstGid = moveMatrixFirstGid;
		short[][] moveMatrix = new short[l.width * 2][l.height * 2];

		for (int x = 0; x < l.width; x++) {
			for (int y = 0; y < l.height; y++) {
				int mx = 2 * x, my = 2 * y;
				if (l.data[x][y] == 0) {
					moveMatrix[mx][my] = 0;
					moveMatrix[mx + 1][my] = 0;
					moveMatrix[mx][my + 1] = 0;
					moveMatrix[mx + 1][my + 1] = 0;
					continue;
				}
				int tileid = l.data[x][y] - firstGid;
				moveMatrix[mx][my] = Zone.MOVE_MATRIX_TILE_MATRIX[tileid][0];
				moveMatrix[mx + 1][my] = Zone.MOVE_MATRIX_TILE_MATRIX[tileid][1];
				moveMatrix[mx][my + 1] = Zone.MOVE_MATRIX_TILE_MATRIX[tileid][2];
				moveMatrix[mx + 1][my + 1] = Zone.MOVE_MATRIX_TILE_MATRIX[tileid][3];
			}
		}
		this.moveMatrix = moveMatrix;
	}

	/**
	 * Return on screen coordinates from move matrix position.
	 *
	 * @param x position in move matrix
	 * @param y position in move matrix
	 * @return position x,y on screen
	 */
	public Position getCoords(int x, int y) {
		return new Position(getXCoords(x), getYCoords(y));

	}

	/**
	 * Return on screen x coordinate from move matrix position x.
	 *
	 * @param x position in move matrix
	 * @return the x coords
	 */
	public int getXCoords(int x) {
		return ((x * this.tileWidth / 2) + (this.tileWidth / 4));
	}

	/**
	 * Return on screen y coordinate from move matrix position y.
	 *
	 * @param y position in move matrix
	 * @return the y coords
	 */
	public int getYCoords(int y) {
		return y * this.tileHeight / 2 + this.tileHeight / 4;
	}

	/**
	 * Gets the map position.
	 *
	 * @param x the x
	 * @param y the y
	 * @param sX the s x
	 * @param sY the s y
	 * @return the map position
	 */
	public Position getMapPosition(int x, int y, int sX, int sY) {
		Position p = new Position();
		p.x = (int) (((-1 * sX) + x) / tileWidth * 2);
		p.y = (int) (((-1 * sY) + y) / tileHeight * 2);
		return p;
	}

	/**
	 * Gets the map position.
	 *
	 * @param p the p
	 * @param sX the s x
	 * @param sY the s y
	 * @return the map position
	 */
	public Position getMapPosition(Position p, int sX, int sY) {
		return getMapPosition(p.x, p.y, sX, sY);
	}

}
