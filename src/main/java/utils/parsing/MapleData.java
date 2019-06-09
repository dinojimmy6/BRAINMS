/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
 Matthias Butz <matze@odinms.de>
 Jan Christian Meyer <vimes@odinms.de>

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as
 published by the Free Software Foundation version 3 as published by
 the Free Software Foundation. You may not use, modify or distribute
 this program under any other version of the GNU Affero General Public
 License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package utils.parsing;

import tools.Eval;

import java.awt.Point;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class MapleData implements Iterable<MapleData> {
    public enum MapleDataType {
        NONE,
        IMG_0x00,
        SHORT,
        INT,
        FLOAT,
        DOUBLE,
        STRING,
        EXTENDED,
        PROPERTY,
        CANVAS,
        VECTOR,
        CONVEX,
        SOUND,
        UOL,
        UNKNOWN_TYPE,
        UNKNOWN_EXTENDED_TYPE;
    }

    private Node node;

    public MapleData(FileInputStream fis) {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(fis);
            this.node = document.getFirstChild();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private MapleData(Node node) {
        this.node = node;
    }

    public MapleData getChildByPath(String path) {
        String segments[] = path.split("/");
        if (segments[0].equals("..")) {
            return getParent().getChildByPath(path.substring(path.indexOf("/") + 1));
        }

        Node myNode = node;
        for (String segment : segments) {
            NodeList childNodes = myNode.getChildNodes();
            boolean foundChild = false;
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node childNode = childNodes.item(i);
                if (childNode.getNodeType() == Node.ELEMENT_NODE && childNode.getAttributes().getNamedItem("name").getNodeValue().equals(segment)) {
                    myNode = childNode;
                    foundChild = true;
                    break;
                }
            }
            if (!foundChild) {
                return null;
            }
        }
        MapleData ret = new MapleData(myNode);
        return ret;
    }

    public List<MapleData> getChildren() {
        List<MapleData> ret = new ArrayList<>();
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                MapleData child = new MapleData(childNode);
                ret.add(child);
            }
        }
        return ret;
    }

    public Object getData() {
        NamedNodeMap attributes = node.getAttributes();
        MapleDataType type = getType();
        switch (type) {
            case DOUBLE:
            case FLOAT:
            case INT:
            case SHORT:
            case STRING:
            case UOL: {
                String value = attributes.getNamedItem("value").getNodeValue();
                switch (type) {
                    case DOUBLE:
                        return Double.valueOf(Double.parseDouble(value));
                    case FLOAT:
                        return Float.valueOf(Float.parseFloat(value));
                    case INT:
                        return Integer.valueOf(Integer.parseInt(value));
                    case SHORT:
                        return Short.valueOf(Short.parseShort(value));
                    case STRING:
                    case UOL:
                        return value;
                }
            }
            case VECTOR: {
                String x = attributes.getNamedItem("x").getNodeValue();
                String y = attributes.getNamedItem("y").getNodeValue();
                return new Point(Integer.parseInt(x), Integer.parseInt(y));
            }
            case CANVAS: {
                throw new UnsupportedOperationException("Maple Canvas has been truncated.");
            }
        }
        return null;
    }

    public MapleDataType getType() {
        String nodeName = node.getNodeName();
        switch (nodeName) {
            case "imgdir":
                return MapleDataType.PROPERTY;
            case "canvas":
                return MapleDataType.CANVAS;
            case "convex":
                return MapleDataType.CONVEX;
            case "sound":
                return MapleDataType.SOUND;
            case "uol":
                return MapleDataType.UOL;
            case "double":
                return MapleDataType.DOUBLE;
            case "float":
                return MapleDataType.FLOAT;
            case "int":
                return MapleDataType.INT;
            case "short":
                return MapleDataType.SHORT;
            case "string":
                return MapleDataType.STRING;
            case "vector":
                return MapleDataType.VECTOR;
            case "null":
                return MapleDataType.IMG_0x00;
        }
        return null;
    }

    public MapleData getParent() {
        Node parentNode = node.getParentNode();
        if (parentNode.getNodeType() == Node.DOCUMENT_NODE) {
            return null;
        }
        MapleData parentData = new MapleData(parentNode);
        return parentData;
    }

    public String getName() {
        return node.getAttributes().getNamedItem("name").getNodeValue();
    }

    @Override
    public Iterator<MapleData> iterator() {
        return getChildren().iterator();
    }

    public static String getString(MapleData data) {
        if (data.getType() == MapleDataType.STRING) {
            return ((String) data.getData());
        } else {
            return String.valueOf(getInt(data));
        }
    }

    public static String getString(MapleData data, String def) {
        if (data == null || data.getData() == null) {
            return def;
        }
        return getString(data);
    }

    public static int getInt(MapleData data) {
        if (data.getType() == MapleDataType.STRING) {
            return Integer.parseInt(getString(data));
        } else if (data.getType() == MapleDataType.SHORT) {
            return Integer.valueOf(((Short) data.getData()).shortValue());
        } else {
            return ((Integer) data.getData()).intValue();
        }
    }

    public static int getInt(MapleData data, int def) {
        if (data == null || data.getData() == null) {
            return def;
        }
        return getInt(data);
    }

    public static int evaluate(MapleData data, String variables, int target) {
        if (data == null) {
            return -1;
        }
        String d = getString(data).toLowerCase();
        if(d.contains("\\r\\n")) {
            d = d.replace("\\r\\n", "");
        }
        if(d.endsWith("u") || d.endsWith("y")) {
            d = d.substring(0, d.length() - 1) + "x";
        } else if (d.endsWith("%")) {
            d = d.substring(0, d.length() - 1);
        }

        d = d.replace(variables, String.valueOf(target));
        if (d.substring(0, 1).equals("-")) {
            if (d.substring(1, 2).equals("u") || d.substring(1, 2).equals("d")) {
                d = "n(" + d.substring(1, d.length()) + ")"; // n(u(x/2))
            } else {
                d = "n" + d.substring(1, d.length()); // n30+3*x
            }
        } else if (d.substring(0, 1).equals("=")) { // lol nexon and their mistakes
            d = d.substring(1, d.length());
        }
        return (int) (new Eval(d).evaluate());
    }
}
