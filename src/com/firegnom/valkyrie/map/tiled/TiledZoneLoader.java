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

public class TiledZoneLoader implements ZoneLoader {

	private static final String TAG = "TiledZoneLoader";
	ResourceLoader rl;
	Context c;

	public TiledZoneLoader(Context context) {
		c = context;
		rl = new ResourceLoader(
				GameController.getInstance().resourceLoaderService);

	}

	@Override
	public Zone load(String name) {
		long startTime, stopTime;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			factory.setValidating(false);
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setEntityResolver(new EntityResolver() {
				public InputSource resolveEntity(String publicId,
						String systemId) throws SAXException, IOException {
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
			} catch (JSONException e) {
				throw new ValkyrieRuntimeException(e);
			}

			InputStream inputStream = new GZIPInputStream(
					rl.getResourceAsStream(
							name + "-ver_" + version + ".tmx.gz", true));

			Document doc = builder.parse(inputStream);
			Element docElement = doc.getDocumentElement();
			stopTime = System.currentTimeMillis();
			Log.d(TAG, "Loaded Zone tmx file in  in :"
					+ ((stopTime - startTime) / 1000) + " secondsand "
					+ ((stopTime - startTime) % 1000) + " miliseconds");
			System.gc();
			String orient = docElement.getAttribute("orientation");
			if (!orient.equals("orthogonal")) {
				throw new TiledLoaderException(
						"Only orthogonal maps supported, found: " + orient);
			}

			int width = Integer.parseInt(docElement.getAttribute("width"));
			int height = Integer.parseInt(docElement.getAttribute("height"));
			int tileWidth = Integer.parseInt(docElement
					.getAttribute("tilewidth"));
			int tileHeight = Integer.parseInt(docElement
					.getAttribute("tileheight"));

			Zone zone = new Zone(name, width, height, tileWidth, tileHeight);
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
			NodeList setNodes = docElement.getElementsByTagName("tileset");
			int i;
			for (i = 0; i < setNodes.getLength(); i++) {
				Element current = (Element) setNodes.item(i);

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
			NodeList layerNodes = docElement.getElementsByTagName("layer");
			Element current;
			for (i = 0; i < layerNodes.getLength(); i++) {
				current = (Element) layerNodes.item(i);
				Layer layer = getLayer(zone, current);
				layer.index = i;
				zone.layers.add(layer);

			}

			stopTime = System.currentTimeMillis();
			Log.d(TAG, "Loaded Zone Layers in  in :"
					+ ((stopTime - startTime) / 1000) + " secondsand "
					+ ((stopTime - startTime) % 1000) + " miliseconds");
			NodeList objectGroupNodes = docElement
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

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	private void getZoneProperties(Zone zone, Element docElement) {
		Element propsElement = (Element) docElement.getElementsByTagName(
				"properties").item(0);
		if (propsElement != null) {
			NodeList properties = propsElement.getElementsByTagName("property");
			if (properties != null) {
				zone.props = new Properties();
				for (int p = 0; p < properties.getLength(); p++) {
					Element propElement = (Element) properties.item(p);

					String k = propElement.getAttribute("name");
					String v = propElement.getAttribute("value");
					zone.props.setProperty(k, v);
				}
			}
		}
	}

	@Override
	public Zone load(String name, int version) {
		// TODO Auto-generated method stub
		return null;
	}

	public StringTileSet getTileSet(Zone map, Element element, Context c)
			throws TiledLoaderException {
		// TileSet = new com.firegnom.valkyrie.map.TileSet(tiles, tileWidth,
		// tileHeight)

		String name = element.getAttribute("name");
		int firstGID = Integer.parseInt(element.getAttribute("firstgid"));
		if (name.equals("_moveMatrix")) {
			map.moveMatrixFirstGid = firstGID;
		}
		String source = element.getAttribute("source");
		if ((source != null) && (!source.equals(""))) {
			throw new TiledLoaderException("Not Implemented "
					+ map.tilesLocation + "/" + source);
		}
		String tileWidthString = element.getAttribute("tilewidth");
		String tileHeightString = element.getAttribute("tileheight");
		if (tileWidthString.length() == 0 || tileHeightString.length() == 0) {
			throw new TiledLoaderException(
					"TiledMap requires that the map be created with tilesets that use a "
							+ "single image.  Check the WiKi for more complete information.");
		}
		int tileWidth = Integer.parseInt(tileWidthString);
		int tileHeight = Integer.parseInt(tileHeightString);

		String sv = element.getAttribute("spacing");
		if ((sv != null) && (!sv.equals(""))) {
			throw new TiledLoaderException("Not Implemented "
					+ map.tilesLocation + "/" + source);
		}

		String mv = element.getAttribute("margin");
		if ((mv != null) && (!mv.equals(""))) {
			throw new TiledLoaderException("Not Implemented "
					+ map.tilesLocation + "/" + source);
		}

		NodeList list = element.getElementsByTagName("image");
		Element imageNode = (Element) list.item(0);
		String ref = imageNode.getAttribute("source");

		StringTileSet t;
		JSONObject obj;
		try {
			obj = new JSONObject(convertStreamToString(rl.getResourceAsStream(
					ref + ".json", true)));
			t = new StringTileSet(ref, tileWidth, tileHeight,
					obj.getInt("height"), obj.getInt("width"));
		} catch (JSONException e) {
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

	public Layer getLayer(Zone map, Element element)
			throws TiledLoaderException {
		// this.map = map;
		String name = element.getAttribute("name");
		int width = Integer.parseInt(element.getAttribute("width"));
		int height = Integer.parseInt(element.getAttribute("height"));
		Layer l = new Layer(name, width, height);
		// data = new int[width][height][3];
		// now read the layer properties

		Element propsElement = (Element) element.getElementsByTagName(
				"properties").item(0);
		if (propsElement != null) {
			NodeList properties = propsElement.getElementsByTagName("property");
			if (properties != null) {
				l.props = new Properties();
				for (int p = 0; p < properties.getLength(); p++) {
					Element propElement = (Element) properties.item(p);

					String k = propElement.getAttribute("name");
					String v = propElement.getAttribute("value");
					l.props.setProperty(k, v);
				}
			}
		}

		Element dataNode = (Element) element.getElementsByTagName("data").item(
				0);
		String encoding = dataNode.getAttribute("encoding");
		String compression = dataNode.getAttribute("compression");
		long decodetime = 0, readTime = 0;
		if (encoding.equals("base64") && compression.equals("gzip")) {
			try {
				Node cdata = dataNode.getFirstChild();
				char[] enc = cdata.getNodeValue().trim().toCharArray();
				byte[] dec = Base64.decode(enc);

				GZIPInputStream is = new GZIPInputStream(
						new ByteArrayInputStream(dec));

				byte[] r = new byte[height * width * 4];
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
			} catch (IOException e) {
				Log.e("error", e.toString());
				throw new TiledLoaderException("Unable to decode base 64 block");
			}
		} else {
			throw new TiledLoaderException("Unsupport tiled map type: "
					+ encoding + "," + compression
					+ " (only gzip base64 supported)");
		}
	}

	public ActionIndex getActionIndex(Element element)
			throws UnsupportedEncodingException {
		ActionIndex ret = new ActionIndex();
		if (!element.getAttribute("name").equals("_ContextActions")) {
			return null;
		}
		// read object properties
		NodeList objectNodes = element.getElementsByTagName("object");
		for (int i = 0; i < objectNodes.getLength(); i++) {
			Element objElement = (Element) objectNodes.item(i);
			String container = objElement.getAttribute("name");
			int x = Integer.parseInt(objElement.getAttribute("x"));
			int y = Integer.parseInt(objElement.getAttribute("y"));
			int width = Integer.parseInt(objElement.getAttribute("width"));
			int height = Integer.parseInt(objElement.getAttribute("height"));
			// now read the layer properties
			Element propsElement = (Element) objElement.getElementsByTagName(
					"properties").item(0);
			if (propsElement != null) {
				NodeList properties = propsElement
						.getElementsByTagName("property");
				if (properties != null) {

					for (int p = 0; p < properties.getLength(); p++) {
						try {
							ContextAction ca = new ContextAction();
							ca.container = container;
							ca.x = x;
							ca.y = y;
							ca.width = width;
							ca.height = height;
							Element propElement = (Element) properties.item(p);
							ca.name = propElement.getAttribute("name");
							String script = ((CDATASection) (propElement
									.getFirstChild().getNextSibling()))
									.getData();
							ca.script = script.getBytes("UTF-8");
							// Log.d("",ca.toString());
							ret.put(ca);

						} catch (Exception e) {
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

	private void appendMapObjects(Element element, MapObjectsIndex mapObjects) {
		// right now i am not reading object group fields
		NodeList objectNodes = element.getElementsByTagName("object");
		for (int i = 0; i < objectNodes.getLength(); i++) {
			Element objElement = (Element) objectNodes.item(i);
			MapObject mo = new MapObject();
			mo.name = objElement.getAttribute("name");
			// type = objElement.getAttribute("type");
			mo.x = Integer.parseInt(objElement.getAttribute("x"));
			mo.y = Integer.parseInt(objElement.getAttribute("y"));
			mo.width = Integer.parseInt(objElement.getAttribute("width"));
			mo.height = Integer.parseInt(objElement.getAttribute("height"));

			Element imageElement = (Element) objElement.getElementsByTagName(
					"image").item(0);
			if (imageElement != null) {
				mo.image = imageElement.getAttribute("source");
			}
			if (mo.image != null) {
				mapObjects.put(mo);
			}

		}
	}

	@Override
	public Zone load(String name, Context c) {
		// TODO Auto-generated method stub
		return null;
	}

	public String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * return null which means there's no more data to read. Each line will
		 * appended to a StringBuilder and returned as String.
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sb.toString();
	}

}
