package dk.itu.groupe.parsing.krak;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
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
public abstract class KrakLoader
{

    /**
     * This method is called when a node has been instantiated.
     *
     * @param nd The <code>Node</code> to process.
     */
    public abstract void processNode(NodeData nd);

    /**
     * This method is called when an edge has been instantiated.
     *
     * @param ed The <code>Edge</code> to process.
     */
    public abstract void processEdge(EdgeData ed);

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
    public void load(String nodeFile, String edgeFile, Map<Integer, NodeData> nodeMap) throws IOException
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(nodeFile), Charset.forName("UTF-8")));
        br.readLine(); // First line is column names, not data.

        String line;
        while ((line = br.readLine()) != null) {
            processNode(new NodeData(line));
        }

        br = new BufferedReader(new InputStreamReader(new FileInputStream(edgeFile), Charset.forName("ISO-8859-1")));
        br.readLine(); // Again, first line is column names, not data.

        while ((line = br.readLine()) != null) {
            processEdge(new EdgeData(line, nodeMap));
        }
    }
}
