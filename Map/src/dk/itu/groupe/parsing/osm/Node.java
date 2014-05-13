package dk.itu.groupe.parsing.osm;

import java.awt.geom.Point2D;

/**
 * An object storing the raw node data from the parsed krak data file.
 *
 * @author Peter Bindslev (plil@itu.dk), Rune Henriksen (ruju@itu.dk) &amp;
 * Mikael Jepsen (mlin@itu.dk)
 */
public class Node implements Comparable<Node>
{

    private long id;
    private final double x;
    private final double y;
    private boolean marked;
    private int edgeCount;

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

    public void addEdge()
    {
        edgeCount++;
    }

    public boolean split()
    {
        return edgeCount > 2;
    }

    @Override
    public int compareTo(Node that)
    {
        return (int) (this.id - that.id);
    }
}
