package dk.itu.groupe.parsing.osm;

import de.jotschi.geoconvert.GeoConvert;
import dk.itu.groupe.data.OneWay;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    private Set<Node> nodeRef, ferryRef;
    private Set<Edge> edgeSet;
    // This stores the nodes so they can be looked up by their ID.
    private Map<Long, Node> nodemap;

    private OSMRoadType placeType;
    private String placeName;

    double xMin, xMax, yMin, yMax;

    private long nodeIdNew;

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
        ferryRef = new HashSet<>();
        nodeRef = new HashSet<>();
        edgeSet = new HashSet<>();
        rtMap = new HashMap<>();
        for (OSMRoadType rt : OSMRoadType.values()) {
            for (String s : rt.getOSMTypes()) {
                rtMap.put(s, rt);
            }
        }
        nodeList = new ArrayList<>();
        nodemap = new HashMap<>();
    }

    @Override
    public void endDocument()
    {
        new File("./res/data/osm").mkdirs();

        //Clear memory (shouldn't be necessary but looks good)
        resetInterner();

        try (DataOutputStream nodeStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream("./res/data/osm/nodes.bin")))) {
            // For each of the used nodeIDs:
            for (Node n : nodeRef) {
                if (!n.isMarked()) {
                    System.err.println(n);
                    assert (n.isMarked());
                }
                double x = n.getPoint().x;
                double y = n.getPoint().y;
                xMin = Math.min(x, xMin);
                xMax = Math.max(x, xMax);
                yMin = Math.min(y, yMin);
                yMax = Math.max(y, yMax);
                ferryRef.remove(n);
                nodeStream.writeInt((int) n.getId());
                nodeStream.writeFloat((float)x);
                nodeStream.writeFloat((float)y);
            }
            for (Node n : ferryRef) {
                assert n.isMarked();
                nodeStream.writeInt((int) n.getId());
                nodeStream.writeFloat((float)n.getPoint().x);
                nodeStream.writeFloat((float)n.getPoint().y);
            }
            nodeStream.close();
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }

        try {
            Map<OSMRoadType, DataOutputStream> edgeStreams = new HashMap<>();
            for (OSMRoadType rt : OSMRoadType.values()) {
                edgeStreams.put(rt, new DataOutputStream(new BufferedOutputStream(new FileOutputStream("./res/data/osm/edges" + rt.getTypeNo() + ".bin"))));
            }
            for (Edge e : edgeSet) {
                writeEdge(e, edgeStreams.get(e.getType()));
            }
            for (DataOutputStream edgeStream : edgeStreams.values()) {
                edgeStream.close();
            }
        } catch (IOException ex) {
            // This happens if the printwriters are about to write a file to a nonexisting folder.
            // Which shouldn't happen because the folders are created on document-start.
            ex.printStackTrace(System.err);
        }

        try (DataOutputStream info = new DataOutputStream(new BufferedOutputStream(new FileOutputStream("./res/data/osm/info.bin")))) {
            info.writeDouble(xMin);
            info.writeDouble(yMin);
            info.writeDouble(xMax);
            info.writeDouble(yMax);
            info.writeInt(nodeRef.size() + ferryRef.size());
            info.writeInt(numberOfEdges);
            info.close();
        } catch (IOException ex) {
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
                    n.setNewId(nodeIdNew++);
                    n.addEdge();
                    assert n.isMarked();
                    edgeSet.add(new Edge(placeType, placeName, 0, 0, OneWay.NO, new long[]{nodeID, nodeID}));
                    nodeRef.add(n);
                }
                resetElement();
                break;
            case "way":
                if (way) {
                    saveEdge();
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

    private void saveEdge()
    {
        long[] nodeIds = new long[nodeList.size()];
        for (int i = 0; i < nodeList.size(); i++) {
            long n = nodeList.get(i);
            Node node = nodemap.get(n);
            if (!node.isMarked()) {
                node.setNewId(nodeIdNew++);
            }
            if (i != 0 && i != nodeList.size() - 1) {
                node.addEdge();
            }
            node.addEdge();
            nodeIds[i] = n;
            if (roadType == OSMRoadType.FERRY) {
                ferryRef.add(node);
            } else {
                nodeRef.add(node);
            }
        }
        edgeSet.add(new Edge(roadType, name, exitNumber, speedLimit, oneWay, nodeIds));
    }

    public void writeEdge(Edge edge, DataOutputStream edgeStream) throws IOException
    {
        long[] nodeIds = edge.getNodeIds();
        Node[] nodes = new Node[nodeIds.length];
        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = nodemap.get(nodeIds[i]);
            assert nodes[i] != null;
        }
        int lastSplitIndex = 0;
        int index = 1;
        while (index < nodes.length) {
            while (!nodes[index].split() && index != nodes.length - 1) {
                index++;
            }
            double length = 0;
            for (int i = lastSplitIndex + 1; i <= index; i++) {
                length += nodes[i - 1].getPoint().distance(nodes[i].getPoint());
            }
            //edgeStream.println("TYPE,VEJNAVN,LÆNGDE,FRAKOERSEL,SPEED,DRIVETIME,ONEWAY,NODES...");
            edgeStream.writeInt(edge.getType().getTypeNo());
            if (edge.getRoadname() == null) {
                edgeStream.writeUTF("");
            } else {
                edgeStream.writeUTF(edge.getRoadname());
            }
            edgeStream.writeFloat((float)length);
            edgeStream.writeFloat((float)((length / (edge.getSpeedLimit() * 1000 / 60)) * 1.15));
            edgeStream.writeInt(edge.getOneWay().getNumber());
            edgeStream.writeInt(index + 1 - lastSplitIndex);
            for (int i = lastSplitIndex; i <= index; i++) {
                edgeStream.writeInt((int) nodes[i].getId());
            }
            numberOfEdges++;
            lastSplitIndex = index;
            index++;
        }
    }
}
