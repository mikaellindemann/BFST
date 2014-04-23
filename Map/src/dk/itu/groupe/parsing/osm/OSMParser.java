package dk.itu.groupe.parsing.osm;

import de.jotschi.geoconvert.GeoConvert;
import dk.itu.groupe.OneWay;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Mikael
 */
public class OSMParser extends DefaultHandler
{

    //Used for fast lookup from an OSM-roadtype-tag to the OSMRoadType-enum.
    private static Map<String, OSMRoadType> rtMap;
    // Used to limit the amount of RAM that is to be consumed by roadnames.
    private static final Map<String, String> interner = new HashMap<>();

    //Node fields:
    private long nodeID;
    private double lat;
    private double lon;

    //Edge fields:
    private long edgeID;
    private OSMRoadType roadType;
    private String name;
    private int exitNumber;
    private int speedLimit;
    private OneWay oneWay;
    private boolean area;

    private boolean edge, way, coastline;
    private int numberOfEdges;
    // List used to connect a single edge in the OSM-file.
    private List<Long> nodeList;
    // If this set contains a nodeID it has been used for an edge.
    private Set<Long> nodeRef, coastlineRef;
    // This stores the nodes so they can be looked up by their ID.
    private Map<Long, Node> nodemap;
    private Set<Edge> placeEdges;

    private OSMRoadType placeType;
    private String placeName;
    private long lastNodeId;

    // OutputStreams.
    private PrintWriter edgeStream;
    private PrintWriter nodeStream;
    private PrintWriter coastlineNodeStream;
    private PrintWriter coastlineStream;

    private static File f;

