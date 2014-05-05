package dk.itu.groupe.data;

import java.awt.Shape;
import java.awt.geom.Path2D;

/**
 * Represents the raw data from a line in edges.csv.
 *
 * @author Peter Bindslev (plil@itu.dk), Rune Henriksen (ruju@itu.dk) & Mikael
 * Jepsen (mlin@itu.dk)
 */
public class Edge
{

    private final CommonRoadType type;
    private final String roadname;
    private final float length;
    private final int exitNumber;
    private final int speedLimit;
    private final float driveTime;
    private final OneWay oneWay;
    private final Shape path;
    private Node[] nodes;
    private final float centerX, centerY;

    public Edge(Node[] nodes)
    {
        type = null;
        roadname = null;
        length = 0;
        exitNumber = 0;
        speedLimit = 0;
        driveTime = 0;
        oneWay = null;
        float xMin = nodes[0].x();
        float xMax = nodes[0].x();
        float yMin = nodes[0].y();
        float yMax = nodes[0].y();
        Path2D p = new Path2D.Float();
        p.moveTo(nodes[0].x(), nodes[0].y());
        for (int i = 1; i < nodes.length; i++) {
            xMin = Math.min(xMin, nodes[i].x());
            yMin = Math.min(yMin, nodes[i].y());
            xMax = Math.max(xMax, nodes[i].x());
            yMax = Math.max(yMax, nodes[i].y());
            p.lineTo(nodes[i].x(), nodes[i].y());
        }
        p.closePath();
        path = p;
        centerX = (xMin + xMax) / 2;
        centerY = (yMin + yMax) / 2;
    }

    public Edge(CommonRoadType type, String roadname, float length, int exitNumber, int speedLimit, float driveTime, OneWay oneWay, Node[] nodes)
    {
        this.type = type;
        this.roadname = roadname;
        this.length = length;
        this.exitNumber = exitNumber;
        this.speedLimit = speedLimit;
        this.driveTime = driveTime;
        this.oneWay = oneWay;
        this.nodes = nodes;
        float xMin = nodes[0].x();
        float xMax = nodes[0].x();
        float yMin = nodes[0].y();
        float yMax = nodes[0].y();
        Path2D p = new Path2D.Double();
        p.moveTo(nodes[0].x(), nodes[0].y());
        for (int i = 1; i < nodes.length; i++) {
            xMin = Math.min(xMin, nodes[i].x());
            yMin = Math.min(yMin, nodes[i].y());
            xMax = Math.max(xMax, nodes[i].x());
            yMax = Math.max(yMax, nodes[i].y());
            p.lineTo(nodes[i].x(), nodes[i].y());
        }
        path = p;
        centerX = (xMin + xMax) / 2;
        centerY = (yMin + yMax) / 2;
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

    public float getDriveTime()
    {
        return driveTime;
    }

    public Node from()
    {
        return nodes[0];
    }

    public Node to()
    {
        return nodes[nodes.length - 1];
    }

    public float getLength()
    {
        return length;
    }

    public OneWay getOneWay()
    {
        return oneWay;
    }

    public float getCenterX()
    {
        return centerX;
    }

    public float getCenterY()
    {
        return centerY;
    }
}
