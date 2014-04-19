package dk.itu.groupe.parsing.osm;

import de.jotschi.geoconvert.GeoConvert;
import dk.itu.groupe.OneWay;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
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

    private boolean edge, way, coastline;
    private int numberOfEdges;
    // List used to connect a single edge in the OSM-file.
    private List<Long> nodeList;
    // If this set contains a nodeID it has been used for an edge.
    private Set<Long> nodeRef;
    // This stores the nodes so they can be looked up by their ID.
    private Map<Long, Node> nodemap;

    // OutputStreams.
    private PrintWriter edgeStream;
    private PrintWriter nodeStream;
    private PrintWriter coastlineStream;

    public static void main(String[] args) throws Exception
    {
        File f = null;
        JFileChooser j = new JFileChooser();
        j.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter fnef = new FileNameExtensionFilter("OpenStreetMap XML-file", "osm", "xml");
        j.setFileFilter(fnef);
        j.setVisible(true);
        int status = j.showOpenDialog(null);
        switch (status) {
            case JFileChooser.CANCEL_OPTION:
                return;
            case JFileChooser.APPROVE_OPTION:
                f = j.getSelectedFile();
                break;
            case JFileChooser.ERROR_OPTION:
            default:
                System.exit(404);

        }
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        SAXParser saxParser = spf.newSAXParser();
        XMLReader xmlReader = saxParser.getXMLReader();
        xmlReader.setContentHandler(new OSMParser());
        xmlReader.parse(f.getAbsolutePath());
    }

    @Override
    public void startDocument()
    {
        // Initializes all the lists and maps that are used to parse the OSM-file.
        // It also writes the first lines containing information about fields.
        nodeRef = new TreeSet<>();
        rtMap = new HashMap<>();
        for (OSMRoadType rt : OSMRoadType.values()) {
            for (String s : rt.getOSMTypes()) {
                rtMap.put(s, rt);
            }
        }
        nodeList = new LinkedList<>();
        nodemap = new TreeMap<>();

        try {
            edgeStream = new PrintWriter("./res/data/osm/edges.csv");
            edgeStream.println("FNODE,TNODE,LENGTH,ID,TYPE,VEJNAVN,FRAKOERSEL,SPEED,DRIVETIME,ONE_WAY");
            coastlineStream = new PrintWriter("./res/data/osm/coastline.csv");
            coastlineStream.println("FNODE,TNODE");
        } catch (FileNotFoundException ex) {
            // This happens if the printwriters are about to write a file to a nonexisting folder.
            ex.printStackTrace(System.err);
        }
    }

    @Override
    public void endDocument()
    {
        try {
            nodeStream = new PrintWriter("./res/data/osm/nodes.csv");
            nodeStream.println("id,x,y");
        } catch (FileNotFoundException ex) {
            ex.printStackTrace(System.err);
        }

        double xMin, xMax, yMin, yMax;
        xMin = yMin = Double.MAX_VALUE;
        xMax = yMax = Double.MIN_VALUE;
        int numberOfNodes = 0;
        //Clear memory (shouldn't be necessary but looks good)
        resetInterner();
        // For each of the used nodeIDs:
        for (long id : nodeRef) {
            // This should always return true, but I havent checked.
            if (nodemap.containsKey(id)) {
                // Write the node to the node-file.
                Node nd = nodemap.get(id);
                Point2D p = nd.getPoint();
                double x = p.getX();
                double y = p.getY();
                if (x < xMin) {
                    xMin = x;
                } else if (x > xMax) {
                    xMax = x;
                }
                if (y < yMin) {
                    yMin = y;
                } else if (y > yMax) {
                    yMax = y;
                }
                nodeStream.println(nd.toString());
                numberOfNodes++;
            }
        }
        // Close the streams to make sure all data has been flushed to the files.
        nodeStream.close();
        edgeStream.close();
        coastlineStream.close();

        try (PrintWriter info = new PrintWriter("./res/data/osm/info.csv")) {
            info.println(xMin);
            info.println(yMin);
            info.println(xMax);
            info.println(yMax);
            info.println(numberOfNodes);
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
        oneWay = OneWay.NO;
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
            case "natural":
                if (atts.getValue("v").equals("coastline")) {
                    coastline = true;
                    way = true;
                }
        }
    }

    private void writeEdge()
    {
        Node fnode;
        Node tnode = null;
        for (long id : nodeList) {
            if (!nodemap.containsKey(id)) {
                System.err.println("A node has been used without existing??!?");
                continue;
            }
            fnode = tnode;
            tnode = nodemap.get(id);
            assert tnode != null;
            if (fnode == null) {
                continue;
            }
            if (name != null) {
                nodeRef.add(fnode.getId());
                nodeRef.add(tnode.getId());
                if (!coastline) {
                    Edge ed = new Edge(fnode, tnode, edgeID, roadType, name, exitNumber, speedLimit, oneWay);
                    edgeStream.println(ed.toString());
                    numberOfEdges++;
                } else {
                    coastlineStream.println(fnode.getId() + "," + tnode.getId());
                }
            }
        }
    }
}
