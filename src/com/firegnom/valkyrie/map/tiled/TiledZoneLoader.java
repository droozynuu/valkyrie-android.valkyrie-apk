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
package com.firegnom.valkyrie.map.tiled;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.content.Context;
import android.util.Log;

import com.firegnom.valkyrie.action.ActionIndex;
import com.firegnom.valkyrie.action.ContextAction;
import com.firegnom.valkyrie.engine.GameController;
import com.firegnom.valkyrie.engine.ValkyrieRuntimeException;
import com.firegnom.valkyrie.map.Layer;
import com.firegnom.valkyrie.map.MapObject;
import com.firegnom.valkyrie.map.MapObjectsIndex;
import com.firegnom.valkyrie.map.StringTileSet;
import com.firegnom.valkyrie.map.Zone;
import com.firegnom.valkyrie.map.ZoneLoader;
import com.firegnom.valkyrie.util.Base64;
import com.firegnom.valkyrie.util.ResourceLoader;

// TODO: Auto-generated Javadoc
/**
 * The Class TiledZoneLoader.
 */
public class TiledZoneLoader implements ZoneLoader {

	/** The Constant TAG. */
	private static final String TAG = "TiledZoneLoader";
	
	/** The rl. */
	ResourceLoader rl;
	
	/** The c. */
	Context c;

	/**
	 * Instantiates a new tiled zone loader.
	 *
	 * @param context the context
	 */
	public TiledZoneLoader(final Context context) {
		c = context;
		rl = new ResourceLoader(
				GameController.getInstance().resourceLoaderService);

	}

	/**
	 * Append map objects.
	 *
	 * @param element the element
	 * @param mapObjects the map objects
	 */
	private void appendMapObjects(final Element element,
			final MapObjectsIndex mapObjects) {
		// right now i am not reading object group fields
		final NodeList objectNodes = element.getElementsByTagName("object");
		for (int i = 0; i < objectNodes.getLength(); i++) {
			final Element objElement = (Element) objectNodes.item(i);
			final MapObject mo = new MapObject();
			mo.name = objElement.getAttribute("name");
			// type = objElement.getAttribute("type");
			mo.x = Integer.parseInt(objElement.getAttribute("x"));
			mo.y = Integer.parseInt(objElement.getAttribute("y"));
			mo.width = Integer.parseInt(objElement.getAttribute("width"));
			mo.height = Integer.parseInt(objElement.getAttribute("height"));

			final Element imageElement = (Element) objElement
					.getElementsByTagName("image").item(0);
			if (imageElement != null) {
				mo.image = imageElement.getAttribute("source");
			}
			if (mo.image != null) {
				mapObjects.put(mo);
			}

		}
	}

	/**
	 * Convert stream to string.
	 *
	 * @param is the is
	 * @return the string
	 */
	public String convertStreamToString(final InputStream is) {
		/*
		 * To convert the InputStream to String we use the
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * return null which means there's no more data to read. Each line will
		 * appended to a StringBuilder and returned as String.
		 */
		final BufferedReader reader = new BufferedReader(new InputStreamReader(
				is));
		final StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (final IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}

		return sb.toString();
	}

	/**
	 * Gets the action index.
	 *
	 * @param element the element
	 * @return the action index
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 */
	public ActionIndex getActionIndex(final Element element)
			throws UnsupportedEncodingException {
		final ActionIndex ret = new ActionIndex();
		if (!element.getAttribute("name").equals("_ContextActions")) {
			return null;
		}
		// read object properties
		final NodeList objectNodes = element.getElementsByTagName("object");
		for (int i = 0; i < objectNodes.getLength(); i++) {
			final Element objElement = (Element) objectNodes.item(i);
			final String container = objElement.getAttribute("name");
			final int x = Integer.parseInt(objElement.getAttribute("x"));
			final int y = Integer.parseInt(objElement.getAttribute("y"));
			final int width = Integer
					.parseInt(objElement.getAttribute("width"));
			final int height = Integer.parseInt(objElement
					.getAttribute("height"));
			// now read the layer properties
			final Element propsElement = (Element) objElement
					.getElementsByTagName("properties").item(0);
			if (propsElement != null) {
				final NodeList properties = propsElement
						.getElementsByTagName("property");
				if (properties != null) {

					for (int p = 0; p < properties.getLength(); p++) {
						try {
							final ContextAction ca = new ContextAction();
							ca.container = container;
							ca.x = x;
							ca.y = y;
							ca.width = width;
							ca.height = height;
							final Element propElement = (Element) properties
									.item(p);
							ca.name = propElement.getAttribute("name");
							final String script = ((CDATASection) (propElement
									.getFirstChild().getNextSibling()))
									.getData();
							ca.script = script.getBytes("UTF-8");
							// Log.d("",ca.toString());
							ret.put(ca);

						} catch (final Exception e) {
							e.printStackTrace();
							Log.e("TiledZoneLoader",
									"Problems with loading actions from context action layer");
						}
					}
				}
			}

		}

		return ret;

	}

