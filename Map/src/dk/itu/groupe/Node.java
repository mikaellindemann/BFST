package dk.itu.groupe;

import java.util.LinkedList;
import java.util.List;

/**
 * An object storing the raw node data from the krak data file.
 */
public class Node {

    //final int ARC;
    final int KDV;
    //final int KDV_ID;
    final double X_COORD;
    final double Y_COORD;
    private final List<Edge> edges;

    /**
     * Parses node data from line, throws an IOException if something unexpected
     * is read
     *
     * @param line The source line from which the NodeData fields are parsed
     */
    public Node(String line) {
        edges = new LinkedList<>();
        DataLine dl = new DataLine(line);
        dl.getInt();
        KDV = dl.getInt();
        dl.getInt();
        X_COORD = dl.getDouble();
        Y_COORD = dl.getDouble();
    }
    
    public void addEdge(Edge e)
    {
        edges.add(e);
    }
    
    public List<Edge> getEdges()
    {
        return edges;
    }

    /**
     * Returns a string representing the node data in the same format as used in
     * the kdv_node_unload.txt file.
     * @return 
     */
    @Override
    public String toString() {
        return KDV + "," + X_COORD + "," + Y_COORD;
    }
}
