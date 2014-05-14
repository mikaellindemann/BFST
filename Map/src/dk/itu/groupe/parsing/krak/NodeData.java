package dk.itu.groupe.parsing.krak;

/**
 * An object storing the raw node data from the krak data file.
 *
 * @author Peter Bindslev (plil@itu.dk), Rune Henriksen (ruju@itu.dk) &amp;
 * Mikael Jepsen (mlin@itu.dk)
 */
public class NodeData
{

    public final int ID;
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
}
