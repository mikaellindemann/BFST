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
    public final int ID;
    public final double X_COORD;
    public final double Y_COORD;

    /**
     * Parses node data from line, throws an IOException if something unexpected
     * is read
     *
     * @param line The source line from which the Node fields are parsed
     */
    public Node(String line)
    {
        DataLine dl = new DataLine(line);
        ID = dl.getInt();
        X_COORD = dl.getDouble();
        Y_COORD = dl.getDouble();
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
        return ID + "," + X_COORD + "," + Y_COORD;
    }
}
