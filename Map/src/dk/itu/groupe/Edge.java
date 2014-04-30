package dk.itu.groupe;

import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

/**
 * Represents the raw data from a line in edges.csv.
 *
 * @author Peter Bindslev (plil@itu.dk), Rune Henriksen (ruju@itu.dk) & Mikael
 * Jepsen (mlin@itu.dk)
 */
public class Edge
{
    private final int id;
    private final CommonRoadType type;
    private final String roadname;
    private final double length;
    private final int exitNumber;
    private final int speedLimit;
    private final double driveTime;
    private final OneWay oneWay;
    private final Shape path;
    private Node from, to;
    private final double centerX, centerY;

    public Edge(Node[] nodes)
    {
        id = 0;
        type = null;
        roadname = null;
        length = 0;
        exitNumber = 0;
        speedLimit = 0;
        driveTime = 0;
        oneWay = null;
        Path2D p = new Path2D.Double();
        p.moveTo(nodes[0].x(), nodes[0].y());
        for (int i = 1; i < nodes.length; i++) {
            p.lineTo(nodes[i].x(), nodes[i].y());
        }
        p.closePath();
        path = new Area(p);
        Rectangle2D bounds = path.getBounds2D();
        centerX = bounds.getCenterX();
        centerY = bounds.getCenterY();
    }

    public Edge(int id, CommonRoadType type, String roadname, double length, int exitNumber, int speedLimit, double driveTime, OneWay oneWay, Node from, Node to)
    {
        this.id = id;
        this.type = type;
        this.roadname = roadname;
        this.length = length;
        this.exitNumber = exitNumber;
        this.speedLimit = speedLimit;
        this.driveTime = driveTime;
        this.oneWay = oneWay;
        this.from = from;
        this.to = to;
        path = new Line2D.Double(from.x(), from.y(), to.x(), to.y());
        centerX = (from.x() + to.x()) / 2;
        centerY = (from.y() + to.y()) / 2;
    }

    public CommonRoadType getType()
    {
        return type;
    }

    public Shape getShape()
    {
        return path;
    }

    public String getRoadname()
    {
        return roadname;
    }

    public Node from()
    {
        return from;
    }
    
    public Node to()
    {
        return to;
    }

    public double getWeight(boolean length)
    {
        if (length) {
            return this.length;
        }
        return driveTime;
    }

    public OneWay getOneWay()
    {
        return oneWay;
    }
    
    public Edge revert()
    {
        return new Edge(id, type, roadname, length, exitNumber, speedLimit, driveTime, oneWay, to, from);
    }

    public double getCenterX()
    {
        return centerX;
    }

    public double getCenterY()
    {
        return centerY;
    }
    
}