	/**
	 * Gets the layer.
	 *
	 * @param map the map
	 * @param element the element
	 * @return the layer
	 * @throws TiledLoaderException the tiled loader exception
	 */
	public Layer getLayer(final Zone map, final Element element)
			throws TiledLoaderException {
		// this.map = map;
		final String name = element.getAttribute("name");
		final int width = Integer.parseInt(element.getAttribute("width"));
		final int height = Integer.parseInt(element.getAttribute("height"));
		final Layer l = new Layer(name, width, height);
		// data = new int[width][height][3];
		// now read the layer properties

		final Element propsElement = (Element) element.getElementsByTagName(
				"properties").item(0);
		if (propsElement != null) {
			final NodeList properties = propsElement
					.getElementsByTagName("property");
			if (properties != null) {
				l.props = new Properties();
				for (int p = 0; p < properties.getLength(); p++) {
					final Element propElement = (Element) properties.item(p);

					final String k = propElement.getAttribute("name");
					final String v = propElement.getAttribute("value");
					l.props.setProperty(k, v);
				}
			}
		}

		final Element dataNode = (Element) element.getElementsByTagName("data")
				.item(0);
		final String encoding = dataNode.getAttribute("encoding");
		final String compression = dataNode.getAttribute("compression");
		final long decodetime = 0, readTime = 0;
		if (encoding.equals("base64") && compression.equals("gzip")) {
			try {
				final Node cdata = dataNode.getFirstChild();
				final char[] enc = cdata.getNodeValue().trim().toCharArray();
				final byte[] dec = Base64.decode(enc);

				final GZIPInputStream is = new GZIPInputStream(
						new ByteArrayInputStream(dec));

				final byte[] r = new byte[height * width * 4];
				int read = 0;
				while (read < height * width * 4) {
					read += is.read((byte[]) (r), read, height * width * 4
							- read);
				}
				// Log.d("aadsdasdsa","data read :"+read+"more :"+is.available());
				// //5072
				// if (l.name.equals("Layer 2")){
				// read = is.read((byte[])(r), read, height*width*4-read);
				// Log.d("aadsdasdsa","data read :"+read+"more :"+is.available());
				// }

				int tileId, y, x;
				int pos = 0, tmp;
				StringTileSet set;

				for (y = 0; y < height; y++) {
					for (x = 0; x < width; x++) {
						tmp = 0;
						tmp |= ((int) (r[pos] & 0xFF));
						tmp |= ((int) (r[pos + 1] & 0xFF)) << 8;
						tmp |= ((int) (r[pos + 2] & 0xFF)) << 16;
						tmp |= ((int) (r[pos + 3] & 0xFF)) << 24;

						tileId = tmp;
						l.data[x][y] = tileId;
						if (tileId != 0 && map.activeTiles.get(tileId) == null) {
							set = map.findTileSet(tileId);
							// rl.downloadService(set.getTileNamePng(tileId));
							map.activeTiles.put(tileId,
									set.getTileNamePng(tileId));
						}
						pos += 4;
					}

				}
				if (l.name.equals(Zone.MOVE_MATRIX)) {
					map.buildMoveMatrix(l);
				}
				return l;
			} catch (final IOException e) {
				Log.e("error", e.toString());
				throw new TiledLoaderException("Unable to decode base 64 block");
			}
		} else {
			throw new TiledLoaderException("Unsupport tiled map type: "
					+ encoding + "," + compression
					+ " (only gzip base64 supported)");
		}
	}

