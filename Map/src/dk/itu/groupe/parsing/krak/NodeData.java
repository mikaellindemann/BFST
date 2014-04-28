package dk.itu.groupe.parsing.krak;

import dk.itu.groupe.loading.DataLine;

/**
 * An object storing the raw node data from the krak data file.
 *
 * @author Peter Bindslev (plil@itu.dk), Rune Henriksen (ruju@itu.dk) & Mikael
 * Jepsen (mlin@itu.dk)
 */
public class NodeData
{

    //final int ARC;
    public final int ID;
    //final int KDV_ID;
    public final double X_COORD;
    public final double Y_COORD;

    /**
     * Parses node data from line, throws an IOException if something unexpected
     * is read
     *
     * @param line The source line from which the NodeData fields are parsed
     */
    public NodeData(String line)
    {
        DataLine dl = new DataLine(line);
        dl.getInt();
        ID = dl.getInt();
        dl.getInt();
        X_COORD = dl.getDouble();
        Y_COORD = dl.getDouble();
    }

    /**
     * Returns a string representing the node data in the same format as used in
     * the kdv_node_unload.txt file.
     *
     * @return
     */
    @Override
    public String toString()
    {
        return (ID - 1) + "," + X_COORD + "," + Y_COORD;
    }
}
