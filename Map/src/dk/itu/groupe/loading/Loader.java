package dk.itu.groupe.loading;

import dk.itu.groupe.Coastline;
import dk.itu.groupe.Edge;
import dk.itu.groupe.Node;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
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
    public void load(String nodeFile, String edgeFile, Map<Long, Node> nodeMap) throws IOException
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
            processEdge(new Edge(line, nodeMap));
        }

        loadCl("./res/data/coastline/");
    }

    private void loadCl(String dir) throws IOException
    {
        Map<Long, Node> coastlinemap = new HashMap<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(dir + "nodes.csv")));
        //br.readLine();
        String line;
        while ((line = br.readLine()) != null) {
            Node n = new Node(line);
            coastlinemap.put(n.ID, n);
        }
        br.close();

        br = new BufferedReader(new InputStreamReader(new FileInputStream(dir + "edges.csv")));
        //br.readLine();
        while ((line = br.readLine()) != null) {
            DataLine l = new DataLine(line);
            List<Node> nodes = new LinkedList<>();
            while (l.hasNext()) {
                Node lo = coastlinemap.get(l.getLong());
                assert lo != null;
                nodes.add(lo);
            }
            processLand(new Coastline(nodes.toArray(new Node[0])));
        }
    }
}
