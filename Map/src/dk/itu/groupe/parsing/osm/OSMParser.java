package dk.itu.groupe.parsing.osm;

import de.jotschi.geoconvert.GeoConvert;
import dk.itu.groupe.OneWay;
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

    private boolean edge, way;
    private int numberOfEdges;
    // List used to connect a single edge in the OSM-file.
    private List<Long> nodeList;
    // If this set contains a nodeID it has been used for an edge.
    private Set<Long> nodeRef, ferryRef;
    // This stores the nodes so they can be looked up by their ID.
    private Map<Long, Node> nodemap;
    private Set<Edge> placeEdges;

    private OSMRoadType placeType;
    private String placeName;
    private long lastNodeId;

    double xMin, xMax, yMin, yMax;

    private long nodeIdNew;

    // OutputStreams.
    private PrintWriter edgeStream;
    private PrintWriter nodeStream;

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
        nodeIdNew = 0;
        xMin = yMin = Double.MAX_VALUE;
        xMax = yMax = Double.MIN_VALUE;
        // Initializes all the lists and maps that are used to parse the OSM-file.
        // It also writes the first lines containing information about fields.
        ferryRef = new TreeSet<>();
        nodeRef = new TreeSet<>();
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
            edgeStream = new PrintWriter("./res/data/osm/edges.csv");
            edgeStream.println("ID,TYPE,VEJNAVN,FRAKOERSEL,SPEED,ONE_WAY,NODES...");
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
            nodeStream.println("id,x,y");
        } catch (FileNotFoundException ex) {
            ex.printStackTrace(System.err);
        }

        //Clear memory (shouldn't be necessary but looks good)
        resetInterner();
        // For each of the used nodeIDs:
        for (long id : nodeRef) {
            // This should always return true, but I havent checked.
            if (nodemap.containsKey(id)) {
                // Write the node to the node-file.
                Node n = nodemap.get(id);
                assert n.isMarked();
                double x = n.getPoint().x;
                double y = n.getPoint().y;
                xMin = Math.min(x, xMin);
                xMax = Math.max(x, xMax);
                yMin = Math.min(y, yMin);
                yMax = Math.max(y, yMax);
                nodeStream.println(n.toString());
            } else {
                throw new RuntimeException("ID wasn't found in the map");
            }
        }
        for (long id : ferryRef) {
            if (nodemap.containsKey(id)) {
                Node n = nodemap.get(id);
                assert n.isMarked();
                nodeStream.println(n.toString());
            } else {
                throw new RuntimeException("ID wasn't found in the map");
            }
        }

        for (Edge ed : placeEdges) {
            edgeStream.println(ed.toString());
            numberOfEdges++;
            nodeRef.add(ed.getNodeIds()[0]);
        }

        // Close the streams to make sure all data has been flushed to the files.
        nodeStream.close();

        edgeStream.close();

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
                            break;
                        case "place":
                            placeType = OSMRoadType.PLACES;
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
                if (placeType != null && placeName != null) {
                    Node nn = nodemap.get(lastNodeId);
                    nn.setNewId(nodeIdNew++);
                    placeEdges.add(new Edge(-1, placeType, placeName, 0, 0, 0, OneWay.NO, new long[]{nn.getId()}));
                    nodeRef.add(lastNodeId);
                }
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
        }
    }

    private void writeEdge()
    {
        long[] nodeIds = new long[nodeList.size()];
        // Copy and mark the nodes used.
        double length = 0;
        Node lastNode = null;
        for (int i = 0; i < nodeIds.length; i++) {
            long node = nodeList.get(i);
            // If nodemap does not contain the key, something went very wrong.
            assert (nodemap.containsKey(node));
            Node n = nodemap.get(node);
            if (!n.isMarked()) {
                n.setNewId(nodeIdNew++);
            }
            if (i > 0) {
                assert(lastNode != null);
                length += n.getPoint().distance(lastNode.getPoint());
            }
            lastNode = n;
            nodeIds[i] = n.getId();
            if (roadType == OSMRoadType.FERRY) {
                ferryRef.add(node);
            } else {
                nodeRef.add(node);
            }
        }
        Edge ed = new Edge(edgeID, roadType, name, length, exitNumber, speedLimit, oneWay, nodeIds);
        edgeStream.println(ed.toString());
        numberOfEdges++;
    }
}
