package dk.itu.groupe;

import dk.itu.groupe.loading.DataLine;

/**
 * An object storing the raw node data from the parsed krak data file.
 *
 * @author Peter Bindslev (plil@itu.dk), Rune Henriksen (ruju@itu.dk) & Mikael
 * Jepsen (mlin@itu.dk)
 */
public class Node
{
    private final int id;
    private final double x;
    private final double y;

    /**
     * Parses node data from line, throws an IOException if something unexpected
     * is read
     *
     * @param line The source line from which the Node fields are parsed
     */
    public Node(String line)
    {
        DataLine dl = new DataLine(line);
        id = dl.getInt();
        x = dl.getDouble();
        y = dl.getDouble();
    }
    
    public int id()
    {
        return id;
    }
    
    public double x()
    {
        return x;
    }
    
    public double y()
    {
        return y;
    }

    /**
     * Returns a string representing the node data in the same format as used in
     * the nodes.csv file.
     *
     * @return
     */
    @Override
    public String toString()
    {
        return id + "," + x + "," + y;
    }
}