	/**
	 * Gets the tile set.
	 *
	 * @param map the map
	 * @param element the element
	 * @param c the c
	 * @return the tile set
	 * @throws TiledLoaderException the tiled loader exception
	 */
	public StringTileSet getTileSet(final Zone map, final Element element,
			final Context c) throws TiledLoaderException {
		// TileSet = new com.firegnom.valkyrie.map.TileSet(tiles, tileWidth,
		// tileHeight)

		final String name = element.getAttribute("name");
		final int firstGID = Integer.parseInt(element.getAttribute("firstgid"));
		if (name.equals("_moveMatrix")) {
			map.moveMatrixFirstGid = firstGID;
		}
		final String source = element.getAttribute("source");
		if ((source != null) && (!source.equals(""))) {
			throw new TiledLoaderException("Not Implemented "
					+ map.tilesLocation + "/" + source);
		}
		final String tileWidthString = element.getAttribute("tilewidth");
		final String tileHeightString = element.getAttribute("tileheight");
		if (tileWidthString.length() == 0 || tileHeightString.length() == 0) {
			throw new TiledLoaderException(
					"TiledMap requires that the map be created with tilesets that use a "
							+ "single image.  Check the WiKi for more complete information.");
		}
		final int tileWidth = Integer.parseInt(tileWidthString);
		final int tileHeight = Integer.parseInt(tileHeightString);

		final String sv = element.getAttribute("spacing");
		if ((sv != null) && (!sv.equals(""))) {
			throw new TiledLoaderException("Not Implemented "
					+ map.tilesLocation + "/" + source);
		}

		final String mv = element.getAttribute("margin");
		if ((mv != null) && (!mv.equals(""))) {
			throw new TiledLoaderException("Not Implemented "
					+ map.tilesLocation + "/" + source);
		}

		final NodeList list = element.getElementsByTagName("image");
		final Element imageNode = (Element) list.item(0);
		final String ref = imageNode.getAttribute("source");

		StringTileSet t;
		JSONObject obj;
		try {
			obj = new JSONObject(convertStreamToString(rl.getResourceAsStream(
					ref + ".json", true)));
			t = new StringTileSet(ref, tileWidth, tileHeight,
					obj.getInt("height"), obj.getInt("width"));
		} catch (final JSONException e) {
			throw new ValkyrieRuntimeException(e);
		}
		t.lastGID = (firstGID + (t.maxX * t.maxY) - 1);
		t.name = name;
		t.firstGID = firstGID;
		return t;

		// properties for individual tile are not yet implemented for performane
		// issues i think it might not be wise to do so

		// NodeList pElements = element.getElementsByTagName("tile");
		// for (int i=0;i<pElements.getLength();i++) {
		// Element tileElement = (Element) pElements.item(i);
		//
		// int id = Integer.parseInt(tileElement.getAttribute("id"));
		// id += firstGID;
		// Properties tileProps = new Properties();
		//
		// Element propsElement = (Element)
		// tileElement.getElementsByTagName("properties").item(0);
		// NodeList properties = propsElement.getElementsByTagName("property");
		// for (int p=0;p<properties.getLength();p++) {
		// Element propElement = (Element) properties.item(p);
		//
		// String name = propElement.getAttribute("name");
		// String value = propElement.getAttribute("value");
		//
		// tileProps.setProperty(name, value);
		// }
		//
		// props.put(new Integer(id), tileProps);
		// }
	}