    public static void main(String[] args) throws Exception
    {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            ex.printStackTrace(System.err);
        }
        selectFile();
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        SAXParser saxParser = spf.newSAXParser();
        XMLReader xmlReader = saxParser.getXMLReader();
        xmlReader.setContentHandler(new OSMParser());
        assert (f != null && f.exists());
        xmlReader.parse(f.getAbsolutePath());
    }

    private static void selectFile()
    {
        JFileChooser j = new JFileChooser();
        j.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter fnef = new FileNameExtensionFilter("OpenStreetMap XML-file (.osm)", "osm");
        j.setFileFilter(fnef);
        j.setVisible(true);
        int status = j.showDialog(null, "Parse");
        switch (status) {
            case JFileChooser.CANCEL_OPTION:
                System.exit(0);
            case JFileChooser.APPROVE_OPTION:
                f = j.getSelectedFile();
                break;
            case JFileChooser.ERROR_OPTION:
            default:
                System.err.println("Error picking file. Exiting...");
                System.exit(404);
        }
    }

    @Override
    public void startDocument()
    {
        // Initializes all the lists and maps that are used to parse the OSM-file.
        // It also writes the first lines containing information about fields.
        nodeRef = new TreeSet<>();
        coastlineRef = new TreeSet<>();
        placeEdges = new HashSet<>();
        rtMap = new HashMap<>();
        for (OSMRoadType rt : OSMRoadType.values()) {
            for (String s : rt.getOSMTypes()) {
                rtMap.put(s, rt);
            }
        }
        nodeList = new LinkedList<>();
        nodemap = new TreeMap<>();

        try {
            new File("./res/data/osm").mkdirs();
            new File("./res/data/coastline").mkdirs();
            edgeStream = new PrintWriter("./res/data/osm/edges.csv");
            edgeStream.println("ID,TYPE,VEJNAVN,FRAKOERSEL,SPEED,ONE_WAY,NODES...");
            coastlineStream = new PrintWriter("./res/data/coastline/edges.csv");
            coastlineStream.println("AREA,NODES...");
        } catch (FileNotFoundException ex) {
            // This happens if the printwriters are about to write a file to a nonexisting folder.
            // Which shouldn't happen because the folders are created on document-start.
            ex.printStackTrace(System.err);
        }
    }

    @Override
    public void endDocument()
    {
        try {
            nodeStream = new PrintWriter("./res/data/osm/nodes.csv");
            coastlineNodeStream = new PrintWriter("./res/data/coastline/nodes.csv");
            coastlineNodeStream.println("id,x,y");
            nodeStream.println("id,x,y");
        } catch (FileNotFoundException ex) {
            ex.printStackTrace(System.err);
        }

        double xMin, xMax, yMin, yMax;
        xMin = yMin = Double.MAX_VALUE;
        xMax = yMax = Double.MIN_VALUE;
        //Clear memory (shouldn't be necessary but looks good)
        resetInterner();
        // For each of the used nodeIDs:
        for (long id : nodeRef) {
            // This should always return true, but I havent checked.
            if (nodemap.containsKey(id)) {
                // Write the node to the node-file.
                nodeStream.println(nodemap.get(id).toString());
            }
        }
        for (long id : coastlineRef) {
            // This should always return true, but I havent checked.
            if (nodemap.containsKey(id)) {
                // Write the node to the coastline-node-file.
                Node nd = nodemap.get(id);
                Point2D p = nd.getPoint();
                double x = p.getX();
                double y = p.getY();
                xMin = Math.min(x, xMin);
                xMax = Math.max(x, xMax);
                yMin = Math.min(y, yMin);
                yMax = Math.max(y, yMax);
                coastlineNodeStream.println(nd.toString());
            }
        }

        for (Edge ed : placeEdges) {
            edgeStream.println(ed.toString());
            numberOfEdges++;
            nodeRef.add(ed.getNodeIds()[0]);
        }
        // Close the streams to make sure all data has been flushed to the files.
        nodeStream.close();
        coastlineNodeStream.close();
        edgeStream.close();
        coastlineStream.close();

        try (PrintWriter info = new PrintWriter("./res/data/osm/info.csv")) {
            info.println(xMin);
            info.println(yMin);
            info.println(xMax);
            info.println(yMax);
            info.println(nodeRef.size());
            info.println(numberOfEdges);
            info.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace(System.err);
        }

    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts)
    {
        switch (localName) {
            case "node":
                nodeID = Long.parseLong(atts.getValue("id"));
                lastNodeId = nodeID;
                lat = Double.parseDouble(atts.getValue("lat"));
                lon = Double.parseDouble(atts.getValue("lon"));
                break;
            case "way":
                edgeID = Long.parseLong(atts.getValue("id"));
                // The ID must be different from 0.
                assert edgeID != 0;

                // If something is left in the nodeList, it is a mistake.
                assert nodeList.isEmpty();
                edge = true;
                break;
            case "tag":
                if (edge) {
                    getWayInfo(atts);
                } else {
                    switch (atts.getValue("k")) {
                        case "name":
                            placeName = atts.getValue("v");
                            if(placeType != null) {
                                placeEdges.add(new Edge(-1, placeType, placeName, 0, 0, OneWay.NO, new long[]{lastNodeId}));
                                nodeRef.add(lastNodeId);
                            }
                            break;
                        case "place":
                            placeType = OSMRoadType.PLACES;
                            if (placeName != null) {
                                placeEdges.add(new Edge(-1, placeType, placeName, 0, 0, OneWay.NO, new long[]{lastNodeId}));
                                nodeRef.add(lastNodeId);
                            }
                            break;
                    }
                }
                break;
            case "nd":
                nodeList.add(Long.parseLong(atts.getValue("ref")));
                break;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName)
    {
        switch (localName) {
            case "node":
                double[] xy = new double[2];
                GeoConvert.LatLonToUTMXY(GeoConvert.DegToRad(lat), GeoConvert.DegToRad(lon), 32, xy);
                Node n = new Node(nodeID, xy[0], xy[1]);
                //Write to nodes.
                assert (n.getId() != 0);
                nodemap.put(n.getId(), n);
                resetElement();
                break;
            case "way":
                if (way) {
                    writeEdge();
                    resetElement();
                }
                nodeList.clear();
                break;
        }
    }

    private void resetElement()
    {
        way = false;
        edge = false;
        roadType = null;
        lat = 0;
        lon = 0;
        nodeID = 0;
        edgeID = 0;
        name = null;
        exitNumber = 0;
        speedLimit = 0;
        coastline = false;
        area = false;
        oneWay = OneWay.NO;
        placeType = null;
        placeName = null;
    }

    private static String intern(String s)
    {
        if (!interner.containsKey(s)) {
            interner.put(s, s);
        }
        return interner.get(s);
    }

    private static void resetInterner()
    {
        interner.clear();
    }

    private void getWayInfo(Attributes atts)
    {
        switch (atts.getValue("k")) {
            case "highway":
                roadType = rtMap.get(atts.getValue("v"));
                if (roadType != null) {
                    way = true;
                }
                break;
            case "route":
                if (atts.getValue("v").equals("ferry")) {
                    roadType = OSMRoadType.FERRY;
                    way = true;
                }
                break;
            case "name":
            case "addr:street":
            case "kms:street_name":
                if (name == null) {
                    name = intern(atts.getValue("v"));
                }
                break;
            case "maxspeed":
                if (atts.getValue("v").matches("[0-9]+")) {
                    speedLimit = Integer.parseInt(atts.getValue("v"));
                }
                break;
            case "oneway":
                switch (atts.getValue("v")) {
                    case "yes":
                        oneWay = OneWay.FROM_TO;
                        break;
                    case "no":
                        oneWay = OneWay.NO;
                        break;
                    case "-1":
                        oneWay = OneWay.TO_FROM;
                        break;
                }
                break;
            case "natural":
                if (atts.getValue("v").equals("coastline")) {
                    coastline = true;
                    way = true;
                }/* else if (atts.getValue("v").equals("water")) {
                 coastline = true;
                 way = true;
                 }*/

                break;
            case "area":
                if (atts.getValue("v").equals("yes")) {
                    area = true;
                }
                break;
        }
    }

    private void writeEdge()
    {
        long[] nodeIds = new long[nodeList.size()];
        // Copy and mark the nodes used.
        for (int i = 0; i < nodeIds.length; i++) {
            long node = nodeList.get(i);
            // If nodemap does not contain the key, something went very wrong.
            assert (nodemap.containsKey(node));
            nodeIds[i] = node;
            if (coastline) {
                coastlineRef.add(node);
            } else {
                nodeRef.add(node);
            }
        }
        if (!coastline) {
            Edge ed = new Edge(edgeID, roadType, name, exitNumber, speedLimit, oneWay, nodeIds);
            edgeStream.println(ed.toString());
            numberOfEdges++;
        } else {
            String s = "";
            if (area) {
                s += "1";
            } else {
                s += "0";
            }
            for (long node : nodeIds) {
                s += "," + node;
            }
            coastlineStream.println(s);
        }
    }
}
