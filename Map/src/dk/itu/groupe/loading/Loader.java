package dk.itu.groupe.loading;

import dk.itu.groupe.Coastline;
import dk.itu.groupe.CommonRoadType;
import dk.itu.groupe.Edge;
import dk.itu.groupe.Node;
import dk.itu.groupe.OneWay;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
     * @param wholeEdge Tells the receiver if there has been read a complete
     * edge from the datafile.
     */
    public abstract void processEdge(Edge ed, boolean wholeEdge);

    public abstract void processLand(Coastline cl);

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
     * @throws IOException if there is a problem reading data or the files don't
     * exist
     */
    public void load(String nodeFile, String edgeFile, Map<Integer, Node> nodeMap) throws IOException
    {
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
            int id = dl.getInt();
            int typ = dl.getInt();
            CommonRoadType type = rtMap.get(typ);
            if (type == null) {
                System.err.println(typ);
                assert (type != null);
            }
            String roadname = dl.getString();
            double length = dl.getDouble();
            int exitNumber = dl.getInt();
            int speedLimit = dl.getInt();
            double driveTime = dl.getDouble();
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
            List<Node> nodeList = new ArrayList<>();
            while (dl.hasNext()) {
                nodeList.add(nodeMap.get(dl.getInt()));
            }
            if (nodeList.size() > 1) {
                for (int i = 1; i < nodeList.size(); i++) {
                    if (i == nodeList.size() - 1) {
                        processEdge(new Edge(id, type, roadname, length, exitNumber, speedLimit, driveTime, oneWay, nodeList.get(i - 1), nodeList.get(i)), true);
                    } else {
                        processEdge(new Edge(id, type, roadname, length, exitNumber, speedLimit, driveTime, oneWay, nodeList.get(i - 1), nodeList.get(i)), false);
                    }
                }
            } else {
                processEdge(new Edge(id, type, roadname, length, exitNumber, speedLimit, driveTime, oneWay, nodeList.get(0), nodeList.get(0)), true);
            }
        }
        loadCl("./res/data/coastline/");
    }

    private void loadCl(String dir) throws IOException
    {
        Map<Integer, Node> coastlinemap = new HashMap<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(dir + "nodes.csv")));
        String line;
        while ((line = br.readLine()) != null) {
            Node n = new Node(line);
            coastlinemap.put(n.ID, n);
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
            processLand(new Coastline(nodes.toArray(new Node[0])));
        }
    }
}
