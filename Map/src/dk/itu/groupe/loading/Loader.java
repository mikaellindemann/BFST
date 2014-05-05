package dk.itu.groupe.loading;

import dk.itu.groupe.data.CommonRoadType;
import dk.itu.groupe.data.Edge;
import dk.itu.groupe.data.Node;
import dk.itu.groupe.data.OneWay;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;

/**
 * Parse Krak data files (kdv_node_unload.txt, kdv_unload.txt).
 *
 * Customize to your needs by overriding processNode and processEdge. See
 * example in main.
 *
 * Original author Peter Tiedemann petert@itu.dk; updates (2014) by Søren
 * Debois, debois@itu.dk; changes (2014) by Peter, Rune and Mikael
 */
public abstract class Loader
{

    private final Map<Integer, CommonRoadType> rtMap;

    public Loader()
    {
        rtMap = new HashMap<>();
        for (CommonRoadType rt : CommonRoadType.values()) {
            rtMap.put(rt.getTypeNo(), rt);
        }
    }

    /**
     * This method is called when a node has been instantiated.
     *
     * @param nd The <code>Node</code> to process.
     */
    public abstract void processNode(Node nd);

    /**
     * This method is called when an edge has been instantiated.
     *
     * @param ed The <code>Edge</code> to process.
     */
    public abstract void processEdge(Edge ed);

    public abstract void processLand(Edge cl);

    //public abstract void processWater(Coastline cl);
    /**
     * Load krak-data from given files, invoking processNode and processEdge
     * once for each node- and edge- specification in the input file,
     * respectively.
     *
     * @param nodeFile The path to the file containing the nodes.
     * @param edgeFile The path to the file containing the edges.
     * @param nodeMap The nodemap to use for looking up nodes in the process of
     * creating the edges.
     */
    public void loadMap(String nodeFile, String edgeFile, Node[] nodeMap)
    {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(nodeFile), Charset.forName("UTF-8")));
            br.readLine(); // First line is column names, not data.

            String line;
            while ((line = br.readLine()) != null) {
                processNode(new Node(line));
            }

            br = new BufferedReader(new InputStreamReader(new FileInputStream(edgeFile), Charset.forName("UTF-8")));
            br.readLine(); // Again, first line is column names, not data.

            while ((line = br.readLine()) != null) {
                DataLine dl = new DataLine(line);
                int typ = dl.getInt();
                CommonRoadType type = rtMap.get(typ);
                if (type == null) {
                    System.err.println(typ);
                    assert (type != null);
                }
                String roadname = dl.getString();
                float length = dl.getFloat();
                int exitNumber = dl.getInt();
                int speedLimit = dl.getInt();
                float driveTime = dl.getFloat();
                OneWay oneWay;
                switch (dl.getInt()) {
                    case -1:
                        oneWay = OneWay.TO_FROM;
                        break;
                    case 0:
                        oneWay = OneWay.NO;
                        break;
                    case 1:
                        oneWay = OneWay.FROM_TO;
                        break;
                    default:
                        oneWay = OneWay.NO;
                        System.err.println("Assuming no restrictions on edge.");
                }
                Node[] nodes = new Node[dl.tokensLeft()];
                for (int i = 0; dl.hasNext(); i++) {
                    nodes[i] = nodeMap[dl.getInt()];
                    assert nodes[i] != null;
                }
                assert nodes[nodes.length - 1] != null;
                processEdge(new Edge(type, roadname, length, exitNumber, speedLimit, driveTime, oneWay, nodes));
                assert !dl.hasNext();
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    "An unexpected error has occured.\nThis program will exit.",
                    "Error loading",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace(System.err);
            System.exit(300);
        }
    }

    public void loadCoastline(String dir)
    {
        try {
            Map<Integer, Node> coastlinemap = new HashMap<>();
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(dir + "nodes.csv")));
            String line;
            while ((line = br.readLine()) != null) {
                Node n = new Node(line);
                coastlinemap.put(n.id(), n);
            }
            br.close();

            br = new BufferedReader(new InputStreamReader(new FileInputStream(dir + "edges.csv")));
            while ((line = br.readLine()) != null) {
                DataLine l = new DataLine(line);
                List<Node> nodes = new LinkedList<>();
                while (l.hasNext()) {
                    Node lo = coastlinemap.get(l.getInt());
                    assert lo != null;
                    nodes.add(lo);
                }
                processLand(new Edge(nodes.toArray(new Node[0])));
            }
            coastlinemap.clear();
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }

    public static Info loadInfo(String dir)
    {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(dir + "info.csv")));
            return new Info(Double.parseDouble(br.readLine()),
                    Double.parseDouble(br.readLine()),
                    Double.parseDouble(br.readLine()),
                    Double.parseDouble(br.readLine()),
                    Integer.parseInt(br.readLine()),
                    Integer.parseInt(br.readLine()));
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
        throw new RuntimeException("Bad shit happens!");
    }

    public static class Info
    {

        public final double xLow, yLow, xHigh, yHigh;
        public final int maxNodes, maxEdges;

        public Info(double xLow, double yLow, double xHigh, double yHigh, int maxNodes, int maxEdges)
        {
            this.xLow = xLow;
            this.yLow = yLow;
            this.xHigh = xHigh;
            this.yHigh = yHigh;
            this.maxNodes = maxNodes;
            this.maxEdges = maxEdges;
        }

    }
}
