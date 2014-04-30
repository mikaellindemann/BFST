package dk.itu.groupe.parsing.osm;

import java.awt.geom.Point2D;
import java.util.Locale;

/**
 * An object storing the raw node data from the parsed krak data file.
 *
 * @author Peter Bindslev (plil@itu.dk), Rune Henriksen (ruju@itu.dk) & Mikael
 * Jepsen (mlin@itu.dk)
 */
public class Node implements Comparable<Node>
{

    private long id;
    private final double x;
    private final double y;
    private boolean marked;

    public Node(long id, double x, double y)
    {
        marked = false;
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public long getId()
    {
        return id;
    }

    public Point2D.Double getPoint()
    {
        return new Point2D.Double(x, y);
    }

    public boolean isMarked()
    {
        return marked;
    }

    public void setNewId(long id)
    {
        if (!marked) {
            this.id = id;
            marked = true;
        }
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
        return id + ","
                + String.format(Locale.ENGLISH, "%.2f,", x)
                + String.format(Locale.ENGLISH, "%.2f", y);
    }

    @Override
    public int compareTo(Node that)
    {
        return (int) (this.id - that.id);
    }
}
