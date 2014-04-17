package dk.itu.groupe;

import java.awt.geom.Line2D;
import java.util.HashMap;
import java.util.Locale;
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

    private final long fromNode;
    private final long toNode;
    private final double length;
    private final long id;
    private final CommonRoadType type;
    private final String roadname;
    private final int exitNumber;
    private final int speedLimit;
    private final double driveTime;
    private final OneWay oneWay;
    private final Line2D line;

    @Override
    public String toString()
    {
        return fromNode + ","
                + toNode + ","
                + String.format(Locale.ENGLISH, "%.5f,", length)
                + id + ","
                + type.getTypeNo() + ","
                + "`" + roadname + "`,"
                + exitNumber + ","
                + speedLimit + ","
                + String.format(Locale.ENGLISH, "%.3f,", driveTime)
                + oneWay.getNumber();
    }
    
    protected Edge(Node from, Node to) {
        line = new Line2D.Double(from.X_COORD, from.Y_COORD, to.X_COORD, to.Y_COORD);
        fromNode = 0;
        toNode = 0;
        length = 0;
        id = 0;
        type = null;
        roadname = null;
        exitNumber = 0;
        speedLimit = 0;
        driveTime = 0;
        oneWay = null;
    }

    public Edge(String line, Map<Long, Node> nodeMap)
    {
        if (rtMap == null) {
            rtMap = new HashMap<>();
            for (CommonRoadType rt : CommonRoadType.values()) {
                rtMap.put(rt.getTypeNo(), rt);
            }
        }
        DataLine dl = new DataLine(line);
        fromNode = dl.getLong();
        toNode = dl.getLong();
        Node fN = nodeMap.get(fromNode);
        Node tN = nodeMap.get(toNode);
        length = dl.getDouble();
        id = dl.getLong();
        int typ = dl.getInt();
        type = rtMap.get(typ);
        if (type == null) {
            System.err.println(typ);
            assert (type != null);
        }
        roadname = dl.getString();
        exitNumber = dl.getInt();
        speedLimit = dl.getInt();
        driveTime = dl.getDouble();
        switch (dl.getInt()) {
            case -1:
                oneWay = OneWay.TO_FROM;
                break;
            case 0:
                oneWay = OneWay.NO;
                break;
            case 1:
                oneWay = OneWay.FROM_TO;
                break;
            default:
                oneWay = OneWay.NO;
                System.err.println("Assuming no restrictions on edge.");
        }
        assert(fN != null);
        if (tN == null) {
            System.out.println(toNode);
            System.out.println(nodeMap.containsKey(toNode));
            System.out.println(roadname);
            assert(tN != null);
        }
        this.line = new Line2D.Double(
                fN.X_COORD,
                fN.Y_COORD,
                tN.X_COORD,
                tN.Y_COORD);
    }

    public CommonRoadType getType()
    {
        return type;
    }
    
    public double getLength()
    {
        return length;
    }
    
    public Line2D getLine()
    {
        return line;
    }
    
    public String getRoadname()
    {
        return roadname;
    }
}
