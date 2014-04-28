package dk.itu.groupe;

import dk.itu.groupe.loading.DataLine;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the raw data from a line in edges.csv.
 *
 * @author Peter Bindslev (plil@itu.dk), Rune Henriksen (ruju@itu.dk) & Mikael
 * Jepsen (mlin@itu.dk)
 */
public class Edge
{

    private static HashMap<Integer, CommonRoadType> rtMap;

    private final int id;
    private final CommonRoadType type;
    private final String roadname;
    private final double length;
    private final int exitNumber;
    private final int speedLimit;
    private final double driveTime;
    private final OneWay oneWay;
    private final Shape path;
    private final Node[] nodes;

    @Override
    public String toString()
    {
        throw new UnsupportedOperationException("Edges should not be printed");
        /*return id + ","
         + type.getTypeNo() + ","
         + "`" + roadname + "`,"
         + exitNumber + ","
         + speedLimit + ","
         + oneWay.getNumber();*/
    }

    protected Edge(Node[] nodes)
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
        p.moveTo(nodes[0].X_COORD, nodes[0].Y_COORD);
        for (int i = 1; i < nodes.length; i++) {
            p.lineTo(nodes[i].X_COORD, nodes[i].Y_COORD);
        }
        p.closePath();
        path = new Area(p);
        this.nodes = nodes;
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
        nodes = new Node[]{from, to};
        assert nodes.length == 2;
        path = new Line2D.Double(from.X_COORD, from.Y_COORD, to.X_COORD, to.Y_COORD);
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

    public Node[] getNodes()
    {
        return nodes;
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
        return new Edge(id, type, roadname, length, exitNumber, speedLimit, driveTime, oneWay, nodes[1], nodes[0]);
    }

    /*public static Edge[] parseEdges(String line, Map<Integer, Node> nodeMap)
    {
        
    }*/
}