	/**
	 * Gets the zone properties.
	 *
	 * @param zone the zone
	 * @param docElement the doc element
	 * @return the zone properties
	 */
	private void getZoneProperties(final Zone zone, final Element docElement) {
		final Element propsElement = (Element) docElement.getElementsByTagName(
				"properties").item(0);
		if (propsElement != null) {
			final NodeList properties = propsElement
					.getElementsByTagName("property");
			if (properties != null) {
				zone.props = new Properties();
				for (int p = 0; p < properties.getLength(); p++) {
					final Element propElement = (Element) properties.item(p);

					final String k = propElement.getAttribute("name");
					final String v = propElement.getAttribute("value");
					zone.props.setProperty(k, v);
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.map.ZoneLoader#load(java.lang.String)
	 */
	@Override
	public Zone load(final String name) {
		long startTime, stopTime;
		try {
			final DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			factory.setValidating(false);
			final DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setEntityResolver(new EntityResolver() {
				public InputSource resolveEntity(final String publicId,
						final String systemId) throws SAXException, IOException {
					return new InputSource(
							new ByteArrayInputStream(new byte[0]));
				}
			});
			startTime = System.currentTimeMillis();

			// map version
			JSONObject obj;
			int version = 0;
			try {
				obj = new JSONObject(
						convertStreamToString(rl
								.getResourceAsStreamDownload(name + ".json")));
				version = obj.getInt("version");
			} catch (final JSONException e) {
				throw new ValkyrieRuntimeException(e);
			}

			final InputStream inputStream = new GZIPInputStream(
					rl.getResourceAsStream(
							name + "-ver_" + version + ".tmx.gz", true));

			final Document doc = builder.parse(inputStream);
			final Element docElement = doc.getDocumentElement();
			stopTime = System.currentTimeMillis();
			Log.d(TAG, "Loaded Zone tmx file in  in :"
					+ ((stopTime - startTime) / 1000) + " secondsand "
					+ ((stopTime - startTime) % 1000) + " miliseconds");
			System.gc();
			final String orient = docElement.getAttribute("orientation");
			if (!orient.equals("orthogonal")) {
				throw new TiledLoaderException(
						"Only orthogonal maps supported, found: " + orient);
			}

			final int width = Integer
					.parseInt(docElement.getAttribute("width"));
			final int height = Integer.parseInt(docElement
					.getAttribute("height"));
			final int tileWidth = Integer.parseInt(docElement
					.getAttribute("tilewidth"));
			final int tileHeight = Integer.parseInt(docElement
					.getAttribute("tileheight"));

			final Zone zone = new Zone(name, width, height, tileWidth,
					tileHeight);
			// now read the map properties
			startTime = System.currentTimeMillis();
			getZoneProperties(zone, docElement);
			stopTime = System.currentTimeMillis();
			Log.d(TAG, "Loaded Zone Properties  in  in :"
					+ ((stopTime - startTime) / 1000) + " secondsand "
					+ ((stopTime - startTime) % 1000) + " miliseconds");
			System.gc();
			startTime = System.currentTimeMillis();

			StringTileSet tileSet = null;
			final NodeList setNodes = docElement
					.getElementsByTagName("tileset");
			int i;
			for (i = 0; i < setNodes.getLength(); i++) {
				final Element current = (Element) setNodes.item(i);

				tileSet = getTileSet(zone, current, c);
				tileSet.index = i;
				Log.d(TAG, "Adding tileset to zone tilestets firstGID ="
						+ tileSet.firstGID + ",lastGID=" + tileSet.lastGID
						+ ", name=" + tileSet.imageName);
				zone.tileSets.add(tileSet.firstGID, tileSet.lastGID, tileSet);
			}
			stopTime = System.currentTimeMillis();
			Log.d("performance", "Loaded Zone tilesets in  in :"
					+ ((stopTime - startTime) / 1000) + " secondsand "
					+ ((stopTime - startTime) % 1000) + " miliseconds");
			System.gc();
			startTime = System.currentTimeMillis();
			final NodeList layerNodes = docElement
					.getElementsByTagName("layer");
			Element current;
			for (i = 0; i < layerNodes.getLength(); i++) {
				current = (Element) layerNodes.item(i);
				final Layer layer = getLayer(zone, current);
				layer.index = i;
				zone.layers.add(layer);

			}

			stopTime = System.currentTimeMillis();
			Log.d(TAG, "Loaded Zone Layers in  in :"
					+ ((stopTime - startTime) / 1000) + " secondsand "
					+ ((stopTime - startTime) % 1000) + " miliseconds");
			final NodeList objectGroupNodes = docElement
					.getElementsByTagName("objectgroup");
			for (i = 0; i < objectGroupNodes.getLength(); i++) {
				current = (Element) objectGroupNodes.item(i);
				if (current.getAttribute("name").equals("_ContextActions")) {
					zone.contextActions = getActionIndex(current);
				} else {
					appendMapObjects(current, zone.mapObjects);
				}
			}
			System.gc();
			return zone;

		} catch (final Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.map.ZoneLoader#load(java.lang.String, android.content.Context)
	 */
	@Override
	public Zone load(final String name, final Context c) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.map.ZoneLoader#load(java.lang.String, int)
	 */
	@Override
	public Zone load(final String name, final int version) {
		// TODO Auto-generated method stub
		return null;
	}

}
